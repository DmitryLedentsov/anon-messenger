#!/bin/bash
set -x

apt-get install -y htop iotop-c nftables selinux-basics selinux-policy-default auditd ecryptfs-utils
apt-get install -y net-tools iptraf-ng iputils-ping iproute2