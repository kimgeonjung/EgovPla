package pla.converter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureWriter;
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
        Map<String, Object> params = new HashMap<>();
        params.put("url", newFile.toURI().toURL());
        DataStore dataStore = DataStoreFinder.getDataStore(params);
        if (dataStore == null) {
            throw new IOException("Could not create data store");
        }

        try (CSVReader csvReader = new CSVReader(new FileReader(csvPath))) {
            // Read header row
            String[] headers = csvReader.readNext();
            if (headers == null || headers.length < 3) {
                throw new IllegalArgumentException("CSV file must contain at least 'lon', 'lat', and one additional field");
            }

            // Define the schema dynamically
            SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();
            typeBuilder.setName("Location");
            typeBuilder.add("geometry", Point.class); // Geometry field

            for (String header : headers) {
                if (header.equalsIgnoreCase("lon") || header.equalsIgnoreCase("lat")) {
                    typeBuilder.add(header, Double.class);
                } else {
                    typeBuilder.add(header, String.class);
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

                        if (columnName.equalsIgnoreCase("lon") || columnName.equalsIgnoreCase("lat")) {
                            double numericValue = Double.parseDouble(value);
                            attributes.put(columnName, numericValue);

                            if (columnName.equalsIgnoreCase("lon")) {
                                double lat = (double) attributes.getOrDefault("lat", 0.0);
                                point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(numericValue, lat));
                            } else if (columnName.equalsIgnoreCase("lat")) {
                                double lon = (double) attributes.getOrDefault("lon", 0.0);
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

        System.out.println("Shapefile 생성 완료: " + outputShpPath);
    }
}
