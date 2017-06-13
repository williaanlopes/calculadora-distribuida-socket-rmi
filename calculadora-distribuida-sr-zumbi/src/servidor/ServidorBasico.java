package servidor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.json.JSONObject;

import stub.Basico;

/**
 * Servidor Basico efetua os calculos
 */
public class ServidorBasico extends UnicastRemoteObject implements Basico {  

	private static final long serialVersionUID = 1L;

	public ServidorBasico() throws RemoteException {
		super();
	}

	private JSONObject json = null;
    private static final String TAG = "# " + ServidorBasico.class.getSimpleName() + " Log -> ";

	@Override
	public String getNomeServidor() throws RemoteException {
		return this.getClass().getSimpleName();  
	} 

	@Override
	public void setJsonObject(String json) throws RemoteException {
		System.out.println(TAG + "Recebendo JSON...");
		this.json = new JSONObject(json);
	}

	@Override
	public String getJsonObject() throws RemoteException {
		
		System.out.println(TAG + "JSON Recebido!");
		System.out.println(TAG + "Descobrindo operacao...");  
        
        String valor1 = this.json.getString("valor1");
        String valor2 = this.json.getString("valor2");
        String operador = this.json.getString("operacao");
        Double resultado = 0d;
        
        System.out.println(TAG + this.json);
        
        switch (operador) {
		case "ADD":
			resultado = Double.parseDouble(valor1) + Double.parseDouble(valor2);
			System.out.println(TAG + "Efetuando operacao de adicao..."); 
			break;
		case "SUB":
			resultado = Double.parseDouble(valor1) - Double.parseDouble(valor2);
			System.out.println(TAG + "Efetuando operacao de subtracao..."); 
			break;
		case "MUL":
			resultado = Double.parseDouble(valor1) * Double.parseDouble(valor2);
			System.out.println(TAG + "Efetuando operacao de multiplicacao..."); 
			break;
		case "DIV":
			resultado = Double.parseDouble(valor1) / Double.parseDouble(valor2);
			System.out.println(TAG + "Efetuando operacao de divisao..."); 
			break;
		} 
        
        System.out.println(TAG + "Operacao concluida!");
        System.out.println(TAG + "Resultado: " + resultado);
        
        JSONObject json = new JSONObject();
        json.put("valor", resultado);
        
        return json.toString();
	}
}  