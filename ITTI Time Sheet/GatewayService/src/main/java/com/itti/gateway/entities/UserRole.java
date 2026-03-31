package com.itti.gateway.entities;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_role")
public class UserRole {

	@Id
	@Column(name = "user_role_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long userRoleId;

	@Column(name = "user_roles")
	public String userRoles;

	@Column(name = "employee_code")
	public String employeeCode;

	@Column(name = "created_date")
	public Timestamp createdDate;

	@Column(name = "deleted")
	public int deleted;

	public Long getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(Long userRoleId) {
		this.userRoleId = userRoleId;
	}

	public String getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(String userRoles) {
		this.userRoles = userRoles;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "UserRole [userRoleId=" + userRoleId + ", userRoles=" + userRoles + ", employeeCode=" + employeeCode + ", createdDate="
				+ createdDate + ", deleted=" + deleted + "]";
	}

}
