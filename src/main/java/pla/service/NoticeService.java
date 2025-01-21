package pla.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pla.entity.FileDetail;
import pla.entity.Notice;
import pla.repository.NoticeRepository;

/**
 * 공지사항 관련 비즈니스 로직을 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final FileService fileService;
    
    @Value("${file.upload.dir}")
    private String uploadDir;

    // 모든 공지사항을 생성일시 기준 내림차순으로 반환합니다.
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll(Sort.by(Sort.Order.desc("createdAt")));
    }

    // 새로운 공지사항을 생성합니다.
    public void createNotice(String title, String content, MultipartFile[] files) throws IOException {
        Notice notice = Notice.builder()
                .title(title)
                .content(content)
                .hits(0) // 조회수 초기값
                .createdAt(LocalDateTime.now())
                .build();

        // 파일 업로드 처리
        List<FileDetail> fileDetails = fileService.uploadFiles(files);
        notice.setFiles(fileDetails);

        noticeRepository.save(notice);
    }

    // 특정 게시글을 조회하고, 조회수 증가 여부를 설정하여 게시글 반환
    // 수정 전 메서드 명: selectNoticeDetail
    @Transactional
    public Notice getNoticeDetail(Long id, boolean increaseHitCount) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. ID: " + id));

        if (increaseHitCount) {
            notice.setHits(notice.getHits() + 1);
            noticeRepository.save(notice);
        }

        return notice;
    }

    // 공지사항을 수정합니다.
    @Transactional
    public void updateNotice(Notice notice) {
        Notice existingNotice = noticeRepository.findById(notice.getId())
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        existingNotice.setTitle(notice.getTitle()); // 제목 수정
        existingNotice.setContent(notice.getContent()); // 내용 수정
        noticeRepository.save(existingNotice);  // 수정된 공지사항 저장
    }

 // 특정 공지사항을 삭제합니다.
    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다. ID: " + id));

        // 삭제할 파일 정보 가져오기
        List<FileDetail> fileDetails = notice.getFiles();

        // 파일 삭제 처리
        if (fileDetails != null && !fileDetails.isEmpty()) {
            for (FileDetail fileDetail : fileDetails) {
                try {
                    // 경로 결합 (슬래시 중복 방지)
                    String relativePath = fileDetail.getFilePath().replaceFirst("^/uploads", "");
                    Path filePath = Paths.get(uploadDir, relativePath);
                    Files.deleteIfExists(filePath); // 파일 삭제
                } catch (IOException e) {
                    // 파일 삭제 실패 시 로그 출력 (삭제 실패는 전체 트랜잭션을 롤백하지 않음)
                    System.err.println("파일 삭제 실패: " + fileDetail.getFilePath());
                }
            }
        }

        noticeRepository.deleteById(id);
    }

}
