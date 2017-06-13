package app;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import servidor.ServidorBasico;
import stub.Principal;
import util.PropertiesFile;

/**
 * Servidor de operacoes
 * 
 */
public class ZumbiServer implements Runnable {

	private String serverHost = null;
	private int serverPort = 0;
	private String tipoServidor = null;
	private Registry registry = null;
	private String serverName = null;
	private ServidorBasico servidorBasico = new ServidorBasico();
	private static final String TAG = "# ServidorZumbi -> ";

	public ZumbiServer(String tipoServidor)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		this.tipoServidor = tipoServidor;
		
		
		cadastro();
	}

	public void run() {

		startRmi();
		
		System.out.println(TAG + "online e pronto para uso!" + "\n  #Name: " + this.serverName + " | Host: "
				+ this.serverHost + " | Port: " + this.serverPort);

	}

	private void startRmi() {

		try {

			Runtime.getRuntime().exec("rmid -J-Djava.security.policy=rmid.policy");
			System.setProperty("java.rmi.server.hostname", this.serverName);

			registry = LocateRegistry.createRegistry(this.serverPort);
			registry.rebind(this.serverName, servidorBasico);
			
			System.out.println(TAG + "RMI Propriedades Name: " + serverName + " Port: " + this.serverPort);

		} catch (Exception e) {
			String s = stopRmi();
			System.err.println(TAG + "Erro nao foi possivel iniciar o servico: " + s + e.getMessage());
		}

	}
	
	private String stopRmi(){
		
		String s = " Falha: ";
		
		try {
			if (registry != null) {
				registry.unbind(serverName);
				UnicastRemoteObject.unexportObject(servidorBasico, true);
			}

		} catch (RemoteException | NotBoundException e) {
			s += e.getMessage();
		}
		// retorna messagem de erro caso tenha alguma
		return s;
	}

	private boolean cadastro() {

		boolean r = false;

		System.out.println(TAG + "cadastrando servidor...");

		try {

			Map<String, String> parametrosZumbi;
			parametrosZumbi = PropertiesFile.loadProperties("servidorZumbi");

			if (parametrosZumbi == null) {

				this.serverName = UUID.randomUUID().toString().replaceAll("-", "");
				this.serverHost = InetAddress.getLocalHost().toString().split("/")[1];
				this.serverPort = 2005;

				parametrosZumbi = new HashMap<>();
				parametrosZumbi.put("zumbi.host", this.serverHost);
				parametrosZumbi.put("zumbi.port", this.serverPort + "");
				parametrosZumbi.put("zumbi.name", this.serverName);

				PropertiesFile.writeProperties(parametrosZumbi);

			} else {

				Map<String, String> parametrosPrincipal = PropertiesFile.loadProperties("principal");

				this.serverHost = parametrosZumbi.get("zumbi.host");
				this.serverPort = Integer.parseInt(parametrosZumbi.get("zumbi.port"));
				this.serverName = parametrosZumbi.get("zumbi.name");

				Runtime.getRuntime().exec("rmid -J-Djava.security.policy=rmid.policy");
				Principal principal = (Principal) Naming.lookup("rmi://" + parametrosPrincipal.get("principal.host")
						+ ":" + parametrosPrincipal.get("principal.port") + "/"
						+ parametrosPrincipal.get("principal.name"));

				JSONObject jobj = new JSONObject();

				jobj.put("host", this.serverHost);
				jobj.put("port", this.serverPort + "");
				jobj.put("name", this.serverName);
				jobj.put("tipo", tipoServidor);
				
				principal.cadastrar(jobj.toString());
				
				System.out.println(TAG + "servidor cadastrado!");
				System.out.println(TAG + "aguardando...");

			}

		} catch (Exception e) {
			r = false;
			System.err.println(TAG + "Erro nao foi possivel cadastrar este servidor: " + e.getMessage());
			// e.printStackTrace();
		}

		return r;
	}
}