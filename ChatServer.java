//===============================================================
// Server
//===============================================================


package Server;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import Interfaces.*;
import Mensaje.*;
import Channel.*;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;



public class ChatServer extends UnicastRemoteObject implements IChatServer {
    // Hashtables for channels and clients.
    Hashtable<String, Channel>     myChannels    = new Hashtable <String, Channel>(); 
    Hashtable<String, IChatClient> myClients     = new Hashtable <String, IChatClient>();
    
    
    public ChatServer() throws RemoteException {
       Channel Default = new Channel("Default");
       Topico def      = new Topico("Default", "Default");
       Default.addTopic("Default", def);
    }
    
    @Override
    public synchronized void login(String nameCliente, IChatClient nc) throws RemoteException {
        if(!myClients.contains(nameCliente)){
            myClients.put(nameCliente, nc);
            loginChannel(nameCliente, nc, "Default");
            Message mensaje = new Message("Servidor", "Creando usuario y entrando", "Default", "Default");
            sendUnicast(mensaje, nameCliente);
            System.out.println("Client " + nameCliente + " has logged." );
        }else{
            Message mensaje = new Message("Servidor", "No se pudo crear usuario", "Default", "Default");
            sendUnicast(mensaje, nameCliente);
        }  
    }

    @Override
    public synchronized void logout(String nameC) throws RemoteException {
        if(myClients.containsKey(nameC)){
            myClients.remove(nameC);
            Message mensaje = new Message("Servidor", "Usuario salida", "Default", "Default");
            sendUnicast(mensaje, nameC);
            System.out.println("Client " + nameC + " has logged out." );
        }else{
            Message mensaje = new Message("Servidor", "No se pudo quitar usuario", "Default", "Default");
            sendUnicast(mensaje, nameC);
        }    
    }

    @Override
    public synchronized void loginChannel(String nameCliente, IChatClient nc, String canal) throws RemoteException {
        if(myChannels.containsKey(canal)){
            Channel aux = myChannels.get(canal);
            aux.addClient(nameCliente, nc);
            subscribeToTopic(nameCliente, nc, "Default", canal);
            Message mensaje = new Message("Servidor", "Canal existente, agregando usuario: " + nameCliente, canal, "Default");
            sendUnicast(mensaje, nameCliente);
        }else{
            Channel nuevo = new Channel(canal);
            nuevo.addClient(nameCliente, nc);
            myChannels.put(canal, nuevo);
            subscribeToTopic(nameCliente, nc, "Default", canal);
            Message mensaje = new Message("Tu servidor", "Canal creado, agregando usuario: " + nameCliente, canal, "Default");
            sendUnicast(mensaje, nameCliente);    
        }
        Channel auxiliar = myChannels.get(canal);
        Enumeration entChater = auxiliar.getClients().elements();
        while (entChater.hasMoreElements()) {
            ((IChatClient) entChater.nextElement()).receiveSubscribeToChannel(nameCliente, canal);
        }
        System.out.println("Client " + nameCliente + " has logged in Channel: " + canal+ ".");
    }

    @Override
     public synchronized void logoutChannel(String nameC, String canal) throws RemoteException {
        if(myChannels.containsKey(canal)){
            Channel aux = myChannels.get(canal);
            Enumeration topicosCanal = aux.getTopics().elements();
            while(topicosCanal.hasMoreElements()){
                ((Topico)topicosCanal.nextElement()).removeClient(nameC);
            }
            aux.removeClient(nameC);
            Channel auxiliar = myChannels.get(canal);
            Enumeration entChater = auxiliar.getClients().elements();
            while (entChater.hasMoreElements()) {
                ((IChatClient) entChater.nextElement()).receiveUnsubscribeToChannel(nameC, canal);
            }
        }
        System.out.println("Client " + nameC + " has logged out from Channel: " + canal + ".");  
    }
    
    @Override
      public synchronized void subscribeToTopic(String name, IChatClient newClient, String topico, String canal) throws RemoteException {
        if (myChannels.containsKey(canal)) {
            Channel auxiliar = myChannels.get(canal);
            boolean res = false;
            if(auxiliar.containsTopico(topico) && !auxiliar.getTopico(topico).containsClient(name)) {
                myChannels.get(canal).getTopico(topico).addClient(name, newClient);
                res = true;
            }
            if(!auxiliar.containsTopico(topico) ) {
                Topico nuevoT = new Topico(topico, canal); 
                myChannels.get(canal).addTopic(topico, nuevoT);
                myChannels.get(canal).getTopico(topico).addClient(name, newClient);
                res = true;
            }
            if(res){
                Enumeration entChater = myChannels.get(canal).getTopico(topico).getClients().elements();
                while (entChater.hasMoreElements()) {
                    ((IChatClient) entChater.nextElement()).receiveSubscribeToTopic(name, canal, topico);
                }
                System.out.println("Se agrego: "+ name+ " a topico: " + canal + ":" + topico);
            }else{
                System.out.println("No se pudo agregar el topico " + canal + ":" + topico);
            }
            
        }else{
            System.out.println("No se pudo agregar al canal " + canal);
        }     
    }
    
    @Override
    public synchronized void unsubscribeToTopic(String name, String topico, String canal) throws RemoteException {
        if(myChannels.containsKey(canal)){
            Channel aux = myChannels.get(canal);
            if(aux.containsTopico(topico)){
                myChannels.get(canal).getTopico(topico).removeClient(name);
            }
            Enumeration entChater = aux.getTopico(topico).getClients().elements();
            while (entChater.hasMoreElements()) {
                ((IChatClient) entChater.nextElement()).receiveUnsubscribeToChannel(name, canal);
            }
            System.out.println("Se desagrego: "+ name+ " a topico: " + canal + "_"+topico);
        }
        
    }
    
     
     
    @Override
    public synchronized void sendUnicast(Message message, String toWhom) throws RemoteException {
        if(myChannels.containsKey(message.canal)){
            Channel aux = myChannels.get(message.canal);
            if(aux.containsTopico(message.topic)){
                IChatClient dd = aux.getIChatClient(toWhom);
                if(dd != null){
                    dd.receiveMessage(message);
                    aux.getTopico(message.topic).agregaMensaje(message);
                    System.out.println("Message from client " + message.name + " Canal:Topico: " + message.canal + ":"+ message.topic +":\n" + message.text);
                }
            }
        }
    }
    
    @Override
    public synchronized void sendBroadcast(Message message) throws RemoteException {
        
        if(myChannels.containsKey(message.canal)){
           Channel aux = myChannels.get(message.canal);
           if (aux.containsTopico(message.topic)) {
                if(aux.getTopico(message.topic).getClients().containsKey(message.name)){
                    Enumeration entChater = aux.getTopico(message.topic).getClients().elements();
                    while (entChater.hasMoreElements()) {
                        ((IChatClient) entChater.nextElement()).receiveMessage(message);
                    }
                    aux.getTopico(message.topic).agregaMensaje(message);
                    System.out.println("Message from client " + message.name + " Topico: " + message.canal + ":"+ message.topic +":\n" + message.text);
                }  
           }
        }     
    }
    
    public synchronized void getLog(String name, String canal, String topico)throws RemoteException{
        String text = "";
        if(myChannels.containsKey(canal)){
            Channel aux = myChannels.get(canal);
            if(aux.containsTopico(topico)){
                text = aux.getTopico(topico).getLog();
                Message mensaje = new Message("Server", text, "Default", "Default");
                sendUnicast(mensaje, name);
            }
        }
        
    }
    @Override
    public synchronized void getAllTopics(String name, String canal) throws RemoteException {
        String text = "";
        if(myChannels.containsKey(canal)){
            Enumeration entChater = myChannels.get(canal).getTopics().elements();
            while (entChater.hasMoreElements()) {
                    text = text + ((Topico) entChater.nextElement()).nombre + ",";
            }
            
            Message m = new Message("Server",text, canal, "Default");
            Channel aux = myChannels.get(canal);
            aux.getTopico(m.topic).agregaMensaje(m);
            myClients.get(name).receiveMessage(m);
        }
        
        
    }
    
    @Override
    public synchronized void getAllChannels(String name)throws RemoteException {
        String text = "";        
        Enumeration entChater = myChannels.elements();
        while (entChater.hasMoreElements()) {
            text = text + ((Channel) entChater.nextElement()).name + ",";
        }
        Message m = new Message("Server",text, "Default", "Default");
        Channel aux = myChannels.get("Default");
        aux.getTopico(m.topic).agregaMensaje(m);
        myClients.get(name).receiveMessage(m);
    }

    public static void main(String[] args) {

        System.setProperty("java.security.policy", "file:/home/luis/Documents/ITAM/spring_2016/distribuidos/proy1/server.policy");
        String downloadLocation = "file:/home/luis/Documents/ITAM/spring_2016/distribuidos/proy1/";
        String serverURL = "///ChatServer";
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            LocateRegistry.createRegistry(52365);
            //System.setProperty("java.rmi.server.codebase", downloadLocation);
            ChatServer server = new ChatServer();
            Naming.rebind(serverURL, server);
            System.out.println("Chat server ready");
        } catch (RemoteException | MalformedURLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
