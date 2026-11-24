# Outbox Pattern w Kotlin + Spring Boot

Prosta aplikacja demonstracyjna prezentująca działanie **Transactional Outbox Pattern** w języku Kotlin z wykorzystaniem Spring Boot, PostgreSQL, Flyway oraz Docker Compose.

## Jak to działa

1. Klient wysyła żądanie `POST /orders` z ciałem JSON:
   ```json
   {
     "customerId": "123"
   }
   ```
2. `OrderService.createOrder` w **jednej transakcji bazodanowej (`@Transactional`)**:
   - zapisuje zamówienie w tabeli `orders`,
   - zapisuje zdarzenie `OrderCreated` w tabeli `outbox_events` (`processed = false`).
3. `OutboxPublisher` uruchamiany cyklicznie co 5 sekund (`@Scheduled`):
   - pobiera nieprzetworzone rekordy z `outbox_events` (`processed = false`),
   - wypisuje zdarzenie do logów aplikacji,
   - oznacza rekord jako przetworzony (`processed = true`, ustawia `processed_at`).

## Stack technologiczny

- Kotlin + Spring Boot
- Spring Data JPA + Flyway
- PostgreSQL 16
- Gradle (Kotlin DSL)
- Docker & Docker Compose

## Uruchomienie na Docker Desktop

Upewnij się, że Docker Desktop jest uruchomiony, a następnie w katalogu projektu wykonaj:

```bash
docker compose up -d --build
```

Sprawdź logi aplikacji:

```bash
docker compose logs -f app
```

## Testowanie endpointu

### curl (Bash / Git Bash)
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId": "123"}'
```

### PowerShell
```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/orders" -ContentType "application/json" -Body '{"customerId": "123"}'
```

Po kilku sekundach w logach aplikacji pojawi się wpis z `OutboxPublisher`:
```text
Publishing outbox event: id=..., eventType=OrderCreated, aggregateType=Order, aggregateId=..., payload={"orderId":"...","customerId":"123","createdAt":"..."}
```

### Podgląd rekordów w PostgreSQL

W przeglądarce przez **Adminer** (`http://localhost:8082`):
- **System:** PostgreSQL
- **Serwer:** `postgres`
- **Użytkownik:** `outbox`
- **Hasło:** `outbox`
- **Baza danych:** `outbox`

Lub z terminala:

```bash
docker compose exec postgres psql -U outbox -d outbox -c "SELECT * FROM orders;"
docker compose exec postgres psql -U outbox -d outbox -c "SELECT id, event_type, aggregate_id, processed, created_at, processed_at FROM outbox_events;"
```

## Uruchomienie testów lokalnie

```bash
./gradlew test
```

Testy używają bazy H2 w trybie kompatybilności z PostgreSQL oraz migracji Flyway.

## Zatrzymanie kontenerów

```bash
docker compose down -v
```
