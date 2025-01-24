package pla.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@Slf4j
public class FileDownloadController {
	@Value("${file.upload.dir}")
    private String uploadDir;
	
	@Value("${file.download.dir}")
    private String downloadDir;

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("filename") String filename, 
    		@RequestParam(value = "dir", defaultValue = "dir1") String dir,
    		@RequestParam(value = "saveAs", required = false) String saveAs) throws IOException {
        try {
        	// 한글 파일명 디코딩 (필요 시 적용)
//            String decodedFilename = URLDecoder.decode(filename, StandardCharsets.UTF_8.toString());
        	String decodedFilename = filename;
            // 선택된 경로 설정
            String selectedDir = "dir2".equalsIgnoreCase(dir) ? downloadDir : uploadDir;
            // 파일 경로 설정 (예: 클라우드 업로드 경로)
            Path filePath = Paths.get(selectedDir + decodedFilename);
            // 리소스 생성
            Resource resource;
            try {
                resource = new UrlResource(filePath.toUri());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid file path: " + filePath.toString());
            }           

            // 파일 존재 및 읽기 가능 여부 확인
            if (!Files.exists(filePath)) {
                throw new RuntimeException("File does not exist: " + decodedFilename);
            }

            if (!Files.isReadable(filePath)) {
                throw new RuntimeException("File is not readable: " + decodedFilename);
            }
           
            // 파일 MIME 타입 설정
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            
            // 파일명 인코딩 (브라우저 호환성 유지)
//            String encodedFilename = URLEncoder.encode(decodedFilename, StandardCharsets.UTF_8.toString()).replace("+", "%20");
         // 파일명 설정 (saveAs 값이 없으면 원래 파일명 사용)
            String downloadFilename = (saveAs != null && !saveAs.isEmpty()) ? saveAs : decodedFilename;
            String encodedDownloadFilename = URLEncoder.encode(downloadFilename, StandardCharsets.UTF_8.toString()).replace("+", "%20");          
            
            return ResponseEntity.ok()
            		.contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedDownloadFilename)
                    .body(resource);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
