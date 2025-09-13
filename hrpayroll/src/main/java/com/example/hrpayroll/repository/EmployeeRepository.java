package com.example.hrpayroll.repository;

import com.example.hrpayroll.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Employee> findAllByDepartment_Id(Long departmentId, Pageable pageable);

    @Query("""
           select e
             from Employee e
            where (:q is null or lower(e.firstName) like lower(concat('%', :q, '%'))
                               or lower(e.lastName)  like lower(concat('%', :q, '%'))
                               or lower(e.email)     like lower(concat('%', :q, '%')))
           """)
    Page<Employee> search(String q, Pageable pageable);
}
