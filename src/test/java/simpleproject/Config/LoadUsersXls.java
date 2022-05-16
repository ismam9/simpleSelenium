package simpleproject.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Clase que se encarga de procesar los datos del fichero users.xlsx para utilizarlos en los tests.
 */
public class LoadUsersXls {

	public static String PATH_XLS = "config/users.xlsx";
		
	/**
	 * Método que lee el fichero de formato excel con los datos de Usuario, Contraseña y URL.
	 * Por cada fila con datos, crea un objeto de tipo Auth y lo añade a una Lista de objetos.
	 * Los tests se ejecutarán tantas veces como objetos Auth existan en esta Lista.
	 */
	public List<Auth> getListaUsuarios() throws EncryptedDocumentException, InvalidFormatException, IOException {
	
		Workbook workbook = WorkbookFactory.create(new File(PATH_XLS));
		
		Sheet sheet = workbook.getSheetAt(0);
		
		List<Auth> listaDatos = new ArrayList<Auth>();
		
		for (Row row : sheet) {

			if (row.getRowNum() == 0) {
				continue;
			}
			
			Auth auth = new Auth();
			
			for (Cell cell:row) {
				
				DataFormatter formatter = new DataFormatter();
		        
				switch (cell.getColumnIndex()) {
				
				case 0: 
					auth.setUsername(formatter.formatCellValue(row.getCell(0)));
					break;
				case 1: 
					auth.setPassword(formatter.formatCellValue(row.getCell(1)));
					break;
				case 2: 
					auth.setUrl(formatter.formatCellValue(row.getCell(2)));
					break;
				}
				
			}
			
			listaDatos.add(auth);
			
		}
		
		return listaDatos;
		
	}
	
	
	
}
