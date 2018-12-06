package com.rbkmoney.midgard.upload.commands;

import com.rbkmoney.midgard.data.ClearingData;
import com.rbkmoney.midgard.data.MtsXmlHeader;
import com.rbkmoney.midgard.data.enums.Bank;
import com.rbkmoney.midgard.helpers.ClearingInfoHelper;
import com.rbkmoney.midgard.helpers.MerchantHelper;
import com.rbkmoney.midgard.helpers.TransactionHelper;
import com.rbkmoney.midgard.pojos.CardData;
import com.rbkmoney.midgard.pojos.ClearingInstruction;
import com.rbkmoney.midgard.utils.MidgardUtils;
import com.rbkmoney.midgard.utils.MtsXmlUtil;
import com.rbkmoney.midgard.utils.VelocityUtil;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;
import org.jooq.generated.tables.pojos.ClearingTransaction;
import org.jooq.generated.tables.pojos.Merchant;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
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
        transactionHelper.saveClearingTransactionsInfo(transactions, failedTransactions, clearingId);

        // обновить статусы успешных/неуспешных транзакций в таблице clearing_transaction
        transactionHelper.updateClearingTransactionsState(transactions, failedTransactions, clearingId);

        // обновить статус клиринга
        clearingInfoHelper.setExecutedClearingEvent(clearingId);

        // сформировать объект для передачи в адаптер формирования XML клиринга
        ClearingData clearingData = new ClearingData(transactions, merchants);

        // отправить объект в адаптер
        sendData(clearingData);

        createXml(clearingData);
    }

    private void sendData(ClearingData clearingData) {
        //TODO: отправка данных в адаптер формирования XML
    }

    //
    private void createXml(ClearingData clearingData) {
        String fileOriginator = "fileOriginator";
        int fileNumber = 1;
        MtsXmlHeader header = new MtsXmlHeader(fileOriginator, fileNumber);
        VelocityContext headerContext = new VelocityContext();
        headerContext.put("header", header);
        String clearingXmlHeader = VelocityUtil.create("vm/header.vm", headerContext);


        DateTool dateTool = new DateTool();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String procDate = dateFormat.format(new Date());
        String msgNr = UUID.randomUUID().toString();
        List<ClearingTransaction> transactions = clearingData.getTransactions();
        List<Merchant> merchants = clearingData.getMerchants();

        List<VelocityContext> transactionsContext = new ArrayList<>();
        for (ClearingTransaction transaction : transactions) {
            VelocityContext context = new VelocityContext();
            CardData cardData = new CardData();
            context.put("dateTool", dateTool);
            context.put("procDate", procDate);
            context.put("msgNr", msgNr);
            context.put("cardData", cardData);
            context.put("transaction", transaction);
            Merchant merchant = merchants.stream()
                    .filter(mrch -> mrch.getMerchantId().equals(transaction.getMerchantId()))
                    .findFirst().orElse(new Merchant());
            context.put("merchant", merchant);
            transactionsContext.add(context);
        }
        List<String> trxXmlBlocks = VelocityUtil.create("vm/transaction.vm", transactionsContext);

        try {
            MidgardUtils.saveToFile("clearing.xml", MtsXmlUtil.createXML(clearingXmlHeader, trxXmlBlocks));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
