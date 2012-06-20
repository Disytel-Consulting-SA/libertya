/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2006 de Dataware Sistemas, otras partes son 
 *    CopyRight (c)  2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */

package org.openXpertya.model;

import java.util.Properties;
import org.openXpertya.util.Env;

public class CalloutFile extends CalloutEngine {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */
	
    public String SetADTableID( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {

    	mTab.setValue("Record_ID", null);
    	//mTab.setValue("AD_Unavailability_Type_ID",null);
    	
    	return "";
    }

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */
    
    public String SetRecordID( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
/*
    	// Si el campo esta vacio
    	if( value == null ) {
    		// Recogemos del contexto p_Record_ID
    		String p_Record_ID = ctx.getProperty("p_Record_ID");
        	// Guardamos la varible en el campo del formulario Record_ID
    		//mTab.setValue("Record_ID",Integer.parseInt(p_Record_ID));
    		mTab.setValue("Record_ID",p_Record_ID);
    		if (p_Record_ID==null){
    			p_Record_ID="100000";
    			return "Warning Record_ID is null";
    		}

    		return "";
        }
    		*/
    	return "";
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param WindowNo
     * @param mTab
     * @param mField
     * @param value
     *
     * @return
     */
    
    public String ConfCBPartnerID( Properties ctx,int WindowNo,MTab mTab,MField mField,Object value ) {
    	
    	// Si el campo esta vacio
    	Integer C_BPartner_ID = ( Integer )value;
	  	if ((C_BPartner_ID == null) || (C_BPartner_ID.intValue() == 0) ) {
	  		
	  		//recogemos el id del usuario que ha hecho login
	  		int AD_User_ID = Env.getContextAsInt(ctx,WindowNo,"AD_User_ID" );
	  		
	  		// vemos al BPartner que pertenece
	  		MUser usuario = new MUser(ctx,AD_User_ID,null);
	  		int partner = usuario.getC_BPartner_ID();
	  		
	  		// Si no es cero (como en el caso de System) se inicializa el campo a ese valor
	  		if( partner != 0 ) {
	  			mTab.setValue( "C_BPartner_ID",new Integer( partner ));
            }
	  	}
	  	return "";
    }
}