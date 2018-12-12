FROM openshift/jenkins-slave-maven-centos7:v3.11

## https://github.com/dsanchor/openshift-jenkins-slave-sbt/blob/master/centos/Dockerfile
## https://github.com/hseeberger/scala-sbt/blob/master/Dockerfile
## https://github.com/openshift/jenkins/blob/master/agent-maven-3.5/Dockerfile

USER root

# Install sbt

# https://bintray.com/sbt/rpm/rpm
RUN  curl https://bintray.com/sbt/rpm/rpm | tee /etc/yum.repos.d/bintray-sbt-rpm.repo && \
     yum -y install sbt-1.2.7 && \
     sbt sbtVersion

# RUN chown -R 1001:0 $HOME && \
#    chmod -R g+rw $HOME

USER 1001