# docker container kill $(docker ps -aq)
# docker image rm -f $(docker image ls -aq)
# docker buildx build . --add-host=host:192.167.1.1 --no-cache --network=host -t base -f base.Dockerfile #TODO
# docker buildx build . --add-host=host:192.167.1.2 --no-cache --network=host -t messenger -f messenger.Dockerfile
# docker buildx build . --add-host=host:192.167.1.3 --no-cache --network=host --progress=plain -t pgsql -f pgsql.Dockerfile
# docker buildx build . --add-host=host:192.167.1.4 --no-cache --network=host --progress=plain -t zabbix -f zabbix.Dockerfile
# Go to docker-files/ and execute
sudo docker-compose --env-file ./default.env build --no-cache # build without cache