FROM --platform=amd64 debian

LABEL maintainer="sasha.atalyan@gmail.com"

SHELL ["/bin/bash", "-c"]

ENV SERVICEPATH=/etc/main_service
ENV INSTALLPATH=${SERVICEPATH}/infra/installation
ENV BINPATH=${SERVICEPATH}/bin

RUN mkdir -p ${INSTALLPATH} ${BINPATH}

#copy files to image
COPY ./infra /etc/main_service/infra
COPY ./messanger/target/messanger-0.0.1-SNAPSHOT.jar /etc/main_service/bin

#set env
ENV LANG ru_RU.utf8

RUN apt-get update && apt-get install -y locales && rm -rf /var/lib/apt/lists/* \
	&& localedef -i ru_RU -c -f UTF-8 -A /usr/share/locale/locale.alias ru_RU.UTF-8

#run installation scripts
RUN apt update -y && apt upgrade -y

RUN set -a; source ${INSTALLPATH}/runtime/install.sh; set +a

RUN set -a; source ${INSTALLPATH}/tools/install.sh; set +a

#run configuration scripts
RUN set -a; source ${INSTALLPATH}/postgresql/configure.sh postgresql; set +a

EXPOSE 443/tcp
EXPOSE 80/tcp

# TODO add logs destination for web app
ENTRYPOINT /usr/bin/pg_ctlcluster 15 messenger start && java -jar /etc/main_service/bin/messanger-0.0.1-SNAPSHOT.jar & /bin/bash