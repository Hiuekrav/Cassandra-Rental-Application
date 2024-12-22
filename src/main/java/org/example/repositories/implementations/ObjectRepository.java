package org.example.repositories.implementations;
import lombok.Getter;
import org.example.model.AbstractEntity;
import org.example.repositories.interfaces.IObjectRepository;

import java.util.*;

//public abstract class ObjectRepository<T extends AbstractEntity> implements IObjectRepository<T> {
//
//    private final Class<T> modelClass;
//
//    public ObjectRepository(Class<T> modelClass) {
//        this.modelClass = modelClass;
//    }
//
//    public T findByIdOrNull(UUID id) {
//        //todo implement
//        return null;
//    }
//
//    @Override
//    public T findById(UUID id) {
//        //todo implement
//        return null;
//    }
//
//    @Override
//    public List<T> findAll() {
//        //todo implement
//        return null;
//    }
//
//    @Override
//    public T save(T object) {
//        //todo implement
//        return null;
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        //todo implement
//    }
//
//}
