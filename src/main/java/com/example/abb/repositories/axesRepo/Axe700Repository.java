package com.example.abb.repositories.axesRepo;

import com.example.abb.dto.AxesE700.AxeE700;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Axe700Repository extends JpaRepository<AxeE700, Long> {
    List<AxeE700> findByE700DTO_Id(Long id);

}