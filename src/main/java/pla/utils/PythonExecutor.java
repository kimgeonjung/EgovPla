package pla.utils;

import static pla.converter.FileZipper.compressFilesToZip;
import static pla.converter.FileZipper.deleteFilesAfterCompression;
import static pla.converter.JsonToPdfConverter.convertJsonToPdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pla.converter.CsvToShpConverter;
import pla.converter.JsonToCsvConverter;
import pla.dto.AuthInfo;
import pla.service.ApplyService;
import pla.service.UserFileLinkService;

@Slf4j
@RequiredArgsConstructor
@Component
public class PythonExecutor {
	@Value("${file.download.dir}")
	private String downloadDir;
    @Value("${server.home.dir}")
    private String homeDir;


	private final ApplyService applyService;
	private final AutoAddLinkToApply linkToApply;
	private final UserFileLinkService userFileLinkService;
	
    /**
     * Python 스크립트를 실행하고 결과를 반환하는 메서드
     *
     * @param authInfo 세션에서 가져온 접속자 정보
     * @param modelName 실행할 Python 모델의 이름
     * @param location 모델을 실행시킬 지역 ex)광주, 천안
     * @param parameters 모델에 적용시킬 매개변수들
     * @return Python 스크립트 실행 결과
     */
    public String runPythonScript(AuthInfo authInfo, String modelName, String location, String... parameters) {
        StringBuilder output = new StringBuilder();

        if (!modelName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("잘못된 입력입니다.");
        } else if (!modelName.equals("kiosk") && !modelName.equals("library") && !modelName.equals("public_wifi") && !modelName.equals("canopy")) {
            throw new IllegalArgumentException("잘못된 모델 이름이 들어갔습니다.");
        }

        // 매개변수를 포함한 명령어 작성
        String paramStr = String.join(" ", parameters); // 빈 배열이면 paramStr은 빈 문자열
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String timeString = now.format(formatter);
        String fileName = location + "_" + modelName + "_" + timeString + "_" + authInfo.getLoginId();
        String excModel = "";
        
        if(modelName.equals("kiosk")) {
        	excModel = modelName + ".sh ";
        }else {
        	excModel = modelName + "1.sh ";
        }
                
        // 명령어 작성
        String[] commands = {
        		"bash", "-c",
        		"cd " + homeDir + modelName + " && source ./venv/bin/activate && ./" + excModel + paramStr
        		+ " --map_filename " + fileName
        };
        try {
            // ProcessBuilder 초기화
            ProcessBuilder processBuilder = new ProcessBuilder(commands);

            // 환경 변수 설정
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":" + homeDir + modelName + "/venv/bin");
            processBuilder.environment().put("VIRTUAL_ENV", homeDir + modelName + "/venv");

            // 프로세스 실행
            Process process = processBuilder.start();

            // 표준 출력 및 표준 에러 비동기 처리
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            new Thread(() -> {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        output.append("Error: ").append(line).append("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            output.append("Python script exited with code: ").append(exitCode).append("\n");

            // 정상 종료시 게시글 생성
            if (exitCode == 0) {
                // 게시글 생성
            	String applyTitle = authInfo.getName() + "님이 신청한 " + modelName + "분석 데이터입니다.";
                String applyContent = authInfo.getName() + "님이 신청한 " + modelName + "분석 데이터입니다. "
                		+ "\n자세한 내용은 하단 결과보기를 확인해 주세요";
                
                String type = modelName;
                Long applyId = applyService.createBoardId(authInfo.getName(), applyTitle, applyContent, location, type, authInfo);
                linkToApply.addLinkToApply(applyId, authInfo.getLoginId(), modelName, fileName);
                
                // 다운로드될 파일 추가
                String downloadPath = downloadDir + fileName + "/" + fileName;
                File directory = new File(downloadDir + fileName);
                if (!directory.exists()) {
                    directory.mkdirs();  // 경로가 없으면 디렉토리 생성
                }
                try {
                    // 1. JSON → CSV 변환
                    String jsonInputPath = applyService.getApplyLink(applyId);
                    String csvOutputPath = downloadPath + ".csv";

                    JsonToCsvConverter jsonToCsvConverter = new JsonToCsvConverter();
                    jsonToCsvConverter.convertJsonToCsv(jsonInputPath, csvOutputPath);

                    // 2. JSON → PDF 변환
                    String pdfOutputPath = downloadPath + ".pdf";
                    convertJsonToPdf(jsonInputPath, pdfOutputPath);

                    // 3. CSV → SHP 변환
                    String shpOutputPath = downloadPath + ".shp";

                    CsvToShpConverter csvToShpConverter = new CsvToShpConverter();
                    csvToShpConverter.convertCsvToShapefile(csvOutputPath, shpOutputPath);

                    // 4. SHP 관련 파일 압축
                    String[] filesToZip = {
                    		downloadPath + ".shp",
                    		downloadPath + ".dbf",
                    		downloadPath + ".fix",
                    		downloadPath + ".prj",
                    		downloadPath + ".shx"
                    };

                    String outputZip = downloadPath + ".zip";

                    compressFilesToZip(filesToZip, outputZip);

                    // 5. 압축하고 남은 파일 삭제
                    deleteFilesAfterCompression(filesToZip);
                    
                    userFileLinkService.createFileLink(authInfo.getId(), applyId, fileName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            output.append("Exception: ").append(e.getMessage());
        }

        return output.toString();
    }
}

