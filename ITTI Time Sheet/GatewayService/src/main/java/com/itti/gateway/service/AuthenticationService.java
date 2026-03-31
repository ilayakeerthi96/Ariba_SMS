package com.itti.gateway.service;

import com.itti.gateway.dao.request.SigninRequest;
import com.itti.gateway.dao.response.JwtAuthenticationResponse;

public interface AuthenticationService {
    JwtAuthenticationResponse signin(SigninRequest request);
}
