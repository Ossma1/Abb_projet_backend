package com.example.abb.utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodeLigne703 {
    public static final Map<String, String> codeMapping = new LinkedHashMap<>();

    static {
        codeMapping.put("CL-01", "CL01"); // PETITES ET MOYENNES ENTREPRISES
        codeMapping.put("CL-01-1", "CL02"); // Risque faible
        codeMapping.put("CL-01-2", "CL03"); // Risque moyen
        codeMapping.put("CL-01-3", "CL04"); // Risque moyen-élevé
        codeMapping.put("CL-01-4", "CL05"); // Risque élevé
        codeMapping.put("CL-01-4-1", "CL06"); // Dont PPE
        codeMapping.put("CL-01-4-2", "CL07"); // Dont organismes à but non lucratifs
        codeMapping.put("CL-02", "CL08"); // GRANDES ENTREPRISES
        codeMapping.put("CL-02-1", "CL09"); // Risque faible
        codeMapping.put("CL-02-2", "CL10"); // Risque moyen
        codeMapping.put("CL-02-3", "CL11"); // Risque moyen-élevé
        codeMapping.put("CL-02-4", "CL12"); // Risque élevé
        codeMapping.put("CL-02-4-1", "CL13"); // Dont PPE
        codeMapping.put("CL-02-4-2", "CL14"); // Dont constructions juridiques y compris les trusts ou toutes structures juridiques équivalentes
        codeMapping.put("PDT-01", "PP01"); // Compte courant ordinaire en dirhams
        codeMapping.put("PDT-02", "PP02"); // Compte en Dirham convertible
        codeMapping.put("PDT-03", "PP03"); // Compte en devises
        codeMapping.put("PDT-04", "PP04"); // DAT
        codeMapping.put("PDT-05", "PP05"); // Autres produits de placement
        codeMapping.put("PDT-06", "PP06"); // Crédit de trésorerie
        codeMapping.put("PDT-07", "PP07"); // Avance sur marchés publics
        codeMapping.put("PDT-08", "PP08"); // Crédit d'enlèvement
        codeMapping.put("PDT-09", "PP09"); // Escompte de papier commercial
        codeMapping.put("PDT-10", "PP10"); // Cautions et garanties administratives
        codeMapping.put("PDT-11", "PP11"); // crédits d'investissement
        codeMapping.put("PDT-12", "PP12"); // Financements structurés
        codeMapping.put("TR-01", "TR01"); // Versements d'espèces
        codeMapping.put("TR-02", "TR02"); // Retraits d'espèces
        codeMapping.put("TR-03", "TR03"); // Virement nationnaux émis
        codeMapping.put("TR-04", "TR04"); // Virement nationnaux reçus
        codeMapping.put("TR-05", "TR05"); // Remise LCN-chèques
        codeMapping.put("TR-06", "TR06"); // Paiement LCN-chèques
        codeMapping.put("TR-07", "TR07"); // Virmement internationnaux émis
        codeMapping.put("TR-08", "TR08"); // Virmement internationnaux reçus
        codeMapping.put("CD-01", "CD01"); // Digital : Web, Application Mobile
        codeMapping.put("CD-02", "CD02"); // Digital : Web, Application Mobile
        codeMapping.put("GEO-01", "GE01"); // Clients ou Bénéficiaire effectif résidants dans des juridictions à risque élevé
        codeMapping.put("GEO-02", "GE02"); // Clients pu bénéficaire effectif de nationalité considérés comme juridictions à risques élevés
        codeMapping.put("GEO-03", "GE03"); // Virements transfrontalièrs en provenance de juridictions à risque élevé
        codeMapping.put("GEO-04", "GE04"); // Virements transfrontalièrs à distinations de juridictions à risque élevé
        codeMapping.put("Autres-01", "AT01"); // Affaires de fraudes et détournements sur les 3 dernières années concernant la ligne de métiers
    }
}
