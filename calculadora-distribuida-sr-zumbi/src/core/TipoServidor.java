package core;

/** 
 * Define os tipos de operacao existentes 
 */  
public enum TipoServidor {  

    // servidor zumbi basico
    ADD("servidor.ServidorBasico"),  
    SUB("servidor.ServidorBasico"),  
    MUL("servidor.ServidorBasico"),  
    DIV("servidor.ServidorBasico"),
    
    // servidor zumbi especial
	RAIZ("servidor.ServidorEspecial"),
	POTE("servidor.ServidorEspecial"),
	PORC("servidor.ServidorEspecial"),
	
	// usado somente para cadastro
	BASICO("servidor.ServidorBasico"),
	ESPECIAL("servidor.ServidorEspecial");
      
    private String className = null;  
      
      
    private TipoServidor(String className) {  
        this.className = className;      
    }        
      
    public String getClassName() {  
        return this.className;  
    }  
   
}  