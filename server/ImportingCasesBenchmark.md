# Importing cases - benchmark

## HDD

Disk is working hard, you can hear it

-   Reading file, no database operations: 1280ms

### 300 records

-   No optimalizations: ~47s
-   Optimalization: no transaction: ~47s

## SSD

-   Reading file, no database operations: 287ms
-   Reading file and creating data, only accessing country db (inserting countries): 2.6s
-   Reading file and creating data, only accessing country db (finding countries): 1.5s

### 300 records

-   no optimalizations: 2.07s
-   no transaction: 1.7s
-   +db country index: 1.8s
-   +db country firstOrCreate replaced with query-where-first and seperate create: 1.75s
-   +db cases insert instead of create: 1.67s

### 3000 records

-   +all above: 15,61s
-   +db country useIndex suggestion to engine: 15s
-   -db country firstOrCreate replaced with query-where-first and seperate create: 15.7s
-   +cache country id to reduce db lookup: ~14s
-   +chunk cases (500 per chunk) to reduce db operations: ~2s

### 30000 records

-   +all above: 2.3s

### All (264664) records

-   +all above: ~28s
-   +chunk size 1000: ~27s
