FROM os_base as postgres_db


ARG SERVICEPATH
ARG INSTALLPATH
ARG BINPATH
ARG PGPASSWORD

#run configuration scripts
RUN set -a; source ${INSTALLPATH}/postgresql/install.sh postgresql; set +a

# EXPOSE 443/tcp
# EXPOSE 80/tcp
EXPOSE 5435/tcp

# TODO add logs destination for web app
ENTRYPOINT /usr/bin/pg_ctlcluster 15 messenger start && su -c "PGPASSWORD=${PGPASSWORD} psql -h 192.167.1.3 -U zabbix -d zabbix -p 5435 -f ${INSTALLPATH}/postgresql/scripts/zabbix_server.sql" postgres & /bin/bash