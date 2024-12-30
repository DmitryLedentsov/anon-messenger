# Анонимный Мессенджер AnonMessenger

## Deploy

Инструкция для запуска новой версии мессенджера с использованием Apache Kafka

Для каждого экземпляра сервера меняем порт в следующих строчках:

#### **`application-net.properties`**

```properties
spring.kafka.group-id=updates-9087
server.port=9087
```

```bash
cd messenger
sudo mvn package # если серверов несколько, то надо переименовывать каждый полученный jar, чтобы они не перезатирались
cd ../docker-files

# Если нет postgres локального, берём образ docker в этой же директории обязан лежать messenger-0.0.1-spring-boot.jar
sudo docker-compose --env-file ./default.env -f ./docker-compose-net.yml build --no-cache
sudo docker-compose --env-file ./default.env -f ./docker-compose-net.yml up postgres_db

# start Apache Kafka from container
sudo docker pull apache/kafka:3.9.0
sudo docker run -p 9092:9092 apache/kafka:3.9.0
```

Создайте nginx load balancing конфигурацию и на месте закомментированных серверов в *upstream web* добавьте свои (изменять механизм балансировки с *ip_hash* строго запрещено!):

#### **`/etc/nginx/nginx.conf`**

```properties
user www-data;
pid /run/nginx.pid;
include /etc/nginx/modules-enabled/*.conf;

# This number should be, at maximum, the number of CPU cores on your system. 
worker_processes 24;

# Number of file descriptors used for Nginx.
worker_rlimit_nofile 200000;

# Only log critical errors.
# error_log /var/log/nginx/error.log crit;

events {

    # Determines how many clients will be served by each worker process.
    worker_connections 4000;

    # The effective method, used on Linux 2.6+, optmized to serve many clients with each thread.
    use epoll;

    # Accept as many connections as possible, after nginx gets notification about a new connection.
    multi_accept on;

}
http {
        # Caches information about open FDs, freqently accessed files.
    open_file_cache max=200000 inactive=20s; 
    open_file_cache_valid 30s; 
    open_file_cache_min_uses 2;
    open_file_cache_errors on;

    # Disable access log altogether.
    access_log off;

    # Sendfile copies data between one FD and other from within the kernel.
    sendfile on; 

    # Causes nginx to attempt to send its HTTP response head in one packet,  instead of using partial frames.
    tcp_nopush on;

    # Don't buffer data-sends (disable Nagle algorithm).
    tcp_nodelay on; 

    # Timeout for keep-alive connections. Server will close connections after this time.
    keepalive_timeout 30;

    # Number of requests a client can make over the keep-alive connection.
    keepalive_requests 1000;

    # Allow the server to close the connection after a client stops responding. 
    reset_timedout_connection on;

    # Send the client a "request timed out" if the body is not loaded by this time.
    client_body_timeout 10;

    # If the client stops reading data, free up the stale client connection after this much time.
    send_timeout 2;

    # Compression.
    gzip on;
    gzip_min_length 10240;
    gzip_proxied expired no-cache no-store private auth;
    gzip_types text/plain text/css text/xml text/javascript application/x-javascript application/xml;
    gzip_disable "msie6";
    upstream web {
        ip_hash;
        # server localhost:9089;
        # server localhost:9088;
        server localhost:9087;
    }

    map $http_upgrade $proxy_connection {
        default upgrade;
        ''      close;
    }

    server {
        listen 80;
        proxy_next_upstream error timeout http_503;
        proxy_next_upstream_tries 2; 

        location / {
            proxy_pass http://web;
        }

        location /ws {
           proxy_pass http://web;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header Host $host;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

           # Pass the csrf token (see https://de.wikipedia.org/wiki/Cross-Site-Request-Forgery)
           # Default in Spring Boot
           proxy_pass_header X-XSRF-TOKEN;

           # WebSocket support (nginx 1.4)
           proxy_http_version 1.1;
           proxy_set_header Upgrade $http_upgrade;
           proxy_set_header Connection $proxy_connection;
           proxy_read_timeout 10000s;
        }
    }
}
```

Применяем конфигурацию и запускаем сервера.

```bash
sudo systemctl start nginx.service

# Start new distributed server with Apache Kafka
sudo java -jar messenger-0.0.1-spring-boot.jar --spring.profiles.active=net
# sudo java -jar messenger-0.0.2-spring-boot.jar --spring.profiles.active=net
# ...
```

### Deprecated

<strike>

```bash
# Build application
cd messenger
# Put jar file into ${project_dir}/docker-files
sudo mvn -с install
cd ../docker-files
# Remove old images
sudo docker-compose --env-file ./default.env down
sudo docker image rm -f $(sudo docker image ls -q)

# Build new (without caching, cache doesn't update after not Dockerfile changes)
sudo docker-compose --env-file ./default.env build --no-cache

# Start postgres, messenger at http://localhost:9087 and zabbix at http://localhost:8080
sudo docker-compose --env-file ./default.env up postgres_db messenger monitoring_server -d 
```

Если вы хотите запустить сервер на определённом IP-адресе, то надо отредактировать файл *application-net.properties*, где указываете вместо localhost нужный вам IP-адрес:

```text
messenger.public-url = http://localhost:9087
messenger.websocket.url = ws://localhost:9087/ws
```

Собрать и запустить как раньше, но с указанием другого docker-compose конфигурационного файла:

```bash
sudo docker-compose --env-file ./default.env -f ./docker-compose-net.yml build --no-cache

sudo docker-compose --env-file ./default.env -f ./docker-compose-net.yml up postgres_db messenger monitoring_server -d 
```

</strike>

## Описание предметной области

Анонимный мессенджер с минимальными данными о пользователе - не хранится никакой личной информации, только логин/пароль. Для пользования мессенджером пользователь регистрируется или заходит под уже созданным аккаунтом.

Для входа нужны:

* Логин
* Пароль

Для регистрации нужны:

* Уникальный никнейм (логин)
* Пароль

После аутентификации и авторизации пользователь имеет следующие возможности:

* Создавать собственные чаты
* Приглашать в свои чаты людей (по логину)
* Писать и просматривать текстовые сообщения
* Банить пользователей в чате (если есть роль админа или создателя чата)
* Создатель чата может назначать админами других пользователей
* Удалять свои чаты вместе со всеми сообщениями
* Удаление аккаунта
* Выход из аккаунта

## Описание бизнес процессов

При входе на сайт пользователю отображается страница для аутентификации и авторизации с полями “Логин” и “Пароль”, а также кнопками “Войти” и “Регистрация”.
Для входа в аккаунт, пользователю нужно будет ввести никнейм (логин) и пароль, а потом нажать “Войти” - данные пройдут валидацию и отправятся на сервер для проверки на наличие таких пользователей в БД.

Если пользователь захочет создать новый аккаунт - ему нужно будет нажать на кнопку “Регистрация”, которая перекинет на страницу для регистрации, ввести уникальный логин (которого ещё не существует), пароль и нажать на кнопку “Зарегистрироваться” - данные провалидируются и отправятся на сервер, где будут проверены (уникальность логина и server-side валидация пароля).

После аутентификации и авторизации пользователя перекинет на основную страницу с списком чатов. Здесь пользователь сможет:

* Создать новый чат
* Читать сообщения в уже существующих чатах
* Удалять чаты
* Выйти из аккаунта
* Удалить аккаунт

При нажатии на чат будут загружаться данные о чате и сообщения пользователей.
В чате пользователь сможет:

* Удалять пользователей (если создатель)
* Просматривать сообщения
* Удалить текущий чат(все свои сообщения)

При создании чата будет отображаться соответствующее окно с следующими полями:

* Название чата (Не обязательно уникальное)
* Список людей, которых пользователь собирается пригласить
* Строка поиска, для поиска людей по никнейму

## Стек технологий

Backend: Spring+PostgreSQL

Frontend: Js(Jquery) + Websockets

## Этапы рефакторинга

* Внедрение шифрования по алгоритму ГОСТ
* Переделать под SPA(все на одной странице)
* Минимизировать и нормализовать базу данных
