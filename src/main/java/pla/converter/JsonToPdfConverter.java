package pla.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonToPdfConverter {

    public static void convertJsonToPdf(String inputJsonPath, String outputPdfPath) {
        long startTime = System.currentTimeMillis();

        try {
            // JSON 파일 존재 여부 확인
            File jsonFile = new File(inputJsonPath);
            if (!jsonFile.exists()) {
                System.err.println("JSON 파일이 존재하지 않습니다: " + inputJsonPath);
                return;
            }

            // JSON 데이터 읽기
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new FileReader(jsonFile));

            // PDF 문서 생성
            PdfWriter writer = new PdfWriter(outputPdfPath);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // JSON 필드 추출 (첫 번째 레코드 기준)
            Set<String> fields = new LinkedHashSet<>();
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    Iterator<String> fieldNames = node.fieldNames();
                    while (fieldNames.hasNext()) {
                        fields.add(fieldNames.next());
                    }
                    break;
                }
            } else {
                System.err.println("올바른 JSON 배열 형식이 아닙니다.");
                document.close();
                return;
            }

            // 필드 이름 변환
            Set<String> modifiedFields = fields.stream().map(field -> {
                switch (field) {
                    case "lat":
                        return "위도";
                    case "lon":
                        return "경도";
                    case "total_score":
                        return "총점";
                    case "accessibility_score_standardized":
                        return "접근성 점수";
                    case "predicted_usage_standardized":
                        return "예측 사용량";
                    case "traffic_score_standardized":
                        return "트래픽 점수";
                    default:
                        return field;
                }
            }).collect(Collectors.toCollection(LinkedHashSet::new));

            // PDF 테이블 생성 (헤더는 처음에만 추가)
            Table table = new Table(modifiedFields.size());
            for (String field : modifiedFields) {
                table.addHeaderCell(new Paragraph(field).setBold());
            }

            int rowCount = 0;
            for (JsonNode node : rootNode) {
                for (String field : fields) { // 원래 필드 순서를 사용해 데이터 추출
                    JsonNode valueNode = node.get(field);
                    table.addCell(valueNode != null ? valueNode.asText() : "N/A");
                }

                rowCount++;

                // 메모리 최적화를 위한 플러시
                if (rowCount % 1000 == 0) {
                    document.add(table); // 이미 테이블이 존재하므로 매번 추가하지 않도록
                    table = new Table(modifiedFields.size());  // 새로운 테이블 시작
                    for (String field : modifiedFields) {
                        table.addHeaderCell(new Paragraph(field).setBold()); // 새로운 테이블에 헤더 추가
                    }
                    System.out.println(rowCount + "개의 레코드를 PDF에 저장 중...");
                }
            }

            // 마지막 남은 데이터를 PDF에 추가
            document.add(table);
            document.close();

            long endTime = System.currentTimeMillis();
            System.out.println("PDF 파일 생성 완료: " + outputPdfPath);
            System.out.println("총 실행 시간: " + (endTime - startTime) + "ms");

        } catch (IOException e) {
            System.err.println("파일 읽기/쓰기 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("PDF 생성 중 오류 발생:");
            e.printStackTrace();
        }
    }

}
