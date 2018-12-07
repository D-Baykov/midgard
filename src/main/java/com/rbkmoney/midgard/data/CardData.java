package com.rbkmoney.midgard.data;

/** Class containing card data */
//TODO: переедет в адаптер
public class CardData {

    private String pan;

    private String expDate;

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
}
