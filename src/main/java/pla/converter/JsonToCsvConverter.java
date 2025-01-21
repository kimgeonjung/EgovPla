package pla.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class JsonToCsvConverter {

    public void convertJsonToCsv(String inputFilePath, String outputFilePath) {
        try {
            // JSON 파일 읽기
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(inputFilePath));

            // 모든 필드 이름 추출 (순서를 유지)
            Set<String> fields = new LinkedHashSet<>();
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    Iterator<String> fieldNames = node.fieldNames();
                    while (fieldNames.hasNext()) {
                        fields.add(fieldNames.next());
                    }
                }
            }

            // CSV 파일 작성
            FileWriter writer = new FileWriter(outputFilePath);

            // 헤더 작성
            String[] fieldArray = fields.toArray(new String[0]);
            writer.append(String.join(",", fieldArray));
            writer.append("\n");

            // 데이터 작성
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    for (String field : fieldArray) {
                        JsonNode valueNode = node.get(field);
                        if (valueNode != null) {
                            writer.append(valueNode.asText());
                        } else {
                            writer.append(""); // 기본값 (빈 값)
                        }
                        writer.append(",");
                    }
                    writer.append("\n");
                }
            }

            writer.flush();
            writer.close();
            System.out.println("CSV 파일 생성 완료: " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
