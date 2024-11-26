#!/bin/bash
set -x

apt-get install -y htop iotop-c 
apt-get install -y selinux-basics selinux-policy-default auditd ecryptfs-utils
apt-get install -y net-tools iptraf-ng iputils-ping iproute2 nftables