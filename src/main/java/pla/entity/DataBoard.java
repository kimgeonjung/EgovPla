package pla.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 식별자

    @Column(nullable = false)
    private String title; //제목

    @Column(nullable = false, columnDefinition = "TEXT") // 긴 텍스트 저장
    private String content; // 내용

    @Column(nullable = false)
    private Integer hits; // 조회수

    private LocalDateTime createdAt;  // 생성일시

    private LocalDateTime updatedAt; // 수정일시

    @Transient
    private String preview; // 미리보기

    private String a1; //파일 데이터명
    private String a2; //분류 체계
    private String a3; //제공기관
    private String a4; //관리부서명
    private String a5; //관리부서 전화번호
    private String a6; //보유근거
    private String a7; //수집방법
    private String a8; //업데이트 주기
    private String a9; //차기 등록 예정일
    private String a10; //매체유형
    private String a11; //전체 행
    private String a12; //확장자
    private String a13; //키워드
    private String a14; //데이터 한계
    private String a15; //제공형태
    private String a16; //설명
    private String a17; //기타 유의사항
    private String a18; //비용부과유무
    private String a19; //비용부과기준 및 단위
    private String a20; //이용허락범위
    private String a21; //URL


    @ElementCollection
    @CollectionTable(name = "dataBoard_files", joinColumns = @JoinColumn(name = "dataBoard_id"))
    private List<FileDetail> files = new ArrayList<>(); // 첨부 파일 리스트

    // 생성일시 자동 설정
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();  // 생성시 현재 시간 설정
            this.updatedAt = LocalDateTime.now();
        }
    }

    // 업데이트 시 자동 설정
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();  // 수정일시를 현재 시간으로 업데이트
    }
}
