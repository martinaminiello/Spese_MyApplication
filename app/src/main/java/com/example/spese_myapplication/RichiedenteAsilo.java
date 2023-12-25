package com.example.spese_myapplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RichiedenteAsilo {
    private String idRichiedenteAsilo;
    private Double budget;

    int currentDate = Calendar.getInstance().get(Calendar.MONTH);
    private Integer creationDate;



    public RichiedenteAsilo(String idRichiedenteAsilo){
        this.idRichiedenteAsilo=idRichiedenteAsilo;


    }
    public String getIdRichiedenteAsilo() {
        return idRichiedenteAsilo;
    }

}
