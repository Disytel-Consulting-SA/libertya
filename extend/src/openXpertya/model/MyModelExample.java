/*
 * @(#)MyModelExample.java   11.jun 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */


package openXpertya.model;

import java.sql.*;

import java.util.*;

import org.openXpertya.model.*;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 11.12.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MyModelExample extends PO {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ID
     * @param trxName
     */

    public MyModelExample( Properties ctx,int ID,String trxName ) {
        super( ctx,ID,trxName );
    }    // MyModelExample

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MyModelExample( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MyModelExample

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    protected POInfo initPO( Properties ctx ) {
        return null;
    }    // initPO
}    // MyModelExample



/*
 *  @(#)MyModelExample.java   11.12.06
 * 
 *  Fin del fichero MyModelExample.java
 *  
 *  Versión 2.2
 *
 */
