package util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

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

	@SuppressWarnings("unchecked")
	public List<String> readSlaveJson(JSONObject json) {

		JSONParser parser = new JSONParser();
		List<String> lista = new ArrayList<>();
		TipoServidor tipoServidor = TipoServidor.valueOf(json.getString("operacao"));
		String nomeArquivo = ".\\" + tipoServidor.getClassName().split("\\.")[1] + ".json";

		try {

			Object obj = parser.parse(new FileReader(nomeArquivo));

			org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
			// loop array
			org.json.simple.JSONArray slavers = (org.json.simple.JSONArray) jsonObject.get("zumbis");

			Iterator<Object> iterator = slavers.iterator();
			while (iterator.hasNext()) {
				lista.add(iterator.next().toString());
			}

		} catch (IOException | org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}

		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> readSlaveJson(String fileName) {

		JSONParser parser = new JSONParser();
		List<String> lista = new ArrayList<>();

		try {

			Object obj = parser.parse(new FileReader(fileName + ".json"));
			org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
			org.json.simple.JSONArray slavers = (org.json.simple.JSONArray) jsonObject.get("zumbis");

			Iterator<Object> iterator = slavers.iterator();
			while (iterator.hasNext()) {
				lista.add(iterator.next().toString());
			}

		} catch (IOException | org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	public boolean verificaSlaveJson(JSONObject json) {

		JSONParser parser = new JSONParser();
		String host = json.getString("host");

		TipoServidor tipoServidor = TipoServidor.valueOf(json.getString("server"));
		String nomeArquivo = ".\\" + tipoServidor.getClassName().split("\\.")[1] + ".json";
		boolean rt = false;

		File file = new File(nomeArquivo);

		if (file.exists()) {

			try {

				Object obj = parser.parse(new FileReader(nomeArquivo));
				org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
				org.json.simple.JSONArray slavers = (org.json.simple.JSONArray) jsonObject.get("zumbis");

				Iterator<Object> iterator = slavers.iterator();
				while (iterator.hasNext()) {
					if (host.equals(iterator.next())) {
						rt = true;						
						break;
					}
				}

			} catch (IOException | org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			}

		}

		return rt;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	public boolean addNewSlaveJson(JSONObject json) throws org.json.simple.parser.ParseException {

		JSONParser parser = new JSONParser();
		String host = json.getString("host");

		TipoServidor tipoServidor = TipoServidor.valueOf(json.getString("tipo"));
		String nomeArquivo = ".\\" + tipoServidor.getClassName().split("\\.")[1] + ".json";
		boolean rt = false;

		if (!verificaSlaveJson(json)) {
			try {

				org.json.simple.JSONObject obj = new org.json.simple.JSONObject();
				org.json.simple.JSONArray array = new org.json.simple.JSONArray();
				
				array.add(host);				
				obj.put("zumbis", array);				
				
				FileWriter file = null;
				file = new FileWriter(nomeArquivo);
				file.write(obj.toString());
				file.flush();

				if (file != null) {
					try {
						file.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				rt = true;
				System.out.println("-> Novo servidor cadastrado! \n  #Host: " + host);

			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			System.err.println("-> Servidor ja cadastrado!");
		}

		return rt;
	}
}
