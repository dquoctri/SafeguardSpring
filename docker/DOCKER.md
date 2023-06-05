# Database
## 1. Postgres container
```
docker run --name safeguard-postgres -p5432:5432 -e POSTGRES_DB=postgres -e POSTGRES_PASSWORD=postgres -v safeguard_postgres_pgdata:/var/lib/postgresql/data --restart always -d postgres:15.3
```

#### setup empty postgres database:
```
docker exec -it safeguard-postgres sh -c "createdb -U postgres safeguard-postgres;"
```