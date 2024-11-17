#!/bin/bash
set -x
export APPPATH=${INSTALLPATH}/${1}

pg_createcluster -p 5435 15 messenger -- --auth=md5 --username=postgres --pwfile=${APPPATH}/pwfile.conf

cp ${APPPATH}/pg_hba.conf /etc/postgresql/15/messenger/
pg_ctlcluster 15 messenger start
su -c  "psql -U postgres -p 5435 -f ${APPPATH}/scripts/create.sql" postgres
pg_ctlcluster 15 messenger stop
