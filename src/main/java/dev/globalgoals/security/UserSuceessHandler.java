package dev.globalgoals.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class UserSuceessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        //로그인 성공했을때 쿠키를 만들어 사용자에게 보냄
        //해당 쿠키가 있으면 뿌려줌
        String remember = request.getParameter("remember");

        if(remember !=null && remember.equals("remember")) {

            //Authentication의 userName(id)을 가져옴
            Cookie cookie = new Cookie("remember",authentication.getName());
            //쿠키 유효시간 : 60*60*24=24시간
            cookie.setMaxAge(60*60*24);
            response.addCookie(cookie);
        } else {
            Cookie[] cookies  =request.getCookies();
            for (Cookie cookie : cookies) {
                if(cookie.getName().equals("remember")) {
                    cookie.setMaxAge(0);
                    //쿠키 유효시간 : 0
                    response.addCookie(cookie);
                }
            }
        }

        response.sendRedirect("/");
    }

}
