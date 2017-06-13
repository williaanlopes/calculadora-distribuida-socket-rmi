package stub;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** 
 * Interface que sera implementada pelos servidores de operacao (BASICO e ESPECIAL) 
 */  
public interface Especial extends Remote {  
      
    /** 
     * Seta o Json que sera usado no calculo 
     * @param json JSONObject com os parametros 
     */  
    void setJsonObject(String json) throws RemoteException;  
      
    /** 
     * Efetua o calculo e retorna o Json com os parametros 
     * @return JSONObject produzido com o resultado da operacao 
     */ 
    String getJsonObject() throws RemoteException;
      
    /** 
     * Retorna o nome do servidor que esta sendo usado 
     * @return Uma String contendo o nome do servidor que esta sendo usado 
     */  
    String getNomeServidor() throws RemoteException;
}  