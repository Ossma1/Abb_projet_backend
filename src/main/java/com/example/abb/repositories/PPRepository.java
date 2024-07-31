package com.example.abb.repositories;
import com.example.abb.dto.BanqueParticuliersProfessionnels;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PPRepository extends JpaRepository<BanqueParticuliersProfessionnels, Long> {
    BanqueParticuliersProfessionnels findByBkamEntityId(Long bkamEntityId);
}
