FROM debezium/postgres:16-alpine as builder

RUN sed -i 's|dl-cdn.alpinelinux.org|mirrors.tuna.tsinghua.edu.cn|g' /etc/apk/repositories \
    && apk add --no-cache --purge -uU git make gcc clang15 llvm15 postgresql16-dev \
    && git clone https://mirror.ghproxy.com/https://github.com/eulerto/wal2json.git \
    && cd wal2json \
    && make \
    && make install \
    && apk del git make gcc clang15 llvm15 postgresql16-dev \
    && rm -rf /var/cache/apk/* \
    && rm -rf /wal2json

FROM debezium/postgres:16-alpine

COPY --from=builder /usr/local/lib/postgresql /usr/local/lib/postgresql

# docker-buildx build -t debezium/postgres:16-alpine-wal2json .