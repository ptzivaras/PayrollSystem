package com.example.hrpayroll.repository;

import com.example.hrpayroll.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);

    Page<Department> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
