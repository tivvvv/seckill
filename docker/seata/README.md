# Seata local environment

This directory runs Seata Server 2.6.0 for local seckill development.

## Ports

- TC service: 127.0.0.1:8091

The `apache/seata-server:2.6.0` image runs the TC service with `spring.main.web-application-type=none`, so this local
setup does not expose the web console.

## Local dependencies

- MySQL: `localhost:3306`, user `root`, password `root`
- Seata server database: `seata`
- Business database: `seckill`

This project currently runs Nacos `3.2.0`. The `apache/seata-server:2.6.0` image contains `nacos-client-1.4.6`, which
registers through the removed Nacos v1 naming API, so this local setup uses Seata file registry instead of registering
Seata Server into Nacos.

## Initialize database

```bash
mysql -uroot -proot < docker/seata/sql/seata-server-mysql.sql
mysql -uroot -proot < docker/seata/sql/undo-log-mysql.sql
```

## Start

```bash
docker compose -f docker/seata/docker-compose.yml up -d
```

## Logs

```bash
docker logs -f seata-server
```
