package escalonador;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import util.JsonObjectIO;
import util.TipoServidor;

public class QueueServer {
	
	private static final String TAG = "# " + QueueServer.class.getSimpleName() + " -> ";

	private List<String> basicHosts = new ArrayList<>();
	private List<String> specialHosts = new ArrayList<>();
	private CircularFifoQueue<String> basicQueue = null;
	private CircularFifoQueue<String> specialQueue = null;
	private JsonObjectIO jsonObjectIO = null;
	
	public QueueServer() {
		this.basicQueue = new CircularFifoQueue<>();
		this.specialQueue = new CircularFifoQueue<>();		
	}

	public QueueServer(JsonObjectIO jsonObjectIO) {
		
		this.basicQueue = new CircularFifoQueue<>();
		this.specialQueue = new CircularFifoQueue<>();
		this.jsonObjectIO = jsonObjectIO;

		load();
	}

	public String get (JSONObject obj) {

		String tipo = obj.getString("operacao");
		TipoServidor tipoServidor = TipoServidor.valueOf(tipo);
		
		String server = "";
		
		switch (tipoServidor.getClassName().split("\\.")[1]) {
		case "ServidorBasico":
			server = this.basicQueue.poll();
			System.out.println(TAG + "Get Server Basic Pool: " + this.basicQueue);
			break;
		case "ServidorEspecial":
			server = this.specialQueue.poll();
			System.out.println(TAG + "Get Server Special Pool: " + this.specialQueue);
			break;
		default:
			server = null;
			break;
		}

		return server;
	}

	public void add (JSONObject obj) throws ParseException {
		
		TipoServidor tipoServidor = TipoServidor.valueOf(obj.getString("tipo"));
		String url = obj.getString("host");

		switch (tipoServidor.getClassName().split("\\.")[1]) {
		case "ServidorBasico":
			this.basicQueue.add(url);
			System.out.println(TAG + "Set BasicServer Basic Pool: " + this.basicQueue);
			break;
		case "ServidorEspecial":
			this.specialQueue.add(url);
			System.out.println(TAG + "Set SpecialServer Special Pool: " + this.specialQueue);
			break;
		default:
			// nada a fazer
			break;
		}

	}

	private void load() {

		this.basicHosts = jsonObjectIO.readSlaveJson("ServidorBasico");
		if(basicHosts != null) {
			for (String host : basicHosts) {
				basicQueue.add(host);
			}
		}

		this.specialHosts = jsonObjectIO.readSlaveJson("ServidorEspecial");
		if(specialHosts != null) {
			for (String host : specialHosts) {
				specialQueue.add(host);
			}
		}
		
		System.out.println(TAG + "Servidores Basicos ativos: " + this.basicQueue.size());
		System.out.println(TAG + "Servidores Especiais ativos: " + this.specialQueue.size());
	}

	public void update() {
		
		System.out.println(TAG + "Queue Updating... ");
		
		basicQueue.clear();
		specialQueue.clear();
		
		load();
		
		System.out.println(TAG + "Queue Updated! ");
	}	
}
