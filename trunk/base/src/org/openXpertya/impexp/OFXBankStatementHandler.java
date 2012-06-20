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



package org.openXpertya.impexp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openXpertya.model.MBankStatementLoader;
import org.openXpertya.util.Env;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public abstract class OFXBankStatementHandler extends DefaultHandler {

    /** Descripción de Campos */

    protected Stack m_context = new Stack();

    /** Descripción de Campos */

    protected MBankStatementLoader m_controller;

    /** Descripción de Campos */

    protected String m_errorMessage = "";

    /** Descripción de Campos */

    protected String m_errorDescription = "";

    /** Descripción de Campos */

    protected BufferedReader m_reader = null;

    /** Descripción de Campos */

    protected SAXParser m_parser;

    /** Descripción de Campos */

    protected boolean m_success = false;

    // private boolean m_valid = false;

    /** Descripción de Campos */

    protected StatementLine m_line;

    /** Descripción de Campos */

    protected String routingNo = "0";

    /** Descripción de Campos */

    protected String bankAccountNo = null;

    /** Descripción de Campos */

    protected String currency = null;

    /** Descripción de Campos */

    protected int HEADER_SIZE = 20;

    /** Descripción de Campos */

    protected boolean test = false;

    /** Descripción de Campos */

    protected Timestamp dateLastRun = null;

    /** Descripción de Campos */

    protected Timestamp statementDate = null;

    /** Descripción de Campos */

    public static final String XML_OFX_TAG = "OFX";

    /** Descripción de Campos */

    public static final String XML_SIGNONMSGSRSV2_TAG = "SIGNONMSGSRSV2";

    /** Descripción de Campos */

    public static final String XML_SIGNONMSGSRSV1_TAG = "SIGNONMSGSRSV1";

    /** Descripción de Campos */

    public static final String XML_SONRS_TAG = "SONRS";

    /** Descripción de Campos */

    public static final String XML_DTSERVER_TAG = "DTSERVER";

    /** Descripción de Campos */

    public static final String XML_USERKEY_TAG = "USERKEY";

    /** Descripción de Campos */

    public static final String XML_TSKEYEXPIRE_TAG = "TSKEYEXPIRE";

    /** Descripción de Campos */

    public static final String XML_LANGUAGE_TAG = "LANGUAGE";

    /** Descripción de Campos */

    public static final String XML_DTPROFUP_TAG = "DTPROFUP";

    /** Descripción de Campos */

    public static final String XML_STATUS_TAG = "STATUS";

    /** Descripción de Campos */

    public static final String XML_STMTRS_TAG = "STMTRS";

    /** Descripción de Campos */

    public static final String XML_CURDEF_TAG = "CURDEF";

    /** Descripción de Campos */

    public static final String XML_BANKACCTFROM_TAG = "BANKACCTFROM";

    /** Descripción de Campos */

    public static final String XML_BANKID_TAG = "BANKID";

    /** Descripción de Campos */

    public static final String XML_BRANCHID_TAG = "BRANCHID";

    /** Descripción de Campos */

    public static final String XML_ACCTID_TAG = "ACCTID";

    /** Descripción de Campos */

    public static final String XML_ACCTTYPE_TAG = "ACCTTYPE";

    /** Descripción de Campos */

    public static final String XML_ACCTTYPE2_TAG = "ACCTTYPE2";

    /** Descripción de Campos */

    public static final String XML_ACCTKEY_TAG = "ACCTKEY";

    /** Descripción de Campos */

    public static final String XML_BANKTRANLIST_TAG = "BANKTRANLIST";

    /** Descripción de Campos */

    public static final String XML_DTSTART_TAG = "DTSTART";

    /** Descripción de Campos */

    public static final String XML_DTEND_TAG = "DTEND";

    /** Descripción de Campos */

    public static final String XML_STMTTRN_TAG = "STMTTRN";

    /** Descripción de Campos */

    public static final String XML_TRNTYPE_TAG = "TRNTYPE";

    /** Descripción de Campos */

    public static final String XML_TRNAMT_TAG = "TRNAMT";

    /** Descripción de Campos */

    public static final String XML_DTPOSTED_TAG = "DTPOSTED";

    /** Descripción de Campos */

    public static final String XML_DTAVAIL_TAG = "DTAVAIL";

    /** Descripción de Campos */

    public static final String XML_FITID_TAG = "FITID";

    /** Descripción de Campos */

    public static final String XML_CHECKNUM_TAG = "CHECKNUM";

    /** Descripción de Campos */

    public static final String XML_CHKNUM_TAG = "CHKNUM";

    /** Descripción de Campos */

    public static final String XML_REFNUM_TAG = "REFNUM";

    /** Descripción de Campos */

    public static final String XML_MEMO_TAG = "MEMO";

    /** Descripción de Campos */

    public static final String XML_NAME_TAG = "NAME";

    /** Descripción de Campos */

    public static final String XML_PAYEEID_TAG = "PAYEEID";

    /** Descripción de Campos */

    public static final String XML_PAYEE_TAG = "PAYEE";

    /** Descripción de Campos */

    public static final String XML_LEDGERBAL_TAG = "LEDGERBAL";

    /** Descripción de Campos */

    public static final String XML_BALAMT_TAG = "BALAMT";

    /** Descripción de Campos */

    public static final String XML_DTASOF_TAG = "DTASOF";

    /** Descripción de Campos */

    public static final String XML_AVAILBAL_TAG = "AVAILBAL";

    /** Descripción de Campos */

    public static final String XML_MKTGINFO_TAG = "MKTGINFO";

    /**
     * Descripción de Método
     *
     *
     * @param controller
     *
     * @return
     */

    protected boolean init( MBankStatementLoader controller ) {
        boolean result = false;

        if( controller == null ) {
            m_errorMessage     = "ErrorInitializingParser";
            m_errorDescription = "ImportController is a null reference";

            return result;
        }

        this.m_controller = controller;

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            m_parser = factory.newSAXParser();
            result   = true;
        } catch( ParserConfigurationException e ) {
            m_errorMessage     = "ErrorInitializingParser";
            m_errorDescription = "Unable to configure SAX parser: " + e.getMessage();
        } catch( SAXException e ) {
            m_errorMessage     = "ErrorInitializingParser";
            m_errorDescription = "Unable to initialize SAX parser: " + e.getMessage();
        }

        return result;
    }    // init

    /**
     * Descripción de Método
     *
     *
     * @param is
     *
     * @return
     */

    protected boolean attachInput( InputStream is ) {
        boolean isOfx1 = true;
        boolean result = false;

        try {
            BufferedReader reader = new BufferedReader( new InputStreamReader( is ));

            reader.mark( HEADER_SIZE + 100 );

            String header = "";

            for( int i = 0;i < HEADER_SIZE;i++ ) {
                header = header + reader.readLine();
            }

            if(( header.indexOf( "<?OFX" ) != -1 ) || ( header.indexOf( "<?ofx" ) != -1 )) {
                isOfx1 = false;
            } else if(( header.indexOf( "<?XML" ) != -1 ) || ( header.indexOf( "<?xml" ) != -1 )) {
                isOfx1 = false;

                // deted specific OFX version

            } else {
                isOfx1 = true;

                // detect specific OFX version

            }

            reader.reset();

            if( isOfx1 ) {
                m_reader = new BufferedReader( new InputStreamReader( new OFX1ToXML( reader )));
            } else {
                m_reader = reader;
            }

            result = true;
        } catch( IOException e ) {
            m_errorMessage     = "ErrorReadingData";
            m_errorDescription = e.getMessage();

            return result;
        }

        return result;
    }    // attachInput

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isValid() {
        boolean result = true;

        /*
         * try
         * {
         *       if (loadLines())
         *       {
         *               result = true;
         *               test = false;
         *       }
         *       m_reader.reset();
         * }
         * catch(IOException e)
         * {
         *       m_errorMessage = "ErrorReadingData";
         *       m_errorDescription = e.getMessage();
         * }
         */

        return result;
    }    // isValid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean importSuccessfull() {

        /*
         * Currently there are no checks after the statement lines are read.
         * Once all lines are read correctly a successfull import is assumed.
         */

        return m_success;
    }    // importSuccessfull

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean loadLines() {
        boolean result = false;

        try {
            m_parser.parse( new InputSource( m_reader ),this );
            result    = true;
            m_success = true;
        } catch( SAXException e ) {
            m_errorMessage     = "ErrorParsingData";
            m_errorDescription = e.getMessage();
        } catch( IOException e ) {
            m_errorMessage     = "ErrorReadingData";
            m_errorDescription = e.getMessage();
        }

        return result;
    }    // loadLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getDateLastRun() {
        return dateLastRun;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getRoutingNo() {
        return m_line.routingNo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getBankAccountNo() {
        return m_line.bankAccountNo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getStatementReference() {
        return m_line.statementReference;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStatementDate() {
        return statementDate;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getReference() {
        return m_line.reference;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStatementLineDate() {
        return m_line.statementLineDate;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getValutaDate() {
        return m_line.valutaDate;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTrxType() {
        return m_line.trxType;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean getIsReversal() {
        return m_line.isReversal;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCurrency() {
        return m_line.currency;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getStmtAmt() {
        return m_line.stmtAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getTrxAmt() {

        /*
         *  assume total amount = transaction amount
         * todo: detect interest & charge amount
         */

        return m_line.stmtAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getInterestAmt() {
        return Env.ZERO;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMemo() {
        return m_line.memo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getChargeName() {
        return m_line.chargeName;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getChargeAmt() {
        return m_line.chargeAmt;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTrxID() {
        return m_line.trxID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPayeeAccountNo() {
        return m_line.payeeAccountNo;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPayeeName() {
        return m_line.payeeName;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCheckNo() {
        return m_line.checkNo;
    }

    /**
     * Descripción de Método
     *
     *
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     *
     * @throws org.xml.sax.SAXException
     */

    public void startElement( String uri,String localName,String qName,Attributes attributes ) throws org.xml.sax.SAXException {
        boolean validOFX = true;

        /*
         * Currently no validating is being done, valid OFX structure is assumed.
         */

        if( validOFX ) {
            m_context.push( qName );
        } else {
            m_errorDescription = "Invalid OFX syntax: " + qName;

            throw new SAXException( "Invalid OFX syntax: " + qName );
        }

        if( qName.equals( XML_STMTTRN_TAG )) {
            m_line = new StatementLine( routingNo,bankAccountNo,currency );
        }
    }    // startElement

    /**
     * Descripción de Método
     *
     *
     * @param ch
     * @param start
     * @param length
     *
     * @throws SAXException
     */

    public void characters( char ch[],int start,int length ) throws SAXException {
        String XML_TAG = ( String )m_context.peek();
        String value   = new StringBuffer().append( ch,start,length ).toString();

        try {

            // Read statment level data

            /*
             * Default currency for this set of statement lines
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>
             */

            if( XML_TAG.equals( XML_CURDEF_TAG )) {
                currency = value;
            }

            /*
             *  Routing Number (or SWIFT Code) for this set of statement lines
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKACCTFROM>
             */

            else if( XML_TAG.equals( XML_BANKID_TAG )) {
                routingNo = value;
            }

            /*
             * Bank Account Number for this set of bank statement lines
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKACCTFROM>
             */

            else if( XML_TAG.equals( XML_ACCTID_TAG )) {
                bankAccountNo = value;
            }

            /*
             * Last date for this set of statement lines
             * This is the date that should be specified as the <DTSTART>
             * for the next batch of statement lines, in order not to miss any
             * transactions.
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST>
             */

            else if( XML_TAG.equals( XML_DTEND_TAG )) {
                dateLastRun = parseOfxDate( value );
            }

            /*
             *
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<AVAILBAL>
             */

            else if( XML_TAG.equals( XML_DTASOF_TAG )) {
                statementDate = parseOfxDate( value );
            }

            // Read statement line level data

            /*
             * Transaction type, e.g. DEBIT, CREDIT, SRVCHG
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST><STMTTRN>
             */

            else if( XML_TAG.equals( XML_TRNTYPE_TAG )) {
                m_line.trxType = value;
            }

            /*
             * Statement line date
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST><STMTTRN>
             */

            else if( XML_TAG.equals( XML_DTPOSTED_TAG )) {
                m_line.statementLineDate = parseOfxDate( value );
            }

            /*
             * Valuta date
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST><STMTTRN>
             */

            else if( XML_TAG.equals( XML_DTAVAIL_TAG )) {
                m_line.valutaDate = parseOfxDate( value );
            }

            /*
             * Total statement line amount
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST><STMTTRN>
             */

            else if( XML_TAG.equals( XML_TRNAMT_TAG )) {
                m_line.stmtAmt = new BigDecimal( value );
            }

            /*
             * Transaction Identification
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST><STMTTRN>
             */

            else if( XML_TAG.equals( XML_FITID_TAG )) {
                m_line.trxID = value;
            }

            /*
             * Check number for check transactions
             * CHECKNUM for generic OFX, CHKNUM for MS-Money OFC
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST><STMTTRN>
             */

            else if(( XML_TAG.equals( XML_CHECKNUM_TAG )) || ( XML_TAG.equals( XML_CHKNUM_TAG ))) {
                m_line.checkNo = value;
            }

            /*
             * Statement line reference
             * Additional transaction reference information
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST><STMTTRN>
             */

            else if( XML_TAG.equals( XML_REFNUM_TAG )) {
                m_line.reference = value;
            }

            /*
             * Transaction memo
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST><STMTTRN>
             */

            else if( XML_TAG.equals( XML_MEMO_TAG )) {
                m_line.memo = value;
            }

            /*
             * Payee Name
             * <OFX>-<BANKMSGSRSV2>-<STMTTRNRS>-<STMTRS>-<BANKTRANLIST><STMTTRN>
             */

            else if( XML_TAG.equals( XML_NAME_TAG )) {
                m_line.payeeName = value;
            }
        } catch( Exception e ) {
            m_errorDescription = "Invalid data: " + value + " <-> " + e.getMessage();

            throw new SAXException( "Invalid data: " + value );
        }
    }    // characters

    /**
     * Descripción de Método
     *
     *
     * @param uri
     * @param localName
     * @param qName
     *
     * @throws SAXException
     */

    public void endElement( String uri,String localName,String qName ) throws SAXException {
        if( qName.equals( m_context.peek())) {
            m_context.pop();
        } else {
            m_errorDescription = "Invalid XML syntax: " + qName;

            throw new SAXException( "Invalid XML syntax: " + qName );
        }

        if( qName.equals( XML_STMTTRN_TAG )) {
            if( !test ) {
                if( !m_controller.saveLine()) {
                    m_errorMessage     = m_controller.getErrorMessage();
                    m_errorDescription = m_controller.getErrorDescription();

                    throw new SAXException( m_errorMessage );
                }
            }
        }
    }    // endElement

    /**
     * Descripción de Método
     *
     *
     * @param value
     *
     * @return
     *
     * @throws ParseException
     */

    private Timestamp parseOfxDate( String value ) throws ParseException {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd" );

            sdf.setLenient( false );

            return new Timestamp( sdf.parse( value ).getTime());
        } catch( Exception e ) {
            throw new ParseException( "Error parsing date: " + value,0 );
        }
    }    // parseOfxDate

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getLastErrorMessage() {
        return m_errorMessage;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getLastErrorDescription() {
        return m_errorDescription;
    }

    /**
     * Descripción de Clase
     *
     *
     * @version    2.2, 12.10.07
     * @author     Equipo de Desarrollo de openXpertya    
     */

    class StatementLine {

        /** Descripción de Campos */

        protected String routingNo = null;

        /** Descripción de Campos */

        protected String bankAccountNo = null;

        /** Descripción de Campos */

        protected String statementReference = null;

        /** Descripción de Campos */

        protected Timestamp statementLineDate = null;

        /** Descripción de Campos */

        protected String reference = null;

        /** Descripción de Campos */

        protected Timestamp valutaDate;

        /** Descripción de Campos */

        protected String trxType = null;

        /** Descripción de Campos */

        protected boolean isReversal = false;

        /** Descripción de Campos */

        protected String currency = null;

        /** Descripción de Campos */

        protected BigDecimal stmtAmt = null;

        /** Descripción de Campos */

        protected String memo = null;

        /** Descripción de Campos */

        protected String chargeName = null;

        /** Descripción de Campos */

        protected BigDecimal chargeAmt = null;

        /** Descripción de Campos */

        protected String payeeAccountNo = null;

        /** Descripción de Campos */

        protected String payeeName = null;

        /** Descripción de Campos */

        protected String trxID = null;

        /** Descripción de Campos */

        protected String checkNo = null;

        /**
         * Constructor de la clase ...
         *
         *
         * @param routingNo
         * @param bankAccountNo
         * @param currency
         */

        public StatementLine( String routingNo,String bankAccountNo,String currency ) {
            this.bankAccountNo = bankAccountNo;
            this.routingNo     = routingNo;
            this.currency      = currency;
        }
    }
}    // OFXBankStatementHandler



/*
 *  @(#)OFXBankStatementHandler.java   02.07.07
 * 
 *  Fin del fichero OFXBankStatementHandler.java
 *  
 *  Versión 2.2
 *
 */
