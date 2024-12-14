#!/bin/bash
set -x

apt-get install -y postgresql
export SERVICEPATH=/etc/main_service
export INSTALLPATH=${SERVICEPATH}/infra/installation
export BINPATH=${SERVICEPATH}/bin
export APPPATH=${INSTALLPATH}/postgresql

touch ${APPPATH}/pwfile.conf
echo $PGPASSWORD > ${APPPATH}/pwfile.conf

pg_createcluster -p 5435 15 messenger -- --auth=scram-sha-256 --username=postgres --pwfile=${APPPATH}/pwfile.conf

cat ${APPPATH}/pg_hba.conf > /etc/postgresql/15/messenger/pg_hba.conf
pg_ctlcluster 15 messenger start

cat ${APPPATH}/scripts/zabbix.sql | sed "s/PGPASSWORD/${PGPASSWORD}/" > ${APPPATH}/scripts/zabbix_with_pw.sql

cat ${APPPATH}/scripts/zabbix_with_pw.sql > ${APPPATH}/scripts/zabbix.sql

su -c  "psql -U postgres -d postgres -p 5435 -f ${APPPATH}/scripts/create.sql" postgres

useradd zabbix
su -c "psql -U postgres -d postgres -p 5435 -f ${APPPATH}/scripts/zabbix.sql" postgres
su -c "psql -U zabbix -d zabbix -p 5435 -f ${APPPATH}/scripts/zabbix_server.sql" zabbix
pg_ctlcluster 15 messenger stop

cat ${APPPATH}/postgresql.conf > /etc/postgresql/15/messenger/postgresql.conf
