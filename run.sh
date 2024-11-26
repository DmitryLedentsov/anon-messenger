docker run -p 5435:5435 --net=spring-boot --ip=192.168.0.3  -dit pgsql /bin/bash
sleep 1
docker run -p 9087:9087 --net=spring-boot --ip=192.168.0.2  -dit messenger /bin/bash
docker run -p 8080:8080 --net=spring-boot --ip=192.168.0.4  -dit zabbix /bin/bash