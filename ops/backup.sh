#!/usr/bin/env bash
set -e

STAMP=$(date +"%Y%m%d_%H%M%S")
mkdir -p backups

docker exec stabilityos-postgres pg_dump -U stabilityos stabilityos > "backups/db_$STAMP.sql"

tar -czf "backups/stabilityos_project_$STAMP.tar.gz" \
  --exclude='backups' \
  --exclude='.git' \
  .

echo "Backup complete: $STAMP"
