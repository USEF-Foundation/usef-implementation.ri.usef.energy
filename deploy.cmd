@echo on
call mvn clean install -P demo

cd usef-vudp-deployments

FOR /D %%d in ("*") DO (
    echo %%d
    cd %%d
    mvn wildfly:deploy
    cd .. )

cd ..