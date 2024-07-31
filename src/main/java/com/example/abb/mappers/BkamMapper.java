package com.example.abb.mappers;

import com.example.abb.dto.Banquefinancement703;
import com.example.abb.models.BkamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(componentModel = "spring")  // Utilisez Spring pour l'injection de dépendances

public interface BkamMapper {
    BkamMapper INSTANCE = Mappers.getMapper(BkamMapper.class);
    @Mapping(target = "id", ignore = true) // Ignore mapping ID from source to target
    @Mapping(target = "codeEtablissement", source = "codeEtablissement")
    @Mapping(target = "dateExercixe", source = "annee")
    @Mapping(source = "dateChargement", target = "dateChargement", qualifiedByName = "convertFormeDate")
    @Mapping(target = "nombreDocumentsRemis", constant = "001")//for now specifie 1 seul doc
    @Mapping(target = "codeDocument", source = "etat")
    @Mapping(source=".",target = "nombreChampsRenseidnes",qualifiedByName ="calculerNombreChampsRenseidnes")
    BkamEntity toBkamEntity(Banquefinancement703 source);
    @Named("calculerNombreChampsRenseidnes")
    default int calculerNombreChampsRenseidnes(Banquefinancement703 source) {
        if (source.getProduits() == null) {
            return 0;
        }
        int a =source.getProduits().size()+source.getProduits().size() * 4;
        System.out.println(a);
        return a;
    }

    @Named("convertFormeDate")
    default String convertFormeDate(Date dateEntreeEnRelation) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("mmssHHddMMyyyy");
            return dateFormat.format(dateEntreeEnRelation);
        } catch (Exception e) {
            System.err.println("Error formatting date: " + dateEntreeEnRelation + ". Error: " + e.getMessage());
            return null;
        }
    }
}
