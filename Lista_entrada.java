package ar.com.nowait.emedido;

/** Handler para listado.
 * @author Ramon Invarato Men�ndez
 * www.jarroba.es
 */
public class Lista_entrada {
	private int idImagen; 
	private String textoEncima; 
	private String textoDebajo;
	private String RetVal;
	  
	public Lista_entrada ( String textoEncima, String textoDebajo, String tRetVal) {
	    this.idImagen = idImagen; 
	    this.textoEncima = textoEncima; 
	    this.textoDebajo = textoDebajo;
		this.RetVal = tRetVal;
	}
	
	public String get_textoEncima() { 
	    return textoEncima; 
	}
	
	public String get_textoDebajo() { 
	    return textoDebajo; 
	}

	public String get_RetVal() {
		return RetVal;
	}
	
	public int get_idImagen() {
	    return idImagen; 
	} 
}
