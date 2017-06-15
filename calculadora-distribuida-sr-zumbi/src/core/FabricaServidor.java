package core;

import stub.Basico;

/** 
 * Fabrica de servidores 
 */  
public class FabricaServidor {  
  
    /** 
     * Fabrica a classe que vai realizar o calculo de acordo com o tipo da operacao 
     * @param tipoServidor Tipo de servidor de operacao solicitado (BASICO/ESPECIAL) 
     * @return rt servidor
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */  
    @SuppressWarnings("unchecked")  
    public static Basico getServidor(TipoServidor tipoServidor) throws ClassNotFoundException, InstantiationException, IllegalAccessException {  
        
    	Basico rt = null;  
          
        Class<Basico> clazz = (Class<Basico>) Class.forName(tipoServidor.getClassName());  
          
        rt =  clazz.newInstance();  
        
        return rt;  
    }  
}  