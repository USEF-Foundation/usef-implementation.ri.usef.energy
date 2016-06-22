#!/bin/bash

mvn clean install -P demo

cd usef-vudp-deployments

for d in */; do echo $d;cd $d; mvn wildfly:deploy ;cd ..; done;

cd ..
