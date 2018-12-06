package com.rbkmoney.midgard.data;

import org.jooq.generated.tables.pojos.ClearingTransaction;
import org.jooq.generated.tables.pojos.Merchant;

import java.util.List;

public class ClearingData {

    private List<ClearingTransaction> transactions;

    private List<Merchant> merchants;

    public ClearingData(List<ClearingTransaction> transactions, List<Merchant> merchants) {
        this.transactions = transactions;
        this.merchants = merchants;
    }

    public List<ClearingTransaction> getTransactions() {
        return transactions;
    }

    public List<Merchant> getMerchants() {
        return merchants;
    }
}
