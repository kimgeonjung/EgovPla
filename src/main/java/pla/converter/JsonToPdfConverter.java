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

            document.add(new Paragraph("JSON Data to PDF").setBold().setFontSize(16));

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

            // PDF 테이블 생성
            Table table = new Table(fields.size());
            for (String field : fields) {
                table.addHeaderCell(new Paragraph(field).setBold());
            }

            int rowCount = 0;
            for (JsonNode node : rootNode) {
                for (String field : fields) {
                    JsonNode valueNode = node.get(field);
                    table.addCell(valueNode != null ? valueNode.asText() : "N/A");
                }

                rowCount++;

                // 메모리 최적화를 위한 플러시
                if (rowCount % 1000 == 0) {
                    document.add(table);
                    table = new Table(fields.size());  // 새로운 테이블 시작
                    for (String field : fields) {
                        table.addHeaderCell(new Paragraph(field).setBold());
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