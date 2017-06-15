package service;

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

import core.ServidorBasico;
import stub.Principal;
import util.Util;

/**
 * Servidor de operacoes
 * 
 */
public class ZumbiService implements Runnable {

	private static final String TAG = "# ServidorZumbi -> ";
	private static final String zrmiFile = "zumbi";
	private static final String prmiFile = "principal";
	
	private String zrmiHost = null;
	private int zrmiPort = 0;
	private String zrmiName = null;

	private String tipoServidor = null;
	private Registry registry = null;
	private ServidorBasico servidorBasico = null;

	public ZumbiService(String tipoServidor)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		this.tipoServidor = tipoServidor;
		servidorBasico = new ServidorBasico();
		cadastro();
	}

	public void run() {

		startRmi();

		System.out.println(TAG + "Servidor online e pronto para uso!");

	}

	private void startRmi() {

		try {

			Runtime.getRuntime().exec("rmid -J-Djava.security.policy=rmid.policy");
			System.setProperty("java.rmi.server.hostname", this.zrmiName);

			registry = LocateRegistry.createRegistry(this.zrmiPort);
			registry.rebind(this.zrmiName, servidorBasico);

			System.out.println(TAG + "# RMI Name: # Host: " + this.zrmiHost + " | " + this.zrmiName
					+ " | Port: " + this.zrmiPort);

		} catch (Exception e) {
			String s = stopRmi();
			System.err.println(TAG + "Erro nao foi possivel iniciar o servico: " + s + e.getMessage());
			stopService();
		}

	}

	private String stopRmi() {

		String s = " Falha: ";

		try {

			if (registry != null) {
				registry.unbind(zrmiName);
				UnicastRemoteObject.unexportObject(servidorBasico, true);
			}

		} catch (RemoteException | NotBoundException e) {
			s += e.getMessage();
			stopService();
		}
		// retorna messagem de erro caso tenha alguma
		return s;
	}

	private boolean cadastro() {

		boolean r = false;

		System.out.println(TAG + "cadastrando servidor...");

		try {

			Map<String, String> parametrosZumbi;
			parametrosZumbi = Util.readProperties(zrmiFile);

			Map<String, String> parametrosPrincipal;

			if (parametrosZumbi == null || parametrosZumbi.isEmpty()) {

				System.out.println(TAG + "Gerando arquivos de propriedades...");

				this.zrmiName = UUID.randomUUID().toString().replaceAll("-", "");
				this.zrmiHost = InetAddress.getLocalHost().toString().split("/")[1];
				this.zrmiPort = 2005;

				parametrosZumbi = new HashMap<>();
				parametrosZumbi.put("zrmi.host", this.zrmiHost);
				parametrosZumbi.put("zrmi.port", this.zrmiHost + "");
				parametrosZumbi.put("zrmi.name", this.zrmiName);
				Util.writeProperties(zrmiFile, parametrosZumbi);

				parametrosPrincipal = new HashMap<>();
				parametrosPrincipal.put("prmi.host", "");
				parametrosPrincipal.put("prmi.port", "");
				parametrosPrincipal.put("prmi.name", "");
				Util.writeProperties(prmiFile, parametrosPrincipal);

				System.out.println(TAG + "Os arquivos de propriedades foram criado!");

				System.out.println(TAG + "Edite o arquivo (principal.properties) adicionando "
						+ "as configuracoes do servidor principal!\n\n");

				stopService();

			} else {

				parametrosPrincipal = Util.readProperties(prmiFile);

				this.zrmiHost = parametrosZumbi.get("zrmi.host");
				this.zrmiPort = Integer.parseInt(parametrosZumbi.get("zrmi.port"));
				this.zrmiName = parametrosZumbi.get("zrmi.name");

				Runtime.getRuntime().exec("rmid -J-Djava.security.policy=rmid.policy");
				Principal principal = (Principal) Naming.lookup("rmi://" + parametrosPrincipal.get("prmi.host") + ":"
						+ parametrosPrincipal.get("prmi.port") + "/" + parametrosPrincipal.get("prmi.name"));

				JSONObject jobj = new JSONObject();

				jobj.put("host", this.zrmiHost);
				jobj.put("port", this.zrmiPort + "");
				jobj.put("name", this.zrmiName);
				jobj.put("tipo", this.tipoServidor);

				principal.cadastrar(jobj.toString());

				System.out.println(TAG + "servidor cadastrado!");
				System.out.println(TAG + "aguardando requisicoes...");

			}

		} catch (Exception e) {
			r = false;
			System.err.println(TAG + "Erro: Nao foi possivel cadastrar este servidor: " + e.getMessage());
			stopService();
		}

		return r;
	}

	private void stopService() {
		System.exit(1);
	}
}