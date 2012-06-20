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



package org.openXpertya.grid;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.compiere.swing.CButton;
import org.compiere.swing.CCheckBox;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CDialog;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ALayout;
import org.openXpertya.apps.ALayoutConstraint;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.model.MRecordAccess;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RecordAccessDialog extends CDialog {

    /**
     * Constructor de la clase ...
     *
     *
     * @param owner
     * @param AD_Table_ID
     * @param Record_ID
     */

    public RecordAccessDialog( JFrame owner,int AD_Table_ID,int Record_ID ) {
        super( owner,Msg.translate( Env.getCtx(),"RecordAccessDialog" ));
        log.info( "AD_Table_ID=" + AD_Table_ID + ", Record_ID=" + Record_ID );
        m_AD_Table_ID = AD_Table_ID;
        m_Record_ID   = Record_ID;

        try {
            dynInit();
            jbInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"RecordAccessDialog",e );
        }

        AEnv.showCenterWindow( owner,this );
    }    // RecordAccessDialog

    /** Descripción de Campos */

    private int m_AD_Table_ID;

    /** Descripción de Campos */

    private int m_Record_ID;

    /** Descripción de Campos */

    private ArrayList m_recordAccesss = new ArrayList();

    /** Descripción de Campos */

    private int m_currentRow = 0;

    /** Descripción de Campos */

    private MRecordAccess m_currentData = null;

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel( new ALayout());

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private CLabel roleLabel = new CLabel( Msg.translate( Env.getCtx(),"AD_Role_ID" ));

    /** Descripción de Campos */

    private CComboBox roleField = null;

    /** Descripción de Campos */

    private CCheckBox cbActive = new CCheckBox( Msg.translate( Env.getCtx(),"IsActive" ));

    /** Descripción de Campos */

    private CCheckBox cbExclude = new CCheckBox( Msg.translate( Env.getCtx(),"IsExclude" ));

    /** Descripción de Campos */

    private CCheckBox cbReadOnly = new CCheckBox( Msg.translate( Env.getCtx(),"IsReadOnly" ));

    /** Descripción de Campos */

    private CCheckBox cbDependent = new CCheckBox( Msg.translate( Env.getCtx(),"IsDependentEntities" ));

    /** Descripción de Campos */

    private CButton bDelete = AEnv.getButton( "Delete" );

    /** Descripción de Campos */

    private CButton bNew = AEnv.getButton( "New" );

    /** Descripción de Campos */

    private JLabel rowNoLabel = new JLabel();

    /** Descripción de Campos */

    private CButton bUp = AEnv.getButton( "Previous" );

    /** Descripción de Campos */

    private CButton bDown = AEnv.getButton( "Next" );

    /** Descripción de Campos */

    private ConfirmPanel confirmPanel = new ConfirmPanel( true );

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {

        // Load Roles

        String sql = MRole.getDefault().addAccessSQL( "SELECT AD_Role_ID, Name FROM AD_Role ORDER BY 2","AD_Role",MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        roleField = new CComboBox( DB.getKeyNamePairs( sql,false ));

        // Load Record Access for all roles

        sql = "SELECT * FROM AD_Record_Access " + "WHERE AD_Table_ID=? AND Record_ID=? AND AD_Client_ID=?";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_AD_Table_ID );
            pstmt.setInt( 2,m_Record_ID );
            pstmt.setInt( 3,Env.getAD_Client_ID( Env.getCtx()));

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                m_recordAccesss.add( new MRecordAccess( Env.getCtx(),rs,null ));
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

        log.fine( "dynInit - RecordAccess #" + m_recordAccesss.size());
        setLine( 0,false );
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        this.getContentPane().setLayout( mainLayout );
        this.getContentPane().add( centerPanel,BorderLayout.CENTER );
        this.getContentPane().add( confirmPanel,BorderLayout.SOUTH );

        //

        centerPanel.add( bUp,new ALayoutConstraint( 0,0 ));
        centerPanel.add( bNew,new ALayoutConstraint( 0,6 ));
        centerPanel.add( roleLabel,new ALayoutConstraint( 1,0 ));
        centerPanel.add( roleField,null );
        centerPanel.add( cbActive,null );
        centerPanel.add( cbExclude,null );
        centerPanel.add( cbReadOnly,null );
        centerPanel.add( cbDependent,null );
        centerPanel.add( bDelete,null );
        centerPanel.add( bDown,new ALayoutConstraint( 2,0 ));
        centerPanel.add( rowNoLabel,new ALayoutConstraint( 2,6 ));

        //

        Dimension size = centerPanel.getPreferredSize();

        size.width = 600;
        centerPanel.setPreferredSize( size );

        //

        bUp.addActionListener( this );
        bDown.addActionListener( this );
        bDelete.addActionListener( this );
        bNew.addActionListener( this );
        confirmPanel.addActionListener( this );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param rowDelta
     * @param newRecord
     */

    private void setLine( int rowDelta,boolean newRecord ) {
        log.fine( "setLine - delta=" + rowDelta + ", new=" + newRecord + " - currentRow=" + m_currentRow + ", size=" + m_recordAccesss.size());

        int maxIndex = 0;

        // nothing defined

        if( m_recordAccesss.size() == 0 ) {
            m_currentRow = 0;
            maxIndex     = 0;
            newRecord    = true;
            setLine( null );
        } else if( newRecord ) {
            m_currentRow = m_recordAccesss.size();
            maxIndex     = m_currentRow;
            setLine( null );
        } else {
            m_currentRow += rowDelta;
            maxIndex     = m_recordAccesss.size() - 1;

            if( m_currentRow < 0 ) {
                m_currentRow = 0;
            } else if( m_currentRow > maxIndex ) {
                m_currentRow = maxIndex;
            }

            //

            MRecordAccess ra = ( MRecordAccess )m_recordAccesss.get( m_currentRow );

            setLine( ra );
        }

        // Label

        StringBuffer txt = new StringBuffer();

        if( newRecord ) {
            txt.append( "+" );
        }

        txt.append( m_currentRow + 1 ).append( "/" ).append( maxIndex + 1 );
        rowNoLabel.setText( txt.toString());

        // set up/down

        bUp.setEnabled( m_currentRow > 0 );
        bDown.setEnabled( m_currentRow < maxIndex );
    }    // setLine

    /**
     * Descripción de Método
     *
     *
     * @param ra
     */

    private void setLine( MRecordAccess ra ) {
        int     AD_Role_ID = 0;
        boolean active     = true;
        boolean exclude    = true;
        boolean readonly   = false;
        boolean dependent  = false;

        //

        if( ra != null ) {
            AD_Role_ID = ra.getAD_Role_ID();
            active     = ra.isActive();
            exclude    = ra.isExclude();
            readonly   = ra.isReadOnly();
            dependent  = ra.isDependentEntities();
        }

        cbActive.setSelected( active );
        cbExclude.setSelected( exclude );
        cbReadOnly.setSelected( readonly );
        cbDependent.setSelected( dependent );
        bDelete.setEnabled( ra != null );

        //

        KeyNamePair selection = null;

        for( int i = 0;i < roleField.getItemCount();i++ ) {
            KeyNamePair pp = ( KeyNamePair )roleField.getItemAt( i );

            if( pp.getKey() == AD_Role_ID ) {
                selection = pp;
            }
        }

        if( (selection != null) && (ra != null) ) {
            roleField.setSelectedItem( selection );
            m_currentData = ra;
            log.fine( "setLine - " + ra );
        } else {
            m_currentData = null;
        }
    }    // setLine

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == bUp ) {
            setLine( -1,false );
        } else if( e.getSource() == bDown ) {
            setLine( +1,false );
        } else if( e.getSource() == bNew ) {
            setLine( 0,true );
        } else {
            if( e.getSource() == bDelete ) {
                cmd_delete();
            } else if( e.getActionCommand().equals( ConfirmPanel.A_OK )) {
                if( !cmd_save()) {
                    return;
                }
            }

            dispose();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean cmd_save() {
        KeyNamePair pp = ( KeyNamePair )roleField.getSelectedItem();

        roleField.setBackground( pp == null );

        if( pp == null ) {
            return false;
        }

        int AD_Role_ID = pp.getKey();

        //

        boolean isActive            = cbActive.isSelected();
        boolean isExclude           = cbExclude.isSelected();
        boolean isReadOnly          = cbReadOnly.isSelected();
        boolean isDependentEntities = cbDependent.isSelected();

        //

        if( m_currentData == null ) {
            m_currentData = new MRecordAccess( Env.getCtx(),AD_Role_ID,m_AD_Table_ID,m_Record_ID,null );
            m_recordAccesss.add( m_currentData );
            m_currentRow = m_recordAccesss.size() - 1;
        }

        m_currentData.setIsActive( isActive );
        m_currentData.setIsExclude( isExclude );
        m_currentData.setIsReadOnly( isReadOnly );
        m_currentData.setIsDependentEntities( isDependentEntities );

        boolean success = m_currentData.save();

        //

        log.fine( "cmd_save - Success=" + success );

        return success;
    }    // cmd_save

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private boolean cmd_delete() {
        boolean success = false;

        if( m_currentData == null ) {
            log.log( Level.SEVERE,"cmd_delete - no data" );
        } else {
            success       = m_currentData.delete( true );
            m_currentData = null;
            m_recordAccesss.remove( m_currentRow );
            log.fine( "cmd_delete - Success=" + success );
        }

        return success;
    }    // cmd_delete
}    // RecordAccessDialog



/*
 *  @(#)RecordAccessDialog.java   02.07.07
 * 
 *  Fin del fichero RecordAccessDialog.java
 *  
 *  Versión 2.2
 *
 */
