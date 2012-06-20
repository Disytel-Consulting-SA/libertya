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



package org.openXpertya.grid.ed;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;

import javax.swing.border.Border;

import org.compiere.swing.CEditor;
import org.openXpertya.model.MField;

/**
 * Descripción de Interface
 *
 *
 * @version    2.2, 12.10.07
 * @author         Equipo de Desarrollo de openXpertya    
 */

public interface VEditor extends CEditor,PropertyChangeListener {

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName();

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addVetoableChangeListener( VetoableChangeListener listener );

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void removeVetoableChangeListener( VetoableChangeListener listener );

    /**
     * Descripción de Método
     *
     *
     * @param listener
     */

    public void addActionListener( ActionListener listener );

//      public void removeActionListener(ActionListener listener);

    /**
     * Descripción de Método
     *
     *
     * @param border
     */

    public void setBorder( Border border );

    /**
     * Descripción de Método
     *
     *
     * @param font
     */

    public void setFont( Font font );

    /**
     * Descripción de Método
     *
     *
     * @param color
     */

    public void setForeground( Color color );

    /**
     * Descripción de Método
     *
     *
     * @param mField
     */

    public void setField( MField mField );

    /**
     * Descripción de Método
     *
     */

    public void dispose();
}    // VEditor



/*
 *  @(#)VEditor.java   02.07.07
 * 
 *  Fin del fichero VEditor.java
 *  
 *  Versión 2.2
 *
 */
