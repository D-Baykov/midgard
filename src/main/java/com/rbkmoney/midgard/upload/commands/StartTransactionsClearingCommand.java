package com.rbkmoney.midgard.upload.commands;

import com.rbkmoney.midgard.Bank;
import com.rbkmoney.midgard.helpers.ClearingInfoHelper;
import com.rbkmoney.midgard.helpers.MerchantHelper;
import com.rbkmoney.midgard.helpers.TransactionHelper;
import com.rbkmoney.midgard.pojos.ClearingInstruction;
import org.jooq.generated.tables.pojos.ClearingTransaction;
import org.jooq.generated.tables.pojos.Merchant;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StartTransactionsClearingCommand implements Command {

    private final ClearingInstruction instruction;

    private TransactionHelper transactionHelper;

    private MerchantHelper merchantHelper;

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

        // записать список сбойных транзакций в таблицу failure_transaction
        String reason = "Для данной транзакции не был найден мерчант";
        transactionHelper.saveAllFailureTransactionByOneReason(failedTransactions, clearingId, reason);

        // записать историю выгрузки транзакций в таблицу clearing_transaction_info
        transactionHelper.saveSentClearingTransactionsInfo(transactions, clearingId);
        transactionHelper.saveRefusedClearingTransactionsInfo(failedTransactions, clearingId);

        //TODO: обновить статусы успешных/неуспешных транзакций в таблице clearing_transaction


        //TODO: обновить статус клиринга


        //TODO: сформировать объект для передачи в адаптер формирования XML клиринга


        //TODO: отправить объект в адаптер


    }


}
