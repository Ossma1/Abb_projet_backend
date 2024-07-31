package com.example.abb.services.convertion;

import com.example.abb.dto.CorrespondanceBancaire705;
import com.example.abb.dto.Tables705.CB1;
import com.example.abb.dto.Tables705.CB2;
import com.example.abb.mappers.Bkam705Mapper;
import com.example.abb.mappers.Ligne705Mapper;
import com.example.abb.models.*;
import com.example.abb.models.Anomalies.Anomalie;
import com.example.abb.repositories.CBRepository;
import com.example.abb.repositories.ModelRepo.BkamEntityRepository;
import com.example.abb.repositories.ModelRepo.LigneCb1Repo;
import com.example.abb.repositories.ModelRepo.LigneCb2Repo;
import com.example.abb.repositories.PPRepository;
import com.example.abb.repositories.repoAnomalies.AnomalieRepository;
import com.example.abb.repositories.tables705Repo.Cb1Repo;
import com.example.abb.repositories.tables705Repo.Cb2Repo;
import com.example.abb.repositories.tables705Repo.PaysRepo;
import com.example.abb.services.Controle.XControle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class Conversion705Service {
    @Autowired
    private AnomalieRepository anomalie705Repository;
    @Autowired
    private XControle xControle;
    @Autowired
    private PaysRepo paysRepo;
    @Autowired
    private Ligne705Mapper ligne705Mapper;
    @Autowired
    private Bkam705Mapper bkam705mapper;
    @Autowired
    private LigneCb1Repo ligneFolioCB1Repository;  // Injecter le repository
    @Autowired
    private LigneCb2Repo ligneFolioCB2Repository;
    @Autowired
    private Cb1Repo cb1Repository;  // Injecter le repository
    @Autowired
    private Cb2Repo cb2Repository;
    @Autowired
    private BkamEntityRepository bkamEntityRepository;  // I
    @Autowired
    private CBRepository cbRepository;

    public void convertTobkam705mapper(CorrespondanceBancaire705 banque) {
        List<LigneFolioCB1> entities1 = new ArrayList<>();
        List<LigneFolioCB2> entities2 = new ArrayList<>();
        BkamEntity bkamEntity = bkamEntityRepository.save(bkam705mapper.toBkam705Entity(banque));
        List<LigneFolioCB1> lignesCb1 = convertToLigneCB1Entities(banque);
        List<LigneFolioCB2> lignesCb2 = convertToLigneCB2Entities(banque);
        lignesCb1.forEach(ligne -> {
            ligne.setBkamEntity(bkamEntity);
            entities1.add(ligneFolioCB1Repository.save(ligne));
        });
        lignesCb2.forEach(ligne ->{
            ligne.setBkamEntity(bkamEntity);
            ligne.setId(null);
            LigneFolioCB2 savedLigne = ligneFolioCB2Repository.save(ligne);
            entities2.add(savedLigne);
        });
        bkamEntity.setLigneFolioCB1s(entities1);
        bkamEntity.setLigneFolioCB2s(entities2);

        long id = banque.getId();
        boolean hasAnomalies = false;
        int countAnomalies=0;
        int countLignesAnomalies=0;

        List<CB1> cb1List = cb1Repository.findByCorrespondanceBancaire705_Id(id);
        List<CB2> cb2List = cb2Repository.findByCorrespondanceBancaire705_Id(id);
        for (CB1 cb1 : cb1List) {
            List<Anomalie> anomalies = anomalie705Repository.findByCb1(cb1);
            if (!anomalies.isEmpty()) {
                countLignesAnomalies++;
                hasAnomalies = true;
                countAnomalies+=anomalies.size();
            }
        }
        for (CB2 cb2 : cb2List) {
            List<Anomalie> anomalies = anomalie705Repository.findByCb2(cb2);
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
        cbRepository.save(banque);
    }


    public List<LigneFolioCB1> convertToLigneCB1Entities(CorrespondanceBancaire705 banque) {
        return banque.getListCB1().stream()
                .map(axe -> {
                            LigneFolioCB1 ligneCb1=ligne705Mapper.toCB1Entity(axe, paysRepo.findAll(), xControle);
                             return  ligneCb1;
                        }

                )
                .collect(Collectors.toList());
    }

    public List<LigneFolioCB2> convertToLigneCB2Entities(CorrespondanceBancaire705 banque) {
        return banque.getListCB2().stream()
                .map(axe -> {
                            LigneFolioCB2 ligneCb2= ligne705Mapper.toCB2Entity(axe ,paysRepo.findAll(),xControle);
                    return  ligneCb2;
                }
                )
                .collect(Collectors.toList());
    }
}
