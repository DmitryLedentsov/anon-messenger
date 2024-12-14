set -x

export SERVICEPATH=/etc/main_service
export INSTALLPATH=${SERVICEPATH}/infra/installation
export APPPATH=${INSTALLPATH}/zabbix

apt-get install -y postgresql-client
apt-get install -y wget

wget https://repo.zabbix.com/zabbix/7.0/debian/pool/main/z/zabbix-release/zabbix-release_latest+debian12_all.deb

dpkg -i zabbix-release_latest+debian12_all.deb

apt-get update -y

apt-get install -y zabbix-server-pgsql zabbix-frontend-php php8.2-pgsql zabbix-nginx-conf zabbix-sql-scripts zabbix-agent

cat ${INSTALLPATH}/zabbix/zabbix_server.conf > /etc/zabbix/zabbix_server.conf
cat ${INSTALLPATH}/zabbix/zabbix_agentd.conf > /etc/zabbix/zabbix_agentd.conf
cat ${INSTALLPATH}/zabbix/php-fpm.conf > /etc/zabbix/php-fpm.conf
cat ${INSTALLPATH}/zabbix/nginx.conf > /etc/zabbix/nginx.conf