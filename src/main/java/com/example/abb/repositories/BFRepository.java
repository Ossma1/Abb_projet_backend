package com.example.abb.repositories;

import com.example.abb.dto.Banquefinancement703;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BFRepository extends JpaRepository<Banquefinancement703, Long> {
    Banquefinancement703 findByBkamEntityId(Long bkamEntityId);

}
