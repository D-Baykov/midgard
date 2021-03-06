package com.rbkmoney.midgard.helpers;

import com.rbkmoney.midgard.data.enums.Bank;
import com.rbkmoney.midgard.DAO.ClearingInfoDAO;
import org.jooq.generated.enums.ClearingState;
import org.jooq.generated.tables.pojos.ClearingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Arrays;

import static org.jooq.generated.enums.ClearingState.*;

/** Вспомогательный класс для работы с дополнительной информацией в рамках задачи клиринга */
public class ClearingInfoHelper {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(ClearingInfoHelper.class);
    /** Объект для работы с данными в БД */
    private final ClearingInfoDAO dao;

    public ClearingInfoHelper(DataSource dataSource) {
        dao = new ClearingInfoDAO(dataSource);
    }

    /**
     * Создание нового события клиринга
     *
     * @param bank банк, для которого будет создано новое событие клиринга
     * @return ID созданного события
     */
    //TODO: рассмотреть вариант, когда для банка присутствует незавершенное клиринговое событие.
    public Long createNewClearingEvent(Bank bank) {
        log.trace("Creating new clearing event for {}", bank);
        ClearingEvent clearingEvent = new ClearingEvent();
        clearingEvent.setBankName(bank.name());
        clearingEvent.setState(STARTED);
        return dao.save(clearingEvent);
    }

    /**
     * Получение последнего ID клиринга для банка
     *
     * @param bank банк, для которого нухно получить ID события
     * @return ID события
     */
    public Long getLastClearingId(Bank bank) {
        return dao.getLastClearingId(bank, Arrays.asList(STARTED, SUCCESSFULLY, FAILED));
    }

    /**
     * Полуение ID последнего успешного клиринга
     *
     * @param bank банк, для которого нухно получить ID события
     * @return ID события
     */
    public Long getLastSuccessfulClearingId(Bank bank) {
        return dao.getLastClearingId(bank, Arrays.asList(SUCCESSFULLY));
    }

    /**
     * Полуение ID последнего неуспешного клиринга
     *
     * @param bank банк, для которого нухно получить ID события
     * @return ID события
     */
    public Long getLastFailedfulClearingId(Bank bank) {
        return dao.getLastClearingId(bank, Arrays.asList(FAILED));
    }

    /**
     * Полуение ID последнего активного клиринга
     *
     * @param bank банк, для которого нухно получить ID события
     * @return ID события
     */
    public Long getStartedClearingId(Bank bank) {
        return dao.getLastClearingId(bank, Arrays.asList(STARTED));
    }

    /**
     * Получение клирингового события
     *
     * @param eventId ID события
     * @return клиринговое событие
     */
    public ClearingEvent getClearingEvent(Long eventId) {
        return dao.get(String.valueOf(eventId));
    }

    /**
     * Обновление статуса клирингового события на "Выполняется"
     *
     * @param clearingId ID события
     */
    public void setExecutedClearingEvent(Long clearingId) {
        dao.updateClearingState(clearingId, ClearingState.EXECUTE);
    }

    /**
     * Обновление статуса клирингового события на "Успешно"
     *
     * @param clearingId ID события
     */
    public void setSuccessClearingEvent(Long clearingId) {
        dao.updateClearingState(clearingId, ClearingState.SUCCESSFULLY);
    }

    /**
     * Обновление статуса клирингового события на "Ошибка"
     *
     * @param clearingId ID события
     */
    public void setFailedClearingEvent(Long clearingId) {
        dao.updateClearingState(clearingId, ClearingState.FAILED);
    }

}
