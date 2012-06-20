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



package org.openXpertya.apps.wf;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CTextArea;
import org.compiere.swing.CTextField;
import org.compiere.swing.CTextPane;
import org.openXpertya.apps.ADialog;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AMenu;
import org.openXpertya.apps.AWindow;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.apps.StatusBar;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.M_Column;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.ValueNamePair;
import org.openXpertya.wf.MWFActivity;
import org.openXpertya.wf.MWFNode;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WFActivity extends CPanel implements FormPanel,ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Constructor de la clase ...
     *
     */

    public WFActivity() {
        super();

        // needs to call init

    }    // WFActivity

    /**
     * Constructor de la clase ...
     *
     *
     * @param menu
     */

    public WFActivity( AMenu menu ) {
        super();
        log.config( "" );

        try {
            dynInit( 0 );
            jbInit();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }

        m_menu = menu;
    }    // WFActivity

    /** Descripción de Campos */

    private int m_WindowNo = 0;

    /** Descripción de Campos */

    private FormFrame m_frame = null;

    /** Descripción de Campos */

    private AMenu m_menu = null;

    /** Descripción de Campos */

    private MWFActivity[] m_activities = null;

    /** Descripción de Campos */

    private MWFActivity m_activity = null;

    /** Descripción de Campos */

    private int m_index = 0;

    /** Descripción de Campos */

    private M_Column m_column = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( WFActivity.class );

    //

    /** Descripción de Campos */

    private CPanel centerPanel = new CPanel();

    /** Descripción de Campos */

    private GridBagLayout centerLayout = new GridBagLayout();

    /** Descripción de Campos */

    private CLabel lNode = new CLabel( Msg.translate( Env.getCtx(),"AD_WF_Node_ID" ));

    /** Descripción de Campos */

    private CTextField fNode = new CTextField();

    /** Descripción de Campos */

    private CLabel lDesctiption = new CLabel( Msg.translate( Env.getCtx(),"Description" ));

    /** Descripción de Campos */

    private CTextArea fDescription = new CTextArea();

    /** Descripción de Campos */

    private CLabel lHelp = new CLabel( Msg.translate( Env.getCtx(),"Help" ));

    /** Descripción de Campos */

    private CTextArea fHelp = new CTextArea();

    /** Descripción de Campos */

    private CLabel lHistory = new CLabel( Msg.translate( Env.getCtx(),"History" ));

    /** Descripción de Campos */

    private CTextPane fHistory = new CTextPane();

    /** Descripción de Campos */

    private CLabel lAnswer = new CLabel( Msg.getMsg( Env.getCtx(),"Answer" ));

    /** Descripción de Campos */

    private JPanel answers = new JPanel( new FlowLayout( FlowLayout.LEADING ));

    /** Descripción de Campos */

    private CTextField fAnswerText = new CTextField();

    /** Descripción de Campos */

    private CComboBox fAnswerList = new CComboBox();

    /** Descripción de Campos */

    private CButton fAnswerButton = new CButton();

    /** Descripción de Campos */

    private CButton bPrevious = AEnv.getButton( "Previous" );

    /** Descripción de Campos */

    private CButton bNext = AEnv.getButton( "Next" );

    /** Descripción de Campos */

    private CButton bZoom = AEnv.getButton( "Zoom" );

    /** Descripción de Campos */

    private CLabel lTextMsg = new CLabel( Msg.getMsg( Env.getCtx(),"Messages" ));

    /** Descripción de Campos */

    private CTextArea fTextMsg = new CTextArea();

    /** Descripción de Campos */

    private CButton bOK = ConfirmPanel.createOKButton( true );

    /** Descripción de Campos */

    private CButton bRefresh = ConfirmPanel.createRefreshButton(false);

    /** Descripción de Campos */

    private VLookup fForward = null;    // dynInit

    /** Descripción de Campos */

    private CLabel lForward = new CLabel( Msg.getMsg( Env.getCtx(),"Forward" ));

    /** Descripción de Campos */

    private CLabel lOptional = new CLabel( "(" + Msg.translate( Env.getCtx(),"Optional" ) + ")" );

    /** Descripción de Campos */

    private StatusBar statusBar = new StatusBar();

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     */

    private void dynInit( int WindowNo ) {
        loadActivities();

        // Forward

        fForward = VLookup.createUser( WindowNo );
    }    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        centerPanel.setLayout( centerLayout );
        fNode.setReadWrite( false );
        fDescription.setReadWrite( false );
        fDescription.setPreferredSize( new Dimension( 130,40 ));
        fHelp.setReadWrite( false );
        fHelp.setPreferredSize( new Dimension( 150,80 ));
        fHistory.setReadWrite( false );
        fHistory.setPreferredSize( new Dimension( 150,60 ));
        fTextMsg.setPreferredSize( new Dimension( 150,40 ));

        //

        bPrevious.addActionListener( this );
        bNext.addActionListener( this );
        bZoom.addActionListener( this );
        bOK.addActionListener( this );
        bRefresh.addActionListener(this);

        //

        this.setLayout( new BorderLayout());
        this.add( centerPanel,BorderLayout.CENTER );
        this.add( statusBar,BorderLayout.SOUTH );

        //

        answers.setOpaque( false );
        answers.add( fAnswerText );
        answers.add( fAnswerList );
        answers.add( fAnswerButton );
        fAnswerButton.addActionListener( this );

        //

        centerPanel.add( lNode,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 5,10,5,5 ),0,0 ));
        centerPanel.add( fNode,new GridBagConstraints( 1,0,2,1,0.5,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
        centerPanel.add( bPrevious,new GridBagConstraints( 3,0,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 5,5,5,10 ),0,0 ));
        centerPanel.add( lDesctiption,new GridBagConstraints( 0,1,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 5,10,5,5 ),0,0 ));
        centerPanel.add( fDescription,new GridBagConstraints( 1,1,2,1,0.0,0.1,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,5 ),0,0 ));
        centerPanel.add( bNext,new GridBagConstraints( 3,1,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 5,5,5,10 ),0,0 ));
        centerPanel.add( lHelp,new GridBagConstraints( 0,2,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 5,10,5,5 ),0,0 ));
        centerPanel.add( fHelp,new GridBagConstraints( 1,2,3,1,0.0,0.1,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,10 ),0,0 ));
        centerPanel.add( lHistory,new GridBagConstraints( 0,3,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 5,10,5,5 ),0,0 ));
        centerPanel.add( fHistory,new GridBagConstraints( 1,3,3,1,0.5,0.5,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH,new Insets( 5,0,5,10 ),0,0 ));
        centerPanel.add( lAnswer,new GridBagConstraints( 0,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 10,10,5,5 ),0,0 ));
        centerPanel.add( answers,new GridBagConstraints( 1,4,2,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 10,0,5,5 ),0,0 ));
        centerPanel.add( bZoom,new GridBagConstraints( 3,4,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 10,0,10,10 ),0,0 ));
        centerPanel.add( lTextMsg,new GridBagConstraints( 0,5,1,1,0.0,0.0,GridBagConstraints.NORTHEAST,GridBagConstraints.NONE,new Insets( 5,10,5,5 ),0,0 ));
        centerPanel.add( fTextMsg,new GridBagConstraints( 1,5,3,1,0.5,0.0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL,new Insets( 5,0,5,10 ),0,0 ));
        centerPanel.add( lForward,new GridBagConstraints( 0,6,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 10,10,5,5 ),0,0 ));
        centerPanel.add( fForward,new GridBagConstraints( 1,6,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 10,0,5,0 ),0,0 ));
        centerPanel.add( lOptional,new GridBagConstraints( 2,6,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 10,5,5,5 ),0,0 ));
        centerPanel.add( bRefresh,new GridBagConstraints( 3,1,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 33,5,5,10 ),0,0 ));
        centerPanel.add( bOK,new GridBagConstraints( 3,6,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 10,5,5,10 ),0,0 ));
    }    // jbInit

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

        //

        log.info( "" );

        try {
            dynInit( WindowNo );
            jbInit();

            //
            // this.setPreferredSize(new Dimension (400,400));

            frame.getContentPane().add( this,BorderLayout.CENTER );
            display();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"",e );
        }
    }    // init

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
     * @return
     */

    public int loadActivities() {
        long      start = System.currentTimeMillis();
        ArrayList list  = new ArrayList();
        String    sql   = "SELECT * FROM AD_WF_Activity a " + "WHERE a.Processed='N' AND a.WFState='OS' AND ("

        // Owner of Activity

        + " a.AD_User_ID=?"    // #1

        // Invoker (if no invoker = all)

        + " OR EXISTS (SELECT * FROM AD_WF_Responsible r WHERE a.AD_WF_Responsible_ID=r.AD_WF_Responsible_ID" + " AND COALESCE(r.AD_User_ID,0)=0 AND (a.AD_User_ID=? OR a.AD_User_ID IS NULL))"    // #2

        // Responsible User

        + " OR EXISTS (SELECT * FROM AD_WF_Responsible r WHERE a.AD_WF_Responsible_ID=r.AD_WF_Responsible_ID" + " AND r.AD_User_ID=?)"    // #3

        // Responsible Role

        + " OR EXISTS (SELECT * FROM AD_WF_Responsible r INNER JOIN AD_User_Roles ur ON (r.AD_Role_ID=ur.AD_Role_ID)" + " WHERE a.AD_WF_Responsible_ID=r.AD_WF_Responsible_ID AND ur.AD_User_ID=?)"    // #4

        //

        + ") ORDER BY a.Priority DESC, Created";
        int               AD_User_ID = Env.getAD_User_ID( Env.getCtx());
        PreparedStatement pstmt      = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_User_ID );
            pstmt.setInt( 2,AD_User_ID );
            pstmt.setInt( 3,AD_User_ID );
            pstmt.setInt( 4,AD_User_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MWFActivity( Env.getCtx(),rs,null ));
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

        m_activities = new MWFActivity[ list.size()];
        list.toArray( m_activities );

        //

        log.fine( "#" + m_activities.length + "(" + ( System.currentTimeMillis() - start ) + "ms)" );

        return m_activities.length;
    }    // loadActivities

    /**
     * Descripción de Método
     *
     */

    public void display() {
        log.fine( "Index=" + m_index );

        //

        fTextMsg.setText( "" );
        fAnswerText.setVisible( false );
        fAnswerList.setVisible( false );
        fAnswerButton.setIcon( Env.getImageIcon( "mWindow.gif" ));
        fAnswerButton.setVisible( false );
        fTextMsg.setReadWrite( m_activities.length != 0 );
        bZoom.setEnabled( m_activities.length != 0 );
        bOK.setEnabled( m_activities.length != 0 );
        fForward.setValue( null );
        fForward.setEnabled( m_activities.length != 0 );
        statusBar.setStatusDB( String.valueOf( m_index ) + "/" + m_activities.length );
        m_activity = null;

        if( m_activities.length > 0 ) {
            if( m_index + 1 > m_activities.length ) {
                log.log( Level.SEVERE,"Index (" + m_index + ") greater then activity length=" + m_activities.length );
            } else {
                m_activity = m_activities[ m_index ];
            }
        }

        // Nothing to show

        if( m_activity == null ) {
            fNode.setText( "" );
            fDescription.setText( "" );
            fHelp.setText( "" );
            fHistory.setText( "" );
            statusBar.setStatusDB( "0/0" );
            statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"WFNoActivities" ));
            bNext.setEnabled( false );
            bPrevious.setEnabled( false );
            m_menu.updateInfo();

            return;
        }

        // Display Activity

        fNode.setText( m_activity.getNodeName());
        fDescription.setText( m_activity.getNodeDescription());
        fHelp.setText( m_activity.getNodeHelp());

        //

        fHistory.setText( m_activity.getHistoryHTML());

        // User Actions

        MWFNode node = m_activity.getNode();

        if( MWFNode.ACTION_UserChoice.equals( node.getAction())) {
            if( m_column == null ) {
                m_column = node.getColumn();
            }

            if( (m_column != null) && (m_column.getID() != 0) ) {
                int dt = m_column.getAD_Reference_ID();

                if( dt == DisplayType.YesNo ) {
                    ValueNamePair[] values = MRefList.getList( 319,false );    // _YesNo

                    fAnswerList.setModel( new DefaultComboBoxModel( values ));
                    fAnswerList.setVisible( true );
                } else if( dt == DisplayType.List ) {
                    ValueNamePair[] values = MRefList.getList( m_column.getAD_Reference_Value_ID(),false );

                    fAnswerList.setModel( new DefaultComboBoxModel( values ));
                    fAnswerList.setVisible( true );
                } else    // other display types come here
                {
                    fAnswerText.setText( "" );
                    fAnswerText.setVisible( true );
                }
            }
        }

        // --

        else if( MWFNode.ACTION_UserWindow.equals( node.getAction()) || MWFNode.ACTION_UserForm.equals( node.getAction())) {
            fAnswerButton.setText( node.getName());
            fAnswerButton.setToolTipText( node.getDescription());
            fAnswerButton.setVisible( true );
        } else if( MWFNode.ACTION_UserWorkbench.equals( node.getAction())) {
            log.log( Level.SEVERE,"Workflow Action not implemented yet" );
        } else {
            log.log( Level.SEVERE,"Unknown Node Action: " + node.getAction());
        }

        //

        if( m_menu != null ) {
            m_menu.updateInfo();    // calls loadActivities - updates menu tab
        }

        // End

        if( m_index + 1 >= m_activities.length ) {
            m_index = m_activities.length - 1;
            bNext.setEnabled( false );
        } else {
            bNext.setEnabled( true );
        }

        // Start

        if( m_index <= 0 ) {
            m_index = 0;
            bPrevious.setEnabled( false );
        } else {
            bPrevious.setEnabled( true );
        }

        statusBar.setStatusDB(( m_index + 1 ) + "/" + m_activities.length );
        statusBar.setStatusLine( Msg.getMsg( Env.getCtx(),"WFActivities" ));
    }    // display

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        this.setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));

        //

        if( (e.getSource() == bNext) || (e.getSource() == bPrevious) ) {
            if( e.getSource() == bNext ) {
                m_index++;
            } else {
                m_index--;
            }

            display();
        } else if( e.getSource() == bZoom ) {
            cmd_zoom();
        } else if( e.getSource() == bOK ) {
            cmd_OK();
        } else if( e.getSource() == fAnswerButton ) {
            cmd_button();
        } else if(e.getSource() == bRefresh){
        	cmd_refresh();
        }

        //

        this.setCursor( Cursor.getDefaultCursor());
    }    // actionPerformed
    
    
    private void cmd_refresh(){
    	log.config( "Activity=" + m_activity );
        
    	this.loadActivities();
        this.display();
    }

    /**
     * Descripción de Método
     *
     */

    private void cmd_zoom() {
        log.config( "Activity=" + m_activity );

        if( m_activity == null ) {
            return;
        }

        AEnv.zoom( m_activity.getAD_Table_ID(),m_activity.getRecord_ID());
    }    // cmd_zoom

    /**
     * Descripción de Método
     *
     */

    private void cmd_button() {
        log.config( "Activity=" + m_activity );

        if( m_activity == null ) {
            return;
        }

        //

        MWFNode node = m_activity.getNode();

        if( MWFNode.ACTION_UserWindow.equals( node.getAction())) {
            int     AD_Window_ID = node.getAD_Window_ID();    // Explicit Window
            String  ColumnName   = m_activity.getPO().get_TableName() + "_ID";
            int     Record_ID    = m_activity.getRecord_ID();
            MQuery  query        = MQuery.getEqualQuery( ColumnName,Record_ID );
            boolean IsSOTrx      = m_activity.isSOTrx();

            //

            log.info( "Zoom to AD_Window_ID=" + AD_Window_ID + " - " + query + " (IsSOTrx=" + IsSOTrx + ")" );

            AWindow frame = new AWindow();

            if( !frame.initWindow( AD_Window_ID,query )) {
                return;
            }

            AEnv.showCenterScreen( frame );
            frame = null;
        } else if( MWFNode.ACTION_UserForm.equals( node.getAction())) {
            int       AD_Form_ID = node.getAD_Form_ID();
            FormFrame ff         = new FormFrame();

            ff.openForm( AD_Form_ID );
            ff.pack();
            AEnv.showCenterScreen( ff );
        } else if( MWFNode.ACTION_UserWorkbench.equals( node.getAction())) {}
        else {
            log.log( Level.SEVERE,"No User Action:" + node.getAction());
        }
    }    // cmd_button

    /**
     * Descripción de Método
     *
     */

    private void cmd_OK() {
        log.config( "Activity=" + m_activity );

        if( m_activity == null ) {
            return;
        }

        int    AD_User_ID = Env.getAD_User_ID( Env.getCtx());
        String textMsg    = fTextMsg.getText();

        //

        MWFNode node    = m_activity.getNode();
        Object  forward = fForward.getValue();

        if( forward != null ) {
            log.config( "Forward to " + forward );

            int fw = (( Integer )forward ).intValue();

            if( (fw == AD_User_ID) || (fw == 0) ) {
                log.log( Level.SEVERE,"Forward User=" + fw );

                return;
            }

            if( !m_activity.forwardTo( fw,textMsg )) {
                ADialog.error( m_WindowNo,this,"CannotForward" );

                return;
            }
        }

        // User Choice - Answer

        else if( MWFNode.ACTION_UserChoice.equals( node.getAction())) {
            if( m_column == null ) {
                m_column = node.getColumn();
            }

            // Do we have an answer?

            int    dt    = m_column.getAD_Reference_ID();
            String value = fAnswerText.getText();

            if( (dt == DisplayType.YesNo) || (dt == DisplayType.List) ) {
                ValueNamePair pp = ( ValueNamePair )fAnswerList.getSelectedItem();

                value = pp.getValue();
            }

            if( (value == null) || (value.length() == 0) ) {
                ADialog.error( m_WindowNo,this,"FillMandatory",Msg.getMsg( Env.getCtx(),"Answer" ));

                return;
            }

            //

            log.config( "Answer=" + value + " - " + textMsg );

            try {
                m_activity.setUserChoice( AD_User_ID,value,dt,textMsg );
            } catch( Exception e ) {
                log.log( Level.SEVERE,"(1)",e );
                ADialog.error( m_WindowNo,this,"Error",e.toString());

                return;
            }
        }

        // User Action

        else {
            log.config( "Action=" + node.getAction() + " - " + textMsg );

            try {
                m_activity.setUserConfirmation( AD_User_ID,textMsg );
            } catch( Exception e ) {
                log.log( Level.SEVERE,"(2)",e );
                ADialog.error( m_WindowNo,this,"Error",e.toString());

                return;
            }
        }

        // Next

        this.m_index = 0;
        this.cmd_refresh();
    }    // cmd_OK
}    // WFActivity



/*
 *  @(#)WFActivity.java   02.07.07
 * 
 *  Fin del fichero WFActivity.java
 *  
 *  Versión 2.2
 *
 */
