package com.example.abb.services.convertion;
import com.example.abb.dto.BanqueParticuliersProfessionnels;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.mappers.Bkam0102Mapper;
import com.example.abb.mappers.LigneX0102Mapper;
import com.example.abb.models.Anomalies.Anomalie;
import com.example.abb.models.BkamEntity;
import com.example.abb.models.LigneX0102;
import com.example.abb.repositories.ModelRepo.BkamEntityRepository;
import com.example.abb.repositories.ModelRepo.LigneX0102Repo;
import com.example.abb.repositories.PPRepository;
import com.example.abb.repositories.axesRepo.WithNombreClientRepo;
import com.example.abb.repositories.repoAnomalies.AnomalieRepository;
import com.example.abb.services.Controle.XControle;
import com.example.abb.services.helper.SortingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class Conversion0102Service {
    @Autowired
    private XControle xControle;
    @Autowired
    private LigneX0102Mapper ligneX0102Mapper;
    @Autowired
    private Bkam0102Mapper bkam0102Mapper;
    @Autowired
    private LigneX0102Repo ligneX0102Repo;
    @Autowired
    private BkamEntityRepository bkamEntityRepository;
    @Autowired
    private WithNombreClientRepo withNombreClientRepo;
    @Autowired
    private AnomalieRepository anomalieRepository;
    @Autowired
    private PPRepository ppRepository;
    @Autowired
    private SortingService sortingService;

    public void convertToBkam(BanqueParticuliersProfessionnels banque) throws Exception {
        BkamEntity bkamEntity = convertToBkamEntite(banque);
        long id = banque.getId();
        boolean hasAnomalies = false;
        int countAnomalies=0;
        int countLignesAnomalies=0;
        List<AxeWithNombreClientsCartes> axesList = withNombreClientRepo.findByBanqueParticuliersProfessionnels_Id(id);
        //lister les axes
        for (AxeWithNombreClientsCartes axe : axesList) {
            List<Anomalie> anomalies = anomalieRepository.findByClient(axe);
            if (!anomalies.isEmpty()) {
                countLignesAnomalies++;
                hasAnomalies = true;
                countAnomalies+=anomalies.size();
            }
        }
        if (hasAnomalies) {
            bkamEntity.setNombreAnomalies(countAnomalies);
            bkamEntity.setNombreLignesError(countLignesAnomalies);
            bkamEntity.setStatus("Failed");
        } else {
            bkamEntity.setStatus("OK");
        }
        banque.setBkamEntity(bkamEntityRepository.save(bkamEntity));
        ppRepository.save(banque);

        System.out.println("Updated BkamEntity with associated lines.");
    }

    public BkamEntity convertToBkamEntite(BanqueParticuliersProfessionnels banque) throws Exception {
        BkamEntity bkamEntity = bkam0102Mapper.toBkamEntity(banque);
        BkamEntity savedBkamEntity = bkamEntityRepository.save(bkamEntity);

        List<LigneX0102> lignes = convertToLigneXEntities(banque);
        lignes.forEach(ligne -> {
            ligne.setBkamEntity(savedBkamEntity);
            System.err.println(ligne);
            ligneX0102Repo.save(ligne);
        });
        return bkamEntityRepository.save(savedBkamEntity);
    }

    public List<LigneX0102> convertToLigneXEntities(BanqueParticuliersProfessionnels banque) throws Exception {
        String state = banque.getEtat();
        List<AxeWithNombreClientsCartes> sortedClients = sortingService.sortAndSave(banque.getClients(), state);
        return sortedClients.stream()
                .map(axe -> ligneX0102Mapper.toEntity(axe, banque.getEtat(), xControle))
                .collect(Collectors.toList());
    }
}