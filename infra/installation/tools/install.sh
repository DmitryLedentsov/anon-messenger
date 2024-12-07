#!/bin/bash
set -x

apt-get install -y htop iotop-c nftables vim  selinux-basics selinux-policy-default auditd 
apt-get install -y net-tools iptraf-ng iputils-ping iproute2