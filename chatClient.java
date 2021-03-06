/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import Mensaje.*;
import Interfaces.*;
import java.net.MalformedURLException;

public class chatClient extends UnicastRemoteObject implements IChatClient {

    public String name;
    IChatServer server;
    String serverURL;

    public chatClient(String name, String url) throws RemoteException {
        this.name = name;
        serverURL = url;
        connect();
    }

    private void connect() {
        try {
            server = (IChatServer) Naming.lookup("rmi://" + serverURL + "/ChatServer");
            server.login(name, this); // callback object
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            System.out.println(e.getMessage());
        }
    }

    private void disconnect() {
        try {
            server.logout(name);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendTextToChat(String text, String canal, String topico) {
        try {
            server.sendBroadcast(new Message(name, text, canal, topico));
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }
 
    private void addToTopic(String name, chatClient cliente, String topico, String canal){
        try{
            server.subscribeToTopic(name, cliente, topico, canal);
        }catch (RemoteException e) {
            System.out.println(e.getMessage());
        } 
        System.out.println("Cliente:" + name + " \n Ingresa: \n -canal:" + canal + "\n -topico:" + topico);
    }
    
    private void deleteFromTopic(String name, String topico, String canal){
        try{
            server.unsubscribeToTopic(name, topico, canal);
        }catch(RemoteException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Cliente:" + name + " \n Abandona: \n -canal:" + canal + "\n -topico:" + topico);
    }
    
    private void getAllTopics(String canal){
        try{
            server.getAllTopics(name, canal);
        }catch(RemoteException e){
            System.out.println(e.getMessage());
        }
    }
    
    private void getAllChannels(){
        try{
            server.getAllChannels(name);
        }catch(RemoteException e){
            System.out.println(e.getMessage());
        }
    }
    
    private void getLog(String canal, String topico){
        try{
            server.getLog(name, canal, topico);
        }catch(RemoteException e){
            System.out.println(e.getMessage());
        }
    }
     
    private void addToChannel(String name, chatClient cliente, String canal){
        try{
            server.loginChannel(name, (IChatClient)cliente, canal);
            System.out.println("Cliente:" + name + " \n Ingresa: \n -canal:" + canal );
        }catch (RemoteException e) {
            System.out.println(e.getMessage());
        } 
        
    }
    
    private void deleteFromChannel(String name, String canal){
        try{
            server.logoutChannel(name, canal);
            System.out.println("Cliente:" + name + " \n Abandona: \n -canal:" + canal );
        }catch(RemoteException e){
            System.out.println(e.getMessage());
        }
        
    }

    @Override
    public void receiveEnter(String name) {
        System.out.println("\nLog in " + name );
    }

    @Override
    public void receiveExit(String name) {
        System.out.println("\nLog out " + name);
        if (name.equals(this.name)) {
            System.exit(0);
        } 
    }
    
    @Override
    public void receiveSubscribeToChannel(String name, String canal){
         System.out.println("Se agrego: "+ name + " a canal: " + canal);
    }
    
    @Override
    public void receiveUnsubscribeToChannel(String name, String canal){
         System.out.println("Se quitó: "+ name+ " de canal: " + canal);
    }
    
    @Override
    public void receiveSubscribeToTopic(String name, String canal, String topico){
         System.out.println("Se agrego: "+ name + " a topico: " + canal + ":" + topico);
    }
    
    @Override
    public void receiveUnsubscribeToTopic(String name, String canal, String topico){
         System.out.println("Se quitó: "+ name+ " de topico: " + canal + ":" + topico);
    }

    @Override
    public void receiveMessage(Message message) {
        if(!message.name.equals(name)){
            System.out.println(message.canal + ":" + message.topic + ", " + message.name + " escribió : " + message.text + "\n");
        }  
    }

    public static String pideCadena(String letrero) {
        StringBuffer strDato = new StringBuffer();
        String strCadena = "";
        try {
            System.out.print(letrero);
            BufferedInputStream bin = new BufferedInputStream(System.in);
            byte bArray[] = new byte[256];
            int numCaracteres = bin.read(bArray);
            while (numCaracteres == 1 && bArray[0] < 32) {
                numCaracteres = bin.read(bArray);
            }
            for (int i = 0; bArray[i] != 13 && bArray[i] != 10 && bArray[i] != 0; i++) {
                strDato.append((char) bArray[i]);
            }
            strCadena = new String(strDato);
        } catch (IOException ioe) {
            System.out.println(ioe.toString());
        }
        return strCadena;
    }

    public static void main(String[] args) {
        String strCad;
        try {
            String name=args[0];
            
            chatClient clte = new chatClient(name, args[1]);
            
            String menuCanal =  " Canales: \n s : Suscribirte a un canal. \n q : Desuscribirte a un canal. \n d : Muestra canales. ";
            String menuTopico = " Topicos: \n S : Suscribirte a un tópico.\n Q : Desuscribirte a un tópico. \n D : Muestra tópico. ";
            String menuNormal = " Log: \n l : Log de topico   \n exit : Salir ";

            
            strCad = pideCadena(name + ": \n");
            while (!strCad.equals("exit")) {
                char entrada = strCad.charAt(0);
                String aux;
                switch(entrada){
                case 'h':
                    System.out.println("---------------------------------");
                    System.out.println(menuCanal);
                    System.out.println("---------------------------------");
                    System.out.println(menuTopico);
                    System.out.println("---------------------------------");
                    System.out.println(menuNormal);
                    System.out.println("---------------------------------");
                    strCad = pideCadena(name + ": \n");
                    break;
                    
                case 's'://Suscribirse a un canal
                    aux = strCad.substring(1);
                        aux = aux.trim();
                        if(aux.length() != 0){
                            clte.addToChannel(name, clte, aux);
                        }else{
                            System.out.println("Estructura comando: \n s [canal]");
                        }
                        strCad = pideCadena(name + ": \n");
                        break;
                        
                    case 'q': //Desuscribirse de un canal
                        aux = strCad.substring(1);
                        aux = aux.trim();
                        if(aux.length() != 0){
                            clte.deleteFromChannel(name, aux);
                        }else{
                            System.out.println("Estructura comando: \n q [canal]");
                        }
                        strCad = pideCadena(name + ": \n");
                        break;
                        
                    case 'd': //Muestra los canales
                        clte.getAllChannels();
                        strCad = pideCadena(name + ": \n");
                        break;
                    
                    case 'S': //Suscribirse a un topico
                        String[] aux2 = strCad.split(" ");
                        if (aux2.length >= 3){
                            clte.addToTopic(name, clte, aux2[2].trim(), aux2[1].trim());
                        }else{
                             System.out.println("Estructura comando: \n s [canal] [topico]");
                        }
                        strCad = pideCadena(name + ": \n");
                        break;
                    
                    case 'Q': //desuscribirse de un topico
                        String[] aux3 = strCad.split(" ");
                        if (aux3.length >= 3){
                            clte.deleteFromTopic(name, aux3[2].trim(), aux3[1].trim());
                        }else{
                             System.out.println("Estructura comando: \n s [canal] [topico]");
                        }
                        strCad = pideCadena(name + ": \n");
                        break;
                    
                    case 'D': //Muestra topicos
                        String aux4 = strCad.substring(1);
                        aux = aux4.trim();
                        if(aux.length() != 0){
                            clte.getAllTopics(aux);
                        }else{
                            System.out.println("Estructura comando: \n s [canal]");
                        }
                        strCad = pideCadena(name + ": \n");
                        break;
                        
                    case 'l': //log del topico
                        String[] aux5 = strCad.split(" ");
                        if (aux5.length >= 3){
                            clte.getLog(aux5[1].trim(), aux5[2].trim());
                        }else{
                             System.out.println("Estructura comando: \n s [canal] [topico]");
                        }
                        strCad = pideCadena(name + ": \n");
                        break;
                    default: //escribir mensaje a chat
                        String[] aux6 = strCad.split(":");
                        if (aux6.length >= 3){
                            clte.sendTextToChat(strCad, aux6[0].trim(),aux6[1].trim());
                        }else{
                            clte.sendTextToChat(strCad, "def", "def");
                        }
                        strCad = pideCadena(name + ": \n");
                        break;
                }   
            }
            System.out.println("Local console " + clte.name + ", going down");
            clte.disconnect();
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }
    }
}
//===============================================================
