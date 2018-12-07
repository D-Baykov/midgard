package com.rbkmoney.midgard.DAO;

import com.rbkmoney.midgard.DAO.common.AbstractGenericDao;
import com.rbkmoney.midgard.DAO.common.RecordRowMapper;
import com.rbkmoney.midgard.exception.DaoException;
import org.jooq.Query;
import org.jooq.generated.tables.pojos.Config;
import org.jooq.generated.tables.records.ConfigRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;

import java.util.Optional;

import static org.jooq.generated.tables.Config.CONFIG;

/** Класс для взаимодействия с конфигурационной таблицей клиринга */
public class ConfigDAO extends AbstractGenericDao<Config> {

    /** Логгер */
    private static final Logger log = LoggerFactory.getLogger(ConfigDAO.class);
    /** Маппер */
    private final RowMapper<Config> configRowMapper;

    public ConfigDAO(DataSource dataSource) {
        super(dataSource);
        configRowMapper = new RecordRowMapper<>(CONFIG, Config.class);
    }

    @Override
    public Long save(Config config) {
        log.debug("Adding new config: {}", config);
        ConfigRecord record = getDslContext().newRecord(CONFIG, config);
        Query query = getDslContext().insertInto(CONFIG).set(record);
        execute(query);
        log.debug("Config with name {} was added", config.getName());
        return 0L;
    }

    @Override
    public Config get(String name) {
        Query query = getDslContext().selectFrom(CONFIG).where(CONFIG.NAME.eq(name));
        return fetchOne(query, configRowMapper);
    }

    /**
     * Получение идентификатора последнего эвента полученного от внешней системы
     *
     * @return номер последнего эвента
     */
    public Optional<Long> getLastEventId() {
        Config config = get("last_event_id");
        if (config != null) {
            return Optional.ofNullable(Long.parseLong(config.getValue()));
        }
        return Optional.empty();
    }

}
