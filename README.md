# Docker Deploy

## To Deploy PostgreSQL with Docker:

1. Open a shell and navigate to your project directory.
2. Run the following command to start the PostgreSQL container:

```bash
docker-compose up -d
```

This will:

- Download the latest official PostgreSQL image.
- Create and start a container using the downloaded image.
- Map port `5432` of the container to `localhost:5432` on your machine for easy access.
- Create a named volume (`postgres_myfitnessapp_docker_try`) to store the PostgreSQL data persistently.

## To Access the Database:

Once the container is up and running, you can interact with your PostgreSQL database:

1. To check the initialization folder where scripts are run at startup (like table creation), use the following command:

```bash
docker exec -it myfitnessappbackenddraft-postgres-1 ls /docker-entrypoint-initdb.d
```

2. To access the PostgreSQL database and run queries, use:

```bash
docker exec -it myfitnessappbackenddraft-postgres-1 psql -U az03250 -d myfitnessapp_docker_try
```

- Replace `<CONTAINER_NAME>` with the name of your PostgreSQL container.
- Replace `<IPN>` with the PostgreSQL user (in this case, `az03250`).
- Replace `<DB_NAME>` with the database name (`myfitnessapp_docker_try` in this case).

---
