package util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Classe responsavel pela construcao dos Jsons
 */
public class JsonObjectIO {

	private Socket socket = null;

	public JsonObjectIO() {
	}

	public JsonObjectIO(Socket socket) throws IOException {
		this.socket = socket;
	}

	/**
	 * Envia o Json atraves do socket
	 * @param obj JSONObject a ser enviado
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void sendJson(JSONObject obj) {
		
		System.out.println("-> Enviando Json...");
		try {
			OutputStream out = socket.getOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(out);
			o.writeObject(obj.toString());
			out.flush();
			System.out.println("-> Json enviado!");
		} catch (Exception e) {
			System.err.println("-> Erro ao enviar Json");
			new RuntimeException(e);
		}
	}

	/**
	 * Le o JSONObject enviado pelo socket
	 * @return JSONObject json lido do servidor principal
	 * @throws JSONException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public JSONObject getJsonObject() throws JSONException, IOException, ClassNotFoundException {

		System.out.println("-> Lendo Json...");

		JSONObject json;
		String resposta = null;

		InputStream in = socket.getInputStream();
		ObjectInputStream ois = new ObjectInputStream(in);
		resposta = (String) ois.readObject();

		json = new JSONObject(resposta);

		System.out.println("-> Json lido!");

		return json;
	}
}
