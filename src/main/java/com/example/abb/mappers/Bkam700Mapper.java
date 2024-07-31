package com.example.abb.mappers;

import com.example.abb.dto.CorrespondanceBancaire705;
import com.example.abb.dto.E700DTO;
import com.example.abb.models.BkamEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(componentModel = "spring")
public interface Bkam700Mapper {
    Bkam700Mapper INSTANCE = Mappers.getMapper(Bkam700Mapper.class);
    @Mapping(target = "id", ignore = true) // Ignore mapping ID from source to target
    @Mapping(target = "codeEtablissement", source = "codeEtablissement")
    @Mapping(target = "dateExercixe", source = "annee")
    @Mapping(source = "dateChargement", target = "dateChargement", qualifiedByName = "convertFormeDate")
    @Mapping(target = "nombreDocumentsRemis", constant = "001")//for now specifie 1 seul doc
    @Mapping(target = "codeDocument", source = "etat")
//    @Mapping(target = "nombreChampsRenseidnes", expression = "java(calculerNombreChampsRenseidnes(source))")
    BkamEntity toBkam705Entity(E700DTO source);

//    default int calculerNombreChampsRenseidnes(E700DTO source) {
//        int count = 0;
//        if (source.getListCB1() != null) {
//            count += source.getListCB1().size();
//        }
//        if (source.getListCB2() != null) {
//            count += source.getListCB2().size();
//        }
//        return count;
//    }

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
