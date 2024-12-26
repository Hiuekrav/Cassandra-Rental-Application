package org.example.repositories.interfaces;

import java.util.List;
import java.util.UUID;

/**
 * Ogolne repozytorium, z bazowymi metodami dla wszystkich obiektow(encji)
 * @param <T> - obiekt klasy modelu
 */
public interface IObjectRepository<T> {

    T findById(UUID id);

    T findByIdOrNull(UUID id);

    T save(T obj);

    void deleteById(UUID id);

    void deleteAll();
}
