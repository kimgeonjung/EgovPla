package pla.converter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.*;

public class FileZipper {

    public static void compressFilesToZip(String[] filePaths, String outputZipPath) {
        try (FileOutputStream fos = new FileOutputStream(outputZipPath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String filePath : filePaths) {
                File fileToZip = new File(filePath);
                if (!fileToZip.exists()) {
                    continue;
                }
                addFileToZip(fileToZip, fileToZip.getName(), zos);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addFileToZip(File file, String fileName, ZipOutputStream zos) throws IOException {
        if (file.isHidden()) {
            return;
        }

        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToZip(child, fileName + "/" + child.getName(), zos);
                }
            }
            return;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        }
    }
    public static void deleteFilesAfterCompression(String[] files) {
        for (String filePath : files) {
            try {
                Path path = Paths.get(filePath);
                Files.deleteIfExists(path);
            } catch (Exception e) {
                System.err.println(filePath + " 삭제 중 오류 발생: " + e.getMessage());
            }
        }
    }
}
