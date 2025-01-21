package pla.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalTime;
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

import static pla.converter.JsonToPdfConverter.convertJsonToPdf;
import static pla.converter.FileZipper.compressFilesToZip;
import static pla.converter.FileZipper.deleteFilesAfterCompression;

@Slf4j
@RequiredArgsConstructor
@Component
public class PythonExecutor {
	@Value("${file.download.dir}")
	private String downloadDir;

	private final ApplyService applyService;
	private final AutoAddLinkToApply linkToApply;
	private final UserFileLinkService userFileLinkService;
	
    /**
     * Python 스크립트를 실행하고 결과를 반환하는 메서드
     *
     * @param modelName 실행할 Python 모델의 이름
     * @return Python 스크립트 실행 결과
     */
    public String runPythonScript(AuthInfo authInfo, String modelName, String location, String... parameters) {
        StringBuilder output = new StringBuilder();

        if (!modelName.matches("[a-zA-Z0-9_]+")) {
        	log.info("입력 오류");
            throw new IllegalArgumentException("잘못된 입력입니다.");
        } else if (!modelName.equals("kiosk") && !modelName.equals("library") && !modelName.equals("public_wifi")) {
        	log.info("여기는 모델 이름 잘못된걸로 들어가면 남");
            throw new IllegalArgumentException("잘못된 모델 이름이 들어갔습니다.");
        }

        // 매개변수를 포함한 명령어 작성
        String paramStr = String.join(" ", parameters); // 빈 배열이면 paramStr은 빈 문자열
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
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
        		"cd /home/ubuntu/" + modelName + " && source ./venv/bin/activate && ./" + excModel + paramStr
        		+ " --map_filename " + fileName
        };
        try {
            log.info("Command to execute: {}", Arrays.toString(commands));

            // ProcessBuilder 초기화
            ProcessBuilder processBuilder = new ProcessBuilder(commands);

            // 환경 변수 설정
            processBuilder.environment().put("PATH", System.getenv("PATH") + ":/home/ubuntu/" + modelName + "/venv/bin");
            processBuilder.environment().put("VIRTUAL_ENV", "/home/ubuntu/" + modelName + "/venv");

            // 프로세스 실행
            Process process = processBuilder.start();

            // 표준 출력 및 표준 에러 비동기 처리
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("{}", line);
                        output.append(line).append("\n");
                    }
                } catch (Exception e) {
                    log.error("Error reading Python stdout", e);
                }
            }).start();

            new Thread(() -> {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        log.error("{}", line);
                        output.append("Error: ").append(line).append("\n");
                    }
                } catch (Exception e) {
                    log.error("Error reading Python stderr", e);
                }
            }).start();

            // 프로세스 종료 대기
            int exitCode = process.waitFor();
            log.info("Python process exited with code: {}", exitCode);
            output.append("Python script exited with code: ").append(exitCode).append("\n");
            
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
                    System.out.println("JSON을 CSV로 변환 중...");
                    jsonToCsvConverter.convertJsonToCsv(jsonInputPath, csvOutputPath);
                    System.out.println("JSON → CSV 변환 완료!");

                    // 2. JSON → PDF 변환
                    String pdfOutputPath = downloadPath + ".pdf";

                    System.out.println("JSON을 PDF로 변환 중...");
                    convertJsonToPdf(jsonInputPath, pdfOutputPath);
                    System.out.println("JSON → PDF 변환 완료!");

                    // 3. CSV → SHP 변환
                    String shpOutputPath = downloadPath + ".shp";

                    CsvToShpConverter csvToShpConverter = new CsvToShpConverter();
                    System.out.println("CSV를 SHP로 변환 중...");
                    csvToShpConverter.convertCsvToShapefile(csvOutputPath, shpOutputPath);
                    System.out.println("CSV → SHP 변환 완료!");

                    // 4. SHP 관련 파일 압축
                    String[] filesToZip = {
                    		downloadPath + ".shp",
                    		downloadPath + ".dbf",
                    		downloadPath + ".fix",
                    		downloadPath + ".prj",
                    		downloadPath + ".shx"
                    };

                    String outputZip = downloadPath + ".zip";

                    System.out.println("SHP 파일을 ZIP으로 압축 중...");
                    compressFilesToZip(filesToZip, outputZip);
                    System.out.println("SHP 파일 압축 완료!");

                    // 5. 압축하고 남은 파일 삭제
                    deleteFilesAfterCompression(filesToZip);
                    
                    userFileLinkService.createFileLink(authInfo.getId(), applyId, fileName);

                } catch (Exception e) {
                    System.err.println("처리 중 오류 발생: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            log.error("Exception occurred while executing Python script", e);
            output.append("Exception: ").append(e.getMessage());
        }

        return output.toString();
    }
}

