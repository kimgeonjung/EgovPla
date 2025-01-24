package pla.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pla.dto.AuthInfo;
import pla.service.TaskService;
import pla.utils.PythonExecutor;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {
	private final TaskService taskService;
	private final PythonExecutor pythonExecutor;
		
	/**
	 * 모델 이름과 추가 매개변수를 받는 경우
	 */
	@PostMapping("/run/{modelName}")
	public ResponseEntity<String> runPythonScriptWithParams(
	        HttpSession session,
	        @PathVariable String modelName,
	        @RequestBody Map<String, Object> parameters) {

	    // 세션에서 사용자 인증 정보 가져오기
	    AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
	    if (authInfo == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
	    }

	    String userId = authInfo.getLoginId();
	    
	    // 모델 이름 검증
	    if (modelName == null || modelName.isEmpty()) {
	        return ResponseEntity.badRequest().body("modelName is required");
	    }

	    // 요청 바디에서 location과 params 추출
	    String location = (String) parameters.get("location"); // location 값 추출
	    if (location == null || location.isEmpty()) {
	        return ResponseEntity.badRequest().body("location is required");
	    }

	    String rawParams = (String) parameters.get("params"); // JSON에서 params 추출
	    if (rawParams == null || rawParams.isEmpty()) {
	        rawParams = ""; // 기본값
	    }
	    final String commandParams = rawParams;

	    // 실행 제한 확인
	    if (!taskService.isExecutionAllowed(userId)) {
	        long remainingMillis = taskService.getRemainingTime(userId);
	        long remainingMinutes = remainingMillis / 1000 / 60; // 남은 시간 (분 단위)

	        String message = remainingMinutes < 1
	                ? "분석 가능 시간까지 약 1분 미만 남았습니다."
	                : "분석 가능 시간까지 약 " + remainingMinutes + "분 남았습니다.";

	        return ResponseEntity.status(429).body(message); // Too Many Requests
	    }

	    // PythonExecutor를 비동기로 호출
	    CompletableFuture.runAsync(() -> {
	        try {
	            pythonExecutor.runPythonScript(authInfo, modelName, location, commandParams);
	        } catch (Exception e) {
				e.printStackTrace();
	        }
	    });

	    return ResponseEntity.ok("작업이 시작되었습니다. 완료까지 약 30분 정도 소요됩니다.");
	}
}
