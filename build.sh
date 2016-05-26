pushd inject
sbt docker
popd
pushd app
sbt docker
popd
pushd monitor
sbt docker
popd
