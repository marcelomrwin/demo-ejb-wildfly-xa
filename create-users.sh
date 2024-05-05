#!/bin/bash

$SERVERS_ROOT/server-a/bin/add-user.sh -u wildfly -p R3dH4t1!
$SERVERS_ROOT/server-b/bin/add-user.sh -u wildfly -p R3dH4t1!
$SERVERS_ROOT/server-c/bin/add-user.sh -u wildfly -p R3dH4t1!

$SERVERS_ROOT/server-a/bin/add-user.sh -a -u wildfly -p R3dH4t1!
$SERVERS_ROOT/server-b/bin/add-user.sh -a -u wildfly -p R3dH4t1!
$SERVERS_ROOT/server-c/bin/add-user.sh -a -u wildfly -p R3dH4t1!