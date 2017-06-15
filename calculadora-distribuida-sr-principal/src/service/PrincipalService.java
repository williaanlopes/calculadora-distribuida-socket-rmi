package service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import app.CadastrorZumbi;
import escalonador.QueueServer;
import stub.Basico;
import stub.Especial;
import util.JsonObjectIO;
import util.TipoServidor;
import util.Util;

public class PrincipalService implements Runnable {

	private static final String TAG = "# ServidorPrincipal -> ";
	private static final String rmiFile = "principal";

	private CadastrorZumbi cadastroZumbi = null;
	private JsonObjectIO jsonObjectIO = null;
	private ServerSocket serverSocket = null;
	private Registry registry = null;
	private QueueServer queue = null;
	
	private Integer prmiPort = null;
	private String prmiHost = null;
	private String prmiName = null;

	private Integer socketPort = null;
	private String socketHost = null;
	
	private String resultado = null;
	private boolean canRun = true;

	public PrincipalService(int serverPort) throws IOException {
		
		this.socketPort = serverPort;
		startSocket();
	}

	public void run() {

		System.out.println(TAG + "Servidor principal pronto para uso!");
		System.out.println(TAG + "# Host: " + this.socketHost + " Port: " + this.socketPort + " (porta atual).");
		
		System.out.println(TAG + "# RMI propriedades # Host: " + this.socketHost + " Port: " + this.socketPort);

		startRmi();

		while (canRun) {
			Socket socket = null;
			try {

				System.out.println(TAG + "Servidor principal: Aguardando conexao...");
				socket = serverSocket.accept();

				System.out.println(TAG + "Cliente conectado: " + socket.getRemoteSocketAddress());

				jsonObjectIO = new JsonObjectIO(socket);
				calcular(jsonObjectIO);

			} catch (Exception e) {
				canRun = false;
				stopService(e.getMessage());
			}
		}
	}
	
	private void startSocket () {
		try {
			
			this.serverSocket = new ServerSocket(this.socketPort);
			this.socketHost = InetAddress.getLocalHost().toString().split("/")[1];
			this.queue = new QueueServer();
			this.cadastroZumbi = new CadastrorZumbi(queue);
			
		} catch (IOException e) {
			stopService(e.getMessage());
		}
	}

	private void startRmi() {
		try {

			Map<String, String> rmiProperties = Util.readProperties(rmiFile);

			if (rmiProperties == null || rmiProperties.isEmpty()) {

				rmiProperties = new HashMap<>();
				rmiProperties.put("prmi.host", "");
				rmiProperties.put("prmi.port", "");
				rmiProperties.put("prmi.name", "");
				Util.writeProperties(rmiFile, rmiProperties);

				stopService("Edite o arquivo (principal.properties) adicionando "
						+ "as configuracoes do servidor principal!\n\n");
				
			} else {

				this.prmiHost = rmiProperties.get("prmi.host"); 
				this.prmiPort = Integer.parseInt(rmiProperties.get("prmi.port"));
				this.prmiName = rmiProperties.get("prmi.name");
				
				Runtime.getRuntime().exec("rmid -J-Djava.security.policy=rmid.policy");
				System.setProperty("java.rmi.server.hostname", this.prmiHost);

				registry = LocateRegistry.createRegistry(this.prmiPort);
				registry.rebind(this.prmiName, cadastroZumbi);
			}

		} catch (Exception e) {
			try {

				registry.unbind("servidorprincipal");
				UnicastRemoteObject.unexportObject(cadastroZumbi, true);

			} catch (RemoteException | NotBoundException e1) {
				stopService(e1.getMessage());
			}

			stopService(e.getMessage());
		}
	}

	public void calcular(JsonObjectIO jsonObjectIO) {

		JSONObject jsonFromServer = null;

		try {

			jsonFromServer = jsonObjectIO.getJsonObject();

			System.out.println(TAG + "Descobrindo o tipo da operacao... ");

			String json = jsonFromServer.getString("operacao");
			TipoServidor tipo = TipoServidor.valueOf(json);

			switch (tipo.getClassName().toString().split("\\.")[1]) {
			case "ServidorBasico":
				System.out.println(TAG + "tipo da operacao: basico");
				basico(jsonFromServer);
				break;
			case "ServidorEspecial":
				System.out.println(TAG + "tipo da operacao: especial");
				especial(jsonFromServer);
				break;
			default:
				// nada a fazer
				break;
			}

		} catch (JSONException | ClassNotFoundException | IOException e) {
			stopService(e.getMessage());
		}
	}

	private void basico(JSONObject obj) {

		try {

			System.err.println(obj);
			String queueServer = queue.get(obj);
			System.err.println(queueServer);

			Basico basico = (Basico) Naming.lookup("rmi://" + queueServer);
			basico.setJsonObject(obj.toString());
			obj.put("host", queueServer);
			obj.put("tipo", obj.get("operacao"));
			queue.add(obj);

			this.resultado = basico.getJsonObject();

			jsonObjectIO.sendJson(new JSONObject(this.resultado));

			System.err.println(TAG + "resultado: " + this.resultado);

		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			stopService(e.getMessage());
		} catch (ParseException e) {
			stopService(e.getMessage());
		}
	}

	private void especial(JSONObject obj) {

		String queueServer = null;

		try {

			queueServer = queue.get(obj);

			if (queueServer != null) {
				Especial basico = (Especial) Naming.lookup("rmi://" + queueServer);
				basico.setJsonObject(obj.toString());

				obj.put("host", queueServer);
				obj.put("tipo", obj.get("operacao"));

				queue.add(obj);

				this.resultado = basico.getJsonObject();

				jsonObjectIO.sendJson(new JSONObject(resultado));

				System.err.println(TAG + "resultado: " + resultado);
			}

		} catch (MalformedURLException | RemoteException | NotBoundException | ParseException e) {
			stopService(e.getMessage());
		}
	}

	private void stopService(String erroMsg) {
		System.err.println(TAG + erroMsg);
		System.exit(1);
	}
}