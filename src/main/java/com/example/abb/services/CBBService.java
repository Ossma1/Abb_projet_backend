package com.example.abb.services;

import com.example.abb.dto.CorrespondanceBancaire705;
import com.example.abb.dto.Tables705.CB1;
import com.example.abb.dto.Tables705.CB2;
import com.example.abb.dto.Tables705.Pays;
import com.example.abb.exception.MissingHeaderException;
import com.example.abb.models.Anomalies.TypeAnomalie;
import com.example.abb.repositories.CBRepository;
import com.example.abb.repositories.tables705Repo.Cb1Repo;
import com.example.abb.repositories.tables705Repo.Cb2Repo;
import com.example.abb.repositories.tables705Repo.PaysRepo;
import com.example.abb.services.Controle.XControle;
import com.example.abb.services.convertion.Conversion705Service;
import com.example.abb.services.helper.SharedServices;
import com.example.abb.utility.ErrorMessage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class CBBService  implements ExcelService<CorrespondanceBancaire705> {
    @Autowired
    CBRepository cbRepository;
    @Autowired
    PaysRepo paysRepository;
    @Autowired
    Conversion705Service conversionservice;
    @Autowired
    XControle xControle;
    @Autowired
    Cb2Repo cb2Repo;
    @Autowired
    Cb1Repo cb1Repo;
    @Autowired
    SharedServices sharedServices;
    public boolean isValidExcelFile(MultipartFile file) {
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public CorrespondanceBancaire705 readExcel(MultipartFile file, String etat, String exercice, String etablissement) throws IOException {
        CorrespondanceBancaire705 PB = new CorrespondanceBancaire705(etat, exercice, etablissement, new Date());
        PB = cbRepository.save(PB);


        List<CB1> cb1List = readSheet0(file, etat);
        List<CB2> cb2List = readSheet1(file, etat);
        List<Pays> paysList = readSheet2(file);

        for (CB1 cb1 : cb1List) {
            cb1.setCorrespondanceBancaire705(PB);
        }

        for (CB2 cb2 : cb2List) {
            cb2.setCorrespondanceBancaire705(PB);
        }

        paysRepository.deleteAll();
        paysRepository.saveAll(paysList);

        PB.setListCB1(cb1List);
        PB.setListCB2(cb2List);

        cbRepository.save(PB);

        conversionservice.convertTobkam705mapper(PB);
        return PB;
    }


    private List<CB1> readSheet0(MultipartFile excelFilePath, String etat) throws IOException {
        List<CB1> cb1s = new ArrayList<>();
        List<String> anomalies = new ArrayList<>();
        Properties headers = sharedServices.loadHeadersForEtat(etat); // Load headers based on etat
        try (InputStream fis = excelFilePath.getInputStream();
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Map<String, Integer> headerMap = findHeaders(sheet, headers.getProperty("cb1_pays"));
            Properties filteredHeaders = new Properties();
            for (String key : headers.stringPropertyNames()) {
                if (key.startsWith("cb1_")) {
                    filteredHeaders.put(key, headers.getProperty(key));
                }
            }
            sharedServices.verifyHeaders(filteredHeaders, headerMap);
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                boolean relevantRow = false;
                String relevantType = null;
                for (Cell cell : currentRow) {
                    if (cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue();
                        if (cellValue.contains("Total Flux Emis")) {
                            relevantRow = true;
                            relevantType = "Emis";
                            break;
                        } else if (cellValue.contains("Total Flux Reçus")) {
                            relevantRow = true;
                            relevantType = "Reçu";
                            break;
                        }
                    }
                }
                if (relevantRow) {
                    int count = 0;
                    while (rowIterator.hasNext() && count <= 4) {
                        count++;
                        currentRow = rowIterator.next();
                        CB1 cb1 = new CB1();
                        cb1.setNumLigne(currentRow.getRowNum());
                        cb1.setTotalFluxType(relevantType);
                        Cell cell;

                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb1_pays")));
                        if (cell.getCellType() == CellType.BLANK ||  cell.getCellType() == CellType.STRING) {
                            cb1.setPays(cell.getStringCellValue().trim());
                        }else if(cell.getCellType() == CellType.NUMERIC){
                            cb1.setPays(Double.toString(cell.getNumericCellValue()));
                        }

                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb1_nombre")));
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb1.setNombreValue((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone nombre");
                            }
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb1_volume")));
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb1.setVolume((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone volume");
                            }
                        }
                        cb1Repo.save(cb1);
                        for (String anomaly : anomalies) {
                            xControle.createAnomalie(cb1, anomaly, TypeAnomalie.ERREUR_DE_TYPE);
                        }
                        anomalies.clear();
                        cb1s.add(cb1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cb1s;
    }

    private List<CB2> readSheet1(MultipartFile file, String etat) throws IOException {
        List<CB2> cb2s = new ArrayList<>();
        List<String> anomalies = new ArrayList<>();
        Properties headers = sharedServices.loadHeadersForEtat(etat);
        try (InputStream fis = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(1); // Read Sheet 1
            Iterator<Row> rowIterator = sheet.iterator();
            Map<String, Integer> headerMap = findHeaders(sheet, headers.getProperty("cb2_nom_correspondant"));
            Properties filteredHeaders = new Properties();
            for (String key : headers.stringPropertyNames()) {
                if (key.startsWith("cb2_")) {
                    filteredHeaders.put(key, headers.getProperty(key));
                }
            }
            sharedServices.verifyHeaders(filteredHeaders, headerMap);

            boolean relevantRowFound = false;

            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                boolean relevantRow = false;

                if (relevantRowFound) {
                    Cell cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_nom_correspondant")));
                    if (cell != null && cell.getCellType() == CellType.STRING) {
                        relevantRow = true;
                    }
                } else {
                    for (Cell cell : currentRow) {
                        if (cell.getCellType() == CellType.STRING) {
                            String cellValue = cell.getStringCellValue();
                            if (cellValue.contains(headers.getProperty("cb2_nom_correspondant"))) {
                                relevantRowFound = true;
                                break;
                            }
                        }
                    }
                }
                if (relevantRow) {
                    boolean firstItem = false;
                    while (rowIterator.hasNext()) {
                        if (firstItem) currentRow = rowIterator.next();
                        firstItem = true;
                        CB2 cb2 = new CB2();
                        cb2.setNumLigne(currentRow.getRowNum());
                        Cell cell;

                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_nom_correspondant")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setNom(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_code_bic")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setCodeBIC(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_date_entree_relation")));
                        if (cell != null && DateUtil.isCellDateFormatted(cell)) {
                            cb2.setDateEntreeEnRelation(cell.getDateCellValue());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_maison_mere_filiale")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setMaisonMereOuFiliale(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_compte_nostro_vostro")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setCompteNostroOuVostro(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_devise")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setDevise(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_pays_agrement")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setPaysAgrement(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_risque_bcft")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setNiveauRisqueBCFTDuPays(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_profil_risque")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setProfilRisqueBeneficiairesEffectifs(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_utilisation_compte")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setUtilisationCompteCorrespondanceParTiers(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_statut_compte")));
                        if (cell != null && cell.getCellType() == CellType.STRING) {
                            cb2.setStatutDuCompte(cell.getStringCellValue().trim());
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_statut_compte")) + 1);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb2.setNombreFluxEmis((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone Flux Emis Nombre");
                            }
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_statut_compte")) + 2);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb2.setNombreFluxEmisPaysRisque((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone Flux Emis Nombre PaysRisque ");
                            }
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_statut_compte")) + 3);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb2.setVolumeFluxEmis((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone Flux Emis volume");
                            }
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_statut_compte")) + 4);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb2.setVolumeFluxEmisPaysRisque((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone Flux Emis volume PaysRisque");
                            }
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_statut_compte")) + 5);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb2.setNombreFluxRecus((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone Flux Recus Nombre");
                            }
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_statut_compte")) + 6);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb2.setNombreFluxRecusPaysRisque((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone Flux Recus Nombre PaysRisque");
                            }
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_statut_compte")) + 7);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb2.setVolumeFluxRecus((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone Flux Recus Volume");
                            }
                        }
                        cell = currentRow.getCell(headerMap.get(headers.getProperty("cb2_statut_compte")) + 8);
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                cb2.setVolumeFluxRecusPaysRisque((int) cell.getNumericCellValue());
                            } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour la colone Flux Recus Volume PaysRisque");
                            }
                        }

                        cb2Repo.save(cb2);
                        for (String anomaly : anomalies) {
                            xControle.createAnomalie(cb2, anomaly, TypeAnomalie.ERREUR_DE_TYPE);
                        }
                        anomalies.clear();
                        cb2s.add(cb2);

                    }
                }

            }

        } catch (MissingHeaderException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cb2s;
    }

    private List<Pays> readSheet2(MultipartFile file) {
        List<Pays> paysList = new ArrayList<>();
        try (InputStream fis = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(3); // Read Sheet 1
            Iterator<Row> rowIterator = sheet.iterator();


            boolean headerFound = false;
            int codeIndex = -1;
            int paysIndex = -1;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (!headerFound) {
                    // Find the header row and determine the indices of "code" and "pays"
                    for (Cell cell : row) {
                        String cellValue = cell.getStringCellValue();
                        if ("code".equalsIgnoreCase(cellValue)) {
                            codeIndex = cell.getColumnIndex();
                        } else if ("pays".equalsIgnoreCase(cellValue)) {
                            paysIndex = cell.getColumnIndex();
                        }
                    }
                    if (codeIndex != -1 && paysIndex != -1) {
                        headerFound = true;
                    }
                } else {
                    // Process the data rows
                    Pays pays = new Pays();
                    boolean hasStringCode = false;
                    Cell codeCell = row.getCell(codeIndex);
                    Cell paysCell = row.getCell(paysIndex);

                    if (codeCell != null) {
                        CellType type = codeCell.getCellType();
                        if (type == CellType.STRING) {
                            hasStringCode = true;
                            pays.setCode(codeCell.getStringCellValue().trim());
                        }
                    }
                    if (paysCell != null) {
                        pays.setPays(paysCell.getStringCellValue());
                    }
                    if (hasStringCode) {
                        paysList.add(pays);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paysList;
    }

    public Map<String, Integer> findHeaders(Sheet sheet, String key) {
        Map<String, Integer> headerMap = new HashMap<>();
        boolean headerFound = false;
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext() && !headerFound) {
            Row currentRow = rowIterator.next();
            for (Cell cell : currentRow) {
                if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals(key)) {
                    headerFound = processHeaderRow(currentRow, headerMap, key);
                }
            }
        }
        if (!headerFound) {
            throw new MissingHeaderException("Header row with "+key+"not found.");
        }

        return headerMap;
    }

    public boolean processHeaderRow(Row currentRow, Map<String, Integer> headerMap, String key) {
        for (Cell headerCell : currentRow) {
            if (headerCell.getCellType() == CellType.STRING) {
                String headerValue = headerCell.getStringCellValue().trim();
                headerMap.put(headerValue, headerCell.getColumnIndex());
                System.out.println("Header found: " + headerValue + " at index " + headerCell.getColumnIndex());
                String key2;
                if (key.equals("Pays")) {
                    key2 = "volume";
                } else {
                    key2 = "Flux";
                }
                if (headerValue.contains(key2)) {
                    return true;
                }
            }

        }
        return false;
    }


}

