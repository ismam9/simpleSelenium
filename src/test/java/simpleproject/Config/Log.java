package simpleproject.Config;

import static org.testng.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.testng.Reporter;

/**
 * Clase que crea registros de las acciones ejecutadas durante el proceso de testing.
 */
public class Log extends WebActions {
	
	static BufferedWriter bw = null;
	static FileWriter fw = null;
	static String ruta = null;
	
	private static String strBase64 = "";
	
	/**
	 * Constructor que crea el directorio raíz donde se almacenarán los ficheros de logs.
	 */
	public Log() throws IOException {
		String rutaOutputs = "outputs";
		Path p = Paths.get(rutaOutputs);
		
		String rutaLogs = rutaOutputs + File.separator + "logs";
		File file = new File(rutaLogs);
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				System.out.println("Creando padre");
				file.getParentFile().mkdir();
			}
			file.mkdir();
		}
		
		ruta = System.getProperty("user.dir") + File.separator + "outputs" + File.separator + "logs" + File.separator + "log" + systemDate() + ".txt";
		
	}
	
	/**
	 *  Con este método se mostrará en el Log (un archivo guardado en una carpeta) el resultado del caso.
	 *  También se mostrará por consola y en un reporte de TestNG
	 */
	public static void register(String msg) {

		String log;
		String httpLog = null;
		log = "[" + systemHour() + "] - " + msg;
		System.out.println(log);
		writeLog(log);
		log = "[<em><b><span style=\"color:grey;\">" + systemHour() + "</span></b></em>] - " + msg + "<br/>";
		if(log.contains("[OK]")) {
			httpLog = log.replace("[OK]", "<b><span style=\"color:green;\">[OK]</span></b>");
		}else if(log.contains("[ERROR]")) {
			httpLog = log.replace("[ERROR]", "<b><span style=\"color:red;\">[ERROR]</span></b>");
		}else if(log.contains("[WARNING]")) {
			httpLog = log.replace("[WARNING]", "<b><span style=\"color:orange;\">[WARNING]</span></b>");
		}
		Reporter.log("<br><span style=\"color:black;\">" + httpLog + "</span><br/>");

	}
	
	/**
	 *  Método que retorna la hora del sistema en formato String.
	 */
	public static String systemHour() {
		Date hour = new Date();
		String strDateFormat = "HH_mm_ss"; // El formato de fecha esta especificado
		SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat); // La cadena de formato de fecha se pasa como un
																		// argumento al objeto
		String time = objSDF.format(hour);
		return time;
	}
	
	/**
	 *  Método que retorna la fecha del sistema en formato String.
	 */
	public static String systemDate() {
		Date date = new Date();
		String strDateFormat = "hh_mm_ss---dd_MMM"; // El formato de fecha esta especificado
		SimpleDateFormat objSDF = new SimpleDateFormat(strDateFormat); // La cadena de formato de fecha se pasa como un
																		// argumento al objeto
		String dt = objSDF.format(date);
		return dt;
	}
	
	/**
	 *  Método que escribe en los ficheros de log
	 */
	public static void writeLog(String log) {

		try {
			File fl = new File(ruta);
			if (!fl.exists()) {
				fl.createNewFile();
			}
			fw = new FileWriter(fl.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(log);
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(1 == 2);
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void registerFinal() {
		// TODO Auto-generated method stub
		String log;
		log = "/**********************************************************************************/ \n" +
				"[" + systemHour() +"] - [Cerrando chromedriver]";
		System.out.println(log);
		Reporter.log(log);
		writeLog(log);
	}


}
