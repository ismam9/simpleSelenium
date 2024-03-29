package simpleproject.Config;


/**
 * Esta clase crea objetos de autenticacion cuyos valores seran asignados acorde a lo indicado en el fichero users.xlsx.
 * Una vez creados, se utilizan para el login en la web.
 */

public class Auth extends GridConfig {
	
	public String url;
	public String username;
	public String password;
	
	
	public Auth() {
		
		this.username = username;
		this.password = password;
		this.url = url;
		
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	
	

}
