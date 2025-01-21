package pla.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pla.entity.Apply;
import pla.repository.ApplyRepository;

@Component
@RequiredArgsConstructor
public class AutoAddLinkToApply {
	@Value("${server.home.dir}")
    private String homeDir;
	
	private final ApplyRepository applyRepository;
	
	// 파일 경로 자동 추가	 
    public void addLinkToApply(Long applyId, String userId, String modelName, String fileName) {
		Apply apply = applyRepository.findById(applyId).orElseThrow(()-> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
		
		// 결과 파일 경로 생성
		if(modelName.equals("kiosk")) {
			String kioskFilePath = homeDir + modelName + "/" + fileName + "_parallel.json";
			apply.setLink(kioskFilePath);
		} else if(modelName.equals("library")) {
			String libraryFilePath = homeDir + modelName + "/" + fileName;
			apply.setLink(libraryFilePath);
		}
		applyRepository.save(apply);
	}
}
