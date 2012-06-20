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



package org.openXpertya.wstore;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openXpertya.model.MBPBankAccount;
import org.openXpertya.model.MBPartner;
import org.openXpertya.model.MBPartnerLocation;
import org.openXpertya.model.MLocation;
import org.openXpertya.model.MRefList;
import org.openXpertya.model.MUser;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.WebUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class WebUser {

    /**
     * Descripción de Método
     *
     *
     * @param request
     *
     * @return
     */

    public static WebUser get( HttpServletRequest request ) {
        HttpSession session = request.getSession( false );

        if( session == null ) {
            return null;
        }

        return( WebUser )session.getAttribute( WebUser.NAME );
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param email
     *
     * @return
     */

    public static WebUser get( Properties ctx,String email ) {
        return get( ctx,email,null,true );
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param email
     * @param password
     * @param useCache
     *
     * @return
     */

    public static WebUser get( Properties ctx,String email,String password,boolean useCache ) {
        if( !useCache ) {
            s_cache = null;
        }

        if( (s_cache != null) && (email != null) && email.equals( s_cache.getEmail())) {

            // if password is null, don't check it

            if( (password == null) || password.equals( s_cache.getPassword())) {
                return s_cache;
            }

            s_cache.setPasswordOK( false,null );

            return s_cache;
        }

        s_cache = new WebUser( ctx,email,password );

        return s_cache;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_User_ID
     *
     * @return
     */

    public static WebUser get( Properties ctx,int AD_User_ID ) {
        if( (s_cache != null) && (s_cache.getAD_User_ID() == AD_User_ID) ) {
            return s_cache;
        }

        s_cache = new WebUser( ctx,AD_User_ID,null );

        return s_cache;
    }    // get

    /** Descripción de Campos */

    private static WebUser s_cache = null;

    /** Descripción de Campos */

    public static final String NAME = "webUser";

    /** Descripción de Campos */

    private CLogger log = CLogger.getCLogger( getClass());

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param email
     * @param password
     */

    private WebUser( Properties ctx,String email,String password ) {
        m_ctx          = ctx;
        m_AD_Client_ID = Env.getAD_Client_ID( ctx );
        load( email,password );
    }    // WebUser

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_User_ID
     * @param trxName
     */

    private WebUser( Properties ctx,int AD_User_ID,String trxName ) {
        m_ctx          = ctx;
        m_AD_Client_ID = Env.getAD_Client_ID( ctx );
        load( AD_User_ID );
    }    // WebUser

    /** Descripción de Campos */

    private Properties m_ctx;

    //

    /** Descripción de Campos */

    private MBPartner m_bp;

    /** Descripción de Campos */

    private MUser m_bpc;

    /** Descripción de Campos */

    private MBPartnerLocation m_bpl;

    /** Descripción de Campos */

    private MLocation m_loc;

    //

    /** Descripción de Campos */

    private boolean m_passwordOK = false;

    /** Descripción de Campos */

    private String m_passwordMessage;

    /** Descripción de Campos */

    private String m_saveErrorMessage;

    //

    /** Descripción de Campos */

    private int m_AD_Client_ID = 0;

    /** Descripción de Campos */

    private boolean m_loggedIn = false;

    /**
     * Descripción de Método
     *
     *
     * @param email
     * @param password
     */

    private void load( String email,String password ) {
        log.info( "load -" + email + "- AD_Client_ID=" + m_AD_Client_ID );

        String sql = "SELECT * " + "FROM AD_User " + "WHERE AD_Client_ID=?" + " AND TRIM(EMail)=?";

        if( email == null ) {
            email = "";
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_AD_Client_ID );
            pstmt.setString( 2,email.trim());

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_bpc = new MUser( m_ctx,rs,null );
                log.fine( "load - found BPC=" + m_bpc );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"load",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        // Check Password

        m_passwordOK = false;

        if( (m_bpc != null) && (password != null) && password.equals( m_bpc.getPassword())) {
            m_passwordOK = true;
        }

        if( m_passwordOK || (m_bpc == null) ) {
            m_passwordMessage = null;
        } else {
            setPasswordOK( false,password );
        }

        // Load BPartner

        if( m_bpc != null ) {
            m_bp = new MBPartner( m_ctx,m_bpc.getC_BPartner_ID(),null );
            log.fine( "load - found BP=" + m_bp );
        } else {
            m_bp = null;
        }

        // Load Loacation

        if( m_bpc != null ) {
            if( m_bpc.getC_BPartner_Location_ID() != 0 ) {
                m_bpl = new MBPartnerLocation( m_ctx,m_bpc.getC_BPartner_Location_ID(),null );
                log.fine( "load - found BPL=" + m_bpl );
            } else {
                MBPartnerLocation[] bpls = m_bp.getLocations( false );

                if( (bpls != null) && (bpls.length > 0) ) {
                    m_bpl = bpls[ 0 ];
                    log.fine( "load - found BPL=" + m_bpl );
                }
            }

            if( m_bpl != null ) {
                m_loc = MLocation.get( m_ctx,m_bpl.getC_Location_ID(),null );
                log.fine( "load - found LOC=" + m_loc );
            } else {
                m_loc = null;
            }
        } else {
            m_bpl = null;
            m_loc = null;
        }

        // Make sure that all entities exist

        if( m_bpc == null ) {
            m_bpc = new MUser( m_ctx,0,null );
            m_bpc.setEMail( email );
            m_bpc.setPassword( password );
        }

        if( m_bp == null ) {
            m_bp = new MBPartner( m_ctx );    // template
            m_bp.setIsCustomer( true );
        }

        if( m_bpl == null ) {
            m_bpl = new MBPartnerLocation( m_bp );
        }

        if( m_loc == null ) {
            m_loc = new MLocation( m_ctx,0,null );
        }

        //

        log.info( "load complete - " + m_bp + " - " + m_bpc );
    }    // load

    /**
     * Descripción de Método
     *
     *
     * @param AD_User_ID
     */

    private void load( int AD_User_ID ) {
        log.info( "load ID=" + AD_User_ID + ", AD_Client_ID=" + m_AD_Client_ID );

        String sql = "SELECT * " + "FROM AD_User " + "WHERE AD_Client_ID=?" + " AND AD_User_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,m_AD_Client_ID );
            pstmt.setInt( 2,AD_User_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_bpc = new MUser( m_ctx,rs,null );
                log.fine( "load = found BPC=" + m_bpc );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"load",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        // Password not entered

        m_passwordOK = false;
        m_loggedIn   = false;

        // Load BPartner

        if( m_bpc != null ) {
            m_bp = new MBPartner( m_ctx,m_bpc.getC_BPartner_ID(),null );
            log.fine( "load = found BP=" + m_bp );
        } else {
            m_bp = null;
        }

        // Load Loacation

        if( m_bpc != null ) {
            if( m_bpc.getC_BPartner_Location_ID() != 0 ) {
                m_bpl = new MBPartnerLocation( m_ctx,m_bpc.getC_BPartner_Location_ID(),null );
                log.fine( "load = found BPL=" + m_bpl );
            } else {
                MBPartnerLocation[] bpls = m_bp.getLocations( false );

                if( (bpls != null) && (bpls.length > 0) ) {
                    m_bpl = bpls[ 0 ];
                    log.fine( "load = found BPL=" + m_bpl );
                }
            }

            if( m_bpl != null ) {
                m_loc = MLocation.get( m_ctx,m_bpl.getC_Location_ID(),null );
                log.fine( "load = found LOC=" + m_loc );
            } else {
                m_loc = null;
            }
        } else {
            m_bpl = null;
            m_loc = null;
        }

        // Make sure that all entities exist

        if( m_bpc == null ) {
            m_bpc = new MUser( m_ctx,0,null );
            m_bpc.setEMail( "?" );
            m_bpc.setPassword( "?" );
        }

        if( m_bp == null ) {
            m_bp = new MBPartner( m_ctx );    // template
            m_bp.setIsCustomer( true );
        }

        if( m_bpl == null ) {
            m_bpl = new MBPartnerLocation( m_bp );
        }

        if( m_loc == null ) {
            m_loc = new MLocation( m_ctx,0,null );
        }

        //

        log.info( "load = " + m_bp + " - " + m_bpc );
    }    // load

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isValid() {
        if( m_bpc == null ) {
            return false;
        }

        boolean ok = m_bpc.getAD_User_ID() != 0;

        return ok;
    }    // isValid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEMailValid() {
        if( (m_bpc == null) ||!WebUtil.exists( getEmail())) {
            log.fine( getEmail() + ", bpc=" + m_bpc );

            return false;
        }

        //

        boolean ok = (m_bpc.getAD_User_ID() != 0) && m_bpc.isOnline() && m_bpc.isEMailValid();

        if( !ok ) {
            log.fine( getEmail() + ", ID=" + m_bpc.getAD_User_ID() + ", Online=" + m_bpc.isOnline() + ", EMailValid=" + m_bpc.isEMailValid());
        }

        return ok;
    }    // isEMailValid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean setEMailValid() {
        if( isEMailValid()) {
            return true;
        }

        //m_passwordMessage = "EMail invalid";
        m_passwordMessage = "Correo electr\u00f3nico no v\u00e1lido";

        return false;
    }    // setEMailValid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEMailVerified() {
        return (m_bpc != null) && m_bpc.isEMailVerified();
    }    // isEMailVerified

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "WebUser[" );

        sb.append( getEmail()).append( ",LoggedIn=" ).append( m_loggedIn ).append( "," ).append( m_bpc ).append( ",PasswordOK=" ).append( m_passwordOK ).append( ",Valid=" ).append( isValid()).append( " - " ).append( m_bp ).append( "Customer=" ).append( isCustomer()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean save() {
        m_saveErrorMessage = null;
        log.info( "save - BP.Value=" + m_bp.getValue() + ", Name=" + m_bp.getName());

        try {

            // check if BPartner exists        ***********************************

            if( m_bp.getC_BPartner_ID() == 0 ) {
                String sql = "SELECT * FROM C_BPartner WHERE AD_Client_ID=? AND Value=?";
                PreparedStatement pstmt = null;

                try {
                    pstmt = DB.prepareStatement( sql );
                    pstmt.setInt( 1,m_AD_Client_ID );
                    pstmt.setString( 2,m_bp.getValue());

                    ResultSet rs = pstmt.executeQuery();

                    if( rs.next()) {
                        m_bp = new MBPartner( m_ctx,m_bpc.getC_BPartner_ID(),null );
                        log.fine( "save - BP loaded =" + m_bp );
                    }

                    rs.close();
                    pstmt.close();
                    pstmt = null;
                } catch( Exception e ) {
                    log.log( Level.SEVERE,"save-check",e );
                } finally {
                    try {
                        if( pstmt != null ) {
                            pstmt.close();
                        }
                    } catch( Exception e ) {
                    }

                    pstmt = null;
                }
            }

            // save BPartner                   ***************************************

            if( (m_bp.getName() == null) || (m_bp.getName().length() == 0) ) {
                m_bp.setName( m_bpc.getName());
            }

            if( (m_bp.getValue() == null) || (m_bp.getValue().length() == 0) ) {
                m_bp.setValue( m_bpc.getEMail());
            }

            log.fine( "save - BP=" + m_bp );

            if( !m_bp.save()) {
                m_saveErrorMessage = "Could not save Business Partner";

                return false;
            }

            // save Location                   ***************************************

            log.fine( "save - LOC=" + m_loc );
            m_loc.save();

            // save BP Location                ***************************************

            if( m_bpl.getC_BPartner_ID() != m_bp.getC_BPartner_ID()) {
                m_bpl.setC_BPartner_ID( m_bp.getC_BPartner_ID());
            }

            if( m_bpl.getC_Location_ID() != m_loc.getC_Location_ID()) {
                m_bpl.setC_Location_ID( m_loc.getC_Location_ID());
            }

            log.fine( "save - BPL=" + m_bpl );

            if( !m_bpl.save()) {
                m_saveErrorMessage = "Could not save Location";

                return false;
            }

            // save Contact                    ***************************************

            if( m_bpc.getC_BPartner_ID() != m_bp.getC_BPartner_ID()) {
                m_bpc.setC_BPartner_ID( m_bp.getC_BPartner_ID());
            }

            if( m_bpc.getC_BPartner_Location_ID() != m_bpl.getC_BPartner_Location_ID()) {
                m_bpc.setC_BPartner_Location_ID( m_bpl.getC_BPartner_Location_ID());
            }

            log.fine( "save - BPC=" + m_bpc );

            if( !m_bpc.save()) {
                m_saveErrorMessage = "Could not save Contact";

                return false;
            }
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"save",ex );
            m_saveErrorMessage = ex.toString();

            return false;
        }

        //

        return true;
    }    // save

    /**
     * Descripción de Método
     *
     *
     * @param msg
     */

    public void setSaveErrorMessage( String msg ) {
        m_saveErrorMessage = msg;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSaveErrorMessage() {
        return m_saveErrorMessage;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getEmail() {
        return m_bpc.getEMail();
    }

    /**
     * Descripción de Método
     *
     *
     * @param email
     */

    public void setEmail( String email ) {
        m_bpc.setEMail( email );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_bpc.getName();
    }

    /**
     * Descripción de Método
     *
     *
     * @param name
     */

    public void setName( String name ) {
        m_bpc.setName( name );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTitle() {
        return m_bpc.getTitle();
    }

    /**
     * Descripción de Método
     *
     *
     * @param title
     */

    public void setTitle( String title ) {
        m_bpc.setTitle( title );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPassword() {
        String pwd = m_bpc.getPassword();

        if( (pwd == null) || (pwd.length() == 0) ) {    // if no password use time
            pwd = String.valueOf( System.currentTimeMillis());
        }

        return pwd;
    }    // getPassword

    /**
     * Descripción de Método
     *
     */

    public void setPassword() {
        String pwd = m_bpc.getPassword();

        if( ( (pwd == null) || (pwd.length() == 0) )                                    // no password set
                && (m_bpc.getC_BPartner_ID() != 0) && (m_bpc.getAD_User_ID() != 0) )    // existing BPartner
                {
            pwd = String.valueOf( System.currentTimeMillis());
            m_bpc.setPassword( pwd );
            m_bpc.save();
        }
    }    // setPassword

    /**
     * Descripción de Método
     *
     *
     * @param password
     */

    public void setPassword( String password ) {
        if( (password == null) || (password.length() == 0) ) {
            //m_passwordMessage = "Enter Password";
            m_passwordMessage = "Introduzca la contrase\u00f1a";
        }

        m_bpc.setPassword( password );
    }    // setPassword

    /**
     * Descripción de Método
     *
     *
     * @param ok
     * @param password
     */

    private void setPasswordOK( boolean ok,String password ) {
        m_passwordOK = ok;

        if( ok ) {
            m_passwordMessage = null;
        } else if( (password == null) || (password.length() == 0) ) {
            m_passwordMessage = "Introduzca la contrase\u00f1a";
            //m_passwordMessage = "Enter Password";
        } else {
            //m_passwordMessage = "Invalid Password";
            m_passwordMessage = "Contrase\u00f1a no v\u00e1lida";
        }
    }    // setPasswordOK

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isPasswordOK() {
        if( (m_bpc == null) ||!WebUtil.exists( m_bpc.getPassword())) {
            return false;
        }

        return m_passwordOK;
    }    // isPasswordOK

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPasswordMessage() {
        return m_passwordMessage;
    }    // getPasswordMessage

    /**
     * Descripción de Método
     *
     *
     * @param passwordMessage
     */

    protected void setPasswordMessage( String passwordMessage ) {
        m_passwordMessage = passwordMessage;
    }    // setPasswordMessage

    /**
     * Descripción de Método
     *
     *
     * @param password
     *
     * @return
     */

    public boolean login( String password ) {
        m_loggedIn = isValid()                        // we have a contact
                     && WebUtil.exists( password )    // we have a password
                     && password.equals( getPassword());
        setPasswordOK( m_loggedIn,password );
        log.fine( "login - success=" + m_loggedIn );

        if( m_loggedIn ) {
            Env.setContext( m_ctx,"#AD_User_ID",getAD_User_ID());
        }

        return m_loggedIn;
    }    // isLoggedIn

    /**
     * Descripción de Método
     *
     */

    public void logout() {
        m_loggedIn = false;
    }    // isLoggedIn

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isLoggedIn() {
        return m_loggedIn;
    }    // isLoggedIn

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPhone() {
        return m_bpc.getPhone();
    }

    /**
     * Descripción de Método
     *
     *
     * @param phone
     */

    public void setPhone( String phone ) {
        m_bpc.setPhone( phone );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPhone2() {
        return m_bpc.getPhone2();
    }

    /**
     * Descripción de Método
     *
     *
     * @param phone2
     */

    public void setPhone2( String phone2 ) {
        m_bpc.setPhone2( phone2 );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getFax() {
        return m_bpc.getFax();
    }

    /**
     * Descripción de Método
     *
     *
     * @param fax
     */

    public void setFax( String fax ) {
        m_bpc.setFax( fax );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getBirthday() {
        return m_bpc.getBirthday();
    }

    /**
     * Descripción de Método
     *
     *
     * @param birthday
     */

    public void setBirthday( Timestamp birthday ) {
        m_bpc.setBirthday( birthday );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTaxID() {
        return m_bp.getTaxID();
    }

    /**
     * Descripción de Método
     *
     *
     * @param taxID
     */

    public void setTaxID( String taxID ) {
        m_bp.setTaxID( taxID );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Client_ID() {
        return m_bpc.getAD_Client_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_User_ID() {
        return m_bpc.getAD_User_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getContactID() {
        return getAD_User_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCompany() {
        return m_bp.getName();
    }

    /**
     * Descripción de Método
     *
     *
     * @param company
     */

    public void setCompany( String company ) {
        m_bp.setName( company );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_ID() {
        return m_bp.getC_BPartner_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_PriceList_ID() {
        return m_bp.getM_PriceList_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_Location_ID() {
        return m_bpl.getC_BPartner_Location_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getAddress() {
        return m_loc.getAddress1();
    }

    /**
     * Descripción de Método
     *
     *
     * @param address
     */

    public void setAddress( String address ) {
        m_loc.setAddress1( address );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getAddress2() {
        return m_loc.getAddress2();
    }

    /**
     * Descripción de Método
     *
     *
     * @param address2
     */

    public void setAddress2( String address2 ) {
        m_loc.setAddress2( address2 );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCity() {
        return m_loc.getCity();
    }

    /**
     * Descripción de Método
     *
     *
     * @param city
     */

    public void setCity( String city ) {
        m_loc.setCity( city );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPostal() {
        return m_loc.getPostal();
    }

    /**
     * Descripción de Método
     *
     *
     * @param postal
     */

    public void setPostal( String postal ) {
        m_loc.setPostal( postal );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getRegionName() {
        return m_loc.getRegionName( false );
    }

    /**
     * Descripción de Método
     *
     *
     * @param region
     */

    public void setRegionName( String region ) {
        m_loc.setRegionName( region );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Region_ID() {
        return m_loc.getC_Region_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getRegionID() {
        return String.valueOf( getC_Region_ID());
    }

    /**
     * Descripción de Método
     *
     *
     * @param C_Region_ID
     */

    public void setC_Region_ID( int C_Region_ID ) {
        m_loc.setC_Region_ID( C_Region_ID );
    }

    /**
     * Descripción de Método
     *
     *
     * @param C_Region_ID
     */

    public void setC_Region_ID( String C_Region_ID ) {
        try {
            if( (C_Region_ID == null) || (C_Region_ID.length() == 0) ) {
                setC_Region_ID( 0 );
            } else {
                setC_Region_ID( Integer.parseInt( C_Region_ID ));
            }
        } catch( Exception e ) {
            setC_Region_ID( 0 );
            log.log( Level.WARNING,"",e );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCountryName() {
        return m_loc.getCountryName();
    }

    /**
     * Descripción de Método
     *
     *
     * @param country
     */

    public void setCountryName( String country ) {
        log.warning( country + " Ignored - C_Country_ID=" + m_loc.getC_Country_ID());

        // m_loc.setCountryName(country);

    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Country_ID() {
        return m_loc.getC_Country_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCountryID() {
        return String.valueOf( getC_Country_ID());
    }

    /**
     * Descripción de Método
     *
     *
     * @param C_Country_ID
     */

    public void setC_Country_ID( int C_Country_ID ) {
        m_loc.setC_Country_ID( C_Country_ID );
    }

    /**
     * Descripción de Método
     *
     *
     * @param C_Country_ID
     */

    public void setC_Country_ID( String C_Country_ID ) {
        try {
            if( (C_Country_ID == null) || (C_Country_ID.length() == 0) ) {
                setC_Country_ID( 0 );
            } else {
                setC_Country_ID( Integer.parseInt( C_Country_ID ));
            }
        } catch( Exception e ) {
            setC_Country_ID( 0 );
            log.log( Level.WARNING,"setC_Country_ID",e );
        }
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEmployee() {
        return m_bp.isEmployee();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSalesRep() {
        return m_bp.isSalesRep();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCustomer() {
        return m_bp.isCustomer();
    }

    /**
     * Descripción de Método
     *
     *
     * @param isCustomer
     */

    public void setIsCustomer( boolean isCustomer ) {
        m_bp.setIsCustomer( isCustomer );
    } 
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isVendor() {
        return m_bp.isVendor();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getSalesRep_ID() {
        return m_bp.getSalesRep_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCreditStopHold() {
        return m_bp.isCreditStopHold();
    }    // isCreditStopHold

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isCreditCritical() {
        String status = m_bp.getSOCreditStatus();

        return MBPartner.SOCREDITSTATUS_CreditStop.equals( status ) || MBPartner.SOCREDITSTATUS_CreditHold.equals( status ) || MBPartner.SOCREDITSTATUS_CreditWatch.equals( status );
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSOCreditStatus() {
        return MRefList.getListName( m_ctx,MBPartner.SOCREDITSTATUS_AD_Reference_ID,m_bp.getSOCreditStatus());
    }    // getSOCreditStatus

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MBPBankAccount getBankAccount() {
        MBPBankAccount retValue = null;

        // Find Bank Account for exact User

        MBPBankAccount[] bas = m_bp.getBankAccounts( false );

        for( int i = 0;i < bas.length;i++ ) {
            if( (bas[ i ].getAD_User_ID() == getAD_User_ID()) && bas[ i ].isActive()) {
                retValue = bas[ i ];
            }
        }

        // create new

        if( retValue == null ) {
            retValue = new MBPBankAccount( m_ctx,m_bp,m_bpc,m_loc );
            retValue.setAD_User_ID( getAD_User_ID());
            retValue.save();
        }

        return retValue;
    }    // getBankAccount

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getEMailVerifyCode() {
        return m_bpc.getEMailVerifyCode();
    }

    /**
     * Descripción de Método
     *
     *
     * @param code
     * @param info
     */

    public void setEMailVerifyCode( String code,String info ) {
        if( m_bpc.setEMailVerifyCode( code,info )) {
            setPasswordMessage( null );
        } else {
            setPasswordMessage( "Invalid Code" );
        }

        m_bpc.save();
    }    // setEMailVerifyCode

	public String getEstadoCredito(){
		return MRefList.getListName(m_ctx, MBPartner.SOCREDITSTATUS_AD_Reference_ID, m_bp.getSOCreditStatus());
	}	//	getEstadoCredito
	
}    // WebUser



/*
 *  @(#)WebUser.java   12.10.07
 * 
 *  Fin del fichero WebUser.java
 *  
 *  Versión 2.2
 *
 */