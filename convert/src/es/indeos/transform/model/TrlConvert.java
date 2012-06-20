/**
 * Herramienta para importar y exportar ventanas del Application Dictionary 
 * de OpenXpertya 
 *
 * Diseño y desarrollo por Indeos Consultoria S.L.
 */

package es.indeos.transform.model;

public class TrlConvert {
	
	/** El idioma en el que esta esta traduccion */
	private String AD_Language;
	
	/** La columna a la que se refiere */
	private String Column;
	
	/** La traduccion */
	private String Trl;

	/**
	 * Constructor 
	 * @param language
	 * @param column
	 * @param trl
	 */
	public TrlConvert(String language, String column, String trl) {
		super();
		AD_Language = language;
		Column = column;
		Trl = trl;
	}

	/**
	 * @return Devuelve AD_Language.
	 */
	public String getAD_Language() {
		return AD_Language;
	}

	/**
	 * @param language Fija o asigna AD_Language.
	 */
	public void setAD_Language(String language) {
		AD_Language = language;
	}

	/**
	 * @return Devuelve column.
	 */
	public String getColumn() {
		return Column;
	}

	/**
	 * @param column Fija o asigna column.
	 */
	public void setColumn(String column) {
		Column = column;
	}

	/**
	 * @return Devuelve trl.
	 */
	public String getTrl() {
		return Trl;
	}

	/**
	 * @param trl Fija o asigna trl.
	 */
	public void setTrl(String trl) {
		Trl = trl;
	}
	
	

}
