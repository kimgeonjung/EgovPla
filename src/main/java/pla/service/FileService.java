package pla.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pla.entity.FileDetail;

/**
 * 파일 업로드와 관련된 공통 로직을 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class FileService {
	@Value("${file.upload.dir}")
    private String uploadDir;

    /**
     * 파일 업로드를 처리하고 파일 정보를 반환합니다.
     *
     * @param files 업로드할 파일 배열
     * @return 파일 정보 리스트
     * @throws IOException 파일 저장 실패 시 발생
     */
    public List<FileDetail> uploadFiles(MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        Path uploadPath = Paths.get(uploadDir);
        if (uploadPath == null || !Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath); // 디렉토리가 없으면 생성
        }

        List<FileDetail> fileDetails = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null || originalFilename.isEmpty()) {
                    throw new IllegalArgumentException("파일 이름이 유효하지 않습니다.");
                }

                Path filePath = Paths.get(uploadDir, originalFilename);
                file.transferTo(filePath.toFile()); // 파일 저장

                fileDetails.add(new FileDetail("/uploads/" + originalFilename, originalFilename));
            }
        }
        return fileDetails;
    }    
    
    // 상대 경로를 절대 경로로 변환하는 메소드
    public Path getFilePathFromRelative(String relativePath) {
        String filePath = relativePath.replace("/uploads/", uploadDir + "/");
        return Paths.get(filePath);  // 절대 경로로 반환
    }

    public void deleteFile(FileDetail fileDetail) {
        Path filePath = getFilePathFromRelative(fileDetail.getFilePath());
        try {
            Files.deleteIfExists(filePath);  // 파일 삭제
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + filePath.toString(), e);
        }
    }
}
