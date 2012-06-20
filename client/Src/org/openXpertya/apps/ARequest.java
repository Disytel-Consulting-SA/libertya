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



package org.openXpertya.apps;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.openXpertya.model.MAsset;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MCampaign;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MPayment;
import org.openXpertya.model.MProduct;
import org.openXpertya.model.MProject;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MTab;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ARequest implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param invoker
     * @param AD_Table_ID
     * @param Record_ID
     */

    public ARequest( JComponent invoker,int AD_Table_ID,int Record_ID ) {
        super();
        log.config( "AD_Table_ID=" + AD_Table_ID + ", Record_ID=" + Record_ID );
        m_AD_Table_ID = AD_Table_ID;
        m_Record_ID   = Record_ID;
        getRequests( invoker );
    }    // ARequest

    /** Descripción de Campos */

    private int m_AD_Table_ID;

    /** Descripción de Campos */

    private int m_Record_ID;

    /** Descripción de Campos */

    private JPopupMenu m_popup = new JPopupMenu( "RequestMenu" );

    /** Descripción de Campos */

    private JMenuItem m_new = null;

    /** Descripción de Campos */

    private JMenuItem m_active = null;

    /** Descripción de Campos */

    private JMenuItem m_all = null;

    /** Descripción de Campos */

    StringBuffer m_where = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ARequest.class );

    /**
     * Descripción de Método
     *
     *
     * @param invoker
     */

    private void getRequests( JComponent invoker ) {
        m_new = new JMenuItem( Msg.getMsg( Env.getCtx(),"RequestNew" ));
        m_new.setIcon( Env.getImageIcon( "New16.gif" ));
        m_popup.add( m_new ).addActionListener( this );

        //

        int activeCount   = 0;
        int inactiveCount = 0;

        m_where = new StringBuffer();
        m_where.append( "(AD_Table_ID=" ).append( m_AD_Table_ID ).append( " AND Record_ID=" ).append( m_Record_ID ).append( ")" );

        if( m_AD_Table_ID == MUser.Table_ID ) {
            m_where.append( " OR AD_User_ID=" ).append( m_Record_ID ).append( " OR SalesRep_ID=" ).append( m_Record_ID );
        } else if( m_AD_Table_ID == MBPartner.Table_ID ) {
            m_where.append( " OR C_BPartner_ID=" ).append( m_Record_ID );
        } else if( m_AD_Table_ID == MOrder.Table_ID ) {
            m_where.append( " OR C_Order_ID=" ).append( m_Record_ID );
        } else if( m_AD_Table_ID == MInvoice.Table_ID ) {
            m_where.append( " OR C_Invoice_ID=" ).append( m_Record_ID );
        } else if( m_AD_Table_ID == MPayment.Table_ID ) {
            m_where.append( " OR C_Payment_ID=" ).append( m_Record_ID );
        } else if( m_AD_Table_ID == MProduct.Table_ID ) {
            m_where.append( " OR M_Product_ID=" ).append( m_Record_ID );
        } else if( m_AD_Table_ID == MProject.Table_ID ) {
            m_where.append( " OR C_Project_ID=" ).append( m_Record_ID );
        } else if( m_AD_Table_ID == MCampaign.Table_ID ) {
            m_where.append( " OR C_Campaign_ID=" ).append( m_Record_ID );
        } else if( m_AD_Table_ID == MAsset.Table_ID ) {
            m_where.append( " OR A_Asset_ID=" ).append( m_Record_ID );
        }

        //

        String sql = "SELECT Processed, COUNT(*) " + "FROM R_Request WHERE " + m_where + " GROUP BY Processed " + "ORDER BY Processed DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                if( "Y".equals( rs.getString( 1 ))) {
                    inactiveCount = rs.getInt( 2 );
                } else {
                    activeCount += rs.getInt( 2 );
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        if( activeCount > 0 ) {
            m_active = new JMenuItem( Msg.getMsg( Env.getCtx(),"RequestActive" ) + " (" + activeCount + ")" );
            m_popup.add( m_active ).addActionListener( this );
        }

        if( inactiveCount > 0 ) {
            m_all = new JMenuItem( Msg.getMsg( Env.getCtx(),"RequestAll" ) + " (" + ( activeCount + inactiveCount ) + ")" );
            m_popup.add( m_all ).addActionListener( this );
        }

        //

        if( invoker.isShowing()) {
            m_popup.show( invoker,0,invoker.getHeight());    // below button
        }
    }                                                        // getZoomTargets

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        MQuery query = null;

        if( e.getSource() == m_active ) {
            query = new MQuery( "" );

            String where = "(" + m_where + ") AND Processed='N'";

            query.addRestriction( where );
        } else if( e.getSource() == m_all ) {
            query = new MQuery( "" );
            query.addRestriction( m_where.toString());
        }

        //

        int     AD_Window_ID = 232;    // 232=all - 201=my
        AWindow frame        = new AWindow();

        if( !frame.initWindow( AD_Window_ID,query )) {
            return;
        }

        // New - set Table/Record

        if( e.getSource() == m_new ) {
            MTab tab = frame.getAPanel().getCurrentTab();

            tab.dataNew( false );
            tab.setValue( "AD_Table_ID",new Integer( m_AD_Table_ID ));
            tab.setValue( "Record_ID",new Integer( m_Record_ID ));

            if( m_AD_Table_ID == MBPartner.Table_ID ) {
                tab.setValue( "C_BPartner_ID",new Integer( m_Record_ID ));
            } else if( m_AD_Table_ID == MOrder.Table_ID ) {
                tab.setValue( "C_Order_ID",new Integer( m_Record_ID ));
            } else if( m_AD_Table_ID == MInvoice.Table_ID ) {
                tab.setValue( "C_Invoice_ID",new Integer( m_Record_ID ));
            } else if( m_AD_Table_ID == MPayment.Table_ID ) {
                tab.setValue( "C_Payment_ID",new Integer( m_Record_ID ));
            } else if( m_AD_Table_ID == MProduct.Table_ID ) {
                tab.setValue( "M_Product_ID",new Integer( m_Record_ID ));
            } else if( m_AD_Table_ID == MProject.Table_ID ) {
                tab.setValue( "C_Project_ID",new Integer( m_Record_ID ));
            } else if( m_AD_Table_ID == MCampaign.Table_ID ) {
                tab.setValue( "C_Campaign_ID",new Integer( m_Record_ID ));
            } else if( m_AD_Table_ID == MAsset.Table_ID ) {
                tab.setValue( "A_Asset_ID",new Integer( m_Record_ID ));
            }
        }

        AEnv.showCenterScreen( frame );
        frame = null;
    }    // actionPerformed
}    // ARequest



/*
 *  @(#)ARequest.java   02.07.07
 * 
 *  Fin del fichero ARequest.java
 *  
 *  Versión 2.2
 *
 */
