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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import org.openXpertya.model.MTree;
import org.openXpertya.model.MTree_Base;
import org.openXpertya.model.MTree_Node;
import org.openXpertya.model.MTree_NodeBP;
import org.openXpertya.model.MTree_NodePR;
import org.openXpertya.model.PO;
import org.openXpertya.util.DB;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class TreeMaintenance extends SvrProcess {

    /** Descripción de Campos */

    private int m_AD_Tree_ID;

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
            } else {
                log.log( Level.SEVERE,"prepare - Unknown Parameter: " + name );
            }
        }

        m_AD_Tree_ID = getRecord_ID();    // from Window
    }                                     // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        log.info( "doIt - AD_Tree_ID=" + m_AD_Tree_ID );

        if( m_AD_Tree_ID == 0 ) {
            throw new IllegalArgumentException( "Tree_ID = 0" );
        }

        MTree tree = new MTree( getCtx(),m_AD_Tree_ID,get_TrxName());

        if( (tree == null) || (tree.getAD_Tree_ID() == 0) ) {
            throw new IllegalArgumentException( "No Tree -" + tree );
        }

        //

        if( MTree.TREETYPE_BoM.equals( tree.getTreeType())) {
            return "BOM Trees not implemented";
        }

        return verifyTree( tree );
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param tree
     *
     * @return
     */

    private String verifyTree( MTree_Base tree ) {
        String nodeTableName   = tree.getNodeTableName();
        String sourceTableName = tree.getSourceTableName( true );
        String sourceTableKey  = sourceTableName + "_ID";
        int    AD_Client_ID    = tree.getAD_Client_ID();
        int    C_Element_ID    = 0;

        if( MTree.TREETYPE_ElementValue.equals( tree.getTreeType())) {
            String sql = "SELECT C_Element_ID FROM C_Element " + "WHERE " + tree.getAD_Tree_ID() + " IN (AD_Tree_ID, ADD1Tree_ID, ADD2Tree_ID)";

            C_Element_ID = DB.getSQLValue( null,sql );

            if( C_Element_ID <= 0 ) {
                throw new IllegalStateException( "No Account Element found" );
            }
        }

        // Delete unused

        StringBuffer sql = new StringBuffer();

        sql.append( "DELETE " ).append( nodeTableName ).append( " WHERE AD_Tree_ID=" ).append( tree.getAD_Tree_ID()).append( " AND Node_ID NOT IN (SELECT " ).append( sourceTableKey ).append( " FROM " ).append( sourceTableName ).append( " WHERE AD_Client_ID=" ).append( AD_Client_ID );

        if( C_Element_ID > 0 ) {
            sql.append( " AND C_Element_ID=" ).append( C_Element_ID );
        }

        sql.append( ")" );
        log.finer( sql.toString());

        //

        int deletes = DB.executeUpdate( sql.toString(),get_TrxName());

        addLog( 0,null,new BigDecimal( deletes ),tree.getName() + " Deleted" );

        if( !tree.isAllNodes()) {
            return tree.getName() + " OK";
        }

        // Insert new

        int inserts = 0;

        sql = new StringBuffer();
        sql.append( "SELECT " ).append( sourceTableKey ).append( " FROM " ).append( sourceTableName ).append( " WHERE AD_Client_ID=" ).append( AD_Client_ID );

        if( C_Element_ID > 0 ) {
            sql.append( " AND C_Element_ID=" ).append( C_Element_ID );
        }

        sql.append( " AND " ).append( sourceTableKey ).append( "  NOT IN (SELECT Node_ID FROM " ).append( nodeTableName ).append( " WHERE AD_Tree_ID=" ).append( tree.getAD_Tree_ID()).append( ")" );
        log.finer( sql.toString());

        //

        boolean           ok    = true;
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int Node_ID = rs.getInt( 1 );
                PO  node    = null;

                if( nodeTableName.equals( "AD_TreeNode" )) {
                    node = new MTree_Node( tree,Node_ID );
                } else if( nodeTableName.equals( "AD_TreeNodeBP" )) {
                    node = new MTree_NodeBP( tree,Node_ID );
                } else if( nodeTableName.equals( "AD_TreeNodePR" )) {
                    node = new MTree_NodePR( tree,Node_ID );
                }

//                              else if (nodeTableName.equals("AD_TreeNodeMM"))
//                                      node = new MTree_NodeMM(tree, Node_ID);
                //

                if( node == null ) {
                    log.log( Level.SEVERE,"verifyTree - no Model for " + nodeTableName );
                } else {
                    if( node.save()) {
                        inserts++;
                    } else {
                        log.log( Level.SEVERE,"verifyTree - Could not add to " + tree + " Node_ID=" + Node_ID );
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"verifyTree",e );
            ok = false;
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        addLog( 0,null,new BigDecimal( inserts ),tree.getName() + " Inserted" );

        return tree.getName() + ( ok
                                  ?" OK"
                                  :" Error" );
    }    // verifyTree
}    // TreeMaintenence



/*
 *  @(#)TreeMaintenance.java   02.07.07
 * 
 *  Fin del fichero TreeMaintenance.java
 *  
 *  Versión 2.2
 *
 */
