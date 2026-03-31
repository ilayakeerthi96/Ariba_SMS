
// package com.itti.leadcapturing.repo;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import com.itti.leadcapturing.model.User;
// import java.util.List;
// import java.util.Optional;

// public interface UserRepository extends JpaRepository<User, Long> {
    
//     @Query("SELECT u FROM User u WHERE u.isDeleted = false")
//     List<User> findAll();

//     @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
//     Optional<User> findById(@Param("id") Long id);

//     @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
//     Optional<User> findByEmail(@Param("email") String email);

//     @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.isDeleted = false")
//     boolean existsByEmail(@Param("email") String email);

//     @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id AND u.isDeleted = false")
//     boolean existsById(@Param("id") Long id);
// }


package com.itti.leadcapturing.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import com.itti.leadcapturing.model.User;
import java.util.List;
import java.util.Optional;

/**
 * ✅ UPDATED: Added Entity Graph method for login to fetch relationships eagerly
 * while keeping default LAZY loading for other operations
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find all non-deleted users (LAZY loading)
     */
    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    List<User> findAll();

    /**
     * Find user by ID (LAZY loading)
     */
    @Query("SELECT u FROM User u WHERE u.id = :id AND u.isDeleted = false")
    Optional<User> findById(@Param("id") Long id);

    /**
     * ✅ NEW: Find user by email WITH all relationships loaded (for login)
     * Uses Entity Graph to fetch department, location, and buyer eagerly
     */
    @EntityGraph(attributePaths = {
        "department", 
        "department.location", 
        "department.location.buyer", 
        "location", 
        "location.buyer",
        "buyer"
    })
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmailWithRelationships(@Param("email") String email);

    /**
     * Find user by email (LAZY loading - for other use cases)
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isDeleted = false")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * Check if user exists by email
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email AND u.isDeleted = false")
    boolean existsByEmail(@Param("email") String email);

    /**
     * Check if user exists by ID
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id AND u.isDeleted = false")
    boolean existsById(@Param("id") Long id);
}