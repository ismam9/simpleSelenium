package simpleproject.Config;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.internal.TestResult;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;

import simpleproject.WebElements.PagLogin;

/**
 * Clase que define las acciones que se ejecutarán durante el test.
 */

public class WebActions extends GridConfig {

	public boolean login(String userName, String passWord, String urlAddress) {

		PagLogin login = new PagLogin();

		try {

			driver.navigate().refresh();
			driver.get(urlAddress);

			waitForVisibilityID(login.ID_INPUT_USERNAME);
			sendKeys(login.INPUT_USERNAME, userName);
			sendKeys(login.INPUT_PASSWORD, passWord);
			insertarCaptura();
			click(login.BTN_ACCESO);

			//assertTrue(waitForInvisibility(login.XPATH_MAIN_LOADER));

			/*
			 * if (waitForInvisibility(login.XPATH_MAIN_LOADER)) { extentReportPass("PASS");
			 * 
			 * } else if (!waitForInvisibility(login.XPATH_MAIN_LOADER)) {
			 * extentReportWarning("Timeout"); } else { extentReportFail("FAIL"); }
			 */

			waitForVisibility(login.XPATH_LINK_LOGOUT);
			assertTrue(login.LINK_LOGOUT.isDisplayed());
//			click(login.LINK_LOGOUT);
			waitSecs(5);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * Método que envía una cadena de texto a un elemento.
	 * 
	 * @param elm   Elemento al cual se enviará la cadena de texto. Generalmente un
	 *              input o textarea.
	 * @param texto Cadena de texto que se enviará al elemento.
	 */
	public void sendKeys(WebElement elm, String texto) {
		String msg;
		try {
			elm.clear();
			elm.sendKeys(texto);
//			Screenshot img = new AShot().coordsProvider(new WebDriverCoordsProvider()).takeScreenshot(driver, elm);
//			BufferedImage captura = img.getImage();
//			File archivo = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "captura.jpg");
//			ImageIO.write(captura, "jpg", archivo);
//			String elemento = imageToBase64(archivo);
			msg = "[OK] " + ": \"" + texto + "\" enviado correcto a: " + elm.toString();
//					+ "<img style=\"max-height:350px; max-width:350px\" src=\"data:image/jpg;base64, " + elemento
//					+ "\" width=\"auto\" height=\"auto\">";
			logExtentReport(msg, true, false);
		} catch (Exception ex) {
			msg = "[ERROR] " + ": No se ha podido enviar \"" + texto + "\" al elemento " + elm.toString();
			logExtentReport(msg, false, true);
		}
		Log.register(msg);
	}

	/**
	 * Método que ejecuta un click sobre un elemento.
	 * 
	 * @param elm Elemento al cual se quiere hacer click.
	 */
	public void click(WebElement elm) {
		String msg;
		try {
			Actions actions = new Actions(driver);
			actions.moveToElement(elm).click().perform();
			msg = "[OK] " + ": Click correcto en: " + elm.toString();
			logExtentReport(msg, true, false);
		} catch (Exception e) {
			msg = "[ERROR] " + ": Fallo al dar click: " + elm.toString();
			logExtentReport(msg, false, true);
		}
		Log.register(msg);
	}

	/**
	 * Método que espera a que un elemento sea visible. Este metodo recibe un xpath
	 * en lugar de un WebElement, porque si se espera la visibilidad de este ultimo,
	 * Selenium asume que existe ese WebElement en el DOM antes de que haga la
	 * espera. Si el WebElement no existe en el DOM en el momento de la llamada a
	 * este metodo, por ser un elemento que se genera dinamicamente, el test falla
	 * antes de realizar la espera.
	 * 
	 * @param xpath Ruta xpath del elemento.
	 */
	public void waitForVisibility(String xpath) {
		String msg;
		int espera = 35;
		try {
			WebDriverWait wait = new WebDriverWait(driver, espera);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
			msg = ("[OK] " + ": Espera OK. Elemento " + xpath + " visible");
		} catch (Exception ex) {
			msg = ("[WARNING] " + ": Tiempo de espera superado. Elemento no visible.");
			msg = msg.replace("[WARNING]", "<span style=\"color:orange; font-weight: bold;\"\\>[WARNING]</span>");
			// extentReportWarning(msg);
			// throw new SkipException("Skipping the test method!");
		}
		Log.register(msg);
	}

	public boolean isDisplayed(WebElement elm) {
		try {
			if (elm.isDisplayed()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public void insertarCaptura() {
		try {
			test.info("Pantalla mostrada", MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
		} catch (Exception ex) {
			logExtentReport(ex.getMessage(), true, false);
			test.fail("No se ha podido realizar la captura de pantalla");
		}
	}

	public void logProcesoActual(String step) {
		test.log(Status.DEBUG, "<span style=\"color:#20B2AA; font-weight: bold;\"\\>[PROCESO ACTUAL]</span> " + step);
	}

	/**
	 * Metodo que espera a que un elemento sea invisible. Este metodo recibe un
	 * xpath en lugar de un WebElement, porque si se espera la invisibilidad de este
	 * ultimo, Selenium asume que existe ese WebElement en el DOM antes de que haga
	 * la espera. Si el WebElement no existe en el DOM en el momento de la llamada a
	 * este metodo, por ser un elemento que se genera dinamicamente, el test falla
	 * antes de realizar la espera.
	 * 
	 * @param xpath Ruta xpath del elemento.
	 */
	public boolean waitForInvisibility(String xpath) {
		String msg = "";
		int espera = 80;
		try {
			WebDriverWait wait = new WebDriverWait(driver, espera);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
			msg = ("[OK] " + ": Espera OK. Elemento " + xpath + " es ahora invisible.");
			return true;
		} catch (Exception ex) {
			msg = ("[WARNING] " + ": Tiempo de espera superado (" + espera + "seg.). Elemento se mantiene visible.");
			msg = msg.replace("[WARNING]", "<span style=\"color:orange; font-weight: bold;\"\\>[WARNING]</span>");
			extentReportWarning(msg);
			return false;
		} finally {
			Log.register(msg);
		}

	}

	/**
	 * Metodo que espera a que un elemento sea visible. Este metodo recibe un ID en
	 * lugar de un WebElement, porque si se espera la visibilidad de este ultimo,
	 * Selenium asume que existe ese WebElement en el DOM antes de que haga la
	 * espera. Si el WebElement no existe en el DOM en el momento de la llamada a
	 * este metodo, por ser un elemento que se genera dinamicamente, el test falla
	 * antes de realizar la espera.
	 * 
	 * @param id ID del elemento.
	 */
	public void waitForVisibilityID(String id) {
		String msg;
		int espera = 35;
		try {
			WebDriverWait wait = new WebDriverWait(driver, espera);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
			msg = ("[OK] " + ": Espera OK. Elemento " + id + " visible");
		} catch (Exception ex) {
			msg = ("[WARNING] " + ": Tiempo de espera superado. Elemento no visible.");
			msg = msg.replace("[WARNING]", "<span style=\"color:orange; font-weight: bold;\"\\>[WARNING]</span>");
			// extentReportWarning(msg);
			// throw new SkipException("Skipping the test method!");
		}
		Log.register(msg);
	}

	/**
	 * Metodo que espera a que un elemento sea visible. Este metodo recibe un
	 * WebElement. Se recomienda el uso de este metodo para elementos que existen en
	 * el DOM, aunque no sean visibles. Si son elementos generados dinamicamente, se
	 * recomienda utilizar los metodos que utilizan xpath o ID.
	 * 
	 * @param elm Objeto WebElement sobre el cual se quiere esperar su visibilidad.
	 */
	public void waitForVisibility(WebElement elm) {
		String msg;
		int espera = 35;
		try {
			WebDriverWait wait = new WebDriverWait(driver, espera);
			wait.until(ExpectedConditions.visibilityOf(elm));
			msg = ("[OK] " + ": Espera OK. Elemento " + elm.toString() + " visible");
		} catch (Exception ex) {
			msg = ("[WARNING] " + ": Tiempo de espera superado. Elemento no visible.");
			msg = msg.replace("[WARNING]", "<span style=\"color:orange; font-weight: bold;\"\\>[WARNING]</span>");
			// extentReportWarning(msg);
			// throw new SkipException("Skipping the test method!");
		}
		Log.register(msg);
	}

	/**
	 * Metodo que selecciona dias aleatorios del calendario de trasnferencia
	 * periodica por fechas.
	 */

	public WebElement randomCalendarDay(String xpathElms) {

		List<WebElement> dias = driver.findElements(By.xpath(xpathElms));

		Random r = new Random();
		int valorDado = r.nextInt(28) + 1; // Entre 0 y 29, mas 1.

		WebElement diaSeleccionado = dias.get(valorDado);

		return diaSeleccionado;
	}

	public WebElement primerDiaCalendarDay(String xpathElms) {

		List<WebElement> dias = driver.findElements(By.xpath(xpathElms));

		WebElement diaSeleccionado = dias.get(1);

		return diaSeleccionado;
	}

	/**
	 * Metodo que espera a que un elemento sea clickable. Este metodo recibe un
	 * WebElement. Se recomienda el uso de este metodo para elementos que existen en
	 * el DOM, aunque no sean visibles. Si son elementos generados dinamicamente,
	 * cuando este metodo busque ese WebElement, si no existe en el DOM, retornara
	 * una NoSuchElementException.
	 * 
	 * @param elm Objeto WebElement sobre el cual se quiere esperar para hacer click
	 *            sobre el.
	 */
	public void waitAndClick(WebElement elm) {
		String msg;
		int espera = 35;
		try {
			WebDriverWait wait = new WebDriverWait(driver, espera);
			wait.until(ExpectedConditions.elementToBeClickable(elm));
			msg = ("[OK] " + ": Elemento clickable " + elm.toString() + " visible");
			waitSecs(3);
			elm.click();
		} catch (Exception ex) {

			msg = ("[WARNING] " + ": Tiempo de espera superado (" + espera + "seg.). Elemento se mantiene visible.");
			msg = msg.replace("[WARNING]", "<span style=\"color:orange; font-weight: bold;\"\\>[WARNING]</span>");
			extentReportWarning(msg);
		}
		Log.register(msg);
	}

	/**
	 * Metodo que espera a que un elemento sea clickable. Este metodo recibe un
	 * xpath. Se recomienda el uso de este metodo para elementos generados
	 * dinamicamente en el DOM.
	 * 
	 * @param xpath Localizador del elemento sobre el cual se quiere esperar para
	 *              hacer click.
	 */
	public void waitAndClick(String xpath) {
		String msg;
		int espera = 35;
		try {
			WebDriverWait wait = new WebDriverWait(driver, espera);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
			msg = ("[OK] " + ": Elemento clickable " + xpath + " visible");
			waitSecs(3);
			driver.findElement(By.xpath(xpath)).click();
		} catch (Exception ex) {

			msg = ("[ERROR] " + ": Tiempo de espera superado. Elemento no clickable.");
		}
		Log.register(msg);
	}

	/**
	 * Metodo que establece una espera explicita durante la ejecucion de un test.
	 * 
	 * @param secs Cantidad de segundos a esperar.
	 */
	public void waitSecs(int secs) {
		String msg;
		try {
			Thread.sleep(secs * 1000);
			msg = ("[OK] " + ": Espera de " + secs + " seg. realizada.");

		} catch (Exception ex) {
			msg = ("[ERROR] " + ": Error al realizar la espera.");

		}
		Log.register(msg);
	}

	/**
	 * Metodo que comprueba si un elemento existe en el DOM. Recibe un xpath para
	 * obtener la cantidad de elementos con esas caracteristicas y con ello evaluar
	 * su existencia.
	 * 
	 * @param xpath Localizador del elemento.
	 */
	public boolean exists(String xpath) {
		String msg = "";
		try {
			int cant = driver.findElements(By.xpath(xpath)).size();

			if (cant == 0) {
				msg = ("[OK] " + ": Busqueda correcta. El elemento no existe.");
				return false;
			} else {
				msg = ("[OK] " + ": Busqueda correcta. El elemento existe (" + cant + ")");
				return true;
			}

		} catch (Exception ex) {
			msg = ("[ERROR] " + ": Error al buscar existencia del elemento");
			return false;
		} finally {
			Log.register(msg);
		}

	}

	/**
	 * Metodo que comprueba si un fichero ha sido descargado en la ruta
	 * especificada.
	 * 
	 * @param downloadPath Ruta del directorio a comprobar.
	 * @param fileName     Nombre de fichero a buscar dentro del directorio.
	 */
	public boolean isFileDownloaded(String downloadPath, String fileName) {
		File dir = new File(downloadPath);
		File[] dirContents = dir.listFiles();

		for (int i = 0; i < dirContents.length; i++) {
			System.out.println("Isfiledownloaded encontrados: " + dirContents[i].getAbsolutePath());
			if (dirContents[i].getName().equals(fileName)) {
				System.out.println("Fichero " + fileName + " encontrado.");
				dirContents[i].delete();
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo que retorna el texto contenido en un WebElement.
	 * 
	 * @param elm WebElement del cual se quiere obtener el texto.
	 * @return Texto localizado dentro del WebElement.
	 */
	public String getText(WebElement elm) {
		String msg;
		String texto = "";
		try {
			texto = elm.getText();
			msg = "[OK] " + "Texto obtenido: \"" + texto + "\" del elemento " + elm.getTagName();
		} catch (Exception ex) {
			msg = "[ERROR] " + ": No se ha podido obtener el texto de " + elm.getTagName();
		}

		return texto;

	}

	/**
	 * Metodo que cambia el foco al frame indicado.
	 * 
	 * @param frameId Identificador del frame a enfocar.
	 */
	public void switchFrame(String frameId) {
		String msg;
		try {
			driver.switchTo().frame(frameId);
			msg = "[OK] " + ": Cambio al frame " + frameId + " realizado correcto";
		} catch (Exception e) {
			msg = "[ERROR] " + ": Error al realizar un cambio al frame " + frameId;
		}
		Log.register(msg);
	}

	/**
	 * Metodo que ejecuta un desplazamiento al tope superior de la pagina.
	 */
	public static void scrollTop() {
		try {
			((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollTop)");
			Log.register("[OK][" + Thread.currentThread().getStackTrace()[1].getClassName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]"
					+ ": Page was scrolled to the top");
		} catch (Exception e) {
			Log.register("[ERROR][" + Thread.currentThread().getStackTrace()[1].getClassName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]"
					+ ": Page wasn't scrolled to the top: " + e.getMessage());
		}
	}

	/**
	 * Metodo que ejecuta un desplazamiento al tope inferior de la pagina.
	 */
	public static void scrollBottom() {
		try {
			((JavascriptExecutor) driver).executeScript("window.scrollBy(0,document.body.scrollHeight)");
			Log.register("[OK][" + Thread.currentThread().getStackTrace()[1].getClassName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]"
					+ ": Page was scrolled to the bottom");
		} catch (Exception e) {
			Log.register("[ERROR][" + Thread.currentThread().getStackTrace()[1].getClassName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]"
					+ ": Page wasn't scrolled to the bottom: " + e.getMessage());
		}
	}

	/**
	 * Metodo que ejecuta un desplazamiento de la pagina hasta la ubicacion del
	 * elemento indicado.
	 * 
	 * @param elm WebElement hacia el cual se quiere hacer el desplazamiento.
	 */
	public static void scrollTo(WebElement elm) {
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", elm);
			Log.register("[OK][" + Thread.currentThread().getStackTrace()[1].getClassName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]"
					+ ": Page was scrolled to the element");
		} catch (Exception e) {
			Log.register("[ERROR][" + Thread.currentThread().getStackTrace()[1].getClassName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]" + "["
					+ Thread.currentThread().getStackTrace()[1].getMethodName() + "]"
					+ ": Page wasn't scrolled to the element: " + e.getMessage());
		}
	}

	public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	/**
	 * Metodo que genera un alias aleatorio.
	 * 
	 * @param len Longitud de caracteres del alias que se quiere generar.
	 */
	public static String generateAlias(int len) {
		int numAleatorio = (int) (Math.random() * 10);
		StringBuilder builder = new StringBuilder(len);

		for (int i = 0; i < (len - 1); i++) {
			int index = (int) (ALPHABET.length() * Math.random());
			builder.append(ALPHABET.charAt(index));
		}
		StringBuilder alias = builder.append(String.valueOf(numAleatorio));

		return String.valueOf(alias);
	}

	/**
	 * Metodo que genera capturas de pantalla y las almacena en el directorio de
	 * reportes.
	 * 
	 * @return Cadena con la ubicacion de la captura de pantalla.
	 */

	/**
	 * Metodos para extentReport
	 * 
	 * @author alejandro.morenor
	 */
	public void extentReportInfo(String texto) {
		try {
			waitSecs(3);
			test.info(texto, MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
			Reporter.getCurrentTestResult().setAttribute("custom_result", "INFO");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void extentReportPass(String texto) {
		try {
			waitSecs(3);
			test.pass(texto, MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
			Reporter.getCurrentTestResult().setAttribute("custom_result", "PASS");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void extentReportFail(String texto) {
		try {
			waitSecs(3);
			test.fail(texto, MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
			Reporter.getCurrentTestResult().setAttribute("custom_result", "FAIL");
			fail();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void extentReportCoordenadas(String texto) {
		try {
			waitSecs(3);
			test.fatal(texto, MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
			Reporter.getCurrentTestResult().setAttribute("custom_result", "COORD");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void extentReportError(String texto) {
		try {
			waitSecs(3);
			test.error(texto, MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
			Reporter.getCurrentTestResult().setAttribute("custom_result", "ERROR");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void extentReportSkip(String texto) {
		try {
			waitSecs(3);
			test.skip(texto, MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
			Reporter.getCurrentTestResult().setAttribute("custom_result", "SKIPPED");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Añadido un warning para que en caso de timeOut en la web no lo tome como test
	// fallido en el reporte grafico.
	public void extentReportWarning(String texto) {
		try {
			waitSecs(3);
			test.warning(texto, MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
			Reporter.getCurrentTestResult().setAttribute("custom_result", "WARNING");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void extentReportNotCompleted(String texto) {
		try {
			waitSecs(3);
			test.skip(texto, MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
			Reporter.getCurrentTestResult().setAttribute("custom_result", "SKIPPED");
			// throw new SkipException (texto);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Metodo que selecciona la primera transferencia de la lista de ultimas
	// transferencias
	public WebElement primerElemento(List<WebElement> elms) throws IOException {
		return elms.get(0);
	}

	// Forzar captura cuando quieras
//	public void insertarCaptura() {
//        try {
//            test.info("Captura OK", MediaEntityBuilder.createScreenCaptureFromPath(returnScreenshot()).build());
//        }catch(Exception ex) {
//            test.fail("Captura KO");
//        }
//    }

	/**
	 * Metodo para mostrar con colores los steps
	 * 
	 * @param texto   -> El texto a mostrar
	 * @param exito   -> Segun se muestra el OK o el ERROR
	 * @param captura -> Segun si queremos que se haga una captura
	 * @author alejandro.morenor
	 */
	public void logExtentReport(String texto, boolean exito, boolean captura) {

		String reporteHTML = "";
		if (texto.contains("[OK]")) {
			reporteHTML = texto.replace("[OK]", "<span style=\"color:limegreen; font-weight: bold;\"\\>[OK]</span>");
		} else if (texto.contains("[ERROR]")) {
			reporteHTML = texto.replace("[ERROR]", "<span style=\"color:red; font-weight: bold;\"\\>[ERROR]</span>");
		} else if (texto.contains("[WARNING]")) {
			reporteHTML = texto.replace("[WARNING]",
					"<span style=\"color:orange; font-weight: bold;\"\\>[WARNING]</span>");
		} else if (texto.contains("[NOT COMPLETED]")) {
			reporteHTML = texto.replace("[NOT COMPLETED]",
					"<span style=\"color:blue; font-weight: bold;\"\\>[NOT COMPLETED]</span>");
		} else if (texto.contains("[COORDENADAS]")) {
			reporteHTML = texto.replace("[COORDENADAS]",
					"<span style=\"color:black; font-weight: bold;\"\\>[COORDENADAS]</span>");
		}
		try {
			if (exito == true && captura == true) {
				// test.log(Status.PASS, test.addScreenCaptureFromPath(returnScreenshot()) +
				// reporteHTML);
				extentReportPass(reporteHTML);
			} else if (exito == true && captura == false) {
				test.log(Status.PASS, reporteHTML);
			} else if (exito == false && captura == true) {
				// test.log(Status.FAIL, test.addScreenCaptureFromPath(returnScreenshot()) +
				// reporteHTML);
				extentReportFail(reporteHTML);
			} else if (exito == false && captura == false) {
				test.log(Status.FAIL, reporteHTML);
			} else {
				test.log(Status.SKIP, reporteHTML);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

//	public void finalizarTransferencia (PagTransfPer transfTrasp) {
//		boolean interruptor = WebActions.isOtp();
//		if (interruptor == true) {
//			waitForVisibility(transfTrasp.BTN_VALIDAR);
//			waitSecs(3);
//			click(transfTrasp.BTN_VALIDAR);
//			logProcesoActual(" ---- APERTURA DE TUNEL | Servicio SMS Nexmo ---- ");
//			logProcesoActual(" ---- Recibiendo codigo SMS .... ... .. . ---- ");
//			logProcesoActual(" ---- Codigo: [" + SmsRecieve.codigoSMS + "] ---- ");
//			System.out.println("Codigo: " + SmsRecieve.codigoSMS);
//			waitForVisibility(transfTrasp.INPUT_CONFIRMAR_TRANSF);
//			sendKeys(transfTrasp.INPUT_CONFIRMAR_TRANSF, SmsRecieve.codigoSMS);
//			waitSecs(2);
//			insertarCaptura();
//			waitSecs(2);
//			click(transfTrasp.BTN_CONTINUAR);
//			waitSecs(3);
//			waitForVisibility(transfTrasp.MSG_TRANSF_CORRECTA);
//			assertTrue(transfTrasp.MSG_TRANSF_CORRECTA.isDisplayed());
//			waitSecs(2);
//			extentReportPass("Transferencia Instantanea realizada correctamente");
//		}else {
//			String notCompleted = "[NOT COMPLETED]";
//            notCompleted = notCompleted.replace("[NOT COMPLETED]", "<span style=\"color:#33ACFF; font-weight: bold;\"\\>[NOT COMPLETED]</span>");
//            extentReportNotCompleted(notCompleted + " Prueba no finalizada a falta de servicio SMS");
//		}
//	}

	public void lookupElement(WebElement elm) throws Exception {
		try {
			assertTrue(elm.isDisplayed());
			Log.register("[OK][" + this.getClass().getSimpleName() + "][El elemento " + elm.toString() + " existe]");
			test.log(Status.PASS, "<span style=\"color:#33cc33; font-weight: bold;\"\\>[OK]</span>" + "El elemento "
					+ elm.toString() + " existe");
		} catch (Exception ex) {
			Log.register(
					"[ERROR][" + this.getClass().getSimpleName() + "][El elemento " + elm.toString() + " no existe");
			test.fail(
					"<span style=\"color:#ff0000; font-weight: bold;\"\\>[KO]</span>" + "El elemento " + " no existe");
			throw new Exception("Elemento no existe");
		}
	}

	public void leerIdentificadorPDF(File documento) throws IOException {

		PDDocument document = PDDocument.load(documento);

		try {
			document.getClass();

			if (!document.isEncrypted()) {

				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);

				PDFTextStripper tStripper = new PDFTextStripper();

				String pdfFileInText = tStripper.getText(document);
				// System.out.println("Text:" + st);

				// split by whitespace
				String lines[] = pdfFileInText.split("\\r?\\n");
				System.out.println(lines);
				int cont = 0;
				for (String line : lines) {
					
						test.log(Status.PASS, "<span style=\"color:#33cc33; font-weight: bold;\"\\>[OK]</span>"
								+ " Se ha encontrado el fichero PDF con los datos: " + line);
						cont++;
					

				}
				if (cont == 0) {
					test.log(Status.ERROR, "<span style=\"color:#ff0000; font-weight: bold;\"\\>[KO]</span>"
							+ "No se encuentran datos en el fichero PDF");
				}
				document.close();
			}

		} catch (Exception e) {
			Log.register("[ERROR][" + this.getClass().getSimpleName() + "][No se puede abrir el fichero " + documento);
		} finally {
			document.close();
		}
	}

	public void leerTarjetaPDF(File documento) throws IOException {

		PDDocument document = PDDocument.load(documento);
		
		try {
			document.getClass();

			if (!document.isEncrypted()) {

				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);

				PDFTextStripper tStripper = new PDFTextStripper();

				String pdfFileInText = tStripper.getText(document);
				// System.out.println("Text:" + st);

				// split by whitespace
				String lines[] = pdfFileInText.split("\\r?\\n");
				//int cont = 1;
				for (String line : lines) {
					//if (line.charAt(0) == '*' && line.charAt(1) == '*') {
						test.log(Status.PASS, "<span style=\"color:#33cc33; font-weight: bold;\"\\>[OK]</span>"
								+ " Se ha encontrado el fichero PDF con los datos de: " + line);
						//cont++;
					//} else {
					//	cont = 0;
					//}

				}
				//if (cont == 0) {
					//test.log(Status.ERROR, "<span style=\"color:#ff0000; font-weight: bold;\"\\>[KO]</span>"
					//		+ "No se encuentran datos en el fichero PDF");
				//}
				document.close();
			}

		} catch (Exception e) {
			Log.register("[ERROR][" + this.getClass().getSimpleName() + "][No se puede abrir el fichero " + documento);
		} finally {
			document.close();
		}
	}

	public void leerIdentificadorExcel(File excel)
			throws EncryptedDocumentException, InvalidFormatException, IOException {

		try {
			Workbook workbook = WorkbookFactory.create(excel);
			Sheet sheet = workbook.getSheetAt(0);
			int cont = 0;
			for (Row row : sheet) {
				if (row.getRowNum() == 1) {
					test.log(Status.PASS,
							"<span style=\"color:#33cc33; font-weight: bold;\"\\>[OK]</span>"
									+ " Se ha encontrado el fichero Excel con los datos: "
									+ row.getCell(0).getStringCellValue());
					cont++;
				}
			}
			if (cont == 0) {
				test.log(Status.ERROR, "<span style=\"color:#ff0000; font-weight: bold;\"\\>[KO]</span>"
						+ "No se encuentran datos en el fichero Excel");
			}
			workbook.close();
		} catch (Exception e) {
			Log.register("[ERROR][" + this.getClass().getSimpleName() + "][No se puede abrir el fichero " + excel);
		}
	}

	public void leerIdentificadorExcelFF(File excel)
			throws EncryptedDocumentException, InvalidFormatException, IOException {

		try {
			Workbook workbook = WorkbookFactory.create(excel);
			Sheet sheet = workbook.getSheetAt(0);
			int cont = 0;
			for (Row row : sheet) {
				System.out.println("linea" + cont);
				if (row.getRowNum() == 2) {
					test.log(Status.PASS,
							"<span style=\"color:#33cc33; font-weight: bold;\"\\>[OK]</span>"
									+ " Se ha encontrado el fichero Excel con los datos: "
									+ row.getCell(0).getStringCellValue());

				} else {
					cont++;
				}
			}
			if (cont == 0) {
				test.log(Status.ERROR, "<span style=\"color:#ff0000; font-weight: bold;\"\\>[KO]</span>"
						+ "No se encuentran datos en el fichero Excel");
			}
			workbook.close();
		} catch (Exception e) {
			Log.register("[ERROR][" + this.getClass().getSimpleName() + "][No se puede abrir el fichero " + excel);
		}
	}

	public boolean runOTP() {

		System.out.println("runOTP: " + this.getClass().getSimpleName());

		Map<String, String> epicas = new HashMap<>();
		epicas.put("Transferencias_Instantanea", otp.getProperty("validar.prueba.sms.transferencia.instantanea"));
		epicas.put("Transferencias_Internacional", otp.getProperty("validar.prueba.sms.transferencia.internacional"));
		epicas.put("Transferencias_Puntual", otp.getProperty("validar.prueba.sms.transferencia.interior"));
		epicas.put("Transferencias_Reutilizar", otp.getProperty("validar.prueba.sms.transferencia.interior"));
		epicas.put("Transferencias_Traspaso", otp.getProperty("validar.prueba.sms.transferencia.interior"));
		epicas.put("Inversion_SuscripcionPuntual", otp.getProperty("validar.prueba.sms.inversion.suscripcionpuntual"));
		epicas.put("Perfil_CambioDeAlias", otp.getProperty("validar.prueba.sms.perfil.cambiaralias"));
		epicas.put("Perfil_CambioDePIN", otp.getProperty("validar.prueba.sms.perfil.cambiarpin"));

		String currentCheck = epicas.get(this.getClass().getSimpleName());

		Log.register("[" + this.getClass().getSimpleName() + "]" + "[ Validacion principal -> "
				+ otp.getProperty("validar.pruebas.sms") + " ]");
		Log.register("[" + this.getClass().getSimpleName() + "]" + "[¿Validar pruebas SMS? -> " + currentCheck + "]");

		if (otp.getProperty("validar.pruebas.sms").equalsIgnoreCase("maybe")) {

			if (currentCheck.equalsIgnoreCase("true")) {
				return true;
			}
		} else if (otp.getProperty("validar.pruebas.sms").equalsIgnoreCase("false")) {
			return false;
		}
		return false;
	}

	// Metodo para retornar el contenido del portapapeles dependiendo del entorno en
	// el que se esté ejecutando la prueba
	public String contenidoPortapapeles(String entorno) {

		if (entorno.equalsIgnoreCase("windows")) {

			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			String result = "";

			try {
				result = (String) clipboard.getData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;

		} else if (entorno.equalsIgnoreCase("remote")) {

			String localClipboardData = this.getCBContents();

			return localClipboardData;

		} else {
			return "";
		}

	}

	// Obtiene el contenido del portapeles desde el propio navegador mediante
	// JavaScript.
	public String getCBContents() {

		((JavascriptExecutor) driver).executeScript(
				"async function getCBContents() { try { window.cb = await navigator.clipboard.readText(); console.log(\"Pasted content: \", window.cb); } catch (err) { console.error(\"Failed to read clipboard contents: \", err); window.cb = \"Error : \" + err; } } getCBContents();");
		Object content = ((JavascriptExecutor) driver).executeScript("return window.cb;");

		return Objects.isNull(content) ? "null" : content.toString();
	}

	// Metodo para simular la operacion Pegar con combinación de teclas
	public void manualClipboard(WebElement elemento) {

		Actions actions = new Actions(driver);
		actions.sendKeys(Keys.chord(Keys.LEFT_CONTROL, "v")).build().perform();

	}

	public String leerCoordenadas(String coordenada) {
		final String[][] matriz = { { "90", "50", "12", "76", "50", "06", "65", "27", "91", "66" },
				{ "66", "22", "81", "42", "05", "83", "38", "96", "57", "20" },
				{ "20", "54", "12", "72", "35", "16", "70", "28", "87", "33" },
				{ "33", "87", "43", "03", "64", "03", "59", "79", "19", "75" },
				{ "75", "94", "83", "35", "48", "08", "51", "63", "23", "67" },
				{ "21", "78", "37", "93", "52", "53", "09", "68", "24", "82" },
				{ "32", "84", "39", "97", "11", "15", "69", "26", "80", "31" },
				{ "41", "46", "55", "14", "13", "25", "40", "71", "85", "92" },
				{ "89", "02", "18", "44", "62", "10", "36", "29", "17", "30" },
				{ "56", "77", "61", "88", "58", "07", "45", "34", "47", "86" } };
		int fila = 0;
		String columna = coordenada.substring(1);
		int columnaInt = Integer.parseInt(columna.trim());
		switch (coordenada.charAt(0)) {
		case 'A':
			fila = 0;
			break;
		case 'B':
			fila = 1;
			break;
		case 'C':
			fila = 2;
			break;
		case 'D':
			fila = 3;
			break;
		case 'E':
			fila = 4;
			break;
		case 'F':
			fila = 5;
			break;
		case 'G':
			fila = 6;
			break;
		case 'H':
			fila = 7;
			break;
		case 'I':
			fila = 8;
			break;
		case 'J':
			fila = 9;
			break;
		}
		String valor = matriz[fila][columnaInt - 1];
		return valor;
	}

	public void leerIdentificadorPDFCuentas(File documento) throws IOException {

		PDDocument document = PDDocument.load(documento);

		try {
			document.getClass();

			if (!document.isEncrypted()) {

				PDFTextStripperByArea stripper = new PDFTextStripperByArea();
				stripper.setSortByPosition(true);

				PDFTextStripper tStripper = new PDFTextStripper();

				String pdfFileInText = tStripper.getText(document);
				// System.out.println("Text:" + st);

				// split by whitespace
				String lines[] = pdfFileInText.split("\\r?\\n");
				int cont = 0;
				for (String line : lines) {
					int pagina = 1;
					if (line.charAt(0) == 'E' && line.charAt(1) == 'S') {
						test.log(Status.PASS, "<span style=\"color:#33cc33; font-weight: bold;\"\\>[OK]</span>"
								+ "Página " + pagina + ": " + line);
						cont++;
						pagina++;
					}

				}
				if (cont == 0) {
					test.log(Status.ERROR, "<span style=\"color:#ff0000; font-weight: bold;\"\\>[KO]</span>"
							+ "No se encuentran datos en el fichero PDF");
				}
				document.close();
			}

		} catch (Exception e) {
			Log.register("[ERROR][" + this.getClass().getSimpleName() + "][No se puede abrir el fichero " + documento);
		} finally {
			document.close();
		}
	}

	public void leerIdentificadorExcelCuentas(File excel)
			throws EncryptedDocumentException, InvalidFormatException, IOException {

		try {
			Workbook workbook = WorkbookFactory.create(excel);
			Sheet sheet = workbook.getSheetAt(0);
			Sheet sheet2 = workbook.getSheetAt(1);
			//int cont = 0;
			for (Row row : sheet) {
					test.log(Status.PASS, "<span style=\"color:#33cc33; font-weight: bold;\"\\>[OK]</span>"
							+ " Cuenta con movimientos en la Hoja 1: " + row.getCell(0).getStringCellValue());
				
			}
			for (Row row : sheet2) {
				//if (row.getRowNum() == 1) {
					test.log(Status.PASS, "<span style=\"color:#33cc33; font-weight: bold;\"\\>[OK]</span>"
							+ " Cuenta con movimientos en la Hoja 2: " + row.getCell(0).getStringCellValue());
				//	cont++;
				//}
			}
			//if (cont == 0) {
				test.log(Status.ERROR, "<span style=\"color:#ff0000; font-weight: bold;\"\\>[KO]</span>"
						+ "No se encuentran datos en el fichero Excel");
			//}
			workbook.close();
		} catch (Exception e) {
			Log.register("[ERROR][" + this.getClass().getSimpleName() + "][No se puede abrir el fichero " + excel);
		}
	}

}
