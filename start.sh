#!/bin/bash

docker-compose down
docker-compose up --build -d

echo "Waiting for PostgreSQL to start..."
while ! docker exec $(docker-compose ps -q db) pg_isready; do
  sleep 1
done

echo "PostgreSQL started, application is now available!"
