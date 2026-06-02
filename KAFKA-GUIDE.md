# Kafka Setup Guide — Local Development

## Architecture

```
POST /api/v1/enterprise/employees
          ↓
EnterpriseEmployeeController
          ↓
EmployeeApplicationService
          ↓
EmployeeAggregate → EmployeeCreatedEvent
          ↓
DomainEventPublisherPort  (interface — domain layer)
          ↓
KafkaDomainEventPublisher (infrastructure adapter)
          ↓ KafkaTemplate<String, Object>
    employee-events  (Kafka Topic, 3 partitions)
          ↓
EmployeeEventConsumer  @KafkaListener
          ↓
Application Logs: [CONSUMER] ✅ Received employee event
```

## Local Runtime Architecture

```
┌─────────────────────────────────┐
│    Spring Boot Application      │
│    mvn spring-boot:run          │
│                                 │
│  REST Controllers               │
│       ↓                         │
│  Application Services           │
│       ↓                         │
│  Domain (Events raised)         │
│       ↓                         │
│  KafkaDomainEventPublisher      │
└──────────┬──────────────────────┘
           │
           ▼
┌──────────────────┐
│   PostgreSQL     │  localhost:5432
│ employee_directory│
└──────────────────┘
           │
           ▼
┌──────────────────┐
│     Kafka        │  localhost:9092
│ employee-events  │
│ department-events│
│ audit-events     │
└──────────┬───────┘
           │
           ▼
┌──────────────────┐
│   Consumers      │
│ EmployeeEvent    │
│ DepartmentEvent  │
│ AuditEvent       │
└──────────────────┘
```

---

## Kafka Topics

| Topic               | Partitions | Purpose                    |
|---------------------|-----------|----------------------------|
| `employee-events`   | 3         | Employee CRUD domain events |
| `department-events` | 3         | Department CRUD domain events |
| `audit-events`      | 1         | All events as audit trail  |

---

## Event Payload Examples

### employee-events

```json
{
  "eventId": "a1b2c3d4-...",
  "eventType": "EMPLOYEE_CREATED",
  "employeeId": 1,
  "email": "john@example.com",
  "departmentId": 2,
  "timestamp": "2026-06-01T12:00:00Z"
}
```

### department-events

```json
{
  "eventId": "e5f6g7h8-...",
  "eventType": "DEPARTMENT_CREATED",
  "departmentId": 5,
  "name": "Engineering",
  "timestamp": "2026-06-01T12:00:00Z"
}
```

---

## Step 1 — Install Kafka

### Linux

```bash
wget https://downloads.apache.org/kafka/3.7.0/kafka_2.13-3.7.0.tgz
tar -xzf kafka_2.13-3.7.0.tgz
cd kafka_2.13-3.7.0
```

### macOS (Homebrew)

```bash
brew install kafka

# Homebrew installs and manages Zookeeper automatically.
# Kafka binary directory: /opt/homebrew/bin  (Apple Silicon)
#                      or /usr/local/bin     (Intel)
```

### macOS (Manual — same as Linux)

```bash
curl -O https://downloads.apache.org/kafka/3.7.0/kafka_2.13-3.7.0.tgz
tar -xzf kafka_2.13-3.7.0.tgz
cd kafka_2.13-3.7.0
```

### Windows

1. Download from https://kafka.apache.org/downloads
2. Extract to `C:\kafka`
3. All commands below use `C:\kafka\bin\windows\` and `.bat` extension

---

## Step 2 — Start Zookeeper

Open **Terminal 1** and keep it running.

**Linux / macOS (manual install):**
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

**macOS (Homebrew):**
```bash
zookeeper-server-start /opt/homebrew/etc/kafka/zookeeper.properties
# or simply:
brew services start zookeeper
```

**Windows:**
```cmd
C:\kafka\bin\windows\zookeeper-server-start.bat C:\kafka\config\zookeeper.properties
```

Wait until you see: `INFO binding to port 0.0.0.0/0.0.0.0:2181`

---

## Step 3 — Start Kafka Broker

Open **Terminal 2** and keep it running.

**Linux / macOS (manual install):**
```bash
bin/kafka-server-start.sh config/server.properties
```

**macOS (Homebrew):**
```bash
kafka-server-start /opt/homebrew/etc/kafka/server.properties
# or simply:
brew services start kafka
```

**Windows:**
```cmd
C:\kafka\bin\windows\kafka-server-start.bat C:\kafka\config\server.properties
```

Wait until you see: `INFO [KafkaServer id=0] started`

---

## Step 4 — Create Topics (optional — app auto-creates them)

**Linux / macOS (manual install):**
```bash
bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 3 \
  --topic employee-events

bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 3 \
  --topic department-events

bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 1 \
  --topic audit-events
```

**macOS (Homebrew):**
```bash
kafka-topics --create --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 3 --topic employee-events

kafka-topics --create --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 3 --topic department-events

kafka-topics --create --bootstrap-server localhost:9092 \
  --replication-factor 1 --partitions 1 --topic audit-events
```

**Windows:**
```cmd
C:\kafka\bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 ^
  --replication-factor 1 --partitions 3 --topic employee-events

C:\kafka\bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 ^
  --replication-factor 1 --partitions 3 --topic department-events

C:\kafka\bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 ^
  --replication-factor 1 --partitions 1 --topic audit-events
```

---

## Step 5 — Verify Topics

**Linux / macOS (manual):**
```bash
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

**macOS (Homebrew):**
```bash
kafka-topics --list --bootstrap-server localhost:9092
```

**Windows:**
```cmd
C:\kafka\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

Expected output:
```
audit-events
department-events
employee-events
```

---

## Step 6 — Watch Events Live

Open **Terminal 3** to watch employee events:

**Linux / macOS (manual):**
```bash
bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic employee-events \
  --from-beginning
```

**macOS (Homebrew):**
```bash
kafka-console-consumer --bootstrap-server localhost:9092 \
  --topic employee-events --from-beginning
```

**Windows:**
```cmd
C:\kafka\bin\windows\kafka-console-consumer.bat ^
  --bootstrap-server localhost:9092 ^
  --topic employee-events --from-beginning
```

---

## Step 7 — Start the Application

```bash
mvn spring-boot:run
```

Or with the jar:
```bash
mvn clean package -DskipTests
java -jar target/employee-directory-0.0.1-SNAPSHOT.jar
```

---

## Step 8 — Trigger Events via API

**Create a department first (employees require a department):**
```bash
curl -X POST http://localhost:8080/api/v1/enterprise/departments \
  -H "Content-Type: application/json" \
  -d '{"name":"Engineering","description":"Engineering team","requestedBy":"admin"}'
```

**Create an employee:**
```bash
curl -X POST http://localhost:8080/api/v1/enterprise/employees \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "salary": 75000,
    "hireDate": "2026-01-15",
    "departmentId": 1,
    "requestedBy": "admin"
  }'
```

---

## Verification Checklist

### Producer Verification

Check application logs for:
```
[KAFKA] Routing event: type=EMPLOYEE_CREATED ...
[KAFKA] ✅ Employee event published: type=EMPLOYEE_CREATED partition=0 offset=0
```

### Consumer Verification

Check application logs for:
```
[CONSUMER] ✅ Received employee event: type=EMPLOYEE_CREATED employeeId=1 email=john.doe@example.com
```

### Wire Verification

Check the console consumer terminal (Terminal 3) for:
```json
{"eventId":"...","eventType":"EMPLOYEE_CREATED","employeeId":1,"email":"john.doe@example.com","departmentId":1,"timestamp":"2026-06-01T12:00:00Z"}
```

---

## Running Tests

Tests use EmbeddedKafka — no local Kafka installation needed to run tests.

```bash
# All tests
mvn clean test

# Kafka integration tests only
mvn test -Dtest=KafkaEventIntegrationTest

# Context load test only
mvn test -Dtest=EmployeeDirectoryApplicationTests
```

---

## Troubleshooting

### Broker unavailable / TimeoutException

```
org.apache.kafka.common.errors.TimeoutException: Topic not present in metadata
```

Kafka is not running. Start Zookeeper first, then Kafka broker. Verify:
```bash
# Linux/macOS manual
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

---

### Port 9092 already in use

```bash
# Linux/macOS
lsof -i :9092

# Windows
netstat -ano | findstr :9092
```

Change port in `config/server.properties`:
```properties
listeners=PLAINTEXT://localhost:9093
```

And update `application.yml`:
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9093
```

---

### Consumer not receiving messages

Check the consumer group offset:
```bash
# Linux/macOS manual
bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --describe --group employee-directory-group
```

Reset to earliest if needed:
```bash
bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 \
  --group employee-directory-group \
  --topic employee-events \
  --reset-offsets --to-earliest --execute
```

---

### Serialization / DeserializationException

Ensure the trusted packages config is present in `application.yml`:
```yaml
spring:
  kafka:
    consumer:
      properties:
        spring.json.trusted.packages: "com.act.intern.employeedirectory.enterprise.infrastructure.messaging.dto"
```

---

### Zookeeper connection refused

Start Zookeeper before Kafka. Wait for:
```
INFO binding to port 0.0.0.0/0.0.0.0:2181
```
Then start Kafka.

