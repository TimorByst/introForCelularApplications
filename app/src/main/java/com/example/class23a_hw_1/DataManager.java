package com.example.class23a_hw_1;

import java.util.ArrayList;

public class DataManager {

    public static ArrayList<Record> getRecords(){
        ArrayList<Record> records = new ArrayList<>();

        records.add(new Record().setName("Moscow")
                                .setLatitude(55)
                                .setLongitude(37)
                                .setScore(666));

        records.add(new Record().setName("Sydney")
                                .setLatitude(-33)
                                .setLongitude(151)
                                .setScore(70));

        records.add(new Record().setName("Jerusalem")
                                .setLatitude(31.7777104)
                                .setLongitude(35.232792)
                                .setScore(180));

        records.add(new Record().setName("Washington")
                                .setLatitude(38.900497)
                                .setLongitude(	-77.007507)
                                .setScore(800));

        records.add(new Record().setName("London")
                                .setLatitude(51)
                                .setLongitude(0)
                                .setScore(20));

        records.add(new Record().setName("Berlin")
                                .setLatitude(52)
                                .setLongitude(13)
                                .setScore(250));

        records.add(new Record().setName("Tokyo")
                                .setLatitude(35)
                                .setLongitude(139)
                                .setScore(999));

        records.add(new Record().setName("Brasilia")
                                .setLatitude(-15)
                                .setLongitude(-47)
                                .setScore(405));

        records.add(new Record().setName("Reykjavík")
                                .setLatitude(64)
                                .setLongitude(-21)
                                .setScore(-500));

        records.add(new Record().setName("Addis Ababa")
                                .setLatitude(9)
                                .setLongitude(38)
                                .setScore(755));

     return records;
    }
}
