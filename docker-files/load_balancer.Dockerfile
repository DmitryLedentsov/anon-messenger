FROM os_base as load_balancer

ARG SERVICEPATH
ARG INSTALLPATH
ARG BINPATH

# EXPOSE 443/tcp
EXPOSE 80/tcp

RUN apt-get install -y nginx && cp /etc/main_service/infra/installation/nginx/nginx.conf /etc/nginx/

# TODO add logs destination for web app
ENTRYPOINT service nginx start & /bin/bash