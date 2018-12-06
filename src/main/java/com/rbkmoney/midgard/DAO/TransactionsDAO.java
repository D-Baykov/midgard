package com.rbkmoney.midgard.DAO;

import com.rbkmoney.midgard.Bank;
import com.rbkmoney.midgard.DAO.common.AbstractGenericDao;
import com.rbkmoney.midgard.DAO.common.RecordRowMapper;
import org.jooq.Query;
import org.jooq.generated.enums.TransactionClearingState;
import org.jooq.generated.tables.pojos.ClearingTransactionInfo;
import org.jooq.generated.tables.pojos.FailureTransaction;
import org.jooq.generated.tables.pojos.ClearingTransaction;
import org.jooq.generated.tables.records.ClearingTransactionInfoRecord;
import org.jooq.generated.tables.records.ClearingTransactionRecord;
import org.jooq.generated.tables.records.FailureTransactionRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.jooq.generated.enums.TransactionClearingState.*;
import static org.jooq.generated.tables.ClearingTransaction.CLEARING_TRANSACTION;
import static org.jooq.generated.tables.ClearingTransactionInfo.CLEARING_TRANSACTION_INFO;
import static org.jooq.generated.tables.FailureTransaction.*;

/**
 * DAO for work with clearing_transaction table
 *
 * @author d.baykov
 *         29.11.2018
 */
public class TransactionsDAO extends AbstractGenericDao<ClearingTransaction> {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(TransactionsDAO.class);
    /** A list of merchant row */
    private final RowMapper<ClearingTransaction> transactionRowMapper;

    public TransactionsDAO(DataSource dataSource) {
        super(dataSource);
        transactionRowMapper = new RecordRowMapper<>(CLEARING_TRANSACTION, ClearingTransaction.class);
    }

    @Override
    public Long save(ClearingTransaction transaction) {
        log.debug("Adding new merchant: {}", transaction);
        ClearingTransactionRecord record = getDslContext().newRecord(CLEARING_TRANSACTION, transaction);
        Query query = getDslContext().insertInto(CLEARING_TRANSACTION).set(record);
        int addedRows = execute(query);
        log.debug("New transaction with id {} was added", transaction.getMerchantId());
        return Long.valueOf(addedRows);
    }

    @Override
    public ClearingTransaction get(String transactionId) {
        log.debug("Getting a transaction with id {}", transactionId);
        Query query = getDslContext().selectFrom(CLEARING_TRANSACTION)
                .where(CLEARING_TRANSACTION.TRANSACTION_ID.eq(transactionId));
        ClearingTransaction clearingTransaction = fetchOne(query, transactionRowMapper);
        log.debug("Transaction with id {} {}", transactionId, clearingTransaction == null ? "not found" : "found");
        return clearingTransaction;
    }

    public List<ClearingTransaction> getClearingTransactionsByBank(Bank bank,
                                                                   LocalDateTime dateTo,
                                                                   List<TransactionClearingState> states) {
        Query query = getDslContext().selectFrom(CLEARING_TRANSACTION)
                .where((CLEARING_TRANSACTION.TRANSACTION_DATE.lessThan(dateTo))
                        .and(CLEARING_TRANSACTION.BANK_NAME.eq(bank.name()))
                        .and(CLEARING_TRANSACTION.TRANSACTION_CLEARING_STATE.in(states)));
        return fetch(query, transactionRowMapper);
    }

    public List<ClearingTransaction> getClearingTransactions(LocalDateTime dateTo,
                                                             List<TransactionClearingState> states) {
        Query query = getDslContext().selectFrom(CLEARING_TRANSACTION)
                .where((CLEARING_TRANSACTION.TRANSACTION_DATE.lessThan(dateTo))
                        .and(CLEARING_TRANSACTION.TRANSACTION_CLEARING_STATE.in(states)));
        return fetch(query, transactionRowMapper);
    }


    public void saveFailureTransaction(FailureTransaction failureTransaction) {
        FailureTransactionRecord record = getDslContext().newRecord(FAILURE_TRANSACTION, failureTransaction);
        Query query = getDslContext().insertInto(FAILURE_TRANSACTION).set(record);
        execute(query);
    }

    public void saveClearingTransactionInfo(ClearingTransactionInfo transactionInfo) {
        ClearingTransactionInfoRecord record = getDslContext().newRecord(CLEARING_TRANSACTION_INFO, transactionInfo);
        Query query = getDslContext().insertInto(CLEARING_TRANSACTION_INFO).set(record);
        execute(query);
    }


    //TODO: так делать нельзя, потому что могли быть добавлены новые транзакции и в дальгейшем в выгрузку они не попадут
//    public void setReadyActualClearingTransactions() {
//        Query query = getDslContext().update(CLEARING_TRANSACTION)
//                .set(CLEARING_TRANSACTION.TRANSACTION_CLEARING_STATE, READY)
//                .where((CLEARING_TRANSACTION.TRANSACTION_DATE.lessOrEqual(LocalDateTime.now()))
//                        .and(CLEARING_TRANSACTION.TRANSACTION_CLEARING_STATE.in(ACTIVE, FAILED)));
//        execute(query);
//    }



    public void setClearingTransactionsState(List<ClearingTransaction> transactions, TransactionClearingState state) {
        log.debug("Set clearing transactions to state {}", state);
        for (ClearingTransaction transaction : transactions) {
            setClearingTransactionState(transaction.getTransactionId(), state);
        }
        log.debug("The states of clearing transaction successful chenged to state {}", state);
    }

    public void setClearingTransactionState(String transactionId, TransactionClearingState state) {
        log.trace("Set transaction {} to state {}", transactionId, state);
        Query query = getDslContext().update(CLEARING_TRANSACTION)
                .set(CLEARING_TRANSACTION.TRANSACTION_CLEARING_STATE, state)
                .where(CLEARING_TRANSACTION.TRANSACTION_ID.eq(transactionId));
        execute(query);
        log.trace("The state of transacion {} successful chenged to state {}", transactionId, state);
    }

    public void setClearingTransactionMetaInfo(String transactionId,
                                               Long clearingId,
                                               TransactionClearingState state) {
        log.trace("Set transaction {} to state {}", transactionId, state);
        Query query = getDslContext().update(CLEARING_TRANSACTION)
                .set(CLEARING_TRANSACTION.TRANSACTION_CLEARING_STATE, state)
                .set(CLEARING_TRANSACTION.CLEARING_ID, clearingId)
                .set(CLEARING_TRANSACTION.LAST_ACT_TIME, LocalDateTime.now())
                .where(CLEARING_TRANSACTION.TRANSACTION_ID.eq(transactionId));
        execute(query);
        log.trace("The state of transacion {} successful chenged to state {}", transactionId, state);
    }


}
