package com.example.abb.utility;

public enum ErrorMessage {
    CODE_MONNAIE_INVALIDE("Valeur non valide pour le Code Monnaie (doit être 1, 2 ou 3)"),
    C2_C3_DOUBLONT_DETECTE("Doublon détecté pour la combinaison de 'Code de ligne du document' et 'Rang colonne'"),
    SENS_MONTANT_INVALIDE("Le code Sens doit être 'D' (Débit) ou 'C' (Crédit)"),
    MONTANT_INVALIDE("Le montant doit être Valeur numérique"),
    IDENTIFIANT_lIGNE_INTROUVABLE("Intitulé de ligne n'appartient pas à la table des Codes et intitulés"),
    lIGNE_DOCUMENT_INTROUVABLE("Ligne Document de ligne n'appartient pas à la table des Lignes Document"),
    RISQUE_INHERENT_ABSENT("Le champ RISQUE_INHERENT doit appartenir à la liste de valeur ('0','1','2','3', '4')"),
    Statut_OUT_OF_Range("Le champ STATUS doit être inclus dans la liste de valeurs ('E', 'R')"),
    DATE_FORMAT_INVALIDE("La date doit être au format JJMMAAAA"),
    MaisonMereFiliale_OUT_OF_Range("Le champ Maison Mère / Filiale doit être 'M' ou 'F'"),
    CompteNostroVostro_OUT_OF_Range("Le champ Compte Nostro / Vostro doit être 'N' ou 'V'"),
    NiveauRisqueBCFT_OUT_OF_Range("Le champ Niveau de Risque BCFT doit être 'F', 'M' ou 'E'"),
    BeneficiaryRiskProfile_OUT_OF_Range("Le champ Profil de Risque des Bénéficiaires doit être 'F', 'M' ou 'E'"),
    CorrespondenceAccountUsage_OUT_OF_Range("Le champ Utilisation du Compte de Correspondance doit être 'O' ou 'N'"),
    AccountStatus_OUT_OF_Range("Le champ Statut du Compte doit être 'A' ou 'I'"),
    Code_Country_NOT_Fond("Country code not found for country"),
    Commentent_Too_Long("Le champs Commentaires de l'établissement est trop long"),
    Country_NOT_Fond("Le champs Code pays d’agrément est vide"),
    //structure
    COLONNE_NON_TROUVEE("La colonne spécifiée n'a pas été trouvée"),
    CELLULE_NON_NUMERIQUE("Cellule attendue comme numérique mais trouvée avec un type différent"),
    CELLULE_NON_STRING("Cellule attendue comme chaîne mais trouvée avec un type différent");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
