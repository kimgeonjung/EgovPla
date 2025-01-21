package pla.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import pla.dto.AuthInfo;

@Component
public class UserInterceptor implements HandlerInterceptor {

    private void handleUnauthorizedAccess(HttpServletResponse response, String message) throws Exception {
    	 // 특수문자 및 작은따옴표 이스케이프 처리
        String escapedMessage = message.replace("'", "\\'").replace("\"", "\\\"");
        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "<script>" +
                        "alert('" + escapedMessage + "');" +
                        "location.href='/';" +
                        "</script>"
        );
        response.getWriter().flush();

    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        String requestURI = request.getRequestURI();

        if (session == null || session.getAttribute("authInfo") == null) {
            handleUnauthorizedAccess(response, "로그인이 필요합니다.");
            return false;
        }

        AuthInfo authInfo = (AuthInfo) session.getAttribute("authInfo");
        String userRole = authInfo.getRole();

        if (requestURI.startsWith("/admin") && !"ADMIN".equals(userRole)) {
            handleUnauthorizedAccess(response, "권한이 없습니다.");
            return false;
        }

        return true;
    }
    
    

}
