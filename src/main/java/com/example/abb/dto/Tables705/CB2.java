package com.example.abb.dto.Tables705;

import com.example.abb.dto.CorrespondanceBancaire705;
import com.example.abb.dto.axesEntities.Axes;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@ToString(exclude = {"correspondanceBancaire705"})
public class CB2 implements Axes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "num_ligne")
    private long numLigne;
    private String nom;
    private String codeBIC;
    @Temporal(TemporalType.DATE)
    private Date dateEntreeEnRelation;
    private String maisonMereOuFiliale;
    private String compteNostroOuVostro;
    private String devise;
    private String paysAgrement;
    private String niveauRisqueBCFTDuPays;
    private String profilRisqueBeneficiairesEffectifs;
    private String utilisationCompteCorrespondanceParTiers;
    private String statutDuCompte;
    private int nombreFluxEmis;
    private int nombreFluxEmisPaysRisque;
    private double volumeFluxEmis;
    private double volumeFluxEmisPaysRisque;
    private int nombreFluxRecus;
    private int nombreFluxRecusPaysRisque;
    private double volumeFluxRecus;
    private double volumeFluxRecusPaysRisque;

    @ManyToOne
    @JoinColumn(name = "cb705_id")
    @JsonBackReference
    private CorrespondanceBancaire705 correspondanceBancaire705;
}
