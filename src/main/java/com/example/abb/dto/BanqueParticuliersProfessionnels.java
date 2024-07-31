    package com.example.abb.dto;

    import com.example.abb.dto.axesEntities.*;
    import com.example.abb.models.BkamEntity;
    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import lombok.Data;

    import javax.persistence.*;

    import java.util.Date;
    import java.util.List;
    @Entity
    @Data
    public class BanqueParticuliersProfessionnels implements ExcelData {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String Etat;
        private String codeEtablissement;
        private String Annee;
        private Date dateChargement;

        @OneToMany(mappedBy = "banqueParticuliersProfessionnels", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<AxeWithNombreClientsCartes> clients;

        @OneToOne
        @JoinColumn(name = "bkam_entity_id", referencedColumnName = "id")
        private BkamEntity bkamEntity;

        public BanqueParticuliersProfessionnels(String etat, String annee, String codeEtablissement, Date dateChargement) {
            this.Etat = etat;
            this.Annee = annee;
            this.codeEtablissement = codeEtablissement;
            this.dateChargement = dateChargement;
        }
        public BanqueParticuliersProfessionnels() {

        }
    }
