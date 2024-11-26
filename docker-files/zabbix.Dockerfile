FROM base as database

ENV SERVICEPATH=/etc/main_service
ENV INSTALLPATH=${SERVICEPATH}/infra/installation
ENV BINPATH=${SERVICEPATH}/bin

# EXPOSE 443/tcp
# EXPOSE 80/tcp
EXPOSE 8080/tcp

RUN set -a; source ${INSTALLPATH}/zabbix/install.sh; set +a


# TODO add logs destination for web app
ENTRYPOINT service zabbix-server restart && service zabbix-agent restart && service nginx start && service php8.2-fpm start & /bin/bash