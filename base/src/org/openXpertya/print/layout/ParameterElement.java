/*
 *    El contenido de este fichero est� sujeto a la  Licencia P�blica openXpertya versi�n 1.1 (LPO)
 * en tanto en cuanto forme parte �ntegra del total del producto denominado:  openXpertya, soluci�n 
 * empresarial global , y siempre seg�n los t�rminos de dicha licencia LPO.
 *    Una copia  �ntegra de dicha  licencia est� incluida con todas  las fuentes del producto.
 *    Partes del c�digo son CopyRight (c) 2002-2007 de Ingenier�a Inform�tica Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultor�a y  Soporte en  Redes y  Tecnolog�as  de  la
 * Informaci�n S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de c�digo original de  terceros, recogidos en el  ADDENDUM  A, secci�n 3 (A.3) de dicha
 * licencia  LPO,  y si dicho c�digo es extraido como parte del total del producto, estar� sujeto a
 * su respectiva licencia original.  
 *     M�s informaci�n en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.print.layout;

import java.util.Properties;

import org.openXpertya.model.MQuery;
import org.openXpertya.print.MPrintTableFormat;
import org.openXpertya.util.Msg;

/**
 * Descripci�n de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ParameterElement extends GridElement {

    /**
     * Constructor de la clase ...
     *
     *
     * @param query
     * @param ctx
     * @param tFormat
     */

    public ParameterElement( MQuery query,Properties ctx,MPrintTableFormat tFormat ) {
        super( query.getRestrictionCount(),4 );
        setData( 0,0,Msg.getMsg( ctx,"Parameter" ) + ":",tFormat.getPageHeader_Font(),tFormat.getPageHeaderFG_Color());

        for( int r = 0;r < query.getRestrictionCount();r++ ) {
            setData( r,1,query.getInfoName( r ),tFormat.getParameter_Font(),tFormat.getParameter_Color());
            setData( r,2,query.getInfoOperator( r ),tFormat.getParameter_Font(),tFormat.getParameter_Color());
            setData( r,3,query.getInfoDisplayAll( r ),tFormat.getParameter_Font(),tFormat.getParameter_Color());
        }
    }    // ParameterElement
}    // ParameterElement



/*
 *  @(#)ParameterElement.java   12.10.07
 * 
 *  Fin del fichero ParameterElement.java
 *  
 *  Versión 2.2
 *
 */
