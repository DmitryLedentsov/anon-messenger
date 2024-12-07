FROM --platform=amd64 debian as base

LABEL maintainer="sasha.atalyan@gmail.com"

SHELL ["/bin/bash", "-c"]

ENV SERVICEPATH=/etc/main_service
ENV INSTALLPATH=${SERVICEPATH}/infra/installation
ENV BINPATH=${SERVICEPATH}/bin

RUN mkdir -p ${INSTALLPATH} ${BINPATH}

#copy files to image
COPY ./infra /etc/main_service/infra

#set env
ENV LANG ru_RU.utf8

RUN apt-get update && apt-get install -y locales && rm -rf /var/lib/apt/lists/* \
	&& localedef -i ru_RU -c -f UTF-8 -A /usr/share/locale/locale.alias ru_RU.UTF-8

#run installation scripts
RUN apt-get update -y && apt-get upgrade -y

RUN set -a; source ${INSTALLPATH}/runtime/install.sh; set +a

RUN set -a; source ${INSTALLPATH}/tools/install.sh; set +a

