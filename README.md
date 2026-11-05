# Note-API

## Introduction

This project is a demo application built using Spring Boot. It provides a simple RESTful API for managing notes. The application allows you to create, read, update, and delete notes. Each note has a title, content, labels, URLs, and an optional color.

## How to run it using command line

To run the application from the command line, follow these steps:

1. Clone the repository from GitHub:
   ```sh
   git clone https://github.com/marcelloraffaele/notes-api.git
   cd notes-api
   ```

2. Install Java 21 and build the project using the Maven wrapper:
   ```powershell
   .\mvnw.cmd clean package
   ```

3. Run the Spring Boot application:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

The application will start and be accessible at `http://localhost:8080`.
On Linux/macOS, use `./mvnw` instead of `.\mvnw.cmd`.

## Choose note storage

Set `notes.storage` in [application.properties](src/main/resources/application.properties),
or override it with the `NOTES_STORAGE` environment variable.

| Value | Implementation | Behavior |
| --- | --- | --- |
| `memory` (default) | `NoteService` | Temporary in-process list; four example notes are loaded each time the API starts. No database is needed. |
| `postgres` | `NoteServiceDB` | Persistent PostgreSQL storage through Spring Data JPA. Requires a reachable, initialized database. |

Both implementations expose the same `/notes` API and JSON fields. The server assigns
IDs and ignores IDs supplied during creation or replacement updates. Labels are
matched exactly and case-sensitively; labels and URLs preserve order and duplicates.
Missing or null label/URL collections are treated as empty lists in both modes.
PostgreSQL IDs continue increasing and may have gaps.

Switching storage requires restarting the API and does **not** migrate data between
modes. Unknown or empty storage values fail startup. PostgreSQL connection or schema
errors also fail startup; the application never silently falls back to memory.

### Start PostgreSQL locally

[compose.yaml](compose.yaml) runs **only PostgreSQL**, exposed on `localhost:5432`.
Run the Java backend separately. Docker with Compose is required; port 5432 must be free.

In PowerShell, choose a local development password and start the database:

```powershell
$env:POSTGRES_USER = "notes"
$env:POSTGRES_PASSWORD = Read-Host "Choose a local development database password"
docker compose up -d --wait
```

if you are in unix:

```bash
export POSTGRES_USER="notes"
export POSTGRES_PASSWORD=$(read -sp "Choose a local development database password: " && echo $REPLY)
docker compose up -d --wait
```

The password is required; there is no committed default password. These instructions
are for local development, not production credential management. The database port
is bound to loopback rather than exposed to other machines.

In the same terminal, configure and run the backend:

```powershell
$env:NOTES_STORAGE = "postgres"
.\mvnw.cmd spring-boot:run
```

The default JDBC URL is `jdbc:postgresql://localhost:5432/notes`. The username defaults
to `notes`; `POSTGRES_USER` and `POSTGRES_PASSWORD` are used by both Compose and the API.
Environment variables are terminal-specific; configure them again in another terminal
or in your IDE run configuration.

For an existing external PostgreSQL database, override standard Spring settings:

```powershell
$env:NOTES_STORAGE = "postgres"
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://your-host:5432/notes"
$env:SPRING_DATASOURCE_USERNAME = "your-database-user"
$env:SPRING_DATASOURCE_PASSWORD = Read-Host "Database password"
.\mvnw.cmd spring-boot:run
```

`NOTES_DB_URL` is also supported as an override of the default JDBC URL.
Provision the schema from [001-notes.sql](docker/postgres/001-notes.sql) in a **new,
empty** external database before starting the API. That script includes sample data.
Hibernate validates the schema; the API does not create tables or seed PostgreSQL
on startup. The persistence model uses `notes`, `note_labels`, and `note_urls` tables.

### Sample data, persistence, and reset

On first creation of its named volume, PostgreSQL runs
[001-notes.sql](docker/postgres/001-notes.sql), creating tables and inserting the
same four sample notes used in memory mode. The identity sequence is advanced so
subsequent inserts do not collide with sample IDs.

Database data survives API and database restarts. `docker compose down` stops and
removes the container but keeps the volume. Initialization scripts do not rerun
against an existing volume; changing credentials in the environment does not change
an already initialized database's credentials.

**To delete all database data and initialize the samples again:**

```powershell
docker compose down -v
docker compose up -d --wait
```

**Warning:** `down -v` permanently deletes the notes database volume. Do not use it
if you need to preserve your notes.

To return to temporary storage, set `$env:NOTES_STORAGE = "memory"` and restart the API.

### To run the complete application with Docker Compose

```shell
export POSTGRES_USER="notes"
export POSTGRES_PASSWORD=$(read -sp "Choose a local development database password: " && echo $REPLY)

docker compose -f compose-complete.yml up -d
```

navigate to `http://localhost:8081` in your browser to access the Notes UI.

## Tests

Run JUnit unit tests, memory API tests, and storage configuration tests without Docker:

```powershell
.\mvnw.cmd test
```

Run the complete build:

```powershell
.\mvnw.cmd verify
```

The PostgreSQL integration test class, `NotePostgresIT`, is temporarily disabled
with JUnit's `@Disabled`. Remove that annotation to enable it during `verify`.

When enabled, integration tests use Testcontainers and require a running Docker engine and access
to pull `postgres:17-alpine`. They use an isolated database on a dynamically assigned
port and the same initialization SQL as Compose, not your local notes database.
They run through Maven Failsafe (`*IT`) and fail rather than silently skip when Docker
is unavailable. They cover both API contract parity and persistence across a fresh
application context.

## Accessing pgAdmin

If you have pgAdmin running via Docker Compose, you can access it at `http://localhost:5050` using the default credentials:
- Email: `admin@example.com`
- Password: `admin`

In order to connect pgAdmin to your PostgreSQL container, follow these steps:

1. Open pgAdmin in your browser at `http://localhost:5050`.
2. Log in using the default credentials (Email: `admin@example.com`, Password: `admin`).
3. Register a new server by right-clicking on "Servers" in the left sidebar and selecting "Create" -> "Server...".
4. In the "General" tab, provide a name for the server (e.g., `Notes PostgreSQL`).
5. In the "Connection" tab, enter the following details:
   - Hostname/address: `postgres`
   - Port: `5432`
   - Username: `notes`
   - Password: the password you set for the `POSTGRES_PASSWORD` environment variable.
6. Click "Save" to connect to the PostgreSQL container.

## How to change properties

To change the properties of the application, you can modify the `application.properties` file located in the `src/main/resources` directory. For example, to change the server port, you can add the following line to the `application.properties` file:

```properties
server.port=9090
```

This will change the server port to `9090`. You can also add other properties as needed.

## Note Fields

Each note has the following fields:
- `id`: The unique identifier of the note.
- `title`: The title of the note.
- `content`: The content of the note.
- `labels`: A list of labels associated with the note.
- `urls`: A list of URLs associated with the note.
- `color` (optional): The color of the note, which must be a valid HTML hex color code.
- `creationDate` (optional): The creation date and time of the note in ISO 8601 format.

### Examples of valid HTML hex color codes:
- Black: `#000000`
- White: `#FFFFFF`
- Red: `#FF0000`
- Green: `#00FF00`

## Use it from docker

To run the application from docker, follow these steps:
```sh
docker pull ghcr.io/marcelloraffaele/notes-api:latest
docker run -p 8080:8080 ghcr.io/marcelloraffaele/notes-api:latest
```

The application will start and be accessible at `http://localhost:8080`.
The swagger UI will be accessible at `http://localhost:8080/swagger-ui/index.html`
