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
import org.openXpertya.util.ErrorUsuarioOXP;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TabCopy extends SvrProcess {

    /** Descripción de Campos */

    private int p_AD_TabTo_ID = 0;

    /** Descripción de Campos */

    private int p_AD_TabFrom_ID = 0;

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
            } else if( name.equals( "AD_Tab_ID" )) {
                p_AD_TabFrom_ID = para[ i ].getParameterAsInt();
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_AD_TabTo_ID = getRecord_ID();
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
        log.info( "To AD_Tab_ID=" + p_AD_TabTo_ID + ", From=" + p_AD_TabFrom_ID );

        M_Tab from = new M_Tab( getCtx(),p_AD_TabFrom_ID,get_TrxName());

        if( from.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@ (from->) @AD_Tab_ID@" );
        }

        M_Tab to = new M_Tab( getCtx(),p_AD_TabTo_ID,get_TrxName());

        
        if( to.getID() == 0 ) {
            throw new ErrorUsuarioOXP( "@NotFound@ (to<-) @AD_Tab_ID@" );
        }

        if( from.getAD_Table_ID() != to.getAD_Table_ID()) {
            throw new ErrorUsuarioOXP( "@Error@ @AD_Table_ID@" );
        }
        to.copyTranslation(from);
        int       count     = 0;
        M_Field[] oldFields = from.getFields( false,get_TrxName());

        for( int i = 0;i < oldFields.length;i++ ) {
            M_Field oldField = oldFields[ i ];
            M_Field newField = new M_Field( to,oldField );

            if( newField.save()) {
            	newField.copyTranslation(oldField);
                count++;
            } else {
                throw new ErrorUsuarioOXP( "@Error@ @AD_Field_ID@" );
            }
        }

        return "@Copied@ #" + count;
    }    // doIt
}    // TabCopy



/*
 *  @(#)TabCopy.java   02.07.07
 * 
 *  Fin del fichero TabCopy.java
 *  
 *  Versión 2.2
 *
 */
