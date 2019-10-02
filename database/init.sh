#!/bin/bash

docker exec -it trade_db_1 psql -Udev -c "create role dev login;"
docker exec -it trade_db_1 psql -Udev -c "create database dev;"
