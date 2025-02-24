# Note-API

## Introduction

This project is a demo application built using Spring Boot. It provides a simple RESTful API for managing notes. The application allows you to create, read, update, and delete notes. Each note has a title, content, labels, URLs, and an optional color.

## How to run it using command line

To run the application from the command line, follow these steps:

1. Clone the repository from GitHub:
   ```sh
   git clone https://github.com/marcelloraffaele/spring-boot-demo.git
   cd spring-boot-demo
   ```

2. Build the project using Maven:
   ```sh
   ./mvn clean install
   ```

3. Run the Spring Boot application:
   ```sh
   ./mvn spring-boot:run
   ```

The application will start and be accessible at `http://localhost:8080`.

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
