package com.sysnormal.starters.security.sso.spring.sso_starter.server;

import com.sysnormal.libs.commons.DefaultDataSwap;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestController
public class GlobalErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<DefaultDataSwap> handleError(HttpServletRequest request) {

        Integer statusCode = (Integer)
                request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        HttpStatus status = HttpStatus.resolve(statusCode);

        DefaultDataSwap body = new DefaultDataSwap();
        body.message = "request error";
        body.data = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        return ResponseEntity
                .status(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }
}