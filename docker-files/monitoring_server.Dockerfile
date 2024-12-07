FROM os_base

ARG SERVICEPATH
ARG INSTALLPATH
ARG BINPATH
ARG PGPASSWORD
# EXPOSE 443/tcp
# EXPOSE 80/tcp
EXPOSE 8080/tcp

RUN set -a; source ${INSTALLPATH}/zabbix/install.sh; set +a


# TODO add logs destination for web app
ENTRYPOINT service zabbix-server restart && service zabbix-agent restart && service nginx restart && service php8.2-fpm restart & /bin/bash