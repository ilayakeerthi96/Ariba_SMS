package com.itti.gateway.repository;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itti.gateway.entities.Employee;



public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	@Query("SELECT e FROM Employee e WHERE e.employeeCode = :employeeCode")
	List<Employee> getEmployeeCode(String employeeCode);

	@Query("SELECT e.password FROM Employee e WHERE e.employeeId = :employeeId")
	String getPasswordByEmailId(Long employeeId);

	@Query("SELECT e FROM Employee e WHERE e.employeeCode = :employeeCode")
	List<Employee> loginValidationByEmployeeID(String employeeCode);

	@Transactional
	@Modifying
	@Query("update Employee e " + "set e.password =:password " + "  where e.employeeCode =:employeeCode")
	int newPassword(String employeeCode, String password);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("update Employee e "
			+ "set e.employeeCode = :employeeCode, employeeReportingCode = :employeeReportingCode, employeeReportingName = :employeeReportingName, firstName = :firstName, middleName = :middleName, lastName = :lastName,"
			+ "e.genderId = :genderId, emailId = :emailId, dob = :dob,"
			+ "e.joiningDate = :joiningDate, separationDate = :separationDate, grade = :grade, designation = :designation, locationId = :locationId,"
			+ "e.aadharNumber = :aadharNumber, panNumber = :panNumber, gstNumber = :gstNumber, uinNumber = :uinNumber, mobileNumber = :mobileNumber, personalMailId = :personalMailId,"
			+ "e.employeeStatus = :employeeStatus, e.salutation = :salutation, practiceId = :practiceId, practiceName = :practiceName" + "  where  e.employeeId =:employeeId")
	int updateEmployee(String employeeCode, String employeeReportingCode, String firstName, String middleName,
			String lastName, Long genderId, String emailId, String dob,
			String joiningDate, String separationDate, String grade, String designation,
			Long locationId, String employeeStatus, Long employeeId, String salutation, String uinNumber,
			String gstNumber, String panNumber, String aadharNumber, String mobileNumber, String personalMailId,
			String employeeReportingName, Long practiceId, String practiceName);

	@Query("SELECT e FROM Employee e WHERE e.practiceName IN :practiceName AND e.employeeStatus IN :employeeStatus ")
	Page<Employee> findByPractice(@Param("practiceName") List<String> practiceName, @Param("employeeStatus") List<String> employeeStatus, Pageable pageable);

	@Query("SELECT e FROM Employee e WHERE e.designation IN :designation AND e.employeeStatus IN :employeeStatus")
	Page<Employee> findByDesignation(@Param("designation") List<String> designation, @Param("employeeStatus") List<String> employeeStatus, Pageable pageable);

	@Query("SELECT e FROM Employee e WHERE e.grade IN :grade AND e.employeeStatus IN :employeeStatus")
	Page<Employee> findByGrade(@Param("grade") List<String> grade, @Param("employeeStatus") List<String> employeeStatus, Pageable pageable);

	@Query("SELECT e FROM Employee e WHERE e.practiceName IN :practiceName AND e.designation IN :designation AND e.employeeStatus IN :employeeStatus")
	Page<Employee> findByPracticeAndDesignation(@Param("practiceName") List<String> practiceName, @Param("designation") List<String> designation, @Param("employeeStatus") List<String> employeeStatus, Pageable pageable);

	@Query("SELECT e FROM Employee e WHERE e.designation IN :designation AND e.grade IN :grade AND e.employeeStatus IN :employeeStatus")
	Page<Employee> findByDesignationAndGrade(@Param("designation") List<String> designation, @Param("grade") List<String> grade, @Param("employeeStatus") List<String> employeeStatus, Pageable pageable);

	@Query("SELECT e FROM Employee e WHERE e.practiceName IN :practiceName AND e.grade IN :grade AND e.employeeStatus IN :employeeStatus")
	Page<Employee> findByPracticeAndGrade(@Param("practiceName") List<String> practiceName, @Param("grade") List<String> grade, @Param("employeeStatus") List<String> employeeStatus, Pageable pageable);

	@Query("SELECT e FROM Employee e WHERE e.practiceName IN :practiceName AND e.designation IN :designation AND e.grade IN :grade AND e.employeeStatus IN :employeeStatus")
	Page<Employee> findByFilter(@Param("practiceName") List<String> practiceName, @Param("designation") List<String> designation, @Param("grade") List<String> grade,
			@Param("employeeStatus") List<String> employeeStatus, Pageable pageable);
	
	@Query("SELECT e FROM Employee e WHERE e.practiceName LIKE %:value% OR e.designation LIKE %:value% OR e.grade LIKE %:value% OR e.employeeStatus  LIKE %:value% OR e.firstName LIKE %:value%")
	List<Employee> searchEmployoee(String value);

	@Query("SELECT e FROM Employee e WHERE e.employeeStatus IN :employeeStatus")
	Page<Employee> findByEmployeeStatus(@Param("employeeStatus") List<String> employeeStatus, Pageable pageable);

	@Query("SELECT e FROM Employee e WHERE e.employeeReportingCode = :employeeReportingCode")
	List<Employee> getEmployeeListByManagerId(String employeeReportingCode);

	@Query("SELECT e.firstName FROM Employee e WHERE e.employeeId = :employeeId")
	String getEmployeeNameByEmployeeId(Long employeeId);
	
	@Query("SELECT e FROM Employee e WHERE e.employeeCode LIKE %:value% OR e.firstName LIKE %:value%")
	List<Employee> seachEmployeeByManagerId(String value);

	@Query("SELECT e FROM Employee e WHERE e.employeeId IN :employeeId")
	List<Employee> getEmployeeDetailsByEmployeeId(List<Long> employeeId);

	@Query("SELECT e FROM Employee e WHERE e.employeeCode =:employeeCode")
	List<Employee> getEmployeeDetailsByEmployeeCode(String employeeCode);

	@Query("SELECT e.practiceName FROM Employee e WHERE e.employeeId =:employeeId")
	List<String> getPracticeNameByEmployeeId(Long employeeId);

	@Query("SELECT e.firstName FROM Employee e WHERE e.employeeId IN :employeeId")
	List<String> getEmployeeNameByEmplyoeeList(List<Long> employeeId);

	@Query("SELECT e.joiningDate FROM Employee e WHERE e.joiningDate BETWEEN :startDate AND :endDate")
	List<String> getNewJoinByMonth(String startDate, String endDate);

	@Query("SELECT e.separationDate FROM Employee e WHERE e.separationDate BETWEEN :startDate AND :endDate")
	List<String> getSeparationByMonth(String startDate, String endDate);

	@Query("SELECT COUNT(e.locationId) FROM Employee e WHERE e.locationId = :locationId AND e.joiningDate BETWEEN :startDate AND :endDate")
	int getEmpActiveCountByLocation(Long locationId, String startDate, String endDate);

	@Query("SELECT COUNT(e.joiningDate) FROM Employee e WHERE e.joiningDate BETWEEN :startDate AND :endDate")
	int getNewJoinByMonthCount(String startDate, String endDate);

	@Query("SELECT COUNT(e.separationDate) FROM Employee e WHERE e.separationDate BETWEEN :startDate AND :endDate")
	int getSeparationByMonthCount(String startDate, String endDate);



}
