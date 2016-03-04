## RMI init


### Start Process
rmiregistry

### Compile
javac -d chat Channel.java ChatServer.java chatClient.java IChatClient.java IChatServer.java Topico.java Message.java 

### Start Server
java -classpath ./chat -Djava.rmi.server.codebase=file:./chat Server.ChatServer  &

### Start Client
java  -classpath . Client.chatClient [client] [ip]

*********

Una vez que el cliente ha accedido, se mostrará una pantalla como la siguiente:

cliente:

al escribir h se mostrará un menú de ayuda:

---------------------------------
 Canales: 
 s : Suscribirte a un canal. 
 q : Desuscribirte a un canal. 
 d : Muestra canales. 
---------------------------------
 Topicos: 
 S : Suscribirte a un tópico.
 Q : Desuscribirte a un tópico. 
 D : Muestra tópico. 
---------------------------------
 Log: 
 l : Log de topico   
 exit : Salir 
---------------------------------

Para suscribirse a un canal con un topico basta escribir

cliente: S [canal] [topico]
