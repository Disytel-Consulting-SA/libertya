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



package org.openXpertya.process;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Field;
import org.openXpertya.model.M_Tab;
import org.openXpertya.util.DB;
import org.openXpertya.util.ErrorOXPSystem;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TabCreateFields extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_Tab_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        p_AD_Tab_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        M_Tab tab = new M_Tab( getCtx(),p_AD_Tab_ID,get_TrxName());

        if( (p_AD_Tab_ID == 0) || (tab == null) || (tab.getID() == 0) ) {
            throw new ErrorOXPSystem( "@NotFound@: @AD_Tab_ID@ " + p_AD_Tab_ID );
        }

        log.info( tab.toString());

        //

        int    count = 0;
        String sql   = "SELECT * FROM AD_Column c " + "WHERE NOT EXISTS (SELECT * FROM AD_Field f " + "WHERE c.AD_Column_ID=f.AD_Column_ID" + " AND c.AD_Table_ID=?"    // #1
                       + " AND f.AD_Tab_ID=?)"    // #2
                       + " AND AD_Table_ID=?"     // #3
                       + " AND NOT (Name LIKE 'Created%' OR Name LIKE 'Updated%')" + " AND IsActive='Y' " + "ORDER BY Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql, get_TrxName() );
            pstmt.setInt( 1,tab.getAD_Table_ID());
            pstmt.setInt( 2,tab.getAD_Tab_ID());
            pstmt.setInt( 3,tab.getAD_Table_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                M_Column column = new M_Column( getCtx(),rs,get_TrxName());

                //

                M_Field field = new M_Field( tab );

                field.setColumn( column );

                if( field.save()) {
					// Se agrega metodo para el traspaso de las traducciones si existen en ad_element_trl
                	field.exportElement2FieldTranslations(field.getAD_Field_ID(), column.getAD_Column_ID());                	
                    addLog( 0,null,null,column.getName());
                    count++;
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return "@Created@ #" + count;
    }    // doIt
}    // TabCreateFields



/*
 *  @(#)TabCreateFields.java   02.07.07
 * 
 *  Fin del fichero TabCreateFields.java
 *  
 *  Versión 2.2
 *
 */
