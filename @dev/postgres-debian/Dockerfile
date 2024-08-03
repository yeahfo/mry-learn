FROM debezium/postgres:16
RUN sed -i 's|deb.debian.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list \
    && sed -i 's|ftp.debian.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apt/sources.list \
    && sed -i 's|apt.postgresql.org/pub|mirrors.tuna.tsinghua.edu.cn/postgresql|g' /etc/apt/sources.list.d/pgdg.list \
    && apt update \
    && apt install postgresql-16-wal2json -y \
    && apt clean \
    && rm -rf /var/lib/apt/lists /var/cache/apt/archives
# docker-buildx build -t debezium/postgres:16-wal2json .