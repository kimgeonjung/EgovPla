package pla.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
                        return "접근성점수";
                    case "predicted_usage_standardized":
                        return "예측사용량";
                    case "traffic_score_standardized":
                        return "트래픽점수";
                    default:
                        return field;
                }
            }).collect(Collectors.toCollection(LinkedHashSet::new));

            // CSV 파일 작성
            FileWriter writer = new FileWriter(outputFilePath);

            // 헤더 작성
            String[] fieldArray = modifiedFields.toArray(new String[0]);
            writer.append(String.join(",", fieldArray));
            writer.append("\n");

            // 데이터 작성
            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    for (String field : fields) { // 원래 필드 순서를 사용해 데이터 추출
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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
