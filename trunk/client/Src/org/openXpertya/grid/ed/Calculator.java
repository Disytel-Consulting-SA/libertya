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



package org.openXpertya.grid.ed;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openXpertya.apps.ADialog;
import org.openXpertya.model.MConversionRate;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
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

public final class Calculator extends JDialog implements ActionListener,KeyListener {

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param title
     * @param displayType
     * @param format
     * @param number
     */

    public Calculator( Frame frame,String title,int displayType,DecimalFormat format,BigDecimal number ) {
        super( frame,title,true );
        setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );

        // Get WindowNo for Currency

        m_WindowNo = Env.getWindowNo( frame );

        //

        m_DisplayType = displayType;

        if( !DisplayType.isNumeric( m_DisplayType )) {
            m_DisplayType = DisplayType.Number;
        }

        //

        m_format = format;

        if( m_format == null ) {
            m_format = DisplayType.getNumberFormat( m_DisplayType );
        }

        //

        m_number = number;

        if( m_number == null ) {
            m_number = new BigDecimal( 0.0 );
        }

        //

        try {
            jbInit();
            finishSetup();
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"Calculator" + ex );
        }
    }    // Calculator

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     */

    public Calculator( Frame frame ) {
        this( frame,Msg.getMsg( Env.getCtx(),"Calculator" ),DisplayType.Number,null,null );
    }    // Calculator

    /**
     * Constructor de la clase ...
     *
     *
     * @param frame
     * @param number
     */

    public Calculator( Frame frame,BigDecimal number ) {
        this( frame,Msg.getMsg( Env.getCtx(),"Calculator" ),DisplayType.Number,null,number );
    }    // Calculator

    /** Descripción de Campos */

    private BigDecimal m_number;    // the current number

    /** Descripción de Campos */

    private String m_display = "";    // what is displayed

    /** Descripción de Campos */

    private int m_DisplayType;

    /** Descripción de Campos */

    private DecimalFormat m_format;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private boolean m_abort = true;

    /** Descripción de Campos */

    private boolean m_currencyOK = false;

    /** Descripción de Campos */

    private final static String OPERANDS = "/*-+%";

    /** Descripción de Campos */

    private char m_decimal = '.';

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Calculator.class );

    //

    /** Descripción de Campos */

    private JPanel mainPanel = new JPanel();

    /** Descripción de Campos */

    private JPanel displayPanel = new JPanel();

    /** Descripción de Campos */

    private BorderLayout mainLayout = new BorderLayout();

    /** Descripción de Campos */

    private JPanel keyPanel = new JPanel();

    /** Descripción de Campos */

    private JLabel display = new JLabel();

    /** Descripción de Campos */

    private BorderLayout displayLayout = new BorderLayout();

    /** Descripción de Campos */

    private JButton b7 = new JButton();

    /** Descripción de Campos */

    private JButton b8 = new JButton();

    /** Descripción de Campos */

    private JButton b9 = new JButton();

    /** Descripción de Campos */

    private JButton b4 = new JButton();

    /** Descripción de Campos */

    private JButton b5 = new JButton();

    /** Descripción de Campos */

    private JButton b6 = new JButton();

    /** Descripción de Campos */

    private JButton b1 = new JButton();

    /** Descripción de Campos */

    private JButton b2 = new JButton();

    /** Descripción de Campos */

    private JButton b3 = new JButton();

    /** Descripción de Campos */

    private GridLayout keyLayout = new GridLayout();

    /** Descripción de Campos */

    private JButton bCur = new JButton();

    /** Descripción de Campos */

    private JButton bC = new JButton();

    /** Descripción de Campos */

    private JButton bDiv = new JButton();

    /** Descripción de Campos */

    private JButton bM = new JButton();

    /** Descripción de Campos */

    private JButton bMin = new JButton();

    /** Descripción de Campos */

    private JButton bProc = new JButton();

    /** Descripción de Campos */

    private JButton bAC = new JButton();

    /** Descripción de Campos */

    private JButton bResult = new JButton();

    /** Descripción de Campos */

    private JButton bDec = new JButton();

    /** Descripción de Campos */

    private JButton b0 = new JButton();

    /** Descripción de Campos */

    private JButton bPlus = new JButton();

    /** Descripción de Campos */

    private JPanel bordPanel = new JPanel();

    /** Descripción de Campos */

    private JPanel currencyPanel = new JPanel();

    /** Descripción de Campos */

    private BorderLayout bordLayout = new BorderLayout();

    /** Descripción de Campos */

    private JComboBox curFrom = new JComboBox();

    /** Descripción de Campos */

    private JComboBox curTo = new JComboBox();

    /** Descripción de Campos */

    private JLabel curLabel = new JLabel();

    /** Descripción de Campos */

    private FlowLayout currencyLayout = new FlowLayout();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */

    void jbInit() throws Exception {
        mainPanel.setLayout( mainLayout );
        displayPanel.setLayout( displayLayout );
        keyPanel.setLayout( keyLayout );
        mainLayout.setHgap( 2 );
        mainLayout.setVgap( 2 );
        mainPanel.setBorder( BorderFactory.createLoweredBevelBorder());
        mainPanel.addKeyListener( this );
        display.setBackground( Color.white );
        display.setFont( new java.awt.Font( "SansSerif",0,14 ));
        display.setBorder( BorderFactory.createLoweredBevelBorder());
        display.setText( "0" );
        display.setHorizontalAlignment( SwingConstants.RIGHT );
        b7.setText( "7" );
        b8.setText( "8" );
        b9.setText( "9" );
        b4.setText( "4" );
        b5.setText( "5" );
        b6.setText( "6" );
        b1.setText( "1" );
        b2.setText( "2" );
        b3.setText( "3" );
        keyLayout.setColumns( 5 );
        keyLayout.setHgap( 3 );
        keyLayout.setRows( 4 );
        keyLayout.setVgap( 3 );
        bCur.setForeground( Color.yellow );
        bCur.setToolTipText( Msg.getMsg( Env.getCtx(),"CurrencyConversion" ));
        bCur.setText( "$" );
        bC.setForeground( Color.red );
        bC.setText( "C" );
        bDiv.setForeground( Color.blue );
        bDiv.setText( "/" );
        bM.setForeground( Color.blue );
        bM.setText( "*" );
        bMin.setForeground( Color.blue );
        bMin.setText( "-" );
        bProc.setForeground( Color.blue );
        bProc.setText( "%" );
        bAC.setForeground( Color.red );
        bAC.setText( "AC" );
        bResult.setForeground( Color.green );
        bResult.setText( "=" );
        bDec.setText( "." );
        b0.setText( "0" );
        bPlus.setForeground( Color.blue );
        bPlus.setText( "+" );
        bordPanel.setLayout( bordLayout );
        curLabel.setHorizontalAlignment( SwingConstants.CENTER );
        curLabel.setHorizontalTextPosition( SwingConstants.CENTER );
        curLabel.setText( " >> " );
        currencyPanel.setLayout( currencyLayout );
        bordLayout.setHgap( 2 );
        bordLayout.setVgap( 2 );
        displayLayout.setHgap( 2 );
        displayLayout.setVgap( 2 );
        currencyLayout.setHgap( 3 );
        currencyLayout.setVgap( 2 );
        displayPanel.setBackground( Color.white );
        getContentPane().add( mainPanel );
        mainPanel.add( displayPanel,BorderLayout.NORTH );
        displayPanel.add( display,BorderLayout.CENTER );
        mainPanel.add( bordPanel,BorderLayout.CENTER );
        bordPanel.add( currencyPanel,BorderLayout.NORTH );
        currencyPanel.add( curFrom,null );
        currencyPanel.add( curLabel,null );
        currencyPanel.add( curTo,null );
        bordPanel.add( keyPanel,BorderLayout.CENTER );
        keyPanel.add( bAC,null );
        keyPanel.add( b7,null );
        keyPanel.add( b8,null );
        keyPanel.add( b9,null );
        keyPanel.add( bM,null );
        keyPanel.add( bC,null );
        keyPanel.add( b4,null );
        keyPanel.add( b5,null );
        keyPanel.add( b6,null );
        keyPanel.add( bDiv,null );
        keyPanel.add( bProc,null );
        keyPanel.add( b1,null );
        keyPanel.add( b2,null );
        keyPanel.add( b3,null );
        keyPanel.add( bMin,null );
        keyPanel.add( bCur,null );
        keyPanel.add( b0,null );
        keyPanel.add( bDec,null );
        keyPanel.add( bResult,null );
        keyPanel.add( bPlus,null );
    }    // jbInit

    /**
     * Descripción de Método
     *
     */

    private void finishSetup() {
        Insets in = new Insets( 2,2,2,2 );

        // For all buttons

        Component[] comp = keyPanel.getComponents();

        for( int i = 0;i < comp.length;i++ ) {
            if( comp[ i ] instanceof JButton ) {
                JButton b = ( JButton )comp[ i ];

                b.setMargin( in );
                b.addActionListener( this );
                b.addKeyListener( this );
            }
        }

        // Currency

        toggleCurrency();

        // Format setting

        m_decimal = m_format.getDecimalFormatSymbols().getDecimalSeparator();

        // display start number

        m_display = m_format.format( m_number );
        display.setText( m_display );
    }    // finishSetup

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void actionPerformed( ActionEvent e ) {

        // Handle Button input

        if( e.getSource() instanceof JButton ) {
            String cmd = e.getActionCommand();

            if( (cmd != null) && (cmd.length() > 0) ) {
                handleInput( cmd.charAt( 0 ));
            }
        }

        // Convert Amount

        else if( e.getSource() == curTo ) {
            KeyNamePair p         = ( KeyNamePair )curFrom.getSelectedItem();
            int         curFromID = p.getKey();

            p = ( KeyNamePair )curTo.getSelectedItem();

            int curToID = p.getKey();

            // convert

            int AD_Client_ID = Env.getAD_Client_ID( Env.getCtx());
            int AD_Org_ID    = Env.getAD_Org_ID( Env.getCtx());

            m_number = MConversionRate.convert( Env.getCtx(),evaluate(),curFromID,curToID,AD_Client_ID,AD_Org_ID );
            m_display = m_format.format( m_number );
            display.setText( m_display );
            curFrom.setSelectedItem( p );
        }
    }    // actionPerformed

    /**
     * Descripción de Método
     *
     *
     * @param c
     */

    public void handleInput( char c ) {

        // System.out.println("Input: " + c);

        switch( c ) {

        // Number          ===============================

        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            m_display += c;

            break;

        // Decimal         ===============================

        case '.':
        case ',':
            m_display += m_decimal;

            break;

        // Commands        ===============================

        case '/':
        case '*':
        case '-':
        case '+':
        case '%':
            if( m_display.length() > 1 ) {
                char last = m_display.charAt( m_display.length() - 1 );

                if( OPERANDS.indexOf( last ) == -1 ) {
                    m_display += c;
                } else {
                    m_display = m_display.substring( 0,m_display.length() - 1 ) + c;
                }
            }

            m_display = m_format.format( evaluate());

            if( c != '%' ) {
                m_display += c;
            }

            break;

        // Clear last char

        case 'C':
            if( m_display.length() > 0 ) {
                m_display = m_display.substring( 0,m_display.length() - 1 );
            }

            break;

        // Clear all

        case 'A':
            m_display = "";

            break;

        // Currency convert toggle

        case '$':
            m_display = m_format.format( evaluate());
            toggleCurrency();

            break;

        // fini

        case '=':
            m_display = m_format.format( evaluate());
            m_abort   = false;
            dispose();

            break;

        // Error           ===============================

        default:
            ADialog.beep();

            break;
        }    // switch

        if( m_display.equals( "" )) {
            m_display = "0";
        }

        // Eliminate leading zeroes

        if( (m_display.length() > 1) && m_display.startsWith( "0" )) {
            if( (m_display.charAt( 1 ) != ',') && (m_display.charAt( 1 ) != '.') ) {
                m_display = m_display.substring( 1 );
            }
        }

        // Display it

        display.setText( m_display );
    }    // handleInput

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private BigDecimal evaluate() {

        // nothing or zero

        if( (m_display == null) || m_display.equals( "" ) || m_display.equals( "0" )) {
            m_number = new BigDecimal( 0.0 );

            return m_number;
        }

        StringTokenizer st = new StringTokenizer( m_display,OPERANDS,true );

        // first token

        String token = st.nextToken();

        // do we have a negative number ?

        if( token.equals( "-" )) {
            if( st.hasMoreTokens()) {
                token += st.nextToken();
            } else {
                m_number = new BigDecimal( 0.0 );

                return m_number;
            }
        }

        // First Number

        Number firstNumber;

        try {
            firstNumber = m_format.parse( token );
        } catch( ParseException pe1 ) {
            log.log( Level.SEVERE,"Calculator.evaluate - token: " + token,pe1 );
            m_number = new BigDecimal( 0.0 );

            return m_number;
        }

        BigDecimal firstNo = new BigDecimal( firstNumber.toString());

        // intermediate result

        m_number = firstNo;

        // only one number

        if( !st.hasMoreTokens()) {
            return m_number;
        }

        // now we should get an operand

        token = st.nextToken();

        if( OPERANDS.indexOf( token ) == -1 ) {
            log.log( Level.SEVERE,"Calculator.evaluate - Unknown token: " + token );

            return m_number;
        }

        // get operand

        char op = token.charAt( 0 );

        // no second number

        if( !st.hasMoreTokens()) {
            return m_number;
        }

        token = st.nextToken();

        Number secondNumber;

        try {
            secondNumber = m_format.parse( token );
        } catch( ParseException pe2 ) {
            log.log( Level.SEVERE,"Calculator.evaluate - token: " + token,pe2 );
            m_number = new BigDecimal( 0.0 );

            return m_number;
        }

        BigDecimal secondNo = new BigDecimal( secondNumber.toString());

        // Check the next operant

        char op2 = 0;

        if( st.hasMoreTokens()) {
            token = st.nextToken();

            if( OPERANDS.indexOf( token ) == -1 ) {
                log.log( Level.SEVERE,"Calculator.evaluate - Unknown token: " + token );

                return m_number;
            }

            // get operand

            op2 = token.charAt( 0 );
        }

        // Percent operation

        if( op2 == '%' ) {
            secondNo = firstNo.multiply( secondNo ).divide( new BigDecimal( 100.0 ),m_format.getMaximumFractionDigits(),BigDecimal.ROUND_HALF_UP );
        }

        switch( op ) {
        case '/':
            m_number = firstNo.divide( secondNo,m_format.getMaximumFractionDigits(),BigDecimal.ROUND_HALF_UP );

            break;
        case '*':
            m_number = firstNo.multiply( secondNo );

            break;
        case '-':
            m_number = firstNo.subtract( secondNo );

            break;
        case '+':
            m_number = firstNo.add( secondNo );

            break;
        default:
            break;
        }

        return m_number.setScale( m_format.getMaximumFractionDigits(),BigDecimal.ROUND_HALF_UP );
    }    // evaluate

    /**
     * Descripción de Método
     *
     */

    private void toggleCurrency() {
        if( currencyPanel.isVisible()) {
            currencyPanel.setVisible( false );
        } else {
            if( !m_currencyOK ) {
                loadCurrency();
            }

            currencyPanel.setVisible( true );
        }

        pack();
    }    // toggleCurrency

    /**
     * Descripción de Método
     *
     */

    private void loadCurrency() {

        // Get Default

        int C_Currency_ID = Env.getContextAsInt( Env.getCtx(),m_WindowNo,"C_Currency_ID" );

        if( C_Currency_ID == 0 ) {
            C_Currency_ID = Env.getContextAsInt( Env.getCtx(),"$C_Currency_ID" );
        }

        String sql = "SELECT C_Currency_ID, ISO_Code FROM C_Currency " + "WHERE IsActive='Y' ORDER BY 2";
        KeyNamePair defaultValue = null;

        try {
            Statement stmt = DB.createStatement();
            ResultSet rs   = stmt.executeQuery( sql );

            while( rs.next()) {
                int         id = rs.getInt( "C_Currency_ID" );
                String      s  = rs.getString( "ISO_Code" );
                KeyNamePair p  = new KeyNamePair( id,s );

                curFrom.addItem( p );
                curTo.addItem( p );

                // Default

                if( id == C_Currency_ID ) {
                    defaultValue = p;
                }
            }

            rs.close();
            stmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"Calculator.loadCurrency",e );
        }

        // Set Defaults

        if( defaultValue != null ) {
            curFrom.setSelectedItem( defaultValue );
            curTo.setSelectedItem( defaultValue );
        }

        // Set Listener

        curTo.addActionListener( this );
        m_currencyOK = true;
    }    // loadCurrency

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getNumber() {
        if( m_abort ) {
            return null;
        }

        return m_number;
    }    // getNumber

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyPressed( KeyEvent e ) {

    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyTyped( KeyEvent e ) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */

    public void keyReleased( KeyEvent e ) {
        // sequence:   pressed - typed(no KeyCode) - released

        char input = e.getKeyChar();
        int  code  = e.getKeyCode();

        e.consume();    // does not work on JTextField

        if( code == KeyEvent.VK_DELETE ) {
            input = 'A';
        } else if( code == KeyEvent.VK_BACK_SPACE ) {
            input = 'C';
        } else if( code == KeyEvent.VK_ENTER ) {
            input = '=';
            // abort

        } else if( (code == KeyEvent.VK_CANCEL) || (code == KeyEvent.VK_ESCAPE) ) {
            m_abort = true;
            dispose();

            return;
        }

        handleInput( input );
    }
}    // Calculator



/*
 *  @(#)Calculator.java   02.07.07
 * 
 *  Fin del fichero Calculator.java
 *  
 *  Versión 2.2
 *
 */
