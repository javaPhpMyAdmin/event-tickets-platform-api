#!/bin/bash
set -e

for db in event_ticket_platform keycloak_db; do
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" -c "CREATE DATABASE $db;" || echo "Database $db already exists"
done