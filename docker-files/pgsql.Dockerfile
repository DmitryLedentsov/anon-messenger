FROM base as database

ENV SERVICEPATH=/etc/main_service
ENV INSTALLPATH=${SERVICEPATH}/infra/installation
ENV BINPATH=${SERVICEPATH}/bin

#run configuration scripts
RUN set -a; source ${INSTALLPATH}/postgresql/install.sh postgresql; set +a

# EXPOSE 443/tcp
# EXPOSE 80/tcp
EXPOSE 5435/tcp

# TODO add logs destination for web app
ENTRYPOINT /usr/bin/pg_ctlcluster 15 messenger start && su -c "PGPASSWORD='athyfylj' psql -h 192.168.0.3 -U zabbix -d zabbix -p 5435 -f ${INSTALLPATH}/postgresql/scripts/zabbix_server.sql" postgres & /bin/bash