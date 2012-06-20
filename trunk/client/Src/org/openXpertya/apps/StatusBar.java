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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.compiere.plaf.CompierePLAF;
import org.compiere.swing.CPanel;
import org.openXpertya.model.DataStatusEvent;
import org.openXpertya.model.MRole;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class StatusBar extends CPanel {

    /**
     * Constructor de la clase ...
     *
     */

    public StatusBar() {
        this( false );
    }    // StatusBar

    /**
     * Constructor de la clase ...
     *
     *
     * @param withInfo
     */

    public StatusBar( boolean withInfo ) {
        super();

        try {
            jbInit();
        } catch( Exception e ) {
        }

        this.setName( "statusBar" );

        if( !withInfo ) {
            infoLine.setVisible( false );
        }
    }    // StatusBar

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private JLabel statusLine = new JLabel();

    /** Descripción de Campos */

    private JLabel statusDB = new JLabel();

    /** Descripción de Campos */

    private JLabel infoLine = new JLabel();

    //

    /** Descripción de Campos */

    private boolean mt_error;

    /** Descripción de Campos */

    private String mt_text;

    //

    /** Descripción de Campos */

    private String m_text;
    
    /** Mensaje a mostrar dentro de la ventana Dialog */
    
    private String dialogMsg;

    /** Descripción de Campos */

    private DataStatusEvent m_dse = null;
    

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    private void jbInit() throws Exception {
        statusLine.setBorder( BorderFactory.createEtchedBorder());
        statusLine.setText( "statusLine" );
        statusLine.setOpaque( false );
        // Disytel - Franco Bonafine
        // Agregado de listener para abrir un cuadro de diálogo con el mensaje
        // de la línea de estado en caso que el usuario haga doble click sobre 
        // la misma.
        statusLine.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() > 1) {
					showStatusLineDialog();
				}
			}
        });
        statusLine.setToolTipText(Msg.translate(Env.getCtx(), "StatusBarExpandMessageTooltip"));
        // --
        statusDB.setForeground( Color.blue );
        statusDB.setBorder( BorderFactory.createEtchedBorder());
        statusDB.setText( "#" );
        statusDB.setOpaque( false );
        statusDB.addMouseListener( new StatusBar_mouseAdapter( this ));
        this.setLayout( mainLayout );
        infoLine.setFont( CompierePLAF.getFont_Label());
        infoLine.setBorder( BorderFactory.createRaisedBevelBorder());
        infoLine.setHorizontalAlignment( SwingConstants.CENTER );
        infoLine.setHorizontalTextPosition( SwingConstants.CENTER );
        infoLine.setText( "info" );
        mainLayout.setHgap( 2 );
        mainLayout.setVgap( 2 );
        this.add( statusLine,BorderLayout.CENTER );
        this.add( statusDB,BorderLayout.EAST );
        this.add( infoLine,BorderLayout.NORTH );
    }    // jbInit

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    public void setStatusLine( String text ) {
        if( text == null ) {
            setStatusLine( "",false );
        } else {
            setStatusLine( text,false );
        }
    }    // setStatusLine

    /**
     * Descripción de Método
     *
     *
     * @param text
     * @param error
     */

    public void setStatusLine( String text,boolean error ) {
        mt_error = error;
        mt_text  = text;

        if( mt_error ) {
            statusLine.setForeground( CompierePLAF.getTextColor_Issue());
        } else {
            statusLine.setForeground( CompierePLAF.getTextColor_OK());
        }

        statusLine.setText( " " + mt_text );
        setDialogMsg(mt_text);
        //

        Thread.yield();
    }    // setStatusLine

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getStatusLine() {
        return statusLine.getText().trim();
    }    // setStatusLine

    /**
     * Descripción de Método
     *
     *
     * @param tip
     */

    public void setStatusToolTip( String tip ) {
        statusLine.setToolTipText( tip );
    }    // setStatusToolTip

    /**
     * Descripción de Método
     *
     *
     * @param text
     * @param dse
     */

    public void setStatusDB( String text,DataStatusEvent dse ) {

        // log.config( "StatusBar.setStatusDB - " + text + " - " + created + "/" + createdBy);

        if( (text == null) || (text.length() == 0) ) {
            statusDB.setText( "" );
            statusDB.setVisible( false );
        } else {
            StringBuffer sb = new StringBuffer( " " );

            sb.append( text ).append( " " );
            statusDB.setText( sb.toString());

            if( !statusDB.isVisible()) {
                statusDB.setVisible( true );
            }
        }

        // Save

        m_text = text;
        m_dse  = dse;
    }    // setStatusDB

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    public void setStatusDB( String text ) {
        setStatusDB( text,null );
    }    // setStatusDB

    /**
     * Descripción de Método
     *
     *
     * @param no
     */

    public void setStatusDB( int no ) {
        setStatusDB( String.valueOf( no ),null );
    }    // setStatusDB

    /**
     * Descripción de Método
     *
     *
     * @param text
     */

    public void setInfo( String text ) {
        if( !infoLine.isVisible()) {
            infoLine.setVisible( true );
        }

        infoLine.setText( text );
    }    // setInfo

    /**
     * Descripción de Método
     *
     *
     * @param component
     */

    public void addStatusComponent( JComponent component ) {
        this.add( component,BorderLayout.EAST );
    }    // addStatusComponent

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    void mouseClicked( MouseEvent e ) {
        if( (m_dse == null) || (m_dse.CreatedBy == null) ||!MRole.getDefault().isShowPreference()) {
            return;
        }

        //

        String     title = Msg.getMsg( Env.getCtx(),"Who" ) + m_text;
        RecordInfo info  = new RecordInfo( Env.getFrame( this ),title,m_dse );

        AEnv.showCenterScreen( info );
    }    // addStatusComponent
    
    /**
     * Muestra el mensaje de la línea de estado en un diálogo.
     */
    public void showStatusLineDialog() {
		if (mt_error) {
			ADialog.error(0, StatusBar.this, "Error", getDialogMsg());
		} else {
			ADialog.info(0, StatusBar.this, "Info", getDialogMsg());
		}

    }

	public void setDialogMsg(String dialogMsg) {
		this.dialogMsg = dialogMsg;
	}

	public String getDialogMsg() {
		return dialogMsg;
	}
}    // StatusBar


/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

class StatusBar_mouseAdapter extends java.awt.event.MouseAdapter {

    /** Descripción de Campos */

    private StatusBar adaptee;

    /**
     * Constructor de la clase ...
     *
     *
     * @param adaptee
     */

    StatusBar_mouseAdapter( StatusBar adaptee ) {
        this.adaptee = adaptee;
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void mouseClicked( MouseEvent e ) {
        adaptee.mouseClicked( e );
    }
}    // StatusBar_mouseAdapter



/*
 *  @(#)StatusBar.java   02.07.07
 * 
 *  Fin del fichero StatusBar.java
 *  
 *  Versión 2.2
 *
 */
