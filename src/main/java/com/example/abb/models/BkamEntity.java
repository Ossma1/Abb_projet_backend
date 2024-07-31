package com.example.abb.models;


import com.example.abb.dto.BanqueParticuliersProfessionnels;
import com.example.abb.dto.Banquefinancement703;
import com.example.abb.dto.CorrespondanceBancaire705;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class BkamEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_document")
    private String codeDocument;
    @Column(name = "code_etablissement")
    private String codeEtablissement;
    @Column(name = "date_exercice")
    private String dateExercixe;
    @Column(name = "status")
    private String status;
    @Column(name = "nombre_anomalies")
    private int nombreAnomalies;
    @Column(name = "nombre_lignes_error")
    private int nombreLignesError;

    @Column(name = "date_de_chargement")
    private String dateChargement;
    @Column(name = "nombre_document_remis")
    private String nombreDocumentsRemis;
    @Column(name = "nombre_Champs_renseidnes")
    private long     nombreChampsRenseidnes;

    @OneToMany(mappedBy = "bkamEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LigneFolioCB1> ligneFolioCB1s;
    @OneToMany(mappedBy = "bkamEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LigneFolioCB2> ligneFolioCB2s;
    @OneToMany(mappedBy = "bkamEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LigneX> ligneXs;
    @OneToMany(mappedBy = "bkamEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LigneX0102> ligneX0102s;
    @OneToMany(mappedBy = "bkamEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Ligne700> ligne700s;
}
