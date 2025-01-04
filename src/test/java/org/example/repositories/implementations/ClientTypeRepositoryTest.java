package org.example.repositories.implementations;

import org.example.model.clientType.Default;
import org.example.model.clientType.Gold;
import org.example.model.clientType.Silver;
import org.example.repositories.interfaces.IClientTypeRepository;
import org.example.utils.consts.DatabaseConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientTypeRepositoryTest {

        private IClientTypeRepository clientTypeRepository = new ClientTypeRepository();

        @BeforeEach
        void setUp() {
                clientTypeRepository.deleteAll();
        }

        @Test
        void createClientType() {
                Silver silver = new Silver(UUID.randomUUID(), 50.0, 15);
                clientTypeRepository.save(silver);
                Gold gold = new Gold(UUID.randomUUID(), 100.0, 25);
                clientTypeRepository.save(gold);
                assertEquals(silver.getId(), clientTypeRepository.findById(silver.getId()).getId());
                assertEquals(gold.getId(), clientTypeRepository.findById(gold.getId()).getId());
                assertEquals(2, clientTypeRepository.findAll().size());
        }

        @Test
        void findClientTypeById() {
                Default basic = new Default(UUID.randomUUID(), 0.0, 3);
                clientTypeRepository.save(basic);
                assertEquals(basic.getId(), clientTypeRepository.findById(basic.getId()).getId());
        }

        @Test
        void findClientTypeById_NotFoundException() {
                Gold extraGold = new Gold(UUID.randomUUID(), 200.0, 50);
                clientTypeRepository.save(extraGold);
                assertThrows(RuntimeException.class, ()-> clientTypeRepository.findById(UUID.randomUUID()));
        }


        @Test
        void updateClientType() {
                Silver silver = new Silver(UUID.randomUUID(), 50.0, 15);
                clientTypeRepository.save(silver);
                Integer newMaxVehicles = 18;
                Silver modified = Silver.builder()
                        .discriminator(DatabaseConstants.CLIENT_TYPE_SILVER_DISCRIMINATOR)
                        .maxVehicles(newMaxVehicles)
                        .id(silver.getId()).build();
                clientTypeRepository.save(modified);
                assertEquals(newMaxVehicles, clientTypeRepository.findById(silver.getId()).getMaxVehicles());
        }

        @Test
        void removeClientType() {
                Gold gold = new Gold(UUID.randomUUID(),20.0,4);
                clientTypeRepository.save(gold);
                assertEquals(1, clientTypeRepository.findAll().size());
                clientTypeRepository.deleteById(gold.getId());
                assertEquals(0, clientTypeRepository.findAll().size());
        }
}
