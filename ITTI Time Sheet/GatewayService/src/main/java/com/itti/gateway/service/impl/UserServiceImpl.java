package com.itti.gateway.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.itti.gateway.repository.EmployeeRepository;
import com.itti.gateway.repository.UserRoleRepository;
import com.itti.gateway.service.UserService;
import com.itti.gateway.entities.Employee;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final EmployeeRepository employeeRepository;
    private final UserRoleRepository userRoleRepository;
    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String empCode) {

                List<Employee> employees= employeeRepository.getEmployeeCode(empCode);
                
                if(!employees.isEmpty()){
                   List<String> roles =  userRoleRepository.getRoleByEmployeeCode(empCode);
                   Employee employee = employees.get(0);
                   employee.setRoles(roles);
                   return employee;
                }
                 throw  new UsernameNotFoundException("User not found");
            }
        };
    }
}
