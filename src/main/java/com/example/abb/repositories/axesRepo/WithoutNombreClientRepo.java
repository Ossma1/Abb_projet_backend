package com.example.abb.repositories.axesRepo;

import com.example.abb.dto.axesEntities.AxeWithoutNombreClientsCartes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface WithoutNombreClientRepo extends JpaRepository<AxeWithoutNombreClientsCartes, Long> {
    List<AxeWithoutNombreClientsCartes> findByBanquefinancement703_Id(Long id);

}
