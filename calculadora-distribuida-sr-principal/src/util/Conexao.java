package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Responsavel pela conexao com os servidores
 */
public abstract class Conexao {
	
	private static final String TAG = "# ServidorPrincipal -> ";

	protected Socket socket = null;
	protected int serverPort = 0;
	protected String serverAddress = null;
	protected Properties properties = null;
	protected String configFileName = null;
	protected JsonObjectIO jsonObjectIO = null;

	protected Conexao() {
	}

	/**
	 * Cria uma conexao baseada no arquivo de configuracao passado por parametro
	 * @param configFileName Arquivo de configuracao para a conexao
	 */
	protected Conexao(String configFileName) throws FileNotFoundException, IOException {
		this.configFileName = configFileName;
		loadProperties();
	}

	/**
	 * Carrega as propriedades do arquivo de configuracao
	 */
	private void loadProperties() throws FileNotFoundException, IOException {
		File file = null;
		FileReader reader = null;
		file = new File(this.configFileName);
		reader = new FileReader(file);
		properties = new Properties();
		properties.load(reader);
	}

	/**
	 * Método que configura a conexão
	 */
	public abstract void configure();

	/**
	 * Abre a conexao com o servidor
	 */
	public final void openByProperties() throws UnknownHostException, IOException {
		configure();
		System.out.println(this.serverAddress + " - " + this.serverPort);
		this.socket = new Socket(this.serverAddress, this.serverPort);
		this.jsonObjectIO = new JsonObjectIO(socket);
	}

	/**
	 * Abre a conexao com o servidor
	 */
	public final void openByJson() throws UnknownHostException, IOException {
		configure();
		this.socket = new Socket(this.serverAddress, this.serverPort);
		this.jsonObjectIO = new JsonObjectIO(socket);
		System.out.println(TAG + "serverConneciont " + this.serverAddress + " - " + this.serverPort);
	}
	
	public final boolean openByJson(String s) {
		boolean rt = false;
		try {
			configure();
			this.socket = new Socket(this.serverAddress, this.serverPort);
			this.jsonObjectIO = new JsonObjectIO(socket);
			rt = true;
		} catch (Exception e) {
			// TODO: handle exception
		}	
		
		System.out.println(TAG + "serverConneciont " + this.serverAddress + " - " + this.serverPort);
		return rt;		
	}

	public final void close() throws UnknownHostException, IOException {
		this.socket.close();
	}

	/**
	 * Retorna o Stream de entrada da conexao
	 * @return InputStream da conexao
	 */
	public InputStream getInputStream() throws IOException {
		return this.socket.getInputStream();
	}

	/**
	 * Retorna o Stream de saida da conexao
	 * @return OutputStream da conexao
	 */
	public OutputStream getOutputStream() throws IOException {
		return this.socket.getOutputStream();
	}

	public void sendJsonToServer(JSONObject obj) throws JSONException, IOException, ClassNotFoundException {
		System.out.println(TAG + "enviado JSON...");
		
		OutputStream out = socket.getOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		oos.writeObject(obj.toString());
		out.flush();

		System.out.println(TAG + "JSON enviado!");
	}

	/**
	 * Retorna o Stream de saida da conexao
	 * @return JSONObject vindo do servidor
	 * @throws IOException
	 * @throws JSONException
	 * @throws ClassNotFoundException
	 */
	public JSONObject getJsonFromServer() throws IOException, JSONException, ClassNotFoundException {
		return this.jsonObjectIO.getJsonObject();
	}
}
