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
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;

import org.compiere.swing.CPanel;
import org.openXpertya.model.Scriptlet;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class ScriptEditor extends JFrame implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     */

    public ScriptEditor() {
        this( Msg.getMsg( Env.getCtx(),"Script" ),null,0 );
    }    // ScriptEditor

    /**
     * Constructor de la clase ...
     *
     *
     * @param title
     * @param script
     * @param WindowNo
     */

    public ScriptEditor( String title,Scriptlet script,int WindowNo ) {
        super( title );
        m_WindowNo = WindowNo;

        if( m_WindowNo == 0 ) {
            m_WindowNo = Env.createWindowNo( this );
        }

        log.info( "Window=" + m_WindowNo );

        try {
            jbInit();
            setScript( script );
            dynInit();
            AEnv.showCenterScreen( this );
            toFront();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"ScriptEditor",ex );
        }
    }    // ScriptEditor

    /** Descripción de Campos */

    private Scriptlet m_script;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private String m_origScript;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( ScriptEditor.class );

    // --

    /** Descripción de Campos */

    private CPanel mainPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout borderLayout1 = new BorderLayout();

    /** Descripción de Campos */

    private JScrollPane editorPane = new JScrollPane();

    /** Descripción de Campos */

    private JTextArea editor = new JTextArea();

    /** Descripción de Campos */

    private JScrollPane variablesPane = new JScrollPane();

    /** Descripción de Campos */

    private JTextPane variables = new JTextPane();

    /** Descripción de Campos */

    private TitledBorder titledBorder1;

    /** Descripción de Campos */

    private TitledBorder titledBorder2;

    /** Descripción de Campos */

    private CPanel northPanel = new CPanel();

    /** Descripción de Campos */

    private CPanel southPanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout southLayout = new BorderLayout();

    /** Descripción de Campos */

    private CPanel okPanel = new CPanel();

    /** Descripción de Campos */

    private JButton bOK = ConfirmPanel.createOKButton( true );

    /** Descripción de Campos */

    private JButton bCancel = ConfirmPanel.createCancelButton( true );

    /** Descripción de Campos */

    private CPanel resultPanel = new CPanel();

    /** Descripción de Campos */

    private JButton bProcess = ConfirmPanel.createProcessButton( true );

    /** Descripción de Campos */

    private JLabel lResult = new JLabel();

    /** Descripción de Campos */

    private JTextField fResult = new JTextField();

    /** Descripción de Campos */

    private CPanel resultVariablePanel = new CPanel();

    /** Descripción de Campos */

    private BorderLayout northLayout = new BorderLayout();

    /** Descripción de Campos */

    private JLabel lResultVariable = new JLabel();

    /** Descripción de Campos */

    private JTextField fResultVariable = new JTextField();

    /** Descripción de Campos */

    private CPanel helpPanel = new CPanel();

    /** Descripción de Campos */

    private JButton bHelp = ConfirmPanel.createHelpButton( true );

    /** Descripción de Campos */

    private GridBagLayout resultVariableLayout = new GridBagLayout();

    /** Descripción de Campos */

    private FlowLayout okLayout = new FlowLayout();

    /** Descripción de Campos */

    private GridBagLayout resultLayout = new GridBagLayout();

    /** Descripción de Campos */

    private JSplitPane centerPane = new JSplitPane();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        this.setIconImage( Env.getImage( "OXP16.gif" ));
        this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        titledBorder1 = new TitledBorder( BorderFactory.createEtchedBorder( Color.white,new Color( 148,145,140 )),Msg.getMsg( Env.getCtx(),"ScriptVariables" ));
        titledBorder2 = new TitledBorder( BorderFactory.createEtchedBorder( Color.white,new Color( 148,145,140 )),Msg.getMsg( Env.getCtx(),"ScriptEditor" ));
        mainPanel.setLayout( borderLayout1 );
        editor.setLineWrap( true );
        editor.setTabSize( 4 );
        editor.setWrapStyleWord( true );
        bOK.addActionListener( this );
        bCancel.addActionListener( this );
        bHelp.addActionListener( this );
        bProcess.addActionListener( this );
        variables.setBackground( Color.lightGray );
        variables.setEditable( false );
        variables.setContentType( "text/html" );
        variablesPane.setBorder( titledBorder1 );
        editorPane.setBorder( titledBorder2 );
        centerPane.setPreferredSize( new Dimension( 500,500 ));
        centerPane.setLeftComponent( editorPane );
        centerPane.setRightComponent( variablesPane );
        southPanel.setLayout( southLayout );
        resultPanel.setLayout( resultLayout );
        lResult.setText( Msg.getMsg( Env.getCtx(),"ScriptResult" ));
        fResult.setBackground( Color.lightGray );
        fResult.setEditable( false );
        fResult.setText( "" );
        northPanel.setLayout( northLayout );
        lResultVariable.setText( Msg.getMsg( Env.getCtx(),"ScriptResultVariable" ));
        fResultVariable.setBackground( Color.lightGray );
        fResultVariable.setEditable( false );
        resultVariablePanel.setLayout( resultVariableLayout );
        okPanel.setLayout( okLayout );
        northPanel.add( resultVariablePanel,BorderLayout.CENTER );
        getContentPane().add( mainPanel );
        editorPane.getViewport().add( editor,null );
        variablesPane.getViewport().add( variables,null );
        mainPanel.add( northPanel,BorderLayout.NORTH );
        mainPanel.add( southPanel,BorderLayout.SOUTH );
        southPanel.add( okPanel,BorderLayout.EAST );
        okPanel.add( bCancel,null );
        okPanel.add( bOK,null );
        southPanel.add( resultPanel,BorderLayout.CENTER );
        resultPanel.add( bProcess,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        resultPanel.add( lResult,new GridBagConstraints( 1,0,1,1,0.0,0.0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        resultPanel.add( fResult,new GridBagConstraints( 2,0,1,1,1.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        mainPanel.add( centerPane,BorderLayout.CENTER );
        resultVariablePanel.add( lResultVariable,new GridBagConstraints( 0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets( 5,5,5,5 ),0,0 ));
        resultVariablePanel.add( fResultVariable,new GridBagConstraints( 1,0,1,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets( 5,5,5,5 ),0,0 ));
        northPanel.add( helpPanel,BorderLayout.EAST );
        helpPanel.add( bHelp,null );
        centerPane.setDividerLocation( 350 );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param script
     */

    public void setScript( Scriptlet script ) {
        if( script == null ) {
            m_script = new Scriptlet( Scriptlet.VARIABLE,";",Env.getCtx(),m_WindowNo );
        } else {
            m_script = script;
        }

        //

        fResultVariable.setText( m_script.getVariable());
        m_origScript = m_script.getScript();
        editor.setText( m_script.getScript());

        //

        StringBuffer sb  = new StringBuffer( "<HTML><BODY>" );
        HashMap      ctx = m_script.getEnvironment();
        String[]     pp  = new String[ ctx.size()];

        ctx.keySet().toArray( pp );
        Arrays.sort( pp );

        for( int i = 0;i < pp.length;i++ ) {
            String key   = pp[ i ].toString();
            Object value = ctx.get( key );

            sb.append( "<font color=" ).append( '"' ).append( getColor( value )).append( '"' ).append( ">" ).append( key ).append( " (" ).append( value ).append( ")</font><br>" );
        }

        sb.append( "</BODY></HTML>" );
        variables.setText( sb.toString());
        variables.setCaretPosition( 0 );
    }    // setScript

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     */

    private String getColor( Object value ) {
        if( value instanceof String ) {
            return "#009900";    // "green";
        } else if( value instanceof Integer ) {
            return "#0000FF";    // "blue";
        } else if( value instanceof Double ) {
            return "#00FFFF";    // "cyan";
        } else if( value instanceof Timestamp ) {
            return "#FF00FF";    // "magenta";
        } else if( value instanceof Boolean ) {
            return "#FF9900";    // "orange";
        }

        return "#FF0000";    // "red";
    }                        // getColor

    /**
     * Descripción de Método
     *
     */

    private void dynInit() {}    // dynInit

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        if( e.getSource() == bOK ) {
            dispose();
        } else if( e.getSource() == bCancel ) {
            m_script.setScript( m_origScript );
            dispose();
        } else if( e.getSource() == bProcess ) {
            actionProcess();
        } else if( e.getSource() == bHelp ) {
            Help h = new Help( this,Msg.getMsg( Env.getCtx(),"ScriptHelp" ),getClass().getResource( "Script.html" ));

            h.setVisible( true );
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void actionProcess() {
        m_script.setScript( editor.getText());

        Exception e = m_script.execute();

        if( e != null ) {
            ADialog.error( m_WindowNo,this,"ScriptError",e.toString());
        }

        Object result = m_script.getResult( false );

        fResult.setText( (result == null)
                         ?""
                         :result.toString());
    }    // actionProcess

    /**
     * Descripción de Método
     *
     *
     * @param header
     * @param script
     * @param editable
     * @param WindowNo
     *
     * @return
     */

    public static String start( String header,String script,boolean editable,int WindowNo ) {
        Scriptlet scr = new Scriptlet( Scriptlet.VARIABLE,script,Env.getCtx(),WindowNo );
        ScriptEditor se = new ScriptEditor( header,scr,WindowNo );

        return scr.getScript();
    }    // start
}    // ScriptEditor



/*
 *  @(#)ScriptEditor.java   02.07.07
 * 
 *  Fin del fichero ScriptEditor.java
 *  
 *  Versión 2.2
 *
 */
