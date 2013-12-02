package ru.shubert.jobportal.service;

import org.bson.types.ObjectId;
import ru.shubert.jobportal.dao.QueryParams;

import java.util.List;

/**
 * User: user
 * Date: 10.07.12 23:26
 */
public interface IService {
    <T> T findOne(String id, Class<T> tClass);

    <T> T findOne(ObjectId id, Class<T> tClass);

    <T> T save(T t);

    <T> void delete(T t);

    <T> long count(T t);

    <T> List<T> find(T example, QueryParams params);
}
