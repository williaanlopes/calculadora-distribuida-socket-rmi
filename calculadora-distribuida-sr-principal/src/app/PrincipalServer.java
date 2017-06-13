package app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import escalonador.QueueServer;
import util.PropertiesFile;

public class PrincipalServer implements Runnable {

	private static final String TAG = "" + PrincipalServer.class.getSimpleName() + " -> ";
	
	private ServerSocket serverSocket = null;
	private boolean canRun = true;
	private Registry registry = null;
	private CadastrorZumbi cadastroZumbi = null;
	private int serverPort = 1099;
	private QueueServer queue = null;
	
	public PrincipalServer(int serverPort) throws IOException {
		
		this.serverSocket = new ServerSocket(serverPort);
		queue = new QueueServer();
		cadastroZumbi = new CadastrorZumbi(queue);
	}

	public void run() {

		System.out.println("-> Servidor principal pronto para uso! " + "\n  # Host: "
				+ this.serverSocket.getInetAddress().getHostAddress() + " Port: " + this.serverSocket.getLocalPort()
				+ " (porta atual).");

		startRmi();

		while (canRun) {
			Socket socket = null;
			try {

				System.out.println("-> Servidor principal: Aguardando conexao...");
				socket = serverSocket.accept();

				System.out.println(TAG + "Cliente conectado: " + socket.getRemoteSocketAddress());	
				
				SelecionarServidor calcular = new SelecionarServidor(queue, socket);
				calcular.calcular();

			} catch (Exception e) {
				canRun = false;
				System.err.println(TAG + "Erro " + e.getMessage());
			}
		}
	}

	private void startRmi() {
		try {
			
			Map<String, String> parametrosPrincipal = PropertiesFile.loadProperties("principal"); 
			
			Runtime.getRuntime().exec("rmid -J-Djava.security.policy=rmid.policy");
			System.setProperty("java.rmi.server.hostname", parametrosPrincipal.get("principal.name"));

			registry = LocateRegistry.createRegistry(Integer.parseInt(parametrosPrincipal.get("principal.port")));
			registry.rebind("servidorprincipal", cadastroZumbi);

		} catch (Exception e) {
			try {
				
				registry.unbind("servidorprincipal");
				UnicastRemoteObject.unexportObject(cadastroZumbi, true);
				
			} catch (RemoteException | NotBoundException e1) {
				e1.printStackTrace();
			}
			
			e.printStackTrace();
		}
	}
}