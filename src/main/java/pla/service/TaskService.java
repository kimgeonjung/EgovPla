package pla.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class TaskService {
	
    // 실행 제한 저장 (userId -> 제한 종료 시간)
    private final Map<String, Long> executionLimits = new ConcurrentHashMap<>();

    // 실행 제한 시간 (밀리초, 전체: 30분)
//    private final long EXECUTION_LIMIT_KIOSK = 10 * 60 * 1000;
    private final long EXECUTION_LIMIT = 30 * 60 * 1000;
    
    /** 실행 제한 확인 */
    public boolean isExecutionAllowed(String userId) {
        Long currentTime = System.currentTimeMillis();
        Long userLimitTime = executionLimits.getOrDefault(userId, 0L);

        // 실행 제한 확인
        if (currentTime < userLimitTime) {
            return false; // 제한 시간 내 실행 불가
        }

        // 실행 허용 및 제한 시간 갱신
        executionLimits.put(userId, currentTime + EXECUTION_LIMIT);
        return true;
    }

    /** 제한 시간 조회 */
    public long getRemainingTime(String userId) {
        Long currentTime = System.currentTimeMillis();
        Long userLimitTime = executionLimits.getOrDefault(userId, 0L);
        return Math.max(userLimitTime - currentTime, 0); // 남은 시간 반환
    }
}
