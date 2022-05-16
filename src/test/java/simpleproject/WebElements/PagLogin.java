package simpleproject.WebElements;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import simpleproject.Config.WebActions;

public class PagLogin extends WebActions {
	
	 public PagLogin() {
	        PageFactory.initElements(driver, this);
	 }  
	 
	 @FindBy(id = "loginForm-user")
	 public WebElement INPUT_USERNAME;
	 
	 public String ID_INPUT_USERNAME = "loginForm-user";
	 
	 @FindBy(id = "loginForm-pass")
	 public WebElement INPUT_PASSWORD;
	 
	 @FindBy(xpath = "//*[@id=\"wrapper\"]/div/grace-login/div/div[2]/div[1]/main/div/grace-main-login/form/button")
	 public WebElement BTN_ACCESO;
	 
	 @FindBy(xpath = "/html/body/grace-root/header/div/grace-utility-nav/div/div/ul/li[4]/a")
	 public WebElement LINK_LOGOUT;
	 
	 public String XPATH_LINK_LOGOUT = "/html/body/grace-root/header/div/grace-utility-nav/div/div/ul/li[4]/a";
	 
	 @FindBy(xpath = "//span[text()='El identificador de usuario o contraseña es incorrecto']")
	 public WebElement SPAN_ERROR_LOGIN;
	 
	 public String XPATH_SPAN_ERROR_LOGIN = "//span[text()='El identificador de usuario o contraseña es incorrecto']";
	 
	 @FindBy(xpath = "//*[@id=\"main-header\"]")
	 public WebElement NAV;
	 
	 @FindBy(xpath = "//*[@id=\"btn-free-search\"]")
	 public WebElement BUTTON;
}
