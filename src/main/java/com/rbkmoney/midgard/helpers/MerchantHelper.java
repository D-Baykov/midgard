package com.rbkmoney.midgard.helpers;

import com.rbkmoney.midgard.DAO.MerchantDAO;
import org.jooq.generated.tables.pojos.Merchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.List;

/** Вспомогательный класс для работы с таблицей мерчантов */
public class MerchantHelper {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(MerchantHelper.class);
    /** Объект для работы с данными в БД */
    private final MerchantDAO dao;

    public MerchantHelper(DataSource dataSource) {
        dao = new MerchantDAO(dataSource);
    }

    /**
     * Сохранение списка мерчантов
     *
     * @param merchants список мерачнтов
     */
    public void saveMerchants(List<Merchant> merchants) {
        merchants.stream().forEach(this::saveMerchant);
    }

    /**
     * Saving a new merchant to the database
     *
     * @param merchant a new merchant
     */
    public void saveMerchant(Merchant merchant) {
        log.debug("Saving a merchant {}...", merchant);
        String merchantId = merchant.getMerchantId();
        Merchant tmpMerchant = dao.get(merchantId);
        if (tmpMerchant == null) {
            dao.save(merchant);
        } else {
            if (!tmpMerchant.equals(merchant)) {
                log.debug("A merchant with other data was found in the database ({}). The existing object will be " +
                        "closed and a new one will be added", tmpMerchant);
                dao.closeMerchant(merchantId);
                dao.save(merchant);
            }
        }
        log.debug("The merchant with id {} was saved", merchant.getMerchantId());
    }

    public Merchant getMerchant(String merchantId) {
        return dao.get(merchantId);
    }

    public List<Merchant> getMerchantHistory(String merchantId) {
        return dao.getMerchantHistory(merchantId);
    }

}
