package com.example.abb.models;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@ToString(exclude = {"bkamEntity"})
public class LigneFolioCB2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code_ligne")
    private String codeLigne;  // X01

    @Column(name = "nom_correspondant_bancaire")
    private String nomCorrespondantBancaire;

    @Column(name = "code_bic_correspondant_bancaire")
    private String codeBicCorrespondantBancaire;

    @Column(name = "date_entree_relation")
    private String dateEntreeRelation;  // X04

    @Column(name = "maison_mere_filiale")
    private char maisonMereOuFiliale;  // X05

    @Column(name = "compte_nostro_vostro")
    private char compteNostroVostro;  // X06

    @Column(name = "devise")
    private String devise;  // X07

    @Column(name = "code_pays_agrement")
    private String codePaysAgrement;  // X08

    @Column(name = "niveau_risque_bc_ft")
    private char niveauRisqueBCFT;  // X09

    @Column(name = "profil_risque_beneficiaires")
    private char profilRisqueBeneficiaires;  // X10

    @Column(name = "utilisation_compte_correspondance")
    private char utilisationCompteCorrespondance;  // X11

    @Column(name = "statut_compte")
    private char statutCompte;  // X12

    @Column(name = "flux_emis_nombre")
    private int fluxEmisNombre;  // X13

    @Column(name = "flux_emis_pays_risque")
    private int fluxEmisPaysRisque;  // X14

    @Column(name = "flux_emis_volume")
    private long fluxEmisVolume;  // X15

    @Column(name = "flux_emis_pays_risque_volume")
    private long fluxEmisPaysRisqueVolume;  // X16

    @Column(name = "flux_recus_nombre")
    private int fluxRecusNombre;  // X17

    @Column(name = "flux_recus_pays_risque")
    private int fluxRecusPaysRisque;  // X18

    @Column(name = "flux_recus_volume")
    private long fluxRecusVolume;  // X19

    @Column(name = "flux_recus_pays_risque_volume")
    private long fluxRecusPaysRisqueVolume;  // X20
    @ManyToOne
    @JoinColumn(name = "bkam_entity_id", nullable = false)
    private BkamEntity bkamEntity;
}
