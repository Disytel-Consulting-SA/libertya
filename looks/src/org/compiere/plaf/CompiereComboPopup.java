/*
 * @(#)CompiereComboPopup.java   12.oct 2007  Versión 2.2
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



package org.compiere.plaf;

import org.compiere.swing.CComboBox;
import org.compiere.swing.CField;

//~--- Importaciones JDK ------------------------------------------------------

import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboPopup;

/**
 *  OpenXpertya Combo Popup - allows to prevent the display of the popup
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *     Jorg Janke
 *  @version    $Id: CompiereComboPopup.java,v 1.7 2005/03/11 20:34:37 jjanke Exp $
 */
public class CompiereComboPopup extends BasicComboPopup {

    /**
     *  Constructor
     *  @param combo
     */
    public CompiereComboPopup(JComboBox combo) {
        super(combo);
    }		// CompiereComboPopup

    /**
     *  Conditionally show the Popup.
     *  If the combo is a CComboBox/CField, the return value of the
     *  method displayPopup determines if the popup is actually displayed
     *  @see CComboBox#displayPopup()
     *  @see CField#displayPopup()
     */
    public void show() {

        // Check ComboBox if popup should be displayed
        if ((comboBox instanceof CComboBox) &&!((CComboBox) comboBox).displayPopup()) {
            return;
        }

        // Check Field if popup should be displayed
        if ((comboBox instanceof CField) &&!((CField) comboBox).displayPopup()) {
            return;
        }

        super.show();
    }		// show

    /**
     *  Inform CComboBox and CField that Popup was hidden
     *  @see CComboBox.hidingPopup
     *  @see CField.hidingPopup
     *
     * public void hide()
     * {
     *       super.hide();
     *       //  Inform ComboBox that popup was hidden
     *       if (comboBox instanceof CComboBox)
     *               (CComboBox)comboBox).hidingPopup();
     *       else if (comboBox instanceof CComboBox)
     *               (CComboBox)comboBox).hidingPopup();
     * }   //  hided
     * /*
     */
}	// CompiereComboPopup



/*
 * @(#)CompiereComboPopup.java   02.jul 2007
 * 
 *  Fin del fichero CompiereComboPopup.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
