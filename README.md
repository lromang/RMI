## RMI init


### Start Process
rmiregistry

### Compile
javac -d chat Channel.java ChatServer.java chatClient.java IChatClient.java IChatServer.java Topico.java Message.java 

### Start Server
java -classpath ./chat -Djava.rmi.server.codebase=file:./chat Server.ChatServer  &

### Start Client
java  -classpath . Client.chatClient Luis 10.105.192.12
