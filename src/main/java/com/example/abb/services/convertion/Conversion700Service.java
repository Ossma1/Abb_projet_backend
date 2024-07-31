package com.example.abb.services.convertion;

import com.example.abb.dto.AxesE700.AxeE700;
import com.example.abb.dto.E700DTO;
import com.example.abb.mappers.Bkam700Mapper;
import com.example.abb.mappers.Ligne700Mapper;
import com.example.abb.models.Anomalies.Anomalie;
import com.example.abb.models.BkamEntity;
import com.example.abb.models.Ligne700;

import com.example.abb.repositories.E700Repository;
import com.example.abb.repositories.ModelRepo.BkamEntityRepository;
import com.example.abb.repositories.ModelRepo.Ligne700Repo;
import com.example.abb.repositories.axesRepo.Axe700Repository;
import com.example.abb.repositories.repoAnomalies.AnomalieRepository;
import com.example.abb.services.Controle.XControle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Conversion700Service {

    @Autowired
    private Ligne700Repo ligne700Repository;
    @Autowired
    private XControle xControle;
    @Autowired
    private Bkam700Mapper bkamMapper;
    @Autowired
    private BkamEntityRepository bkamEntityRepository;

    @Autowired
    private Axe700Repository axe700Repository;
    @Autowired
    private E700Repository e700Repository;
    @Autowired
    AnomalieRepository anomalie705Repository;

    @Autowired
    private Ligne700Mapper ligne700Mapper;

    public void convertTobkam(E700DTO banque) throws Exception {
        BkamEntity bkamEntity = convertToBkamEntite(banque);


        long id = banque.getId();
        boolean hasAnomalies = false;
        int countAnomalies=0;
        int countLignesAnomalies=0;
        List<AxeE700> axesList = axe700Repository.findByE700DTO_Id(id);
        for (AxeE700 axe : axesList) {
            List<Anomalie> anomalies = anomalie705Repository.findByAxeE700(axe);
            if (!anomalies.isEmpty()) {
                countLignesAnomalies++;
                hasAnomalies = true;
                countAnomalies+=anomalies.size();
            }
        }
        System.err.println("anomalies :" +hasAnomalies);

        if (hasAnomalies) {
            bkamEntity.setNombreLignesError(countLignesAnomalies);
            bkamEntity.setNombreAnomalies(countAnomalies);
            bkamEntity.setStatus("Failed");
        } else {
            bkamEntity.setStatus("OK");
        }
        banque.setBkamEntity(bkamEntityRepository.save(bkamEntity));
        e700Repository.save(banque);

        System.out.println("Updated BkamEntity with associated lines.");
    }

    public BkamEntity convertToBkamEntite(E700DTO banque) throws Exception {
        BkamEntity bkamEntity = bkamMapper.toBkam705Entity(banque);
        BkamEntity savedBkamEntity = bkamEntityRepository.save(bkamEntity);
        List<Ligne700> lignes = convertToLigneXEntities(banque);
        System.err.println("sorted lignes : "+ lignes.size());

        savedBkamEntity.setNombreChampsRenseidnes(lignes.size());
        lignes.forEach(ligne -> {
            ligne.setBkamEntity(savedBkamEntity);
            ligne700Repository.save(ligne);
        });
        return bkamEntityRepository.save(savedBkamEntity);
    }
    public List<Ligne700> convertToLigneXEntities(E700DTO banque) throws Exception {
       // List<AxeE700> sortedAxes = sortingService.sortAndSave(banque.getProduits(), state);
        List<AxeE700> sortedAxes = banque.getAxes();
        System.err.println("sorted lignes : "+ sortedAxes);
        return sortedAxes.stream()
                .flatMap(axe -> ligne700Mapper.toEntity(axe, xControle).stream())
                .collect(Collectors.toList());
    }
}
