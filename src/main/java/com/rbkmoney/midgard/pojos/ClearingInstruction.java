package com.rbkmoney.midgard.pojos;

import com.rbkmoney.midgard.data.enums.Bank;

import java.util.Date;

public class ClearingInstruction {

    private Bank bank;

    private Date dateFrom;

    private Date dateTo;

    private String command;

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "ClearingInstruction{" +
                "bank=" + bank +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                ", command='" + command + '\'' +
                '}';
    }
}
