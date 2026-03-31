package com.itti.gateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.itti.gateway.entities.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long>{

	@Query(value = "SELECT e.userRoles FROM UserRole e WHERE e.employeeCode =:employeeCode")
	List<String> getRoleByEmployeeCode(String employeeCode);

//	@Transactional
//	@Modifying(clearAutomatically = true)
//	@Query("update UserRole e "
//			+ "set e.userRoles = :userRoles "
//			+ "where  e.userRoleId =: userRoleId")
//	int updateUserRole(String employeeCode, String userRoles, Long userRoleId);

	@Query(value = "SELECT e FROM UserRole e WHERE e.employeeCode =:employeeCode")
	List<UserRole> getUserRole(String employeeCode);

	@Transactional
	@Modifying
	@Query("delete FROM UserRole e WHERE e.employeeCode =:employeeCode")
	void deleteUserRole(String employeeCode);

}
