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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import org.compiere.swing.CButton;
import org.openXpertya.db.CConnection;
import org.openXpertya.interfaces.Server;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MWindowVO;
import org.openXpertya.util.CLogMgt;
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

public final class AEnv {

    /**
     * Descripción de Método
     *
     *
     * @param window
     */

    public static void showCenterScreen( Window window ) {
        positionCenterScreen( window );
        window.setVisible( true );
        window.toFront();
        log.info( "Aparicion de la ventana" );
    }    // showCenterScreen

    /**
     * Descripción de Método
     *
     *
     * @param window
     */

    public static void positionCenterScreen( Window window ) {
        positionScreen( window,SwingConstants.CENTER);
    }    // positionCenterScreen

    /**
     * Descripción de Método
     *
     *
     * @param window
     * @param position
     */

    public static void showScreen( Window window,int position ) {
        positionScreen( window,position );
        window.setVisible( true );
        window.toFront();
    }    // showScreen

    /**
     * Descripción de Método
     *
     *
     * @param window
     * @param position
     */

    public static void positionScreen( Window window,int position ) {
        window.pack();

        Dimension sSize     = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wSize     = window.getSize();
        int       maxWidth  = ( int )( sSize.width * .97 );
        int       maxHeight = ( int )( sSize.height * .97 );

        // fit on window

        if( wSize.height > maxHeight ) {
            wSize.height = maxHeight;
        }

        if( wSize.width > maxWidth ) {
            wSize.width = maxWidth;
        }

        window.setSize( wSize );

        // Center

        int x = ( sSize.width - wSize.width ) / 2;
        int y = ( sSize.height - wSize.height ) / 2;

        if( position == SwingConstants.CENTER ) {
            ;
        } else if( position == SwingConstants.NORTH_WEST ) {
            x = 0;
            y = 0;
        } else if( position == SwingConstants.NORTH ) {
            y = 0;
        } else if( position == SwingConstants.NORTH_EAST ) {
            x = ( sSize.width - wSize.width );
            y = 0;
        } else if( position == SwingConstants.WEST ) {
            x = 0;
        } else if( position == SwingConstants.EAST ) {
            x = ( sSize.width - wSize.width );
        } else if( position == SwingConstants.SOUTH ) {
            y = ( sSize.height - wSize.height );
        } else if( position == SwingConstants.SOUTH_WEST ) {
            x = 0;
            y = ( sSize.height - wSize.height );
        } else if( position == SwingConstants.SOUTH_EAST ) {
            x = ( sSize.width - wSize.width );
            y = ( sSize.height - wSize.height );
        }

        //

        window.setLocation( x,y );
    }    // positionScreen

    /**
     * Descripción de Método
     *
     *
     * @param parent
     * @param window
     */

    public static void showCenterWindow( Window parent,Window window ) {
        
    	positionCenterWindow( parent,window );
       
        window.setVisible( true );
        window.toFront();
    }    // showCenterWindow

    /**
     * Descripción de Método
     *
     *
     * @param parent
     * @param window
     */

    public static void positionCenterWindow( Window parent,Window window ) {
        if( parent == null ) {
            positionCenterScreen( window );

            return;
        }

        window.pack();

        //

        Dimension sSize     = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension wSize     = window.getSize();
        int       maxWidth  = ( int )( sSize.width * .97 );
        int       maxHeight = ( int )( sSize.height * .97 );

        // fit on window

        if( wSize.height > maxHeight ) {
            wSize.height = maxHeight;
        }

        if( wSize.width > maxWidth ) {
            wSize.width = maxWidth;
        }

        window.setSize( wSize );

        // center in parent

        Rectangle pBounds = parent.getBounds();

        // Parent is in upper left corner

        if( (pBounds.x == pBounds.y) && (pBounds.x == 0) ) {
            positionCenterScreen( window );

            return;
        }

        // Find middle

        int x = pBounds.x + (( pBounds.width - wSize.width ) / 2 );

        if( x < 0 ) {
            x = 0;
        }

        int y = pBounds.y + (( pBounds.height - wSize.height ) / 2 );

        if( y < 0 ) {
            y = 0;
        }

        // Is it on Screen?

        if( x + wSize.width > sSize.width ) {
            x = sSize.width - wSize.width;
        }

        if( y + wSize.height > sSize.height ) {
            y = sSize.height - wSize.height;
        }

        //
        // System.out.println("Position: x=" + x + " y=" + y + " w=" + wSize.getWidth() + " h=" + wSize.getHeight()
        // + " - Parent loc x=" + pLoc.x + " y=" + y + " w=" + pSize.getWidth() + " h=" + pSize.getHeight());

        window.setLocation( x,y );
    }    // positionCenterScreen

    /**
     * Descripción de Método
     *
     *
     * @param iconName
     *
     * @return
     */

    public static CButton getButton( String iconName ) {
        CButton button = new CButton( Env.getImageIcon( iconName + "16.gif" ));

        button.setMargin( new Insets( 0,0,0,0 ));
        button.setToolTipText( Msg.getMsg( Env.getCtx(),iconName ));
        button.setDefaultCapable( false );

        return button;
    }    // getButton

    /**
     * Descripción de Método
     *
     *
     * @param AD_Message
     *
     * @return
     */

    public static JMenu getMenu( String AD_Message ) {
        JMenu  menu = new JMenu();
        String text = Msg.getMsg( Env.getCtx(),AD_Message );
        int    pos  = text.indexOf( "&" );

        if( pos != -1 )    // We have a nemonic
        {
            char ch = text.charAt( pos + 1 );

            text = text.substring( 0,pos ) + text.substring( pos + 1 );
            menu.setMnemonic( ch );
        }

        menu.setText( text );

        return menu;
    }    // getMenu

    /**
     * Descripción de Método
     *
     *
     * @param action
     * @param iconName
     * @param ks
     * @param menu
     * @param al
     *
     * @return
     */

    public static JMenuItem addMenuItem( String action,String iconName,KeyStroke ks,JMenu menu,ActionListener al ) {
        if( iconName == null ) {
            iconName = action;
        }

        JMenuItem mi = new JMenuItem( Msg.getMsg( Env.getCtx(),action ),Env.getImageIcon( iconName + "16.gif" ));

        mi.setActionCommand( action );

        if( ks != null ) {
            mi.setAccelerator( ks );
        }

        if( menu != null ) {
            menu.add( mi );
        }

        if( al != null ) {
            mi.addActionListener( al );
        }

        return mi;
    }    // addMeniItem

    /**
     * Descripción de Método
     *
     *
     * @param actionCommand
     * @param WindowNo
     * @param c
     *
     * @return
     */

    public static boolean actionPerformed( String actionCommand,int WindowNo,Container c ) {
        MRole role = MRole.getDefault();

        // File Menu   ------------------------

        if( actionCommand.equals( "PrintScreen" )) {
            PrintScreenPainter.printScreen( Env.getFrame( c ));
        } else if( actionCommand.equals( "ScreenShot" )) {
            ScreenShot.createJPEG( Env.getFrame( c ),null );
        }

        // else if (actionCommand.equals("Report"))
        // {
        // AEnv.showCenterScreen (new ProcessStart());
        // }

        else if( actionCommand.equals( "Exit" )) {
            if( ADialog.ask( WindowNo,c,"ExitApplication?" )) {
                Env.exitEnv( 0 );
            }
        }else if (actionCommand.equals("Logout")) // dREHER
		{
			org.openXpertya.apps.AMenu aMenu = (org.openXpertya.apps.AMenu)Env.getWindow(0); 
			aMenu.logout();
		}

        // View Menu   ------------------------

        else if( actionCommand.equals( "InfoProduct" )) {
            org.openXpertya.apps.search.Info.showProduct( Env.getFrame( c ),WindowNo );
        } else if( actionCommand.equals( "InfoBPartner" )) {
            org.openXpertya.apps.search.Info.showBPartner( Env.getFrame( c ),WindowNo );
        } else if( actionCommand.equals( "InfoAsset" )) {
            org.openXpertya.apps.search.Info.showAsset( Env.getFrame( c ),WindowNo );
        } else if( actionCommand.equals( "InfoAccount" ) && MRole.getDefault().isShowAcct()) {
            new org.openXpertya.acct.AcctViewer();
        } else if( actionCommand.equals( "InfoSchedule" )) {
            new org.openXpertya.apps.search.InfoSchedule( Env.getFrame( c ),null,false );
        } else if( actionCommand.equals( "InfoOrder" )) {
            org.openXpertya.apps.search.Info.showOrder( Env.getFrame( c ),WindowNo,"" );
        } else if( actionCommand.equals( "InfoInvoice" )) {
            org.openXpertya.apps.search.Info.showInvoice( Env.getFrame( c ),WindowNo,"" );
        } else if( actionCommand.equals( "InfoInOut" )) {
            org.openXpertya.apps.search.Info.showInOut( Env.getFrame( c ),WindowNo,"" );
        } else if( actionCommand.equals( "InfoPayment" )) {
            org.openXpertya.apps.search.Info.showPayment( Env.getFrame( c ),WindowNo,"" );
        } else if( actionCommand.equals( "InfoCashLine" )) {
            org.openXpertya.apps.search.Info.showCashLine( Env.getFrame( c ),WindowNo,"" );
        } else if( actionCommand.equals( "InfoAssignment" )) {
            org.openXpertya.apps.search.Info.showAssignment( Env.getFrame( c ),WindowNo,"" );
        }

        // Go Menu     ------------------------

        else if( actionCommand.equals( "WorkFlow" )) {
            startWorkflowProcess( 0,0 );
        } else if( actionCommand.equals( "Home" )) {
            Env.getWindow( 0 ).toFront();
        }

        // Tools Menu  ------------------------

        else if( actionCommand.equals( "Calculator" )) {
            AEnv.showCenterScreen( new org.openXpertya.grid.ed.Calculator( Env.getFrame( c )));
        } else if( actionCommand.equals( "Calendar" )) {
            AEnv.showCenterScreen( new org.openXpertya.grid.ed.Calendar( Env.getFrame( c )));
        } else if( actionCommand.equals( "Editor" )) {
            AEnv.showCenterScreen( new org.openXpertya.grid.ed.Editor( Env.getFrame( c )));
        } else if( actionCommand.equals( "Script" )) {
            new ScriptEditor();
        } else if( actionCommand.equals( "Preference" )) {
            if( role.isShowPreference()) {
                AEnv.showCenterScreen( new Preference( Env.getFrame( c ),WindowNo ));
            }
        }

        // Help Menu   ------------------------

        else if( actionCommand.equals( "Online" )) {
            Env.startBrowser( org.openXpertya.OpenXpertya.getURL());
        } else if( actionCommand.equals( "EMailSupport" )) {
            ADialog.createSupportEMail( Env.getFrame( c ),Env.getFrame( c ).getTitle(),"\n\n" );
        } else if( actionCommand.equals( "About" )) {
            AEnv.showCenterScreen( new AboutBox( Env.getFrame( c )));
        } else {
            return false;
        }

        //

        return true;
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param b
     * @param text
     */

    public static void setTextMnemonic( JButton b,String text ) {
        if( (text == null) || (b == null) ) {
            return;
        }

        int pos = text.indexOf( "&" );

        if( pos != -1 )    // We have a nemonic
        {
            char ch = text.charAt( pos + 1 );

            b.setMnemonic( ch );
            b.setText( text.substring( 0,pos ) + text.substring( pos + 1 ));
        }

        b.setText( text );
    }    // setTextMnemonic

    /**
     * Descripción de Método
     *
     *
     * @param text
     *
     * @return
     */

    public static char getMnemonic( String text ) {
        int pos = text.indexOf( "&" );

        if( pos != -1 ) {    // We have a nemonic
            return text.charAt( pos + 1 );
        }

        return 0;
    }    // getMnemonic

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param Record_ID
     */

    public static void zoom( int AD_Table_ID,int Record_ID ) {
        String TableName    = null;
        int    AD_Window_ID = 0;
        int    PO_Window_ID = 0;
        String sql          = "SELECT TableName, AD_Window_ID, PO_Window_ID FROM AD_Table WHERE AD_Table_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                TableName    = rs.getString( 1 );
                AD_Window_ID = rs.getInt( 2 );
                PO_Window_ID = rs.getInt( 3 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        // Nothing to Zoom to

        if( (TableName == null) || (AD_Window_ID == 0) ) {
            return;
        }

        // PO Zoom ?

        boolean isSOTrx = true;

        if( PO_Window_ID != 0 ) {
            String whereClause = TableName + "_ID=" + Record_ID;

            isSOTrx = DB.isSOTrx( TableName,whereClause );

            if( !isSOTrx ) {
                AD_Window_ID = PO_Window_ID;
            }
        }

        log.info( "Zoom to - " + TableName + " - Record_ID=" + Record_ID + " (IsSOTrx=" + isSOTrx + ")" );

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,MQuery.getEqualQuery( TableName + "_ID",Record_ID ))) {
            return;
        }

        AEnv.showCenterScreen( frame );
        frame = null;
    }    // zoom

    // zoom con detalle a�adido para abrir ventanas con la lupa e ir directo al detalle
    // a�adido por ConSerTi

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param Record_ID
     * @param IsSOTrx
     */

    public static void zoomDetallado( int AD_Table_ID,int Record_ID,boolean IsSOTrx ) {
        String TableName    = null;
        int    AD_Window_ID = 0;
        String sql          = "SELECT TableName, AD_Window_ID, PO_Window_ID FROM AD_Table WHERE AD_Table_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                TableName    = rs.getString( 1 );
                AD_Window_ID = rs.getInt( 2 );

                if( !IsSOTrx && (rs.getInt( 3 ) != 0) ) {    // Point to PO Window
                    AD_Window_ID = rs.getInt( 3 );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"AEnv.zoom",e );
        }

        // Nothing to Zoom to

        if( (TableName == null) || (AD_Window_ID == 0) ) {
            return;
        }

        log.info( "Zoom to - " + TableName + " - Record_ID=" + Record_ID + " (IsSOTrx=" + IsSOTrx + ")" );

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,MQuery.getEqualQuery( TableName + "_ID",Record_ID ))) {
            return;
        }

        frame.irADetalle();
        AEnv.showCenterScreen( frame );
        frame = null;
    }    // zoom con detalle a�adido por ConSerTi para lupa

    // zoom con detalle a�adido para abrir ventanas con la lupa e ir directo al detalle
    // a�adido por ConSerTi

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param Record_ID
     * @param tabla_hijo
     * @param id_hijo
     * @param IsSOTrx
     */

    public static void zoomLinea( int AD_Table_ID,int Record_ID,String tabla_hijo,int id_hijo,boolean IsSOTrx ) {
        String TableName    = null;
        int    AD_Window_ID = 0;
        String sql          = "SELECT TableName, AD_Window_ID, PO_Window_ID FROM AD_Table WHERE AD_Table_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                TableName    = rs.getString( 1 );
                AD_Window_ID = rs.getInt( 2 );

                if( !IsSOTrx && (rs.getInt( 3 ) != 0) ) {    // Point to PO Window
                    AD_Window_ID = rs.getInt( 3 );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"AEnv.zoom",e );
        }

        // Nothing to Zoom to

        if( (TableName == null) || (AD_Window_ID == 0) ) {
            return;
        }

        log.finer( "Zoom to - " + TableName + " - Record_ID=" + Record_ID + " (IsSOTrx=" + IsSOTrx + ")" );

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,MQuery.getEqualQuery( TableName + "_ID",Record_ID ))) {
            return;
        }

        frame.irATablaRegistro( tabla_hijo,id_hijo );
        AEnv.showCenterScreen( frame );
        frame = null;
    }    // zoom con detalle a�adido por ConSerTi para lupa de las ventanas con doble tabla

    // metodo para abrir ventanas de ConSerTi para abrir ventanas restringidas por
    // una consulta a medida

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param query
     * @param IsSOTrx
     */

    public static void abrirVentana( int AD_Table_ID,MQuery query,boolean IsSOTrx ) {
        String TableName    = null;
        int    AD_Window_ID = 0;
        String sql          = "SELECT TableName, AD_Window_ID, PO_Window_ID FROM AD_Table WHERE AD_Table_ID=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setInt( 1,AD_Table_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                TableName    = rs.getString( 1 );
                AD_Window_ID = rs.getInt( 2 );

                if( !IsSOTrx && (rs.getInt( 3 ) != 0) ) {    // Point to PO Window
                    AD_Window_ID = rs.getInt( 3 );
                }
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"AEnv.abrirVentanaRestringida",e );
        }

        // No hay tabla con ventana

        if( (TableName == null) || (AD_Window_ID == 0) ) {
            return;
        }

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,query )) {
            return;
        }

        AEnv.showCenterScreen( frame );
        frame = null;
    }    // zoom con detalle añadido por ConSerTi para lupa

    // metodo para abrir ventanas de ConSerTi para abrir ventanas restringidas por
    // una consulta a medida e indicadas por su identificador en vez de por su tabla

    /**
     * Descripción de Método
     *
     *
     * @param AD_Window_ID
     * @param query
     * @param IsSOTrx
     */

    public static void openWindow( int AD_Window_ID,MQuery query,boolean IsSOTrx ) {
        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,query )) {
            return;
        }

        AEnv.showCenterScreen( frame );
        frame = null;
    }    // zoom con detalle a�adido por ConSerTi para lupa

    /**
     * Descripción de Método
     *
     *
     * @param query
     */

    public static void zoom( MQuery query ) {
        if( (query == null) || (query.getTableName() == null) || (query.getTableName().length() == 0) ) {
            return;
        }

        String TableName    = query.getTableName();
        int    AD_Window_ID = 0;
        int    PO_Window_ID = 0;
        String sql          = "SELECT AD_Window_ID, PO_Window_ID FROM AD_Table WHERE TableName=?";

        try {
            PreparedStatement pstmt = DB.prepareStatement( sql );

            pstmt.setString( 1,TableName );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                AD_Window_ID = rs.getInt( 1 );
                PO_Window_ID = rs.getInt( 2 );
            }

            rs.close();
            pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,sql,e );
        }

        // Nothing to Zoom to

        if( AD_Window_ID == 0 ) {
            return;
        }

        // PO Zoom ?

        boolean isSOTrx = true;

        if( PO_Window_ID != 0 ) {
            isSOTrx = DB.isSOTrx( TableName,query.getWhereClause( false ));

            if( !isSOTrx ) {
                AD_Window_ID = PO_Window_ID;
            }
        }

        log.info( "Zoom to - " + query + " (IsSOTrx=" + isSOTrx + ")" );

        AWindow frame = new AWindow();

        if( !frame.initWindow( AD_Window_ID,query )) {
            return;
        }

        try
        {
	        // Intentar acceder a la pestaña en cuestion en caso de que se esté
        	// referenciando a un dato que no se encuentra en la primer pestaña
	    	String tableName = TableName;
	    	String whereClause = query.getWhereClause(false); 
	    	Integer recordID = Integer.parseInt(whereClause.substring(whereClause.indexOf(MQuery.EQUAL)+1));
	    	frame.irATablaRegistro( tableName, recordID);
        }
        catch (Exception e) { e.printStackTrace(); }

        AEnv.showCenterScreen( frame );
        frame = null;
    }    // zoom

    /**
     * Descripción de Método
     *
     *
     * @param status
     */

    public static void exit( int status ) {
        if( s_server != null ) {
            try {
                s_server.remove();
            } catch( Exception ex ) {
            }
        }

        Env.exitEnv( status );
    }    // exit
    
 // dREHER, metodo para desloguearse sin cerrar la aplicacion
    public static void logout() 
	{
		if (s_server != null)
		{
			try
			{
				s_server = null; // .remove();
			}
			catch (Exception ex)
			{
			}
		}
		Env.logout();
		
		// Splash.getSplash().setVisible(true);

		//reload
		new AMenu();
	}

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isWorkflowProcess() {
        if( s_workflow == null ) {
            s_workflow = Boolean.FALSE;

            int AD_Table_ID = 645;                                             // AD_WF_Process

            if( MRole.getDefault().isTableAccess( AD_Table_ID,true )) {        // RO
                s_workflow = Boolean.TRUE;
            } else {
                AD_Table_ID = 644;                                             // AD_WF_Activity

                if( MRole.getDefault().isTableAccess( AD_Table_ID,true )) {    // RO
                    s_workflow = Boolean.TRUE;
                } else {
                    log.config( "AEnv.isWorkflowProcess - " + s_workflow );
                }
            }

            // Get Window

            if( s_workflow.booleanValue()) {
                s_workflow_Window_ID = DB.getSQLValue( null,"SELECT AD_Window_ID FROM AD_Table WHERE AD_Table_ID=?",AD_Table_ID );

                if( s_workflow_Window_ID == 0 ) {
                    s_workflow_Window_ID = 297;    // fallback HARDCODED
                }

                // s_workflow = Boolean.FALSE;

                log.config( "AEnv.isWorkflowProcess - " + s_workflow + ",Window=" + s_workflow_Window_ID );
            }
        }

        return s_workflow.booleanValue();
    }    // isWorkflowProcess

    /**
     * Descripción de Método
     *
     *
     * @param AD_Table_ID
     * @param Record_ID
     */

    public static void startWorkflowProcess( int AD_Table_ID,int Record_ID ) {
        if( s_workflow_Window_ID == 0 ) {
            return;
        }

        //

        MQuery query = null;

        if( (AD_Table_ID != 0) && (Record_ID != 0) ) {
            query = new MQuery( "AD_WF_Process" );
            query.addRestriction( "AD_Table_ID",MQuery.EQUAL,AD_Table_ID );
            query.addRestriction( "Record_ID",MQuery.EQUAL,Record_ID );
        }

        //

        AWindow frame = new AWindow();

        if( !frame.initWindow( s_workflow_Window_ID,query )) {
            return;
        }

        AEnv.showCenterScreen( frame );
        frame = null;
    }    // startWorkflowProcess

    /** Descripción de Campos */

    private static Boolean s_workflow = null;

    /** Descripción de Campos */

    private static int s_workflow_Window_ID = 0;

    /** Descripción de Campos */

    private static int s_serverTries = 0;

    /** Descripción de Campos */

    private static Server s_server = null;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( AEnv.class );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static boolean isServerActive() {
        boolean ok = CConnection.get().isAppsServerOK( false );

        if( ok ) {
            s_serverTries = 0;

            return true;
        }

        if( s_serverTries > 1 ) {    // try twice
            return false;
        }

        // Try to connect

        CLogMgt.enable( false );

        try {
            s_serverTries++;
            log.config( "AEnv.isServerActive - try #" + s_serverTries );
            ok = CConnection.get().isAppsServerOK( true );
        } catch( Exception ex ) {
            ok       = false;
            s_server = null;
        }

        CLogMgt.enable( true );

        //

        return ok;
    }    // isServerActive

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public static String getServerVersion() {
        return CConnection.get().getServerVersion();
    }    // getServerVersion

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param AD_Window_ID
     * @param AD_Menu_ID
     *
     * @return
     */

    public static MWindowVO getMWindowVO( int WindowNo,int AD_Window_ID,int AD_Menu_ID ) {
        log.config( "Window=" + WindowNo + ", AD_Window_ID=" + AD_Window_ID );

        MWindowVO mWindowVO = null;

        // try to get from Server when enabled

        if( DB.isRemoteObjects() && isServerActive()) {
            log.config( "trying server" );

            try {
                Server server = CConnection.get().getServer();

                if( server != null ) {
                    mWindowVO = server.getWindowVO( Env.getCtx(),WindowNo,AD_Window_ID,AD_Menu_ID );
                    log.config( "from Server: success" );
                }
            } catch( RemoteException e ) {
                log.log( Level.SEVERE,"(RE)",e );
                mWindowVO = null;
                s_server  = null;
            } catch( Exception e ) {
                Throwable tt = e.getCause();

                if( (tt != null) && (tt instanceof InvalidClassException) ) {
                    log.log( Level.SEVERE,"(Server<>Client class) " + tt );
                } else if( (tt != null) && (tt instanceof NotSerializableException) ) {
                    log.log( Level.SEVERE,"Serialization: " + tt.getMessage(),e );
                } else {
                    log.log( Level.SEVERE,"ex",e );
                }

                mWindowVO = null;
                s_server  = null;
            } catch( Throwable t ) {
                log.log( Level.SEVERE,"AEnv.getWindowVO - " + t );
                mWindowVO = null;
                s_server  = null;
            }
        }

        // Create Window Model on Client

        if( mWindowVO == null ) {
            log.config( "create local" );
            mWindowVO = MWindowVO.create( Env.getCtx(),WindowNo,AD_Window_ID,AD_Menu_ID );
        }

        // Check (remote) context

        else if( !mWindowVO.ctx.equals( Env.getCtx())) {

            // Remote Context is called by value, not reference
            // Add Window properties to context

            Enumeration keyEnum = mWindowVO.ctx.keys();

            while( keyEnum.hasMoreElements()) {
                String key = ( String )keyEnum.nextElement();

                if( key.startsWith( WindowNo + "|" )) {
                    String value = mWindowVO.ctx.getProperty( key );

                    Env.setContext( Env.getCtx(),key,value );
                }
            }

            // Sync Context

            mWindowVO.setCtx( Env.getCtx());
        }

        return mWindowVO;
    }    // getWindow

    /**
     * Descripción de Método
     *
     *
     * @param WindowNo
     * @param AD_Client_ID
     * @param AD_Table_ID
     * @param Record_ID
     * @param force
     *
     * @return
     */

    public static boolean postImmediate( int WindowNo,int AD_Client_ID,int AD_Table_ID,int Record_ID,boolean force ) {
        log.config( "Window=" + WindowNo + ", AD_Table_ID=" + AD_Table_ID + "/" + Record_ID );

        Boolean result = null;
        String  msg    = null;

        // try to get from Server when enabled

        if( isServerActive()) {
            log.config( "trying server" );

            try {
                Server server = CConnection.get().getServer();

                if( server != null ) {
                    result = new Boolean( server.postImmediate( Env.getCtx(),AD_Client_ID,AD_Table_ID,Record_ID,force ));
                    log.config( "from Server: " + result );
                } else {
                    ADialog.error( WindowNo,null,"NoAppsServer",msg );

                    return false;
                }
            } catch( RemoteException e ) {
                log.log( Level.SEVERE,"(RE)",e );
                msg      = e.getMessage();
                result   = null;
                s_server = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"ex",e );
                msg      = e.getMessage();
                result   = null;
                s_server = null;
            }
        } else {
            ADialog.error( WindowNo,null,"NoAppsServer",msg );

            return false;
        }

        if( result == null ) {
            ADialog.error( WindowNo,null,"PostServerError",msg );

            return false;
        }

        return result.booleanValue();
    }    // postImmediate

    /**
     * Descripción de Método
     *
     *
     * @param tableName
     * @param Record_ID
     */

    public static void cacheReset( String tableName,int Record_ID ) {
        log.config( "TableName=" + tableName + ", Record_ID=" + Record_ID );

        // try to get from Server when enabled

        if( isServerActive()) {
            log.config( "trying server" );

            try {
                Server server = CConnection.get().getServer();

                if( server != null ) {
                    server.cacheReset( tableName,Record_ID );
                }
            } catch( RemoteException e ) {
                log.log( Level.SEVERE,"(RE)",e );
                s_server = null;
            } catch( Exception e ) {
                log.log( Level.SEVERE,"ex",e );
                s_server = null;
            }
        }
    }    // cacheReset
}    // AEnv



/*
 *  @(#)AEnv.java   02.07.07
 * 
 *  Fin del fichero AEnv.java
 *  
 *  Versión 2.2
 *
 */
