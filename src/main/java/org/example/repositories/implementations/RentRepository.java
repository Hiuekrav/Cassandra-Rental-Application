package org.example.repositories.implementations;

import org.example.model.Rent;
import org.example.repositories.interfaces.IRentRepository;

import java.util.List;
import java.util.UUID;

public class RentRepository implements IRentRepository {

    public RentRepository() {

    }

    @Override
    public Rent findById(UUID id) {
        return null;
    }

    @Override
    public Rent findByIdOrNull(UUID id) {
        return null;
    }

    @Override
    public List<Rent> findAll() {
        return List.of();
    }

    @Override
    public Rent save(Rent rentMgd) {
        return null;
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteAll() {

    }

    public void moveRentToArchived(UUID rentId) {
        return;
    }


    @Override
    public Rent findActiveById(UUID id) {
        return null;
    }

    @Override
    public Rent findArchiveById(UUID id) {
        return null;
    }

    @Override
    public List<Rent> findAllActiveByClientId(UUID clientId) {
        return null;
    }

    @Override
    public List<Rent> findAllArchivedByClientId(UUID clientId) {
        return null;
    }

    @Override
    public List<Rent> findAllByClientId(UUID clientId) {
        return null;
    }

    @Override
    public List<Rent> findAllArchivedByVehicleId(UUID vehicleId) {
        return null;
    }

    @Override
    public List<Rent> findAllActiveByVehicleId(UUID vehicleId) {
        return null;
    }

    @Override
    public List<Rent> findAllByVehicleId(UUID vehicleId) {
        return null;
    }

}
