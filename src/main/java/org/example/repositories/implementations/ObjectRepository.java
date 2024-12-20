package org.example.repositories.implementations;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import lombok.Getter;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;
import org.example.mgd.*;
import org.example.mgd.clientType.ClientTypeMgd;
import org.example.mgd.vehicle.VehicleMgd;
import org.example.model.AbstractEntity;
import org.example.repositories.interfaces.IObjectRepository;
import org.example.utils.consts.DatabaseConstants;

import java.lang.reflect.Field;
import java.util.*;


@Getter
public abstract class ObjectRepository<T extends AbstractEntity> implements IObjectRepository<T> {

    private final Class<T> modelClass;

    public ObjectRepository(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    public T findByIdOrNull(UUID id) {
        //todo implement
        return null;
    }

    @Override
    public T findById(UUID id) {
        //todo implement
        return null;
    }

    @Override
    public List<T> findAll() {
        //todo implement
        return null;
    }

    @Override
    public T save(T object) {
        //todo implement
        return null;
    }

    @Override
    public void deleteById(UUID id) {
        //todo implement
    }

}
