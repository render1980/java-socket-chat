#! /bin/bash
mvn clean install && java -cp ./socket-chat-0.0.1-SNAPSHOT.jar client.Client
