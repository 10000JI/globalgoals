package dev.globalgoals.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
public class UserLoginFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        //실패시에는 메세지를 줄 수 있다. => 여기서 메세지를 "exception 타입"으로 준다
        String errorMessage = "";
        if(exception instanceof BadCredentialsException) {
            errorMessage="PW 오류";
        }else if(exception instanceof InternalAuthenticationServiceException) {
            errorMessage="ID 확인";
        }else if(exception instanceof DisabledException) {
            errorMessage="유효하지 않은 사용자 입니다.";
            //enabled가 flase인 경우
        }else {
            errorMessage="로그인 실패";
        }

        errorMessage= URLEncoder.encode(errorMessage, "UTF-8");
        response.sendRedirect("/users/login?errorMessage="+errorMessage);
    }

}