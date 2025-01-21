package pla.service;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import lombok.RequiredArgsConstructor;
import pla.dto.DataBoardDto;
import pla.entity.DataBoard;
import pla.entity.FileDetail;
import pla.repository.DataBoardRepository;

@Service
@RequiredArgsConstructor
public class DataBoardService {
    private final DataBoardRepository dataBoardRepository;
    private final FileService fileService;

    public List<DataBoard> getAllDataList() {
        return dataBoardRepository.findAll(Sort.by(Sort.Order.desc("createdAt")));
    }

    @Transactional
    public void createDataBoard(DataBoardDto dto) throws IOException {
        // DataBoard 객체 생성 및 초기화
        DataBoard dataBoard = DataBoard.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .hits(0)
                .a1(dto.getA1())
                .a2(dto.getA2())
                .a3(dto.getA3())
                .a4(dto.getA4())
                .a5(dto.getA5())
                .a6(dto.getA6())
                .a7(dto.getA7())
                .a8(dto.getA8())
                .a9(dto.getA9())
                .a10(dto.getA10())
                .a11(dto.getA11())
                .a12(dto.getA12())
                .a13(dto.getA13())
                .a14(dto.getA14())
                .a15(dto.getA15())
                .a16(dto.getA16())
                .a17(dto.getA17())
                .a18(dto.getA18())
                .a19(dto.getA19())
                .a20(dto.getA20())
                .a21(dto.getA21())
                .build();

        // 파일 업로드 처리
        List<FileDetail> fileDetails = fileService.uploadFiles(dto.getFiles()); // 파일 업로드 서비스 호출
        dataBoard.setFiles(fileDetails); // 파일 정보 설정

        // 데이터 저장
        dataBoardRepository.save(dataBoard);
    }

    @Transactional
    public DataBoard getDataListDetail(Long id, boolean increaseHitCount, int limit, int offset) throws IOException {
        DataBoard dataBoard = dataBoardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("데이터 리스트를 찾을 수 없습니다. ID: " + id));

        if (increaseHitCount) {
            dataBoard.setHits(dataBoard.getHits() + 1);
            dataBoardRepository.save(dataBoard);
        }
        // 파일 경로 가져오기 (첨부된 첫 번째 파일 사용)
        String filePath;
        if (!dataBoard.getFiles().isEmpty()) {
            filePath = dataBoard.getFiles().get(0).getFilePath(); // 첫 번째 파일 경로 사용

            // 상대 경로를 절대 경로로 변환
            Path absoluteFilePath = fileService.getFilePathFromRelative(filePath);

            // 파일 확장자 확인 (CSV 파일만 처리)
            if (isCsvFile(filePath)) {
                // CSV 파일이면 HTML 테이블 생성
                String htmlTable = fetchCsvDataAsHtml(absoluteFilePath.toString(), limit, offset);
                dataBoard.setPreview(htmlTable);
            } else {
                // CSV 파일이 아니면 미리보기 비우기
                dataBoard.setPreview("지원하지 않는 파일 형식입니다.");
            }
        } else {
            dataBoard.setPreview("첨부 파일이 없습니다.");
        }

        return dataBoard;
    }

    // 파일이 CSV인지 확인하는 메서드
    private boolean isCsvFile(String filePath) {
        return filePath != null && filePath.toLowerCase().endsWith(".csv");
    }

    // CSV 파일을 읽어 HTML 테이블로 변환
    public String fetchCsvDataAsHtml(String filePath, int limit, int offset) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> rows = csvReader.readAll();

            // HTML 테이블 생성
            StringBuilder htmlTable = new StringBuilder();
            htmlTable.append("<div style='overflow-x: auto; max-width: 100%;'>"); // 가로 스크롤 추가
            htmlTable.append("<table style='border-collapse: collapse; width: 100%; font-family: Arial, sans-serif; white-space: nowrap;'>");

            // 테이블 헤더 생성
            if (!rows.isEmpty()) {
                htmlTable.append("<thead><tr style='background-color: #f2f2f2;'>");
                String[] header = rows.get(0); // 첫 번째 행은 헤더
                for (String column : header) {
                    htmlTable.append("<th style='border: 1px solid #ddd; padding: 8px; text-align: left;'>")
                            .append(column)
                            .append("</th>");
                }
                htmlTable.append("</tr></thead>");
            }

            // 데이터 행 생성 (offset, limit 적용)
            htmlTable.append("<tbody>");
            for (int i = offset+1; i < Math.min(offset + limit, rows.size()); i++) {
                String[] row = rows.get(i);
                htmlTable.append("<tr>");
                for (String cell : row) {
                    htmlTable.append("<td style='border: 1px solid #ddd; padding: 8px;'>")
                            .append(cell != null ? cell : "")
                            .append("</td>");
                }
                htmlTable.append("</tr>");
            }
            htmlTable.append("</tbody></table>");
            htmlTable.append("</div>"); // 닫는 div 태그 추가

            return htmlTable.toString();
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public void updateDataBoard(DataBoardDto dto) throws IOException {
        DataBoard dataBoard = dataBoardRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. ID: " + dto.getId()));

        // 수정할 필드 업데이트
        dataBoard.setTitle(dto.getTitle());
        dataBoard.setContent(dto.getContent());
        dataBoard.setA1(dto.getA1());
        dataBoard.setA2(dto.getA2());
        dataBoard.setA3(dto.getA3());
        dataBoard.setA4(dto.getA4());
        dataBoard.setA5(dto.getA5());
        dataBoard.setA6(dto.getA6());
        dataBoard.setA7(dto.getA7());
        dataBoard.setA8(dto.getA8());
        dataBoard.setA9(dto.getA9());
        dataBoard.setA10(dto.getA10());
        dataBoard.setA11(dto.getA11());
        dataBoard.setA12(dto.getA12());
        dataBoard.setA13(dto.getA13());
        dataBoard.setA14(dto.getA14());
        dataBoard.setA15(dto.getA15());
        dataBoard.setA16(dto.getA16());
        dataBoard.setA17(dto.getA17());
        dataBoard.setA18(dto.getA18());
        dataBoard.setA19(dto.getA19());
        dataBoard.setA20(dto.getA20());
        dataBoard.setA21(dto.getA21());

        // 기존 파일 삭제
        if (dataBoard.getFiles() != null && !dataBoard.getFiles().isEmpty()) {
            for (FileDetail file : dataBoard.getFiles()) {
                fileService.deleteFile(file);  // 파일 삭제 메서드 호출
            }
            dataBoard.getFiles().clear();  // 파일 리스트 초기화
        }

        // 새 파일 추가
        if (dto.getFiles() != null && dto.getFiles().length > 0) {
            List<FileDetail> newFiles = fileService.uploadFiles(dto.getFiles());
            dataBoard.setFiles(newFiles);
        }


        dataBoardRepository.save(dataBoard);  // 수정된 데이터 저장
    }
    @Transactional
    public void deleteDataBoard(Long id) {
        DataBoard dataBoard = dataBoardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물을 찾을 수 없습니다. ID: " + id));

        // 업로드된 파일 삭제
        if (dataBoard.getFiles() != null && !dataBoard.getFiles().isEmpty()) {
            for (FileDetail file : dataBoard.getFiles()) {
                fileService.deleteFile(file);  // 파일 삭제 메서드 호출
            }
        }
        dataBoardRepository.deleteById(id);
    }

}
