@echo off
mvn clean install -P demo

cd usef-vudp-deployments

FOR /D %%d in ("*") DO (
    echo %%;
    cd %%d
    mvn wildfly:deploy
    cd .. )

cd ..
