/*
 * @(#)CEditor.java   12.oct 2007  Versión 2.2
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



package org.compiere.swing;

/**
 *  OpenXpertya Editor interface
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CEditor.java,v 1.3 2005/03/11 20:34:38 jjanke Exp $
 */
public interface CEditor {

    /**
     *  Return Display Value
     *  @return displayed String value
     */
    public String getDisplay();

    /**
     *      Return Editor value
     *  @return current value
     */
    public Object getValue();

    /**
     *      Is Field mandatory
     *  @return true, if mandatory
     */
    public boolean isMandatory();

    /**
     *      Is it possible to edit
     *  @return true, if editable
     */
    public boolean isReadWrite();

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Background based on editable / mandatory / error
     *  @param error if true, set background to error color, otherwise mandatory/editable
     */
    public void setBackground(boolean error);

    /**
     *      Set Editor Mandatory
     *  @param mandatory true, if you have to enter data
     */
    public void setMandatory(boolean mandatory);

    /**
     *      Enable Editor
     *  @param rw true, if you can enter/select data
     */
    public void setReadWrite(boolean rw);

    /**
     *      Set Editor to value
     *  @param value value of the editor
     */
    public void setValue(Object value);

    /**
     *  Set Visible
     *  @param visible true if field is to be shown
     */
    public void setVisible(boolean visible);
}	// CEditor



/*
 * @(#)CEditor.java   02.jul 2007
 * 
 *  Fin del fichero CEditor.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
