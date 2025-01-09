package org.example.repositories.implementations;

import org.example.model.Client;
import org.example.model.clientType.Gold;
import org.example.model.clientType.Silver;
import org.example.repositories.interfaces.IClientRepository;
import org.example.repositories.interfaces.IClientTypeRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientRepositoryTest {

    private final IClientRepository clientRepository = new ClientRepository();
    private final IClientTypeRepository clientTypeRepository= new ClientTypeRepository();


    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
        clientTypeRepository.deleteAll();
    }

    @Test
    void createClient() {
        String email = "test@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(),
                "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        assertEquals(client.getId(), clientRepository.findById(client.getId()).getId());
        assertEquals(1, clientRepository.findAll().size());
    }


    @Test
    void findByEmail() {
        String email = "test@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        assertEquals(client.getId(), clientRepository.findByEmail(email).getId());
    }

    @Test
    void createClient_UniqueEmailException() {
        String email = "test@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        assertEquals(client.getId(), clientRepository.findById(client.getId()).getId());
        Client duplicatedEmailClient = new Client(UUID.randomUUID(), "Kamil", "Trubik",
                email, silver.getId(), "Wrocek", "Koni", "12");
        assertThrows(RuntimeException.class, ()-> clientRepository.save(duplicatedEmailClient));
        assertEquals(1, clientRepository.findAll().size());
    }


    @Test
    void findClientById_NotFoundException() {
        String email = "test@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email,silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        assertThrows(RuntimeException.class, ()-> clientRepository.findById(UUID.randomUUID()));
    }

    @Test
    void updateClient() {
        String email = "test@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        String newName = "Zmieniony";
        Client modifiedClient = Client.builder().firstName(newName).id(client.getId()).build();
        clientRepository.save(modifiedClient);
        assertEquals(newName, clientRepository.findById(modifiedClient.getId()).getFirstName());
    }

    @Test
    void updateClient_ChangeClientType() {
        String email = "test@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Gold gold = new Gold(UUID.randomUUID(), 20.0, 10);
        clientTypeRepository.save(gold);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        assertEquals(1, clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_SILVER_DISCRIMINATOR).size());
        assertEquals(0, clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_GOLD_DISCRIMINATOR).size());
        clientRepository.changeClientType(client.getId(), gold);
        assertEquals(gold.getId(), clientRepository.findById(client.getId()).getClientTypeId());
        assertEquals(0, clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_SILVER_DISCRIMINATOR).size());
        assertEquals(1, clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_GOLD_DISCRIMINATOR).size());

    }

    @Test
    void updateClient_ChangeClientEmail() {
        String email = "test@test.com";
        String newEmail = "new@mail.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Gold gold = new Gold(UUID.randomUUID(), 20.0, 10);
        clientTypeRepository.save(gold);
        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId, "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        clientRepository.changeClientEmail(clientId, newEmail);
        assertEquals(newEmail, clientRepository.findById(client.getId()).getEmail());
        assertEquals(clientId, clientRepository.findByEmail(newEmail).getId());
    }

    @Test
    void updateClient_ChangeClientEmail_EmailAlreadyTakenException() {
        String email = "test@test.com";;
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Gold gold = new Gold(UUID.randomUUID(), 20.0, 10);
        clientTypeRepository.save(gold);
        UUID clientId = UUID.randomUUID();
        Client client = new Client(clientId, "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        assertThrows(RuntimeException.class, ()-> clientRepository.changeClientEmail(clientId, email));

    }



    @Test
    void deleteByIdClient() {
        String email = "test@test.com";
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Client client = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                email, silver.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(client);
        assertEquals(1, clientRepository.findAll().size());
        clientRepository.deleteById(client.getId());
        assertEquals(0, clientRepository.findAll().size());
        assertEquals(0, clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_SILVER_DISCRIMINATOR).size());
    }

    @Test
    void increaseActiveRents() {
        Gold gold = new Gold(UUID.randomUUID(), 20.0, 10);
        clientTypeRepository.save(gold);
        Client clientG = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                "c@org.com",gold.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(clientG);
        assertEquals(0, clientRepository.findById(clientG.getId()).getActiveRents());
        clientRepository.increaseActiveRents(clientG.getId(), 1);
        assertEquals(1, clientRepository.findById(clientG.getId()).getActiveRents());
    }

    @Test
    void increaseActiveRents_MaxRentsExceeded() {
        Gold gold = new Gold(UUID.randomUUID(), 20.0, 10);
        clientTypeRepository.save(gold);
        Client clientG = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                "c@org.com",gold.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(clientG);
        assertEquals(0, clientRepository.findById(clientG.getId()).getActiveRents());
        clientRepository.increaseActiveRents(clientG.getId(), 1);
        assertEquals(1, clientRepository.findById(clientG.getId()).getActiveRents());
        assertThrows(RuntimeException.class, ()-> clientRepository.increaseActiveRents(clientG.getId(), 1));

    }

    @Test
    void decreaseActiveRents_RentsNumberNegative() {
        Gold gold = new Gold(UUID.randomUUID(), 20.0, 10);
        clientTypeRepository.save(gold);
        Client clientG = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                "c@org.com",gold.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(clientG);
        assertEquals(0, clientRepository.findById(clientG.getId()).getActiveRents());
        clientRepository.increaseActiveRents(clientG.getId(), 1);
        assertEquals(1, clientRepository.findById(clientG.getId()).getActiveRents());
        clientRepository.decreaseActiveRents(clientG.getId());
        assertEquals(0, clientRepository.findById(clientG.getId()).getActiveRents());
        assertThrows(RuntimeException.class, ()-> clientRepository.decreaseActiveRents(clientG.getId()));
    }

    @Test
    void findByType() {
        Silver silver = new Silver(UUID.randomUUID(), 12.0, 5);
        clientTypeRepository.save(silver);
        Gold gold = new Gold(UUID.randomUUID(), 20.0, 10);
        clientTypeRepository.save(gold);
        Client clientS1 = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                "a@org.com", silver.getId(), "Wawa", "Kwiatowa", "15");
        Client clientS2 = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                "b@org.com", silver.getId(), "Wawa", "Kwiatowa", "15");
        Client clientG = new Client(UUID.randomUUID(), "Jan", "Leszcz",
                "c@org.com", gold.getId(), "Wawa", "Kwiatowa", "15");
        clientRepository.save(clientS1);
        clientRepository.save(clientS2);
        clientRepository.save(clientG);
        assertEquals(3, clientRepository.findAll().size());
        assertEquals(2, clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_SILVER_DISCRIMINATOR).size());
        assertEquals(1, clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_GOLD_DISCRIMINATOR).size());
        assertEquals(clientG.getId(), clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_GOLD_DISCRIMINATOR).getFirst());
        assertTrue(clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_SILVER_DISCRIMINATOR).contains(clientS1.getId()));
        assertTrue(clientRepository.findByType(DatabaseConstants.CLIENT_TYPE_SILVER_DISCRIMINATOR).contains(clientS2.getId()));
    }
}