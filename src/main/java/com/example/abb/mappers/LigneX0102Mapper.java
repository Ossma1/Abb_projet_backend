package com.example.abb.mappers;

import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.models.Anomalies.TypeAnomalie;
import com.example.abb.models.LigneX0102;
import com.example.abb.services.Controle.XControle;
import com.example.abb.utility.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")  // Utilisez Spring pour l'injection de dépendances
public interface LigneX0102Mapper {
    LigneX0102Mapper INSTANCE = Mappers.getMapper(LigneX0102Mapper.class);


    @Mapping(target = "codeLigne", constant = "A001")
    @Mapping(source = ".", target = "identifiantLigne", qualifiedByName = "convertIdentifiantLigne")
    @Mapping(source = "nombreClients", target = "nombreClientsOperations")
    @Mapping(source = "encoursDepots", target = "encoursDepotsPlacements", qualifiedByName = "arrondirDouble")
    @Mapping(source = "fluxDebiteurs2020", target = "fluxDebiteurs2019", qualifiedByName = "arrondirDouble")
    @Mapping(source = "fluxCrediteurs2020", target = "fluxCrediteurs2019", qualifiedByName = "arrondirDouble")
    @Mapping(source = "nombreClientsCartes", target = "nombreClientsCartesPrepayes")
    @Mapping(source = ".", target = "risqueInherent", qualifiedByName = "convertRisqueInherent")
    @Mapping(source = "commentaires", target = "b001", qualifiedByName = "splitCommentPart1")
    @Mapping(source = "commentaires", target = "b002", qualifiedByName = "splitCommentPart2")
    @Mapping(source = "commentaires", target = "b003", qualifiedByName = "splitCommentPart3")
    @Mapping(source = "commentaires", target = "b004", qualifiedByName = "splitCommentPart4")
    LigneX0102 toEntity(AxeWithNombreClientsCartes axe,@Context  String Etat, @Context XControle xControle);
    @Named("arrondirDouble")
    default long arrondirDouble(Double value , @Context XControle xControle) {
        return CheckUtility.roundToNearestlong(value);
    }
    @Named("convertIdentifiantLigne")
    static String convertIdentifiantLigne(AxeWithNombreClientsCartes axe,@Context  String Etat, @Context XControle xControle) {
        String codeAxes = axe.getCodeAxes();

        String identifiantLigne;
        switch (Etat) {
            case "701":
                 identifiantLigne = CodeLigne701.codeMapping.getOrDefault(codeAxes, "0");
                if (identifiantLigne.equals("0")) {
                    xControle.createAnomalie(axe, ErrorMessage.IDENTIFIANT_lIGNE_INTROUVABLE.getMessage() + " pour " + codeAxes, TypeAnomalie.VIOLATION_DE_REGLE);
                }
                break;
            case "702":
                identifiantLigne = CodeLigne702.codeMapping.getOrDefault(codeAxes, "0");
                if (identifiantLigne.equals("0")) {
                    xControle.createAnomalie(axe, ErrorMessage.IDENTIFIANT_lIGNE_INTROUVABLE.getMessage() + " pour " + codeAxes, TypeAnomalie.VIOLATION_DE_REGLE);
                }
                break;
            default:
                xControle.createAnomalie(axe, "Unknown Etat", TypeAnomalie.VIOLATION_DE_REGLE);
                identifiantLigne = "0";
                break;
        }
        return identifiantLigne;
    }


    @Named("convertRisqueInherent")
    static char convertRisqueInherent(AxeWithNombreClientsCartes axe, @Context XControle xControle) {
        String risque = axe.getRisqueInherent();
        char result;
        switch (risque) {
            case "Aucun":
            case "0":
            case "":
                result = '0';
                break;
            case "Faible":
            case "1":
                result = '1';
                break;
            case "Moyen":
            case "2":
                result = '2';
                break;
            case "Moyen-Elevé":
            case "3":
                result = '3';
                break;
            case "Elevé":
            case "4":
                result = '4';
                break;
            default:
                xControle.createAnomalie(axe, ErrorMessage.RISQUE_INHERENT_ABSENT.getMessage() + " pour " + risque, TypeAnomalie.VIOLATION_DE_REGLE);
                result = '0';
                break;
        }
        System.out.println("Returning risqueInherent: " + result);
        return result;    }

    @Named("splitCommentPart1")
    static String splitCommentPart1(String commentaires, @Context XControle xControle) {
        String part = splitComment(commentaires, 0, xControle);
         return part != null && !part.isEmpty() ? "B001" + part : "B001";
    }

    @Named("splitCommentPart2")
    static String splitCommentPart2(String commentaires, @Context XControle xControle) {
        String part = splitComment(commentaires, 1, xControle);
        return part != null && !part.isEmpty() ? "B002" + part : "B002";
    }

    @Named("splitCommentPart3")
    static String splitCommentPart3(String commentaires, @Context XControle xControle) {
        String part = splitComment(commentaires, 2, xControle);
        return part != null && !part.isEmpty() ? "B003" + part : "B003";
    }

    @Named("splitCommentPart4")
    static String splitCommentPart4(String commentaires, @Context XControle xControle) {
        String part = splitComment(commentaires, 3, xControle);
        return part != null && !part.isEmpty() ? "B004" + part : "B004";
    }

    static String splitComment(String commentaires, int part, XControle xControle) {
        int partSize = 500;
        if (commentaires != null) {
//            if (commentaires.length() > partSize * 4) {
//                xControle.createAnomalie(null, ErrorMessage.Commentent_Too_Long.getMessage());
//            }
            int start = part * partSize;
            if (start >= commentaires.length()) {
                return null;
            }
            int end = Math.min(start + partSize, commentaires.length());
            return commentaires.substring(start, end);
        }
        return null;
    }
}