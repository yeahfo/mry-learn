FROM debezium/postgres:16
RUN sed -i 's|deb.debian.org|mirrors.ustc.edu.cn|g' /etc/apt/sources.list \
    && sed -i 's|ftp.debian.org|mirrors.ustc.edu.cn|g' /etc/apt/sources.list \
    && sed -i 's|apt.postgresql.org/pub|mirrors.ustc.edu.cn/postgresql|g' /etc/apt/sources.list.d/pgdg.list \
    && apt update \
    && apt install postgresql-16-wal2json -y \
    && apt clean \
    && rm -rf /var/lib/apt/lists /var/cache/apt/archives \
    && localedef -i zh_CN -c -f UTF-8 -A /usr/share/locale/locale.alias zh_CN.UTF-8
ENV LANG zh_CN.utf8
ENV TZ Asia/Shanghai
# docker-buildx build -t debezium/postgres:16-wal2json-zh .