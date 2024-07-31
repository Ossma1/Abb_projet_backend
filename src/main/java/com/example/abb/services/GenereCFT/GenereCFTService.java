package com.example.abb.services.GenereCFT;

import com.example.abb.dto.FileResponse;
import com.example.abb.exception.*;
import com.example.abb.models.BkamEntity;
import com.example.abb.repositories.ModelRepo.BkamEntityRepository;
import com.example.abb.repositories.ModelRepo.Ligne700Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GenereCFTService {
    @Autowired
    private BkamEntityRepository bkamEntityRepository;
    @Autowired
    private LigneXCFT ligneXCFT;
    @Autowired
    private LigneX0102CFT ligneX0102CFT;
    @Autowired
    private Ligne705CFT ligne705CFT;
    @Autowired
    private Ligne700CFT ligne700CFT;

    public boolean haveSameExerciceDateAndCodeDocument(List<String> ids) throws BkamNotFoundException, DifferentExerciceDateException, DifferentCodeDocumentException {
        Set<String> codeDocumentSet = new HashSet<>();
        String exerciceDate = null;
        for (String id : ids) {
            Optional<BkamEntity> bkamOptional = bkamEntityRepository.findById(Long.parseLong(id));
            if (!bkamOptional.isPresent()) {
                throw new BkamNotFoundException("Bkam non trouvé pour l'id: " + id);
            }
            BkamEntity bkam = bkamOptional.get();

            if (exerciceDate == null) {
                exerciceDate = bkam.getDateExercixe();
            } else {
                if (!exerciceDate.equals(bkam.getDateExercixe())) {
                    throw new DifferentExerciceDateException("Les états n'ont pas la même date d'exercice.");
                }
            }

            if (codeDocumentSet.contains(bkam.getCodeDocument())) {
                throw new DifferentCodeDocumentException("Les états doivent avoir des codes Etat différents.");
            } else {
                codeDocumentSet.add(bkam.getCodeDocument());
            }
        }
        return true;
    }
    public FileResponse generateFilesForListEtat(List<String> ids) throws Exception {
        if (ids == null || ids.isEmpty()) {
            throw new EmptyIdListException("La liste des IDs est vide.");
        }
        haveSameExerciceDateAndCodeDocument(ids);
        Optional<BkamEntity> firstBkamOptional = bkamEntityRepository.findById(Long.parseLong(ids.get(0)));
        if (!firstBkamOptional.isPresent()) {
            throw new Exception("Bkam non trouvé pour l'id: " + ids.get(0));
        }
        BkamEntity firstBkam = firstBkamOptional.get();
        String fileName = generateFileName(firstBkam);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, "UTF-8"))) {

            writeRemiseHeader(writer, firstBkam, ids);

            int count = 0;
            int size = ids.size();
            for (String id : ids) {
                Optional<BkamEntity> bkamOptional = bkamEntityRepository.findById(Long.parseLong(id));
                if (!bkamOptional.isPresent()) {
                    throw new Exception("Bkam non trouvé pour l'id: " + id);
                }
                BkamEntity bkam = bkamOptional.get();
                writeDocumentHeader(writer, bkam);
                writeLignesToFile(bkam, writer);
                count++;
                if (count < size) {
                    writer.newLine(); // Ajouter un saut de ligne entre chaque bloc de données, sauf après le dernier
                }
                System.out.println("Fichier généré pour BKAM avec ID " + id + ": " + fileName);
            }

            writer.flush(); // S'assurer que tout est écrit dans le ByteArrayOutputStream

            String encodedFileContent = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            FileResponse fileResponse = new FileResponse(fileName, encodedFileContent, "Fichier généré avec succès.");
            return fileResponse;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du fichier CFT pour BKAM avec ID " + ids.get(0));
        }
    }

//    public File generateFilesForListEtat(List<String> ids) throws Exception {
//        if (ids == null || ids.isEmpty()) {
//            throw new Exception("La liste des IDs est vide.");
//        }
//        if (!haveSameExerciceDateAndCodeDocument(ids)) {
//            throw new Exception("Les états n'ont pas la même date d'exercice ou avoir la même code document.");
//        }
//        Optional<BkamEntity> firstBkamOptional = bkamEntityRepository.findById(Long.parseLong(ids.get(0)));
//        if (!firstBkamOptional.isPresent()) {
//            throw new Exception("Bkam non trouvé pour l'id: " + ids.get(0));
//        }
//        BkamEntity firstBkam = firstBkamOptional.get();
//        String fileName = generateFileName(firstBkam);
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
//            writeRemiseHeader(writer, firstBkam, ids);
//            int count = 0;
//            int size = ids.size();
//            for (String id : ids) {
//                Optional<BkamEntity> bkamOptional = bkamEntityRepository.findById(Long.parseLong(id));
//                if (!bkamOptional.isPresent()) {
//                    throw new Exception("Bkam non trouvé pour l'id: " + id);
//                }
//                BkamEntity bkam = bkamOptional.get();
//                writeDocumentHeader(writer, bkam);
//                writeLignesToFile(bkam, writer);
//                count++;
//                if (count < size) {
//                    writer.newLine(); // Ajouter un saut de ligne entre chaque fichier généré, sauf après le dernier
//                }
//                System.out.println("Fichier généré pour BKAM avec ID " + id + ": " + fileName);
//            }
//            return new File(fileName);
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Erreur lors de la génération du fichier CFT pour BKAM avec ID " + ids.get(0));
//        }
//    }


    public FileResponse genereCFT(String id) throws IOException {
        List<String> ids = Collections.singletonList(id);
        Optional<BkamEntity> bkamOptional = bkamEntityRepository.findById(Long.parseLong(id));
        if (bkamOptional.isPresent()) {
            BkamEntity bkam = bkamOptional.get();
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream, "UTF-8"))) { // Use a valid encoding here
                String fileName = generateFileName(bkam);
                writeRemiseHeader(writer, bkam, ids);
                writeDocumentHeader(writer, bkam);
                writeLignesToFile(bkam, writer);
                writer.flush();
                String encodedFileContent = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());

                FileResponse fileResponse = new FileResponse(fileName, encodedFileContent, "File generated successfully.");
                return fileResponse;
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Erreur lors de la génération du fichier CFT");
            }
        } else {
            throw new BkamNotFoundException("Bkam non trouvé pour l'id: " + id);
        }
    }

    private void writeLignesToFile(BkamEntity bkam, BufferedWriter writer) throws Exception {
        switch (bkam.getCodeDocument()) {
            case "709":
            case "703":
            case "711":
            case "704":
            case "707":
            case "708":
            case "710":
            case "712":
            case "713":
                ligneXCFT.writeLignesToFile(bkam,writer);
                break;
            case "701":
            case "702":
                ligneX0102CFT.writeLignesToFile(bkam,writer);
                break;
            case "705":
                ligne705CFT.writeLignesToFile(bkam,writer);
            case "700":
                ligne700CFT.writeLignesToFile(bkam,writer);
                break;
        }

    }

    private void writeRemiseHeader(BufferedWriter writer, BkamEntity bkam, List<String> idsList) throws IOException, ParseException {
        String codeEtablissement = padLeft(bkam.getCodeEtablissement(), 3);

        SimpleDateFormat dateFormatInput = new SimpleDateFormat("MM/yyyy");
        SimpleDateFormat dateFormatOutput = new SimpleDateFormat("yyyyMM");
        String dateOutput = dateFormatOutput.format(dateFormatInput.parse(bkam.getDateExercixe()));
        String dateArrete = padRight(dateOutput, 6);

        // Calcul du nombre de documents remis en fonction de la liste d'IDs
        String nombreDocumentsRemis = padLeft(String.valueOf( idsList.size()), 3);
        writer.write(codeEtablissement);
        writer.write(dateArrete);
        writer.write(nombreDocumentsRemis);
        writer.newLine();
    }
    private void writeDocumentHeader(BufferedWriter writer, BkamEntity bkam) throws IOException {
        String codeDocument = padRight(bkam.getCodeDocument(), 3);
        String nombreChampsRenseignes = padRight(String.format("%05d", bkam.getNombreChampsRenseidnes()), 5);

        writer.write(codeDocument);
        writer.write(nombreChampsRenseignes);
        writer.newLine();
    }

    private String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }
    private String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s).replace(' ', '0');
    }
    private String generateFileName(BkamEntity bkam) {
        String codeEmetteur = bkam.getCodeEtablissement(); // Remplacez par le getter pour le code émetteur
        String codeRecepteur = "001"; // Remplacez par le code recepteur approprié
        SimpleDateFormat dateFormat = new SimpleDateFormat("mmssHHddMMyyyy");
        String formattedDate = dateFormat.format(new Date());
        //String date = bkam.getDateChargement();
        return String.format("DCC_%s_%s_%s", codeEmetteur, codeRecepteur, formattedDate);
    }
}
