FROM bitnami/minideb:bookworm as builder
RUN sed -i 's|deb.debian.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list \
    && apt update \
    && apt install curl -y \
    && curl https://mirrors.tuna.tsinghua.edu.cn/postgresql/repos/apt/ACCC4CF8.asc -o /etc/apt/keyrings/postgres.gpg.asc \
    && echo "deb [ signed-by=/etc/apt/keyrings/postgres.gpg.asc ] https://mirrors.tuna.tsinghua.edu.cn/postgresql/repos/apt/ bookworm-pgdg main 16" > /etc/apt/sources.list.d/pgdg.list \
    && apt update \
    && apt install git make gcc postgresql-server-dev-16 -y \
    && git clone https://mirror.ghproxy.com/https://github.com/eulerto/wal2json.git \
    && cd wal2json \
    && make \
    && make install \
    && mkdir -p /docker-entrypoint-initdb.d
#    && echo "CREATE SCHEMA IF NOT EXISTS keycloak;" >> /docker-entrypoint-initdb.d/init-keycloak-schema.sql

FROM bitnami/postgresql:16

COPY --from=builder /usr/lib/postgresql/16/lib /opt/bitnami/postgresql/lib
#COPY --from=builder /docker-entrypoint-initdb.d /docker-entrypoint-initdb.d
# docker:
# POSTGRESQL_EXTRA_FLAGS: "-c wal_level=logical"
# k8s bitnami/postgresql:
#      - primary:
#          extendedConfiguration: |
#            wal_level = logical