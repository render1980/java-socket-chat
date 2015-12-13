# java-socket-chat

Chat on Java using Sockets

## Start

### Start derby db server and create schema

Run createdb script on host with derby db instance

<pre>
./createdb.sh
</pre>

### Start client chat instance

Define DBHost param in data.xml (if db instance is on remote host)

Data.xml params

* Host - client host
* Port - client listening port
* DBHost - derby db host
* DBPort - derby db listening port

Run start script on client host

<pre>
./start-client.sh
</pre>
