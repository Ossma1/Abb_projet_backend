package com.example.abb.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CheckUtility {

    public long roundToNearestlong(double value) {
        return (long) Math.round(value);
    }
}
