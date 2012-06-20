/*
 * @(#)CFieldEditor.java   12.oct 2007  Versión 2.2
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

import java.awt.Component;

import javax.swing.ComboBoxEditor;
import javax.swing.JTextField;

/**
 *  OpenXpertya Field Editor.
 *
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CFieldEditor.java,v 1.5 2005/03/11 20:34:38 jjanke Exp $
 */
public class CFieldEditor extends JTextField implements ComboBoxEditor {

    /**
     *
     */
    public CFieldEditor() {}

    //~--- get methods --------------------------------------------------------

    /**
     *  Return the component that should be added to the tree hierarchy
     *  for this editor
     *
     * @return
     */
    public Component getEditorComponent() {
        return this;
    }		// getEditorCimponent

    /**
     *  Returns format Info (for Popup)
     *  @return format
     */
    public Object getFormat() {
        return null;
    }		// getFormat

    /**
     *  Get edited item
     *  @return edited text
     */
    public Object getItem() {
        return getText();
    }		// getItem

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Editor
     *  @param anObject
     */
    public void setItem(Object anObject) {

        if (anObject == null) {
            setText("");
        } else {
            setText(anObject.toString());
        }

    }		// setItem
}	// CFieldEditor



/*
 * @(#)CFieldEditor.java   02.jul 2007
 * 
 *  Fin del fichero CFieldEditor.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
