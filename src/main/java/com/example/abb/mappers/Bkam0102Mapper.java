package com.example.abb.mappers;

import com.example.abb.dto.BanqueParticuliersProfessionnels;
import com.example.abb.models.BkamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.text.SimpleDateFormat;
import java.util.Date;
@Mapper(componentModel = "spring")  // Utilisez Spring pour l'injection de dépendances
public interface Bkam0102Mapper {
    Bkam0102Mapper INSTANCE = Mappers.getMapper(Bkam0102Mapper.class);
    @Mapping(target = "id", ignore = true) // Ignore mapping ID from source to target
    @Mapping(target = "codeEtablissement", source = "codeEtablissement")
    @Mapping(target = "dateExercixe", source = "annee")
    @Mapping(source = "dateChargement", target = "dateChargement", qualifiedByName = "convertFormeDate")
    @Mapping(target = "nombreDocumentsRemis", constant = "001")//for now specifie 1 seul doc
    @Mapping(target = "codeDocument", source = "etat")
    @Mapping(source=".",target = "nombreChampsRenseidnes",qualifiedByName ="calculerNombreChampsRenseidnes")
    BkamEntity toBkamEntity(BanqueParticuliersProfessionnels source);

    @Named("calculerNombreChampsRenseidnes")
    default int calculerNombreChampsRenseidnes(BanqueParticuliersProfessionnels source) {
        if (source.getClients() == null) {
            return 0;
        }
        return source.getClients().size()+source.getClients().size() * 4;
    }

    @Named("convertFormeDate")
    default String convertFormeDate(Date dateEntreeEnRelation) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("mmssHHddMMyyyy");
            String formattedDate = dateFormat.format(dateEntreeEnRelation);
            System.out.println("Formatted Date: " + formattedDate);
            return formattedDate;
        } catch (Exception e) {
            System.err.println("Error formatting date: " + dateEntreeEnRelation + ". Error: " + e.getMessage());
            return null; // Return a default value or null
        }
    }
}
