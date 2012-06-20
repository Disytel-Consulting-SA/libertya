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



package org.openXpertya.pos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;

import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.swing.CScrollPane;
import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.ConfirmPanel;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MCash;
import org.openXpertya.model.MCashBook;
import org.openXpertya.model.MCashLine;
import org.openXpertya.model.MOrder;
import org.openXpertya.model.MQuery;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class SubFuncionesCaja extends PosSubPanel implements ActionListener,InputMethodListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public SubFuncionesCaja( PosPanel posPanel ) {
        super( posPanel );
    }    // PosQueryProduct

    /** Descripción de Campos */

    private CButton f_desplegarCambioInicial = null;

    /** Descripción de Campos */

    private CButton f_entradasSalidas = null;

    /** Descripción de Campos */

    private CButton f_tickets = null;

    /** Descripción de Campos */

    private CButton f_cierreCaja = null;

    /** Descripción de Campos */

    private CButton f_desplegarArqueo = null;

    /** Descripción de Campos */

    private CButton f_pos = null;

    /** Descripción de Campos */

    private CPanel c;

    // para el cambio inicial

    /** Descripción de Campos */

    private CPanel cInicial;

    /** Descripción de Campos */

    private CLabel l_cambioAnterior = null;

    /** Descripción de Campos */

    private VNumber v_cambioAnterior;

    /** Descripción de Campos */

    private CLabel l_cambio = null;

    /** Descripción de Campos */

    private VNumber v_cambio;

    /** Descripción de Campos */

    private CButton f_cambio = null;

    // para el arqueo de caja

    /** Descripción de Campos */

    private CPanel cArqueo;

    /** Descripción de Campos */

    private CLabel l_saldoAnterior = null;

    /** Descripción de Campos */

    private VNumber v_saldoAnterior;

    /** Descripción de Campos */

    private CLabel l_saldoActual = null;

    /** Descripción de Campos */

    private VNumber v_saldoActual;

    /** Descripción de Campos */

    private CLabel l_diferencia = null;

    /** Descripción de Campos */

    private VNumber v_diferencia;

    /** Descripción de Campos */

    private CButton f_calcularDiferencia = null;

    /** Descripción de Campos */

    private CPanel panel;

    /** Descripción de Campos */

    private CScrollPane centerScroll;

    /** Descripción de Campos */

    private ConfirmPanel confirm;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( SubFuncionesCaja.class );

    /**
     * Descripción de Método
     *
     */

    protected void init() {
        setLayout( new BorderLayout( 2,6 ));
        setVisible( false );

        // North

        panel = new CPanel( new GridBagLayout());
        add( panel,BorderLayout.CENTER );
        panel.setBorder( new TitledBorder( Msg.getMsg( p_ctx,"Cash Functions" )));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = PosSubPanel.INSETS2;

        //

        gbc.gridx   = 0;
        gbc.gridy   = 0;
        gbc.anchor  = GridBagConstraints.CENTER;
        gbc.weightx = .3;
        gbc.weighty = 0.05;

        // ********************  botones principales **********************************

        f_desplegarCambioInicial = createButtonAction( "InitialChange",null );
        f_desplegarCambioInicial.setText( Msg.getMsg( p_ctx,"InitialChange" ));
        f_desplegarCambioInicial.setActionCommand( "desplegarCambioInicial" );
        f_desplegarCambioInicial.setMaximumSize( new Dimension( 160,35 ));
        f_desplegarCambioInicial.setMinimumSize( new Dimension( 160,35 ));
        f_desplegarCambioInicial.setPreferredSize( new Dimension( 160,35 ));
        panel.add( f_desplegarCambioInicial,gbc );

        //

        f_cierreCaja = createButtonAction( "CloseCash",null );
        f_cierreCaja.setText( Msg.getMsg( p_ctx,"CloseCash" ));
        f_cierreCaja.setActionCommand( "CierreCaja" );
        f_cierreCaja.setPreferredSize( new Dimension( 160,35 ));
        f_cierreCaja.setMaximumSize( new Dimension( 160,35 ));
        f_cierreCaja.setMinimumSize( new Dimension( 160,35 ));
        gbc.gridy = 1;
        panel.add( f_cierreCaja,gbc );

        //

        f_desplegarArqueo = createButtonAction( "Caunt",null );
        f_desplegarArqueo.setText( "Arqueo de Caja" );
        f_desplegarArqueo.setActionCommand( "desplegarArqueo" );
        f_desplegarArqueo.setPreferredSize( new Dimension( 160,35 ));
        f_desplegarArqueo.setMaximumSize( new Dimension( 160,35 ));
        f_desplegarArqueo.setMinimumSize( new Dimension( 160,35 ));
        gbc.gridy = 2;
        panel.add( f_desplegarArqueo,gbc );

        //

        f_entradasSalidas = createButtonAction( "CashInOuts",null );
        f_entradasSalidas.setText( Msg.getMsg( p_ctx,"CashInOuts" ));
        f_entradasSalidas.setActionCommand( "EntradasSalidas" );
        f_entradasSalidas.setPreferredSize( new Dimension( 160,35 ));
        f_entradasSalidas.setMaximumSize( new Dimension( 160,35 ));
        f_entradasSalidas.setMinimumSize( new Dimension( 160,35 ));
        gbc.gridy = 3;
        panel.add( f_entradasSalidas,gbc );

        //

        f_tickets = createButtonAction( "Tickets",null );
        f_tickets.setText( "Tickets" );
        f_tickets.setPreferredSize( new Dimension( 160,35 ));
        f_tickets.setMaximumSize( new Dimension( 160,35 ));
        f_tickets.setMinimumSize( new Dimension( 160,35 ));
        gbc.gridy = 4;
        panel.add( f_tickets,gbc );

        //

        f_pos = createButtonAction( "End",null );
        f_pos.setText( "TPV" );
        f_pos.setPreferredSize( new Dimension( 160,35 ));
        f_pos.setMaximumSize( new Dimension( 160,35 ));
        f_pos.setMinimumSize( new Dimension( 160,35 ));
        gbc.gridy = 5;
        panel.add( f_pos,gbc );

        // *************************** Panel para colocar botones*************************

        gbc.gridx      = 1;
        gbc.anchor     = GridBagConstraints.WEST;
        gbc.gridy      = 0;
        gbc.gridheight = 5;
        c              = new CPanel( new GridBagLayout());
        c.setBorder( new TitledBorder( "" ));
        c.setMaximumSize( new Dimension( 400,400 ));
        c.setMinimumSize( new Dimension( 400,400 ));
        c.setPreferredSize( new Dimension( 400,400 ));
        panel.add( c,gbc );

        // *************************** Panel para cambio inicial *************************

        gbc.gridx      = 1;
        gbc.anchor     = GridBagConstraints.WEST;
        gbc.gridy      = 0;
        gbc.gridheight = 5;
        cInicial       = new CPanel( new GridBagLayout());
        cInicial.setBorder( new TitledBorder( "Cambio Inicial" ));
        cInicial.setVisible( false );
        panel.add( cInicial,gbc );

        GridBagConstraints gbc0 = new GridBagConstraints();

        gbc0.insets = INSETS2;
        gbc0.anchor = GridBagConstraints.CENTER;

        //

        gbc0.gridx       = 0;
        gbc0.gridy       = 0;
        l_cambioAnterior = new CLabel( "Cambio Anterior" );
        cInicial.add( l_cambioAnterior,gbc0 );
        gbc0.gridx       = 1;
        v_cambioAnterior = new VNumber( "CambioAnterior",false,true,false,DisplayType.Amount,"CambioAnterior" );
        v_cambioAnterior.setColumns( 10,25 );
        cInicial.add( v_cambioAnterior,gbc0 );
        v_cambioAnterior.setValue( Env.ZERO );

        //

        gbc0.gridx = 0;
        gbc0.gridy = 1;
        l_cambio   = new CLabel( "Cambio inicial" );
        cInicial.add( l_cambio,gbc0 );
        gbc0.gridx = 1;
        v_cambio   = new VNumber( "Cambio",false,false,true,DisplayType.Amount,"Cambio" );
        v_cambio.setColumns( 10,25 );
        cInicial.add( v_cambio,gbc0 );
        v_cambio.setValue( Env.ZERO );
        gbc0.gridy     = 2;
        gbc0.gridx     = 0;
        gbc0.gridwidth = 2;

        // gbc0.fill = GridBagConstraints.HORIZONTAL;

        f_cambio = createButtonAction( "SetChange",null );
        f_cambio.setText( "Guardar Cambio" );
        f_cambio.setActionCommand( "guardarCambio" );
        cInicial.add( f_cambio,gbc0 );
        f_cambio.setPreferredSize( new Dimension( 160,35 ));
        f_cambio.setMaximumSize( new Dimension( 160,35 ));
        f_cambio.setMinimumSize( new Dimension( 160,35 ));
        cInicial.setMaximumSize( new Dimension( 400,400 ));
        cInicial.setMinimumSize( new Dimension( 400,400 ));
        cInicial.setPreferredSize( new Dimension( 400,400 ));

        // ******************************  Panel para arqueo de caja  ************************

        gbc.gridx      = 1;
        gbc.anchor     = GridBagConstraints.WEST;
        gbc.gridy      = 0;
        gbc.gridheight = 5;

        // gbc.weightx = .7;

        cArqueo = new CPanel( new GridBagLayout());
        cArqueo.setBorder( new TitledBorder( "Arqueo de Caja" ));
        cArqueo.setVisible( false );
        panel.add( cArqueo,gbc );

        GridBagConstraints gbc1 = new GridBagConstraints();

        gbc1.insets = INSETS2;
        gbc1.anchor = GridBagConstraints.CENTER;

        //

        gbc1.gridx      = 0;
        gbc1.gridy      = 0;
        l_saldoAnterior = new CLabel( "Saldo Anterior" );
        cArqueo.add( l_saldoAnterior,gbc1 );
        gbc1.gridx      = 1;
        v_saldoAnterior = new VNumber( "SaldoAnterior",false,true,false,DisplayType.Amount,"SaldoAnterior" );
        v_saldoAnterior.setColumns( 10,25 );
        cArqueo.add( v_saldoAnterior,gbc1 );
        v_saldoAnterior.setValue( Env.ZERO );

        //

        gbc1.gridx    = 0;
        gbc1.gridy    = 1;
        l_saldoActual = new CLabel( "Saldo Actual" );
        cArqueo.add( l_saldoActual,gbc1 );
        gbc1.gridx    = 1;
        v_saldoActual = new VNumber( "SaldoActual",false,false,true,DisplayType.Amount,"SaldoActual" );
        v_saldoActual.setColumns( 10,25 );
        v_saldoActual.addActionListener( this );
        v_saldoActual.addInputMethodListener( this );
        cArqueo.add( v_saldoActual,gbc1 );
        v_saldoActual.setValue( Env.ZERO );

        //

        gbc1.gridx   = 0;
        gbc1.gridy   = 2;
        l_diferencia = new CLabel( "Diferencia" );
        cArqueo.add( l_diferencia,gbc1 );
        gbc1.gridx   = 1;
        v_diferencia = new VNumber( "Diferencia",false,true,false,DisplayType.Amount,"Diferencia" );
        v_diferencia.setColumns( 10,25 );
        cArqueo.add( v_diferencia,gbc1 );
        v_diferencia.setValue( Env.ZERO );

        //

        gbc1.gridx           = 0;
        gbc1.gridy           = 4;
        gbc1.gridwidth       = 2;
        gbc1.fill            = GridBagConstraints.HORIZONTAL;
        f_calcularDiferencia = createButtonAction( "SetCaunt",null );
        f_calcularDiferencia.setText( "Anotar Diferencia" );
        f_calcularDiferencia.setActionCommand( "AnotarDiferencia" );
        cArqueo.add( f_calcularDiferencia,gbc1 );
        cArqueo.setMaximumSize( new Dimension( 400,400 ));
        cArqueo.setMinimumSize( new Dimension( 400,400 ));
        cArqueo.setPreferredSize( new Dimension( 400,400 ));
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gbc = super.getGridBagConstraints();

        gbc.gridx     = 0;
        gbc.gridy     = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 2;    // GridBagConstraints.REMAINDER;
        gbc.fill      = GridBagConstraints.BOTH;
        gbc.weightx   = 0.5;
        gbc.weighty   = 0.5;

        return gbc;
    }    // getGridBagConstraints

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        removeAll();
        panel        = null;
        centerScroll = null;
        confirm      = null;
    }    // dispose

    /**
     * Descripción de Método
     *
     *
     * @param aFlag
     */

    public void setVisible( boolean aFlag ) {
        super.setVisible( aFlag );
    }    // setVisible

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {
        String action = e.getActionCommand();

        if( (action == null) || (action.length() == 0) ) {
            return;
        }

        log.info( "PosSubFuncionesCaja - actionPerformed: " + action );

        // para desplegar el panel del cambio inicial

        if( action.equals( "desplegarCambioInicial" )) {
            cmd_desplegarCambioInicial();
        }

        // para desplegar el panel de cierre de caja

        else if( action.equals( "CierreCaja" )) {
            Timestamp today = TimeUtil.getDay( System.currentTimeMillis());
            MCash     cash  = MCash.get( p_pos.getCtx(),p_pos.getC_CashBook_ID(),today,null );
            MQuery query = new MQuery( MCash.Table_Name );

            query.addRestriction( "C_Cash_ID",MQuery.EQUAL,cash.getC_Cash_ID());
            AEnv.abrirVentana( MCash.Table_ID,query,false );
        }

        // para abrir la ventana de las entradas y salidas de caja

        else if( action.equals( "EntradasSalidas" )) {
            Timestamp today = TimeUtil.getDay( System.currentTimeMillis());
            MCash     cash  = MCash.get( p_pos.getCtx(),p_pos.getC_CashBook_ID(),today,null );

            AEnv.zoomDetallado( MCash.Table_ID,cash.getC_Cash_ID(),false );
        } else if( action.equals( "Tickets" )) {
            MQuery query = new MQuery( MOrder.Table_Name );

            query.addRestriction( "C_DocTypeTarget_ID",MQuery.EQUAL,p_pos.getC_OrderDocType_ID());
            AEnv.openWindow( p_pos.getAD_Window_ID(),query,true );
        }

        // Cash (Payment)

        else if( action.equals( "desplegarArqueo" )) {
            cmd_desplegarArqueo();
        } else if( action.equals( "End" )) {
            p_posPanel.closeQuery( p_posPanel.f_funcionesCaja );
        } else if( action.equals( "guardarCambio" )) {
            cmd_guardarCambio();
        } else if( action.equals( "AnotarDiferencia" )) {
            cmd_calcularDiferencia();
            cmd_anotarDiferencia();
        } else if( e.getSource() == v_saldoActual ) {
            cmd_calcularDiferencia();
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    private void cmd_desplegarCambioInicial() {
        cArqueo.setVisible( false );
        c.setVisible( false );
        cInicial.setVisible( true );

        Timestamp today = TimeUtil.getDay( System.currentTimeMillis());
        MCash     cash  = MCash.get( p_pos.getCtx(),p_pos.getC_CashBook_ID(),today,null );

        if( cash != null ) {
            v_cambioAnterior.setValue( cash.getEndingBalance());
            v_cambio.setValue( cash.getEndingBalance());
        } else {
            log.log( Level.SEVERE,"No Cash" );
        }
    }

    /**
     * Descripción de Método
     *
     */

    private void cmd_desplegarArqueo() {
        cInicial.setVisible( false );
        c.setVisible( false );
        cArqueo.setVisible( true );

        // calcula el total hasta el momento y lo muestra en el panel de arqueo

        Timestamp today = TimeUtil.getDay( System.currentTimeMillis());
        MCash     cash  = MCash.get( p_pos.getCtx(),p_pos.getC_CashBook_ID(),today,null );

        v_saldoAnterior.setValue( cash.getEndingBalance());
    }

    /**
     * Descripción de Método
     *
     */

    private void cmd_guardarCambio() {
        MCashBook cashBook = new MCashBook( p_ctx,p_pos.getC_CashBook_ID(),null );
        Timestamp  today     = TimeUtil.getDay( System.currentTimeMillis());
        MCash      cash      = MCash.get( p_ctx,p_pos.getC_CashBook_ID(),today,null );
        BigDecimal cambioIni = ( BigDecimal )v_cambio.getValue();

        if( (cash != null) && (cash.getID() != 0) && (cambioIni.compareTo( cash.getEndingBalance()) != 0) ) {
            MCashLine cl = new MCashLine( cash );

            cl.setCashType( MCashLine.CASHTYPE_Difference );
            cl.setAmount( cambioIni.subtract( cash.getEndingBalance()));
            cl.setDescription( "Cambio Inicial Antes: " + cash.getEndingBalance() + " Ahora: " + cambioIni );
            cl.save();
        }

        v_cambioAnterior.setValue( cambioIni );
    }

    /**
     * Descripción de Método
     *
     */

    private void cmd_calcularDiferencia() {

        // calcula la diferencia de arqueo entre saldo anterior y saldo actual

        BigDecimal valorAnterior,valorActual;
        Timestamp  today = TimeUtil.getDay( System.currentTimeMillis());
        MCash      cash  = MCash.get( p_pos.getCtx(),p_pos.getC_CashBook_ID(),today,null );

        v_saldoAnterior.setValue( cash.getEndingBalance());
        valorAnterior = cash.getEndingBalance();
        valorActual   = ( BigDecimal )v_saldoActual.getValue();
        v_diferencia.setValue( valorActual.subtract( valorAnterior ));
    }

    /**
     * Descripción de Método
     *
     */

    private void cmd_anotarDiferencia() {

        // calcula la diferencia de arqueo entre saldo anterior y saldo actual

        BigDecimal valorAnterior,valorActual,diferencia;

        valorAnterior = ( BigDecimal )v_saldoAnterior.getValue();
        valorActual   = ( BigDecimal )v_saldoActual.getValue();
        diferencia    = valorActual.subtract( valorAnterior );

        MCashBook cashBook = new MCashBook( p_ctx,p_pos.getC_CashBook_ID(),null );
        Timestamp today = TimeUtil.getDay( System.currentTimeMillis());
        MCash     cash  = MCash.get( p_ctx,cashBook.getC_CashBook_ID(),today,null );

        if( (cash != null) && (cash.getID() != 0) && (diferencia.compareTo( cash.getStatementDifference()) != 0) ) {
            MCashLine cl = new MCashLine( cash );

            cl.setCashType( MCashLine.CASHTYPE_Difference );
            cl.setAmount( diferencia );
            cl.setDescription( Msg.translate( p_pos.getCtx(),"Cash Balance" ) + " : " + valorAnterior + " --> " + valorActual );
            cl.save();
        }

        cash = MCash.get( p_pos.getCtx(),p_pos.getC_CashBook_ID(),today,null );
        v_saldoAnterior.setValue( cash.getEndingBalance());
        v_saldoActual.setValue( Env.ZERO );
        v_diferencia.setValue( Env.ZERO );
    }

    /**
     * Descripción de Método
     *
     *
     * @param event
     */

    public void caretPositionChanged( InputMethodEvent event ) {
        cmd_calcularDiferencia();
    }

    /**
     * Descripción de Método
     *
     *
     * @param event
     */

    public void inputMethodTextChanged( InputMethodEvent event ) {
        cmd_calcularDiferencia();
    }
}    // SubFuncionesCaja



/*
 *  @(#)SubFuncionesCaja.java   02.07.07
 * 
 *  Fin del fichero SubFuncionesCaja.java
 *  
 *  Versión 2.1
 *
 */
