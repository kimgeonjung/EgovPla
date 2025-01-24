package pla.converter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureWriter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvToShpConverter {

    public void convertCsvToShapefile(String csvPath, String outputShpPath) throws IOException {
        File newFile = new File(outputShpPath);

        // Map<String, Serializable>로 선언
        Map<String, Serializable> params = new HashMap<>();
        params.put("url", newFile.toURI().toURL());

        // ShapefileDataStoreFactory 사용
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
        ShapefileDataStore dataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

        // DBF 파일 문자 인코딩 설정
        dataStore.setCharset(Charset.forName("EUC-KR"));

        // CSVReader 설정
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(csvPath), StandardCharsets.UTF_8))) {
            // Read header row
            String[] headers = csvReader.readNext();
            if (headers == null || headers.length < 3) {
                throw new IllegalArgumentException("CSV file must contain at least '경도', '위도', and one additional field");
            }

            // Define the schema dynamically
            SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
            typeBuilder.setName("Location");
            typeBuilder.add("geometry", Point.class); // Geometry field

            for (String header : headers) {
                if (header.equalsIgnoreCase("경도") || header.equalsIgnoreCase("위도")) {
                    typeBuilder.add(header, Double.class); // 경도 및 위도 필드는 숫자로 정의
                } else {
                    typeBuilder.add(header, String.class); // 기타 필드는 문자열로 정의
                }
            }

            SimpleFeatureType featureType = typeBuilder.buildFeatureType();
            dataStore.createSchema(featureType);

            String typeName = dataStore.getTypeNames()[0];
            GeometryFactory geometryFactory = new GeometryFactory();
            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);

            try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore.getFeatureWriterAppend(typeName, null)) {
                String[] row;

                while ((row = csvReader.readNext()) != null) {
                    Map<String, Object> attributes = new HashMap<>();
                    Point point = null;

                    for (int i = 0; i < headers.length; i++) {
                        String columnName = headers[i];
                        String value = row[i];

                        if (columnName.equalsIgnoreCase("경도") || columnName.equalsIgnoreCase("위도")) {
                            double numericValue = Double.parseDouble(value);
                            attributes.put(columnName, numericValue);

                            if (columnName.equalsIgnoreCase("경도")) {
                                double lat = (double) attributes.getOrDefault("위도", 0.0);
                                point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(numericValue, lat));
                            } else if (columnName.equalsIgnoreCase("위도")) {
                                double lon = (double) attributes.getOrDefault("경도", 0.0);
                                point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(lon, numericValue));
                            }
                        } else {
                            attributes.put(columnName, value);
                        }
                    }

                    featureBuilder.add(point); // Add geometry first
                    for (String header : headers) {
                        featureBuilder.add(attributes.get(header));
                    }

                    SimpleFeature feature = featureBuilder.buildFeature(null);
                    SimpleFeature toWrite = writer.next();
                    toWrite.setAttributes(feature.getAttributes());
                    writer.write();
                }
            } catch (CsvValidationException e) {
                throw new IOException("Error reading CSV file: " + e.getMessage());
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        } finally {
            dataStore.dispose();
        }

    }
}
