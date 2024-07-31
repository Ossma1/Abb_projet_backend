package com.example.abb.repositories;

import com.example.abb.dto.BanqueParticuliersProfessionnels;
import com.example.abb.dto.CorrespondanceBancaire705;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CBRepository extends JpaRepository<CorrespondanceBancaire705, Long> {
    CorrespondanceBancaire705 findByBkamEntityId(Long bkamEntityId);

}
