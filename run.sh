# docker run -p 5435:5435 --net=spring-boot --ip=192.167.1.3  -dit pgsql /bin/bash
# sleep 1
# docker run -p 9087:9087 --net=spring-boot --ip=192.167.1.2  -dit messenger /bin/bash
# docker run -p 8080:8080 --net=spring-boot --ip=192.167.1.4  -dit zabbix /bin/bashs
docker-compose --env-file ./docker-files/default.env up postgres_db messenger monitoring_server -d