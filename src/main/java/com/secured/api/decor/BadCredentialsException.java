package com.secured.api.decor;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.UNAUTHORIZED, reason = "wrong credentials")
public class BadCredentialsException extends RuntimeException {
}
