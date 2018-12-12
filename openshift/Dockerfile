FROM openshift/jenkins-slave-base-centos7:v3.11

## https://github.com/dsanchor/openshift-jenkins-slave-sbt/blob/master/centos/Dockerfile
## https://github.com/hseeberger/scala-sbt/blob/master/Dockerfile
## https://github.com/openshift/jenkins/blob/master/agent-maven-3.5/Dockerfile

# ENV SBT_VERSION 1.2.7
ENV PATH=$PATH:/usr/lib/jvm/jre/bin/

# Install Scala
## Piping curl directly in tar
#RUN \
#  curl -fsL https://downloads.typesafe.com/scala/$SCALA_VERSION/scala-$SCALA_VERSION.tgz | tar xfz - -C ~ && \
#  echo >> ~/.bashrc && \
#  echo "export PATH=~/scala-$SCALA_VERSION/bin:$PATH" >> ~/.bashrc

# RUN export PATH="/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.191.b12-1.el7_6.x86_64/jre/bin:$PATH"
#USER root

# Install sbt
# RUN ls -als /usr/bin/
# RUN /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.191.b12-1.el7_6.x86_64/jre/bin/java -version
# RUN  curl https://bintray.com/sbt/rpm/rpm | tee /etc/yum.repos.d/bintray-sbt-rpm.repo
# RUN INSTALL_PKGS="java-1.8.0-openjdk-devel.x86_64" && \
#     x86_EXTRA_RPMS=$(if [ "$(uname -m)" == "x86_64" ]; then echo -n java-1.8.0-openjdk-devel.i686 ; fi)

RUN  curl https://bintray.com/sbt/rpm/rpm | tee /etc/yum.repos.d/bintray-sbt-rpm.repo && \
     yum -y install sbt-1.2.7 && \
     sbt sbtVersion

# https://bintray.com/sbt/rpm/rpm

#RUN chown -R 1001:0 $HOME && \
#    chmod -R g+rw $HOME

#USER 1001