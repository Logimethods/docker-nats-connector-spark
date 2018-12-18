#!/bin/sh

# https://github.com/gdraheim/docker-copyedit

curl -o docker-copyedit.py https://raw.githubusercontent.com/gdraheim/docker-copyedit/master/docker-copyedit.py
chmod +x docker-copyedit.py

./docker-copyedit.py FROM denvazh/gatling:2.2.2 INTO logimethods/gatling:2.2.2 rm all volumes
#REMOVE ALL VOLUMES
docker image inspect logimethods/gatling:2.2.2 | grep Volumes

docker push logimethods/gatling:2.2.2
