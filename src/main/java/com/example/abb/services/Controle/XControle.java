package com.example.abb.services.Controle;

import com.example.abb.dto.AxesE700.AxeE700;
import com.example.abb.dto.Tables705.CB1;
import com.example.abb.dto.Tables705.CB2;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.dto.axesEntities.AxeWithoutNombreClientsCartes;
import com.example.abb.dto.axesEntities.Axes;
import com.example.abb.models.Anomalies.Anomalie;
import com.example.abb.models.Anomalies.TypeAnomalie;
import com.example.abb.repositories.repoAnomalies.AnomalieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class XControle {
    @Autowired
    AnomalieRepository anomalie705Repository;

    public Anomalie createAnomalie(Axes axe, String controle, TypeAnomalie type) {
        Anomalie anomalie = new Anomalie();
        if(axe instanceof CB1){
            anomalie.setCb1((CB1) axe);
        }else if(axe instanceof CB2){
            anomalie.setCb2((CB2) axe);
        }else if(axe instanceof AxeWithoutNombreClientsCartes){
            anomalie.setProduit((AxeWithoutNombreClientsCartes) axe);
        }else if(axe instanceof AxeWithNombreClientsCartes){
            anomalie.setClient((AxeWithNombreClientsCartes) axe);
        }else if(axe instanceof AxeE700){
            anomalie.setAxeE700((AxeE700) axe);
        }
        anomalie.setControle(controle);
        anomalie.setTypeAnomalie(type);
        return anomalie705Repository.save(anomalie);
    }
}
