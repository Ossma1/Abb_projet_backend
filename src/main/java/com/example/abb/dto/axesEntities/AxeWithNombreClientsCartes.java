package com.example.abb.dto.axesEntities;


import com.example.abb.dto.BanqueParticuliersProfessionnels;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "axe_avec_client_cartes")
@ToString(exclude = {"banqueParticuliersProfessionnels"})
public class AxeWithNombreClientsCartes implements Axes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_ligne")
    private long numLigne;

    private String axes;
    private String codeAxes;
    private String libelleAxes;
    private long nombreClients;

    @Column(name = "encours_depots", nullable = true)
    private double encoursDepots;

    private double fluxDebiteurs2020;
    private double fluxCrediteurs2020;
    private long nombreClientsCartes;
    private String risqueInherent;
    @Column(length = 2000)
    private String commentaires;

    @ManyToOne
    @JoinColumn(name = "bpp701_id")
    @JsonBackReference
    private BanqueParticuliersProfessionnels banqueParticuliersProfessionnels;
}
