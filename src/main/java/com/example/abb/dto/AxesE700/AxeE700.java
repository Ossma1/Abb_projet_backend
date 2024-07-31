package com.example.abb.dto.AxesE700;

import com.example.abb.dto.E700DTO;
import com.example.abb.dto.axesEntities.Axes;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@Table(name = "axe_Etat_700")
@ToString(exclude = {"e700DTO"})
public class AxeE700 implements Axes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_ligne")
    private long numLigne;

    private String LigneDocument;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "trafic_illicite_stupefiants_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "trafic_illicite_stupefiants_value"))
    })
    private ColumnValue traficIlliciteStupefiants;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "trafic_etres_humains_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "trafic_etres_humains_value"))
    })
    private ColumnValue traficEtresHumains;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "trafic_immigrants_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "trafic_immigrants_value"))
    })
    private ColumnValue traficImmigrants;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "trafic_illicite_armes_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "trafic_illicite_armes_value"))
    })
    private ColumnValue traficIlliciteArmes;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "corruption_concussion_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "corruption_concussion_value"))
    })
    private ColumnValue corruptionConcussion;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "contrefacon_monnaies_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "contrefacon_monnaies_value"))
    })
    private ColumnValue contrefaconMonnaies;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "exploitation_sexuelle_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "exploitation_sexuelle_value"))
    })
    private ColumnValue exploitationSexuelle;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "abus_confiance_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "abus_confiance_value"))
    })
    private ColumnValue abusConfiance;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "escroquerie_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "escroquerie_value"))
    })
    private ColumnValue escroquerie;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "vol_extorsion_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "vol_extorsion_value"))
    })
    private ColumnValue volExtorsion;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "contrebande_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "contrebande_value"))
    })
    private ColumnValue contrebande;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "fraude_marchandises_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "fraude_marchandises_value"))
    })
    private ColumnValue fraudeMarchandises;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "faux_usage_faux_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "faux_usage_faux_value"))
    })
    private ColumnValue fauxUsageFaux;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "attelonge_systemes_traitement_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "attelonge_systemes_traitement_value"))
    })
    private ColumnValue attelongeSystemesTraitement;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "financement_terrorisme_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "financement_terrorisme_value"))
    })
    private ColumnValue financementTerrorisme;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "columnNumber", column = @Column(name = "total_column_number")),
            @AttributeOverride(name = "value", column = @Column(name = "total_value"))
    })
    private ColumnValue total;

    @ManyToOne
    @JoinColumn(name = "e700DTO_id")
    @JsonBackReference
    private E700DTO e700DTO;
}
