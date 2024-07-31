package com.example.abb.dto.axesEntities;
import com.example.abb.dto.Banquefinancement703;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "axe_sans_client_cartes")
@ToString(exclude = {"banquefinancement703"})
public class AxeWithoutNombreClientsCartes implements Axes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "num_ligne")
    private long NumLigne;
    private String axes;
    private String codeAxes;
    private String libelleAxes;
    private int nombreClients;
    @Column(name = "encours_depots", nullable = true)
    private double encoursDepots;
    private double fluxDebiteurs2020;
    private double fluxCrediteurs2020;
    private String risqueInherent;
    @Column(length = 2000)
    private String commentaires;
    @ManyToOne
    @JoinColumn(name = "bf703_id")
    @JsonBackReference
    private Banquefinancement703 banquefinancement703;
}