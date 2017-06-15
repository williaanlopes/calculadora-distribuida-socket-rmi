package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class Util {
	
	/**
	 * Carrega as configuracoes do arquivo de propriedades.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Map<String, String> readProperties(String fileName) throws FileNotFoundException, IOException {
		
		Map<String, String> map = new HashMap<>();
		
		String propFileName = ".\\"+ fileName +".properties";
		File file = new File(propFileName);
		InputStream input = null;
		Properties properties = null;		

		if (file.exists()) {

			input = new FileInputStream(file);
			properties = new Properties();
			properties.load(input);
			
			for (Entry<Object, Object> object : properties.entrySet()) {
				map.put(object.getKey().toString(), object.getValue().toString());
			}
	
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} 
//		else {
//			writeProperties();
//		}
		
		return map;
	}

	/**
	 * Escreve as configuracoes no arquivo de propriedades
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeProperties(String fileName, Map<String, String> parametros) throws FileNotFoundException, IOException {

		String propFileName = ".\\"+ fileName + ".properties";
		Properties properties = new Properties();
		OutputStream output = new FileOutputStream(propFileName);

		for (Entry<String, String> entry : parametros.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue());
		}

		properties.store(output, null);
		
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
