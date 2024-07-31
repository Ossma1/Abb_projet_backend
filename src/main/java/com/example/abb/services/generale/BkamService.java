package com.example.abb.services.generale;

import com.example.abb.dto.*;
import com.example.abb.dto.AxesE700.AxeE700;
import com.example.abb.dto.Tables705.CB1;
import com.example.abb.dto.Tables705.CB2;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.dto.axesEntities.AxeWithoutNombreClientsCartes;
import com.example.abb.models.Anomalies.TypeAnomalie;
import com.example.abb.models.BkamEntity;
import com.example.abb.models.LigneFolioCB1;
import com.example.abb.repositories.*;
import com.example.abb.repositories.ModelRepo.*;
import com.example.abb.repositories.axesRepo.Axe700Repository;
import com.example.abb.repositories.axesRepo.WithNombreClientRepo;
import com.example.abb.repositories.axesRepo.WithoutNombreClientRepo;
import com.example.abb.repositories.repoAnomalies.AnomalieRepository;
import com.example.abb.repositories.tables705Repo.Cb1Repo;
import com.example.abb.repositories.tables705Repo.Cb2Repo;
import com.example.abb.services.convertion.Conversion0102Service;
import com.example.abb.services.convertion.Conversion700Service;
import com.example.abb.services.convertion.Conversion705Service;
import com.example.abb.services.convertion.Convertion703Service;
import com.example.abb.services.helper.SortingService;
import com.example.abb.utility.ListeEtats;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class BkamService {
    @Autowired
    private BkamEntityRepository bkamEntityRepository;
    @Autowired
    private AnomalieRepository anomalieRepository;

    @Autowired
    private E700Repository  e700Repository;
    @Autowired
    private BFRepository  bfRepository;
    @Autowired
    private CBRepository cbRepository;
    @Autowired
    private PPRepository ppRepository;
    @Autowired
    private SortingService sortingService;
    @Autowired
    private WithNombreClientRepo clientRepo;
    @Autowired
    private WithoutNombreClientRepo produitRepo;
    @Autowired
    private Axe700Repository axe700Repository;
    @Autowired
    private Cb1Repo cb1Repo;
    @Autowired
    private Cb2Repo cb2Repo;
    @Autowired
    private Convertion703Service convertion703Service;
    @Autowired
    private Conversion700Service convertion700Service;
    @Autowired
    private Conversion0102Service conversion0102Service;
    @Autowired
    private Conversion705Service conversion705Service;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String[] CODE_DOCUMENTS_PP = {"701", "702"};
    private static final String[] CODE_DOCUMENTS_BF = {"703", "704", "707", "708", "709", "710", "711", "712", "713"};
    private static final String CODE_DOCUMENT_CB = "705";
    private static final String CODE_DOCUMENT_700 = "700";
    @Autowired
    private LigneXRepo ligneXRepo;
    @Autowired
    private LigneX0102Repo ligneX0102Repo;
    @Autowired
    private LigneCb1Repo ligneCb1Repo;
    @Autowired
    private LigneCb2Repo ligneCb2Repo;


    public Map<String, Object> getAxesByBkamEntityId(Long bkamEntityId) {
        BkamEntity bkamEntity = getBkamEntityById(bkamEntityId);
        Map<String, Object> result = initializeResultMap(bkamEntity);

        if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_PP)) {
            BanqueParticuliersProfessionnels pp = ppRepository.findByBkamEntityId(bkamEntity.getId());
            result.put("axes", Optional.ofNullable(pp).map(BanqueParticuliersProfessionnels::getClients).orElse(null));
        } else if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_BF)) {
            Banquefinancement703 bf703 = bfRepository.findByBkamEntityId(bkamEntity.getId());
            result.put("axes", Optional.ofNullable(bf703).map(Banquefinancement703::getProduits).orElse(null));
        }else if (CODE_DOCUMENT_700.equals(bkamEntity.getCodeDocument())) {
                E700DTO e700DTO = e700Repository.findByBkamEntityId(bkamEntity.getId());
                result.put("axes", Optional.ofNullable(e700DTO).map(E700DTO::getAxes).orElse(null));
        } else if (CODE_DOCUMENT_CB.equals(bkamEntity.getCodeDocument())) {
            CorrespondanceBancaire705 bf705 = cbRepository.findByBkamEntityId(bkamEntity.getId());
            if (bf705 != null) {
                result.put("cb1", bf705.getListCB1());
                result.put("cb2", bf705.getListCB2());
            }
        } else {
            result.put("axes", null);
        }
        return result;
    }

    public Map<String, Object> getAnomaliesByBkamEntityId(Long bkamEntityId) {
        BkamEntity bkamEntity = getBkamEntityById(bkamEntityId);
        Map<String, Object> result = initializeResultMap(bkamEntity);

        if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_PP)) {
            BanqueParticuliersProfessionnels pp = ppRepository.findByBkamEntityId(bkamEntity.getId());
            if (pp != null) {
                result.put("anomalies", anomalieRepository.findByClient_BanqueParticuliersProfessionnels_Id(pp.getId()));
            }
        } else if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_BF)) {
            Banquefinancement703 bf703 = bfRepository.findByBkamEntityId(bkamEntity.getId());
            if (bf703 != null) {
                result.put("anomalies", anomalieRepository.findByProduit_Banquefinancement703_Id(bf703.getId()));
            }
        } else if (CODE_DOCUMENT_700.equals(bkamEntity.getCodeDocument())) {
            E700DTO e700DTO = e700Repository.findByBkamEntityId(bkamEntity.getId());
            if (e700DTO != null) {
                result.put("anomalies", anomalieRepository.findByAxeE700_E700DTO_Id(e700DTO.getId()));
            }
        } else if (CODE_DOCUMENT_CB.equals(bkamEntity.getCodeDocument())) {
            CorrespondanceBancaire705 bf705 = cbRepository.findByBkamEntityId(bkamEntity.getId());
            if (bf705 != null) {
                result.put("anomaliesCb1", anomalieRepository.findByCb1_CorrespondanceBancaire705_Id(bf705.getId()));
                result.put("anomaliesCb2", anomalieRepository.findByCb2_CorrespondanceBancaire705_Id(bf705.getId()));
            }
        } else {
            result.put("axes", null);
        }
        return result;
    }

    private BkamEntity getBkamEntityById(Long bkamEntityId) {
        return bkamEntityRepository.findById(bkamEntityId)
                .orElseThrow(() -> new IllegalArgumentException("BKAM Entity not found with id: " + bkamEntityId));
    }

    private Map<String, Object> initializeResultMap(BkamEntity bkamEntity) {
        Map<String, Object> result = new HashMap<>();
        result.put("state", bkamEntity.getCodeDocument());
        result.put("etat", ListeEtats.listeEtats.get(bkamEntity.getCodeDocument()));
        return result;
    }

    private boolean isCodeDocumentInList(String codeDocument, String[] codeDocuments) {
        for (String code : codeDocuments) {
            if (code.equals(codeDocument)) {
                return true;
            }
        }
        return false;
    }
    public Map<String, Object> addAxe(Long bkamEntityId, Map<String, Object> axeData, String type) throws Exception {
        BkamEntity bkamEntity = getBkamEntityById(bkamEntityId);
        Map<String, Object> result = initializeResultMap(bkamEntity);

        if (CODE_DOCUMENT_700.equals(bkamEntity.getCodeDocument())) {
            E700DTO e700DTO = e700Repository.findByBkamEntityId(bkamEntity.getId());
            if (e700DTO != null) {
                AxeE700 newAxe = objectMapper.convertValue(axeData, AxeE700.class);
                if (!isAxeExistInE700(newAxe, e700DTO)) {
                    newAxe.setE700DTO(e700DTO);
                    axe700Repository.save(newAxe);
                    e700DTO.getAxes().add(newAxe);
                    e700Repository.save(e700DTO);
                    System.err.println(sortingService.sortAndSave(e700DTO.getAxes(),bkamEntity.getCodeDocument()));
                    result.put("success", "Axe added successfully");
                } else {
                    result.put("error", "Axe already exists for this parent");
                }
            }
         }else if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_PP)) {
            BanqueParticuliersProfessionnels pp = ppRepository.findByBkamEntityId(bkamEntity.getId());
            if (pp != null) {
                AxeWithNombreClientsCartes newAxe = objectMapper.convertValue(axeData, AxeWithNombreClientsCartes.class);
                if (!isAxeExistInPP(newAxe, pp)) {
                    newAxe.setBanqueParticuliersProfessionnels(pp);
                    clientRepo.save(newAxe);
                    pp.getClients().add(newAxe);
                    ppRepository.save(pp);
                    System.err.println(sortingService.sortAndSave(pp.getClients(),bkamEntity.getCodeDocument()));
                    result.put("success", "Axe added successfully");
                } else {
                    result.put("error", "Axe already exists for this parent");
                }
            }
        } else if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_BF)) {
            Banquefinancement703 bf703 = bfRepository.findByBkamEntityId(bkamEntity.getId());
            if (bf703 != null) {
                AxeWithoutNombreClientsCartes newAxe = mapAxeDataToAxeWithoutNombreClientsCartes(axeData);
                if (!isAxeExistInBF703(newAxe, bf703)) {
                    newAxe.setBanquefinancement703(bf703);
                    produitRepo.save(newAxe);
                    bf703.getProduits().add(newAxe);
                    bfRepository.save(bf703);
                    sortingService.sortAndSave(bf703.getProduits(),bkamEntity.getCodeDocument());

                    result.put("success", "Axe added successfully");
                } else {
                    result.put("error", "Axe already exists for this parent");
                }
            }
        } else if (CODE_DOCUMENT_CB.equals(bkamEntity.getCodeDocument())) {
            CorrespondanceBancaire705 cb705 = cbRepository.findByBkamEntityId(bkamEntity.getId());
            if (cb705 != null) {
                if ("CB1".equals(type)) {
                    CB1 newAxe = objectMapper.convertValue(axeData, CB1.class);
                    if (!isAxeExistInCB1(cb705)) {
                        newAxe.setCorrespondanceBancaire705(cb705);
                        cb1Repo.save(newAxe);
                        cb705.getListCB1().add(newAxe);
                        cbRepository.save(cb705);
                        result.put("success", "Axe added successfully");
                    } else {
                        result.put("error", "Axe already exists for this parent or limit exceeded");
                    }
                } else if ("CB2".equals(type)) {
                    CB2 newAxe = objectMapper.convertValue(axeData, CB2.class);
                    if (!isAxeExistInCB2(newAxe, cb705)) {
                        newAxe.setCorrespondanceBancaire705(cb705);
                        cb2Repo.save(newAxe);
                        cb705.getListCB2().add(newAxe);
                        cbRepository.save(cb705);
                        result.put("success", "Axe added successfully");
                    } else {
                        result.put("error", "Axe already exists for this parent");
                    }
                } else {
                    result.put("error", "Invalid type");
                }
            }
        } else {
            result.put("error", "Invalid document code");
        }
        return result;
    }
    @Transactional
    public Map<String, Object> deleteAxeBkamId(Long bkamEntityId, Long axetId, String type) {
        BkamEntity bkamEntity = getBkamEntityById(bkamEntityId);
        Map<String, Object> result = new HashMap<>();
        if (bkamEntity == null) {
            result.put("error", "BkamEntity not found");
            return result;
        }
        System.err.println(bkamEntity.getCodeDocument());

        if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_PP)) {
            if (clientRepo.existsById(axetId)) {
                anomalieRepository.deleteByClient(clientRepo.getById(axetId));
                clientRepo.deleteById(axetId);
                result.put("success", "Axe deleted successfully");
            } else {
                result.put("error", "Axe not found");
            }
        }
        else if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_BF)) {
            if (produitRepo.existsById(axetId)) {
                anomalieRepository.deleteByProduit(produitRepo.getById(axetId));
                produitRepo.deleteById(axetId);
                result.put("success", "Axe deleted successfully");
            } else {
                result.put("error", "Axe not found");
            }
        }
        else if (CODE_DOCUMENT_700.equals(bkamEntity.getCodeDocument())) {
            if (axe700Repository.existsById(axetId)) {
                anomalieRepository.deleteByAxeE700(axe700Repository.getById(axetId));
                axe700Repository.deleteById(axetId);
                result.put("success", "Axe deleted successfully");
            } else {
                result.put("error", "Axe not found");
            }
        }
        else if (CODE_DOCUMENT_CB.equals(bkamEntity.getCodeDocument())) {
            if ("CB1".equals(type)) {
                if (cb1Repo.existsById(axetId)) {
                    anomalieRepository.deleteByCb1(cb1Repo.getById(axetId));
                    cb1Repo.deleteById(axetId);

                    result.put("success", "Cb1 deleted successfully");
                } else {
                    result.put("error", "Cb1 not found");
                }
            } else if ("CB2".equals(type)) {
                if (cb2Repo.existsById(axetId)) {
                    anomalieRepository.deleteByCb2(cb2Repo.getById(axetId));
                    cb2Repo.deleteById(axetId);
                    result.put("success", "Cb2 deleted successfully");
                } else {
                    result.put("error", "Cb2 not found");
                }
            } else {
                result.put("error", "Invalid type");
            }
        }
        else {
            result.put("error", "Invalid code document");
        }

        return result;
    }
    private boolean isAxeExistInPP(AxeWithNombreClientsCartes newAxe, BanqueParticuliersProfessionnels pp) {
        return pp.getClients().stream().anyMatch(axe ->
                axe.getCodeAxes().equals(newAxe.getCodeAxes()));
    }
    private boolean isAxeExistInE700(AxeE700 newAxe, E700DTO e700DTO) {
        return e700DTO.getAxes().stream().anyMatch(axe ->
                axe.getLigneDocument().equals(newAxe.getLigneDocument()));
    }
    private boolean isAxeExistInBF703(AxeWithoutNombreClientsCartes newAxe, Banquefinancement703 bf703) {
        return bf703.getProduits().stream().anyMatch(axe ->
                axe.getCodeAxes().equals(newAxe.getCodeAxes()));
    }
    private boolean isAxeExistInCB1(CorrespondanceBancaire705 cb705) {
        return cb705.getListCB1().stream().anyMatch(axe -> cb705.getListCB1().size() >= 10);
    }
    private boolean isAxeExistInCB2(CB2 newAxe, CorrespondanceBancaire705 cb705) {
        return cb705.getListCB2().stream().anyMatch(axe ->
                axe.getCodeBIC().equals(newAxe.getCodeBIC()));
    }
    @Transactional
    public Map<String, Object> saveData(Long bkamEntityId,String type) throws Exception {
        BkamEntity bkamEntity = getBkamEntityById(bkamEntityId);
        Map<String, Object> result = initializeResultMap(bkamEntity);

        if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_PP)) {
            BanqueParticuliersProfessionnels pp = ppRepository.findByBkamEntityId(bkamEntity.getId());
            if (pp != null) {
                if (pp.getClients() != null) {
                    for (AxeWithNombreClientsCartes ligneX : pp.getClients()) {
                        anomalieRepository.deleteByClientAndTypeAnomalie(ligneX, TypeAnomalie.VIOLATION_DE_REGLE);
                    }
                }
                bkamEntityRepository.delete(bkamEntity);
                pp.setBkamEntity(null);
                conversion0102Service.convertToBkam(pp);
                result.put("success", "Les modifications ont été enregistrées avec succès");
            }
        }
        else if (isCodeDocumentInList(bkamEntity.getCodeDocument(), CODE_DOCUMENTS_BF)) {
            Banquefinancement703 bf703 = bfRepository.findByBkamEntityId(bkamEntity.getId());
            if (bf703 != null) {
                if (bf703.getProduits() != null) {
                    for (AxeWithoutNombreClientsCartes axe : bf703.getProduits()) {
                        anomalieRepository.deleteByProduitAndTypeAnomalie(axe, TypeAnomalie.VIOLATION_DE_REGLE);

                    }
                }
                bkamEntityRepository.delete(bkamEntity);
                convertion703Service.convertTobkamMapper(bf703);
                result.put("success", "Les modifications ont été enregistrées avec succès");
            }
        }
        else if (CODE_DOCUMENT_700.equals(bkamEntity.getCodeDocument())) {
            E700DTO e700DTO = e700Repository.findByBkamEntityId(bkamEntity.getId());
            if (e700DTO != null) {
                if (e700DTO.getAxes() != null) {
                    for (AxeE700 axe : e700DTO.getAxes()) {
                        anomalieRepository.deleteByAxeE700AndTypeAnomalie(axe, TypeAnomalie.VIOLATION_DE_REGLE);

                    }
                }
                bkamEntityRepository.delete(bkamEntity);
                convertion700Service.convertTobkam(e700DTO);
                result.put("success", "Les modifications ont été enregistrées avec succès");
            }
        }
        else if (CODE_DOCUMENT_CB.equals(bkamEntity.getCodeDocument())) {
            CorrespondanceBancaire705 bf705;
            try {
                bf705 = cbRepository.findByBkamEntityId(bkamEntity.getId());

            if (bf705 != null) {
                if ("CB1".equals(type)) {
                    if (bf705.getListCB1() != null) {
                        for (CB1 cb1 : bf705.getListCB1()) {
                            anomalieRepository.deleteByCb1AndTypeAnomalie(cb1, TypeAnomalie.VIOLATION_DE_REGLE);
                            System.err.println(cb1);

                        }
                    }
                } else if ("CB2".equals(type)) {
                    if (bf705.getListCB2() != null) {
                        for (CB2 cb2 : bf705.getListCB2()) {
                            anomalieRepository.deleteByCb2AndTypeAnomalie(cb2, TypeAnomalie.VIOLATION_DE_REGLE);
                            System.err.println(cb2);
                        }
                    }
                } else {
                    result.put("error", "Invalid type");
                }
                ligneCb1Repo.deleteAll(bkamEntity.getLigneFolioCB1s());
                ligneCb2Repo.deleteAll(bkamEntity.getLigneFolioCB2s());
                bkamEntityRepository.delete(bkamEntity);
                conversion705Service.convertTobkam705mapper(bf705);
                result.put("success", "Les modifications ont été enregistrées avec succès");
            }
            }catch (Exception e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return result;
    }
    public AxeWithoutNombreClientsCartes mapAxeDataToAxeWithoutNombreClientsCartes(Map<String, Object> axeData) {
        AxeWithoutNombreClientsCartes newAxe = new AxeWithoutNombreClientsCartes();

        if (axeData.get("axes") != null) {
            newAxe.setAxes((String) axeData.get("axes"));
        }
        if (axeData.get("codeAxes") != null) {
            newAxe.setCodeAxes((String) axeData.get("codeAxes"));
        }
        if (axeData.get("libelleAxes") != null) {
            newAxe.setLibelleAxes((String) axeData.get("libelleAxes"));
        }
        if (axeData.get("nombreClients") != null) {
            newAxe.setNombreClients((Integer) axeData.get("nombreClients"));
        }
        if (axeData.get("encoursDepots") != null) {
            newAxe.setEncoursDepots((Double) axeData.get("encoursDepots"));
        }
        if (axeData.get("fluxDebiteurs2020") != null) {
            newAxe.setFluxDebiteurs2020((Double) axeData.get("fluxDebiteurs2020"));
        }
        if (axeData.get("fluxCrediteurs2020") != null) {
            newAxe.setFluxCrediteurs2020((Double) axeData.get("fluxCrediteurs2020"));
        }
        if (axeData.get("risqueInherent") != null) {
            newAxe.setRisqueInherent((String) axeData.get("risqueInherent"));
        }
        if (axeData.get("commentaires") != null) {
            newAxe.setCommentaires((String) axeData.get("commentaires"));
        }


        // Map additional fields as needed

        return newAxe;
    }
}
