//===============================================================
// Channel
//===============================================================

package Channel;

import Interfaces.IChatClient;
import Interfaces.IChatServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;


public class Channel {
    public String name;
    Hashtable<String, IChatClient> chatters; 
    Hashtable<String, Topico> topics;
    /*
      - chatters: Miembros del canal.
      - topics:   Topicos del canal.
     */
    
    public Channel(String name){
        this.name = name;
        chatters  = new Hashtable<String, IChatClient>();
        topics    = new Hashtable<String, Topico>();
    }
    
    /*
      Verifica si usuario está dentro de chatters,
      en caso de que no, lo agrega.
     */
    public boolean addClient(String usuario, IChatClient objeto){
        boolean res = false;
        if(!chatters.containsKey(usuario)){
            chatters.put(usuario, objeto);
            res = true;
        } 
        return res;
    }

    /*
      Verifica si topico está dentro de topics,
      en caso de que no, lo agrega.
     */
    public boolean addTopic(String topico, Topico objeto){
        boolean res = false;
        if(!topics.containsKey(topico)){
            topics.put(topico, objeto);
            res = true;
        } 
        return res;
    }

    
    /*
      En caso de que un cliente esté dentro de
      chatters, lo elimina.
    */    
    public boolean removeClient(String usuario){
        boolean res = false;
        if(chatters.containsKey(usuario)){
            chatters.remove(usuario);
            res = true;
        } 
        return res;
    
    }
        
    /*
      En caso de que un tópico esté dentro de
      topics, lo elimina.
    */    
    public boolean removeTopic(String topico){
        boolean res = false;
        if(topics.containsKey(topico)){
            topics.remove(topico);
            res = true;
        } 
        return res;
    }

        
    /*
      Verifica si chatters contiene un usuario.
    */    
    public boolean containsClient(String usuario){
        boolean res = false;
        if(chatters.containsKey(usuario)){
            res = true;
        }
        return res;
    }
        
    /*
      Verifica si topics contiene un tópico.
    */        
    public boolean containsTopico(String topico){
        boolean res = false;
        if(topics.containsKey(topico)){
            res = true;
        }
        return res;
    }
            
    /*
      Regresa tabla hash chatters
    */    
    public Hashtable<String, IChatClient> getClients(){
        return chatters;
    }
            
    /*
      Regresa tabla hash topics
    */        
    public Hashtable<String, Topico> getTopics(){
        return topics;
    }
                
    /*
      Regresa objeto IChatClient
    */    
    public IChatClient getIChatClient(String nombreC){
        IChatClient res = null;
        if(containsClient(nombreC)){
            res = chatters.get(nombreC);
        }
        return res;
    }

                
    /*
      Regresa objeto Topico
    */    
    public Topico getTopico(String topico){
        Topico res = null;
        if(containsTopico(topico)){
            res = topics.get(topico);
        }
        return res;
    }
}
