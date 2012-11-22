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



package org.openXpertya.model;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.TimeUtil;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MUser extends X_AD_User {
	
    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_BPartner_ID
     *
     * @return
     */

    public static MUser[] getOfBPartner( Properties ctx,int C_BPartner_ID ) {
        ArrayList         list  = new ArrayList();
        String            sql   = "SELECT * FROM AD_User WHERE C_BPartner_ID=?";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,C_BPartner_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MUser( ctx,rs,null ));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MUser[] retValue = new MUser[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getOfBPartner

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param AD_User_ID
     *
     * @return
     */

    public static MUser get( Properties ctx,int AD_User_ID ) {
        Integer key      = new Integer( AD_User_ID );
        MUser   retValue = ( MUser )s_cache.get( key );

        if( retValue == null ) {
            retValue = new MUser( ctx,AD_User_ID,null );

            if( AD_User_ID == 0 ) {
                String trxName = null;

                retValue.load( trxName );    // load System Record
            }

            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     *
     * @return
     */

    public static MUser get( Properties ctx ) {
        return get( ctx,Env.getAD_User_ID( ctx ));
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param name
     * @param password
     *
     * @return
     */

    public static MUser get( Properties ctx,String name,String password ) {
        return get(ctx, name, password, true);
    }    // get
    
    
    public static MUser get( Properties ctx, String name, String password, boolean controlEmpty ) {
    	return get(ctx, name, password, controlEmpty, true);
    }    // get

	/**
	 * Obtiene un usuario determinado por nombre de usuario y password
	 * 
	 * @param ctx
	 *            contexto
	 * @param name
	 *            nombre de usuario o null caso que se obtenga solo por clave
	 * @param password
	 *            clave de usuario
	 * @param controlEmpty
	 *            true si se debe controlar que los valores de usuario y clave
	 *            no se pasen vacíos, false si no hay que verificar
	 * @param controlPasswordWithUser
	 *            true si se debe controlar que el password venga
	 *            obligatoriamente acompañado de la clave, false si no hay que
	 *            verificar
	 * @return usuario correspondiente a los datos parámetro, null caso que no
	 *         exista o no pase alguna de las validaciones descritas
	 */
    public static MUser get( Properties ctx, String name, String password, boolean controlEmpty, boolean controlPasswordWithUser ) {
    	boolean isUserName = !Util.isEmpty(name, true);
    	boolean isPassword = !Util.isEmpty(password, true);
    	
		if (controlEmpty && (!isUserName || !isPassword)) {
            s_log.severe( "Invalid Name/Password = " + name + "/" + password );
            return null;
        }

		if (controlPasswordWithUser && (isUserName && !isPassword)) {
            s_log.severe( "Invalid Name/Password = " + name + "/" + password );
            return null;
        }
		
        int    AD_Client_ID = Env.getAD_Client_ID( ctx );
        MUser  retValue     = null;
		StringBuffer sql = new StringBuffer("SELECT * FROM AD_User WHERE IsActive='Y' AND AD_Client_ID=?");
		if(isUserName){
			sql.append(" AND Name='").append(name).append("'");
		}
		if(isPassword){
			sql.append(" AND Password='").append(password).append("'");
		}
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString() );
            int i = 1;
            pstmt.setInt( i++,AD_Client_ID );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MUser( ctx,rs,null );

                if( rs.next()) {
                    s_log.warning( "More then one user with Name/Password = " + name );
                }
            } else {
                s_log.fine( "No record" );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql.toString(),e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return retValue;
    }    // get
    
    /**
     * Get users from client id
     * @param C_Client_ID client id
     * @return array of users of that client
     */
    public static MUser[] getOfClient(Properties ctx, String trxName){
    	ArrayList users = new ArrayList();
    	String sql = "SELECT * FROM ad_user WHERE ad_client_id = ?";
    	PreparedStatement pstmt = null;
    	
    	try{
    		pstmt = DB.prepareStatement( sql, trxName );
    		pstmt.setInt(1, Env.getAD_Client_ID(ctx));
    		
    		ResultSet rs = pstmt.executeQuery();
    		
    		while(rs.next()){
    			users.add(new MUser(ctx,rs,trxName));
    		}
    		rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        MUser[] retValue = new MUser[ users.size()];

        users.toArray( retValue );

        return retValue;

    }//getOfClient

	/**
	 * @return true si la clave expiró, false caso contrario
	 */
    public static boolean isPasswordExpired(Properties ctx, Integer userID, String trxName){
    	MClientInfo clientInfo = MClientInfo.get(ctx, Env.getAD_Client_ID(ctx), trxName);
    	if(!clientInfo.isPasswordExpirationActive()){
    		return false;
    	}
    	// Obtengo la fecha última en que el usuario cambió el password
		Object lastDate = DB
		.getSQLObject(
				PluginUtils.getPluginInstallerTrxName(),
				"SELECT lastpasswordchangedate FROM ad_user WHERE ad_user_id = ?",
				new Object[]{userID});
		// Le sumo los días de vencimiento
		Timestamp expirationDate = TimeUtil.addDays(new Timestamp(
				((Date) lastDate).getTime()), clientInfo
				.getPasswordExpirationDays());
		Timestamp today = Env.getDate();
		// Se compara con la fecha actual
		// Si la fecha de hoy es mayor o igual a la fecha resultante de la
		// suma de la cantidad de días de vencimiento, entonces la clave expiró
		if(today.compareTo(expirationDate) >= 0){
			return true;
		}
		return false;
    }

	/**
	 * Retorna la contraseña actual del usuario parámetro
	 * 
	 * @param ctx
	 *            contexto
	 * @param userID
	 *            id de usuario
	 * @param trxName
	 *            transacción actual
	 * @return el password del usuario actual
	 */
    public static String getCurrentPassword(Properties ctx, Integer userID, String trxName){
		return DB.getSQLValueString(trxName,
				"SELECT password FROM ad_user WHERE ad_user_id = ?", userID);
    }

    /** Descripción de Campos */

    static private CCache s_cache = new CCache( "AD_User",30,60 );

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MUser.class );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param AD_User_ID
     * @param trxName
     */

    public MUser( Properties ctx,int AD_User_ID,String trxName ) {
        super( ctx,AD_User_ID,trxName );    // 0 is also System

        if( AD_User_ID == 0 ) {
            setIsLDAPAuthorized( false );    // N
            setNotificationType( NOTIFICATIONTYPE_EMail );
        }
    }                                        // MUser

    /**
     * Constructor de la clase ...
     *
     *
     * @param partner
     */

    public MUser( MBPartner partner ) {
        this( partner.getCtx(),0,partner.get_TrxName());
        setClientOrg( partner );
        setC_BPartner_ID( partner.getC_BPartner_ID());
        setName( partner.getName());

        //

        setPassword( null );
        setDescription( null );
    }    // MUser

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MUser( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MUser

    /** Descripción de Campos */

    private MRole[] m_roles = null;

    /** Descripción de Campos */

    private int m_rolesAD_Org_ID = -1;

    /** Descripción de Campos */

    private Boolean m_isAdministrator = null;

    /**
     * Descripción de Método
     *
     *
     * @param description
     */

    public void addDescription( String description ) {
        if( (description == null) || (description.length() == 0) ) {
            return;
        }

        String descr = getDescription();

        if( (descr == null) || (descr.length() == 0) ) {
            setDescription( description );
        } else {
            setDescription( descr + " - " + description );
        }
    }    // addDescription

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MUser[" ).append( getID()).append( ",Name=" ).append( getName()).append( ",EMailUserID=" ).append( getEMailUser()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOnline() {
        if( (getEMail() == null) || (getPassword() == null) ) {
            return false;
        }

        return true;
    }    // isOnline

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public InternetAddress getInternetAddress() {
        String email = getEMail();

        if( (email == null) || (email.length() == 0) ) {
            return null;
        }

        try {
            InternetAddress ia = new InternetAddress( email,true );

            if( ia != null ) {
                ia.validate();    // throws AddressException
            }

            return ia;
        } catch( AddressException ex ) {
            log.warning( email + " - " + ex.getLocalizedMessage());
        }

        return null;
    }    // getInternetAddress

    /**
     * Descripción de Método
     *
     *
     * @param ia
     *
     * @return
     */

    private String validateEmail( InternetAddress ia ) {
        if( ia == null ) {
            return "NoEmail";
        }

        if( true ) {
            return null;
        }

        Hashtable env = new Hashtable();

        env.put( Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.dns.DnsContextFactory" );

        // env.put(Context.PROVIDER_URL, "dns://admin.openXpertya.org");

        try {
            DirContext ctx = new InitialDirContext( env );

            // Attributes atts = ctx.getAttributes("admin");

            Attributes atts = ctx.getAttributes( "dns://admin.openXpertya.org",new String[]{ "ES" } );
            NamingEnumeration en = atts.getAll();

            // NamingEnumeration en = ctx.list("openXpertya.org");

            while( en.hasMore()) {
                System.out.println( en.next());
            }

            /*  */

        } catch( Exception e ) {
            e.printStackTrace();

            return e.getLocalizedMessage();
        }

        return null;
    }    // validateEmail

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEMailValid() {
        return validateEmail( getInternetAddress()) == null;
    }    // isEMailValid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getEMailVerifyCode() {
        long code = getAD_User_ID() + getName().hashCode();

        return "C" + String.valueOf( Math.abs( code )) + "C";
    }    // getEMailValidationCode

    /**
     * Descripción de Método
     *
     *
     * @param code
     * @param info
     *
     * @return
     */

    public boolean setEMailVerifyCode( String code,String info ) {
        boolean ok = (code != null) && code.equals( getEMailVerifyCode());

        if( ok ) {
            setEMailVerifyDate( new Timestamp( System.currentTimeMillis()));
        } else {
            setEMailVerifyDate( null );
        }

        setEMailVerify( info );

        return ok;
    }    // setEMailValidationCode

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isEMailVerified() {
        return (getEMailVerifyDate() != null) && (getEMailVerify() != null) && (getEMailVerify().length() > 0);
    }    // isEMailVerified

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNotificationEMail() {
        String s = getNotificationType();

        return (s == null) || NOTIFICATIONTYPE_EMail.equals( s );
    }    // isNotificationEMail

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isNotificationNote() {
        String s = getNotificationType();

        return (s != null) && NOTIFICATIONTYPE_Notice.equals( s );
    }    // isNotificationNote

    /**
     * Descripción de Método
     *
     *
     * @param AD_Org_ID
     *
     * @return
     */

    public MRole[] getRoles( int AD_Org_ID ) {
        if( (m_roles != null) && (m_rolesAD_Org_ID == AD_Org_ID) ) {
            return m_roles;
        }

        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM AD_Role r " + "WHERE r.IsActive='Y'" + " AND EXISTS (SELECT * FROM AD_Role_OrgAccess ro" + " WHERE r.AD_Role_ID=ro.AD_Role_ID AND ro.IsActive='Y' AND ro.AD_Org_ID=?)" + " AND EXISTS (SELECT * FROM AD_User_Roles ur" + " WHERE r.AD_Role_ID=ur.AD_Role_ID AND ur.IsActive='Y' AND ur.AD_User_ID=?) " + "ORDER BY AD_Role_ID";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Org_ID );
            pstmt.setInt( 2,getAD_User_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRole( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,sql,e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        //

        m_rolesAD_Org_ID = AD_Org_ID;
        m_roles          = new MRole[ list.size()];
        list.toArray( m_roles );

        return m_roles;
    }    // getRoles

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isAdministrator() {
        if( m_isAdministrator == null ) {
            m_isAdministrator = Boolean.FALSE;

            MRole[] roles = getRoles( 0 );

            for( int i = 0;i < roles.length;i++ ) {
                if( roles[ i ].getAD_Role_ID() == 0 ) {
                    m_isAdministrator = Boolean.TRUE;

                    break;
                }
            }
        }

        return m_isAdministrator.booleanValue();
    }    // isAdministrator

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // New Address invalidates verification

        if( !newRecord && is_ValueChanged( "EMail" )) {
            setEMailVerifyDate( null );
        }
        
        // Validaciones sólo para usuarios de sistema
        if (isSystemAccess()){
        	// No se permiten dos nombres de usuario iguales
    		StringBuffer sql = new StringBuffer(
    				"SELECT count(*)::integer FROM ad_user WHERE name = '"
    						+ getName() + "' and ad_client_id = ?");
            if(!newRecord){
            	sql.append(" AND ad_user_id <> "+getID());
            }
            int sameUser = DB.getSQLValue(get_TrxName(), sql.toString(), getAD_Client_ID());
            if(sameUser > 0){
            	log.saveError("UserNameAlreadyInUse", "");
            	return false;
            }
            
            // Si no ingresó password, error
            if(Util.isEmpty(getPassword(), true)){
            	log.saveError("PasswordInvalidError", "");
            	return false;
            }
            
    		// Si la funcionalidad de claves únicas se encuentra activa entonces no
    		// se debe permitir cargar dos claves iguales para distintos usuarios
            // No se permiten dos nombres de usuario iguales
            sql = new StringBuffer("SELECT ad_user_id FROM ad_user WHERE password = ?");
            if(!newRecord){
            	sql.append(" AND ad_user_id <> "+getID());
            }
            int samePass = DB.getSQLValue(get_TrxName(), sql.toString(), getPassword());
            if(samePass >= 0){
            	log.saveError("PasswordInvalidError", "");
            	return false;
            }	
        }
        
        return true;
    }    // beforeSave
    
    @Override
    protected boolean afterSave(boolean newRecord, boolean success) {
		// Si cambió la clave y está activo , entonces seteo la última fecha de modificación a
		// la actual
    	String expirationActive = Env.getContext(getCtx(), "#PasswordExpirationActive");
    	if(expirationActive.equals("Y") && is_ValueChanged("Password")){
			DB.executeUpdate(
					"UPDATE ad_user SET lastpasswordchangedate = current_date WHERE ad_user_id = "
							+ getID(), get_TrxName());
    	}
    	return true;
    }
    
    public static void clearCache(){
    	s_cache.clear();
    }
    
}    // MUser



/*
 *  @(#)MUser.java   02.07.07
 * 
 *  Fin del fichero MUser.java
 *  
 *  Versión 2.2
 *
 */
