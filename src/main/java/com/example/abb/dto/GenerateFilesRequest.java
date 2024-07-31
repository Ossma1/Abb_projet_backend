package com.example.abb.dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GenerateFilesRequest {
    private List<String> bkamIds;
//    private String exerciceDate;
}