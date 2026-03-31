package com.itti.gateway.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "employee")
public class Employee implements UserDetails {

	@Id
	@Column(name = "employee_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long employeeId;

	@Column(name = "employee_code")
	public String employeeCode;

	@Column(name = "employee_reporting_code")
	public String employeeReportingCode;

	@Column(name = "employee_reporting_name")
	public String employeeReportingName;

	@Column(name = "salutation")
	public String salutation;

	@Column(name = "first_name")
	public String firstName;

	@Column(name = "middle_name")
	public String middleName;

	@Column(name = "last_name")
	public String lastName;

	@Column(name = "gender_id")
	public Long genderId;

	@Column(name = "email_id")
	public String emailId;

	@Column(name = "password")
	public String password;

	@Column(name = "dob")
	public String dob;

	@Column(name = "personal_mail_id")
	public String personalMailId;

	@Column(name = "mobile_number")
	public String mobileNumber;

	@Column(name = "practice_id")
	public Long practiceId;

	@Column(name = "practice_name")
	public String practiceName;

	@Column(name = "profile_image")
	public String profileImage;

	@Column(name = "created_date")
	public Timestamp createdDate;

	@Column(name = "joining_date")
	public String joiningDate;

	@Column(name = "separation_date")
	public String separationDate;

	@Column(name = "grade")
	public String grade;

	@Column(name = "designation")
	public String designation;

	@Column(name = "location_id")
	public Long locationId;

	@Column(name = "employee_status")
	public String employeeStatus;

	@Column(name = "aadhar_number")
	public String aadharNumber;

	@Column(name = "pan_number")
	public String panNumber;

	@Column(name = "gst_number")
	public String gstNumber;

	@Column(name = "uin_number")
	public String uinNumber;

	@Column(name = "deleted")
	public int deleted;

	@Override
	public String getUsername() {
		return this.getEmployeeCode();
	}

	@Transient
    private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> rolesGrantList =new ArrayList<>();
		for (String role : roles) {
			rolesGrantList.add(new SimpleGrantedAuthority(role));
		}
        return rolesGrantList;
    }

	
	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeCode() {
		return employeeCode;
	}

	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getEmployeeReportingCode() {
		return employeeReportingCode;
	}

	public void setEmployeeReportingCode(String employeeReportingCode) {
		this.employeeReportingCode = employeeReportingCode;
	}

	public String getEmployeeReportingName() {
		return employeeReportingName;
	}

	public void setEmployeeReportingName(String employeeReportingName) {
		this.employeeReportingName = employeeReportingName;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Long getGenderId() {
		return genderId;
	}

	public void setGenderId(Long genderId) {
		this.genderId = genderId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getPersonalMailId() {
		return personalMailId;
	}

	public void setPersonalMailId(String personalMailId) {
		this.personalMailId = personalMailId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Long getPracticeId() {
		return practiceId;
	}

	public void setPracticeId(Long practiceId) {
		this.practiceId = practiceId;
	}

	public String getPracticeName() {
		return practiceName;
	}

	public void setPracticeName(String practiceName) {
		this.practiceName = practiceName;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public Timestamp getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Timestamp createdDate) {
		this.createdDate = createdDate;
	}

	public String getJoiningDate() {
		return joiningDate;
	}

	public void setJoiningDate(String joiningDate) {
		this.joiningDate = joiningDate;
	}

	public String getSeparationDate() {
		return separationDate;
	}

	public void setSeparationDate(String separationDate) {
		this.separationDate = separationDate;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	public String getEmployeeStatus() {
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus) {
		this.employeeStatus = employeeStatus;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getUinNumber() {
		return uinNumber;
	}

	public void setUinNumber(String uinNumber) {
		this.uinNumber = uinNumber;
	}

	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int deleted) {
		this.deleted = deleted;
	}

	

	@Override
	public String toString() {
		return "Employee [employeeId=" + employeeId + ", employeeCode=" + employeeCode + ", employeeReportingCode="
				+ employeeReportingCode + ", employeeReportingName=" + employeeReportingName + ", salutation="
				+ salutation + ", firstName=" + firstName + ", middleName=" + middleName + ", lastName=" + lastName
				+ ", genderId=" + genderId + ", emailId=" + emailId + ", password=" + password + ", dob=" + dob
				+ ", personalMailId=" + personalMailId + ", mobileNumber=" + mobileNumber + ", practiceId=" + practiceId
				+ ", practiceName=" + practiceName + ", profileImage=" + profileImage + ", createdDate=" + createdDate
				+ ", joiningDate=" + joiningDate + ", separationDate=" + separationDate + ", grade=" + grade
				+ ", designation=" + designation + ", locationId=" + locationId + ", employeeStatus=" + employeeStatus
				+ ", aadharNumber=" + aadharNumber + ", panNumber=" + panNumber + ", gstNumber=" + gstNumber
				+ ", uinNumber=" + uinNumber + ", deleted=" + deleted + "]";
	}


	public List<String> getRoles() {
		return roles;
	}


	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	


}
