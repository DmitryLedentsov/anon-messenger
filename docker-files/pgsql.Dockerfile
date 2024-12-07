FROM base as database

#run configuration scripts
RUN set -a; source ${INSTALLPATH}/postgresql/install.sh postgresql; set +a
RUN ls -l

# EXPOSE 443/tcp
# EXPOSE 80/tcp
EXPOSE 5435/tcp

# TODO add logs destination for web app
ENTRYPOINT /usr/bin/pg_ctlcluster 15 messenger start & /bin/bash