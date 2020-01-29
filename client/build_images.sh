#!/usr/bin/env bash
#
# build images locally

echo "building master v1"
sudo docker build -t van15h/master:v1 ../Executables/master

echo "building slave v1"
sudo docker build -t van15h/slave:v1 ../Executables/slave

echo "check if both images built"
sudo docker images | grep 'master\|slave'