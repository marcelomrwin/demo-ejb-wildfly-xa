FROM quay.io/wildfly/wildfly-runtime:latest-jdk17
COPY --chown=jboss:root server $JBOSS_HOME
RUN chmod -R ug+rwX $JBOSS_HOME
COPY --chown=jboss:root ROOT.war $JBOSS_HOME/standalone/deployments/ROOT.war