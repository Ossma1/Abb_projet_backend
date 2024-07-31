package com.example.abb.repositories.tables705Repo;

import com.example.abb.dto.Tables705.CB1;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Cb1Repo extends JpaRepository<CB1, Long> {
    List<CB1> findByCorrespondanceBancaire705_Id(Long id);

}

