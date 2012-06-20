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



package org.openXpertya.apps.form;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.logging.Level;

import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MInvoice;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MPayment;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class VMerge extends CPanel implements FormPanel,ActionListener {

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame;

    /** Descripción de Campos */

    private int m_totalCount = 0;

    /** Descripción de Campos */

    private StringBuffer m_errorLog = new StringBuffer();

    /** Descripción de Campos */

    private Connection m_con = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( VMerge.class );

    /** Descripción de Campos */

    static private String AD_ORG_ID = "AD_Org_ID";

    /** Descripción de Campos */

    static private String C_BPARTNER_ID = "C_BPartner_ID";

    /** Descripción de Campos */

    static private String AD_USER_ID = "AD_User_ID";

    /** Descripción de Campos */

    static private String M_PRODUCT_ID = "M_Product_ID";

    /** Descripción de Campos */

    static private String[] s_delete_Org = new String[]{ "AD_OrgInfo" };

    /** Descripción de Campos */

    static private String[] s_delete_User = new String[]{ "AD_User_Roles" };

    /** Descripción de Campos */

    static private String[] s_delete_BPartner = new String[]{ "C_BP_Employee_Acct","C_BP_Vendor_Acct","C_BP_Customer_Acct","T_Aging" };

    /** Descripción de Campos */

    static private String[] s_delete_Product = new String[] {
        "M_Product_PO","M_Replenish","T_Replenish","M_ProductPrice","M_Product_Costing","M_Product_Trl","M_Product_Acct"
    };    // M_Storage

    /** Descripción de Campos */

    private String[] m_columnName = null;

    /** Descripción de Campos */

    private CLabel[] m_label = null;

    /** Descripción de Campos */

    private VLookup[] m_from = null;

    /** Descripción de Campos */

    private VLookup[] m_to = null;

    /** Descripción de Campos */

    private String[] m_deleteTables = null;

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel CenterPanel = new CPanel();

    /** Descripción de Campos */

    private GridLayout centerLayout = new GridLayout();

    /** Descripción de Campos */

    private CLabel mergeFromLabel = new CLabel();

    /** Descripción de Campos */

    private CLabel mergeToLabel = new CLabel();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param frame
     */

    public void init( int WindowNo,FormFrame frame ) {
        m_WindowNo = WindowNo;
        m_frame    = frame;
        log.info( "VMerge.init - WinNo=" + m_WindowNo );

        try {
            preInit();
            jbInit();
            frame.getContentPane().add( this,BorderLayout.CENTER );

            // frame.getContentPane().add(statusBar, BorderLayout.SOUTH);

        } catch( Exception ex ) {
            log.log( Level.SEVERE,"",ex );
        }
    }    // init

    /**
     * Descripción de Método
     *
     */

    private void preInit() {
        int count = 4;    // ** Update **

        m_columnName = new String[ count ];
        m_label      = new CLabel[ count ];
        m_from       = new VLookup[ count ];
        m_to         = new VLookup[ count ];

        // ** Update **

        preInit( 0,2163,DisplayType.TableDir,AD_ORG_ID );      // C_Order.AD_Org_ID
        preInit( 1,2762,DisplayType.Search,C_BPARTNER_ID );    // C_Order.C_BPartner_ID
        preInit( 2,971,DisplayType.Search,AD_USER_ID );    // AD_User_Roles.AD_User_ID
        preInit( 3,2221,DisplayType.Search,M_PRODUCT_ID );    // C_OrderLine.M_Product_ID
    }    // preInit

    /**
     * Descripción de Método
     *
     *
     * @param index
     * @param AD_Column_ID
     * @param displayType
     * @param ColumnName
     */

    private void preInit( int index,int AD_Column_ID,int displayType,String ColumnName ) {
        m_columnName[ index ] = ColumnName;

        String what = Msg.translate( Env.getCtx(),ColumnName );

        m_label[ index ] = new CLabel( what );
        m_from[ index ]  = new VLookup( ColumnName,false,false,true,MLookupFactory.get( Env.getCtx(),m_WindowNo,0,AD_Column_ID,displayType ));
        m_to[ index ] = new VLookup( ColumnName,false,false,true,MLookupFactory.get( Env.getCtx(),m_WindowNo,0,AD_Column_ID,displayType ));
    }    // preInit

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        this.setLayout( mainLayout );
        mainLayout.setHgap( 5 );
        mainLayout.setVgap( 5 );

        //

        this.add( confirmPanel,BorderLayout.SOUTH );
        confirmPanel.addActionListener( this );

        //

        centerLayout.setHgap( 5 );
        centerLayout.setVgap( 5 );
        centerLayout.setColumns( 3 );
        centerLayout.setRows( m_label.length + 1 );

        //

        CenterPanel.setLayout( centerLayout );
        this.add( CenterPanel,BorderLayout.CENTER );
        CenterPanel.add( new CLabel(),null );
        CenterPanel.add( mergeFromLabel,null );
        CenterPanel.add( mergeToLabel,null );

        //

        Font heading = mergeFromLabel.getFont();

        heading = new Font( heading.getName(),Font.BOLD,heading.getSize());
        mergeFromLabel.setFont( heading );
        mergeFromLabel.setRequestFocusEnabled( false );
        mergeFromLabel.setText( Msg.getMsg( Env.getCtx(),"MergeFrom" ));
        mergeToLabel.setFont( heading );
        mergeToLabel.setText( Msg.getMsg( Env.getCtx(),"MergeTo" ));

        //

        for( int i = 0;i < m_label.length;i++ ) {
            CenterPanel.add( m_label[ i ],null );
            CenterPanel.add( m_from[ i ],null );
            CenterPanel.add( m_to[ i ],null );
        }
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        if( m_frame != null ) {
            m_frame.dispose();
        }

        m_frame = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getActionCommand().equals( ConfirmPanel.A_CANCEL )) {
            dispose();

            return;
        }

        //

        String columnName = null;
        String from_Info  = null;
        String to_Info    = null;
        int    from_ID    = 0;
        int    to_ID      = 0;

        // get first merge pair

        for( int i = 0;( (i < m_columnName.length) && (from_ID == 0) && (to_ID == 0) );i++ ) {
            Object value = m_from[ i ].getValue();

            if( value != null ) {
                if( value instanceof Integer ) {
                    from_ID = (( Integer )value ).intValue();
                } else {
                    continue;
                }

                value = m_to[ i ].getValue();

                if( (value != null) && (value instanceof Integer) ) {
                    to_ID = (( Integer )value ).intValue();
                } else {
                    from_ID = 0;
                }

                if( from_ID != 0 ) {
                    columnName = m_columnName[ i ];
                    from_Info  = m_from[ i ].getDisplay();
                    to_Info    = m_to[ i ].getDisplay();
                }
            }
        }    // get first merge pair

        if( (from_ID == 0) || (from_ID == to_ID) ) {
            return;
        }

        String msg = Msg.getMsg( Env.getCtx(),"MergeFrom" ) + " = " + from_Info + "\n" + Msg.getMsg( Env.getCtx(),"MergeTo" ) + " = " + to_Info;

        if( !ADialog.ask( m_WindowNo,this,"MergeQuestion",msg )) {
            return;
        }

        // ** Update **

        if( columnName.equals( AD_ORG_ID )) {
            m_deleteTables = s_delete_Org;
        } else if( columnName.equals( AD_USER_ID )) {
            m_deleteTables = s_delete_User;
        } else if( columnName.equals( C_BPARTNER_ID )) {
            m_deleteTables = s_delete_BPartner;
        } else if( columnName.equals( M_PRODUCT_ID )) {
            m_deleteTables = s_delete_Product;
        }

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        confirmPanel.getOKButton().setEnabled( false );

        //

        boolean success = merge( columnName,from_ID,to_ID );

        postMerge( columnName,to_ID );

        //

        confirmPanel.getOKButton().setEnabled( true );
        setCursor( Cursor.getDefaultCursor());

        //

        if( success ) {
            ADialog.info( m_WindowNo,this,"MergeSuccess",msg + " #" + m_totalCount );
        } else {
            ADialog.error( m_WindowNo,this,"MergeError",m_errorLog.toString());

            return;
        }

        dispose();
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param ColumnName
     * @param from_ID
     * @param to_ID
     *
     * @return
     */

    private boolean merge( String ColumnName,int from_ID,int to_ID ) {
        String TableName = ColumnName.substring( 0,ColumnName.length() - 3 );

        log.config( ColumnName + " - From=" + from_ID + ",To=" + to_ID );

        boolean success = true;

        m_totalCount = 0;
        m_errorLog   = new StringBuffer();

        String sql = "SELECT t.TableName, c.ColumnName " + "FROM AD_Table t" + " INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID) " + "WHERE t.IsView='N' AND (" + "(c.ColumnName=? AND c.IsKey='N')"    // #1 - direct
                     + " OR " + "c.AD_Reference_Value_ID IN "                                                                                                                                                          // Table Reference
                     + "(SELECT rt.AD_Reference_ID FROM AD_Ref_Table rt" + " INNER JOIN AD_Column cc ON (rt.AD_Table_ID=cc.AD_Table_ID AND rt.AD_Key=cc.AD_Column_ID) " + "WHERE cc.IsKey='Y' AND cc.ColumnName=?)"    // #2
                     + ") " + "ORDER BY t.LoadSeq DESC";
        PreparedStatement pstmt = null;
        Savepoint         sp    = null;

        try {
            m_con = DB.createConnection( false,Connection.TRANSACTION_READ_COMMITTED );
            sp = m_con.setSavepoint( "merge" );

            //

            pstmt = DB.prepareStatement( sql );
            pstmt.setString( 1,ColumnName );
            pstmt.setString( 2,ColumnName );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                String tName = rs.getString( 1 );
                String cName = rs.getString( 2 );

                if( !TableName.equals( tName ))    // to be sure - sql should prevent it
                {
                    int count = mergeTable( tName,cName,from_ID,to_ID );

                    if( count < 0 ) {
                        success = false;
                    } else {
                        m_totalCount += count;
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;

            //

            log.config( "Success=" + success + " - " + ColumnName + " - From=" + from_ID + ",To=" + to_ID );

            if( success ) {
                sql = "DELETE " + TableName + " WHERE " + ColumnName + "=" + from_ID;

                Statement stmt  = m_con.createStatement();
                int       count = 0;

                try {
                    count = stmt.executeUpdate( sql );

                    if( count != 1 ) {
                        m_errorLog.append( Env.NL ).append( "DELETE " ).append( TableName ).append( " - Count=" ).append( count );
                        success = false;
                    }
                } catch( SQLException ex1 ) {
                    m_errorLog.append( Env.NL ).append( "DELETE " ).append( TableName ).append( " - " ).append( ex1.toString());
                    success = false;
                }

                stmt.close();
                stmt = null;
            }

            //

            if( success ) {
                m_con.commit();
            } else {
                m_con.rollback( sp );
            }

            m_con.close();
            m_con = null;
        } catch( Exception ex ) {
            log.log( Level.SEVERE,ColumnName,ex );
        }

        // Cleanup

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            if( m_con != null ) {
                m_con.close();
            }
        } catch( Exception ex ) {
        }

        pstmt = null;
        m_con = null;

        //

        return success;
    }    // merge

    /**
     * Descripción de Método
     *
     *
     * @param TableName
     * @param ColumnName
     * @param from_ID
     * @param to_ID
     *
     * @return
     */

    private int mergeTable( String TableName,String ColumnName,int from_ID,int to_ID ) {

        // log.config( "VMerge.mergeTable", TableName + "." + ColumnName
        // + " - From=" + from_ID + ",To=" + to_ID);

        String sql = "UPDATE " + TableName + " SET " + ColumnName + "=" + to_ID + " WHERE " + ColumnName + "=" + from_ID;
        boolean delete = false;

        for( int i = 0;i < m_deleteTables.length;i++ ) {
            if( m_deleteTables[ i ].equals( TableName )) {
                delete = true;
                sql    = "DELETE " + TableName + " WHERE " + ColumnName + "=" + from_ID;
            }
        }

        int count = -1;

        try {
            Statement stmt = m_con.createStatement();

            try {
                count = stmt.executeUpdate( sql );
                log.fine( count + ( delete
                                    ?" -Delete- "
                                    :" -Update- " ) + TableName );
            } catch( SQLException ex1 ) {
                count = -1;
                m_errorLog.append( Env.NL ).append( delete
                        ?"DELETE "
                        :"UPDATE " ).append( TableName ).append( " - " ).append( ex1.toString());
            }

            stmt.close();
            stmt = null;
        } catch( SQLException ex ) {
            count = -1;
            m_errorLog.append( Env.NL ).append( delete
                    ?"DELETE "
                    :"UPDATE " ).append( TableName ).append( " - " ).append( ex.toString());
        }

        return count;
    }    // mergeTable

    /**
     * Descripción de Método
     *
     *
     * @param ColumnName
     * @param to_ID
     */

    private void postMerge( String ColumnName,int to_ID ) {
        if( ColumnName.equals( AD_ORG_ID )) {}
        else if( ColumnName.equals( AD_USER_ID )) {}
        else if( ColumnName.equals( C_BPARTNER_ID )) {
            MBPartner bp = new MBPartner( Env.getCtx(),to_ID,null );

            if( bp.getID() != 0 ) {
                MPayment[] payments = MPayment.getOfBPartner( Env.getCtx(),bp.getC_BPartner_ID(),null );

                for( int i = 0;i < payments.length;i++ ) {
                    MPayment payment = payments[ i ];

                    if( payment.testAllocation()) {
                        payment.save();
                    }
                }

                MInvoice[] invoices = MInvoice.getOfBPartner( Env.getCtx(),bp.getC_BPartner_ID(),null );

                for( int i = 0;i < invoices.length;i++ ) {
                    MInvoice invoice = invoices[ i ];

                    if( invoice.testAllocation()) {
                        invoice.save();
                    }
                }

                bp.setTotalOpenBalance();
                bp.save();
            }
        } else if( ColumnName.equals( M_PRODUCT_ID )) {}
    }    // postMerge
}    // VMerge



/*
 *  @(#)VMerge.java   02.07.07
 * 
 *  Fin del fichero VMerge.java
 *  
 *  Versión 2.2
 *
 */
