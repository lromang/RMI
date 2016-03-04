//===============================================================
// Topico
//===============================================================


package Channel;
import Interfaces.IChatClient;
import Mensaje.Message;
import Server.ChatServer;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;




public class Topico {
    public String nombre;
    public String canal;
    public String ruta;
    Hashtable<String, IChatClient> clientes;

    /*
      Clase topico.
    */
    
    public Topico(String nombre, String canal){
        this.nombre = nombre;
        this.canal  = canal;
        clientes    = new Hashtable<String, IChatClient>();
        ruta        = "/home/luis/Documents/ITAM/spring_2016/distribuidos/proy1/topicos/" + canal + "_" + nombre + ".txt";
    }

    /*
      Escribe mensaje en archivo. Si este último no
      existe, lo genera. 
    */
    public void agregaMensaje(Message message){
        FileWriter fw = null;
        try {
            File file = new File(ruta);
            if(!file.exists()){
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsoluteFile(), true);
            fw.write(message.toString());
            fw.write(System.getProperty( "line.separator" ));
            fw.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } 
    }

    /*
      Lee contenidos del archivo especificado
      por la ruta.
    */    
    public String getLog(){
        String resp = "";
        FileReader rw = null;
        try{
            FileReader fr     = new FileReader(ruta); 
            BufferedReader br = new BufferedReader(fr); 
            String s; 
            while((s = br.readLine()) != null) { 
                resp = resp + s + "\n"; 
            } 
            fr.close(); 
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return resp;
    }

    
    /*
      Agrega cliente a la tabla hash de clientes en caso
      de que este no esté.
    */
    public boolean addClient(String usuario, IChatClient objeto){
        boolean res = false;
        if(!clientes.containsKey(usuario)){
            clientes.put(usuario, objeto);
            res = true;
        } 
        return res;
    }

    /*
      Elimina cliente de la tabla hash de clientes.
    */
    public boolean removeClient(String usuario){
        boolean res = false;
        if(clientes.containsKey(usuario)){
            clientes.remove(usuario);
            res = true;
        } 
        return res;
    }
    
    /*
      Devuelve la tabla hash de  clientes.
    */
    public Hashtable<String, IChatClient> getClients(){
        return clientes;
    }

    /*
      Determina si un cliente está o no dentro de
      clientes.
    */
    public boolean containsClient(String usuario){
        boolean res = false;
        if(clientes.containsKey(usuario)){
            res = true;
        }
        return res;
    }
}
