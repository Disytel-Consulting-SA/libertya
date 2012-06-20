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



package org.openXpertya.report;

import java.util.Enumeration;
import java.util.Properties;

import org.openXpertya.model.MAcctSchemaElement;
import org.openXpertya.model.MTree;
import org.openXpertya.model.MTreeNode;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MReportTree {

    /** Descripción de Campos */

    private static CCache s_trees = new CCache( "MReportTree",20 );

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param ElementType
     *
     * @return
     */

    public static MReportTree get( Properties ctx,String ElementType ) {
        MReportTree tree = ( MReportTree )s_trees.get( ElementType );

        if( tree == null ) {
            tree = new MReportTree( ctx,ElementType );
            s_trees.put( ElementType,tree );
        }

        return tree;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param ElementType
     * @param ID
     *
     * @return
     */

    public static String getWhereClause( Properties ctx,String ElementType,int ID ) {
        MReportTree tree = get( ctx,ElementType );

        return tree.getWhereClause( ID );
    }    // get

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param ElementType
     */

    public MReportTree( Properties ctx,String ElementType ) {
        m_ElementType = ElementType;
        m_TreeType    = m_ElementType;

        if( MAcctSchemaElement.ELEMENTTYPE_Account.equals( m_ElementType ) || MAcctSchemaElement.ELEMENTTYPE_User1.equals( m_ElementType ) || MAcctSchemaElement.ELEMENTTYPE_User2.equals( m_ElementType )) {
            m_TreeType = MTree.TREETYPE_ElementValue;
        }

        //

        m_tree = MTree.getTree( ctx,m_TreeType,true );    // client
    }                                                     // MReportTree

    /** Descripción de Campos */

    private String m_ElementType = null;

    /** Descripción de Campos */

    private String m_TreeType = null;

    /** Descripción de Campos */

    private MTree m_tree = null;

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getElementType() {
        return m_ElementType;
    }    // getElementType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTreeType() {
        return m_TreeType;
    }    // getTreeType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MTree getTree() {
        return m_tree;
    }    // getTreeType

    /**
     * Descripción de Método
     *
     *
     * @param ID
     *
     * @return
     */

    public String getWhereClause( int ID ) {
        log.fine( "getWhereClause - (" + m_ElementType + ") ID=" + ID );

        String ColumnName = MAcctSchemaElement.getColumnName( m_ElementType );

        //

        MTreeNode node = m_tree.getRoot().findNode( ID );

        log.finest( "Root=" + node );

        //

        StringBuffer result = null;

        if( (node != null) && node.isSummary()) {
            StringBuffer sb = new StringBuffer();
            Enumeration  en = node.preorderEnumeration();

            while( en.hasMoreElements()) {
                MTreeNode nn = ( MTreeNode )en.nextElement();

                if( !nn.isSummary()) {
                    if( sb.length() > 0 ) {
                        sb.append( "," );
                    }

                    sb.append( nn.getNode_ID());
                    log.finest( "- " + nn );
                } else {
                    log.finest( "- skipped parent (" + nn + ")" );
                }
            }

            result = new StringBuffer( ColumnName ).append( " IN (" ).append( sb ).append( ")" );
        } else {    // not found or not summary
            result = new StringBuffer( ColumnName ).append( "=" ).append( ID );
        }

        //

        log.finest( "getWhereClause - " + result );

        return result.toString();
    }    // getWhereClause

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MReportTree[ElementType=" );

        sb.append( m_ElementType ).append( ",TreeType=" ).append( m_TreeType ).append( "," ).append( m_tree ).append( "]" );

        return sb.toString();
    }    // toString
}    // MReportTree



/*
 *  @(#)MReportTree.java   02.07.07
 * 
 *  Fin del fichero MReportTree.java
 *  
 *  Versión 2.2
 *
 */
