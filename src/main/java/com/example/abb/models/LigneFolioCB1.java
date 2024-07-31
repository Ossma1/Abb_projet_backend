package com.example.abb.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@ToString(exclude = {"bkamEntity"})
public class LigneFolioCB1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "code_ligne")
    private String codeLigne;  // X01

    @Column(name = "code_pays")
    private String codePays;  // X02

    @Column(name = "statut")
    private char statut;  // X03

    @Column(name = "nombre")
    private long nombre;  // X04

    @Column(name = "volume")
    private long volume;  // X05
    @ManyToOne
    @JoinColumn(name = "bkam_entity_id", nullable = false)
    private BkamEntity bkamEntity;
}
