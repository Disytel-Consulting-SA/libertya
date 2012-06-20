/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.apps.form;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.model.*;
import org.openXpertya.process.*;

import org.openXpertya.util.DB;

/*
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RepairOrderSelect extends SvrProcess {

    /**
     * Constructor de la clase ...
     *
     */

    public RepairOrderSelect() {
        super();
    }    // RepairOrderSelect

    
    int m_C_DocTypeTarget_ID=0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
    	//log.info( " currupio Estoy ProductPriceTemp.Prepare" );
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            if (para[i].getParameter() == null)
				;
            else if("C_DocTypeTarget_ID".equals(name))
            	m_C_DocTypeTarget_ID=para[i].getParameterAsInt();
			else
				log.warning("prepare - Unknown Parameter: " + name);
        }
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected String doIt() {
    	log.info("doIt");
		
		int C_Repair_Order_ID=getRecord_ID();
		int AD_PInstance_ID=getAD_PInstance_ID();
		
		FormFrame ff=new FormFrame();
		ff.storeParameter("C_Repair_Order_ID", new Integer(C_Repair_Order_ID));
		ff.storeParameter("AD_PInstance_ID", new Integer(AD_PInstance_ID));
		ff.storeParameter("C_DocTypeTarget_ID", new Integer(m_C_DocTypeTarget_ID));
		ff.openForm(getFormID("VRepairOrderSelect"));	// TODO: obtener el ID del form
		ff.pack();
		org.openXpertya.apps.AEnv.showCenterScreen(ff);
		
		return "";
    }    // doIt
    
    /**
     * Devuelve el ID del formulario que tiene esta clase
     * 
     * @param classname	nombre de la clase que buscamos
     * 
     * @return	el ID del form o -1 si no lo encuentra
     */
    private int getFormID(String classname)
    {
    	int dev=0;
    	
    	StringBuffer sql=new StringBuffer(	"SELECT AD_Form_ID ")
    								.append("FROM AD_Form ")
    								.append("WHERE ClassName LIKE '%").append(classname).append("%' ");
    	String fsql=MRole.getDefault(getCtx(), false).addAccessSQL(sql.toString(), "AD_Form", MRole.SQL_NOTQUALIFIED, MRole.SQL_RO);
    	dev=DB.getSQLValue(null, fsql);
    	
    	return dev;
    }
    
}    // ProductPriceTemp



/*
 *  @(#)ProductPriceTemp.java   02.07.07
 * 
 *  Fin del fichero ProductPriceTemp.java
 *  
 *  Versión 2.2
 *
 */
