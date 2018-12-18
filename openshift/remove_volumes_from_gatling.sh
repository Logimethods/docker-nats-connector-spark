#!/bin/sh

# https://github.com/gdraheim/docker-copyedit

./docker-copyedit.py \
 FROM denvazh/gatling:2.2.2 INTO logimethods/gatling:2.2.2 REMOVE ALL VOLUMES

docker push logimethods/gatling:2.2.2
