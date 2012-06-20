package org.openXpertya.model;
//CalloutSqlListOrder.View

//Imports
import java.util.Properties;


public class CalloutSqlListOrder extends CalloutEngine {
	
    /** 
     * Creado Por Dataware 02/06/06
     *
     *Descripción de Método: Hace o no visible el campo SQL 
     *para ordenar las listas / Combos de las ventanas.  
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return null
     */
	 public String View( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
		 //Si no se nos pasa objeto no hacemos nada
		if( value == null ) {
			return "";
		}
		//Obtenemos el ID del elemento:
		Integer Object_Type = ( Integer ) mTab.getValue("AD_Reference_ID");
	    
		//nos creamos la fila corespondiente al id anterior (Se debe haber generado X_AD_Column.java anteriormente)
		X_AD_Reference at = new X_AD_Reference(ctx, Object_Type.intValue(), null);

		if (at.get_Value("Name").equals("Table Direct") || at.get_Value("Name").equals("List"))	{
			//islist = nombre de columna
			mTab.setValue("islist","Y");
		}
		else {
			mTab.setValue("islist","N");
		}
		
		return "";

	 }
}
