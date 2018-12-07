package com.rbkmoney.midgard.upload.commands;

import com.rbkmoney.midgard.data.ClearingData;
import com.rbkmoney.midgard.data.enums.Bank;
import com.rbkmoney.midgard.helpers.ClearingInfoHelper;
import com.rbkmoney.midgard.helpers.MerchantHelper;
import com.rbkmoney.midgard.helpers.TransactionHelper;
import com.rbkmoney.midgard.data.ClearingInstruction;
import org.jooq.generated.tables.pojos.ClearingTransaction;
import org.jooq.generated.tables.pojos.Merchant;

import java.util.*;
import java.util.stream.Collectors;

/** Команда начала выполнения клиринга */
public class StartTransactionsClearingCommand implements Command {

    /** Инструкция к выполнению */
    private final ClearingInstruction instruction;
    /** Вспомогательный класс для работы с транзакциями */
    private TransactionHelper transactionHelper;
    /** Вспомогательный класс для работы с мерчантами */
    private MerchantHelper merchantHelper;
    /** Вспомогательный класс для работы с метаинформацией */
    private ClearingInfoHelper clearingInfoHelper;

    public StartTransactionsClearingCommand(ClearingInstruction instruction,
                                            TransactionHelper transactionHelper,
                                            MerchantHelper merchantHelper,
                                            ClearingInfoHelper clearingInfoHelper) {
        this.instruction = instruction;
        this.transactionHelper = transactionHelper;
        this.merchantHelper = merchantHelper;
        this.clearingInfoHelper = clearingInfoHelper;
    }

    @Override
    public void execute() {
        Bank bank = instruction.getBank();
        Long clearingId = clearingInfoHelper.createNewClearingEvent(bank);
        List<ClearingTransaction> transactions = transactionHelper.getActualClearingTransactions(bank);

        List<String> currMerchants = transactions.stream()
                .map(t -> t.getMerchantId())
                .distinct()
                .collect(Collectors.toList());

        //TODO: Вообще по-хорошему необходимо перенести данную часть на сторону БД, так как
        //TODO: там эта операция будет эффективнее. Возможно из БД стоит получасть сразу только интересующие транзакции
        List<ClearingTransaction> failedTransactions = new ArrayList<>();
        List<Merchant> merchants = new ArrayList<>();
        for (String currMerchant : currMerchants) {
            Merchant merchant = merchantHelper.getMerchant(currMerchant);
            if (merchant == null) {
                List<ClearingTransaction> failTrx = transactions.stream()
                        .filter(t -> t.getMerchantId().equals(currMerchant))
                        .collect(Collectors.toList());
                failedTransactions.addAll(failTrx);
                transactions = transactions.stream()
                        .filter(t -> !t.getMerchantId().equals(currMerchant))
                        .collect(Collectors.toList());
            } else {
                merchants.add(merchant);
            }
        }

        // сформировать объект для передачи в адаптер формирования XML клиринга
        ClearingData clearingData = prepareClearingTransactions(transactions, failedTransactions, merchants, clearingId);

        // отправить объект в адаптер
        sendData(clearingData);
    }

    /**
     * Подготовка клиринговых данных
     *
     * @param transactions список успешных клиринговых транзакций
     * @param failedTransactions список неуспешных клиринговых транзакций
     * @param merchants список мерчантов
     * @param clearingId ID клирингового события
     * @return объект клиринговых данных
     */
    private ClearingData prepareClearingTransactions(List<ClearingTransaction> transactions,
                                                     List<ClearingTransaction> failedTransactions,
                                                     List<Merchant> merchants,
                                                     Long clearingId) {
        // записать список сбойных транзакций в таблицу failure_transaction
        String reason = "Для данной транзакции не был найден мерчант";
        transactionHelper.saveAllFailureTransactionByOneReason(failedTransactions, clearingId, reason);

        // записать историю выгрузки транзакций в таблицу clearing_transaction_info
        transactionHelper.saveClearingTransactionsInfo(transactions, failedTransactions, clearingId);

        // обновить статусы успешных/неуспешных транзакций в таблице clearing_transaction
        transactionHelper.updateClearingTransactionsState(transactions, failedTransactions, clearingId);

        // обновить статус клиринга
        clearingInfoHelper.setExecutedClearingEvent(clearingId);

        return new ClearingData(transactions, merchants);
    }

    /**
     * Передача клиринговых данных в адаптер
     *
     * @param clearingData клиринговые данные
     */
    private void sendData(ClearingData clearingData) {
        //TODO: отправка данных в адаптер формирования XML

    }

}
