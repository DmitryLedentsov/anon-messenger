docker container kill $(docker ps -aq)
docker image rm -f $(docker image ls -aq)
docker buildx build . --add-host=host:192.168.0.1 --no-cache --network=host -t base -f base.Dockerfile #TODO
docker buildx build . --add-host=host:192.168.0.2 --no-cache --network=host -t messenger -f messenger.Dockerfile
docker buildx build . --add-host=host:192.168.0.3 --no-cache --network=host --progress=plain -t pgsql -f pgsql.Dockerfile
docker buildx build . --add-host=host:192.168.0.4 --no-cache --network=host --progress=plain -t zabbix -f zabbix.Dockerfile