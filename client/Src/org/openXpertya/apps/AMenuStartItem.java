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

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import javax.swing.SwingUtilities;

import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.MeasurableTask;
import org.openXpertya.util.TimeStatsLogger;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class AMenuStartItem extends Thread implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ID
     * @param isMenu
     * @param name
     * @param menu
     */

    public AMenuStartItem( int ID,boolean isMenu,String name,AMenu menu ) {
        m_ID     = ID;
        m_isMenu = isMenu;
        m_name   = name;
        m_menu   = menu;

        if( menu != null ) {
            m_increment = ( menu.progressBar.getMaximum() - menu.progressBar.getMinimum()) / 5;
        }
    }    // UpdateProgress

    /** Descripción de Campos */

    private int m_ID = 0;

    /** Descripción de Campos */

    private boolean m_isMenu = false;

    /** Descripción de Campos */

    private String m_name;

    /** Descripción de Campos */

    private AMenu m_menu;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AMenuStartItem.class );

    // Reset Progress Bar

    /** Descripción de Campos */

    private Runnable m_resetPB = new Runnable() {
        public void run() {
            runResetPB();
        }
    };

    // Progress Bar tick

    /** Descripción de Campos */

    private Runnable m_tickPB = new Runnable() {
        public void run() {
            runTickPB();
        }
    };

    // Progress Bar max state

    /** Descripción de Campos */

    private Runnable m_updatePB = new Runnable() {
        public void run() {
        	runUpdatePB();
        }
    };

    protected void runUpdatePB() {
        if( m_menu == null ) {
            return;
        }

        m_value += m_increment;

        if( m_menu.progressBar.getValue() > m_value ) {    // max value
            m_menu.progressBar.setValue( m_value );
            m_menu.progressBar.repaint();
        }
    }
    
    protected void runTickPB() {
    	if( m_menu == null ) {
            return;
        }

        // 100/5 => 20 ticks - every .5 sec => 10 seconds loadtime

        final int tick = 5;

        if( m_menu.progressBar.getValue() < ( m_menu.progressBar.getMaximum() - tick )) {
            m_menu.progressBar.setValue( m_menu.progressBar.getValue() + tick );
            m_menu.progressBar.repaint();
        }
    }
    
    protected void runResetPB() {
    	m_value = 0;

        if( m_menu != null ) {
            m_menu.progressBar.setValue( 0 );
        }
    }
    
    /** Descripción de Campos */

    private int m_value = 0;

    /** Descripción de Campos */

    private int m_increment = 20;

    /** Descripción de Campos */

    private javax.swing.Timer m_timer = new javax.swing.Timer( 500,this );    // every 1/2 second

    /**
     * Descripción de Método
     *
     */

    public void run() {
        if( m_menu != null ) {
            m_menu.setBusy( true );
        }

        SwingUtilities.invokeLater( m_resetPB );
        m_timer.start();
        SwingUtilities.invokeLater( m_updatePB );

        try {
            String SQL = "SELECT * FROM AD_Menu WHERE AD_Menu_ID=?";
            log.fine("AmenuStarItem 1");

            if( !m_isMenu ) {
            	log.fine("AmenuStarIten 1 y medio");
                SQL = "SELECT * FROM AD_WF_Node WHERE AD_WF_Node_ID=?";
            }
            log.fine("AmenuStarItem 2");
            PreparedStatement pstmt = DB.prepareStatement( SQL );
            log.fine("AmenuStarItem 3");
            pstmt.setInt( 1,m_ID );
            log.fine("AmenuStarItem 4");

            ResultSet rs = pstmt.executeQuery();
            log.fine("AmenuStarItem 5");

            SwingUtilities.invokeLater( m_updatePB );
            log.fine("AmenuStarItem 6");

            if( rs.next())                                                  // should only be one
            {
            	log.fine("AmenuStarItem 7");
                String Action  = rs.getString( "Action" );
                log.fine("AmenuStarItem 8");
                String IsSOTrx = "Y";
                log.fine("AmenuStarItem 9");

                if( m_isMenu ) {
                    IsSOTrx = rs.getString( "IsSOTrx" );
                }
                log.fine("AmenuStarItem 10");
                int cmd;
// ---> Start Window
                if( Action.equals( "W" ))                                   // Window
                {
                	log.fine("AmenuStarItem 11");
                    cmd = rs.getInt( "AD_Window_ID" );
                    log.fine("Vamos a StarWindow con 0 y cmd= "+cmd);
                    startWindow( 0,cmd );
                } else if( Action.equals( "P" ) || Action.equals( "R" ))    // Process & Report
                {
                	log.fine("AmenuStarItem 12");
                    cmd = rs.getInt( "AD_Process_ID" );
                    startProcess( cmd,IsSOTrx );
                } else if( Action.equals( "B" ))    // Workbench Abro
                {
                	log.fine("AmenuStarItem 13");
                    cmd = rs.getInt( "AD_Workbench_ID" );
//---> Start Window
                    startWindow( cmd,0 );
                } else if( Action.equals( "F" ))    // WorkFlow
                {
                	log.fine("AmenuStarItem 14");
                    if( m_isMenu ) {
                    	log.fine("AmenuStarItem 15");
                        cmd = rs.getInt( "AD_Workflow_ID" );
                    } else {
                    	log.fine("AmenuStarItem 16");
                        cmd = rs.getInt( "Workflow_ID" );
                    }
                    log.fine("AmenuStarItem 17");
                    if( m_menu != null ) {
                    	log.fine("AmenuStarItem 18");
                        m_menu.startWorkFlow( cmd );
                    }
                } else if( Action.equals( "T" ))    // Task
                {
                	log.fine("AmenuStarItem 19");
                    cmd = rs.getInt( "AD_Task_ID" );
                    startTask( cmd );
                } else if( Action.equals( "X" ))    // Form
                {
                	log.fine("AmenuStarItem 20");
                    cmd = rs.getInt( "AD_Form_ID" );
                    // Si es el formulario de TPV se inicia el timer para medición de tiempo
                    if (cmd == 113) {
                    	TimeStatsLogger.beginTask(MeasurableTask.POS_INIT);
                    }
                    startForm( cmd );
                } else {
                    log.log( Level.SEVERE,"AMenuStartItem.run - No valid Action in ID=" + m_ID );
                }
            }    // for all records
            log.fine("AmenuStarItem 21");
            SwingUtilities.invokeLater( m_updatePB );
            log.fine("AmenuStarItem 22");
            rs.close();
            pstmt.close();
        } catch( Exception e ) {
            log.log( Level.SEVERE,"AMenuStartItem.run ID==" + m_ID,e );
            ADialog.error( 0,null,"Error " + e.toString(),Msg.parseTranslation( Env.getCtx(),e.getMessage()));

        }

        try {
            Thread.sleep( 1000 );
        }    // 1 sec
                catch( InterruptedException ie ) {
        }

        // ready for next

        m_timer.stop();
        SwingUtilities.invokeLater( m_resetPB );

        if( m_menu != null ) {
            m_menu.updateInfo();
            m_menu.setBusy( false );
        }
    }    // run

    /**
     * Descripción de Método
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
    	//log.log(Level.SEVERE, e.toString());
    	
        SwingUtilities.invokeLater( m_tickPB );
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param AD_Workbench_ID
     * @param AD_Window_ID
     */

    private void startWindow( int AD_Workbench_ID,int AD_Window_ID ) {
    	log.config("StartWindow con AD_workbench_id= "+AD_Workbench_ID+ " y AD_Window_ID= "+AD_Window_ID);
    	
    	// SwingUtilities.invokeLater( new WindowStarter(AD_Workbench_ID, AD_Window_ID) );
    	
    	SwingUtilities.invokeLater( m_updatePB );

        final AWindow frame = new AWindow( (Frame)m_menu.getRootPane().getParent() );
        boolean OK    = false;

        if( AD_Workbench_ID != 0 ) {
            OK = frame.initWorkbench( AD_Workbench_ID );
        } else {
        	OK = frame.initWindow( AD_Window_ID,null );    // No Query Value
        }

        if( !OK )
            return;

        SwingUtilities.invokeLater( new Runnable() {
        	public void run() {
        		runUpdatePB();
        		frame.pack();
        	}
        });    // 2
        
        // Center the window

        SwingUtilities.invokeLater( new Runnable() {
        	public void run() {
		        runUpdatePB();    // 3
		        AEnv.showCenterScreen( frame );
        	}
        });
        
    }    // startWindow

    /**
     * Descripción de Método
     *
     *
     * @param AD_Process_ID
     * @param IsSOTrx
     */
    
	private class ProcessStarter implements Runnable {
		
		public ProcessStarter(int AD_Process_ID, String IsSOTrx) {
			this.AD_Process_ID = AD_Process_ID;
			this.IsSOTrx = IsSOTrx;
		}
		
		private int AD_Process_ID;
		private String IsSOTrx;
		
		public void run() {
			runUpdatePB();
			
	        boolean isSO = (IsSOTrx != null) && IsSOTrx.equals( "Y" );

	        m_timer.stop();

	        ProcessDialog pd = new ProcessDialog( AD_Process_ID,isSO );

	        if( !pd.init()) {
	            return;
	        }

	        m_timer.start();

	        runUpdatePB();
	        pd.pack();
	        
	        runUpdatePB();
	        
	        if(!pd.isHasParams()){
	        	AEnv.showCenterScreen( pd );
	        }
		}
	}
	
   private class FormStarter implements Runnable {
    	
    	private int AD_Form_ID;
    	
    	public FormStarter(int AD_Form_ID) {
    		this.AD_Form_ID = AD_Form_ID;
    	}
    	
    	public void run() {
    		FormFrame ff = new FormFrame();

            runUpdatePB();
            ff.openForm( AD_Form_ID );
            
            runUpdatePB();
            ff.pack();

            // Center the window

            runUpdatePB();
            AEnv.showCenterScreen( ff );
    	}
    }
    
    private class TaskStarter implements Runnable {
    	public TaskStarter(String title, String command) {
    		this.title = title;
    		this.command = command;
    	}
    	
    	private String title;
    	private String command;
    	
    	public void run() {
    		ATask.start( title, command );
    	}
    }
    
    private void startProcess( int AD_Process_ID,String IsSOTrx ) {
    	SwingUtilities.invokeLater( new ProcessStarter(AD_Process_ID, IsSOTrx) );
    }

    /**
     * Descripción de Método
     *
     *
     * @param AD_Task_ID
     */

    private void startTask( int AD_Task_ID ) {
        SwingUtilities.invokeLater( m_updatePB );    // 1

        // Get Command

        String command = null;
        String SQL     = "SELECT OS_Command FROM AD_Task WHERE AD_Task_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( SQL );

            pstmt.setInt( 1,AD_Task_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                command = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"AMenuStartItem.startTask",e );
        }

        if( command == null ) {
            return;
        }

        SwingUtilities.invokeLater( m_updatePB );    // 2
        
        SwingUtilities.invokeLater( new TaskStarter( m_name, command ) );
    }                                                // startTask

 
    
    /**
     * Descripción de Método
     *
     *
     * @param AD_Form_ID
     */

    private void startForm( int AD_Form_ID ) {
		SwingUtilities.invokeLater( new FormStarter(AD_Form_ID) );
    }                                                // startForm
}    // StartItem



/*
 *  @(#)AMenuStartItem.java   02.07.07
 * 
 *  Fin del fichero AMenuStartItem.java
 *  
 *  Versión 2.2
 *
 */
