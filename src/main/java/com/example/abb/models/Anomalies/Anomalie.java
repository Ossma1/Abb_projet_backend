package com.example.abb.models.Anomalies;

import com.example.abb.dto.AxesE700.AxeE700;
import com.example.abb.dto.Tables705.CB1;
import com.example.abb.dto.Tables705.CB2;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.dto.axesEntities.AxeWithoutNombreClientsCartes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Anomalie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "controle")
    private String controle;
    @Enumerated(EnumType.STRING)
    private TypeAnomalie typeAnomalie;

    @ManyToOne
    private CB1 cb1;
    @ManyToOne
    private CB2 cb2;
    @ManyToOne
    private AxeE700 axeE700;
    @ManyToOne
    private AxeWithNombreClientsCartes client;
    @ManyToOne
    private AxeWithoutNombreClientsCartes produit ;
}
