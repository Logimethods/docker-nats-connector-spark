https://issues.jenkins-ci.org/browse/JENKINS-48300
https://docs.openshift.com/container-platform/3.11/using_images/other_images/jenkins.html#jenkins-environment-variables

```
[0m[[0m[0minfo[0m] [0m[0mUpdating ProjectRef(uri("file:/home/jenkins/workspace/test-build2/test-build2-inject-pipeline/inject/project/"), "inject-build")...[0m
wrapper script does not seem to be touching the log file in /home/jenkins/workspace/test-build2/test-build2-inject-pipeline/inject@tmp/durable-09e4a2b6
(JENKINS-48300: if on a laggy filesystem, consider -Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=300)
```

`JENKINS_JAVA_OVERRIDES=-Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=300`

```
+ exec java -XX:+UseParallelGC -XX:MinHeapFreeRatio=5 -XX:MaxHeapFreeRatio=10 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -Xmx256m -Dfile.encoding=UTF8 -Djavamelody.displayed-counters=log,error -Duser.home=/var/lib/jenkins -Djavamelody.application-name=JENKINS -Dorg.jenkinsci.plugins.durabletask.BourneShellScript.HEARTBEAT_CHECK_INTERVAL=300 -jar /usr/lib/jenkins/jenkins.war
```

https://docs.openshift.com/container-platform/3.11/dev_guide/dev_tutorials/binary_builds.html

oc create -f openshift/BuildConfig

oc new-build --strategy docker --binary --name nats-connector-inject
oc new-build --strategy docker --binary --name nats-connector-app
oc new-build --strategy docker --binary --name nats-connector-monitor

oc policy add-role-to-user admin system:serviceaccount:test-build2:default


------ SPARK Cluster

https://radanalytics.io/howdoi/install-radanalyticsio
https://github.com/radanalyticsio/openshift-spark

oc create -f https://radanalytics.io/resources.yaml

-------- NATS

https://github.com/nats-io/nats-operator

https://medium.com/@xcoulon/fixing-a-crashed-container-on-openshift-8482a4a84bfe

oc apply -f openshift/nats-streaming-deployment.yaml

---------- NATS CONNECTOR

oc apply -f openshift/nats-connector-template.yml


RUN chown -R 1001:0 /opt/gatling && \
    chmod -R g+rw /opt/gatling

USER 1001
