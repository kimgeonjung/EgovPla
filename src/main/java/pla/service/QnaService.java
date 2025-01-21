package pla.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pla.dto.AuthInfo;
import pla.entity.Qna;
import pla.entity.QnaList;
import pla.repository.QnaListRepository;
import pla.repository.QnaRepository;

/**
 * Q&A 관련 비즈니스 로직을 처리하는 서비스.
 */
@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaListRepository qnaListRepository;
    private final QnaRepository qnaRepository;

    // Q&A 목록을 페이징 처리하여 반환.
    public Page<QnaList> getInquiries(AuthInfo authInfo, Pageable pageable) {
        List<QnaList> list = authInfo.getRole().equals("ADMIN")
                ? qnaListRepository.findAllByOrderByIdDesc()
                : qnaListRepository.findAllByUidOrderByIdDesc(authInfo.getId());
        return createPage(list, pageable);
    }
    
    public List<QnaList> needAnswer() {
        return qnaListRepository.findAllByStateOrderByIdAsc('Q');
    }
    
    // Q&A 목록을 반환.
    public List<QnaList> getInquiryList(AuthInfo authInfo) {
        return authInfo.getRole().equals("ADMIN")
                ? qnaListRepository.findAllByOrderByIdDesc()
                : qnaListRepository.findAllByUidOrderByIdDesc(authInfo.getId());
    }

    // Q&A 상세 정보를 반환.
    public QnaList getInquiryDetail(Long id) {
        return qnaListRepository.findById(id).orElse(null);
    }

    // Q&A의 답변 목록을 반환.
    public List<Qna> getInquiryReplies(Long id) {
        QnaList qna = getInquiryDetail(id);
        return qnaRepository.findAllByQnaIdOrderByIdAsc(qna);
    }

    // 새로운 Q&A를 생성.
    public void createInquiry(QnaList list, String content, HttpSession session) {
        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");

        QnaList qnalist = QnaList.builder()
                .title(list.getTitle())
                .created_at(LocalDateTime.now())
                .endYn('N')
                .state('Q')
                .uid(authInfo.getId())
                .build();
        QnaList saveQna = qnaListRepository.save(qnalist);

        Qna qna = Qna.builder()
                .qnaId(saveQna)
                .content(content)
                .state('Q')
                .build();
        qnaRepository.save(qna);
    }

    // Q&A에 답변 추가.
    public void addAnswer(String content, String role, Long id) {
        QnaList qnalist = qnaListRepository.findById(id).orElse(null);

        Qna qna = Qna.builder()
                .qnaId(qnalist)
                .content(content)
                .state(role.equals("ADMIN") ? 'A' : 'Q')
                .build();
        qnaRepository.save(qna);

        if (qnalist != null) {
            qnalist.setState(role.equals("ADMIN") ? 'A' : 'Q');
            qnaListRepository.save(qnalist);
        }
    }

    /**
     * 페이징된 리스트 생성.
     *
     * @param list     전체 데이터 리스트
     * @param pageable 페이징 정보
     * @return 페이징된 리스트
     */
    private Page<QnaList> createPage(List<QnaList> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }
}
