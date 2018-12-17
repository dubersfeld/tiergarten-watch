#!/bin/bash

echo $1

java -jar target/customer-server-0.0.1-SNAPSHOT.jar --server.port=$1 &
disown
