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



package org.openXpertya.util;

import java.security.Principal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.openXpertya.OpenXpertya;
import org.openXpertya.db.CConnection;
import org.openXpertya.model.MClientInfo;
import org.openXpertya.model.MComponentVersion;
import org.openXpertya.model.MCountry;
import org.openXpertya.model.MOrgInfo;
import org.openXpertya.model.MRole;
import org.openXpertya.model.MTree_Base;
import org.openXpertya.model.ModelValidationEngine;
import org.openXpertya.reflection.CallResult;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */


/*
 * RP5. 
 * SportClub
 * dREHER jorge.dreher@gmail.com
 * Mar - 2013
 */


public class Login {

    /**
     * Descripción de Método
     *
     *
     * @param isClient
     *
     * @return
     */

    public static Properties initTest( boolean isClient ) {

        // logger.entering("Env", "initTest");

        OpenXpertya.startupEnvironment( true );

        // Test Context

        Properties    ctx   = Env.getCtx();
        Login         login = new Login( ctx );
        KeyNamePair[] roles = login.getRoles( CConnection.get(),"System","System",true );

        // load role

        if( (roles != null) && (roles.length > 0) ) {
            KeyNamePair[] clients = login.getClients( roles[ 0 ] );

            // load client

            if( (clients != null) && (clients.length > 0) ) {
                KeyNamePair[] orgs = login.getOrgs( clients[ 0 ] );

                // load org

                if( (orgs != null) && (orgs.length > 0) ) {
                    KeyNamePair[] whs = login.getWarehouses( orgs[ 0 ] );

                    //

                    login.loadPreferences( orgs[ 0 ],null,null,null );
                }
            }
        }

        //

        Env.setContext( ctx,"#Date","2000-01-01" );

        // logger.exiting("Env", "initTest");

        return ctx;
    }    // testInit

    /**
     * Descripción de Método
     *
     *
     * @param isClient
     *
     * @return
     */

    public static boolean isJavaOK( boolean isClient ) {

        // Java System version check

        String jVersion = System.getProperty( "java.version" );

        if( jVersion.startsWith( "1.6.0" ) || jVersion.startsWith( "1.5.0" ) || jVersion.startsWith( "1.4.2" )) {    // this release
            return true;
        }

        // Warning

        boolean ok = false;

        if( jVersion.startsWith( "1.6" ) || jVersion.startsWith( "1.4" ) || jVersion.startsWith( "1.5.1" )) {    // later/earlier release
            ok = true;
        }

        // Error Message

        StringBuffer msg = new StringBuffer();

        msg.append( System.getProperty( "java.vm.name" )).append( " - " ).append( jVersion );

        if( ok ) {
            msg.append( "(untested)" );
        }

        msg.append( "  <>  1.6.0" );

        //

        if( isClient ) {
            JOptionPane.showMessageDialog( null,msg.toString(),org.openXpertya.OpenXpertya.getName() + " - Java Version Check",ok
                    ?JOptionPane.WARNING_MESSAGE
                    :JOptionPane.ERROR_MESSAGE );
        } else {
            log.severe( msg.toString());
        }

        return ok;
    }    // isJavaOK

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     */

    public Login( Properties ctx ) {
        if( ctx == null ) {
            throw new IllegalArgumentException( "Context missing" );
        }

        m_ctx = ctx;
    }    // Login

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( Login.class );

    /** Descripción de Campos */

    private Properties m_ctx = null;

    /**
     * Descripción de Método
     *
     *
     * @param cc
     * @param app_user
     * @param app_pwd
     * @param force
     *
     * @return
     */

    protected KeyNamePair[] getRoles( CConnection cc,String app_user,String app_pwd,boolean force ) {

        // Establish connection

        DB.setDBTarget( cc );
        Env.setContext( m_ctx,"#Host",cc.getAppsHost());
        Env.setContext( m_ctx,"#Database",cc.getDbName());

        if( DB.getConnectionRO() == null ) {
            log.saveError( "NoDatabase","" );

            return null;
        }

        if( app_pwd == null ) {
            return null;
        }

        //

        return getRoles( app_user,app_pwd,force );
    }    // getRoles

    /**
     * Descripción de Método
     *
     *
     * @param app_user
     *
     * @return
     */

    public KeyNamePair[] getRoles( Principal app_user ) {
        if( app_user == null ) {
            return null;
        }

        // login w/o password as previously authorized

        return getRoles( app_user.getName(),null,false );
    }    // getRoles

    /**
     * Descripción de Método
     *
     *
     * @param app_user
     * @param app_pwd
     *
     * @return
     */

    public KeyNamePair[] getRoles( String app_user,String app_pwd ) {
        if( app_pwd == null ) {
            return null;
        }

        return getRoles( app_user,app_pwd,false );
    }    // login

    /**
     * Descripción de Método
     *
     *
     * @param app_user
     * @param app_pwd
     * @param force
     *
     * @return
     */

    private KeyNamePair[] getRoles( String app_user,String app_pwd,boolean force ) {
        log.info( "User=" + app_user );

        if( app_user == null ) {
            return null;
        }

        KeyNamePair[] retValue = null;
        ArrayList     list     = new ArrayList();

        //

        String sql = "SELECT AD_User.AD_User_ID, AD_User.Description," + " AD_Role.AD_Role_ID, AD_Role.Name " + "FROM AD_User, AD_User_Roles, AD_Role " + "WHERE AD_User.AD_User_ID=AD_User_Roles.AD_User_ID" + " AND AD_User_Roles.AD_Role_ID=AD_Role.AD_Role_ID" + " AND AD_User.Name=?"    // #1
                     + " AND AD_User.IsActive='Y' AND AD_Role.IsActive='Y' AND AD_User_Roles.IsActive='Y'";

        if( app_pwd != null ) {
            sql += " AND (AD_User.Password=? OR AD_User.Password=?)";    // #2/3
        }

        sql += " ORDER BY AD_Role.Name";

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setString( 1,app_user );

            if( app_pwd != null ) {
                pstmt.setString( 2,app_pwd );
                pstmt.setString( 3,Secure.getDigest( app_pwd ));
            }

            // execute a query
            log.fine("la sql es =" +sql);
            log.fine("ejecutando = " + pstmt);

            ResultSet rs = pstmt.executeQuery();

            if( !rs.next()) {    // no record found
                if( force ) {
                    Env.setContext( m_ctx,"#AD_User_Name","System" );
                    Env.setContext( m_ctx,"#AD_User_ID","0" );
                    Env.setContext( m_ctx,"#AD_User_Description","System Forced Login" );
                    Env.setContext( m_ctx,"#User_Level","S  " );    // Format 'SCO'
                    Env.setContext( m_ctx,"#User_Client","0" );     // Format c1, c2, ...
                    Env.setContext( m_ctx,"#User_Org","0" );    // Format o1, o2, ...
                    rs.close();
                    pstmt.close();
                    retValue = new KeyNamePair[]{ new KeyNamePair( 0,"System Administrator" )};
                    return retValue;
                }
                    rs.close();
                    pstmt.close();
                    log.saveError( "UserPwdError",app_user,false );

                    return null;
                
            }

            Env.setContext( m_ctx,"#AD_User_Name",app_user );
            Env.setContext( m_ctx,"#AD_User_ID",rs.getInt( "AD_User_ID" ));
            Env.setContext( m_ctx,"#SalesRep_ID",rs.getInt( "AD_User_ID" ));

            //

            Ini.setProperty( Ini.P_UID,app_user );

            if( Ini.getPropertyBool( Ini.P_STORE_PWD )) {
                Ini.setProperty( Ini.P_PWD,app_pwd );
            }

            do    // read all roles
            {
                int AD_Role_ID = rs.getInt( "AD_Role_ID" );

                if( AD_Role_ID == 0 ) {
                    Env.setContext( m_ctx,"#SysAdmin","Y" );
                }

                String      Name = rs.getString( "Name" );
                KeyNamePair p    = new KeyNamePair( AD_Role_ID,Name );

                list.add( p );
            } while( rs.next());

            rs.close();
            pstmt.close();
            pstmt = null;

            //

            retValue = new KeyNamePair[ list.size()];
            list.toArray( retValue );
            log.fine( "User=" + app_user + " - roles #" + retValue.length );
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getRoles",ex );
            log.saveError( "DBLogin",ex );
            retValue = null;
        }

        //

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        return retValue;
    }    // getRoles

    /**
     * Descripción de Método
     *
     *
     * @param role
     *
     * @return
     */

    public KeyNamePair[] getClients( KeyNamePair role ) {
        if( role == null ) {
            throw new IllegalArgumentException( "Role missing" );
        }

        // s_log.fine("loadClients - Role: " + role.toStringX());

        ArrayList     list     = new ArrayList();
        KeyNamePair[] retValue = null;
        String        sql      = "SELECT DISTINCT r.UserLevel,"                                                                                 // 1
                                 + " c.AD_Client_ID,c.Name "                                                                                    // 2/3
                                 + "FROM AD_Role r" + " INNER JOIN AD_Client c ON (r.AD_Client_ID=c.AD_Client_ID) " + "WHERE r.AD_Role_ID=?"    // #1
                                 + " AND r.IsActive='Y' AND c.IsActive='Y'";
        PreparedStatement pstmt = null;

        // get Role details

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,role.getKey());

            ResultSet rs = pstmt.executeQuery();

            if( !rs.next()) {
                rs.close();
                pstmt.close();
                log.log( Level.SEVERE,"No Clients for Role: " + role.toStringX());

                return null;
            }

            // Role Info

            Env.setContext( m_ctx,"#AD_Role_ID",role.getKey());
            Env.setContext( m_ctx,"#AD_Role_Name",role.getName());
            Ini.setProperty( Ini.P_ROLE,role.getName());

            // User Level

            Env.setContext( m_ctx,"#User_Level",rs.getString( 1 ));    // Format 'SCO'

            // load Clients

            do {
                int         AD_Client_ID = rs.getInt( 2 );
                String      Name         = rs.getString( 3 );
                KeyNamePair p            = new KeyNamePair( AD_Client_ID,Name );

                list.add( p );
            } while( rs.next());

            rs.close();
            pstmt.close();
            pstmt = null;

            //

            retValue = new KeyNamePair[ list.size()];
            list.toArray( retValue );
            log.fine( "Role: " + role.toStringX() + " - clients #" + retValue.length );
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"clients",ex );
            retValue = null;
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
    }    // getClients

    /*
     * dREHER, para mantener compatibilidad con el metodo actual agrego
     * sobrecarga sin parametro de seteo, por defecto setea variables de entorno,
     * pero si solo se necesita que devuelva lista de organizaciones con acceso de rol actual
     * se llama metodo con flag en false
     * 
     * jorge.dreher@gmail.com
     * 
     */
    
    public KeyNamePair[] getOrgs( KeyNamePair client ) {
    	
    	// Por defecto seteo variables de entorno tal cual trabaja ahora
    	return getOrgs(client, true);
    	
    }	
    
    
    
    /**
     * Descripción de Método
     *
     *
     * @param client
     *
     * @return
     */

    public KeyNamePair[] getOrgs( KeyNamePair client, boolean setContext ) {
        if( client == null ) {
            throw new IllegalArgumentException( "Client missing" );
        }

        if( Env.getContext( m_ctx,"#AD_Role_ID" ).length() == 0 ) {    // could be number 0
            throw new UnsupportedOperationException( "Missing Context #AD_Role_ID" );
        }

        int AD_Role_ID = Env.getContextAsInt( m_ctx,"#AD_Role_ID" );
        int AD_User_ID = Env.getContextAsInt( m_ctx,"#AD_User_ID" );

        // s_log.fine("Client: " + client.toStringX() + ", AD_Role_ID=" + AD_Role_ID);

        // get Client details for role

        ArrayList     list     = new ArrayList();
        KeyNamePair[] retValue = null;

        //

        String sql = "SELECT o.AD_Org_ID,o.Name,o.IsSummary "                                                                                                  // 1..3
                     + "FROM AD_Role r, AD_Client c" + " INNER JOIN AD_Org o ON (c.AD_Client_ID=o.AD_Client_ID OR o.AD_Org_ID=0) " + "WHERE r.AD_Role_ID=?"    // #1
                     + " AND c.AD_Client_ID=?"                                                                                                                                                                                                                                                                                                                                                 // #2
                     + " AND o.IsActive='Y'" + " AND (r.IsAccessAllOrgs='Y' " + "OR (r.IsUseUserOrgAccess='N' AND o.AD_Org_ID IN (SELECT AD_Org_ID FROM AD_Role_OrgAccess ra " + "WHERE ra.AD_Role_ID=r.AD_Role_ID AND ra.IsActive='Y')) " + "OR (r.IsUseUserOrgAccess='Y' AND o.AD_Org_ID IN (SELECT AD_Org_ID FROM AD_User_OrgAccess ua " + "WHERE ua.AD_User_ID=? AND ua.IsActive='Y'))"    // #3
                     + " AND o.IsSummary='N'"  // dREHER, para evitar loguearse con sucursal carpeta, aunque tenga acceso
                     + ") " + "ORDER BY o.Name";

        //

        PreparedStatement pstmt = null;
        MRole             role  = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,AD_Role_ID );
            pstmt.setInt( 2,client.getKey());
            pstmt.setInt( 3,AD_User_ID );

            ResultSet rs = pstmt.executeQuery();

            // load Orgs

            while( rs.next()) {
                int     AD_Org_ID = rs.getInt( 1 );
                String  Name      = rs.getString( 2 );
                boolean summary   = "Y".equals( rs.getString( 3 ));

                if( summary ) {
                    if( role == null ) {
                        role = MRole.get( m_ctx,AD_Role_ID,null );
                    }

                    getOrgsAddSummary( list,AD_Org_ID,Name,role );
                } else {
                    KeyNamePair p = new KeyNamePair( AD_Org_ID,Name );

                    if( !list.contains( p )) {
                        list.add( p );
                    }
                }
            }

            rs.close();
            pstmt.close();
            pstmt = null;

            //

            retValue = new KeyNamePair[ list.size()];
            list.toArray( retValue );
            log.fine( "Client: " + client.toStringX() + ", AD_Role_ID=" + AD_Role_ID + ", AD_User_ID=" + AD_User_ID + " - orgs #" + retValue.length );
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,sql,ex );
            retValue = null;
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // No Orgs

        if( (retValue == null) || (retValue.length == 0) ) {
            log.log( Level.WARNING,"No Org for Client: " + client.toStringX() + ", AD_Role_ID=" + AD_Role_ID + ", AD_User_ID=" + AD_User_ID );

            return null;
        }

        // Client Info

        // dREHER, setea entorno si el flag es true, por defecto es true ?
        if(setContext){

		    Env.setContext( m_ctx,"#AD_Client_ID",client.getKey());
		    Env.setContext( m_ctx,"#AD_Client_Name",client.getName());
		    Ini.setProperty( Ini.P_CLIENT,client.getName());
		    
		    MClientInfo clientInfo = MClientInfo.get(m_ctx, client.getKey());
		    
		    // Info de Cajas Diarias activas
			String isPOSJournalActive = clientInfo.isPOSJournalActive()?"Y":"N";
			Env.setContext(m_ctx, "#IsPOSJournalActive", isPOSJournalActive);
			Env.setContext(m_ctx, "#POSJournalApplication", clientInfo.getPOSJournalApplication());
			Env.setContext(m_ctx, "#VoidingInvoicePOSJournalConfig",
					clientInfo.getVoidingInvoicePOSJournalConfig());
			Env.setContext(m_ctx, "#VoidingInvoicePaymentsPOSJournalConfig",
					clientInfo.getVoidingInvoicePaymentsPOSJournalConfig());
		}
        return retValue;
    }    // getOrgs

    /**
     * Descripción de Método
     *
     *
     * @param list
     * @param Summary_Org_ID
     * @param Summary_Name
     * @param role
     */

    private void getOrgsAddSummary( ArrayList list,int Summary_Org_ID,String Summary_Name,MRole role ) {
        if( role == null ) {
            log.warning( "Summary Org=" + Summary_Name + "(" + Summary_Org_ID + ") - No Role" );

            return;
        }

        // Do we look for trees?

        if( role.getAD_Tree_Org_ID() == 0 ) {
            log.config( "Summary Org=" + Summary_Name + "(" + Summary_Org_ID + ") - No Org Tree: " + role );

            return;
        }

        // Summary Org - Get Dependents

        MTree_Base tree = MTree_Base.get( m_ctx,role.getAD_Tree_Org_ID(),null );
        String sql = "SELECT AD_Client_ID, AD_Org_ID, Name, IsSummary FROM AD_Org " + "WHERE IsActive='Y' AND AD_Org_ID IN (SELECT Node_ID FROM " + tree.getNodeTableName() + " WHERE AD_Tree_ID=? AND Parent_ID=? AND IsActive='Y') " + "ORDER BY Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,tree.getAD_Tree_ID());
            pstmt.setInt( 2,Summary_Org_ID );

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int     AD_Client_ID = rs.getInt( 1 );
                int     AD_Org_ID    = rs.getInt( 2 );
                String  Name         = rs.getString( 3 );
                boolean summary      = "Y".equals( rs.getString( 4 ));

                //

                if( summary ) {
                    getOrgsAddSummary( list,AD_Org_ID,Name,role );
                } else {
                    KeyNamePair p = new KeyNamePair( AD_Org_ID,Name );

                    if( !list.contains( p )) {
                        list.add( p );
                    }
                }
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
    }    // getOrgAddSummary

    /**
     * Descripción de Método
     *
     *
     * @param org
     *
     * @return
     */

    public KeyNamePair[] getWarehouses( KeyNamePair org ) {
        if( org == null ) {
            throw new IllegalArgumentException( "Org missing" );
        }

        // s_log.info("loadWarehouses - Org: " + org.toStringX());

        ArrayList     list     = new ArrayList();
        KeyNamePair[] retValue = null;
        String        sql      = "SELECT M_Warehouse_ID, Name FROM M_Warehouse " + "WHERE AD_Org_ID=? AND IsActive='Y' " + "ORDER BY Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,org.getKey());

            ResultSet rs = pstmt.executeQuery();

            if( !rs.next()) {
                rs.close();
                pstmt.close();
                log.info( "No Warehouses for Org: " + org.toStringX());

                return null;
            }

            // load Warehousess

            do {
                int         AD_Warehouse_ID = rs.getInt( 1 );
                String      Name            = rs.getString( 2 );
                KeyNamePair p               = new KeyNamePair( AD_Warehouse_ID,Name );

                list.add( p );
            } while( rs.next());

            rs.close();
            pstmt.close();
            pstmt = null;

            //

            retValue = new KeyNamePair[ list.size()];
            list.toArray( retValue );
            log.fine( "Org: " + org.toStringX() + " - warehouses #" + retValue.length );
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getWarehouses",ex );
            retValue = null;
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
    }    // getWarehouses

    /**
     * Descripción de Método
     *
     *
     * @param org
     *
     * @return
     */

    public CallResult validateLogin( KeyNamePair org ) {
        int    AD_Client_ID = Env.getAD_Client_ID( m_ctx );
        int    AD_Org_ID    = org.getKey();
        int    AD_Role_ID   = Env.getAD_Role_ID( m_ctx );
        int    AD_User_ID   = Env.getAD_User_ID( m_ctx );
        return ModelValidationEngine.get().loginComplete( AD_Client_ID,AD_Org_ID,AD_Role_ID,AD_User_ID );
        
//        if( result != null && result.isError() ) {
//            log.severe( "Refused: " + error );
//
//            return result;
//        }

//        return result;
    }    // validateLogin

    public String validateLoginString( KeyNamePair org ) {
        int    AD_Client_ID = Env.getAD_Client_ID( m_ctx );
        int    AD_Org_ID    = org.getKey();
        int    AD_Role_ID   = Env.getAD_Role_ID( m_ctx );
        int    AD_User_ID   = Env.getAD_User_ID( m_ctx );
        String error        = ModelValidationEngine.get().loginCompleteString( AD_Client_ID,AD_Org_ID,AD_Role_ID,AD_User_ID );

        if( (error != null) && (error.length() > 0) ) {
            log.severe( "Refused: " + error );

            return error;
        }

        return null;
    }    // validateLogin
    
    /**
     * Crea el schema info a partir del login
     */
    public static String getSchemaInfo(Properties ctx,int AD_Client_ID,int AD_Role_ID) throws SQLException,Exception{
        int C_AcctSchema_ID = 0;
        
        String sql = "SELECT * " + "FROM C_AcctSchema a, AD_ClientInfo c " + "WHERE a.C_AcctSchema_ID=c.C_AcctSchema1_ID " + "AND c.AD_Client_ID=?";
        String retValue = "";
        PreparedStatement pstmt = DB.prepareStatement( sql );
        pstmt.setInt( 1,AD_Client_ID );

        ResultSet rs = pstmt.executeQuery();

        if( !rs.next()) {

            // No Warning for System

            if( AD_Role_ID != 0 ) {
                retValue = "NoValidAcctInfo";
            }
        } else {

            // Accounting Info

            C_AcctSchema_ID = rs.getInt( "C_AcctSchema_ID" );
            Env.setContext( ctx,"$C_AcctSchema_ID",C_AcctSchema_ID );
            Env.setContext( ctx,"$C_Currency_ID",rs.getInt( "C_Currency_ID" ));
            Env.setContext( ctx,"$HasAlias",rs.getString( "HasAlias" ));
            Integer amortizationMethodID = rs.getInt( "M_Amortization_Method_ID" );
            if(!Util.isEmpty(amortizationMethodID, true)){
            	Env.setContext( ctx,"$M_Amortization_Method_ID", amortizationMethodID);
				Env.setContext(
						ctx,
						"$AmortizationAppPeriod",
						DB.getSQLValueString(
								null,
								"SELECT amortizationappperiod FROM m_amortization_method WHERE m_amortization_method_id = ?",
								amortizationMethodID));
            }
        }

        rs.close();
        pstmt.close();

        // Accounting Elements

        sql = "SELECT ElementType " + "FROM C_AcctSchema_Element " + "WHERE C_AcctSchema_ID=?" + " AND IsActive='Y'";
        pstmt = DB.prepareStatement( sql );
        pstmt.setInt( 1,C_AcctSchema_ID );
        rs = pstmt.executeQuery();

        while( rs.next()) {
            Env.setContext( ctx,"$Element_" + rs.getString( "ElementType" ),"Y" );
        }

        rs.close();
        pstmt.close();

        // This reads all relevant window neutral defaults
        // overwriting superseeded ones.  Window specific is read in Mainain

        sql = "SELECT Attribute, Value, AD_Window_ID " + "FROM AD_Preference " + "WHERE AD_Client_ID IN (0, @#AD_Client_ID@)" + " AND AD_Org_ID IN (0, @#AD_Org_ID@)" + " AND (AD_User_ID IS NULL OR AD_User_ID=0 OR AD_User_ID=@#AD_User_ID@)" + " AND IsActive='Y' " + "ORDER BY Attribute, AD_Client_ID, AD_User_ID DESC, AD_Org_ID";

        // the last one overwrites - System - Client - User - Org - Window

        sql = Env.parseContext( ctx,0,sql,false );

        if( sql.length() == 0 ) {
            log.log( Level.SEVERE,"loadPreferences - Missing Environment" );
        } else {
            pstmt = DB.prepareStatement( sql );
            rs    = pstmt.executeQuery();

            while( rs.next()) {
                int    AD_Window_ID = rs.getInt( 3 );
                String at           = "";

                if( rs.wasNull()) {
                    at = "P|" + rs.getString( 1 );
                } else {
                    at = "P" + AD_Window_ID + "|" + rs.getString( 1 );
                }

                String va = rs.getString( 2 );

                Env.setContext( ctx,at,va );
            }

            rs.close();
            pstmt.close();
        }
        
        return retValue;
    }
    
    
    /**
     * Descripción de Método
     *
     *
     * @param org
     * @param warehouse
     * @param timestamp
     * @param printerName
     *
     * @return
     */

    public String loadPreferences( KeyNamePair org,KeyNamePair warehouse,java.sql.Timestamp timestamp,String printerName ) {
        log.info( "Org: " + org.toStringX());

        if( (m_ctx == null) || (org == null) ) {
            throw new IllegalArgumentException( "Required parameter missing" );
        }

        if( Env.getContext( m_ctx,"#AD_Client_ID" ).length() == 0 ) {
            throw new UnsupportedOperationException( "Missing Comtext #AD_Client_ID" );
        }

        if( Env.getContext( m_ctx,"#AD_User_ID" ).length() == 0 ) {
            throw new UnsupportedOperationException( "Missing Comtext #AD_User_ID" );
        }

        if( Env.getContext( m_ctx,"#AD_Role_ID" ).length() == 0 ) {
            throw new UnsupportedOperationException( "Missing Comtext #AD_Role_ID" );
        }

        // Org Info - assumes that it is valid

        Env.setContext( m_ctx,"#AD_Org_ID",org.getKey());
        Env.setContext( m_ctx,"#AD_Org_Name",org.getName());
        Ini.setProperty( Ini.P_ORG,org.getName());

        // Warehouse Info

        if( warehouse != null ) {
            Env.setContext( m_ctx,"#M_Warehouse_ID",warehouse.getKey());
            Ini.setProperty( Ini.P_WAREHOUSE,warehouse.getName());
        }
        
        /* Existe la posibilidad que el usuario cargado en el contexto no pertenezca a la compañía del contexto. 
         * En la primer pestaña del login se obtiene el usuario a partir del name y password. 
         * En caso de que existan varios usuarios con el mismo name y password se elige uno y se carga en el contexto. */
        updateUserFromClient();        
        
        // Disytel - FB - 2010-12-23
        // Ahora la fecha actual #Date se administra internamente en el Env. 
        // El mismo se encarga de obtener la fecha del servidor de BD y setear
        // lavariable #Date con lo cual ya no se hace mas en el login
        // Además, la fecha no será modificable desde el login ni desde
        // la ventana de preferencias.
        
        /* -->
        // Date (default today)

        long today = System.currentTimeMillis();

        if( timestamp != null ) {
            today = timestamp.getTime();
        }


        Env.setContext( m_ctx,"#Date",new java.sql.Timestamp( today ));
		*/
        
        // <-- Fin Disytel - FB - 2010-12-23
        
        // Optional Printer

        if( printerName == null ) {
            printerName = "";
        }

        Env.setContext( m_ctx,"#Printer",printerName );
        Ini.setProperty( Ini.P_PRINTER,printerName );

        // Carga de la configuración del preview de la impresión
        Env.setContext( m_ctx,"#"+Ini.P_PRINTPREVIEW,Ini.getProperty( Ini.P_PRINTPREVIEW ) );
        
        // Load Role Info

        MRole.getDefault( m_ctx,true );

        // Other

        Env.setAutoCommit( m_ctx,Ini.getPropertyBool( Ini.P_A_COMMIT ));

        if( MRole.getDefault( m_ctx,false ).isShowAcct()) {
            Env.setContext( m_ctx,"#ShowAcct",Ini.getProperty( Ini.P_SHOW_ACCT ));
        } else {
            Env.setContext( m_ctx,"#ShowAcct","N" );
        }

        Env.setContext( m_ctx,"#ShowTrl",Ini.getProperty( Ini.P_SHOW_TRL ));
        Env.setContext( m_ctx,"#ShowAdvanced",Ini.getProperty( Ini.P_SHOW_ADVANCED ));

        String retValue     = "";
        int    AD_Client_ID = Env.getContextAsInt( m_ctx,"#AD_Client_ID" );
        int    AD_Org_ID    = org.getKey();
        int    AD_User_ID   = Env.getContextAsInt( m_ctx,"#AD_User_ID" );
        int    AD_Role_ID   = Env.getContextAsInt( m_ctx,"#AD_Role_ID" );

        // Vencimiento de claves
        MClientInfo clientInfo = MClientInfo.get(m_ctx, AD_Client_ID);
		Env.setContext(m_ctx, "#PasswordExpirationActive",
				clientInfo.isPasswordExpirationActive() ? "Y" : "N");
		Env.setContext(m_ctx, "#UniqueKeyActive",
				clientInfo.isUniqueKeyActive() ? "Y" : "N");
        
		// Control de CUIT
		MOrgInfo orgInfo = MOrgInfo.get(m_ctx, AD_Org_ID);
		Env.setContext(m_ctx, "#CheckCuitControl",
				orgInfo.isCheckCuitControl() ? "Y" : "N");
		
		// Supervisor de Cajas Diarias
		MRole role = MRole.get(m_ctx, AD_Role_ID, null);
		Env.setContext(m_ctx, "#POSJournalSupervisor",
				role.isPOSJournalSupervisor() ? "Y" : "N");
		
        // Other Settings

        Env.setContext( m_ctx,"#YYYY","Y" );
        Env.setContext( m_ctx,"#StdPrecision",2 );

        // AccountSchema Info (first)
        
        String sql;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	
        	retValue = Login.getSchemaInfo(m_ctx, AD_Client_ID, AD_Role_ID);        
        
            // Default Values

            log.info( "Default Values ..." );
            sql = "SELECT t.TableName, c.ColumnName " + "FROM AD_Column c " + " INNER JOIN AD_Table t ON (c.AD_Table_ID=t.AD_Table_ID) " + "WHERE c.IsKey='Y' AND t.IsActive='Y'" + " AND EXISTS (SELECT * FROM AD_Column cc " + " WHERE ColumnName = 'IsDefault' AND t.AD_Table_ID=cc.AD_Table_ID AND cc.IsActive='Y')";
            pstmt = DB.prepareStatement( sql );
            rs    = pstmt.executeQuery();

            while( rs.next()) {
                loadDefault( rs.getString( 1 ),rs.getString( 2 ));
            }

            
            // AD_ComponentVersion current development
            MComponentVersion componentVersion = MComponentVersion.getCurrentComponentVersion(m_ctx, null); 
            if(componentVersion != null){
            	Env.setContext(m_ctx,"#AD_ComponentVersion_ID",componentVersion.getID());	
            }
            
            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"loadPreferences",e );
		} catch(Exception e){
			log.log( Level.SEVERE,"loadPreferences",e );
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

        Ini.saveProperties( Ini.isClient());

        // Country

        Env.setContext( m_ctx,"#C_Country_ID",MCountry.getDefault( m_ctx ).getC_Country_ID());

        return retValue;
    }    // loadPreferences

    private void updateUserFromClient() {
    	/*Obtengo los datos del usuario cargado en el contexto*/
        String paramName = null;
        String paramPassword = null;
        int adClientUserID = 0;
        
        String sql = "SELECT AD_User.Name, AD_User.Password, AD_User.AD_Client_ID" +
		   		     " FROM AD_User" + 
		   		     " WHERE AD_User.AD_User_ID=?";

        PreparedStatement pstmt = null;
        try {
	        pstmt = DB.prepareStatement( sql );
	        pstmt.setInt( 1, Env.getContextAsInt( m_ctx,"#AD_User_ID" ) );
	
	        ResultSet rs = pstmt.executeQuery();
	        if( rs.next()) {
	        	paramName = rs.getString( 1 );
	        	paramPassword = rs.getString( 2 );
	        	adClientUserID = rs.getInt( 3 );
	        }
	
	        rs.close();
	        pstmt.close();
        } catch( SQLException e ) {
            log.log( Level.SEVERE,"User Error",e );
        }         
        /* Si el usuario existente en el contexto pertenece a una compañía (AD_CLIENT_ID ) diferente a la selecionada en el login:
         * Entonces busco un usuario para la compañía seleccionada, con el mismo name, password y que este activo. 
         * En caso se existir, actualizo el Contexto. */
        if (adClientUserID != Env.getContextAsInt( m_ctx,"#AD_Client_ID" )) {
        	sql = "SELECT AD_User.AD_User_ID" +
                    " FROM AD_User" + 
                    " WHERE AD_User.AD_Client_ID=? AND AD_User.Name=? AND AD_User.Password=? AND AD_User.IsActive='Y'";
         
        	pstmt = null;
        	try {
        		pstmt = DB.prepareStatement( sql );
			    pstmt.setInt( 1, Env.getContextAsInt( m_ctx,"#AD_Client_ID" ) );
			    pstmt.setString( 2, paramName );
			    pstmt.setString( 3, paramPassword );
			  
			    ResultSet rs = pstmt.executeQuery();
			    if( rs.next()) {
			    	Env.setContext( m_ctx,"#AD_User_ID",rs.getInt(1));
			    }
			
			    rs.close();
			    pstmt.close();
			} catch( SQLException e ) {
			    log.log( Level.SEVERE,"User Error",e );
			}	
        }
	}

	/**
     * Descripción de Método
     *
     *
     * @param TableName
     * @param ColumnName
     */

    private void loadDefault( String TableName,String ColumnName ) {
        if( TableName.startsWith( "AD_Window" ) || TableName.startsWith( "AD_PrintFormat" ) || TableName.startsWith( "AD_Workflow" )) {
            return;
        }

        String value = null;

        //

        String sql = "SELECT " + ColumnName + " FROM " + TableName    // most specific first
                     + " WHERE IsDefault='Y' AND IsActive='Y' ORDER BY AD_Client_ID DESC, AD_Org_ID DESC";

        sql = MRole.getDefault( m_ctx,false ).addAccessSQL( sql,TableName,MRole.SQL_NOTQUALIFIED,MRole.SQL_RO );

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                value = rs.getString( 1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException e ) {
            log.log( Level.SEVERE,TableName + " (" + sql + ")",e );

            return;
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Set Context Value

        if( (value != null) && (value.length() != 0) ) {
            if( TableName.equals( "C_DocType" )) {
                Env.setContext( m_ctx,"#C_DocTypeTarget_ID",value );
            } else {
                Env.setContext( m_ctx,"#" + ColumnName,value );
            }
        }
    }    // loadDefault

    /**
     * Descripción de Método
     *
     *
     * @param loginDate
     *
     * @return
     */

    public boolean batchLogin( java.sql.Timestamp loginDate ) {

        // User Login

        String        uid   = Ini.getProperty( Ini.P_UID );
        String        pwd   = Ini.getProperty( Ini.P_PWD );
        KeyNamePair[] roles = getRoles( uid,pwd );

        if( (roles == null) || (roles.length == 0) ) {
            log.severe( "User/Password invalid: " + uid );

            return false;
        }

        log.info( "User: " + uid );

        // Role

        String      role   = Ini.getProperty( Ini.P_ROLE );
        KeyNamePair rolePP = null;

        for( int i = 0;i < roles.length;i++ ) {
            KeyNamePair pair = roles[ i ];

            if( pair.getName().equalsIgnoreCase( role )) {
                rolePP = pair;

                break;
            }
        }

        if( rolePP == null ) {
            log.severe( "Role invalid: " + role );

            for( int i = 0;i < roles.length;i++ ) {
                log.info( "Option: " + roles[ i ] );
            }

            return false;
        }

        log.info( "Role: " + role );

        // Clients

        String        client  = Ini.getProperty( Ini.P_CLIENT );
        KeyNamePair[] clients = getClients( rolePP );

        if( (clients == null) || (clients.length == 0) ) {
            log.severe( "No Clients for Role: " + role );

            return false;
        }

        KeyNamePair clientPP = null;

        for( int i = 0;i < clients.length;i++ ) {
            KeyNamePair pair = clients[ i ];

            if( pair.getName().equalsIgnoreCase( client )) {
                clientPP = pair;

                break;
            }
        }

        if( clientPP == null ) {
            log.severe( "Client invalid: " + client );

            for( int i = 0;i < clients.length;i++ ) {
                log.info( "Option: " + clients[ i ] );
            }

            return false;
        }

        // Organization

        String        org  = Ini.getProperty( Ini.P_ORG );
        KeyNamePair[] orgs = getOrgs( clientPP );

        if( (orgs == null) || (orgs.length == 0) ) {
            log.severe( "No Orgs for Client: " + client );

            return false;
        }

        KeyNamePair orgPP = null;

        for( int i = 0;i < orgs.length;i++ ) {
            KeyNamePair pair = orgs[ i ];

            if( pair.getName().equalsIgnoreCase( org )) {
                orgPP = pair;

                break;
            }
        }

        if( orgPP == null ) {
            log.severe( "Org invalid: " + org );

            for( int i = 0;i < orgs.length;i++ ) {
                log.info( "Option: " + orgs[ i ] );
            }

            return false;
        }

        CallResult result = validateLogin( orgPP );

        if( result != null && result.isError() ) {
            return false;
        }

        // Warehouse

        String        wh  = Ini.getProperty( Ini.P_WAREHOUSE );
        KeyNamePair[] whs = getWarehouses( orgPP );

        if( (whs == null) || (whs.length == 0) ) {
            log.severe( "No Warehouses for Org: " + org );

            return false;
        }

        KeyNamePair whPP = null;

        for( int i = 0;i < whs.length;i++ ) {
            KeyNamePair pair = whs[ i ];

            if( pair.getName().equalsIgnoreCase( wh )) {
                whPP = pair;

                break;
            }
        }

        if( whPP == null ) {
            log.severe( "Warehouse invalid: " + wh );

            for( int i = 0;i < whs.length;i++ ) {
                log.info( "Option: " + whs[ i ] );
            }

            return false;
        }

        // Language

        String   langName = Ini.getProperty( Ini.P_LANGUAGE );
        Language language = Language.getLanguage( langName );

        Language.setLoginLanguage( language );
        Env.verifyLanguage( m_ctx,language );
        Env.setContext( m_ctx,Env.LANGUAGE,language.getAD_Language());

        Locale loc = language.getLocale();

        Locale.setDefault( loc );
        Msg.getMsg( m_ctx,"0" );

        // Preferences

        String printerName = Ini.getProperty( Ini.P_PRINTER );

        if( loginDate == null ) {
            loginDate = new java.sql.Timestamp( System.currentTimeMillis());
        }

        loadPreferences( orgPP,whPP,loginDate,printerName );

        //

        log.info( "complete" );

        return true;
    }    // batchLogin
}    // Login



/*
 *  @(#)Login.java   25.03.06
 * 
 *  Fin del fichero Login.java
 *  
 *  Versión 2.2
 *
 */
