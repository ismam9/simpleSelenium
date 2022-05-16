package simpleproject.Tests;

import org.testng.annotations.Test;

import simpleproject.Config.WebActions;
import simpleproject.WebElements.PagLogin;

public class Login extends WebActions{
	
	@Test(description = "Login")
	public void login() throws Exception {
		test = extent.createTest("Login - Acceso a Idealista");
		/*******************************************************************/
		PagLogin login = new PagLogin();
		/*******************************************************************/
		driver.get("https://www.idealista.com/");
		// Entrando a TEnvio
		//logProcesoActual(" ---- Comprobando ---- ");
		waitSecs(20);
		
		try {
			//waitForVisibility(inicio.BARRANAV);
			lookupElement(login.NAV);
			
			extentReportPass("Se ha cargado barra de navegacion con exito");
		} catch (Exception e) {

			extentReportFail("Error al cargar la barra de navegacion");
		}
		
		try {
			waitForVisibility(login.BUTTON);
			click(login.BUTTON);
			waitSecs(5);
		} catch (Exception e) {
			extentReportFail("Error al acceder al login");
		}

		extentReportPass("[OK] CAPTURA DE PANTALLA ACTUAL");
		
		
		/*******************************************************************/
		/*setExcelFileSheet("Users_OL");
		String desc = "OL_PRE";
		String user = getFieldValue(desc, "Usuario");
		String pass = getFieldValue(desc, "Password");*/
		/******************************************************************/
		//Login signup = new Login();
		
		/*******************************************************************/
		//driver.get("https://www.microsoft.com/es-es/microsoft-365/outlook/email-and-calendar-software-microsoft-outlook");
		/*******************************************************************/

		
		/**
		 * LOGIN
		 */
		/*
		try {
			
		logProcesoActual(" ---- Accediendo a IRIS con usuario y contrasena ---- ");
		waitForVisibility(login.USERNAME);
		lookupElement(login.USERNAME);
		sendKeys(login.USERNAME, user);
		waitForVisibility(login.NEXT);
		click(login.NEXT);
	
		extentReportPass("[OK] CAPTURA DE PANTALLA ACTUAL");
		waitForVisibility(login.PASSWORD);
		lookupElement(login.PASSWORD);
		sendKeys(login.PASSWORD, pass);
		waitForVisibility(login.NEXT);
		click(login.NEXT);
		
		extentReportPass("[OK] CAPTURA DE PANTALLA ACTUAL");
		
		}catch (Exception e) {
			extentReportFail("[FAIL] FALLO EN EL LOGIN");
		}*/
		
	}
}
