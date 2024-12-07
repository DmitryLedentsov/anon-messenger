#!/bin/bash
set -x

apt-get install -y postgresql

export APPPATH=${INSTALLPATH}/${1}

pg_createcluster -p 5435 15 messenger -- --auth=md5 --username=postgres --pwfile=${APPPATH}/pwfile.conf

cat ${APPPATH}/pg_hba.conf > /etc/postgresql/15/messenger/pg_hba.conf
cat ${APPPATH}/postgresql.conf > /etc/postgresql/15/messenger/postgresql.conf
pg_ctlcluster 15 messenger start
su -c  "psql -U postgres -p 5435 -f ${APPPATH}/scripts/create.sql" postgres
pg_ctlcluster 15 messenger stop
