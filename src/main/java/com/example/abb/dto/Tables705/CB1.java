package com.example.abb.dto.Tables705;

import com.example.abb.dto.CorrespondanceBancaire705;
import com.example.abb.dto.axesEntities.Axes;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString(exclude = {"correspondanceBancaire705"})
public class CB1 implements Axes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "num_ligne")
    private long numLigne;
    private String totalFluxType;
    private String pays;
    private int nombreValue;
    private int volume;

    @ManyToOne
    @JoinColumn(name = "cb705_id")
    @JsonBackReference
    private CorrespondanceBancaire705 correspondanceBancaire705;
}