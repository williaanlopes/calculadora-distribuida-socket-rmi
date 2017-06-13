package app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import escalonador.QueueServer;
import stub.Basico;
import stub.Especial;
import util.JsonObjectIO;
import util.TipoServidor;

public class SelecionarServidor {

	private static final String TAG = "# " + SelecionarServidor.class.getSimpleName() + " -> ";
	
	private QueueServer queue = null;
	private String resultado = null;
	private JsonObjectIO jsonObjectIO = null;

	public SelecionarServidor(QueueServer queue, Socket socket) throws IOException {
		this.queue = queue;
		this.jsonObjectIO = new JsonObjectIO(socket);
	}

	public void calcular () {

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
			throw new RuntimeException(e);
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

			jsonObjectIO.sendJson(new JSONObject(resultado));

			System.err.println("resultado: " + this.resultado);

		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
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
			}

		} catch (MalformedURLException | RemoteException | NotBoundException | ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public String getResultado() {
		return resultado;
	}

}
