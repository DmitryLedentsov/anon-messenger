#!/bin/bash
set -x

apt-get update && apt-get upgrade
apt-get install -y openjdk-17-jre
apt-get install -y mvn
apt-get install -y postgresql