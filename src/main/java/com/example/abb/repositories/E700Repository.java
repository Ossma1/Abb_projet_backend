package com.example.abb.repositories;

import com.example.abb.dto.BanqueParticuliersProfessionnels;
import com.example.abb.dto.E700DTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface E700Repository extends JpaRepository<E700DTO, Long> {
    E700DTO findByBkamEntityId(Long bkamEntityId);
}