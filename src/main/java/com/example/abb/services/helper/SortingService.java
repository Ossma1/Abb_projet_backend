package com.example.abb.services.helper;

import com.example.abb.dto.Tables705.CB1;
import com.example.abb.dto.Tables705.CB2;
import com.example.abb.dto.axesEntities.AxeWithNombreClientsCartes;
import com.example.abb.dto.axesEntities.AxeWithoutNombreClientsCartes;
import com.example.abb.models.Ligne700;
import com.example.abb.models.LigneX;
import com.example.abb.models.LigneX0102;
import com.example.abb.repositories.CBRepository;
import com.example.abb.repositories.axesRepo.WithNombreClientRepo;
import com.example.abb.repositories.axesRepo.WithoutNombreClientRepo;
import com.example.abb.repositories.tables705Repo.Cb1Repo;
import com.example.abb.repositories.tables705Repo.Cb2Repo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class SortingService {

    private final ObjectMapper objectMapper;
    @Autowired
    private Cb2Repo cb2Repo;

    @Autowired
    public SortingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Autowired
    CBRepository cbRepository;

    @Autowired
    WithNombreClientRepo clientRepository;
    @Autowired
    WithoutNombreClientRepo produitRepository;
    // Méthode pour charger dynamiquement la classe de mapping
    public Map<String, String> loadCodeMapping(String state) throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException {
        String className = "com.example.abb.utility.CodeLigne" + state;
        Class<?> clazz = Class.forName(className);
        return (Map<String, String>) clazz.getField("codeMapping").get(null);
    }

    public <T> List<T> sortAndSave(List<T> list, String state) throws Exception {
        Map<String, String> codeMapping = loadCodeMapping(state);
        // Comparator for sorting based on codeMapping keys
        Comparator<T> comparator = Comparator.comparingInt(o -> {
            String codeAxes;
            if (o instanceof AxeWithNombreClientsCartes) {
                codeAxes = ((AxeWithNombreClientsCartes) o).getCodeAxes();
            } else if (o instanceof AxeWithoutNombreClientsCartes) {
                codeAxes = ((AxeWithoutNombreClientsCartes) o).getCodeAxes();
            } else {
                return Integer.MAX_VALUE; // Default value if the type is unknown
            }
            // Sort by the order of keys in the codeMapping
            return new ArrayList<>(codeMapping.keySet()).indexOf(codeAxes);
        });

        list.sort(comparator);
        return list; // Return the sorted list
    }
    public <T> List<T> sortByIdentifiableLigne(List<T> list, String state) throws Exception {
        Map<String, String> codeMapping = loadCodeMapping(state);
        // Comparator for sorting based on codeMapping keys
        Comparator<T> comparator = Comparator.comparingInt(o -> {
            String IdentifiableLigne;
            if (o instanceof LigneX) {
                IdentifiableLigne = ((LigneX) o).getIdentifiantLigne();
                return new ArrayList<>(codeMapping.values()).indexOf(IdentifiableLigne);
            } else if (o instanceof LigneX0102) {
                IdentifiableLigne = ((LigneX0102) o).getIdentifiantLigne();
                return new ArrayList<>(codeMapping.values()).indexOf(IdentifiableLigne);
            } else if (o instanceof Ligne700) {
                IdentifiableLigne = ((Ligne700) o).getLigneDocument();
                return new ArrayList<>(codeMapping.keySet()).indexOf(IdentifiableLigne);
            }  else {
                return Integer.MAX_VALUE; // Default value if the type is unknown
            }
        });

        list.sort(comparator);
        return list; // Return the sorted list
    }
}