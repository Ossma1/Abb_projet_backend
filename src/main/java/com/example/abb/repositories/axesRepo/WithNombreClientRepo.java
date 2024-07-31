package com.example.abb.repositories.axesRepo;

import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface WithNombreClientRepo extends JpaRepository<AxeWithNombreClientsCartes, Long> {
    List<AxeWithNombreClientsCartes> findByBanqueParticuliersProfessionnels_Id(Long id);

}
