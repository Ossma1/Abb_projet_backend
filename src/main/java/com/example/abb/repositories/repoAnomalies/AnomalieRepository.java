package com.example.abb.repositories.repoAnomalies;

import com.example.abb.dto.AxesE700.AxeE700;
import com.example.abb.dto.Tables705.CB1;
import com.example.abb.dto.Tables705.CB2;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.dto.axesEntities.AxeWithoutNombreClientsCartes;
import com.example.abb.models.Anomalies.Anomalie;
import com.example.abb.models.Anomalies.TypeAnomalie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnomalieRepository extends JpaRepository<Anomalie, Long> {
    List<Anomalie> findByCb2(CB2 cb2);
    List<Anomalie> findByCb1(CB1 cb1);
    List<Anomalie> findByClient(AxeWithNombreClientsCartes client);
    List<Anomalie> findByProduit(AxeWithoutNombreClientsCartes produit);
    List<Anomalie> findByAxeE700(AxeE700 axeE700);
    List<Anomalie> findByProduit_Banquefinancement703_Id(Long id);
    List<Anomalie> findByClient_BanqueParticuliersProfessionnels_Id(Long id);
    List<Anomalie> findByCb1_CorrespondanceBancaire705_Id(Long id);
    List<Anomalie> findByCb2_CorrespondanceBancaire705_Id(Long id);
    List<Anomalie> findByAxeE700_E700DTO_Id(Long id);
    void deleteByClient(AxeWithNombreClientsCartes client);
    void deleteByProduit(AxeWithoutNombreClientsCartes produit);
    void deleteByCb1(CB1 cb1);
    void deleteByCb2(CB2 cb2);
    void deleteByAxeE700(AxeE700 axeE700);
    void deleteByCb2AndTypeAnomalie(CB2 cb2, TypeAnomalie type);
    void deleteByCb1AndTypeAnomalie(CB1 cb1, TypeAnomalie type);
    void deleteByClientAndTypeAnomalie(AxeWithNombreClientsCartes client, TypeAnomalie type);
    void deleteByProduitAndTypeAnomalie(AxeWithoutNombreClientsCartes produit, TypeAnomalie type);
    void deleteByAxeE700AndTypeAnomalie(AxeE700 axeE700, TypeAnomalie type);

}
