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

import java.util.logging.Level;

import org.openXpertya.model.M_Field;
import org.openXpertya.model.M_Tab;
import org.openXpertya.model.M_Window;
import org.openXpertya.util.ErrorUsuarioOXP;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WindowCopy extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_WindowTo_ID = 0;

    /** Descripción de Campos */

    private int p_AD_WindowFrom_ID = 0;
    
    private int p_AD_Tab_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "AD_Window_ID" )) {
                p_AD_WindowFrom_ID = para[ i ].getParameterAsInt();
            } else if( name.equals( "AD_Tab_ID" )) {
            	p_AD_Tab_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        p_AD_WindowTo_ID = getRecord_ID();
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
        log.info( "doIt - To AD_Window_ID=" + p_AD_WindowTo_ID + ", From=" + p_AD_WindowFrom_ID );

        M_Window from = new M_Window( getCtx(),p_AD_WindowFrom_ID,get_TrxName());

        if( from.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@ (from->) @AD_Window_ID@" );
        }

        M_Window to = new M_Window( getCtx(),p_AD_WindowTo_ID,get_TrxName());

        if( to.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@ (to<-) @AD_Window_ID@" );
        }

        int     tabCount   = 0;
        int     fieldCount = 0;
        M_Tab[] oldTabs    = from.getTabs( false,get_TrxName());

        for( int i = 0;i < oldTabs.length;i++ ) {
            M_Tab oldTab = oldTabs[ i ];
			// Se copia la pestña siempre y cuando no se haya ingresado
			// parámetro de pestaña o si la pestaña seleccionada es la actual
            if(Util.isEmpty(p_AD_Tab_ID, true) || oldTab.getID() == p_AD_Tab_ID){
                M_Tab newTab = new M_Tab( to,oldTab );

                if( newTab.save()) {
                	newTab.copyTranslation(oldTab);
                    tabCount++;

                    // Copy Fields

                    M_Field[] oldFields = oldTab.getFields( false,get_TrxName());

                    for( int j = 0;j < oldFields.length;j++ ) {
                        M_Field oldField = oldFields[ j ];
                        M_Field newField = new M_Field( newTab,oldField );

                        if( newField.save()) {
                        	newField.copyTranslation(oldField);
                            fieldCount++;
                        } else {
                            throw new ErrorUsuarioOXP( "@Error@ @AD_Field_ID@" );
                        }
                    }
                } else {
                    throw new ErrorUsuarioOXP( "@Error@ @AD_Tab_ID@" );
                }
            }
        }

        return "@Copied@ #" + tabCount + "/" + fieldCount;
    }    // doIt
}    // WindowCopy



/*
 *  @(#)WindowCopy.java   02.07.07
 * 
 *  Fin del fichero WindowCopy.java
 *  
 *  Versión 2.2
 *
 */
