package core;

import org.json.JSONObject;

import stub.Especial;

/**
 * Servidor Especial efetua os calculos
 */
public class ServidorEspecial implements Especial {

	private static final String TAG = "# ServidorZumbi -> ";
	private JSONObject json = null;

	@Override
	public String getNomeServidor() {
		return this.getClass().getSimpleName();
	}

	@Override
	public void setJsonObject(String json) {
		System.out.println(TAG + "Recebendo JSON...");
		this.json = new JSONObject(json);
	}

	@Override
	public String getJsonObject() {
		
		System.out.println(TAG + "JSON Recebido: " + this.json);
		System.out.println(TAG + "Descobrindo operacao...");  

		String valor1 = this.json.getString("valor1");
		String valor2 = this.json.getString("valor2");
		String operador = this.json.getString("operacao");
		Double resultado = 0d;

		switch (operador) {
		case "PORC":
			resultado = (Double.parseDouble(valor1) * Double.parseDouble(valor2) / 100);
			System.out.println(TAG + "Efetuando operacao de porcentagem...");
			break;
		case "POTE":
			resultado = Math.pow(Double.parseDouble(valor1), 2);
			System.out.println(TAG + "Efetuando operacao de potencia...");
			break;
		case "RAIZ":
			resultado = Math.sqrt(Double.parseDouble(valor1));
			System.out.println(TAG + "Efetuando operacao de raiz quadrada...");
			break;
		}
		
		System.out.println(TAG + "Operacao concluida!");
        System.out.println(TAG + "Resultado: " + resultado);

		JSONObject json = new JSONObject();
		json.put("valor", resultado);

		return json.toString();
	}
}