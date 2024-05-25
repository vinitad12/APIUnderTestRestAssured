package com.example.utils;

import com.jayway.jsonpath.JsonPath;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class CommonUtils {

    private CommonUtils(){

    }

    public static int generateRandomInt(int min, int max) {

        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static String insertRandomValue(String jsonString, String jsonPath, int min, int max) {
        int randomValue = ThreadLocalRandom.current().nextInt(min, max + 1);
        return JsonPath.parse(jsonString).set(jsonPath, randomValue).jsonString();
    }


    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return dateFormat.format(new Date());
    }
}
