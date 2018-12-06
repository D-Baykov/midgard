package com.rbkmoney.midgard.DAO;

import com.rbkmoney.midgard.DAO.common.DAO;
import com.rbkmoney.midgard.exception.DaoException;

public interface DominantDao extends DAO {
    Long getLastVersionId() throws DaoException;
}
