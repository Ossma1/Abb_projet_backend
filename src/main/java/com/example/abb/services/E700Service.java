package com.example.abb.services;

import com.example.abb.dto.AxesE700.AxeE700;
import com.example.abb.dto.AxesE700.ColumnValue;
import com.example.abb.dto.E700DTO;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.exception.MissingHeaderException;
import com.example.abb.models.Anomalies.TypeAnomalie;
import com.example.abb.repositories.E700Repository;
import com.example.abb.repositories.PPRepository;
import com.example.abb.repositories.axesRepo.Axe700Repository;
import com.example.abb.services.Controle.XControle;
import com.example.abb.services.convertion.Conversion0102Service;
import com.example.abb.services.convertion.Conversion700Service;
import com.example.abb.utility.CodeLigne700;
import com.example.abb.utility.CodeMappingFactory;
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
public class E700Service  implements ExcelService<E700DTO>{
    @Autowired
    private E700Repository e700Repository;
    @Autowired
    Conversion0102Service conversion0102service;
    @Autowired
    private Conversion700Service conversion700Service;
    @Autowired
    XControle xControle;
    @Autowired
    Axe700Repository axe700Repository;

    private static final List<String> REQUIRED_HEADERS = Arrays.asList(
            "Le trafic illicite de stupéfiants et de substances psychotropes",
            "Le trafic d'êtres humains",
            "Le trafic d'immigrants",
            "Le trafic illicite d'armes et de munitions",
            "La corruption, la concussion, le trafic d'influence et le détournement de biens publics et privés",
            "La contrefaçon ou la falsification des monnaies ou effets de crédit public ou d'autres moyens de paiement",
            "L'exploitation sexuelle",
            "L'abus de confiance",
            "L’escroquerie",
            "Le vol et l'extorsion",
            "La contrebande",
            "La fraude sur les marchandises et sur les denrées alimentaires",
            "Le faux, l’usage de faux et l’usurpation ou l’usage irrégulier de fonctions, de titres ou de noms",
            "L’atteinte aux systèmes de traitement automatisé des données",
            "Le financement du terrorisme",
            "Total"
    );
    public boolean isValidExcelFile(MultipartFile file) {
        System.out.println(file.getContentType());
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public E700DTO readExcel(MultipartFile file, String etat, String exercice, String etablissement) throws IOException,MissingHeaderException {
        List<AxeE700> axes = new ArrayList<>();
        List<String> anomalies = new ArrayList<>();
        Map<String, String> codeMapping = new HashMap<>(CodeMappingFactory.getCodeMapping(etat));
        if (codeMapping == null) {
            return null;
        }
        Collection<String> codeMappingKeys = codeMapping.values();
        Set<String> foundKeys = new HashSet<>();
        try (InputStream fis = file.getInputStream(); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            E700DTO e700DTO = new E700DTO(etat,exercice,etablissement,new Date());
            Iterator<Row> rowIterator = sheet.rowIterator();
            Map<String, Integer> headerMap = findHeaders(sheet);
            validateHeaders(headerMap);
            boolean firstTotalFound = false;

            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                boolean relevantRow = false;
                String ligneDoc = null;
                for (Cell cell : currentRow) {
                    if (cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue().trim();
                        if (cellValue.equals("Total")) {
                            if (!firstTotalFound) {
                                firstTotalFound = true;
                                continue;
                            } else {
                                relevantRow = true;
                                ligneDoc = cellValue;
                                break;
                            }
                        }
                        boolean isRelevant = CodeLigne700.codeMapping.containsValue(cellValue);
                        if (isRelevant) {
                            relevantRow = true;
                            ligneDoc = cellValue;
                            break;
                        }
                    }
                }

                if (relevantRow) {
                    AxeE700 axe = new AxeE700();
                    axe.setLigneDocument(ligneDoc);
                    axe.setNumLigne(currentRow.getRowNum());
                    Cell cell;
                    // Le trafic illicite de stupéfiants et de substances psychotropes
                    cell = currentRow.getCell(headerMap.get("Le trafic illicite de stupéfiants et de substances psychotropes"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setTraficIlliciteStupefiants(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setTraficIlliciteStupefiants(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' trafic illicite de stupéfiants ...");
                    }
                    cell = currentRow.getCell(headerMap.get("Le trafic d'êtres humains"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setTraficEtresHumains(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setTraficEtresHumains(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' trafic d'êtres humains'");
                    }
                    cell = currentRow.getCell(headerMap.get("Le trafic d'immigrants"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setTraficImmigrants(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setTraficImmigrants(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' Le trafic d'immigrants ...");
                    }
                    cell = currentRow.getCell(headerMap.get("Le trafic illicite d'armes et de munitions"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setTraficIlliciteArmes(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setTraficIlliciteArmes(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' Le trafic illicite d'armes ...");
                    }
                    cell = currentRow.getCell(headerMap.get("La corruption, la concussion, le trafic d'influence et le détournement de biens publics et privés"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setCorruptionConcussion(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setCorruptionConcussion(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' La corruption, la concussion ...");
                    }
                    cell = currentRow.getCell(headerMap.get("La contrefaçon ou la falsification des monnaies ou effets de crédit public ou d'autres moyens de paiement"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setContrefaconMonnaies(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setContrefaconMonnaies(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' La contrefaçon ou la falsification ...");
                    }
                    cell = currentRow.getCell(headerMap.get("L'exploitation sexuelle"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setExploitationSexuelle(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setExploitationSexuelle(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' L'exploitation sexuelle'");
                    }
                    cell = currentRow.getCell(headerMap.get("L'abus de confiance"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setAbusConfiance(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setAbusConfiance(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' L'abus de confiance'");
                    }
                    cell = currentRow.getCell(headerMap.get("L’escroquerie"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setEscroquerie(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setEscroquerie(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' L’escroquerie'");
                    }
                    cell = currentRow.getCell(headerMap.get("Le vol et l'extorsion"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setVolExtorsion(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setVolExtorsion(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' Le vol et l'extorsion'");
                    }
                    cell = currentRow.getCell(headerMap.get("La contrebande"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setContrebande(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setContrebande(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' La contrebande'");
                    }
                    cell = currentRow.getCell(headerMap.get("La fraude sur les marchandises et sur les denrées alimentaires"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setFraudeMarchandises(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setFraudeMarchandises(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' La fraude sur les marchandises ...");
                    }
                    cell = currentRow.getCell(headerMap.get("Le faux, l’usage de faux et l’usurpation ou l’usage irrégulier de fonctions, de titres ou de noms"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setFauxUsageFaux(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setFauxUsageFaux(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' Le faux, l’usage de faux ...");
                    }

                    cell = currentRow.getCell(headerMap.get("L’atteinte aux systèmes de traitement automatisé des données"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setAttelongeSystemesTraitement(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setAttelongeSystemesTraitement(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' L’atteinte aux systèmes ...");
                    }

                    cell = currentRow.getCell(headerMap.get("Le financement du terrorisme"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setFinancementTerrorisme(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setFinancementTerrorisme(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' Le financement du terrorisme'");
                    }
                    cell = currentRow.getCell(headerMap.get("Total"));
                    if (cell.getCellType() == CellType.BLANK ||cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                        axe.setTotal(new ColumnValue(cell.getColumnIndex(), (long) cell.getNumericCellValue()));
                    } else {
                        axe.setTotal(new ColumnValue(cell != null ? cell.getColumnIndex() : -1, 0));
                        anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " dans la colone ' Total'");
                    }

                    axe700Repository.save(axe);
                    for (String anomaly : anomalies) {
                        xControle.createAnomalie(axe, anomaly, TypeAnomalie.ERREUR_DE_TYPE);
                    }
                    anomalies.clear();
                    axes.add(axe);
                    foundKeys.add(ligneDoc);
                }
            }

            codeMappingKeys.removeAll(foundKeys);
            if (!codeMappingKeys.isEmpty()) {
              throw new MissingHeaderException("The following lignes document are not found in the Excel file: " + codeMappingKeys);
            }
            for (AxeE700 item : axes) {
                item.setE700DTO(e700DTO);
            }
            e700DTO.setAxes(axes);
            conversion700Service.convertTobkam(e700Repository.save(e700DTO));

            return e700DTO;
        }catch (MissingHeaderException e) {
            throw e;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public Map<String, Integer> findHeaders(Sheet sheet) {
        Map<String, Integer> headerMap = new HashMap<>();
        boolean headerFound = false;
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext() && !headerFound) {
            Row currentRow = rowIterator.next();
            for (Cell cell : currentRow) {
                if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().contains("Typologie de l'infraction")) {
                        headerFound = processHeaderRow(currentRow, headerMap);
                        break;
                }
            }
        }
        if (!headerFound) {
            throw new MissingHeaderException("Header row with 'Typologie de l'infraction' not found.");
        }

        return headerMap;
    }
    private void validateHeaders(Map<String, Integer> headerMap) throws MissingHeaderException {
        for (String requiredHeader : REQUIRED_HEADERS) {
            if (!headerMap.containsKey(requiredHeader)) {
                throw new MissingHeaderException("Header missing: " + requiredHeader);
            }
        }
    }
    public boolean processHeaderRow(Row currentRow, Map<String, Integer> headerMap) {
        for (Cell headerCell : currentRow) {
            if (headerCell.getCellType() == CellType.STRING){
                String headerValue = headerCell.getStringCellValue().trim();
                headerMap.put(headerValue, headerCell.getColumnIndex());
              //  System.out.println("Header found: " + headerValue + " at index " + headerCell.getColumnIndex());
                if (headerValue.contains("Total")) {
                    return true;
                }
            }

        }
        return false;
    }
}
