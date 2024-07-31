package com.example.abb.services.GenereCFT;

import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.models.BkamEntity;
import com.example.abb.models.LigneX0102;
import com.example.abb.services.helper.SortingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LigneX0102CFT {
    @Autowired
    private SortingService sortingService;
    public void writeLignesToFile(BkamEntity bkam, BufferedWriter writer) throws Exception {
        List<LigneX0102> lignesNotSortef = new ArrayList<>(bkam.getLigneX0102s());
        List<LigneX0102> lignes = sortingService.sortByIdentifiableLigne(lignesNotSortef, bkam.getCodeDocument());

        for (LigneX0102 ligne : lignes) {
            StringBuilder sb = new StringBuilder();
            if (!ligne.equals(lignes.get(0))) writer.newLine();
            sb.append(padRight(ligne.getCodeLigne(), 4));
            sb.append(padRight(ligne.getIdentifiantLigne(), 4));
            sb.append(padLeft(String.valueOf(ligne.getNombreClientsOperations()), 13));
            sb.append(padLeft(String.valueOf(ligne.getEncoursDepotsPlacements()), 13));
            sb.append(padLeft(String.valueOf(ligne.getFluxDebiteurs2019()), 13));
            sb.append(padLeft(String.valueOf(ligne.getFluxCrediteurs2019()), 13));
            sb.append(padLeft(String.valueOf(ligne.getNombreClientsCartesPrepayes()), 13));
            sb.append(padRight(ligne.getRisqueInherent(), 1));
            writer.write(sb.toString());
            writer.newLine();

            writeComment(writer, ligne.getB001());
            writeComment(writer, ligne.getB002());
            writeComment(writer, ligne.getB003());
            writeComment(writer, ligne.getB004());
        }
    }

    public String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s).replace(' ', '0');
    }
    public void writeComment(BufferedWriter writer,   String comment) throws IOException {
        if (comment != null && !comment.isEmpty()) {
            if (comment.length() > 4) {
                writer.write(padRight(comment,500));
            } else {
                writer.write(padRight(comment,4));
            }
            if(!comment.startsWith("B004")){
                writer.newLine();
            }
        }
    }
}