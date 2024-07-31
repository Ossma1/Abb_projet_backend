package com.example.abb.mappers;

import com.example.abb.dto.Tables705.CB1;
import com.example.abb.dto.Tables705.CB2;
import com.example.abb.dto.Tables705.Pays;
import com.example.abb.dto.axesEntities.Axes;
import com.example.abb.models.Anomalies.TypeAnomalie;
import com.example.abb.models.LigneFolioCB1;
import com.example.abb.models.LigneFolioCB2;
import com.example.abb.repositories.repoAnomalies.AnomalieRepository;
import com.example.abb.services.Controle.XControle;
import com.example.abb.utility.CheckUtility;
import com.example.abb.utility.ErrorMessage;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Mapper(componentModel = "spring", uses = {AnomalieRepository.class})
public interface Ligne705Mapper {
    Ligne705Mapper INSTANCE = Mappers.getMapper(Ligne705Mapper.class);

    @Mapping(target = "codeLigne", constant = "CB01")
    @Mapping(source = ".", target = "statut", qualifiedByName = "convertToStatut")
    @Mapping(source = ".", target = "codePays", qualifiedByName = "convertPaysToCode")
    @Mapping(source = "nombreValue", target = "nombre")
    @Mapping(source = "volume", target = "volume")
    LigneFolioCB1 toCB1Entity(CB1 cb1, @Context List<Pays> paysList, @Context XControle xControle);

    @Named("convertPaysToCode")
    default String convertPaysToCode(Axes cbEntity, @Context List<Pays> paysList, @Context XControle xControle) {
        String normalizedPays;
        if (cbEntity instanceof CB1) {
            normalizedPays = ((CB1) cbEntity).getPays().toLowerCase(Locale.ROOT);
        } else if (cbEntity instanceof CB2) {
            normalizedPays = ((CB2) cbEntity).getPaysAgrement().toLowerCase(Locale.ROOT);
        } else {
            normalizedPays = null;
            xControle.createAnomalie(cbEntity, ErrorMessage.Country_NOT_Fond.getMessage() +" "+ normalizedPays, TypeAnomalie.VIOLATION_DE_REGLE);
        }

        if (normalizedPays != null) {
            String code = paysList.stream()
                    .filter(p -> p.getPays().toLowerCase(Locale.ROOT).trim().equals(normalizedPays))
                    .findFirst()
                    .map(Pays::getCode)
                    .orElse(null);

            if (code == null) {
                xControle.createAnomalie(cbEntity, ErrorMessage.Code_Country_NOT_Fond.getMessage() +" "  + normalizedPays, TypeAnomalie.VIOLATION_DE_REGLE);
                code="";
            }

            return code;
        } else {
            return "";
        }
    }

    @Named("convertToStatut")
    default char convertToStatut(CB1 cb1, @Context XControle xControle) {
        String totalFluxType=cb1.getTotalFluxType();
        if (totalFluxType.contains("Emis")) {
            return 'E';
        } else if (totalFluxType.contains("Reçu")) {
            return 'R';
        }
        xControle.createAnomalie(cb1, ErrorMessage.Statut_OUT_OF_Range.getMessage() +" " + totalFluxType, TypeAnomalie.VIOLATION_DE_REGLE);
        return ' ';
    }

    @Mapping(target = "codeLigne", constant = "CB02")
    @Mapping(source = "nom", target = "nomCorrespondantBancaire")
    @Mapping(source = "codeBIC", target = "codeBicCorrespondantBancaire")
    @Mapping(source = ".", target = "dateEntreeRelation", qualifiedByName = "toAAAAMMJJ")
    @Mapping(source = ".", target = "compteNostroVostro", qualifiedByName = "convertToVOuN")
    @Mapping(source = "devise", target = "devise")
    @Mapping(source = ".", target = "codePaysAgrement", qualifiedByName = "convertPaysToCode")
    @Mapping(source = ".", target = "maisonMereOuFiliale", qualifiedByName = "convertToMF")
    @Mapping(source = ".", target = "niveauRisqueBCFT", qualifiedByName = "convertProfilRisqueBCFT")
    @Mapping(source = ".", target = "profilRisqueBeneficiaires", qualifiedByName = "convertProfilRisqueBeneficiaires")
    @Mapping(source = ".", target = "utilisationCompteCorrespondance", qualifiedByName = "convertUtilisationCompte")
    @Mapping(source = ".", target = "statutCompte", qualifiedByName = "convertStatutCompte")
    @Mapping(source = "nombreFluxEmis", target = "fluxEmisNombre")
    @Mapping(source = "nombreFluxEmisPaysRisque", target = "fluxEmisPaysRisque")
    @Mapping(source = "volumeFluxEmis", target = "fluxEmisVolume", qualifiedByName = "arrondirDouble")
    @Mapping(source = "volumeFluxEmisPaysRisque", target = "fluxEmisPaysRisqueVolume", qualifiedByName = "arrondirDouble")
    @Mapping(source = "nombreFluxRecus", target = "fluxRecusNombre")
    @Mapping(source = "nombreFluxRecusPaysRisque", target = "fluxRecusPaysRisque")
    @Mapping(source = "volumeFluxRecus", target = "fluxRecusVolume", qualifiedByName = "arrondirDouble")
    @Mapping(source = "volumeFluxRecusPaysRisque", target = "fluxRecusPaysRisqueVolume", qualifiedByName = "arrondirDouble")
    LigneFolioCB2 toCB2Entity(CB2 cb2,@Context List<Pays> paysList, @Context XControle xControle);

    @Named("convertToMF")
    default char convertToMF(CB2 cb2, @Context XControle xControle) {
        try {
            String maisonMereOuFiliale = cb2.getMaisonMereOuFiliale();
            if (maisonMereOuFiliale.contains("Mère")|| maisonMereOuFiliale.equals("M")) {
                return 'M';
            } else if (maisonMereOuFiliale.contains("Filiale")|| maisonMereOuFiliale.equals("F")) {
                return 'F';
            } else {
                xControle.createAnomalie(cb2, ErrorMessage.MaisonMereFiliale_OUT_OF_Range.getMessage() +" "+  maisonMereOuFiliale, TypeAnomalie.VIOLATION_DE_REGLE);
                return '0';
            }
        } catch (Exception e) {
            xControle.createAnomalie(cb2, ErrorMessage.MaisonMereFiliale_OUT_OF_Range.getMessage()+" " + cb2.getMaisonMereOuFiliale(), TypeAnomalie.VIOLATION_DE_REGLE);
            return '0'; // Return a default value
        }
    }

    @Named("toAAAAMMJJ")
    default String toAAAAMMJJ(CB2 cb2 , @Context XControle xControle) {
        Date dateEntreeEnRelation = cb2.getDateEntreeEnRelation();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
            return dateFormat.format(dateEntreeEnRelation);
        } catch (Exception e) {
            xControle.createAnomalie(cb2, ErrorMessage.DATE_FORMAT_INVALIDE.getMessage()+" " + dateEntreeEnRelation, TypeAnomalie.VIOLATION_DE_REGLE );
            return null; // Return a default value or null
        }
    }

    @Named("convertToVOuN")
    default char convertToVOuN(CB2 cb2 , @Context XControle xControle) {
        String  compteNostroOuVostro = cb2.getCompteNostroOuVostro();
        try {
            if (compteNostroOuVostro.contains("Nostro")) {
                return 'N';
            } else if (compteNostroOuVostro.contains("Vostro")) {
                return 'V';
            } else {
                xControle.createAnomalie(cb2, ErrorMessage.CompteNostroVostro_OUT_OF_Range.getMessage() +" "+  compteNostroOuVostro, TypeAnomalie.VIOLATION_DE_REGLE);
                return '0';
            }
        } catch (Exception e) {
            xControle.createAnomalie(cb2, ErrorMessage.CompteNostroVostro_OUT_OF_Range.getMessage()+" " + compteNostroOuVostro, TypeAnomalie.VIOLATION_DE_REGLE);
            return '0';
        }
    }
    @Named("arrondirDouble")
    default long arrondirDouble(Double value , @Context XControle xControle) {
        return CheckUtility.roundToNearestlong(value);
    }

    @Named("convertStatutCompte")
    default String convertStatutCompte(CB2 cb2, @Context XControle xControle) {
        String statutCompte = cb2.getStatutDuCompte();
        try {
            switch (statutCompte) {
                case "Actif":
                case "A":
                    return "A";
                case "Inactif":
                case "I":
                    return "I";
                default:
                    xControle.createAnomalie(cb2, ErrorMessage.AccountStatus_OUT_OF_Range.getMessage() +" "+ statutCompte, TypeAnomalie.VIOLATION_DE_REGLE);
                    return "0"; // Gérer le cas par défaut selon votre logique
            }
        } catch (Exception e) {
            xControle.createAnomalie(cb2, ErrorMessage.AccountStatus_OUT_OF_Range.getMessage() +" "+ statutCompte , TypeAnomalie.VIOLATION_DE_REGLE);
            return " ";
        }
    }

    @Named("convertUtilisationCompte")
    default String convertUtilisationCompte(CB2 cb2, @Context XControle xControle) {
        String utilisationCompte = cb2.getUtilisationCompteCorrespondanceParTiers();

        try {
            switch (utilisationCompte.trim()) {
                case "Non":
                case "N":
                    return "N";
                case "Oui":
                case "O":
                    return "O";
                default:
                    xControle.createAnomalie(cb2, ErrorMessage.CorrespondenceAccountUsage_OUT_OF_Range.getMessage() +" "+  utilisationCompte, TypeAnomalie.VIOLATION_DE_REGLE);
                    return "0";
            }
        } catch (Exception e) {
            xControle.createAnomalie(cb2, ErrorMessage.CorrespondenceAccountUsage_OUT_OF_Range.getMessage()+" " + utilisationCompte, TypeAnomalie.VIOLATION_DE_REGLE );
            return " ";
        }
    }

    @Named("convertProfilRisqueBCFT")
    default String convertProfilRisqueBCFT(CB2 cb2, @Context XControle xControle) {
        return convertProfilRisqueGeneric(cb2.getNiveauRisqueBCFTDuPays(), ErrorMessage.NiveauRisqueBCFT_OUT_OF_Range, cb2, xControle);
    }

    @Named("convertProfilRisqueBeneficiaires")
    default String convertProfilRisqueBeneficiaires(CB2 cb2, @Context XControle xControle) {
        return convertProfilRisqueGeneric(cb2.getProfilRisqueBeneficiairesEffectifs(), ErrorMessage.BeneficiaryRiskProfile_OUT_OF_Range, cb2, xControle);
    }

    default String convertProfilRisqueGeneric(String riskProfile, ErrorMessage errorMessage, CB2 cb2, @Context XControle xControle) {
        try {
            switch (riskProfile) {
                case "Faible":
                case "F":
                    return "F";
                case "Modéré":
                case "M":
                    return "M";
                case "Elevé":
                case "E":
                    return "E";
                default:
                    xControle.createAnomalie(cb2, errorMessage.getMessage() +" "+  riskProfile, TypeAnomalie.VIOLATION_DE_REGLE);
                    return "0";
            }
        } catch (Exception e) {
            xControle.createAnomalie(cb2, errorMessage.getMessage()+" " + riskProfile , TypeAnomalie.VIOLATION_DE_REGLE);
            return "0";
        }
    }


}
