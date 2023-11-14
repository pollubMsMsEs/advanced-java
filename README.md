# Advanced Java: Project

**Temat**: Zestawienie globalnych danych statystycznych dotyczących pandemii

## Jak uruchomić?

-   Zpulluj projekt

### Docker

-   Uruchom `docker compose up`

### lub lokalnie

#### Serwer

-   Zaczekaj na zainstalowanie maven dependencies
-   Uruchom `docker compose -f "db-compose.yml" up`
-   Uruchom z profilem `dev`

#### Klient

-   Uruchom `npm i`
-   Uruchom `npm run dev`

### Po uruchomieniu

-   Zarejestruj się jako admin
-   Zaimportuj Cases i/lub Vaccinations z CSV

## Wymagania

### Obowiązkowe

-   Baza danych ✔️
-   Komunikacja sieciowa ✔️
-   Obsługa plików XML & JSON ✔️
-   Rejestracja, logowanie, autoryzacja JWT ✔️
-   Testy jednostkowe/integracyjne ✔️

### Opcjonalne

-   Lombok ✔️
-   Gson/Xmappr/JAXB ❓ (Jackson)
-   Spring Boot ✔️
-   SQL ✔️
-   Docker ✔️
-   Android ❌
-   Postman ✔️
-   Git ✔️

## Wykorzystywane dane

-   [Przypadki i śmiertelność (według kraju i daty)](https://github.com/owid/covid-19-data/blob/master/public/data/cases_deaths/full_data.csv)
-   [Całkowite szczepienia (według kraju, daty i producenta)](https://github.com/owid/covid-19-data/blob/master/public/data/vaccinations/vaccinations-by-manufacturer.csv)
-   [Szczegóły dotyczące szczepień (według kraju i daty)](https://github.com/owid/covid-19-data/blob/master/public/data/vaccinations/vaccinations.csv)

## Dokumentacja

![Diagram](./diagram.png)
