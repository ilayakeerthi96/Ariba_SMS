package com.itti.gateway.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.itti.gateway.dao.request.SigninRequest;
import com.itti.gateway.dao.response.JwtAuthenticationResponse;
import com.itti.gateway.entities.Employee;
import com.itti.gateway.repository.EmployeeRepository;
import com.itti.gateway.repository.UserRoleRepository;
import com.itti.gateway.service.AuthenticationService;
import com.itti.gateway.service.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final EmployeeRepository employeeRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
      
    @Value("${token.expirationTime}")
     private Long expirationTimeInMillis;
 
     @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmployeeCode(), request.getPassword()));
        List<Employee> employees = employeeRepository.getEmployeeCode(request.getEmployeeCode());
        List<String> roles = userRoleRepository.getRoleByEmployeeCode(request.getEmployeeCode());
        if(employees.isEmpty()) {
            throw new IllegalArgumentException("Invalid user or password");
        }
        Employee employee = employees.get(0);
        employee.setRoles(roles);
        Date  expirationTime = new Date(System.currentTimeMillis() + expirationTimeInMillis);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = formatter.format(expirationTime);

        var jwt = jwtService.generateToken(employee,expirationTime);

        return JwtAuthenticationResponse.builder().token(jwt).expirationTime(formattedDate).build();        
    }  
}
