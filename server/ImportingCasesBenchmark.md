# Importing cases - benchmark

## HDD

Disk is working hard, you can hear it

-   Reading file, no database operations: 1280ms

### 300 records

-   No optimalizations: ~47s
-   Optimalization: no transaction: ~47s

## SSD

-   Reading file, no database operations: 287ms

### 300 records

-   No optimalizations: 2.07s
-   no transaction: 1.7s
-   +db country index: 1.8s
-   +db country firstOrCreate replaced with query-where-first and seperate create: 1.75s
-   +db cases insert instead of create: 1.67ms

### 3000 records

-   +All above optimalizations: 15,61s
-   +db country useIndex suggestion to engine: 15s
-   -db country firstOrCreate replaced with query-where-first and seperate create: 15.7s
