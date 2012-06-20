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



package org.openXpertya.grid.tree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openXpertya.model.MTreeNode;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class VTreeCellRenderer extends DefaultTreeCellRenderer {

    /**
     * Constructor de la clase ...
     *
     */

    public VTreeCellRenderer() {
        super();
    }    // VTreeCellRenderer

    /**
     * Descripción de Método
     *
     *
     * @param tree
     * @param value
     * @param selected
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     *
     * @return
     */

    public Component getTreeCellRendererComponent( JTree tree,Object value,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus ) {
        VTreeCellRenderer c = ( VTreeCellRenderer )super.getTreeCellRendererComponent( tree,value,selected,expanded,leaf,row,hasFocus );

        if( !leaf ) {
            return c;
        }

        // We have a leaf

        MTreeNode nd   = ( MTreeNode )value;
        Icon      icon = nd.getIcon();

        if( icon != null ) {
            c.setIcon( icon );
        }

        c.setText( nd.getName());
        c.setToolTipText( nd.getDescription());

        if( !selected ) {
            c.setForeground( nd.getColor());
        }

        return c;
    }    // getTreeCellRendererComponent
}    // VTreeCellRenderer



/*
 *  @(#)VTreeCellRenderer.java   02.07.07
 * 
 *  Fin del fichero VTreeCellRenderer.java
 *  
 *  Versión 2.2
 *
 */
