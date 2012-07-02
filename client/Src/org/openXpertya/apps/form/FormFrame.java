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

import java.awt.Cursor;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

import org.compiere.plaf.CompiereColor;
import org.compiere.swing.CCheckBox;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AGlassPane;
import org.openXpertya.apps.Help;
import org.openXpertya.model.MRole;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Trace;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public class FormFrame extends JFrame implements ActionListener {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 	lista de parametros para los formularios
	 */
	protected HashMap m_parameters=null;

	/**
     * Constructor de la clase ...
     *
     */

    public FormFrame() {
        super();
        addWindowListener( new java.awt.event.WindowAdapter() {
            public void windowOpened( java.awt.event.WindowEvent evt ) {
                formWindowOpened( evt );
            }
        } );
        m_WindowNo = Env.createWindowNo( this );
        setGlassPane( m_glassPane );

        try {
            jbInit();
            createMenu();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"FormFrame",e );
        }
        
        m_parameters=new HashMap();
    }    // FormFrame

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private AGlassPane m_glassPane = new AGlassPane();

    /** Descripción de Campos */

    private String m_Description = null;

    /** Descripción de Campos */

    private String m_Help = null;

    /** Descripción de Campos */

    private JMenuBar menuBar = new JMenuBar();

    /** Descripción de Campos */

    private FormPanel m_panel = null;

    /** Descripción de Campos */
    private CCheckBox automatico = new CCheckBox();

    public boolean m_maximize = false;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( FormFrame.class );

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        CompiereColor.setBackground( this );

        //

        this.setIconImage( org.openXpertya.OpenXpertya.getImage16());
        this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        this.setJMenuBar( menuBar );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void createMenu() {

    	MRole currentUserRole = MRole.getDefault(Env.getCtx(), true);
    	
        // File

        JMenu mFile = AEnv.getMenu( "File" );

        menuBar.add( mFile );
        AEnv.addMenuItem( "PrintScreen",null,KeyStroke.getKeyStroke( KeyEvent.VK_PRINTSCREEN,0 ),mFile,this );
        AEnv.addMenuItem( "ScreenShot",null,KeyStroke.getKeyStroke( KeyEvent.VK_PRINTSCREEN,Event.SHIFT_MASK ),mFile,this );
        AEnv.addMenuItem( "Report",null,KeyStroke.getKeyStroke( KeyEvent.VK_P,Event.ALT_MASK ),mFile,this );
        mFile.addSeparator();
        AEnv.addMenuItem( "End",null,KeyStroke.getKeyStroke( KeyEvent.VK_X,Event.ALT_MASK ),mFile,this );
        AEnv.addMenuItem( "Exit",null,KeyStroke.getKeyStroke( KeyEvent.VK_X,Event.SHIFT_MASK + Event.ALT_MASK ),mFile,this );

        // View

        JMenu mView = AEnv.getMenu( "View" );

        menuBar.add( mView );
        if (currentUserRole.isInfoProductAccess()) {
        	AEnv.addMenuItem( "InfoProduct",null,KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.CTRL_MASK ),mView,this );
        }
        AEnv.addMenuItem( "InfoBPartner",null,KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.SHIFT_MASK + Event.CTRL_MASK ),mView,this );
        AEnv.addMenuItem( "InfoAccount",null,KeyStroke.getKeyStroke( KeyEvent.VK_I,Event.ALT_MASK + Event.CTRL_MASK ),mView,this );
        mView.addSeparator();
        AEnv.addMenuItem( "InfoOrder","Info",null,mView,this );
        AEnv.addMenuItem( "InfoInvoice","Info",null,mView,this );
        AEnv.addMenuItem( "InfoInOut","Info",null,mView,this );
        AEnv.addMenuItem( "InfoPayment","Info",null,mView,this );
        AEnv.addMenuItem( "InfoSchedule","Info",null,mView,this );

        // Tools

        JMenu mTools = AEnv.getMenu( "Tools" );

        menuBar.add( mTools );
        AEnv.addMenuItem( "Calculator",null,null,mTools,this );
        AEnv.addMenuItem( "Calendar",null,null,mTools,this );
        AEnv.addMenuItem( "Editor",null,null,mTools,this );
        AEnv.addMenuItem( "Script",null,null,mTools,this );

        if(currentUserRole.isShowPreference()) {
            mTools.addSeparator();
            AEnv.addMenuItem( "Preference",null,null,mTools,this );
        }

        // Help

        JMenu mHelp = AEnv.getMenu( "Help" );

        menuBar.add( mHelp );
        AEnv.addMenuItem( "Help","Help",KeyStroke.getKeyStroke( KeyEvent.VK_F1,0 ),mHelp,this );
        AEnv.addMenuItem( "Online",null,null,mHelp,this );
        AEnv.addMenuItem( "EMailSupport",null,null,mHelp,this );
        AEnv.addMenuItem( "About",null,null,mHelp,this );
        //createMenu
        
    }    
    

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        log.config( "" );

        // recursive calls

        if( Trace.isCalledFrom( "JFrame" )) {    // [x] close window pressed
            m_panel.dispose();
        }

        m_panel = null;
        Env.clearWinContext( m_WindowNo );
        super.dispose();
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param AD_Form_ID
     *
     * @return
     */

    public boolean openForm( int AD_Form_ID ) {
        Properties ctx = Env.getCtx();

        //

        String name      = null;
        String className = null;
        String sql       = "SELECT Name, Description, ClassName, Help FROM AD_Form WHERE AD_Form_ID=?";
        boolean trl = !Env.isBaseLanguage( ctx,"AD_Form" );

        if( trl ) {
            sql = "SELECT t.Name, t.Description, f.ClassName, t.Help " + "FROM AD_Form f INNER JOIN AD_Form_Trl t" + " ON (f.AD_Form_ID=t.AD_Form_ID AND AD_Language=?)" + "WHERE f.AD_Form_ID=?";
        }

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            if( trl ) {
                pstmt.setString( 1,Env.getAD_Language( ctx ));
                pstmt.setInt( 2,AD_Form_ID );
            } else {
                pstmt.setInt( 1,AD_Form_ID );
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                name          = rs.getString( 1 );
                m_Description = rs.getString( 2 );
                className     = rs.getString( 3 );
                m_Help        = rs.getString( 4 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"open",e );
        }

        if( className == null ) {
            return false;
        }

        //

        log.info( "AD_Form_ID=" + AD_Form_ID + " - Class=" + className );
        Env.setContext( ctx,m_WindowNo,"WindowName",name );
        setTitle( Env.getHeader( ctx,m_WindowNo ));

        try {

            // Create instance w/o parameters

            m_panel = ( FormPanel )Class.forName( className ).newInstance();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"Class=" + className + ", AD_Form_ID=" + AD_Form_ID,e );

            return false;
        }

        //

        m_panel.init( m_WindowNo,this );

        return true;
    }    // openForm
    /**
	 * 	Get Form Panel
	 *	@return form panel
	 */
	public FormPanel getFormPanel()
	{
		return m_panel;
	}	//	getFormPanel

	/**
	 *  Set Form Panel
	 */
	
	
    /**
     * Descripción de Método
     *
     *
     * @return
     */
    
    //Añadido por ConSerTi
    public void setFormPanel(FormPanel fp)
	{
		m_panel = fp;		
	}
	public int getWindowNo()
	{
		return m_WindowNo;
	}
	//Fin del añadido.


    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        String cmd = e.getActionCommand();

        if( cmd.equals( "End" )) {
    		fireWindowClosing();
        } else if( cmd.equals( "Help" )) {
            actionHelp();
        } else if( !AEnv.actionPerformed( cmd,m_WindowNo,this )) {
            log.log( Level.SEVERE,"Not handeled=" + cmd );
        }
    }    // actionPerformed

    
    protected void fireWindowClosing(){
    	for (WindowListener listener : getWindowListeners()) {
			listener.windowClosing(null);
		}
    }
    
    /**
     * Descripción de Método
     *
     */

    private void actionHelp() {
        StringBuffer sb = new StringBuffer();

        sb.append( "<h2>" ).append( m_Description ).append( "</h2><p>" ).append( m_Help );

        Help hlp = new Help( Env.getFrame( this ),this.getTitle(),sb.toString());

        hlp.setVisible(true);
    }    // actionHelp

    /**
     * Descripción de Método
     *
     *
     * @param busy
     */

    public void setBusy( boolean busy ) {
        if( busy == m_glassPane.isVisible()) {
            return;
        }

        log.info( "Busy=" + busy );

        if( busy ) {
            setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ));
        } else {
            setCursor( Cursor.getDefaultCursor());
        }

        m_glassPane.setMessage( null );
        m_glassPane.setVisible( busy );
        m_glassPane.requestFocus();
    }    // setBusy

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     */

    public void setBusyMessage( String AD_Message ) {
        m_glassPane.setMessage( AD_Message );
    }    // setBusyMessage

    /**
     * Descripción de Método
     *
     *
     * @param time
     */

    public void setBusyTimer( int time ) {
        m_glassPane.setBusyTimer( time );
    }    // setBusyTimer

    /**
     * Descripción de Método
     *
     *
     * @param max
     */

    public void setMaximize( boolean max ) {
        m_maximize = max;
    }    // setMaximize

    /**
     * Descripción de Método
     *
     *
     * @param evt
     */

    private void formWindowOpened( java.awt.event.WindowEvent evt ) {
        if( m_maximize == true ) {
            super.setVisible(true);
            super.setExtendedState( JFrame.MAXIMIZED_BOTH );
        }
    }    // formWindowOpened

    /**
     * Descripción de Método
     *
     *
     * @param process
     *
     * @return
     */

    public Thread startBatch( final Runnable process ) {
        Thread worker = new Thread() {
            public void run() {
                setBusy( true );
                process.run();
                setBusy( false );
            }
        };

        worker.start();

        return worker;
    }    // startBatch
    
    
    /** INDEOS **/
    
    /**
     * 	Guarda un parametro para el formulario
     * 
     *	@param key	el parámetro a guardar
     *	@param value	el valor del parametro
     */
    public void storeParameter(Object key, Object value)
	{
		m_parameters.put(key, value);
	}	// storeParameter
	
    /**
     * 	Obtiene el valor de un parametro
     * 
     * 	@param key	parametro que estamos buscando
     * 
     * 	@return	el valor del parametro, en caso de que esté guardado en la lista de parametros o null si
     * 			no está guardado
     */
	public Object getParameter(Object key)
	{
		return m_parameters.get(key);
	}	// getParameter
    
}    // FormFrame



/*
 *  @(#)FormFrame.java   02.07.07
 *
 *  Fin del fichero FormFrame.java
 *
 *  Versión 2.2
 *
 */
