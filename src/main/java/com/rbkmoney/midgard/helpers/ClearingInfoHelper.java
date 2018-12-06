package com.rbkmoney.midgard.helpers;

import com.rbkmoney.midgard.Bank;
import com.rbkmoney.midgard.DAO.ClearingInfoDAO;
import org.jooq.generated.tables.pojos.ClearingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Arrays;

import static org.jooq.generated.enums.ClearingState.*;

public class ClearingInfoHelper {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(ClearingInfoHelper.class);
    /** DAO for work with clearing events */
    private final ClearingInfoDAO dao;

    public ClearingInfoHelper(DataSource dataSource) {
        dao = new ClearingInfoDAO(dataSource);
    }

    public Long createNewClearingEvent(Bank bank) {
        ClearingEvent clearingEvent = new ClearingEvent();
        clearingEvent.setBankName(bank.name());
        clearingEvent.setState(STARTED);
        return dao.save(clearingEvent);
    }

    public Long getLastClearingId(Bank bank) {
        return dao.getLastClearingId(bank);
    }

    public Long getLastSuccessfulClearingId(Bank bank) {
        return dao.getLastClearingId(bank, Arrays.asList(SUCCESSFULLY));
    }

    public Long getLastFailedfulClearingId(Bank bank) {
        return dao.getLastClearingId(bank, Arrays.asList(FAILED));
    }

    public Long getStartedClearingId(Bank bank) {
        return dao.getLastClearingId(bank, Arrays.asList(STARTED));
    }

    public ClearingEvent getClearingEvent(Long eventId) {
        return dao.get(String.valueOf(eventId));
    }

}
