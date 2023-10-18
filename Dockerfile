# Подключаю образ PostgreSQL
FROM postgres:latest

# Установка параметров окружения postgreSQL
ENV POSTGRES_DB=ylab_db
ENV POSTGRES_USER=ylab
ENV POSTGRES_PASSWORD=123456

# Старт PostgreSQL
CMD ["postgres"]