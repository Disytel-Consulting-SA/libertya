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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.logging.Level;

import javax.swing.border.TitledBorder;

import org.compiere.swing.CButton;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.openXpertya.grid.ed.VNumber;
import org.openXpertya.model.MOrder;
import org.openXpertya.process.DocAction;
import org.openXpertya.process.PrintLabel;
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

public class SubCheckout extends PosSubPanel implements ActionListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param posPanel
     */

    public SubCheckout( PosPanel posPanel ) {
        super( posPanel );
    }    // PosSubCheckout

    /** Descripción de Campos */

    private CButton f_register = null;

    /** Descripción de Campos */

    private CButton f_summary = null;

    /** Descripción de Campos */

    private CButton f_process = null;

    /** Descripción de Campos */

    private CButton f_print = null;

/*      private CLabel f_lcreditCardNumber = null;
        private CTextField f_creditCardNumber = null;
        private CLabel f_lcreditCardExp = null;
        private CTextField f_creditCardExp = null;
        private CLabel f_lcreditCardVV = null;
        private CTextField f_creditCardVV = null;
        private CButton f_creditPayment = null;
*/

    /** Descripción de Campos */

    private CLabel f_lcashGiven = null;

    /** Descripción de Campos */

    private VNumber f_cashGiven;

    /** Descripción de Campos */

    private CLabel f_lcashReturn = null;

    /** Descripción de Campos */

    private VNumber f_cashReturn;

    /** Descripción de Campos */

    private CButton f_cashPayment = null;

    /** Descripción de Campos */

    private CButton f_funcionesCaja;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( SubCheckout.class );

    /**
     * Descripción de Método
     *
     */

    public void init() {

        // Title

        TitledBorder border = new TitledBorder( Msg.getMsg( Env.getCtx(),"Checkout" ));

        setBorder( border );

        // Content

        setLayout( new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = INSETS2;

        // --      0

        gbc.gridx  = 0;
        f_register = createButtonAction( "Register",null );
        gbc.gridy  = 0;
        add( f_register,gbc );

        //

        f_summary      = createButtonAction( "Summary",null );
        gbc.gridy      = 1;
        gbc.gridheight = 1;
        add( f_summary,gbc );

        //

        f_process      = createButtonAction( "Process",null );
        gbc.gridy      = 2;
        gbc.gridheight = 1;
        add( f_process,gbc );

        //

        f_print        = createButtonAction( "Print",null );
        gbc.gridy      = 3;
        gbc.gridheight = 1;
        add( f_print,gbc );

        // --      1 -- Cash

        gbc.gridx      = 1;
        gbc.gridheight = 2;
        gbc.fill       = GridBagConstraints.BOTH;
        gbc.weightx    = .1;

        CPanel cash = new CPanel( new GridBagLayout());

        cash.setBorder( new TitledBorder( Msg.getMsg( Env.getCtx(),"Cash" )));
        gbc.gridy = 1;
        add( cash,gbc );

        GridBagConstraints gbc0 = new GridBagConstraints();

        gbc0.insets = INSETS2;
        gbc0.anchor = GridBagConstraints.WEST;

        //

        f_lcashGiven = new CLabel( Msg.getMsg( Env.getCtx(),"CashGiven" ));
        cash.add( f_lcashGiven,gbc0 );
        f_cashGiven = new VNumber( "CashGiven",false,false,true,DisplayType.Amount,Msg.translate( Env.getCtx(),"CashGiven" ));
        f_cashGiven.setColumns( 10,25 );
        cash.add( f_cashGiven,gbc0 );
        f_cashGiven.setValue( Env.ZERO );
        f_cashGiven.addActionListener( this );    // para que actualice el cambio con el dinero entregado

        //

        f_lcashReturn = new CLabel( Msg.getMsg( Env.getCtx(),"CashReturn" ));
        cash.add( f_lcashReturn,gbc0 );
        f_cashReturn = new VNumber( "CashReturn",false,true,false,DisplayType.Amount,"CashReturn" );
        f_cashReturn.setColumns( 10,25 );
        cash.add( f_cashReturn,gbc0 );
        f_cashReturn.setValue( Env.ZERO );

        //

        f_cashPayment = createButtonAction( "Payment",null );
        f_cashPayment.setActionCommand( "Cash" );
        gbc0.anchor  = GridBagConstraints.EAST;
        gbc0.weightx = 0.1;
        cash.add( f_cashPayment,gbc0 );
        gbc.gridx      = 1;
        gbc.gridy      = 3;
        gbc.gridheight = 1;

        CPanel caja = new CPanel();

        add( caja,gbc );
        caja.setPreferredSize( new Dimension( 30,30 ));
        f_funcionesCaja = createButtonAction( "FuncionesCaja",null );
        f_funcionesCaja.setText( "Movimientos\n de Caja" );
        f_funcionesCaja.setPreferredSize( new Dimension( 160,30 ));
        f_funcionesCaja.setMaximumSize( new Dimension( 160,30 ));
        f_funcionesCaja.setMinimumSize( new Dimension( 160,30 ));
        caja.add( f_funcionesCaja );

/*  Panel para la introducci�n de los datos de CreditCard para el pago quitada por ConSerTi al no considerar
 *  que sea �til de momento

                //      --      1 -- Creditcard
                CPanel creditcard = new CPanel(new GridBagLayout());
                creditcard.setBorder(new TitledBorder(Msg.translate(Env.getCtx(), "CreditCardType")));
                gbc.gridy = 2;
                add (creditcard, gbc);
                GridBagConstraints gbc1 = new GridBagConstraints();
                gbc1.insets = INSETS2;
                gbc1.anchor = GridBagConstraints.WEST;

                gbc1.gridx = 0;
                gbc1.gridy = 0;
                f_lcreditCardNumber = new CLabel(Msg.translate(Env.getCtx(), "CreditCardNumber"));
                creditcard.add (f_lcreditCardNumber, gbc1);
                gbc1.gridy = 1;
                f_creditCardNumber = new CTextField(18);
                creditcard.add (f_creditCardNumber, gbc1);
                gbc1.gridx = 1;
                gbc1.gridy = 0;
                f_lcreditCardExp = new CLabel(Msg.translate(Env.getCtx(),"CreditCardExp"));
                creditcard.add (f_lcreditCardExp, gbc1);
                gbc1.gridy = 1;
                f_creditCardExp = new CTextField(5);
                creditcard.add (f_creditCardExp, gbc1);
                gbc1.gridx = 2;
                gbc1.gridy = 0;
                f_lcreditCardVV = new CLabel(Msg.translate(Env.getCtx(), "CreditCardVV"));
                creditcard.add (f_lcreditCardVV, gbc1);
                gbc1.gridy = 1;
                f_creditCardVV = new CTextField(5);
                creditcard.add (f_creditCardVV, gbc1);
                //
                gbc1.gridx = 3;
                gbc1.gridy = 0;
                gbc1.gridheight = 2;
                f_creditPayment = createButtonAction("Payment", null);
                f_creditPayment.setActionCommand("CreditCard");
                gbc1.anchor = GridBagConstraints.EAST;
                gbc1.weightx = 0.1;
                creditcard.add (f_creditPayment, gbc1);

                **/

        // fin del comentario para quitar la parte del CreditCard

    }    // init

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gbc = super.getGridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 3;

        return gbc;
    }    // getGridBagConstraints

    /**
     * Descripción de Método
     *
     */

    public void dispose() {
        super.dispose();
    }    // dispose

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

        log.info( "PosSubCheckout - actionPerformed: " + action );

        // Register

        if( action.equals( "Register" )) {
            p_posPanel.f_queryTicket.reset();
            p_posPanel.openQuery( p_posPanel.f_queryTicket );
        }

        // Summary

        else if( action.equals( "Summary" )) {
            p_posPanel.f_status.setStatusLine( p_posPanel.f_curLine.getOrder().getResumen());
            mostrarVuelta();
        } else if( action.equals( "Process" )) {
            if( pedidoPagado()) {
                imprimirTicket();
                procesarPedido();
                abrirCaja();
            }
        }

        // Print

        else if( action.equals( "Print" )) {
            if( pedidoPagado()) {
                imprimirTicket();
                abrirCaja();
            }
        }

        // Cash (Payment)

        else if( action.equals( "Cash" )) {
            mostrarVuelta();
            abrirCaja();
        } else if( action.equals( "FuncionesCaja" )) {
            p_posPanel.openQuery( p_posPanel.f_funcionesCaja );
        } else if( e.getSource() == f_cashGiven ) {
            mostrarVuelta();
        }

/*              //      CreditCard (Payment)
                else if (action.equals("CreditCard"))
                {
                        Log.print("CreditCard");
                }  fin del comentario para la Credit Card*/

        f_cashGiven.setValue( Env.ZERO );
        p_posPanel.actualizarInfo();
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     */

    public void procesarPedido() {
        MOrder pedido = p_posPanel.f_curLine.getOrder();

        if( pedido != null ) {
            if( pedido.getDocStatus().equals( "DR" )) {
                pedido.setDocAction( DocAction.ACTION_Complete );
                pedido.processIt( DocAction.ACTION_Complete );
                pedido.save();
                pedido = null;
                p_posPanel.newOrder();
            }
        }
    }

    /**
     * Descripción de Método
     *
     */

    public void imprimirTicket() {
        MOrder pedido = p_posPanel.f_curLine.getOrder();

        if( pedido != null ) {
            try {
                if( p_pos.getAD_PrintLabel_ID() != 0 ) {
                    PrintLabel.printLabelTicket( pedido.getC_Order_ID(),p_pos.getAD_PrintLabel_ID());
                }
            } catch( Exception e ) {
                log.log( Level.SEVERE,"ImprimirTicket - Error al imprimir Ticket" );
            }
        }
    }

    /**
     * Descripción de Método
     *
     */

    public void mostrarVuelta() {
        BigDecimal entregado = new BigDecimal( f_cashGiven.getValue().toString());

        if( (p_posPanel != null) && (p_posPanel.f_curLine != null) ) {
            MOrder     pedido = p_posPanel.f_curLine.getOrder();
            BigDecimal total  = new BigDecimal( 0 );

            if( pedido != null ) {
                total = pedido.getGrandTotal();
            }

            double vuelta = entregado.doubleValue() - total.doubleValue();

            f_cashReturn.setValue( new BigDecimal( vuelta ));
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean pedidoPagado() {
        BigDecimal entregado = new BigDecimal( f_cashGiven.getValue().toString());
        boolean pagado = false;

        if( (p_posPanel != null) && (p_posPanel.f_curLine != null) ) {
            MOrder     pedido = p_posPanel.f_curLine.getOrder();
            BigDecimal total  = new BigDecimal( 0 );

            if( pedido != null ) {
                total = pedido.getGrandTotal();
            }

            pagado = entregado.doubleValue() >= total.doubleValue();
        }

        return pagado;
    }

    /**
     * Descripción de Método
     *
     */

    public void abrirCaja() {
        String puerto = null;

        try {
            String sql = "SELECT p.Port" + " FROM AD_PrintLabel l" + " INNER JOIN AD_LabelPrinter p ON (l.AD_LabelPrinter_ID=p.AD_LabelPrinter_ID)" + " WHERE l.AD_PrintLabel_ID=?";

            puerto = DB.getSQLValueString( null,sql,p_pos.getAD_PrintLabel_ID());
        } catch( Exception e ) {
            log.log( Level.SEVERE,"AbrirCaja - Puerto no encontrado" );
        }

        if( puerto == null ) {
            log.log( Level.SEVERE,"Port is mandatory for cash drawner" );
        } else {
            try {
                byte data[] = new byte[ 5 ];

                data[ 0 ] = 27;
                data[ 1 ] = 112;
                data[ 2 ] = 0;
                data[ 3 ] = 50;
                data[ 4 ] = 50;

                FileOutputStream     fos = new FileOutputStream( puerto );
                BufferedOutputStream bos = new BufferedOutputStream( fos );

                bos.write( data,0,data.length );
                bos.close();
                fos.close();
            } catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}    // PosSubCheckout



/*
 *  @(#)SubCheckout.java   02.07.07
 * 
 *  Fin del fichero SubCheckout.java
 *  
 *  Versión 2.2
 *
 */
