FROM debian:latest

LABEL maintainer="sasha.atalyan@gmail.com"

SHELL ["/bin/bash", "-c"]

ENV LANG=ru_RU.utf8

EXPOSE 80/tcp
EXPOSE 8080/tcp

RUN apt-get update -y && apt-get install -y locales && rm -rf /var/lib/apt/lists/* \
    && localedef -i ru_RU -c -f UTF-8 -A /usr/share/locale/locale.alias ru_RU.UTF-8

RUN apt-get -y update && apt-get -y upgrade
RUN apt-get install -y wget git
RUN wget https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_linux-x64_bin.tar.gz

RUN tar -xvzf openjdk-21.0.2_linux-x64_bin.tar.gz
RUN wget https://downloads.apache.org/maven/mvnd/1.0.2/maven-mvnd-1.0.2-linux-amd64.tar.gz
RUN tar -xvzf maven-mvnd-1.0.2-linux-amd64.tar.gz
RUN wget https://dlcdn.apache.org/kafka/4.0.0/kafka_2.13-4.0.0.tgz
RUN tar -xvzf kafka_2.13-4.0.0.tgz
ENV PATH=${PATH}:/jdk-21.0.2/bin/:/maven-mvnd-1.0.2-linux-amd64/bin/
RUN apt-get install -y lsb-release
RUN apt-get install -y curl ca-certificates \
    && install -d /usr/share/postgresql-common/pgdg \
    && curl -o /usr/share/postgresql-common/pgdg/apt.postgresql.org.asc --fail https://www.postgresql.org/media/keys/ACCC4CF8.asc \
    && echo "deb [signed-by="/usr/share/postgresql-common/pgdg/apt.postgresql.org.asc"] https://apt.postgresql.org/pub/repos/apt $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list \
    && apt-get update -y \
    && apt-get -y install postgresql-17

ENV PGPASSWORD=athyfylj

RUN echo "source $(pwd)/maven-mvnd-1.0.2-linux-amd64/bin/mvnd-bash-completion.bash" >> ~/.bashrc

RUN echo ${PGPASSWORD} > pwfile.conf && pg_createcluster -p 5435 17 messenger -- --auth=scram-sha-256 --username=postgres --pwfile=$(pwd)/pwfile.conf

RUN apt-get -y install python3
RUN apt-get install -y python3-doit
RUN apt-get install -y python3-autopep8

RUN apt-get install -y iproute2 procps net-tools git
WORKDIR /home/dev
