package com.rbkmoney.midgard.data.enums;

/** Список банков */
public enum Bank {

    MTS("MTS", "МТС банк"),
    VTB("VTB", "ВТБ"),
    SBERBANK("SBERBANK", "Сбербанк");

    /** Наимаенование банка */
    private String bankName;
    /** Описание */
    private String descriptopn;

    Bank(String bankName, String descriptopn) {
        this.bankName = bankName;
        this.descriptopn = descriptopn;
    }

    public String getBankName() {
        return bankName;
    }

    public String getDescriptopn() {
        return descriptopn;
    }

    /**
     * Получение типа банка по имени
     *
     * @param bankName наименование банка
     * @return тип
     */
    public static Bank typeOf(String bankName) {
        for (Bank value : values()) {
            if (value.bankName.equals(bankName)) {
                return value;
            }
        }
        return null;
    }

}
