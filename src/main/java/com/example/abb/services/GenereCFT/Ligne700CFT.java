package com.example.abb.services.GenereCFT;

import com.example.abb.models.BkamEntity;
import com.example.abb.models.Ligne700;
import com.example.abb.services.helper.SortingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;

@Service
public class Ligne700CFT {
    @Autowired
    private SortingService sortingService;
    public void writeLignesToFile(BkamEntity bkam, BufferedWriter writer) throws Exception {
        List<Ligne700> lignesNotSorted = new ArrayList<>(bkam.getLigne700s());
        List<Ligne700> lignes = sortingService.sortByIdentifiableLigne(lignesNotSorted, bkam.getCodeDocument());

        for (Ligne700 ligne : lignes) {
            StringBuilder sb = new StringBuilder();
            if (!ligne.equals(lignes.get(0))) writer.newLine();
            sb.append(padLeft(String.valueOf(ligne.getCodeMonnaie()), 1));
            sb.append(padRight(ligne.getLigneDocument(), 4));
            sb.append(padLeft(String.valueOf(ligne.getRangColonne()), 2));
            sb.append(padRight(String.valueOf(ligne.getSens()), 1));
            sb.append(padLeft(String.valueOf(ligne.getMontant()), 13));
            writer.write(sb.toString());
        }
    }

    public String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s).replace(' ', '0');
    }
}
