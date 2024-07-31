package com.example.abb.dto;

import com.example.abb.dto.Tables705.CB1;
import com.example.abb.dto.Tables705.CB2;
import com.example.abb.models.BkamEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class CorrespondanceBancaire705 implements ExcelData{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String Etat;
    private String codeEtablissement;
    private String Annee;
    private Date dateChargement;
    public CorrespondanceBancaire705(String etat, String annee, String codeEtablissement, Date dateChargement) {
        this.Etat = etat;
        this.Annee = annee;
        this.codeEtablissement = codeEtablissement;
        this.dateChargement = dateChargement;
    }
    @OneToMany(mappedBy = "correspondanceBancaire705", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CB1> listCB1;

    @OneToMany(mappedBy = "correspondanceBancaire705", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CB2> listCB2;
    @OneToOne
    @JoinColumn(name = "bkam_entity_id", referencedColumnName = "id")
    private BkamEntity bkamEntity;

    public CorrespondanceBancaire705() {

    }
}
