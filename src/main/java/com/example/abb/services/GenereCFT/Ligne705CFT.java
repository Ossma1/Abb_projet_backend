package com.example.abb.services.GenereCFT;

import com.example.abb.models.BkamEntity;
import com.example.abb.models.LigneFolioCB1;
import com.example.abb.models.LigneFolioCB2;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class Ligne705CFT {
    public void writeLignesToFile(BkamEntity bkam, BufferedWriter writer) throws IOException {

        List<LigneFolioCB2> cb2Lignes = new ArrayList<>(bkam.getLigneFolioCB2s());
        List<LigneFolioCB1> cb1Lignes = new ArrayList<>(bkam.getLigneFolioCB1s());

        for (LigneFolioCB1 ligne : cb1Lignes) {
            writer.write(formatLigneFolioCB1(ligne));
            writer.newLine();
        }
        System.out.println("Ligne 1 finiche !!!!!!!!");
        int count =0;
        for (LigneFolioCB2 ligne : cb2Lignes) {
            if(count>0) writer.newLine();
            writer.write(formatLigneFolioCB2(ligne));
            count++;
        }
        System.out.println("Ligne 2 finiche !!!!!!!!");
    }

    private String formatLigneFolioCB1(LigneFolioCB1 ligne) {
        return String.format("%-4s%-3s%1c%010d%013d",
                padRight(ligne.getCodeLigne(), 4),
                padRight(ligne.getCodePays(), 3),
                ligne.getStatut(),
                ligne.getNombre(),
                ligne.getVolume());
    }

    private String formatLigneFolioCB2(LigneFolioCB2 ligne) {
        return String.format("%-4s%-100s%-20s%-8s%1c%1c%-3s%-3s%1c%1c%1c%1c%013d%013d%013d%013d%013d%013d%013d%013d",
                padRight(ligne.getCodeLigne(), 4),
                padRight(ligne.getNomCorrespondantBancaire(), 100),
                padRight(ligne.getCodeBicCorrespondantBancaire(), 20),
                padRight(ligne.getDateEntreeRelation(), 8),
                ligne.getMaisonMereOuFiliale(),
                ligne.getCompteNostroVostro(),
                padRight(ligne.getDevise(), 3),
                padRight(ligne.getCodePaysAgrement(), 3),
                ligne.getNiveauRisqueBCFT(),
                ligne.getProfilRisqueBeneficiaires(),
                ligne.getUtilisationCompteCorrespondance(),
                ligne.getStatutCompte(),
                ligne.getFluxEmisNombre(),
                ligne.getFluxEmisPaysRisque(),
                ligne.getFluxEmisVolume(),
                ligne.getFluxEmisPaysRisqueVolume(),
                ligne.getFluxRecusNombre(),
                ligne.getFluxRecusPaysRisque(),
                ligne.getFluxRecusVolume(),
                ligne.getFluxRecusPaysRisqueVolume());
    }

    private String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

}