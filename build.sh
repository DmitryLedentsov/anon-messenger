# docker container kill $(docker ps -aq)
docker image rm -f $(docker image ls -aq)
docker buildx build . -t debian --add-host=host:10.10.10.10 --network=host #TODO