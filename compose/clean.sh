docker stop $(docker ps -q)
docker rm -v $(docker ps -a -q)
docker volume rm $(docker volume ls -qf dangling=true)
docker rmi $(docker images | grep "^<none>" | awk "{print $3}")
docker rmi $(docker images | grep "nats-connector-spark" | awk "{print $3}")

