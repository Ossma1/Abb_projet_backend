package com.example.abb.dto;

import com.example.abb.dto.axesEntities.*;
import com.example.abb.models.BkamEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Data
public class Banquefinancement703 implements ExcelData{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String Etat;
    private String Annee;
    private String codeEtablissement;
    private Date dateChargement;
    public Banquefinancement703(String etat, String annee, String codeEtablissement, Date dateChargement) {
        this.Etat = etat;
        this.Annee = annee;
        this.codeEtablissement = codeEtablissement;
        this.dateChargement = dateChargement;
    }
    public Banquefinancement703() {

    }
    @OneToMany(mappedBy = "banquefinancement703", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AxeWithoutNombreClientsCartes> produits;

    @OneToOne
    @JoinColumn(name = "bkam_entity_id", referencedColumnName = "id")
    private BkamEntity bkamEntity;
}
