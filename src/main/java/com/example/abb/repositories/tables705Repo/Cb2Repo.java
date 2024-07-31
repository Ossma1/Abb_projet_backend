package com.example.abb.repositories.tables705Repo;


import com.example.abb.dto.Tables705.CB2;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Cb2Repo extends JpaRepository<CB2, Long> {
    List<CB2> findByCorrespondanceBancaire705_Id(Long id);

}

