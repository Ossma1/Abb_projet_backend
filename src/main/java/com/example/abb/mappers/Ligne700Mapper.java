package com.example.abb.mappers;

import com.example.abb.dto.AxesE700.AxeE700;
import com.example.abb.dto.AxesE700.ColumnValue;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.models.Anomalies.TypeAnomalie;
import com.example.abb.models.Ligne700;
import com.example.abb.services.Controle.XControle;
import com.example.abb.utility.CodeLigne700;
import com.example.abb.utility.CodeLigne701;
import com.example.abb.utility.ErrorMessage;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface Ligne700Mapper {
    Ligne700Mapper INSTANCE = Mappers.getMapper(Ligne700Mapper.class);

default List<Ligne700> toEntity(AxeE700 axeE700, @Context XControle xControle) {
    List<Ligne700> lignes = new ArrayList<>();
    String[] columnNames = {
            "traficIlliciteStupefiants",
            "traficEtresHumains",
            "traficImmigrants",
            "traficIlliciteArmes",
            "corruptionConcussion",
            "contrefaconMonnaies",
            "exploitationSexuelle",
            "abusConfiance",
            "escroquerie",
            "volExtorsion",
            "contrebande",
            "fraudeMarchandises",
            "fauxUsageFaux",
            "attelongeSystemesTraitement",
            "financementTerrorisme",
            "total"
    };

    for (String columnName : columnNames) {
        Ligne700 ligne = mapGenericColumn(axeE700, xControle, columnName);
        if (ligne != null) {
            lignes.add(ligne);
        }
    }

    return lignes;
}

    default Ligne700 mapGenericColumn(AxeE700 axeE700, @Context XControle xControle, String columnName) {
        Ligne700 ligne700 = new Ligne700();
        ligne700.setLigneDocument(convertIdentifiantLigne(axeE700, xControle));

        try {
            Field columnField = AxeE700.class.getDeclaredField(columnName);
            columnField.setAccessible(true);
            ColumnValue columnValue = (ColumnValue) columnField.get(axeE700);
            ligne700.setRangColonne(columnValue.getColumnNumber());
            ligne700.setMontant(columnValue.getValue());
            ligne700.setSens(columnValue.getValue() >= 0 ? 'C' : 'D');
        } catch (NoSuchFieldException e) {
            xControle.createAnomalie(axeE700, ErrorMessage.COLONNE_NON_TROUVEE.getMessage() + " pour " + columnName, TypeAnomalie.VIOLATION_DE_REGLE);
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }

        return ligne700;
    }

    @Named("convertIdentifiantLigne")
    static String convertIdentifiantLigne(AxeE700 axe, @Context XControle xControle) {
        String codeAxes = axe.getLigneDocument();
        String identifiantLigne=null;
        for (Map.Entry<String, String> entry : CodeLigne700.codeMapping.entrySet()) {
            if (entry.getValue().equals(codeAxes)) {
                identifiantLigne = entry.getKey();
                break;
            }
        }
        if (identifiantLigne == null) {
            xControle.createAnomalie(axe, ErrorMessage.lIGNE_DOCUMENT_INTROUVABLE.getMessage() + " pour " + codeAxes, TypeAnomalie.VIOLATION_DE_REGLE);
        }
        return identifiantLigne;
    }
}
