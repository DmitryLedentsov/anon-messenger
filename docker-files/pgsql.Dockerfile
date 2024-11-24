FROM base as database

ENV APP=messenger-0.0.1-spring-boot.jar
ENV SERVICEPATH=/etc/main_service
ENV INSTALLPATH=${SERVICEPATH}/infra/installation
ENV BINPATH=${SERVICEPATH}/bin

#run configuration scripts
RUN set -a; source ${INSTALLPATH}/postgresql/install.sh postgresql; set +a

# EXPOSE 443/tcp
# EXPOSE 80/tcp
EXPOSE 5435/tcp

# TODO add logs destination for web app
ENTRYPOINT /usr/bin/pg_ctlcluster 15 messenger start & /bin/bash