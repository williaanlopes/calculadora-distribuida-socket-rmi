package stub;


import java.rmi.Remote;
import java.rmi.RemoteException;

/** 
 * Interface que sera implementada pelos servidores de operacao (BASICO e ESPECIAL) 
 */  
public interface Principal extends Remote {  
      
    /** 
     * Seta o Json que sera usado no calculo 
     * @param json JSONObject com os parametros 
     */  
    void cadastrar(String json) throws RemoteException;  
}  