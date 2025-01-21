package pla.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pla.entity.UserFileLink;
import pla.repository.UserFileLinkRepository;

@RequiredArgsConstructor
@Service
public class UserFileLinkService {
	
	private final UserFileLinkRepository fileLinkRepository;
	
	public void createFileLink(Long uid, Long applyId, String fileLink) {
		UserFileLink userFileLink = UserFileLink.builder()
			.uid(uid)
			.applyId(applyId)
			.fileLink(fileLink)
			.build();
		fileLinkRepository.save(userFileLink);
	}
	
	public UserFileLink findLink(Long applyId, Long uid) {
		return fileLinkRepository.findByApplyIdAndUid(applyId, uid);
	}
}
