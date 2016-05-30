docker stop $(docker ps -a | awk '{print $1;}')
docker rm $(docker ps -a | awk '{print $1;}')
docker ps -a
