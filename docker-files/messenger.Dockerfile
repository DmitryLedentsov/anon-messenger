FROM base as messenger

ENV APP=messenger-0.0.1-spring-boot.jar
ENV SERVICEPATH=/etc/main_service
ENV INSTALLPATH=${SERVICEPATH}/infra/installation
ENV BINPATH=${SERVICEPATH}/bin

COPY ./${APP} /etc/main_service/bin

# EXPOSE 443/tcp
EXPOSE 80/tcp
EXPOSE 9087/tcp

# TODO add logs destination for web app
ENTRYPOINT java -jar /etc/main_service/bin/${APP} & /bin/bash