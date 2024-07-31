package com.example.abb.utility;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CodeLigne711 {
    public static final Map<String, String> codeMapping = new LinkedHashMap<>();

    static {
        codeMapping.put("CL-01", "CL01"); // PETITES ET MOYENNES ENTREPRISES
        codeMapping.put("CL-01-1", "CL02"); // Risque faible
        codeMapping.put("CL-01-2", "CL03"); // Risque moyen
        codeMapping.put("CL-01-3", "CL04"); // Risque moyen-élevé
        codeMapping.put("CL-01-4", "CL05"); // Risque élevé
        codeMapping.put("CL-01-4-1", "CL06"); // Dont PPE
        codeMapping.put("CL-01-4-3", "CL07"); // Dont organismes à but non lucratifs
        codeMapping.put("CL-02", "CL08"); // GRANDES ENTREPRISES
        codeMapping.put("CL-02-1", "CL09"); // Risque faible
        codeMapping.put("CL-02-2", "CL10"); // Risque moyen
        codeMapping.put("CL-02-3", "CL11"); // Risque moyen-élevé
        codeMapping.put("CL-02-4", "CL12"); // Risque élevé
        codeMapping.put("CL-02-4-1", "CL13"); // Dont PPE
         codeMapping.put("CL-02-4-3", "CL14"); // Dont constructions juridiques y compris les trusts ou toutes structures juridiques équivalentes

        codeMapping.put("PDT-01", "PP01"); // Compte courant ordinaire en dirhams
        codeMapping.put("PDT-02", "PP02"); // Compte en Dirham convertible
        codeMapping.put("PDT-03", "PP03"); // Compte en devises
        codeMapping.put("TR-01", "TR01"); // Versements d'espèces
        codeMapping.put("CD-01", "CD01"); // Digital : Web, Application Mobile

        codeMapping.put("CD-02", "CD02"); // Digital : Web, Application Mobile
        codeMapping.put("GEO-01", "GE01"); // Clients ou Bénéficiaire effectif résidants dans des juridictions à risque élevé
        codeMapping.put("GEO-02", "GE02"); // Clients pu bénéficaire effectif de nationalité considérés comme juridictions à risques élevés
        codeMapping.put("Autres-01", "AT01"); // Affaires de fraudes et détournements sur les 3 dernières années concernant la ligne de métiers
        codeMapping.put("Autres-01-01", "AT02"); // Affaires de fraudes et détournements sur les 3 dernières années concernant la ligne de métiers
        codeMapping.put("Autres-01-02", "AT03"); // Affaires de fraudes et détournements sur les 3 dernières années concernant la ligne de métiers
        codeMapping.put("Autres-01-03", "AT04"); // Affaires de fraudes et détournements sur les 3 dernières années concernant la ligne de métiers
        codeMapping.put("Autres-02", "AT05"); // Affaires de fraudes et détournements sur les 3 dernières années concernant la ligne de métiers
        codeMapping.put("Autres-02-01", "AT06"); // Affaires de fraudes et détournements sur les 3 dernières années concernant la ligne de métiers
        codeMapping.put("Autres-02-02", "AT07"); // Affaires de fraudes et détournements sur les 3 dernières années concernant la ligne de métiers
        codeMapping.put("Autres-02-03", "AT08"); // Affaires de fraudes et détournements sur les 3 dernières années concernant la ligne de métiers
    }
}
