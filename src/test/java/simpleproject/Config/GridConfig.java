package simpleproject.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



/**
 * Clase que contiene la configuración base para iniciar la automatización:
 * carga los datos de usuario del fichero externo, los datos de los properties,
 * define las capabilities, genera el objeto con el driver que corresponda
 * dependiendo del navegador a utilizar, configura la creacion del reporte de
 * resultados y cierra el driver de automatización al finalizar el test.
 */

public class GridConfig{

	public static WebDriver driver = null;
	public static Properties config = new Properties();
	public static Properties client = new Properties();
	public static Properties otp = new Properties();

	public static Log log;
	public static WebActions wa;

	// Reporte
	public static ExtentHtmlReporter htmlReporter;
	public static ExtentReports extent;
	public static ExtentTest test;

	public static DataFormatter formatter = new DataFormatter();
	
	public String nombreClase;
	public String[] splitString;
	
	public String dlPath;
	public static String entorno;
	public static String measurementName;
	public static String suiteName;
	
	//Tunnel
	/*SmsCMD smscmd = new SmsCMD();
	SmsRecieve smsreceive = new SmsRecieve();*/

	/**
	 * Método que recibe los parametros desde la Suite para generar el objeto driver
	 * donde se ejecutará el test.
	 * 
	 * @param browser  Indica el tipo de navegador donde se ejecutará el test.
	 * @param platform Indica el sistema operativo del equipo.
	 */
	@Parameters({ "browser", "platform", "port", "dataname", "suitname"})
	@BeforeMethod(alwaysRun = true)
	public void setupDriver(String browser, String platform, @Optional("OptParam1") String port, @Optional("OptParam2") String dataname, @Optional("OptParam3") String suitname) {
		
		String remoteUrl = "";
		entorno = "";
		measurementName = dataname;
		suiteName = suitname;
		 
		try {
			DesiredCapabilities caps = new DesiredCapabilities();
			try {
				config.load(new FileInputStream("config/config.properties"));
				//client.load(new FileInputStream("datos/client.properties"));
				//otp.load(new FileInputStream("config/otp.properties"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			wa = new WebActions();
			log = new Log();

			// Driver según el browser
			if (browser.equalsIgnoreCase("chrome") && platform.equalsIgnoreCase("windows")) {
				
				caps.setPlatform(Platform.WINDOWS);
				
				System.setProperty("webdriver.chrome.driver", config.getProperty("chromePath"));

				Map<String, Object> prefs = new HashMap<String, Object>();

				// Use File.separator as it will work on any OS
				prefs.put("download.default_directory", System.getProperty("user.dir") + File.separator + "descargas");

				// Adding capabilities to ChromeOptions
				ChromeOptions options = new ChromeOptions();
				options.setExperimentalOption("prefs", prefs);
				
				dlPath = System.getProperty("user.dir") + File.separator + "descargas";
				entorno = platform;

				// Launching browser with desired capabilities
				driver = new ChromeDriver(options);

			} else if (browser.equalsIgnoreCase("chrome") && platform.equalsIgnoreCase("remote")) {
				
				remoteUrl = "http://127.0.0.1:" + port +"/wd/hub";
				
				Map<String, Object> prefs = new HashMap<String, Object>();
				
				ProcessBuilder pb = new ProcessBuilder("ls");
				pb.inheritIO();
				pb.directory(new File("bin"));
				pb.start();
				
				//System.getProperty("user.dir")  ->   /repositorio/automatizacion-banca-a-distancia/bm-web/descargas
								
				prefs.put("download.default_directory", File.separator + "tmp" + File.separator);
				prefs.put("profile.default_content_settings.popups", 0);
				
				//Estas prefs permiten que el driver obtenga info del portapapeles desde el navegador (parametro: 1 habilitar, 2 deshabilitar)
				/*prefs.put("profile.content_settings.exceptions.clipboard", getClipBoardSettingsMap(1));*/
				
				ChromeOptions options = new ChromeOptions();
				options.setExperimentalOption("prefs", prefs);
				options.setCapability(CapabilityType.PLATFORM_NAME, Platform.LINUX);
				options.setCapability(CapabilityType.BROWSER_NAME, "chrome");
				
				dlPath = File.separator + "tmp" + File.separator;
				entorno = platform;
				
				driver = new RemoteWebDriver(new URL(remoteUrl), options);
				
				
			} else {
				System.setProperty("webdriver.gecko.driver", config.getProperty("firefoxPath"));
				caps = DesiredCapabilities.firefox();
				driver = new FirefoxDriver();
			}

			// setupReport(platform, browser);
			driver.manage().window().maximize();

//			driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);

		} catch (Exception ex) {
			System.out.println("Operación fallida. No se ha podido configurar el entorno.");
			System.out.println("Mensaje de error: " + ex.getMessage());
			//JOptionPane.showMessageDialog(null, "Operación fallida. No se ha podido configurar el entorno.");
		}

	}
	
	//Este metodo crea la configuracion necesaria para que el driver obtenga los datos del portapapeles desde el navegador
	//SettingValue: 0 es valor por defecto; 1 habilitar; 2 deshabilitar
	private static Map<String,Object> getClipBoardSettingsMap(int settingValue) throws JsonProcessingException {
	    Map<String,Object> map = new HashMap<>();
	    map.put("last_modified",String.valueOf(System.currentTimeMillis()));
	    map.put("setting", settingValue);
	    Map<String,Object> cbPreference = new HashMap<>();
	    cbPreference.put("[*.],*",map);
	    ObjectMapper objectMapper = new ObjectMapper();
	    String json = objectMapper.writeValueAsString(cbPreference);
	    System.out.println("clipboardSettingJson: " + json);
	    return cbPreference;
	}
	
	/*
	@Parameters({ "starttunel" })
	@BeforeSuite
	public void openTunnel(@Optional("opcional") String starttunel) {
		
		System.out.println("¿Abrir tunel?: " + starttunel);
		
		if (!starttunel.equalsIgnoreCase("no")) {
			SmsCMD smscmd = new SmsCMD();
			SmsRecieve smsreceive = new SmsRecieve();

			smsreceive.start();
			//smscmd.start();

		}
		
	}*/

	
	/**
	 * Método que inicia la creación de un reporte de resultados al ejecutar el
	 * test.
	 * 
	 * @param platformName Indica el sistema operativo del equipo.
	 * @param browser      Indica el tipo de navegador donde se ejecutará el test.
	 */
	@Parameters({ "browser", "platform", "suitname" })
	@BeforeTest
	public void setupReport(String browser, String platform, String suitname) {
		File imgBusiness = new File(getClass().getClassLoader().getResource("logo.jpg").getFile().replaceAll("%20", " "));
		File imgCliente = new File(
				getClass().getClassLoader().getResource("logoEmpresa.jpg").getFile().replaceAll("%20", " "));
		String rutaImgBusiness = imageToBase64(imgBusiness);
		String rutaImgEmpresa = imageToBase64(imgCliente);
//		String titulo = "<img src=\"data:image/jpg;base64, " + rutaImgMTP
//				+ "\" alt=\"Logo MTP\" style=\"height:50px;width:auto;\" >" + " <img src=\"data:image/jpg;base64, "
//				+ rutaImgEmpresa + "\" alt=\"Logo BM\" style=\"height:50px;width:auto;float:right;\" >";
		// String titulo = "<img src=\"" + rutaImg + "\" alt=\"Logo Empresa\"
		// height=\"50\" width=\"auto\" >";
		
		String childTags = "<img src=\"data:image/jpg;base64, " + rutaImgBusiness + "\" alt=\"Logo\" class=\"logo-mtp\">"
                +  "<a class=\"bloque\" href=http://svde8071.bancamarch.es/reportesWeb/><img src=\"data:image/jpg;base64, " + rutaImgEmpresa + "\" alt=\"Logo\" class=\"logo-empresa\"></a>"
                +  "<span class=\"app-version\">Entorno Local Web</span>";
		String titulo = "<span class=\"custom-title\">" + childTags + "</span>";

		SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH.mm.ss");
		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
		Date fecha = new Date();

		htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + File.separator + "reportesHTML" + 
		File.separator +formatoFecha.format(fecha)+ File.separator + "Reporte " + suitname + " " +formato.format(fecha)+".html");
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("OS", platform);
		extent.setSystemInfo("Browser", browser);

		// configuration items to change the look and feel
		// add content, manage tests etc
		htmlReporter.config().setDocumentTitle("Reporte Idelista SimpleProject");
		htmlReporter.config().setReportName(titulo);
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setTheme(Theme.STANDARD);
		htmlReporter.config().setTimeStampFormat("dd-MM-yyyy, hh:mm a");
		htmlReporter.loadXMLConfig(System.getProperty("user.dir") + File.separator + "extent-config.xml");
		
		
		//openTunnel();
		
	}

	@DataProvider
	public Object[][] usuario6299888() throws Exception {

		Object[][] arrayObject = getExcelData(System.getProperty("user.dir") + File.separator + 
				"config" + File.separator + "users.xlsx","Sheet1");
		return arrayObject;

	}
	@DataProvider
	public Object[][] usuario4211915() throws Exception {

		Object[][] arrayObject = getExcelData(System.getProperty("user.dir") + File.separator + 
				"config" + File.separator + "users.xlsx","Sheet2");
		return arrayObject;

	}
	
	@DataProvider
	public Object[][] usuario1918119() throws Exception {

		Object[][] arrayObject = getExcelData(System.getProperty("user.dir") + File.separator + 
				"config" + File.separator + "users.xlsx","Sheet3");
		return arrayObject;

	}

	@DataProvider
	public Object[][] usuarioFF() throws Exception {

		Object[][] arrayObject = getExcelData(System.getProperty("user.dir") + File.separator + 
				"config" + File.separator + "users.xlsx","Sheet4");
		return arrayObject;

	}

	public String[][] getExcelData(String fileName, String sheetName)
			throws EncryptedDocumentException, InvalidFormatException {

		String[][] arrayExcelData = null;

		try {

			Workbook workbook = WorkbookFactory.create(new File(fileName));
			Sheet sheet = workbook.getSheet(sheetName);

			int totalNoOfCols = sheet.getRow(0).getPhysicalNumberOfCells();
			int totalNoOfRows = sheet.getPhysicalNumberOfRows();

			//System.out.println("Columnas y filas: " + totalNoOfCols + " - " + totalNoOfRows);

			arrayExcelData = new String[totalNoOfRows - 1][totalNoOfCols];

			for (int i = 0; i < totalNoOfRows - 1; i++) {

				Row row = sheet.getRow(i + 1);

				for (int j = 0; j < totalNoOfCols; j++) {

					if (row == null) {
						arrayExcelData[i][j] = "";
					} else {
						Cell cell = row.getCell(j);

						if (cell == null) {
							arrayExcelData[i][j] = "";
						} else {
							String value = formatter.formatCellValue(cell);
							arrayExcelData[i][j] = value;
						}
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arrayExcelData;
	}

	/**
	 * Método que convierte un archivo de imagen en una cadena de Base64.
	 * 
	 * @param imageFile fichero de imagen a convertir.
	 */
	public String imageToBase64(File imageFile) {

		try {
			System.out.println(imageFile.getPath());
			FileInputStream fis = new FileInputStream(imageFile);
			byte[] bytes = new byte[(int) imageFile.length()];
			fis.read(bytes);
			String base64Img = new String(Base64.encodeBase64(bytes), "UTF-8");
			return base64Img;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error al convertir la imagen a Base 64");
			return "";
		}

	}

	/**
	 * Método que registra en el reporte el resultado de un test.
	 * 
	 * @param result estado del test ejecutado.
	 * @throws IOException
	 */
	@AfterMethod
	public void getResult(ITestResult result) throws IOException {
		
		System.out.println("ITestResult: " + result);
		
//		if (result.getStatus() == ITestResult.FAILURE) {
//			test.log(Status.FAIL, MarkupHelper.createLabel("Test " + result.getName() + " ha fallado.", ExtentColor.RED));
//			test.fail(result.getThrowable());
//		} else if (result.getStatus() == ITestResult.SUCCESS) {
//			test.log(Status.INFO, MarkupHelper.createLabel("Test " + result.getName() + " se ha completado. ", ExtentColor.BLACK));
//		} else {
//			test.log(Status.WARNING, MarkupHelper.createLabel("Test " + result.getName() + " omitido ", ExtentColor.ORANGE));
//			test.warning(result.getThrowable());
//		}
		
		test.log(Status.INFO,
				MarkupHelper.createLabel("Test " + result.getName() + " se ha completado. ", ExtentColor.GREY));
		test.info("Ultima captura: ", MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
		
		setCategory();
		
		test.getModel().setStartTime(getTime(result.getStartMillis()));
        test.getModel().setEndTime(getTime(result.getEndMillis()));
	}
	
    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();      
    }

	public String returnScreenshot() {

		SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
		Date fecha = new Date();

		String folder = "reportesHTML" + File.separator + formatoFecha.format(fecha) + File.separator + "screenshots";
		File capturaPantalla = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		DateFormat df = new SimpleDateFormat("dd-MM-yyyy__hh_mm_ss_SS");

		// Comprobar si la carpeta existe. Si no, se crea
		File folderScreenshots = new File(folder);

		if (!folderScreenshots.exists()) {
			if (!folderScreenshots.getParentFile().exists()) {
				folderScreenshots.getParentFile().mkdir();
			}
			folderScreenshots.mkdir();
		}

		String FechaHora = df.format(new Date());
		String destFile = System.getProperty("user.dir") + File.separator + "reportesHTML" + 
		File.separator +formatoFecha.format(fecha)+ File.separator + "screenshots" + File.separator + FechaHora + ".jpg";
		File target = new File(destFile);
		String destFileRelative = "screenshots" + File.separator + FechaHora + ".jpg";
		try {
			// FileUtils.copyFile(capturaPantalla, new File(folder + File.separator +
			// destFile));
			FileUtils.copyFile(capturaPantalla, target);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFileRelative;
	}

	/**
	 * Método que finaliza la ejecución del driver y escribe el reporte.
	 */
	@AfterClass
	public void tearDown() {
		driver.close();
	}

	@AfterSuite
	public void genReport() {
		try {
			driver.quit();
			extent.flush();
			System.out.println("OK al desconectar");
			//closeTunnel();
		} catch (Exception ex) {
			System.out.println("Error al desconectar");
		} finally {
			log.registerFinal();
		}

	}
	
	/*
	public void closeTunnel() {	
		try {
			smsreceive.stop();
			smscmd.stop();
			//Runtime.getRuntime().exec("cmd /c start cerrartunel.bat");
		} catch (Exception ex) {
			System.out.println("Tunel no existe.");
		}
	}*/
	

	
	public void setCategory() {
		
		nombreClase = this.getClass().getSimpleName();
		splitString = nombreClase.split("_");
		
		test.assignCategory(splitString[0]);
		
	}

}