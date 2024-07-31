package com.example.abb.services.convertion;

import com.example.abb.dto.Banquefinancement703;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.dto.axesEntities.AxeWithoutNombreClientsCartes;
import com.example.abb.mappers.BkamMapper;
import com.example.abb.mappers.LigneXMapper;
import com.example.abb.models.*;
import com.example.abb.models.Anomalies.Anomalie;
import com.example.abb.repositories.BFRepository;
import com.example.abb.repositories.ModelRepo.BkamEntityRepository;
import com.example.abb.repositories.ModelRepo.LigneXRepo;
import com.example.abb.repositories.axesRepo.WithoutNombreClientRepo;
import com.example.abb.repositories.repoAnomalies.AnomalieRepository;
import com.example.abb.services.Controle.XControle;
import com.example.abb.services.helper.SortingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Convertion703Service {

    @Autowired
    private LigneXMapper ligneXMapper;
    @Autowired
    private XControle xControle;
    @Autowired
    private BkamMapper bkamMapper;
    @Autowired
    private BkamEntityRepository bkamEntityRepository;
    @Autowired
    private LigneXRepo ligneXRepo;
    @Autowired
    private WithoutNombreClientRepo WithoutRepo;
    @Autowired
    BFRepository bfRepo;
    @Autowired
    AnomalieRepository anomalie705Repository;
    @Autowired
    private SortingService sortingService;
    public void convertTobkamMapper(Banquefinancement703 banque) throws Exception {
        BkamEntity bkamEntity = convertToBkamEntite(banque);


        long id = banque.getId();
        boolean hasAnomalies = false;
        int countAnomalies=0;
        int countLignesAnomalies=0;
        List<AxeWithoutNombreClientsCartes> axesList = WithoutRepo.findByBanquefinancement703_Id(id);
        for (AxeWithoutNombreClientsCartes axe : axesList) {
            List<Anomalie> anomalies = anomalie705Repository.findByProduit(axe);
            if (!anomalies.isEmpty()) {
                countLignesAnomalies++;
                hasAnomalies = true;
                countAnomalies+=anomalies.size();
            }
        }
        if (hasAnomalies) {
            bkamEntity.setNombreLignesError(countLignesAnomalies);
            bkamEntity.setNombreAnomalies(countAnomalies);
            bkamEntity.setStatus("Failed");
        } else {
            bkamEntity.setStatus("OK");
        }
        banque.setBkamEntity(bkamEntityRepository.save(bkamEntity));
        bfRepo.save(banque);

        System.out.println("Updated BkamEntity with associated lines.");
    }

    public BkamEntity convertToBkamEntite(Banquefinancement703 banque) throws Exception {
        BkamEntity bkamEntity = bkamMapper.toBkamEntity(banque);
        BkamEntity savedBkamEntity = bkamEntityRepository.save(bkamEntity);
         List<LigneX> lignes = convertToLigneXEntities(banque);
        lignes.forEach(ligne -> {
            ligne.setBkamEntity(savedBkamEntity);
            ligneXRepo.save(ligne);
        });
        return bkamEntityRepository.save(savedBkamEntity);
    }
    public List<LigneX> convertToLigneXEntities(Banquefinancement703 banque) throws Exception {
        String state = banque.getEtat();
        List<AxeWithoutNombreClientsCartes> sortedClients = sortingService.sortAndSave(banque.getProduits(), state);
        System.err.println("sorted lignes : "+ sortedClients);
        return sortedClients.stream()
                .map(axe -> ligneXMapper.toEntity(axe,banque.getEtat(),xControle))
                .collect(Collectors.toList());
    }
}
