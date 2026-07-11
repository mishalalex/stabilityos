package com.stabilityos.backend.burden;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CognitiveBurdenRepository extends JpaRepository<CognitiveBurden, Long> {

    List<CognitiveBurden> findAllByOrderByCreatedAtDescIdDesc();

    List<CognitiveBurden> findByStatusOrderByCreatedAtDescIdDesc(String status);
}
