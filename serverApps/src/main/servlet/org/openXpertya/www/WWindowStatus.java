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



package org.openXpertya.www;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MTab;
import org.openXpertya.model.MWindow;
import org.openXpertya.model.MWindowVO;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WWindowStatus {

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    public static WWindowStatus get( HttpServletRequest request ) {
        HttpSession sess = request.getSession( false );

        if( sess == null ) {
            return null;
        }

        return( WWindowStatus )sess.getAttribute( NAME );
    }    // get

    /**
     * Constructor de la clase ...
     *
     *
     * @param mWindowVO
     */

    public WWindowStatus( MWindowVO mWindowVO ) {
        mWindow = new MWindow( mWindowVO );
        curTab  = mWindow.getTab( 0 );
        curTab.setSingleRow( true );

        //

        ctx = mWindowVO.ctx;
    }    // WWindowStatus

    /** Descripción de Campos */

    public static final String NAME = "WWindowStatus";

    /** Descripción de Campos */

    protected MWindow mWindow;

    /** Descripción de Campos */

    protected MTab curTab;

    /** Descripción de Campos */

    public Properties ctx = null;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return "WWindowStatus[" + mWindow + " - " + curTab + "]";
    }    // toString
}    // WWindowStatus



/*
 *  @(#)WWindowStatus.java   12.10.07
 * 
 *  Fin del fichero WWindowStatus.java
 *  
 *  Versión 2.2
 *
 */
