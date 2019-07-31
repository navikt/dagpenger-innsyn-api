# dagpenger-innsyn-api

API for innsyn i inntekt som er grunnlag for dagpengestøtte.

## Utvikling av applikasjonen

### Starte applikasjonen lokalt

Applikasjonen har avhengigheter til Kafka som kan kjøres
opp lokalt vha. Docker Compose. 
Docker Compose følger med [Docker Desktop](https://www.docker.com/products/docker-desktop).


Starte Kafka:
```
docker-compose -f docker-compose.yml up
```
Etter at containerne er startet kan man starte applikasjonen ved å kjøre main-metoden i InnsynAPI.


Stoppe Kafka:

```
ctrl-c og docker-compose -f docker-compose.yml down
```

## Relaterte repoer

### Brukergrensesnitt
[Repoet for tilhørende brukergrensesnitt.](https://github.com/navikt/dp-inntekt-innsyn-ui)

### dp-innsyn-complete
[dp-innsyn-complete]([dp-innsyn-complete]) samler begge tilhørende repoer.