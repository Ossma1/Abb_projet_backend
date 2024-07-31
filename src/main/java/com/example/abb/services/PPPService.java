package com.example.abb.services;

import com.example.abb.dto.BanqueParticuliersProfessionnels;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.dto.axesEntities.AxeWithoutNombreClientsCartes;
import com.example.abb.exception.MissingHeaderException;
import com.example.abb.models.Anomalies.TypeAnomalie;
import com.example.abb.repositories.PPRepository;
import com.example.abb.repositories.axesRepo.WithNombreClientRepo;
import com.example.abb.services.Controle.XControle;
import com.example.abb.utility.CodeMappingFactory;
import com.example.abb.utility.ErrorMessage;
import com.example.abb.services.convertion.Conversion0102Service;
import com.example.abb.services.helper.SharedServices;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class PPPService implements ExcelService<BanqueParticuliersProfessionnels> {
    @Autowired
    private PPRepository pPRepository;
    @Autowired
    private WithNombreClientRepo withNombreClientRepo;
    @Autowired
    Conversion0102Service conversion0102service;
    @Autowired
    SharedServices sharedServices;
    @Autowired
    XControle xControle;
    private static final List<String> REQUIRED_HEADERS_701 = Arrays.asList(
            "Nombre d'opérations",
            "Nombres de cartes prépayés",
            "Flux débiteurs 2020",
            "Flux créditeurs 2020",
            "Nombre de clients de cartes prépayés",
            "Risque inhérent (Liste)",
            "Commentaires de l'établissement"
    );

    private static final List<String> REQUIRED_HEADERS_DEFAULT = Arrays.asList(
            "Nombre d'opérations",
            "Nombres de cartes prépayés",
            "Flux débiteurs 2019",
            "Flux créditeurs 2019",
            "Nombre de clients de cartes prépayés",
            "Risque inhérent (Liste)",
            "Commentaires de l'établissement"
    );
    public boolean isValidExcelFile(MultipartFile file) {
        System.out.println(file.getContentType());
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public BanqueParticuliersProfessionnels readExcel(MultipartFile file, String etat, String exercice, String etablissement) throws IOException, MissingHeaderException {
        List<AxeWithNombreClientsCartes> clients = new ArrayList<>();
        List<String> anomalies = new ArrayList<>();
        Map<String, String> codeMapping = new HashMap<>(CodeMappingFactory.getCodeMapping(etat));
        if (codeMapping == null) {
            return null;
        }
        Set<String> codeMappingKeys = codeMapping.keySet();
        Set<String> foundKeys = new HashSet<>();

        try (InputStream fis = file.getInputStream(); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            BanqueParticuliersProfessionnels PP = new BanqueParticuliersProfessionnels(etat,exercice,etablissement,new Date());
            Iterator<Row> rowIterator = sheet.rowIterator();
            Map<String, Integer> headerMap = findHeaders(sheet);
            List<String> requiredHeaders = etat.equals("701") ? REQUIRED_HEADERS_701 : REQUIRED_HEADERS_DEFAULT;
            validateHeaders(headerMap, requiredHeaders);

            // Read the data rows
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                boolean relevantRow = false;
                String relevantPrefix = null;
                String libellePrefix = "";

                for (Cell cell : currentRow) {
                    if (cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue().trim();
                        boolean isRelevantPrefix = cellValue.matches("^(CL|PDT|TR|CD|GEO|Autres)-.*");

                        // Inclure "cl1" si etat701 est vrai
                        if (etat.equals("701")) {
                            isRelevantPrefix = isRelevantPrefix || cellValue.equalsIgnoreCase("cl1");
                        }

                        if (isRelevantPrefix) {
                            relevantRow = true;
                            relevantPrefix = cellValue;
                            Cell libelleCell = currentRow.getCell(cell.getColumnIndex() + 2);
                            if (libelleCell != null && libelleCell.getCellType() == CellType.STRING) {
                                libellePrefix = libelleCell.getStringCellValue().trim();
                            }
                            break;
                        }
                    }
                }

                if (relevantRow) {
                    AxeWithNombreClientsCartes axe = new AxeWithNombreClientsCartes();
                    if (relevantPrefix.indexOf('-') == -1) {
                        axe.setAxes(relevantPrefix.split("\\d")[0]);
                    } else {
                        axe.setAxes(relevantPrefix.substring(0, relevantPrefix.indexOf('-')));
                    }
                    axe.setCodeAxes(relevantPrefix);
                    axe.setLibelleAxes(libellePrefix);
                    axe.setNumLigne(currentRow.getRowNum());
                    Cell cell;

                    cell = currentRow.getCell(headerMap.get("Nombre d'opérations"));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                            axe.setNombreClients((long) cell.getNumericCellValue());
                        } else {
                            anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour Nombre d'opérations");
                        }
                    }

                    cell = currentRow.getCell(headerMap.get("Nombres de cartes prépayés"));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                            axe.setEncoursDepots(cell.getNumericCellValue());
                        } else {
                            anomalies.add( ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour Nombres de cartes prépayés");
                        }
                    }

                    if (etat.equals("701")) {
                        cell = currentRow.getCell(headerMap.get("Flux débiteurs 2020"));
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                axe.setFluxDebiteurs2020(cell.getNumericCellValue());
                            } else {
                                    anomalies.add( ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour Flux débiteurs 2020");
                            }
                        }

                        cell = currentRow.getCell(headerMap.get("Flux créditeurs 2020"));
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                axe.setFluxCrediteurs2020(cell.getNumericCellValue());
                            } else {
                                    anomalies.add( ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour Flux créditeurs 2020");
                            }
                        }
                    } else {
                        cell = currentRow.getCell(headerMap.get("Flux débiteurs 2019"));
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                axe.setFluxDebiteurs2020(cell.getNumericCellValue());
                            } else {
                                    anomalies.add( ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour Flux débiteurs 2019");
                            }
                        }

                        cell = currentRow.getCell(headerMap.get("Flux créditeurs 2019"));
                        if (cell != null) {
                            if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                                axe.setFluxCrediteurs2020(cell.getNumericCellValue());
                            } else {
                                    anomalies.add( ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour Flux créditeurs 2019");
                            }
                        }
                    }

                    cell = currentRow.getCell(headerMap.get("Nombre de clients de cartes prépayés"));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                            axe.setNombreClientsCartes((long) cell.getNumericCellValue());
                        } else {
                                anomalies.add( ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour Nombre de clients de cartes prépayés");
                        }
                    }

                    cell = currentRow.getCell(headerMap.get("Risque inhérent (Liste)"));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.STRING) {
                            axe.setRisqueInherent(cell.getStringCellValue().trim());
                        } else {
                                anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + "pour Risque inhérent (Liste)");
                        }
                    }

                    cell = currentRow.getCell(headerMap.get("Commentaires de l'établissement"));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.STRING) {
                            axe.setCommentaires(cell.getStringCellValue().trim());
                        } else {
                                anomalies.add( ErrorMessage.CELLULE_NON_STRING.getMessage() + " pour Commentaires de l'établissement");
                        }
                    }
                    withNombreClientRepo.save(axe);
                    for (String anomaly : anomalies) {
                        xControle.createAnomalie(axe, anomaly, TypeAnomalie.ERREUR_DE_TYPE);
                    }
                    anomalies.clear();
                    clients.add(axe);
                    foundKeys.add(relevantPrefix);
                }

            }
            codeMappingKeys.removeAll(foundKeys);
            if (!codeMappingKeys.isEmpty()) {
                throw new MissingHeaderException("The following code axes are not found in the Excel file: " + codeMappingKeys);
            }
            for (AxeWithNombreClientsCartes item : clients) {
                item.setBanqueParticuliersProfessionnels(PP);
            }
            PP.setClients(clients);
            conversion0102service.convertToBkam(pPRepository.save(PP));

            return PP;
        }catch (MissingHeaderException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    public Map<String, Integer> findHeaders(Sheet sheet) {
        Map<String, Integer> headerMap = new HashMap<>();
        boolean headerFound = false;
        Iterator<Row> rowIterator = sheet.rowIterator();
        int countAxesc = 0;

        while (rowIterator.hasNext() && !headerFound) {
            Row currentRow = rowIterator.next();
            for (Cell cell : currentRow) {
                if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().equals("Axe")) {
                    countAxesc++;
                    if (countAxesc == 2) {
                        headerFound = processHeaderRow(currentRow, headerMap);
                        break;
                    }
                }
            }
        }

        if (!headerFound) {
            throw new MissingHeaderException("Header row with 'Axes' not found.");
        }

        return headerMap;
    }
    public boolean processHeaderRow(Row currentRow, Map<String, Integer> headerMap) {
        for (Cell headerCell : currentRow) {
            if (headerCell.getCellType() == CellType.STRING){
                String headerValue = headerCell.getStringCellValue().trim();
                headerMap.put(headerValue, headerCell.getColumnIndex());
                //System.out.println("Header found: " + headerValue + " at index " + headerCell.getColumnIndex());
                if (headerValue.contains("Commentaire")) {
                    return true;
                }
            }

        }
        return false;
    }
    private void validateHeaders(Map<String, Integer> headerMap, List<String> requiredHeaders) throws MissingHeaderException {
        for (String requiredHeader : requiredHeaders) {
            if (!headerMap.containsKey(requiredHeader)) {
                throw new MissingHeaderException("Missing required header: " + requiredHeader);
            }
        }
    }
}
