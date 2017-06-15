package core;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import util.Conexao;

/**
 * Classe que ira submeter e receber json 
 * com as operacoes
 */
public class ParametroJson {
	
	private static final String TAG = "# Cliente -> ";

	private List<Double> params = null;
	private String operacao = null;
	private Conexao conexao = null;

	public ParametroJson(List<Double> params, String operacao) {
		this.params = params;
		this.operacao = operacao;;
	}

	/**
	 * Le o resultado da operacao
	 * @return Valor da operacao
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws JSONException
	 */
	public Double getValue() {

		if (!isParamsValid()) {
			throw new IllegalArgumentException("Parâmetros inválidos!");
		}
		
		Double returnValue = 0d;
		
		try {
			
			System.out.println(TAG + "Estabelecendo conexao com servidor principal...");
			
			conexao = new ClienteConexaoPrincipal(".\\cliente.properties");
			conexao.openByProperties();
			JSONObject serverJson = jsonParams();			
			conexao.sendJsonToServer(serverJson);
			JSONObject clienteJson = conexao.getJsonFromServer();
			returnValue = getResultFromJson(clienteJson);
			
			System.out.println(TAG + "Conexao com servidor principal estabelecida!");
			
		} catch (JSONException | IOException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return returnValue;
	}

	/**
	 * Le os parametros e gera um Json que sera enviado ao servidor principal
	 * @return json JSONObject, baseado nos parametros
	 */
	private JSONObject jsonParams() {

		System.out.println(TAG + "Setando parametros...");
		
		JSONObject json = new JSONObject();
		json.put("valor1", this.params.get(0).toString());
		json.put("valor2", this.params.get(1).toString());
		json.put("operacao", this.operacao);
		
		System.out.println(TAG + "Parametros setados!");
		
		return json;
	}

	/**
	 * Verifica/Valida o calculo com os argumentos para saber se e possivel calcular
	 * @return verdadeiro se for possivel realizar o calculo, falso se nao for possivel
	 */
	private boolean isParamsValid() {
		return this.params != null && this.params.size() > 0 && this.operacao != null;
	}

	/**
	 * Le o resultado da operacao que veio do servidor principal pelo Json
	 * @param obj JSONObject que veio do servidor principal
	 * @return valor da operacao
	 */
	private Double getResultFromJson(JSONObject obj) {
		System.out.println(TAG + "Recebendo resultado...");
		
		Double returnValue = obj.getDouble("valor");
		
		System.out.println(TAG + "Resultado recebido!");
		return returnValue;
	}

}