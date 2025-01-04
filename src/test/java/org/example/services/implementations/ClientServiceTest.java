package org.example.services.implementations;

import org.example.commons.dto.create.ClientCreateDTO;
import org.example.commons.dto.update.ClientUpdateDTO;
import org.example.model.Client;
import org.example.model.Rent;
import org.example.model.clientType.Silver;
import org.example.model.vehicle.Car;
import org.example.repositories.implementations.VehicleRepository;
import org.example.repositories.interfaces.IVehicleRepository;
import org.example.services.interfaces.IClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientServiceTest {

    IClientService clientService = new ClientService() ;
    private final IVehicleRepository carRepository = new VehicleRepository();

    @BeforeEach
    void setUp() {
        clientService.deleteAll();
        carRepository.deleteAll();
    }

    @Test
    void createClient() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(silver);
        ClientCreateDTO createDTO = new ClientCreateDTO("Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");

        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        assertEquals(createClient.getId(), clientService.findClientById(createClient.getId()).getId());
        assertEquals(createClient.getFirstName(), clientService.findClientById(createClient.getId()).getFirstName());
        assertEquals(createClient.getClientTypeId(), clientService.findClientById(createClient.getId()).getClientTypeId());
    }

    @Test
    void findClientById() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(silver);
        ClientCreateDTO createDTO = new ClientCreateDTO("Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        assertEquals(createClient.getId(), clientService.findClientById(createClient.getId()).getId());
    }

    @Test
    void findClientByEmail() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(silver);
        ClientCreateDTO createDTO = new ClientCreateDTO("Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        assertEquals(createClient.getId(), clientService.findClientByEmail(createClient.getEmail()).getId());
    }

    @Test
    void findAll() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(silver);
        ClientCreateDTO createDTO = new ClientCreateDTO("Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());

    }

    @Test
    void updateClient() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(silver);
        ClientCreateDTO createDTO = new ClientCreateDTO("Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        String newCityName = "Lodz";
        String newFirstName = "Lodz";
        String newMail = "waddwad@gmail.com";
        ClientUpdateDTO updateDTO = ClientUpdateDTO.builder().cityName(newCityName).email(newMail)
                .firstName(newFirstName).id(createClient.getId()).build();
        clientService.updateClient(updateDTO);
        assertEquals(newCityName, clientService.findClientById(createClient.getId()).getCityName());
        assertEquals(newFirstName, clientService.findClientById(createClient.getId()).getFirstName());
        assertEquals(newMail, clientService.findClientById(createClient.getId()).getEmail());

    }

    @Test
    void removeClient() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(silver);
        ClientCreateDTO createDTO = new ClientCreateDTO("Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client client = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());
        clientService.removeClient(client.getId());
        assertEquals(0, clientService.findAll().size());
    }

    @Test
    void removeClient_ClientHasRent() {
        String email = "test23@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 10.0, 5);
        clientService.getClientTypeRepository().save(silver);
        ClientCreateDTO createDTO = new ClientCreateDTO("Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        Client createClient = clientService.createClient(createDTO);
        assertEquals(1, clientService.findAll().size());

        Car car = new Car(UUID.randomUUID(), "AABB123", 100.0,3, Car.TransmissionType.MANUAL);
        carRepository.save(car);

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now().plusHours(4), createClient, car);
        clientService.getRentRepository().save(rent);

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () ->clientService.removeClient(createClient.getId()));
        assertEquals("Unable to remove client, has active or archived rents", runtimeException.getMessage());

    }
}