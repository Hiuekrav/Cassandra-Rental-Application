# Vehicle Rental System with Cassandra database
This project is an alternative implementation of the Vehicle Rental System using Apache Cassandra as the primary database. It replaces the MongoDB and Redis setup with a Cassandra-based architecture, focusing on scalability, denormalization, and high availability. The system includes CRUD operations, business logic for rentals, and testing with JUnit. The business logic remained unchanged from the original design, which can be found here https://github.com/wiktoriaBi/Mongo-and-Redis-Rental-Application.

## Project Overview
The system provides:
- **CRUD operations** for clients, client types, vehicles, and rentals.
- **Business logic** such as ending a rental (archiving it), updating client active rentals, updating vehicle status (rented/free).
- **Denormalized data model** optimized for Cassandra.
- **Optimistic locking** for handling concurrent updates.
- **JUnit tests** for repositories and services.

## Database Structure
### Cassandra Tables
1. **Clients**
    - ```clients```: Stores client details.
    - ```clients_by_email```: Secondary index for querying clients by email.
    - ```clients_by_client_type```: Secondary index for querying clients by type.
2. **ClientTypes**
    - ```client_types```: Stores client type details (e.g., Default, Silver, Gold).
3. **Vehicles**
    - ```vehicles```: Stores vehicle details.
    - ```vehicles_by_plate_number```: Secondary index for querying vehicles by plate number.
4. **Rentals**
    - ```rents```: Stores rental details.
    - ```rents_by_client```: Secondary index for querying rentals by client.
    - ```rents_by_vehicle```: Secondary index for querying rentals by vehicle.

### Consistency and Replication
- **Consistency Level (CL)**: QUORUM for reads and writes to ensure strong consistency.
- **Replication Factor**: 3 for high availability and fault tolerance.

## Setup and Testing the Project
### Prerequisites
- Docker
- Java 21
- Maven

### Setup
- Clone this repository.
- Set up Cassandra by running services directly from docker-compose using IDE or by executing ```docker-compose up -d``` command.

### Testing
- Run all **JUnit** tests using: ```mvn test``` or use your IDE to run tests.

## Chebotko Diagram
Below is the Chebotko diagram representing the application's data model and flow:
![chebotko diagram](https://github.com/user-attachments/assets/28c94727-b200-41e7-a3ac-20d4c1eaaf85)

## Additional Notes
### 3-Node Cassandra Cluster
The project is configured to use a 3-node Cassandra cluster for high availability and fault tolerance. The cluster is set up using Docker Compose, with each node (cassandra1, cassandra2, cassandra3) running in a separate container. Each node is configured with ```MAX_HEAP_SIZE=400M``` and ```HEAP_NEWSIZE=4M``` to optimize memory usage.

### Downgrading to a 2-Node Cluster
If your system does not have enough resources to run a 3-node cluster, you can downgrade to a 2-node cluster by removing the ```cassandra3``` service from the ```docker-compose.yml``` file.

## Authors
### Wiktoria Bilecka
### Grzegorz Janasek
