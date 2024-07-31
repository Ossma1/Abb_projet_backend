package com.example.abb.dto.AxesE700;


import lombok.Data;

import javax.persistence.Embeddable;

@Data
@Embeddable
public class ColumnValue {
    private int columnNumber;
    private long value;
    public ColumnValue() {}

    public ColumnValue(int columnNumber, long value) {
        this.columnNumber = columnNumber;
        this.value = value;
    }
}