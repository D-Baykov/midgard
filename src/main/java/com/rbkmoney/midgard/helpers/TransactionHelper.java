package com.rbkmoney.midgard.helpers;

import com.rbkmoney.midgard.data.enums.Bank;
import com.rbkmoney.midgard.DAO.TransactionsDAO;
import com.rbkmoney.midgard.utils.MidgardUtils;
import org.jooq.generated.enums.CtState;
import org.jooq.generated.tables.pojos.ClearingTransactionInfo;
import org.jooq.generated.tables.pojos.FailureTransaction;
import org.jooq.generated.tables.pojos.ClearingTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.jooq.generated.enums.TransactionClearingState.ACTIVE;
import static org.jooq.generated.enums.TransactionClearingState.FAILED;

/**
 * Helper for work with clearing transactions
 *
 * @author d.baykov
 *         29.11.2018
 */
public class TransactionHelper {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(TransactionHelper.class);
    /** This DAO for work with clearing transactions */
    private final TransactionsDAO dao;

    public TransactionHelper(DataSource dataSource) {
        dao = new TransactionsDAO(dataSource);
    }

    public void saveTransactions(List<ClearingTransaction> transactions) {
        transactions.forEach(this::saveTransaction);
    }

    public void saveTransaction(ClearingTransaction transaction) {
        log.debug("Saving a transaction {}...", transaction);
        ClearingTransaction tmpTransaction = dao.get(transaction.getTransactionId());
        if (tmpTransaction != null) {
            dao.save(transaction);
        } else {
            log.debug("The transaction {} was found in the database", tmpTransaction.getTransactionId());
            if (!MidgardUtils.compareTransactions(transaction, tmpTransaction)) {
                log.warn("Duplicate transactions! Source transaction: {}, arrived transaction: {}",
                        transaction, tmpTransaction);
                //TODO: до конца непонятно как корректно сравнивать транзакции
            }
        }
    }

    public ClearingTransaction getTransaction(String transactionId) {
        return dao.get(transactionId);
    }

    public List<ClearingTransaction> getActualClearingTransactions(Bank bank) {
        return dao.getClearingTransactionsByBank(bank, LocalDateTime.now(), Arrays.asList(ACTIVE, FAILED));
    }

    public List<ClearingTransaction> getAllActualClearingTransactions() {
        return dao.getClearingTransactions(LocalDateTime.now(), Arrays.asList(ACTIVE, FAILED));
    }

    public void saveAllFailureTransactionByOneReason(List<ClearingTransaction> transactions,
                                                     Long clearingId,
                                                     String reason) {
        for (ClearingTransaction transaction : transactions) {
            saveFailureTransaction(transaction.getTransactionId(), clearingId, reason);
        }
    }

    public void saveFailureTransaction(String transactionId, Long clearingId, String reason) {
        FailureTransaction failureTransaction = new FailureTransaction();
        failureTransaction.setTransactionId(transactionId);
        failureTransaction.setClearingId(clearingId);
        failureTransaction.setReason(reason);
        dao.saveFailureTransaction(failureTransaction);
    }

    public void saveClearingTransactionsInfo(List<ClearingTransaction> activeTrx,
                                             List<ClearingTransaction> failedTrx,
                                             Long clearingId) {
        saveSentClearingTransactionsInfo(activeTrx, clearingId);
        saveRefusedClearingTransactionsInfo(failedTrx, clearingId);
    }

    private void saveSentClearingTransactionsInfo(List<ClearingTransaction> transactions, Long clearingId) {
        for (ClearingTransaction transaction : transactions) {
            saveSentClearingTranInfo(clearingId, transaction.getTransactionId());
        }
    }

    private void saveRefusedClearingTransactionsInfo(List<ClearingTransaction> transactions, Long clearingId) {
        for (ClearingTransaction transaction : transactions) {
            saveRefusedClearingTranInfo(clearingId, transaction.getTransactionId());
        }
    }

    public void saveSentClearingTranInfo(Long clearingId, String transactionId) {
        saveClearingTransactionInfo(clearingId, transactionId, CtState.SENT);
    }

    public void saveRefusedClearingTranInfo(Long clearingId, String transactionId) {
        saveClearingTransactionInfo(clearingId, transactionId, CtState.REFUSED);
    }

    private void saveClearingTransactionInfo(Long clearingId, String transactionId, CtState ctState) {
        ClearingTransactionInfo transactionInfo = new ClearingTransactionInfo();
        transactionInfo.setClearingId(clearingId);
        transactionInfo.setTransactionId(transactionId);
        transactionInfo.setCtState(ctState);
        dao.saveClearingTransactionInfo(transactionInfo);
    }

    public void updateClearingTransactionsState(List<ClearingTransaction> activeTrx,
                                                List<ClearingTransaction> failedTrx,
                                                Long clearingId) {
        updateClearingTransactionsToActiveState(activeTrx, clearingId);
        updateClearingTransactionsToFailedState(failedTrx, clearingId);
    }

    private void updateClearingTransactionsToActiveState(List<ClearingTransaction> transactions, Long clearingId) {
        for (ClearingTransaction transaction : transactions) {
            dao.setClearingTransactionMetaInfo(transaction.getTransactionId(), clearingId, ACTIVE);
        }
    }

    private void updateClearingTransactionsToFailedState(List<ClearingTransaction> transactions, Long clearingId) {
        for (ClearingTransaction transaction : transactions) {
            dao.setClearingTransactionMetaInfo(transaction.getTransactionId(), clearingId, FAILED);
        }
    }

}
