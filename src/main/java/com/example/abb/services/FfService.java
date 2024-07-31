package com.example.abb.services;

import com.example.abb.dto.Banquefinancement703;
import com.example.abb.dto.axesEntities.AxeWithoutNombreClientsCartes;
import com.example.abb.exception.MissingHeaderException;
import com.example.abb.models.Anomalies.TypeAnomalie;
import com.example.abb.repositories.BFRepository;
import com.example.abb.repositories.axesRepo.WithoutNombreClientRepo;
import com.example.abb.services.Controle.XControle;
import com.example.abb.services.convertion.Convertion703Service;
import com.example.abb.services.helper.SharedServices;
import com.example.abb.utility.CodeMappingFactory;
import com.example.abb.utility.ErrorMessage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
@Service
public class FfService implements ExcelService<Banquefinancement703>{

    @Autowired
    BFRepository bfRepository;
    @Autowired
    PPPService pPRepository;
    @Autowired
    Convertion703Service conversionservice;
    @Autowired
    SharedServices sharedServices;
    @Autowired
    XControle xControle;
    @Autowired
    WithoutNombreClientRepo withoutNombreClientRepo;
    public boolean isValidExcelFile(MultipartFile file) {
        System.out.println(file.getContentType());
        return Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    public Banquefinancement703 readExcel(MultipartFile file, String etat, String exercice, String etablissement) throws IOException,MissingHeaderException {
        List<AxeWithoutNombreClientsCartes> clients = new ArrayList<>();
        List<String> anomalies = new ArrayList<>();
        Map<String, String> codeMapping = new HashMap<>(CodeMappingFactory.getCodeMapping(etat));
        if (codeMapping == null) {
            return null;
        }
        Set<String> codeMappingKeys = codeMapping.keySet();
        Set<String> foundKeys = new HashSet<>();
        Properties headers = sharedServices.loadHeadersForEtat(etat);
        try (InputStream fis = file.getInputStream(); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Banquefinancement703 PP = new Banquefinancement703(etat,exercice,etablissement,new Date());
            Iterator<Row> rowIterator = sheet.rowIterator();
            Map<String, Integer> headerMap;
            if(etat.equals("713")){
                 headerMap =pPRepository.findHeaders(sheet);
            }else{
                 headerMap = findHeaders(sheet);

            }
            sharedServices.verifyHeaders(headers, headerMap);
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();
                boolean relevantRow = false;
                String relevantPrefix = null;
                String libellePrefix = "";

                for (Cell cell : currentRow) {
                    if (cell.getCellType() == CellType.STRING) {
                        String cellValue = cell.getStringCellValue();
                        if (cellValue.matches("^(CL|PDT|TR|CD|GEO|Autres)-.*") || cellValue.matches("^CL[1-3]$")) {
                            if ("708".equals(etat) && "CL2".equals(cellValue)) {
                                // Skip rows with CL2 if etat is 708 car pas existe dans notice
                                continue;
                            }
                            if ("713".equals(etat) && "CL-05".equals(cellValue)) {
                                // Skip rows with CL-05 if etat is 713 car pas existe dans notice
                                continue;
                            }
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
                    AxeWithoutNombreClientsCartes axe = new AxeWithoutNombreClientsCartes();
                    if (relevantPrefix.indexOf('-') == -1) {
                        axe.setAxes(relevantPrefix.split("\\d")[0]);
                    } else {
                        axe.setAxes(relevantPrefix.substring(0, relevantPrefix.indexOf('-')));
                    }
                    axe.setCodeAxes(relevantPrefix);
                    axe.setLibelleAxes(libellePrefix);
                    axe.setNumLigne(currentRow.getRowNum());
                    Cell cell;

                    cell = currentRow.getCell(headerMap.get(headers.getProperty("nombre_clients")));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                            axe.setNombreClients((int) cell.getNumericCellValue());
                        } else {
                            anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour nombre clients");
                        }
                    }

                    cell = currentRow.getCell(headerMap.get(headers.getProperty("encours_depots")));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                            axe.setEncoursDepots(cell.getNumericCellValue());
                        } else {
                            anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour encours depots");
                        }
                    }

                    cell = currentRow.getCell(headerMap.get(headers.getProperty("flux_debiteurs")));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                            axe.setFluxDebiteurs2020(cell.getNumericCellValue());
                        } else {
                            anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour flux debiteurs");
                        }
                    }

                    cell = currentRow.getCell(headerMap.get(headers.getProperty("flux_crediteurs")));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                            axe.setFluxCrediteurs2020(cell.getNumericCellValue());
                        } else {
                            anomalies.add(ErrorMessage.CELLULE_NON_NUMERIQUE.getMessage() + " pour flux crediteurs");
                        }
                    }

                    cell = currentRow.getCell(headerMap.get(headers.getProperty("risque_inherent")));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.STRING) {
                            axe.setRisqueInherent(cell.getStringCellValue().trim());
                        } else {
                            anomalies.add(ErrorMessage.CELLULE_NON_STRING.getMessage() + " pour risque inherent");
                        }
                    }

                    cell = currentRow.getCell(headerMap.get(headers.getProperty("commentaire")));
                    if (cell != null) {
                        if (cell.getCellType() == CellType.BLANK || cell.getCellType() == CellType.STRING) {
                            axe.setCommentaires(cell.getStringCellValue().trim());
                        } else {
                            anomalies.add(ErrorMessage.CELLULE_NON_STRING.getMessage() + " pour commentaire");
                        }
                    }

                    withoutNombreClientRepo.save(axe);
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
            for (AxeWithoutNombreClientsCartes item : clients) {
                item.setBanquefinancement703(PP);
            }
            PP.setProduits(clients);
            conversionservice.convertTobkamMapper(bfRepository.save(PP));

            return PP;
        }catch (MissingHeaderException e) {
            throw e;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private Map<String, Integer> findHeaders(Sheet sheet) {
        Map<String, Integer> headerMap = new HashMap<>();
        boolean headerFound = false;
        Iterator<Row> rowIterator = sheet.rowIterator();

        while (rowIterator.hasNext() && !headerFound) {
            Row currentRow = rowIterator.next();
            for (Cell cell : currentRow) {
                if (cell.getCellType() == CellType.STRING && cell.getStringCellValue().startsWith("Axe")) {
                    System.err.println("value header ;"+cell.getStringCellValue());
                         headerFound = pPRepository.processHeaderRow(currentRow, headerMap);
                        break;
                }
            }
        }
        if (!headerFound) {
            throw new MissingHeaderException("Header row with 'Axes' not found.");
        }

        return headerMap;
    }


   
}
