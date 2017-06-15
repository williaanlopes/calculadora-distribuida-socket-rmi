package app;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.json.JSONObject;

import escalonador.QueueServer;
import stub.Principal;

public class CadastrorZumbi extends UnicastRemoteObject implements Principal {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "# ServidorPrincipal -> ";
	
	private QueueServer queue = null;

	public CadastrorZumbi(QueueServer queue) throws RemoteException {
		super();
		this.queue = queue;
	}

	@Override
	public void cadastrar(String json) throws RemoteException {
		
		String host = null;
		JSONObject obj = null;
		
		try {
			
			obj = new JSONObject(json);
			host = obj.getString("host") + ":" + obj.getString("port") + "/" + obj.getString("name");
			
			obj.remove("host");
			obj.put("host", host);		
			
			queue.add(obj);
			
		} catch (Exception e) {
			new RuntimeException(e);
		}
		
		System.out.println(TAG + "novo  zumbi cadastrado host: " + host);
	}
}
