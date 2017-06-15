package app;

import core.PoolExecutorRunnable;
import core.TipoServidor;
import service.ZumbiService;

/**
 * aplicacao zumbi
 */
public class ServidorZumbiApp {
	
	private static PoolExecutorRunnable poolExecutor = null;

	public static void main(String[] args) throws Exception {
		
		String server = "basico";
		
//		if (!isArgsValid(args)) {
//			showUsageMode();
//			System.exit(1);
//		 }

		try {
			
			ZumbiService operatorServer = new ZumbiService(server.toUpperCase());
			poolExecutor = new PoolExecutorRunnable(operatorServer);
			poolExecutor.start();
			
		} catch (Exception e) {
			poolExecutor.stop();
			e.printStackTrace();
		}

	}

	private static boolean isArgsValid(String[] args) {
		boolean returnValue = true;

		if (args.length != 1) {
			returnValue = false;
		} else {
			try {
				TipoServidor.valueOf(args[0]);
				returnValue = true;
			} catch (Exception e) {
				returnValue = false;
			}
		}

		return returnValue;
	}

	private static void showUsageMode() {
		
		String msg = " # Use o comando: java -jar ServidorZumbi.jar [tipo] para ativar o serviço\n"
				+ " # [tipo] BASICO | ESPECIAL";
		
		System.out.println(msg);
	}
}