# Jakarta EE EJB XA

## Podman environment

The quickest way to test this demo is to use the docker-compose file and start all services.

```
podman compose up
```

After starting the containers, the following services will be published:

| Port | Path | Description                                                                       | Example                                   |
|------| ---- |-----------------------------------------------------------------------------------|-------------------------------------------|
| 8080 | /api/remote | Checks whether services communicate successfully                                  | curl -s http://localhost:8080/api/remote  |
| 8080 | /api/listAll | List all registries in the service-a local database                               | curl -s http://localhost:8080/api/listAll |
| 8180 | /api/listAll | List all registries in the service-b local database                               | curl -s http://localhost:8180/api/listAll |
| 8280 | /api/listAll | List all registries in the service-c local database                               | curl -s http://localhost:8280/api/listAll |
| 8080 | /api/process-all | Creates a new registry in each of the services                                    | curl -s -X POST http://localhost:8080/api/process-all |
| 8080 | /api/failOnC | Forces service-c to fail causing a rollback of service-b and the interruption of service-a | curl -s -X POST http://localhost:8080/api/failOnC |
| 8080 | /api/failOnAafterBandC | Forces an error in service-a after the transaction has been executed in service-b and service-c | curl -s -X POST http://localhost:8080/api/failOnAafterBandC |


## Local environment

### Compile and install the entire project `./mvnw clean install -DskipTests`
### Compile only the service-a module `./mvnw -pl service-a compile`
### Compile service-b and also it's dependencies `./mvnw -am -pl service-b compile`
### Wildfly deploy only the service-a `./mvnw -pl service-a wildfly:deploy`

### Define project root variable
```
export PROJECT_ROOT=$(pwd)
```

### Define servers root variable
Choose a location to download and configure your wildfly instances. For example ~/servers
```
mkdir ~/servers
cd ~/servers
export SERVERS_ROOT=$(pwd)
```

### Create env.properties
```
printenv > $SERVERS_ROOT/env.properties
```

### Start databases
```
podman compose -f $PROJECT_ROOT/docker-compose-databases.yml up
```

### Download wildfly
```
wget -P $SERVERS_ROOT https://github.com/wildfly/wildfly/releases/download/28.0.1.Final/wildfly-28.0.1.Final.zip
```

### Jdbc driver download for postgres
```
wget -P $SERVERS_ROOT https://jdbc.postgresql.org/download/postgresql-42.7.3.jar
```

### Jdbc driver download for mysql
```
wget -P $SERVERS_ROOT https://downloads.mysql.com/archives/get/p/3/file/mysql-connector-j-8.3.0.tar.gz
tar -xf $SERVERS_ROOT/mysql-connector-j-8.3.0.tar.gz mysql-connector-j-8.3.0/mysql-connector-j-8.3.0.jar
```

### Configure three instances
```
unzip $SERVERS_ROOT/wildfly-28.0.1.Final.zip -d $SERVERS_ROOT
mv $SERVERS_ROOT/wildfly-28.0.1.Final $SERVERS_ROOT/server-a
cp -r $SERVERS_ROOT/server-a $SERVERS_ROOT/server-b
cp -r $SERVERS_ROOT/server-a $SERVERS_ROOT/server-c
```

### Create administration and application user
```
$PROJECT_ROOT/create-users.sh
```

### Configure server-a
```
cd server-a/bin
./standalone.sh -c standalone.xml -Djboss.tx.node.id=servicea -Djboss.node.name=servicea
```
#### In a second terminal
```
$SERVERS_ROOT/server-a/bin/jboss-cli.sh -c --file=$PROJECT_ROOT/configure-server-a.cli --properties=$SERVERS_ROOT/env.properties
```

### Configure server-b
```
cd server-b/bin
./standalone.sh -c standalone.xml -Djboss.tx.node.id=serviceb -Djboss.node.name=serviceb -Djboss.socket.binding.port-offset=100
```
#### In a second terminal
```
$SERVERS_ROOT/server-b/bin/jboss-cli.sh -c --file=$PROJECT_ROOT/configure-server-b.cli --properties=$SERVERS_ROOT/env.properties
```


In a second terminal
```
cd server-b
cd bin
./jboss-cli.sh --connect --controller=localhost:10090

:write-attribute(name=name,value=service-b)

module add --name=com.mysql --resources=$SERVERS_ROOT/mysql-connector-j-8.3.0/mysql-connector-j-8.3.0.jar --dependencies=java.se,jakarta.transaction.api

/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=com.mysql,driver-xa-datasource-class-name=com.mysql.cj.jdbc.MysqlXADataSource)

xa-data-source add --name=mysqlXA --jndi-name=java:jboss/datasources/mysqldsXA --driver-name=mysql --xa-datasource-class=com.mysql.cj.jdbc.MysqlXADataSource --user-name=wildfly --password=password --min-pool-size=1 --max-pool-size=100 --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker --validate-on-match=true --background-validation=false --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter --xa-datasource-properties={"ServerName"=>"localhost","DatabaseName"=>"service","PortNumber"=>"3306"}

/subsystem=datasources/xa-data-source=postgresqlXA/statistics=jdbc:write-attribute(name=statistics-enabled,value=true)

/subsystem=datasources/xa-data-source=postgresqlXA/statistics=pool:write-attribute(name=statistics-enabled,value=true)

/subsystem=transactions:write-attribute(name=recovery-listener,value=true)

/subsystem=elytron/authentication-configuration=ejb-user:add(authentication-name=wildfly, authorization-name=wildfly, credential-reference={clear-text=R3dH4t1!}, realm="ApplicationRealm", sasl-mechanism-selector="DIGEST-MD5")

/subsystem=elytron/authentication-context=ejb-context:add(match-rules=[{authentication-configuration=ejb-user}])

Local -->
    /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=remote-ejb-service-c:add(host=localhost, port=8280)
Container -->
    /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=remote-ejb-service-c:add(host=servicec, port=8080)

/subsystem=remoting/remote-outbound-connection=remote-ejb-service-c-connection:add(outbound-socket-binding-ref=remote-ejb-service-c, authentication-context=ejb-context)

/subsystem=remoting/remote-outbound-connection=remote-ejb-service-c-connection/property=SASL_POLICY_NOANONYMOUS:add(value=false)

/subsystem=remoting/remote-outbound-connection=remote-ejb-service-c-connection/property=SSL_ENABLED:add(value=false)

/subsystem=remoting/remote-outbound-connection=remote-ejb-service-c-connection/property=SASL_DISALLOWED_MECHANISMS:add(value=JBOSS-LOCAL-USER)

podman build -t quay.io/demo-ejb-wildfly-xa/service-b:latest .
podman login -u='demo-ejb-wildfly-xa+default' -p='UQ723QXJCG42RPKE1YEU8RFQ7QSLYL77JKKPZYLE61AJO3XTB0PV2KZWGDKK0IKQ' quay.io
podman push quay.io/demo-ejb-wildfly-xa/service-b:latest
```