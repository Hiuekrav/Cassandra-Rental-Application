package org.example.repositories.implementations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//public class ClientTypeRepositoryTest {
//
//        private IClientTypeRepository clientTypeRepository;
//
//
//
//        @BeforeEach
//        void setUp() {
//                clientTypeRepository = new ClientTypeRepository(client, ClientTypeMgd.class);
//        }
//
//        @AfterEach
//        void dropDatabase() {
//                client.getDatabase(DatabaseConstants.DATABASE_NAME).getCollection(DatabaseConstants.CLIENT_TYPE_COLLECTION_NAME).drop();
//        }
//
//        @Test
//        void createClientType() {
//                Silver silver = new Silver(UUID.randomUUID(), 50.0, 15);
//                clientTypeRepository.save(new SilverMgd(silver));
//                Gold gold = new Gold(UUID.randomUUID(), 100.0, 25);
//                clientTypeRepository.save(new GoldMgd(gold));
//                assertEquals(silver.getId(), clientTypeRepository.findById(silver.getId()).getId());
//                assertEquals(gold.getId(), clientTypeRepository.findById(gold.getId()).getId());
//                assertEquals(2, clientTypeRepository.findAll().size());
//        }
//
//        @Test
//        void findClientTypeById() {
//                Default basic = new Default(UUID.randomUUID(), 0.0, 3);
//                clientTypeRepository.save(new DefaultMgd(basic));
//                assertEquals(basic.getId(), clientTypeRepository.findById(basic.getId()).getId());
//        }
//
//        @Test
//        void findClientTypeById_NotFoundException() {
//                Gold extraGold = new Gold(UUID.randomUUID(), 200.0, 50);
//                clientTypeRepository.save(new GoldMgd(extraGold));
//                assertThrows(RuntimeException.class, ()-> clientTypeRepository.findById(UUID.randomUUID()));
//        }
//
//
//        @Test
//        void updateClientType() {
//                Silver silver = new Silver(UUID.randomUUID(), 50.0, 15);
//                clientTypeRepository.save(new SilverMgd(silver));
//                Integer newMaxVehicles = 18;
//                Silver modified = Silver.builder().maxVehicles(newMaxVehicles).id(silver.getId()).build();
//                clientTypeRepository.save(new SilverMgd(modified));
//                assertEquals(newMaxVehicles, clientTypeRepository.findById(silver.getId()).getMaxVehicles());
//        }
//
//        @Test
//        void removeClientType() {
//                Gold gold = new Gold(UUID.randomUUID(),20.0,4);
//                clientTypeRepository.save(new GoldMgd(gold));
//                assertEquals(1, clientTypeRepository.findAll().size());
//                clientTypeRepository.deleteById(gold.getId());
//                assertEquals(0, clientTypeRepository.findAll().size());
//
//        }
//
//
//}
