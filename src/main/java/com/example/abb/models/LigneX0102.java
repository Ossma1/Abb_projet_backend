package com.example.abb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@ToString(exclude = {"bkamEntity"})
public class LigneX0102 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code_ligne")
    private String codeLigne;  // X01

    @Column(name = "identifiant_ligne")
    private String identifiantLigne;  // X02

    @Column(name = "nombre_clients_operations")
    private  long nombreClientsOperations;  // X03

    @Column(name = "encours_depots_placements" )
    private  long encoursDepotsPlacements;  // X04

    @Column(name = "flux_debiteurs_2019" )
    private  long fluxDebiteurs2019;  // X05

    @Column(name = "flux_crediteurs_2019" )
    private  long fluxCrediteurs2019;  // X06

    @Column(name = "nombre_clients_cartes_prepayes" )
    private  long nombreClientsCartesPrepayes;  // X07

    @Column(name = "risque_inherent")
    private String risqueInherent;  // X08

    @Lob
    private String b001 ;

    @Lob
    private String b002 ;

    @Lob
    private String b003;

    @Lob
    private String b004 ;
    @ManyToOne
    @JoinColumn(name = "bkam_entity_id", nullable = false)
    private BkamEntity bkamEntity;
}
