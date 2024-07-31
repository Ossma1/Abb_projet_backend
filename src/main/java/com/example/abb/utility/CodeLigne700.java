package com.example.abb.utility;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
@Service
public class CodeLigne700 {
    public static final Map<String, String> codeMapping = new LinkedHashMap<>();

    static {
        codeMapping.put("A001", "Banque de détail");
        codeMapping.put("A002", "Banque privée");
        codeMapping.put("A003", "Banque de l'entreprise et du financement");
        codeMapping.put("A004", "Financement du commerce international");
        codeMapping.put("A005", "Correspondance bancaire");
        codeMapping.put("A006", "Banque offshore");
        codeMapping.put("A007", "Crédits à la consommation");
        codeMapping.put("A008", "Crédit immobilier");
        codeMapping.put("A009", "Leasing");
        codeMapping.put("A010", "Factoring");
        codeMapping.put("A011", "Financement participatif");
        codeMapping.put("A012", "Comptes de paiement");
        codeMapping.put("A013", "Transfert de fonds");
        codeMapping.put("A014", "Micro-crédit");
        codeMapping.put("A015", "Total");
        }
}
