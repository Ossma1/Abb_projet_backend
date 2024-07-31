package com.example.abb.services.GenereCFT;

import com.example.abb.models.BkamEntity;
import com.example.abb.models.LigneX;
import com.example.abb.models.LigneX0102;
import com.example.abb.services.helper.SortingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LigneXCFT {
    @Autowired
    LigneX0102CFT ligneX0102cft;
    @Autowired
    private SortingService sortingService;

    public void writeLignesToFile(BkamEntity bkam, BufferedWriter writer) throws Exception {
        List<LigneX> lignesNotSortef = new ArrayList<>(bkam.getLigneXs());
        List<LigneX> lignes = sortingService.sortByIdentifiableLigne(lignesNotSortef, bkam.getCodeDocument());
            for (LigneX ligne : lignes) {
                StringBuilder sb = new StringBuilder();
                if (!ligne.equals(lignes.get(0))) writer.newLine();
                sb.append(ligneX0102cft.padRight(ligne.getCodeLigne(), 4));
                sb.append(ligneX0102cft.padRight(ligne.getIdentifiantLigne(), 4));
                sb.append(ligneX0102cft.padLeft(String.valueOf(ligne.getNombreClientsOperations()), 13));
                sb.append(ligneX0102cft.padLeft(String.valueOf(ligne.getEncoursDepotsPlacements()), 13));
                sb.append(ligneX0102cft.padLeft(String.valueOf(ligne.getFluxDebiteurs2019()), 13));
                sb.append(ligneX0102cft.padLeft(String.valueOf(ligne.getFluxCrediteurs2019()), 13));
                sb.append(ligneX0102cft.padRight(String.valueOf(ligne.getRisqueInherent()), 1));
                writer.write(sb.toString());
                writer.newLine();

                ligneX0102cft.writeComment(writer,ligne.getB001());
                ligneX0102cft.writeComment(writer,ligne.getB002());
                ligneX0102cft.writeComment(writer,ligne.getB003());
                ligneX0102cft.writeComment(writer,ligne.getB004());
            }

    }
}