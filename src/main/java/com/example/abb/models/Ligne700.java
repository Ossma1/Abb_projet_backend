package com.example.abb.models;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ligne700 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "code_monnaie")
    private long codeMonnaie =3; // C1

    @Column(name = "ligne_document")
    private String ligneDocument; // C2

    @Column(name = "rang_colonne")
    private long rangColonne; // C3

    @Column(name = "sens")
    private Character sens; // C4

    @Column(name = "montant")
    private long montant; // C5
    @ManyToOne
    @JoinColumn(name = "bkam_entity_id", nullable = false)
    private BkamEntity bkamEntity;
}
