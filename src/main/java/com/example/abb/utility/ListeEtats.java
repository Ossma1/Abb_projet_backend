package com.example.abb.utility;

import java.util.HashMap;
import java.util.Map;

public class ListeEtats {
    public static final Map<String, String> listeEtats = new HashMap<>();

    static {
        listeEtats.put("700", "Statistiques sur le nombre des DS transmises à l'UTRF par ligne de métiers et typologie d'infraction sous-jacente sur la période 2018-2019");
        listeEtats.put("701", "Risque inhérent \" Banque de détail : Banque des particuliers et des professionnels (PP)\"");
        listeEtats.put("702", "Risque inhérent \" Banque de détail : Banque Privée\"");
        listeEtats.put("703", "Risque inhérent \" Banque de l'entreprise et de financement\"");
        listeEtats.put("704", "Risque inhérent \" Commerce international\"");
        listeEtats.put("705", "Risque inhérent \" Correspondance bancaire \"");
        listeEtats.put("707", "Risque inhérent \" Financement participatif \"  ");
        listeEtats.put("708", "Risque inhérent \" Crédit à la consommation \"");
        listeEtats.put("709", "Risque inhérent \"Crédit Immobilier \"");
        listeEtats.put("710", "Risque inhérent \" Leasing  \" ");
        listeEtats.put("711", "Risque inhérent \" Affacturage \"");
        listeEtats.put("712", "Risque inhérent \"Transfert de fonds \"");
        listeEtats.put("713", "Risque inhérent \"comptes de paiement\"");


    }
}
