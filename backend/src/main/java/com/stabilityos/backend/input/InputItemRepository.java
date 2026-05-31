package com.stabilityos.backend.input;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InputItemRepository extends JpaRepository<InputItem, Long> {

    List<InputItem> findAllByOrderByCreatedAtDescIdDesc();

    List<InputItem> findByStatusOrderByCreatedAtDescIdDesc(String status);
}