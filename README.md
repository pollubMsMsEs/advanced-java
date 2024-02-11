<div align="center">

<picture>
    <source  media="(prefers-color-scheme: light)" srcset="./assets/logo-dark.svg">
    <img width="500px" src="./assets/logo-light.svg" alt="app logo">
</picture>

---

[Launching](#launching-with-docker) • [Requirements](#requirements) • [Database shcema](#database-schema)

</div>

**Project subject**: Compilation of global statistical data regarding the pandemic

## Showcase

![Showcase](./assets/showcase.gif)

## Launching with Docker

-   Run:

```
docker compose up
```

## Launching without Docker

### server

-   Install maven dependencies
-   Run:

```
docker compose -f "db-compose.yml" up
```

-   Launch spring app with `dev` profile

### client

-   Run:

```
npm i
npm run dev
```

## After launching

-   Register as admin
-   Import Cases and/or Vaccinations from CSV

## Requirements

### Mandatory

-   Database ✔️
-   Network communication ✔️
-   Handling of XML & JSON files ✔️
-   Sign up, sign in & authorization of JWT ✔️
-   Unit/Integration tests ✔️

### Optional

-   Lombok ✔️
-   Gson/Xmappr/JAXB ❌(used Jackson)
-   Spring Boot ✔️
-   SQL ✔️
-   Docker ✔️
-   Android app ❌
-   Postman ✔️
-   Git ✔️

## Used data

Data as of 2023-05-25

-   [Cases and deaths (by country and date)](https://github.com/owid/covid-19-data/blob/master/public/data/cases_deaths/full_data.csv)
-   [Total vaccinations (by country, date and manufacturer)](https://github.com/owid/covid-19-data/blob/master/public/data/vaccinations/vaccinations-by-manufacturer.csv)
-   [Details about vaccinations (by country and date)](https://github.com/owid/covid-19-data/blob/master/public/data/vaccinations/vaccinations.csv)

## Database schema

![Diagram](./assets/diagram.png)

---

### Used technologies

[<img align="left" width="26" height="26" alt="Spring" src="https://api.iconify.design/logos:spring-icon.svg" style="padding: 0 20px 16px 0">](https://spring.io)
[<img align="left" width="26" height="26" alt="Java" src="https://api.iconify.design/logos:java.svg" style="padding: 0 20px 16px 0">](https://www.java.com)
[<img align="left" width="26" height="26" alt="React" src="https://api.iconify.design/devicon:react.svg" style="padding: 0 20px 16px 0">](https://react.dev)
[<img align="left" width="26" height="26" alt="Vite" src="https://api.iconify.design/devicon:vitejs.svg" style="padding: 0 20px 16px 0">](https://vitejs.dev/)
[<img align="left" width="26" height="26" alt="MySQL" src="https://api.iconify.design/devicon:mysql.svg" style="padding: 0 20px 16px 0">](https://www.mysql.com)
[<img align="left" width="26" height="26" alt="Docker" src="https://api.iconify.design/logos:docker-icon.svg" style="padding: 0 20px 16px 0">](https://www.docker.com)
