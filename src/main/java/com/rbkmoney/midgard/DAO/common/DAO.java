package com.rbkmoney.midgard.DAO.common;

import com.rbkmoney.midgard.exception.DaoException;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;

/**
 * Инфтерфейс для работы с БД
 *
 * @param <T> тип для взаимодействия с БД
 */
public interface DAO<T> {

    Long save(T element);

    T get(String id);

    int execute(Query query);

    int execute(Query query, int expectedRowsAffected) throws DaoException;

    int execute(Query query,
                int expectedRowsAffected,
                NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;


    int execute(String namedSql,
                SqlParameterSource parameterSource,
                int expectedRowsAffected,
                NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    <T> T fetchOne(Query query, RowMapper<T> rowMapper) throws DaoException;

    <T> T fetchOne(Query query,
                   RowMapper<T> rowMapper,
                   NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    <T> T fetchOne(String namedSql,
                   SqlParameterSource parameterSource,
                   RowMapper<T> rowMapper,
                   NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    <T> List<T> fetch(Query query, RowMapper<T> rowMapper) throws DaoException;

    <T> List<T> fetch(String namedSql,
                      SqlParameterSource parameterSource,
                      RowMapper<T> rowMapper) throws DaoException;

    <T> List<T> fetch(Query query,
                      RowMapper<T> rowMapper,
                      NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;

    <T> List<T> fetch(String namedSql,
                      SqlParameterSource parameterSource,
                      RowMapper<T> rowMapper,
                      NamedParameterJdbcTemplate namedParameterJdbcTemplate) throws DaoException;


    int executeWithReturn(Query query, KeyHolder keyHolder) throws DaoException;

    int executeWithReturn(Query query, int expectedRowsAffected, KeyHolder keyHolder) throws DaoException;

    int executeWithReturn(Query query,
                          int expectedRowsAffected,
                          NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                          KeyHolder keyHolder) throws DaoException;

    int executeWithReturn(String namedSql, KeyHolder keyHolder) throws DaoException;

    int executeWithReturn(String namedSql,
                          SqlParameterSource parameterSource,
                          KeyHolder keyHolder) throws DaoException;

    int executeWithReturn(String namedSql,
                          SqlParameterSource parameterSource,
                          int expectedRowsAffected,
                          KeyHolder keyHolder) throws DaoException;

    int executeWithReturn(String namedSql,
                          SqlParameterSource parameterSource,
                          int expectedRowsAffected,
                          NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                          KeyHolder keyHolder) throws DaoException;


}
