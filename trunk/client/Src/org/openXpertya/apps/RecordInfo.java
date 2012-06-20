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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.table.DefaultTableModel;

import org.compiere.swing.CDialog;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.compiere.swing.CTextArea;
import org.openXpertya.grid.VTable;
import org.openXpertya.model.DataStatusEvent;
import org.openXpertya.model.MChangeLog;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MUser;
import org.openXpertya.model.M_Column;
import org.openXpertya.model.M_Table;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.NamePair;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RecordInfo extends CDialog {

    /**
     * Constructor de la clase ...
     *
     *
     * @param owner
     * @param title
     * @param dse
     */

    public RecordInfo( Frame owner,String title,DataStatusEvent dse ) {
        super( owner,title,true );

        try {
            jbInit( dynInit( dse,title ));
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }

        AEnv.positionCenterWindow( owner,this );
    }    // RecordInfo

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel( new BorderLayout( 0,0 ));

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private CScrollPane scrollPane = new CScrollPane();

    /** Descripción de Campos */

    private VTable table = new VTable();

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( false );

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private Vector m_data = new Vector();

    /** Descripción de Campos */

    private StringBuffer m_info = new StringBuffer();

    /** Descripción de Campos */

    private SimpleDateFormat m_dateTimeFormat = DisplayType.getDateFormat( DisplayType.DateTime,Env.getLanguage( Env.getCtx()));

    /** Descripción de Campos */

    private SimpleDateFormat m_dateFormat = DisplayType.getDateFormat( DisplayType.DateTime,Env.getLanguage( Env.getCtx()));

    /** Descripción de Campos */

    private DecimalFormat m_numberFormat = DisplayType.getNumberFormat( DisplayType.Number,Env.getLanguage( Env.getCtx()));

    /** Descripción de Campos */

    private DecimalFormat m_amtFormat = DisplayType.getNumberFormat( DisplayType.Amount,Env.getLanguage( Env.getCtx()));

    /** Descripción de Campos */

    private DecimalFormat m_intFormat = DisplayType.getNumberFormat( DisplayType.Integer,Env.getLanguage( Env.getCtx()));

    /**
     * Descripción de Método
     *
     *
     * @param showTable
     *
     * @throws Exception
     */

    private void jbInit( boolean showTable ) throws Exception {
        getContentPane().add( mainPanel );

        CTextArea info = new CTextArea( m_info.toString());

        info.setReadWrite( false );
        info.setOpaque( false );    // transparent
        info.setForeground( Color.blue );
        info.setBorder( null );

        //

        if( showTable ) {
            mainPanel.add( info,BorderLayout.NORTH );
            mainPanel.add( scrollPane,BorderLayout.CENTER );
            scrollPane.getViewport().add( table );
            scrollPane.setPreferredSize( new Dimension( 500,100 ));
        } else {
            info.setPreferredSize( new Dimension( 400,75 ));
            mainPanel.add( info,BorderLayout.CENTER );
        }

        //

        mainPanel.add( confirmPanel,BorderLayout.SOUTH );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param dse
     * @param title
     *
     * @return
     */

    private boolean dynInit( DataStatusEvent dse,String title ) {
        if( dse.CreatedBy == null ) {
            return false;
        }

        // Info

        MUser user = MUser.get( Env.getCtx(),dse.CreatedBy.intValue());

        m_info.append( " " ).append( Msg.translate( Env.getCtx(),"CreatedBy" )).append( ": " ).append( user.getName()).append( " - " ).append( m_dateTimeFormat.format( dse.Created )).append( "\n" );

        if( !dse.Created.equals( dse.Updated ) ||!dse.CreatedBy.equals( dse.UpdatedBy )) {
            if( !dse.CreatedBy.equals( dse.UpdatedBy )) {
                user = MUser.get( Env.getCtx(),dse.UpdatedBy.intValue());
            }

            m_info.append( " " ).append( Msg.translate( Env.getCtx(),"UpdatedBy" )).append( ": " ).append( user.getName()).append( " - " ).append( m_dateTimeFormat.format( dse.Updated )).append( "\n" );
        }

        if( (dse.Info != null) && (dse.Info.length() > 0) ) {
            m_info.append( "\n (" ).append( dse.Info ).append( ")" );
        }

        // Title

        if( dse.AD_Table_ID != 0 ) {
            M_Table table = M_Table.get( Env.getCtx(),dse.AD_Table_ID );

            setTitle( title + " - " + table.getName());
        }

        // Only Client Preference can view Change Log

        if( !MRole.PREFERENCETYPE_Client.equals( MRole.getDefault().getPreferenceType())) {
            return false;
        }

        int Record_ID = 0;

        if( dse.Record_ID instanceof Integer ) {
            Record_ID = (( Integer )dse.Record_ID ).intValue();
        } else {
            log.info( "dynInit - Invalid Record_ID=" + dse.Record_ID );
        }

        if( Record_ID == 0 ) {
            return false;
        }

        // Data

        String sql = "SELECT AD_Column_ID, Updated, UpdatedBy, OldValue, NewValue, operationtype, ad_componentobjectuid, ad_componentversion_id " + "FROM AD_ChangeLog " + "WHERE AD_Table_ID=? AND Record_ID=? " + "ORDER BY Updated DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,dse.AD_Table_ID );
            pstmt.setInt( 2,Record_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                addLine( rs.getInt( 1 ),rs.getTimestamp( 2 ),rs.getInt( 3 ),rs.getString( 4 ),rs.getString( 5 ), rs.getString(6), rs.getString(7), rs.getInt(8));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"dynInit",e );
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

        Vector columnNames = new Vector();

        columnNames.add( Msg.translate( Env.getCtx(),"AD_Column_ID" ));
        columnNames.add( Msg.translate( Env.getCtx(),"NewValue" ));
        columnNames.add( Msg.translate( Env.getCtx(),"OldValue" ));
        columnNames.add( Msg.translate( Env.getCtx(),"UpdatedBy" ));
        columnNames.add( Msg.translate( Env.getCtx(),"Updated" ));
        columnNames.add( Msg.translate( Env.getCtx(),"OperationType" ));
        columnNames.add( Msg.translate( Env.getCtx(),"AD_ComponentObjectUID" ));
        columnNames.add( Msg.translate( Env.getCtx(),"AD_ComponentVersion_ID" ));

        DefaultTableModel model = new DefaultTableModel( m_data,columnNames );

        table.setModel( model );
        table.autoSize( false );

        return true;
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @param AD_Column_ID
     * @param Updated
     * @param UpdatedBy
     * @param OldValue
     * @param NewValue
     */

    private void addLine( int AD_Column_ID,Timestamp Updated,int UpdatedBy,String OldValue,String NewValue, String operationType, String componentObjectUID, Integer componentVersion_ID ) {
        Vector line = new Vector();

        // Column

        M_Column column = M_Column.get( Env.getCtx(),AD_Column_ID );

        line.add( column.getName());

        //

        if( OldValue.equals( MChangeLog.NULL )) {
            OldValue = null;
        }

        String showOldValue = OldValue;

        if( NewValue.equals( MChangeLog.NULL )) {
            NewValue = null;
        }

        String showNewValue = NewValue;

        //

        try {
            if( DisplayType.isText( column.getAD_Reference_ID())) {
                ;
            } else if( column.getAD_Reference_ID() == DisplayType.YesNo ) {
                if( OldValue != null ) {
                    boolean yes = OldValue.equals( "true" ) || OldValue.equals( "Y" );

                    showOldValue = Msg.getMsg( Env.getCtx(),yes
                            ?"Y"
                            :"N" );
                }

                if( NewValue != null ) {
                    boolean yes = NewValue.equals( "true" ) || NewValue.equals( "Y" );

                    showNewValue = Msg.getMsg( Env.getCtx(),yes
                            ?"Y"
                            :"N" );
                }
            } else if( column.getAD_Reference_ID() == DisplayType.Amount ) {
                if( OldValue != null ) {
                    showOldValue = m_amtFormat.format( new BigDecimal( OldValue ));
                }

                if( NewValue != null ) {
                    showNewValue = m_amtFormat.format( new BigDecimal( NewValue ));
                }
            } else if( column.getAD_Reference_ID() == DisplayType.Integer ) {
                if( OldValue != null ) {
                    showOldValue = m_intFormat.format( new Integer( OldValue ));
                }

                if( NewValue != null ) {
                    showNewValue = m_intFormat.format( new Integer( NewValue ));
                }
            } else if( DisplayType.isNumeric( column.getAD_Reference_ID())) {
                if( OldValue != null ) {
                    showOldValue = m_numberFormat.format( new BigDecimal( OldValue ));
                }

                if( NewValue != null ) {
                    showNewValue = m_numberFormat.format( new BigDecimal( NewValue ));
                }
            } else if( column.getAD_Reference_ID() == DisplayType.Date ) {
                if( OldValue != null ) {
                    showOldValue = m_dateFormat.format( Timestamp.valueOf( OldValue ));
                }

                if( NewValue != null ) {
                    showNewValue = m_dateFormat.format( Timestamp.valueOf( NewValue ));
                }
            } else if( column.getAD_Reference_ID() == DisplayType.DateTime ) {
                if( OldValue != null ) {
                    showOldValue = m_dateTimeFormat.format( Timestamp.valueOf( OldValue ));
                }

                if( NewValue != null ) {
                    showNewValue = m_dateTimeFormat.format( Timestamp.valueOf( NewValue ));
                }
            } else if( DisplayType.isLookup( column.getAD_Reference_ID())) {
                MLookup lookup = MLookupFactory.get( Env.getCtx(),0,AD_Column_ID,column.getAD_Reference_ID(),Env.getLanguage( Env.getCtx()),column.getColumnName(),column.getAD_Reference_Value_ID(),column.isParent(),null );

                if( OldValue != null ) {
                    Object key = OldValue;

                    if( column.getAD_Reference_ID() != DisplayType.List ) {
                        key = new Integer( OldValue );
                    }

                    NamePair pp = lookup.get( key );

                    if( pp != null ) {
                        showOldValue = pp.getName();
                    }
                }

                if( NewValue != null ) {
                    Object key = NewValue;

                    if( column.getAD_Reference_ID() != DisplayType.List ) {
                        key = new Integer( NewValue );
                    }

                    NamePair pp = lookup.get( key );

                    if( pp != null ) {
                        showNewValue = pp.getName();
                    }
                }
            } else if( DisplayType.isLOB( column.getAD_Reference_ID())) {
                ;
            }
        } catch( Exception e ) {
            log.log( Level.WARNING,OldValue + "->" + NewValue,e );
        }

        //

        line.add( showNewValue );
        line.add( showOldValue );

        // UpdatedBy

        MUser user = MUser.get( Env.getCtx(),UpdatedBy );

        line.add( user.getName());

        // Updated

        line.add( m_dateFormat.format( Updated ));
        
        if((operationType != null) && (operationType.length() > 0)){
        	line.add(MRefList.getListName(Env.getCtx(), MChangeLog.OPERATIONTYPE_AD_Reference_ID, operationType));
        }
        else{
        	line.add(null);
        }
        line.add(componentObjectUID);
        if((componentVersion_ID != null) && (componentVersion_ID.intValue() != 0)){
        	MComponentVersion version = new MComponentVersion(Env.getCtx(), componentVersion_ID, null);
            line.add(version.getName());
        }
        else{
        	line.add(null);
        }
        m_data.add( line );
    }    // addLine

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        dispose();
    }    // actionPerformed
}    // RecordInfo



/*
 *  @(#)RecordInfo.java   02.07.07
 * 
 *  Fin del fichero RecordInfo.java
 *  
 *  Versión 2.2
 *
 */
