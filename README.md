# Internátny informačný systém
Back-end systém vytvorený v jazyku JAVA, databázovým systémom PostgreSQL a PL/pgSQL.

## O projekte
V repozitári sa nachádza dokument [sprava.pdf](sprava.pdf), kde sa nachádza:
* UML diagram dátového modelu
* popis entít 
* relačný model dát tabuliek v databáze
* popis organizácie kódu v packagoch
* vykonané zmeny na optimalizáciu systému a porovnania optimalizácie
* najzložitejší riešený problém

## Databáza
Systém je vytvorený pre PostgreSQL databázu a obsahuje **CREATE SCRIPT** a **GENERATE SCRIPT**, ktoré pripravia databázu na používanie.

## Setup
Stiahnutie obsahu repozitára:
```
https://github.com/ipinfil/idbs.git
```

Pre správne fungovanie aplikácie je potrebné mať k dispozícii JDBC driver pre PostgreSQL systém. Konkrétne sa použila verzia
```
postgresql-42.2.11.jar
```

Následne je potrebné vložiť do [Main.java](src/main/Main.java) údaje na pripojenie k databáze.

Po správnej konfigurácií môžete spustiť [Main.java](src/main/Main.java), v konzole sa vypíše menu, pomocou ktorého sa systém obsluhuje. Voľbou **X** sa inicializuje databáza s dátami.
