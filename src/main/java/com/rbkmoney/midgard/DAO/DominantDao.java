package com.rbkmoney.midgard.DAO;

import com.rbkmoney.midgard.DAO.common.DAO;
import com.rbkmoney.midgard.exception.DaoException;

/** Интерфейс для взаимодействия с доминантой */
//TODO: пока что в зачаточном состояний. Будет реализовано в последующих спринтах
public interface DominantDao extends DAO {

    /** Получение ID последнего события для доминанты */
    Long getLastVersionId() throws DaoException;

}
