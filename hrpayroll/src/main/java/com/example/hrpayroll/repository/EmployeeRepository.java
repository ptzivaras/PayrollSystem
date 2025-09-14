package com.example.hrpayroll.repository;

import com.example.hrpayroll.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Employee> findAllByDepartment_Id(Long departmentId, Pageable pageable);

    @Query(
            value = """
          SELECT *
          FROM employee e
          WHERE (:q IS NULL OR :q = '' OR
                 e.first_name ILIKE CONCAT('%', :q, '%') OR
                 e.last_name  ILIKE CONCAT('%', :q, '%') OR
                 e.email      ILIKE CONCAT('%', :q, '%'))
          ORDER BY e.last_name ASC
        """,
            countQuery = """
          SELECT COUNT(*)
          FROM employee e
          WHERE (:q IS NULL OR :q = '' OR
                 e.first_name ILIKE CONCAT('%', :q, '%') OR
                 e.last_name  ILIKE CONCAT('%', :q, '%') OR
                 e.email      ILIKE CONCAT('%', :q, '%'))
        """,
            nativeQuery = true
    )
    Page<Employee> search(@Param("q") String q, Pageable pageable);
}
