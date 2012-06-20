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

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.process.DocumentTypeVerify;
import org.openXpertya.process.LocaleActivation;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.KeyNamePair;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Trx;

/**
 * Descripción de Clase
 *
 *
 * @version    2.1, 02.07.07
 * @author     Equipo de Desarrollo de openXpertya
 */

public class MSetup {

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param WindowNo
     */

    public MSetup( Properties ctx,int WindowNo ) {
        setM_ctx(new Properties( ctx ));    // copy
        m_lang     = Env.getAD_Language( m_ctx );
        m_WindowNo = WindowNo;
    }                                          // MSetup

    /** Descripción de Campos */

    protected CLogger log = CLogger.getCLogger( getClass());

    /** Descripción de Campos */

    private Trx m_trx = Trx.get( Trx.createTrxName( "Setup" ),true );

    /** Descripción de Campos */

    private Properties m_ctx;

    /** Descripción de Campos */

    private String m_lang;

    /** Descripción de Campos */

    private int m_WindowNo;

    /** Descripción de Campos */

    private StringBuffer m_info;

    //

    /** Descripción de Campos */

    private String m_clientName;

//      private String          m_orgName;
    //

    /** Descripción de Campos */

    private String m_stdColumns = "AD_Client_ID,AD_Org_ID,IsActive,Created,CreatedBy,Updated,UpdatedBy";

    /** Descripción de Campos */

    private String m_stdValues;

    /** Descripción de Campos */

    private String m_stdValuesOrg;

    //

    /** Descripción de Campos */

    private NaturalAccountMap m_nap = null;

    //

    /** Descripción de Campos */

    private MClient m_client;

    /** Descripción de Campos */

    private MOrg m_org;

    /** Descripción de Campos */

    private MAcctSchema m_as;

    //

    /** Descripción de Campos */

    private int AD_User_ID;

    /** Descripción de Campos */

    private String AD_User_Name;

    /** Descripción de Campos */

    private int AD_User_U_ID;

    /** Descripción de Campos */

    private String AD_User_U_Name;

    /** Descripción de Campos */

    private MCalendar m_calendar;

    /** Descripción de Campos */

    private int m_AD_Tree_Account_ID;

    /** Descripción de Campos */

    private int C_Cycle_ID;

    //

    /** Descripción de Campos */

    private boolean m_hasProject = false;

    /** Descripción de Campos */

    private boolean m_hasMCampaign = false;

    /** Descripción de Campos */

    private boolean m_hasSRegion = false;
    
    private MYear m_currentYear = null;

    /**
     * Descripción de Método
     *
     *
     * @param clientName
     * @param orgName
     * @param userClient
     * @param userOrg
     *
     * @return
     */

    public boolean createClient( String clientName,String orgName,String userClient,String userOrg ) {
        log.info( clientName );
        m_trx.start();

        // info header

        m_info = new StringBuffer();

        // Standarc columns

        String name = null;
        String sql  = null;
        int    no   = 0;

        //se guarda el nombre del cliente nuevo pasado como parámetro
        name = clientName;

        //Si no existe, creo un nombre por defecto
        if( (name == null) || (name.length() == 0) ) {
            name = "newClient";
        }

        //Creo el cliente
        m_clientName = name;
        m_client     = new MClient( m_ctx,0,true,m_trx.getTrxName());
        m_client.setValue( m_clientName );
        m_client.setName( m_clientName );
        this.setLanguageClient();

        //Si no se guardó --> error
        if( !m_client.save()) {
            String err = "Client NOT created";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        //Seteo el nombre del cliente en el contexto
        
        int AD_Client_ID = m_client.getAD_Client_ID();

        Env.setContext( m_ctx,m_WindowNo,"AD_Client_ID",AD_Client_ID );
        Env.setContext( m_ctx,"#AD_Client_ID",AD_Client_ID );

        // Standard Values

        m_stdValues = String.valueOf( AD_Client_ID ) + ",0,'Y',SysDate,0,SysDate,0";

        // Info - Client

        m_info.append( Msg.translate( m_lang,"AD_Client_ID" )).append( "=" ).append( name ).append( "\n" );

        // Setup Sequences

        if( !MSequence.checkClientSequences( m_ctx,AD_Client_ID,m_trx.getTrxName())) {
            String err = "Sequences NOT created";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // Trees and Client Info

        if( !m_client.setupClientInfo( m_lang )) {
            String err = "Client Info NOT created";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        m_AD_Tree_Account_ID = m_client.getSetup_AD_Tree_Account_ID();
        
        //Orgs
       
        if(!this.createOrg(orgName)){
        	return false;
        }        

        //Users and roles
        
        if(!this.createUsersFeatures(userClient, userOrg)){
        	return false;
        }
        
        return true;
    }    // createClient

    /**
     * No aplica para esta clase, implementa MFTSetup
     */
    public void setLanguageClient(){

    }
    
    /**
     * Creo la organización pasada como parámetro
     * @param orgName nombre de la organización a crear
     * @return
     */
    public boolean createOrg(String orgName){
    	//Guardo el nombre de la organización pasada como parámetro
        String name = orgName;

        //Si se ingresó ninguno, coloco uno por defecto
        if( (name == null) || (name.length() == 0) ) {
            name = "newOrg";
        }

        //Creo la organización
        m_org = new MOrg( m_client,name );

        //Si no se guardó --> error
        if( !m_org.save()) {
            String err = "Organization NOT created";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        //Seteo la organización del contexto con la que se creó
        Env.setContext( m_ctx,m_WindowNo,"AD_Org_ID",getAD_Org_ID());
        Env.setContext( m_ctx,"#AD_Org_ID",getAD_Org_ID());
        setM_stdValuesOrg(m_client.getAD_Client_ID() + "," + getAD_Org_ID() + ",'Y',SysDate,0,SysDate,0");

        // Info

        m_info.append( Msg.translate( m_lang,"AD_Org_ID" )).append( "=" ).append( name ).append( "\n" );
        
        return true;

    }
   
    /**
     * Crea los usuarios, administrador y usuario de la compañía, y sus roles
     * @param userClient nombre del usuario administrador
     * @param userOrg nombre del usuario de organización (no administrador)
     * @return el éxito del procedimiento
     */
    
    public boolean createUsersFeatures(String userClient, String userOrg){
        //Crea el rol para el administrador de la compañía
        String name = m_clientName + " Admin";

        MRole admin = new MRole( m_ctx,0,m_trx.getTrxName());

        admin.setClientOrg( m_client );
        admin.setName( name );
        admin.setUserLevel( MRole.USERLEVEL_ClientPlusOrganization );
        admin.setPreferenceType( MRole.PREFERENCETYPE_Client );
        admin.setIsShowAcct( true );

        //Si no se guarda el rol --> error
        if( !admin.save()) {
            String err = "Admin Role A NOT inserted";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // OrgAccess x, 0

        MRoleOrgAccess adminClientAccess = new MRoleOrgAccess( admin,0 );

        if( !adminClientAccess.save()) {
            log.log( Level.SEVERE,"Admin Role_OrgAccess 0 NOT created" );
        }

        // OrgAccess x,y

        MRoleOrgAccess adminOrgAccess = new MRoleOrgAccess( admin,m_org.getAD_Org_ID());

        if( !adminOrgAccess.save()) {
            log.log( Level.SEVERE,"Admin Role_OrgAccess NOT created" );
        }

        // Info - Admin Role

        m_info.append( Msg.translate( m_lang,"AD_Role_ID" )).append( "=" ).append( name ).append( "\n" );

        //Creo el rol para el usuario de la compañía
        name = m_clientName + " User";

        MRole user = new MRole( m_ctx,0,m_trx.getTrxName());

        user.setClientOrg( m_client );
        user.setName( name );

        //Si no se guarda --> error
        if( !user.save()) {
            String err = "User Role A NOT inserted";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // OrgAccess x,y

        MRoleOrgAccess userOrgAccess = new MRoleOrgAccess( user,m_org.getAD_Org_ID());

        if( !userOrgAccess.save()) {
            log.log( Level.SEVERE,"User Role_OrgAccess NOT created" );
        }

        // Info - Client Role

        m_info.append( Msg.translate( m_lang,"AD_Role_ID" )).append( "=" ).append( name ).append( "\n" );
        
        //Guardo el nombre del usuario de la compañía pasada como parámetro
        name = userClient;

        if( (name == null) || (name.length() == 0) ) {
            name = m_clientName + "Client";
        }

        //Almaceno en base de datos el usuario de la compañía, el admin
        AD_User_ID   = getNextID( m_client.getAD_Client_ID(),"AD_User" );
        AD_User_Name = name;
        name         = DB.TO_STRING( name );
        String sql          = "INSERT INTO AD_User(" + m_stdColumns + ",AD_User_ID," + "Name,Description,Password)" + " VALUES (" + m_stdValues + "," + AD_User_ID + "," + name + "," + name + "," + name + ")";
        int no = DB.executeUpdate( sql,m_trx.getTrxName());

        //Si no se guarda --> error
        if( no != 1 ) {
            String err = "Admin User NOT inserted - " + AD_User_Name;

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // Info

        m_info.append( Msg.translate( m_lang,"AD_User_ID" )).append( "=" ).append( AD_User_Name ).append( "/" ).append( AD_User_Name ).append( "\n" );
        
        //Guardo el nombre del usuario de la organización
        name = userOrg;

        if( (name == null) || (name.length() == 0) ) {
            name = m_clientName + "Org";
        }

        //Almaceno en la base de datos el usuario de la organización
        AD_User_U_ID   = getNextID( m_client.getAD_Client_ID(),"AD_User" );
        AD_User_U_Name = name;
        name           = DB.TO_STRING( name );
        sql            = "INSERT INTO AD_User(" + m_stdColumns + ",AD_User_ID," + "Name,Description,Password)" + " VALUES (" + m_stdValues + "," + AD_User_U_ID + "," + name + "," + name + "," + name + ")";
        no = DB.executeUpdate( sql,m_trx.getTrxName());

        //Si no se guarda --> error
        if( no != 1 ) {
            String err = "Org User NOT inserted - " + AD_User_U_Name;

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // Info

        m_info.append( Msg.translate( m_lang,"AD_User_ID" )).append( "=" ).append( AD_User_U_Name ).append( "/" ).append( AD_User_U_Name ).append( "\n" );

        // ClientUser - Admin & User

        //Almaceno en la base de datos el rol de admin asociado al usuario de la compañía  
        sql = "INSERT INTO AD_User_Roles(" + m_stdColumns + ",AD_User_ID,AD_Role_ID)" + " VALUES (" + m_stdValues + "," + AD_User_ID + "," + admin.getAD_Role_ID() + ")";
        no = DB.executeUpdate( sql,m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"UserRole ClientUser+Admin NOT inserted" );
        }

        //Almaceno en la base de datos el rol de usuario asociado al usuario de la compañía
        sql = "INSERT INTO AD_User_Roles(" + m_stdColumns + ",AD_User_ID,AD_Role_ID)" + " VALUES (" + m_stdValues + "," + AD_User_ID + "," + user.getAD_Role_ID() + ")";
        no = DB.executeUpdate( sql,m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"UserRole ClientUser+User NOT inserted" );
        }

        // OrgUser - User

        //Almaceno en la base de datos el rol de usuario asociado al usuario de la organización
        sql = "INSERT INTO AD_User_Roles(" + m_stdColumns + ",AD_User_ID,AD_Role_ID)" + " VALUES (" + m_stdValues + "," + AD_User_U_ID + "," + user.getAD_Role_ID() + ")";
        no = DB.executeUpdate( sql,m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"UserRole OrgUser+Org NOT inserted" );
        }

        // Processors

        MAcctProcessor ap = new MAcctProcessor( m_client,AD_User_ID );

        ap.save();

        MRequestProcessor rp = new MRequestProcessor( m_client,AD_User_ID );

        rp.save();
        log.info( "fini" );
        
        return true;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param currency
     * @param hasProduct
     * @param hasBPartner
     * @param hasProject
     * @param hasMCampaign
     * @param hasSRegion
     * @param AccountingFile
     *
     * @return
     */

    public boolean createAccounting( KeyNamePair currency,boolean hasProduct,boolean hasBPartner,boolean hasProject,boolean hasMCampaign,boolean hasSRegion,File AccountingFile ) {
        log.info( m_client.toString());

        //

        m_hasProject   = hasProject;
        m_hasMCampaign = hasMCampaign;
        m_hasSRegion   = hasSRegion;

        // Standard variables

        m_info = new StringBuffer();

        String       name   = null;
        StringBuffer sqlCmd = null;
        int          no     = 0;

        m_calendar = new MCalendar( m_client );

        if( !m_calendar.save()) {
            String err = "Calendar NOT inserted";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // Info

        m_info.append( Msg.translate( m_lang,"C_Calendar_ID" )).append( "=" ).append( m_calendar.getName()).append( "\n" );

        m_currentYear = m_calendar.createYear( m_client.getLocale()); 
        if(m_currentYear == null ) {
            log.log( Level.SEVERE,"Year NOT inserted" );
        }

        // Create Account Elements

        name = m_clientName + " " + Msg.translate( m_lang,"Account_ID" );

        MElement element = new MElement( m_client,name,MElement.ELEMENTTYPE_Account,m_AD_Tree_Account_ID );

        if( !element.save()) {
            String err = "Acct Element NOT inserted";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        int C_Element_ID = element.getC_Element_ID();

        m_info.append( Msg.translate( m_lang,"C_Element_ID" )).append( "=" ).append( name ).append( "\n" );

        // Create Account Values

        m_nap = new NaturalAccountMap( m_ctx,m_trx.getTrxName());

        String errMsg = m_nap.parseFile( AccountingFile );

        if( errMsg.length() != 0 ) {
            log.log( Level.SEVERE,errMsg );
            m_info.append( errMsg );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        if( m_nap.saveAccounts( getAD_Client_ID(),getAD_Org_ID(),C_Element_ID )) {
            m_info.append( Msg.translate( m_lang,"C_ElementValue_ID" )).append( " # " ).append( m_nap.size()).append( "\n" );
        } else {
            String err = "Acct Element Values NOT inserted";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        int C_ElementValue_ID = m_nap.getC_ElementValue_ID( "DEFAULT_ACCT" );

        log.fine( "C_ElementValue_ID=" + C_ElementValue_ID );
        m_as = new MAcctSchema( m_client,currency );

        if( !m_as.save()) {
            String err = "AcctSchema NOT inserted";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // Info

        m_info.append( Msg.translate( m_lang,"C_AcctSchema_ID" )).append( "=" ).append( m_as.getName()).append( "\n" );

        String sql2 = null;

        if( Env.isBaseLanguage( m_lang,"AD_Reference" )) {    // Get ElementTypes & Name
            sql2 = "SELECT Value, Name FROM AD_Ref_List WHERE AD_Reference_ID=181";
        } else {
            sql2 = "SELECT l.Value, COALESCE(t.Name,l.Name) FROM AD_Ref_List l LEFT JOIN AD_Ref_List_Trl t on (l.AD_Ref_List_ID=t.AD_Ref_List_ID AND AD_Language = ? ) WHERE l.AD_Reference_ID=181 ";
        }

        //

        int Element_OO = 0,
            Element_AC = 0,
            Element_PR = 0,
            Element_BP = 0,
            Element_PJ = 0,
            Element_MC = 0,
            Element_SR = 0;

        try {
            int               AD_Client_ID = m_client.getAD_Client_ID();
            PreparedStatement stmt         = DB.prepareStatement( sql2,m_trx.getTrxName());
            
            stmt.setString(1, m_lang);
            
            ResultSet rs = stmt.executeQuery();

            while( rs.next()) {
                String ElementType = rs.getString( 1 );

                name = rs.getString( 2 );

                //

                String IsMandatory             = null;
                String IsBalanced              = "N";
                int    SeqNo                   = 0;
                int    C_AcctSchema_Element_ID = 0;

                if( ElementType.equals( "OO" )) {
                    C_AcctSchema_Element_ID = getNextID( AD_Client_ID,"C_AcctSchema_Element" );
                    Element_OO  = C_AcctSchema_Element_ID;
                    IsMandatory = "Y";
                    IsBalanced  = "Y";
                    SeqNo       = 10;
                } else if( ElementType.equals( "AC" )) {
                    C_AcctSchema_Element_ID = getNextID( AD_Client_ID,"C_AcctSchema_Element" );
                    Element_AC  = C_AcctSchema_Element_ID;
                    IsMandatory = "Y";
                    SeqNo       = 20;
                } else if( ElementType.equals( "PR" ) && hasProduct ) {
                    C_AcctSchema_Element_ID = getNextID( AD_Client_ID,"C_AcctSchema_Element" );
                    Element_PR  = C_AcctSchema_Element_ID;
                    IsMandatory = "N";
                    SeqNo       = 30;
                } else if( ElementType.equals( "BP" ) && hasBPartner ) {
                    C_AcctSchema_Element_ID = getNextID( AD_Client_ID,"C_AcctSchema_Element" );
                    Element_BP  = C_AcctSchema_Element_ID;
                    IsMandatory = "N";
                    SeqNo       = 40;
                } else if( ElementType.equals( "PJ" ) && hasProject ) {
                    C_AcctSchema_Element_ID = getNextID( AD_Client_ID,"C_AcctSchema_Element" );
                    Element_PJ  = C_AcctSchema_Element_ID;
                    IsMandatory = "N";
                    SeqNo       = 50;
                } else if( ElementType.equals( "MC" ) && hasMCampaign ) {
                    C_AcctSchema_Element_ID = getNextID( AD_Client_ID,"C_AcctSchema_Element" );
                    Element_MC  = C_AcctSchema_Element_ID;
                    IsMandatory = "N";
                    SeqNo       = 60;
                } else if( ElementType.equals( "SR" ) && hasSRegion ) {
                    C_AcctSchema_Element_ID = getNextID( AD_Client_ID,"C_AcctSchema_Element" );
                    Element_SR  = C_AcctSchema_Element_ID;
                    IsMandatory = "N";
                    SeqNo       = 70;
                }

                // Not OT, LF, LT, U1, U2, AY

                if( IsMandatory != null ) {
                    sqlCmd = new StringBuffer( "INSERT INTO C_AcctSchema_Element(" );
                    sqlCmd.append( m_stdColumns ).append( ",C_AcctSchema_Element_ID,C_AcctSchema_ID," ).append( "ElementType,Name,SeqNo,IsMandatory,IsBalanced) VALUES (" );
                    sqlCmd.append( m_stdValues ).append( "," ).append( C_AcctSchema_Element_ID ).append( "," ).append( m_as.getC_AcctSchema_ID()).append( "," ).append( "'" ).append( ElementType ).append( "','" ).append( name ).append( "'," ).append( SeqNo ).append( ",'" ).append( IsMandatory ).append( "','" ).append( IsBalanced ).append( "')" );
                    no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

                    if( no == 1 ) {
                        m_info.append( Msg.translate( m_lang,"C_AcctSchema_Element_ID" )).append( "=" ).append( name ).append( "\n" );
                    }

                    if( ElementType.equals( "OO" )) {
                        sqlCmd = new StringBuffer( "UPDATE C_AcctSchema_Element SET Org_ID=" );
                        sqlCmd.append( getAD_Org_ID()).append( " WHERE C_AcctSchema_Element_ID=" ).append( C_AcctSchema_Element_ID );
                        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

                        if( no != 1 ) {
                            log.log( Level.SEVERE,"Default Org in AcctSchamaElement NOT updated" );
                        }
                    }

                    if( ElementType.equals( "AC" )) {
                        sqlCmd = new StringBuffer( "UPDATE C_AcctSchema_Element SET C_ElementValue_ID=" );
                        sqlCmd.append( C_ElementValue_ID ).append( ", C_Element_ID=" ).append( C_Element_ID );
                        sqlCmd.append( " WHERE C_AcctSchema_Element_ID=" ).append( C_AcctSchema_Element_ID );
                        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

                        if( no != 1 ) {
                            log.log( Level.SEVERE,"Default Account in AcctSchamaElement NOT updated" );
                        }
                    }
                }
            }

            rs.close();
            stmt.close();
        } catch( SQLException e1 ) {
            log.log( Level.SEVERE,"Elements",e1 );
            m_info.append( e1.getMessage());
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // Create AcctSchema

        // Create GL Accounts

        sqlCmd = new StringBuffer( "INSERT INTO C_ACCTSCHEMA_GL (" );
        sqlCmd.append( m_stdColumns ).append( ",C_ACCTSCHEMA_ID," + "USESUSPENSEBALANCING,SUSPENSEBALANCING_ACCT," + "USESUSPENSEERROR,SUSPENSEERROR_ACCT," + "USECURRENCYBALANCING,CURRENCYBALANCING_ACCT," + "RETAINEDEARNING_ACCT,INCOMESUMMARY_ACCT," + "INTERCOMPANYDUETO_ACCT,INTERCOMPANYDUEFROM_ACCT," + "PPVOFFSET_ACCT) VALUES (" );
        sqlCmd.append( m_stdValues ).append( "," ).append( m_as.getC_AcctSchema_ID()).append( "," );
        sqlCmd.append( "'Y'," ).append( getAcct( "SUSPENSEBALANCING_ACCT" )).append( "," );
        sqlCmd.append( "'Y'," ).append( getAcct( "SUSPENSEERROR_ACCT" )).append( "," );
        sqlCmd.append( "'Y'," ).append( getAcct( "CURRENCYBALANCING_ACCT" )).append( "," );

        // RETAINEDEARNING_ACCT,INCOMESUMMARY_ACCT,

        sqlCmd.append( getAcct( "RETAINEDEARNING_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "INCOMESUMMARY_ACCT" )).append( "," );

        // INTERCOMPANYDUETO_ACCT,INTERCOMPANYDUEFROM_ACCT)

        sqlCmd.append( getAcct( "INTERCOMPANYDUETO_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "INTERCOMPANYDUEFROM_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "PPVOFFSET_ACCT" )).append( ")" );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            String err = "GL Accounts NOT inserted";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // Create Std Accounts

        sqlCmd = new StringBuffer( "INSERT INTO C_ACCTSCHEMA_DEFAULT (" );
        sqlCmd.append( m_stdColumns ).append( ",C_ACCTSCHEMA_ID," + "W_INVENTORY_ACCT,W_DIFFERENCES_ACCT,W_REVALUATION_ACCT,W_INVACTUALADJUST_ACCT, " + "P_REVENUE_ACCT,P_EXPENSE_ACCT,P_ASSET_ACCT,P_COGS_ACCT, " + "P_PURCHASEPRICEVARIANCE_ACCT,P_INVOICEPRICEVARIANCE_ACCT,P_TRADEDISCOUNTREC_ACCT,P_TRADEDISCOUNTGRANT_ACCT, " + "C_RECEIVABLE_ACCT,C_PREPAYMENT_ACCT, " + "V_LIABILITY_ACCT,V_LIABILITY_SERVICES_ACCT,V_PREPAYMENT_ACCT, " + "PAYDISCOUNT_EXP_ACCT,PAYDISCOUNT_REV_ACCT,WRITEOFF_ACCT, " + "UNREALIZEDGAIN_ACCT,UNREALIZEDLOSS_ACCT,REALIZEDGAIN_ACCT,REALIZEDLOSS_ACCT, " + "WITHHOLDING_ACCT,E_PREPAYMENT_ACCT,E_EXPENSE_ACCT, " + "PJ_ASSET_ACCT,PJ_WIP_ACCT," + "T_EXPENSE_ACCT,T_LIABILITY_ACCT,T_RECEIVABLES_ACCT,T_DUE_ACCT,T_CREDIT_ACCT, " + "B_INTRANSIT_ACCT,B_ASSET_ACCT,B_EXPENSE_ACCT,B_INTERESTREV_ACCT,B_INTERESTEXP_ACCT," + "B_UNIDENTIFIED_ACCT,B_SETTLEMENTGAIN_ACCT,B_SETTLEMENTLOSS_ACCT," + "B_REVALUATIONGAIN_ACCT,B_REVALUATIONLOSS_ACCT,B_PAYMENTSELECT_ACCT,B_UNALLOCATEDCASH_ACCT, " + "CH_EXPENSE_ACCT,CH_REVENUE_ACCT, " + "UNEARNEDREVENUE_ACCT,NOTINVOICEDRECEIVABLES_ACCT,NOTINVOICEDREVENUE_ACCT,NOTINVOICEDRECEIPTS_ACCT, " + "CB_ASSET_ACCT,CB_CASHTRANSFER_ACCT,CB_DIFFERENCES_ACCT,CB_EXPENSE_ACCT,CB_RECEIPT_ACCT) VALUES (" );
        sqlCmd.append( m_stdValues ).append( "," ).append( m_as.getC_AcctSchema_ID()).append( "," );

        // W_INVENTORY_ACCT,W_DIFFERENCES_ACCT,W_REVALUATION_ACCT,W_INVACTUALADJUST_ACCT

        sqlCmd.append( getAcct( "W_INVENTORY_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "W_DIFFERENCES_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "W_REVALUATION_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "W_INVACTUALADJUST_ACCT" )).append( ", " );

        // P_REVENUE_ACCT,P_EXPENSE_ACCT,P_ASSET_ACCT,P_COGS_ACCT,

        sqlCmd.append( getAcct( "P_REVENUE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "P_EXPENSE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "P_ASSET_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "P_COGS_ACCT" )).append( ", " );

        // P_PURCHASEPRICEVARIANCE_ACCT,P_INVOICEPRICEVARIANCE_ACCT,P_TRADEDISCOUNTREC_ACCT,P_TRADEDISCOUNTGRANT_ACCT,

        sqlCmd.append( getAcct( "P_PURCHASEPRICEVARIANCE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "P_INVOICEPRICEVARIANCE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "P_TRADEDISCOUNTREC_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "P_TRADEDISCOUNTGRANT_ACCT" )).append( ", " );

        // C_RECEIVABLE_ACCT,C_PREPAYMENT_ACCT,

        sqlCmd.append( getAcct( "C_RECEIVABLE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "C_PREPAYMENT_ACCT" )).append( ", " );

        // V_LIABILITY_ACCT,V_LIABILITY_SERVICES_ACCT,V_PREPAYMENT_ACCT,

        sqlCmd.append( getAcct( "V_LIABILITY_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "V_LIABILITY_SERVICES_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "V_PREPAYMENT_ACCT" )).append( ", " );

        // PAYDISCOUNT_EXP_ACCT,PAYDISCOUNT_REV_ACCT,WRITEOFF_ACCT,

        sqlCmd.append( getAcct( "PAYDISCOUNT_EXP_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "PAYDISCOUNT_REV_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "WRITEOFF_ACCT" )).append( ", " );

        // UNREALIZEDGAIN_ACCT,UNREALIZEDLOSS_ACCT,REALIZEDGAIN_ACCT,REALIZEDLOSS_ACCT,

        sqlCmd.append( getAcct( "UNREALIZEDGAIN_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "UNREALIZEDLOSS_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "REALIZEDGAIN_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "REALIZEDLOSS_ACCT" )).append( ", " );

        // WITHHOLDING_ACCT,E_PREPAYMENT_ACCT,E_EXPENSE_ACCT,

        sqlCmd.append( getAcct( "WITHHOLDING_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "E_PREPAYMENT_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "E_EXPENSE_ACCT" )).append( ", " );

        // PJ_ASSET_ACCT,PJ_WIP_ACCT,

        sqlCmd.append( getAcct( "PJ_ASSET_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "PJ_WIP_ACCT" )).append( "," );

        // T_EXPENSE_ACCT,T_LIABILITY_ACCT,T_RECEIVABLES_ACCT,T_DUE_ACCT,T_CREDIT_ACCT,

        sqlCmd.append( getAcct( "T_EXPENSE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "T_LIABILITY_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "T_RECEIVABLES_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "T_DUE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "T_CREDIT_ACCT" )).append( ", " );

        // B_INTRANSIT_ACCT,B_ASSET_ACCT,B_EXPENSE_ACCT,B_INTERESTREV_ACCT,B_INTERESTEXP_ACCT,

        sqlCmd.append( getAcct( "B_INTRANSIT_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "B_ASSET_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "B_EXPENSE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "B_INTERESTREV_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "B_INTERESTEXP_ACCT" )).append( "," );

        // B_UNIDENTIFIED_ACCT,B_SETTLEMENTGAIN_ACCT,B_SETTLEMENTLOSS_ACCT,

        sqlCmd.append( getAcct( "B_UNIDENTIFIED_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "B_SETTLEMENTGAIN_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "B_SETTLEMENTLOSS_ACCT" )).append( "," );

        // B_REVALUATIONGAIN_ACCT,B_REVALUATIONLOSS_ACCT,B_PAYMENTSELECT_ACCT,B_UNALLOCATEDCASH_ACCT,

        sqlCmd.append( getAcct( "B_REVALUATIONGAIN_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "B_REVALUATIONLOSS_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "B_PAYMENTSELECT_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "B_UNALLOCATEDCASH_ACCT" )).append( ", " );

        // CH_EXPENSE_ACCT,CH_REVENUE_ACCT,

        sqlCmd.append( getAcct( "CH_EXPENSE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "CH_REVENUE_ACCT" )).append( ", " );

        // UNEARNEDREVENUE_ACCT,NOTINVOICEDRECEIVABLES_ACCT,NOTINVOICEDREVENUE_ACCT,NOTINVOICEDRECEIPTS_ACCT,

        sqlCmd.append( getAcct( "UNEARNEDREVENUE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "NOTINVOICEDRECEIVABLES_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "NOTINVOICEDREVENUE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "NOTINVOICEDRECEIPTS_ACCT" )).append( ", " );

        // CB_ASSET_ACCT,CB_CASHTRANSFER_ACCT,CB_DIFFERENCES_ACCT,CB_EXPENSE_ACCT,CB_RECEIPT_ACCT)

        sqlCmd.append( getAcct( "CB_ASSET_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "CB_CASHTRANSFER_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "CB_DIFFERENCES_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "CB_EXPENSE_ACCT" )).append( "," );
        sqlCmd.append( getAcct( "CB_RECEIPT_ACCT" )).append( ")" );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            String err = "Default Accounts NOT inserted";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // GL Categories

        createGLCategory( "Standard",MGLCategory.CATEGORYTYPE_Manual,true );

        int GL_None = createGLCategory( "Ninguna",MGLCategory.CATEGORYTYPE_Document,false );
        int GL_GL = createGLCategory( "Manual",MGLCategory.CATEGORYTYPE_Manual,false );
        int GL_ARI = createGLCategory( "Factura a Cliente",MGLCategory.CATEGORYTYPE_Document,false );
        int GL_ARR = createGLCategory( "Cobro a Cliente",MGLCategory.CATEGORYTYPE_Document,false );
        int GL_MM = createGLCategory( "Gestion de Materiales",MGLCategory.CATEGORYTYPE_Document,false );
        int GL_API = createGLCategory( "Factura de Proveedor",MGLCategory.CATEGORYTYPE_Document,false );
        int GL_APP = createGLCategory( "Pago a Proveedor",MGLCategory.CATEGORYTYPE_Document,false );
        int GL_CASH = createGLCategory( "Caja/Pagos",MGLCategory.CATEGORYTYPE_Document,false );
        
        /*
        int GL_SignoPositivo = 1;
        int GL_SignoNegativo = -1;
        */

        // begin e-evolution vpj-cd 06/03/2005 CMPCS

        int GL_M = createGLCategory( "Manufactura ",MGLCategory.CATEGORYTYPE_Document,false );
        int GL_AMO = createGLCategory( "Amortizacion",MGLCategory.CATEGORYTYPE_Document,false );
        
        //Create all  doc types
        
        if(!this.createAllDocTypes(GL_None,GL_GL,GL_ARI,GL_ARR,GL_MM,GL_API,GL_APP,GL_CASH,GL_M, GL_AMO,1,-1)){
        	return false;
        }
        
        // Crea los controles de período para el año actual m_currentYear. Es necesario
        // hacerlo aquí ya que en este punto ya se encuentran creados los tipos de documento
        // de la compañía, y los controles de período se basan en estos tipos de documento
        // (a decir verdad, se basan en el tipo de doc. base del tipo de documento de la
        // compañía).
        if (m_currentYear != null)
        	m_currentYear.createControlsOfAllPeriods();
        
        return true;
    }    // createAccounting

    /**
     * Crea los tipos de documento
     * @param GL_None
     * @param GL_GL
     * @param GL_ARI
     * @param GL_ARR
     * @param GL_MM
     * @param GL_API
     * @param GL_APP
     * @param GL_CASH
     * @param GL_M
     * @param GL_SignoPositivo
     * @param GL_SignoNegativo
     * @return éxito del procedimiento
     */
    public boolean createAllDocTypes(int GL_None,int GL_GL,int GL_ARI,int GL_ARR,int GL_MM,int GL_API,int GL_APP,int GL_CASH,int GL_M, int GL_AMO, int GL_SignoPositivo,int GL_SignoNegativo){
    	//Crea los tipos de documento
    	createDocType( "Pedido de Mantenimiento",Msg.getElement( m_ctx,"MPC_Order_ID",false ),MDocType.DOCBASETYPE_MaintenanceOrder,null,0,0,910000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_MaintenanceOrder);
        createDocType( "Asunto de Pedido de Manufactura",Msg.getElement( m_ctx,"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderIssue,null,0,0,920000,GL_M, GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderIssue);
        createDocType( "Variacion de Metodo de Pedido de Manufactura",Msg.getElement( m_ctx,"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderMethodVariation,null,0,0,930000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderMethodVariation);
        createDocType( "Pedido de Manufactura",Msg.getElement( m_ctx,"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrder,null,0,0,940000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrder);
        createDocType( "Planificacion de pedido de Manufactura",Msg.getElement( m_ctx,"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrder,null,0,0,950000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderPlanning);
        createDocType( "Albaran de Pedido de Manufactura",Msg.getElement( m_ctx,"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderReceipt,null,0,0,960000,GL_M, GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderReceipt);
        createDocType( "Variacion de uso de Pedido de Manufactura",Msg.getElement( m_ctx,"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderUseVariation,null,0,0,970000,GL_M, GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderUseVariation);
        createDocType( "Variacion de Tasa de Pedido de Manufactura",Msg.getElement( m_ctx,"MPC_Order_ID",false ),MDocType.DOCBASETYPE_ManufacturingOrderRateVariation,null,0,0,980000,GL_M,GL_SignoPositivo, MDocType.DOCTYPE_ManufacturingOrderRateVariation);
        createDocType( "Planificacion de Aviso de Pedido de Material",Msg.getElement( m_ctx,"M_Requisition_ID",false ),MDocType.DOCBASETYPE_PurchaseRequisition,null,0,0,910000,GL_None, GL_SignoPositivo, MDocType.DOCTYPE_PurchaseRequisitionPlanning);

        // end e-evolution vpj-cd 06/03/2005 CMPCS

        // Base DocumentTypes

        int ii = createDocType( "Diario del Mayor",Msg.getElement( m_ctx,"GL_Journal_ID" ),MDocType.DOCBASETYPE_GLJournal,null,0,0,1000,GL_GL,GL_SignoPositivo, MDocType.DOCTYPE_GLJournal);

        if( ii == 0 ) {
            String err = "Document Type not created";

            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        createDocType( "Lote de Asientos",Msg.getElement( m_ctx,"GL_JournalBatch_ID" ),MDocType.DOCBASETYPE_GLJournal,null,0,0,100,GL_GL,GL_SignoPositivo, MDocType.DOCTYPE_GLJournalBatch);

        // MDocType.DOCBASETYPE_GLDocument
        //

        int DT_I = createDocType( "Factura de Cliente",Msg.getElement( m_ctx,"C_Invoice_ID",true ),MDocType.DOCBASETYPE_ARInvoice,null,0,0,100000,GL_ARI,GL_SignoPositivo, MDocType.DOCTYPE_CustomerInvoice);
        int DT_II = createDocType( "Factura de Cliente Indirecta",Msg.getElement( m_ctx,"C_Invoice_ID",true ),MDocType.DOCBASETYPE_ARInvoice,null,0,0,150000,GL_ARI,GL_SignoPositivo, MDocType.DOCTYPE_CustomerIndirectInvoice);
        int DT_IC = createDocType( "Abono de Cliente",Msg.getMsg( m_ctx,"CreditMemo" ),MDocType.DOCBASETYPE_ARCreditMemo,null,0,0,170000,GL_ARI,GL_SignoNegativo, MDocType.DOCTYPE_CustomerCreditMemo);

        // MDocType.DOCBASETYPE_ARProFormaInvoice

        createDocType( "Factura de Proveedor",Msg.getElement( m_ctx,"C_Invoice_ID",false ),MDocType.DOCBASETYPE_APInvoice,null,0,0,0,GL_API,GL_SignoNegativo, MDocType.DOCTYPE_VendorInvoice, true);
        createDocType( "Abono de Proveedor",Msg.getMsg( m_ctx,"CreditMemo" ),MDocType.DOCBASETYPE_APCreditMemo,null,0,0,0,GL_API, GL_SignoPositivo, MDocType.DOCTYPE_VendorCreditMemo, true);
        createDocType( "Corresponder Factura",Msg.getElement( m_ctx,"M_MatchInv_ID",false ),MDocType.DOCBASETYPE_MatchInvoice,null,0,0,390000,GL_API,GL_SignoPositivo, MDocType.DOCTYPE_MatchInvoice);
        createDocType( "Cobro a Cliente",Msg.getElement( m_ctx,"C_Payment_ID",true ),MDocType.DOCBASETYPE_ARReceipt,null,0,0,0,GL_ARR,GL_SignoNegativo, MDocType.DOCTYPE_CustomerReceipt);
        createDocType( "Pago a Proveedor",Msg.getElement( m_ctx,"C_Payment_ID",false ),MDocType.DOCBASETYPE_APPayment,null,0,0,0,GL_APP,GL_SignoPositivo, MDocType.DOCTYPE_VendorPayment);
        createDocType( "Asignacion","Asignacion",MDocType.DOCBASETYPE_PaymentAllocation,null,0,0,490000,GL_CASH,GL_SignoPositivo, MDocType.DOCTYPE_PaymentAllocation);
        int outTrf_id = createDocType( "Transferencia Saliente",Msg.getElement( m_ctx,"C_BankTransfer_ID",false ),MDocType.DOCBASETYPE_APPayment,null,0,0,0,GL_APP,GL_SignoPositivo, MDocType.DOCTYPE_OutgoingBankTransfer);
        int inTrf_id = createDocType( "Transferencia Entrante",Msg.getElement( m_ctx,"C_BankTransfer_ID",true ),MDocType.DOCBASETYPE_ARReceipt,null,0,0,0,GL_ARR,GL_SignoNegativo, MDocType.DOCTYPE_IncomingBankTransfer);
        // Se asignan los tipos de transferencias a la compañía.
        setClientTransferDocTypes(inTrf_id, outTrf_id);
        
        // Tipos de documentos para retenciones
        createDocType( "Comprobante de Retencion (Proveedor)",Msg.getElement( m_ctx,"CreditMemo",false ),MDocType.DOCBASETYPE_APCreditMemo,null,0,0,0,GL_API,GL_SignoPositivo, MDocType.DOCTYPE_Retencion_Receipt);
        createDocType( "Factura de Retencion (Proveedor)",Msg.getElement( m_ctx,"C_Invoice_ID",false ),MDocType.DOCBASETYPE_APInvoice,null,0,0,0,GL_API,GL_SignoNegativo, MDocType.DOCTYPE_Retencion_Invoice);
        createDocType( "Comprobante de Retencion (Cliente)",Msg.getElement( m_ctx,"CreditMemo",false ),MDocType.DOCBASETYPE_ARCreditMemo,null,0,0,0,GL_ARI,GL_SignoNegativo, MDocType.DOCTYPE_Retencion_ReceiptCustomer);
        createDocType( "Factura de Retencion (Cliente)",Msg.getElement( m_ctx,"C_Invoice_ID",false ),MDocType.DOCBASETYPE_ARInvoice,null,0,0,0,GL_ARI,GL_SignoPositivo, MDocType.DOCTYPE_Retencion_InvoiceCustomer);

        
        int DT_S = createDocType( "Albaran de Salida","Albaran de Salida",MDocType.DOCBASETYPE_MaterialDelivery,null,0,0,500000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialDelivery);
        int DT_SI = createDocType( "Albaran de Salida Indirecto","Albaran de Salida",MDocType.DOCBASETYPE_MaterialDelivery,null,0,0,550000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialIndirectDelivery);

        int DT_RM = createDocType( "Devolucion de Cliente","Devolucion de Cliente",MDocType.DOCBASETYPE_MaterialDelivery,null,0,0,570000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_CustomerReturn);
        
        createDocType( "Albaran de Entrada","Albaran de Entrada",MDocType.DOCBASETYPE_MaterialReceipt,null,0,0,0,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialReceipt);
        
        createDocType( "Devolucion de Proveedor","Devolucion de Proveedor",MDocType.DOCBASETYPE_MaterialReceipt,null,0,0,870000,GL_MM,GL_SignoNegativo, MDocType.DOCTYPE_VendorReturn);

//        int DT_RM = createDocType( "Devolucion de Material de Cliente","Devolucion de Material de Cliente",MDocType.DOCBASETYPE_MaterialReceipt,null,0,0,570000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_CustomerMaterialReturn);

        createDocType( "Pedido a Proveedor",Msg.getElement( m_ctx,"C_Order_ID",false ),MDocType.DOCBASETYPE_PurchaseOrder,null,0,0,800000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_PurchaseOrder);
        createDocType( "Corresponder PP",Msg.getElement( m_ctx,"M_MatchPO_ID",false ),MDocType.DOCBASETYPE_MatchPO,null,0,0,890000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_MatchPO);
        createDocType( "Aviso de Pedido de Material",Msg.getElement( m_ctx,"M_Requisition_ID",false ),MDocType.DOCBASETYPE_PurchaseRequisition,null,0,0,900000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_PurchaseRequisition);
        createDocType( "Extracto Bancario",Msg.getElement( m_ctx,"C_BankStatemet_ID",true ),MDocType.DOCBASETYPE_BankStatement,null,0,0,700000,GL_CASH,GL_SignoPositivo, MDocType.DOCTYPE_BankStatement);
        createDocType( "Diario de Caja",Msg.getElement( m_ctx,"C_Cash_ID",true ),MDocType.DOCBASETYPE_CashJournal,null,0,0,750000,GL_CASH,GL_SignoPositivo, MDocType.DOCTYPE_CashJournal);
        createDocType( "Movimiento de Material",Msg.getElement( m_ctx,"M_Movement_ID",false ),MDocType.DOCBASETYPE_MaterialMovement,null,0,0,610000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialMovement);
        createDocType( "Inventario Fisico",Msg.getElement( m_ctx,"M_Inventory_ID",false ),MDocType.DOCBASETYPE_MaterialPhysicalInventory,null,0,0,620000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialPhysicalInventory);
        createDocType( "Produccion",Msg.getElement( m_ctx,"M_Production_ID",false ),MDocType.DOCBASETYPE_MaterialProduction,null,0,0,630000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_MaterialProduction);
        createDocType( "Asunto de Proyecto",Msg.getElement( m_ctx,"C_ProjectIssue_ID",false ),MDocType.DOCBASETYPE_ProjectIssue,null,0,0,640000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_ProjectIssue);
        createDocType( "Ingreso/Egreso Simple","Ingreso/Egreso Simple",MDocType.DOCBASETYPE_MaterialPhysicalInventory,null,0,0,650000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_SimpleMaterialInOut);
        createDocType( "Parte de Movimientos","Parte de Movimientos",MDocType.DOCBASETYPE_MaterialDelivery,null,0,0,700000,GL_MM,GL_SignoPositivo, MDocType.DOCTYPE_ParteDeMovimientos);
        createDocType( "Parte de Movimientos Valorizados","Parte de Movimientos Valorizados",MDocType.DOCBASETYPE_APInvoice,null,0,0,0,GL_API,GL_SignoNegativo, MDocType.DOCTYPE_ParteDeMovimientosValorizados);

        // Order Entry

        createDocType( "Presupuesto en Firme","Presupuesto en Firme",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_Quotation,0,0,10000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_Quotation);
        createDocType( "Presupuesto","Presupuesto",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_Proposal,0,0,20000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_Proposal);
        createDocType( "Pedido Prepago","Pedido Prepago",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_PrepayOrder,DT_S,DT_I,30000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_PrepayOrder);
        createDocType( "RMA","RMA",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_ReturnMaterial,DT_RM,DT_IC,30000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_ReturnMaterial);
        createDocType( "Pedido","Pedido",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_StandardOrder,DT_S,DT_I,50000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_StandarOrder);
        createDocType( "Pedido a Credito","Pedido a Credito",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_OnCreditOrder,DT_SI,DT_I,60000,GL_None, GL_SignoPositivo, MDocType.DOCTYPE_OnCreditOrder);    // RE
        createDocType( "Pedido de Almacen","Pedido de Almacen",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_WarehouseOrder,DT_S,DT_I,70000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_WarehouseOrder);    // LS

        int DT = createDocType( "Ticket TPV","Ticket TPV",MDocType.DOCBASETYPE_SalesOrder,MDocType.DOCSUBTYPESO_POSOrder,DT_SI,DT_II,80000,GL_None,GL_SignoPositivo, MDocType.DOCTYPE_POSOrder);    // Bar

        // Boleta de Depósito
        createDocType( "Boleta de Deposito", "Boleta de Deposito",MDocType.DOCBASETYPE_ARReceipt,null,0,0,0,GL_ARR,GL_SignoNegativo, MDocType.DOCTYPE_DepositReceipt);
        
        // Amortización
		createDocType("Amortizacion", "Amortizacion",
				MDocType.DOCBASETYPE_Amortization, null, 0, 0, 0, GL_AMO,
				GL_SignoPositivo, MDocType.DOCTYPE_Amortization);

        // POS As Default for window SO

        createPreference( "C_DocTypeTarget_ID",String.valueOf( DT ),143 );

        // Update ClientInfo

        StringBuffer sqlCmd = new StringBuffer( "UPDATE AD_ClientInfo SET " );
        sqlCmd.append( "C_AcctSchema1_ID=" ).append( m_as.getC_AcctSchema_ID()).append( ", C_Calendar_ID=" ).append( m_calendar.getC_Calendar_ID()).append( " WHERE AD_Client_ID=" ).append( m_client.getAD_Client_ID());
        int no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            String err = "ClientInfo not updated";

            log.log( Level.SEVERE,err );
            m_info.append( err );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        // Validate Completeness

        DocumentTypeVerify.createDocumentTypes( m_ctx,getAD_Client_ID(),null,m_trx.getTrxName());
        DocumentTypeVerify.createPeriodControls( m_ctx,getAD_Client_ID(),null,m_trx.getTrxName());

        //

        log.info( "fini" );
        
        return true;

    }
    
    /**
     * Descripción de Método
     *
     *
     * @param key
     *
     * @return
     */

    public int getAcct( String key ) {
        log.fine( key );

        // Element

        int      C_ElementValue_ID = m_nap.getC_ElementValue_ID( key );
        MAccount vc                = MAccount.getDefault( m_as,true );    // optional null

        vc.setAD_Org_ID( 0 );    // will be overwritten
        vc.setAccount_ID( C_ElementValue_ID );
        vc.save();

        int C_ValidCombination_ID = vc.getC_ValidCombination_ID();

        // Error

        if( C_ValidCombination_ID == 0 ) {
            log.log( Level.SEVERE,"No account for " + key );
        }

        return C_ValidCombination_ID;
    }    // getAcct

    /**
     * Descripción de Método
     *
     *
     * @param Name
     * @param CategoryType
     * @param isDefault
     *
     * @return
     */

    public int createGLCategory( String Name,String CategoryType,boolean isDefault ) {
        MGLCategory cat = new MGLCategory( m_ctx,0,m_trx.getTrxName());

        cat.setName( Name );
        cat.setCategoryType( CategoryType );
        cat.setIsDefault( isDefault );

        if( !cat.save()) {
            log.log( Level.SEVERE,"GL Category NOT created - " + Name );

            return 0;
        }

        //

        return cat.getGL_Category_ID();
    }    // createGLCategory

    public int createDocType( String Name,String PrintName,String DocBaseType,String DocSubTypeSO,int C_DocTypeShipment_ID,int C_DocTypeInvoice_ID,int StartNo,int GL_Category_ID, int signo, String docTypeKey) {
    	return createDocType(Name, PrintName, DocBaseType, DocSubTypeSO, C_DocTypeShipment_ID, C_DocTypeInvoice_ID, StartNo, GL_Category_ID, signo, docTypeKey, false);
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param Name
     * @param PrintName
     * @param DocBaseType
     * @param DocSubTypeSO
     * @param C_DocTypeShipment_ID
     * @param C_DocTypeInvoice_ID
     * @param StartNo
     * @param GL_Category_ID
     *
     * @return
     */

    public int createDocType( String Name,String PrintName,String DocBaseType,String DocSubTypeSO,int C_DocTypeShipment_ID,int C_DocTypeInvoice_ID,int StartNo,int GL_Category_ID, int signo, String docTypeKey, boolean isFiscalDocument) {
        MSequence sequence = null;

        if( StartNo != 0 ) {
            sequence = new MSequence( m_ctx,getAD_Client_ID(),Name,StartNo,m_trx.getTrxName());


            if( !sequence.save()) {
                log.log( Level.SEVERE,"Sequence NOT created - " + Name );

                return 0;
            }
        }
        MDocType dt = new MDocType( m_ctx,DocBaseType,Name,m_trx.getTrxName());


        if( (PrintName != null) && (PrintName.length() > 0) ) {
            dt.setPrintName( PrintName );    // Defaults to Name
        }

        if( DocSubTypeSO != null ) {
            dt.setDocSubTypeSO( DocSubTypeSO );
        }

        if( C_DocTypeShipment_ID != 0 ) {
            dt.setC_DocTypeShipment_ID( C_DocTypeShipment_ID );
        }

        if( C_DocTypeInvoice_ID != 0 ) {
            dt.setC_DocTypeInvoice_ID( C_DocTypeInvoice_ID );
        }

        if( GL_Category_ID != 0 ) {
            dt.setGL_Category_ID( GL_Category_ID );
            // asigno el signo por el tipo de documento (1 o -1)
            dt.setsigno_issotrx(String.valueOf(signo));       
        }
        
        if( docTypeKey != null) {
        	dt.setDocTypeKey(docTypeKey);
        }

        if( sequence == null ) {
            dt.setIsDocNoControlled( false );
        } else {
            dt.setIsDocNoControlled( true );
            dt.setDocNoSequence_ID( sequence.getAD_Sequence_ID());
        }

        dt.setIsSOTrx();

        // Documento Fiscal - Locale AR
        if(isFiscalDocument)
        	dt.setdocsubtypeinv(dt.DOCSUBTYPEINV_Fiscal);	
        else
        	dt.setdocsubtypeinv(dt.DOCSUBTYPEINV_NoFiscal);
        
        dt.setIsFiscalDocument(isFiscalDocument);
        
        if( !dt.save()) {
            log.log( Level.SEVERE,"DocType NOT created - " + Name );

            return 0;
        }

        //

        return dt.getC_DocType_ID();
    }    // createDocType

    /**
     * Crea los datos de configuración
     * @param C_Country_ID
     * @param C_Region_ID
     * @param City
     * @param defaultEntry
     * @param defaultName
     * @param bpg
     * @param bp
     * @param C_Currency_ID
     * @return
     */
    public boolean createData(int C_Country_ID,int C_Region_ID,String City,String defaultEntry,String defaultName,MBPGroup bpg,MBPartner bp,int C_Currency_ID){
    	
    	// Create Product Category

        MProductCategory pc = new MProductCategory( m_ctx,0,m_trx.getTrxName());

        pc.setValue( defaultName );
        pc.setName( defaultName );
        pc.setIsDefault( true );

        if( pc.save()) {
            m_info.append( Msg.translate( m_lang,"M_Product_Category_ID" )).append( "=" ).append( defaultName ).append( "\n" );
        } else {
            log.log( Level.SEVERE,"Product Category NOT inserted" );
        }
    	
        // UOM (EA)

        int C_UOM_ID = 100;

        //TaxCategory
        
        int C_TaxCategory_ID = this.createTaxFeatures(C_Country_ID,defaultEntry);       

        // Create Product

        MProduct product = new MProduct( m_ctx,0,m_trx.getTrxName());

        product.setValue( defaultName );
        product.setName( defaultName );
        product.setC_UOM_ID( C_UOM_ID );
        product.setM_Product_Category_ID( pc.getM_Product_Category_ID());
        product.setC_TaxCategory_ID( C_TaxCategory_ID );

        if( product.save()) {
            m_info.append( Msg.translate( m_lang,"M_Product_ID" )).append( "=" ).append( defaultName ).append( "\n" );
        } else {
            log.log( Level.SEVERE,"Product NOT inserted" );
        }

        // Default

        StringBuffer sqlCmd = new StringBuffer( "UPDATE C_AcctSchema_Element SET " );
        sqlCmd.append( "M_Product_ID=" ).append( product.getM_Product_ID());
        sqlCmd.append( " WHERE C_AcctSchema_ID=" ).append( m_as.getC_AcctSchema_ID());
        sqlCmd.append( " AND ElementType='PR'" );
        int no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"AcctSchema Element Product NOT updated" );
        }


        // Location (Company)

        MLocation loc = new MLocation( m_ctx,C_Country_ID,C_Region_ID,City,m_trx.getTrxName());

        loc.save();
        sqlCmd = new StringBuffer( "UPDATE AD_OrgInfo SET C_Location_ID=" );
        sqlCmd.append( loc.getC_Location_ID()).append( " WHERE AD_Org_ID=" ).append( getAD_Org_ID());
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"Location NOT inserted" );
        }

        createPreference( "C_Country_ID",String.valueOf( C_Country_ID ),0 );

        // Default Warehouse

        MWarehouse wh = new MWarehouse( m_ctx,0,m_trx.getTrxName());

        wh.setValue( defaultName );
        wh.setName( defaultName );
        wh.setC_Location_ID( loc.getC_Location_ID());

        if( !wh.save()) {
            log.log( Level.SEVERE,"Warehouse NOT inserted" );
        }

        // Locator

        MLocator locator = new MLocator( wh,defaultName );

        locator.setIsDefault( true );

        if( !locator.save()) {
            log.log( Level.SEVERE,"Locator NOT inserted" );
        }

        // Update ClientInfo

        sqlCmd = new StringBuffer( "UPDATE AD_ClientInfo SET " );
        sqlCmd.append( "C_BPartnerCashTrx_ID=" ).append( bp.getC_BPartner_ID());
        sqlCmd.append( ",M_ProductFreight_ID=" ).append( product.getM_Product_ID());

//              sqlCmd.append("C_UOM_Volume_ID=");
//              sqlCmd.append(",C_UOM_Weight_ID=");
//              sqlCmd.append(",C_UOM_Length_ID=");
//              sqlCmd.append(",C_UOM_Time_ID=");

        sqlCmd.append( " WHERE AD_Client_ID=" ).append( getAD_Client_ID());
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            String err = "ClientInfo not updated";

            log.log( Level.SEVERE,err );
            m_info.append( err );

            return false;
        }

        // PriceList

        MPriceList pl = new MPriceList( m_ctx,0,m_trx.getTrxName());

        pl.setName( defaultName );
        pl.setC_Currency_ID( C_Currency_ID );
        pl.setIsDefault( true );

        if( !pl.save()) {
            log.log( Level.SEVERE,"PriceList NOT inserted" );
        }

        // Price List

        MDiscountSchema ds = new MDiscountSchema( m_ctx,0,m_trx.getTrxName());

        ds.setName( defaultName );
        ds.setDiscountType( MDiscountSchema.DISCOUNTTYPE_Pricelist );

        if( !ds.save()) {
            log.log( Level.SEVERE,"DiscountSchema NOT inserted" );
        }

        // PriceList Version

        MPriceListVersion plv = new MPriceListVersion( pl );

        plv.setName();
        plv.setM_DiscountSchema_ID( ds.getM_DiscountSchema_ID());

        if( !plv.save()) {
            log.log( Level.SEVERE,"PriceList_Version NOT inserted" );
        }

        // ProductPrice

        MProductPrice pp = new MProductPrice( plv,product.getM_Product_ID(),Env.ONE,Env.ONE,Env.ONE );

        if( !pp.save()) {
            log.log( Level.SEVERE,"ProductPrice NOT inserted" );
        }

        //BPartners
        
        this.createPartners(bpg.getC_BP_Group_ID(),C_Country_ID,C_Region_ID,City);
        
        // Payment Term

        int C_PaymentTerm_ID = getNextID( getAD_Client_ID(),"C_PaymentTerm" );

        sqlCmd = new StringBuffer( "INSERT INTO C_PaymentTerm " );
        sqlCmd.append( "(C_PaymentTerm_ID," ).append( m_stdColumns ).append( "," );
        sqlCmd.append( "Value,Name,NetDays,GraceDays,DiscountDays,Discount,DiscountDays2,Discount2,IsDefault) VALUES (" );
        sqlCmd.append( C_PaymentTerm_ID ).append( "," ).append( m_stdValues ).append( "," );
        sqlCmd.append( "'Immediate','Immediate',0,0,0,0,0,0,'Y')" );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"PaymentTerm NOT inserted" );
        }

        return true;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param C_Country_ID
     * @param City
     * @param C_Region_ID
     * @param C_Currency_ID
     *
     * @return
     */

    public boolean createEntities( int C_Country_ID,String City,int C_Region_ID,int C_Currency_ID ) {
        if( m_as == null ) {
            log.severe( "No AcctountingSChema" );
            m_trx.rollback();
            m_trx.close();

            return false;
        }

        log.info( "C_Country_ID=" + C_Country_ID + ", City=" + City + ", C_Region_ID=" + C_Region_ID );
        m_info.append( "\n----\n" );

        //

        String       defaultName  = Msg.translate( m_lang,"Standard" );
        String       defaultEntry = "'" + defaultName + "',";
        StringBuffer sqlCmd       = null;
        int          no           = 0;

        // Create Marketing Channel/Campaign

        int C_Channel_ID = getNextID( getAD_Client_ID(),"C_Channel" );

        sqlCmd = new StringBuffer( "INSERT INTO C_Channel " );
        sqlCmd.append( "(C_Channel_ID,Name," );
        sqlCmd.append( m_stdColumns ).append( ") VALUES (" );
        sqlCmd.append( C_Channel_ID ).append( "," ).append( defaultEntry );
        sqlCmd.append( m_stdValues ).append( ")" );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"Channel NOT inserted" );
        }

               
        // Create Sales Region

        int C_SalesRegion_ID = getNextID( getAD_Client_ID(),"C_SalesRegion" );

        sqlCmd = new StringBuffer( "INSERT INTO C_SalesRegion " );
        sqlCmd.append( "(C_SalesRegion_ID," ).append( m_stdColumns ).append( "," );
        sqlCmd.append( " Value,Name,IsSummary) VALUES (" );
        sqlCmd.append( C_SalesRegion_ID ).append( "," ).append( m_stdValues ).append( ", " );
        sqlCmd.append( defaultEntry ).append( defaultEntry ).append( "'N')" );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no == 1 ) {
            m_info.append( Msg.translate( m_lang,"C_SalesRegion_ID" )).append( "=" ).append( defaultName ).append( "\n" );
        } else {
            log.log( Level.SEVERE,"SalesRegion NOT inserted" );
        }

        if( m_hasSRegion ) {

            // Default

            sqlCmd = new StringBuffer( "UPDATE C_AcctSchema_Element SET " );
            sqlCmd.append( "C_SalesRegion_ID=" ).append( C_SalesRegion_ID );
            sqlCmd.append( " WHERE C_AcctSchema_ID=" ).append( m_as.getC_AcctSchema_ID());
            sqlCmd.append( " AND ElementType='SR'" );
            no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

            if( no != 1 ) {
                log.log( Level.SEVERE,"AcctSchema ELement SalesRegion NOT updated" );
            }
        }

        // Create BP Group

        MBPGroup bpg = new MBPGroup( m_ctx,0,m_trx.getTrxName());

        bpg.setValue( defaultName );
        bpg.setName( defaultName );
        bpg.setIsDefault( true );
        
        if( bpg.save()) {
            m_info.append( Msg.translate( m_lang,"C_BP_Group_ID" )).append( "=" ).append( defaultName ).append( "\n" );
            Env.setContext( m_ctx,"#C_BP_Group_ID", bpg.getID());
        } else {
            log.log( Level.SEVERE,"BP Group NOT inserted" );
        }

        // Create BPartner

        MBPartner bp = new MBPartner( m_ctx,0,m_trx.getTrxName());

        bp.setValue( defaultName );
        bp.setName( defaultName );
        bp.setC_BP_Group_ID( bpg.getC_BP_Group_ID());
        bp.setIsCustomer(true);
        bp.setIsVendor(true);

        if( bp.save()) {
            m_info.append( Msg.translate( m_lang,"C_BPartner_ID" )).append( "=" ).append( defaultName ).append( "\n" );
        } else {
            log.log( Level.SEVERE,"BPartner NOT inserted" );
        }

        // Location for Standard BP

        MLocation bpLoc = new MLocation( m_ctx,C_Country_ID,C_Region_ID,City,m_trx.getTrxName());

        bpLoc.save();

        MBPartnerLocation bpl = new MBPartnerLocation( bp );

        bpl.setC_Location_ID( bpLoc.getC_Location_ID());

        if( !bpl.save()) {
            log.log( Level.SEVERE,"BP_Location (Standard) NOT inserted" );
        }

        // Default

        sqlCmd = new StringBuffer( "UPDATE C_AcctSchema_Element SET " );
        sqlCmd.append( "C_BPartner_ID=" ).append( bp.getC_BPartner_ID());
        sqlCmd.append( " WHERE C_AcctSchema_ID=" ).append( m_as.getC_AcctSchema_ID());
        sqlCmd.append( " AND ElementType='BP'" );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"AcctSchema Element BPartner NOT updated" );
        }

        createPreference( "C_BPartner_ID",String.valueOf( bp.getC_BPartner_ID()),143 );

        
        // Crea los datos de configuración, como por ejemplo, producto, categoría de iva, esquema de vencimiento ,etc
        if(!this.createData(C_Country_ID,C_Region_ID,City,defaultEntry,defaultName,bpg,bp,C_Currency_ID)){
        	m_trx.rollback();
        	m_trx.close();
        	return false;
        }
        
        // Project Cycle

        C_Cycle_ID = getNextID( getAD_Client_ID(),"C_Cycle" );
        sqlCmd     = new StringBuffer( "INSERT INTO C_Cycle " );
        sqlCmd.append( "(C_Cycle_ID," ).append( m_stdColumns ).append( "," );
        sqlCmd.append( " Name,C_Currency_ID) VALUES (" );
        sqlCmd.append( C_Cycle_ID ).append( "," ).append( m_stdValues ).append( ", " );
        sqlCmd.append( defaultEntry ).append( C_Currency_ID ).append( ")" );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"Cycle NOT inserted" );
        }

        // Create Project y Campaign
    	int C_Campaign_ID = getNextID( getAD_Client_ID(),"C_Campaign" );

        sqlCmd = new StringBuffer( "INSERT INTO C_Campaign " );
        sqlCmd.append( "(C_Campaign_ID,C_Channel_ID," ).append( m_stdColumns ).append( "," );
        sqlCmd.append( " Value,Name,Costs) VALUES (" );
        sqlCmd.append( C_Campaign_ID ).append( "," ).append( C_Channel_ID ).append( "," ).append( m_stdValues ).append( "," );
        sqlCmd.append( defaultEntry ).append( defaultEntry ).append( "0)" );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no == 1 ) {
            m_info.append( Msg.translate( m_lang,"C_Campaign_ID" )).append( "=" ).append( defaultName ).append( "\n" );
        } else {
            log.log( Level.SEVERE,"Campaign NOT inserted" );
        }

        if( m_hasMCampaign ) {

            // Default

            sqlCmd = new StringBuffer( "UPDATE C_AcctSchema_Element SET " );
            sqlCmd.append( "C_Campaign_ID=" ).append( C_Campaign_ID );
            sqlCmd.append( " WHERE C_AcctSchema_ID=" ).append( m_as.getC_AcctSchema_ID());
            sqlCmd.append( " AND ElementType='MC'" );
            no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

            if( no != 1 ) {
                log.log( Level.SEVERE,"AcctSchema ELement Campaign NOT updated" );
            }
        }
        
        
        // Create Default Project

        int C_Project_ID = getNextID( getAD_Client_ID(),"C_Project" );

        sqlCmd = new StringBuffer( "INSERT INTO C_Project " );
        sqlCmd.append( "(C_Project_ID," ).append( m_stdColumns ).append( "," );
        sqlCmd.append( " Value,Name,C_Currency_ID,IsSummary) VALUES (" );
        sqlCmd.append( C_Project_ID ).append( "," ).append( getM_stdValuesOrg() ).append( ", " );
        sqlCmd.append( defaultEntry ).append( defaultEntry ).append( C_Currency_ID ).append( ",'N')" );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no == 1 ) {
            m_info.append( Msg.translate( m_lang,"C_Project_ID" )).append( "=" ).append( defaultName ).append( "\n" );
        } else {
            log.log( Level.SEVERE,"Project NOT inserted" );
        }

        // Default Project

        if( m_hasProject ) {
            sqlCmd = new StringBuffer( "UPDATE C_AcctSchema_Element SET " );
            sqlCmd.append( "C_Project_ID=" ).append( C_Project_ID );
            sqlCmd.append( " WHERE C_AcctSchema_ID=" ).append( m_as.getC_AcctSchema_ID());
            sqlCmd.append( " AND ElementType='PJ'" );
            no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

            if( no != 1 ) {
                log.log( Level.SEVERE,"AcctSchema ELement Project NOT updated" );
            }
        }
                        
        // CashBook

        MCashBook cb = new MCashBook( m_ctx,0,m_trx.getTrxName());

        cb.setName( defaultName );
        cb.setC_Currency_ID( C_Currency_ID );

        if( cb.save()) {
            m_info.append( Msg.translate( m_lang,"C_CashBook_ID" )).append( "=" ).append( defaultName ).append( "\n" );
        } else {
            log.log( Level.SEVERE,"CashBook NOT inserted" );
        }

        // Locale Entities
        if (!createLocaleEntities()) {
        	m_trx.rollback();
        	m_trx.close();
        	return false;
        }        
        
        //Commit and close trx
        this.commitAndCloseTrx();
        
        return true;
    }    // createEntities
    
    private boolean createLocaleEntities() {
    	try {
			String msg = LocaleActivation.createClientLocaleData(m_client.getAD_Client_ID(), m_trx.getTrxName());
			if (msg != null && msg.length() > 0) {
		        m_info.append( "\n----\n" );
				m_info.append(Msg.parseTranslation(Env.getCtx(),msg));
			}
		} catch (Exception e) {
			log.saveError("ClientSetupError", e.getMessage(), true);
			return false;
		}
		return true;
    }
    
    /**
     * Commit and close the current transaction
     */
    protected void commitAndCloseTrx(){
    	m_trx.commit();
        m_trx.close();
        log.info( "fini" );
    }

    /**
     * Crea las entidades correspondientes
     * @param C_BP_Group_ID id del grupo de entidad comercial
     * @param C_Country_ID id del país
     * @param C_Region_ID id de la región
     * @param City nombre de la ciudad
     */
    public void createPartners(int C_BP_Group_ID, int C_Country_ID,int C_Region_ID,String City){
    	// Create Sales Rep for Client-User

        MBPartner bpCU = new MBPartner( m_ctx,0,m_trx.getTrxName());

        bpCU.setValue( AD_User_U_Name );
        bpCU.setName( AD_User_U_Name );
        bpCU.setC_BP_Group_ID( C_BP_Group_ID);
        bpCU.setIsEmployee( true );
        bpCU.setIsSalesRep( true );

        if( bpCU.save()) {
            m_info.append( Msg.translate( m_lang,"SalesRep_ID" )).append( "=" ).append( AD_User_U_Name ).append( "\n" );
        } else {
            log.log( Level.SEVERE,"SalesRep (User) NOT inserted" );
        }

        // Location for Client-User

        MLocation bpLocCU = new MLocation( m_ctx,C_Country_ID,C_Region_ID,City,m_trx.getTrxName());

        bpLocCU.save();

        MBPartnerLocation bplCU = new MBPartnerLocation( bpCU );

        bplCU.setC_Location_ID( bpLocCU.getC_Location_ID());

        if( !bplCU.save()) {
            log.log( Level.SEVERE,"BP_Location (User) NOT inserted" );
        }

        // Update User

        StringBuffer sqlCmd = new StringBuffer( "UPDATE AD_User SET C_BPartner_ID=" );
        sqlCmd.append( bpCU.getC_BPartner_ID()).append( " WHERE AD_User_ID=" ).append( AD_User_U_ID );
        int no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"User of SalesRep (User) NOT updated" );
        }

        // Create Sales Rep for Client-Admin

        MBPartner bpCA = new MBPartner( m_ctx,0,m_trx.getTrxName());

        bpCA.setValue( AD_User_Name );
        bpCA.setName( AD_User_Name );
        bpCA.setC_BP_Group_ID( C_BP_Group_ID);
        bpCA.setIsEmployee( true );
        bpCA.setIsSalesRep( true );

        if( bpCA.save()) {
            m_info.append( Msg.translate( m_lang,"SalesRep_ID" )).append( "=" ).append( AD_User_Name ).append( "\n" );
        } else {
            log.log( Level.SEVERE,"SalesRep (Admin) NOT inserted" );
        }

        // Location for Client-Admin

        MLocation bpLocCA = new MLocation( m_ctx,C_Country_ID,C_Region_ID,City,m_trx.getTrxName());

        bpLocCA.save();

        MBPartnerLocation bplCA = new MBPartnerLocation( bpCA );

        bplCA.setC_Location_ID( bpLocCA.getC_Location_ID());

        if( !bplCA.save()) {
            log.log( Level.SEVERE,"BP_Location (Admin) NOT inserted" );
        }

        // Update User

        sqlCmd = new StringBuffer( "UPDATE AD_User SET C_BPartner_ID=" );
        sqlCmd.append( bpCA.getC_BPartner_ID()).append( " WHERE AD_User_ID=" ).append( AD_User_ID );
        no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"User of SalesRep (Admin) NOT updated" );
        }

    }
    
    /**
     * Crea la categoría de impuesto y las tasas
     * @param C_Country_ID 
     * @param defaultEntry
     * @return id de categoría de impuesto creada
     */
    public int createTaxFeatures(int C_Country_ID,String defaultEntry){
    	
    	int C_TaxCategory_ID = getNextID( getAD_Client_ID(),"C_TaxCategory" );

        StringBuffer sqlCmd = new StringBuffer( "INSERT INTO C_TaxCategory " );
        sqlCmd.append( "(C_TaxCategory_ID," ).append( m_stdColumns ).append( "," );
        sqlCmd.append( " Name,IsDefault) VALUES (" );
        sqlCmd.append( C_TaxCategory_ID ).append( "," ).append( m_stdValues ).append( ", " );

        if( C_Country_ID == 100 ) {    // US
            sqlCmd.append( "'Sales Tax','Y')" );
        } else {
            sqlCmd.append( defaultEntry ).append( "'Y')" );
        }

        int no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"TaxCategory NOT inserted" );
        }

        // Tax - Zero Rate

        MTax tax = new MTax( m_ctx,"Standard",Env.ZERO,C_TaxCategory_ID,m_trx.getTrxName());

        tax.setIsDefault( true );
        tax.setTaxType(MTax.TAXTYPE_General);
        
        if( tax.save()) {
            m_info.append( Msg.translate( m_lang,"C_Tax_ID" )).append( "=" ).append( tax.getName()).append( "\n" );
        } else {
            log.log( Level.SEVERE,"Tax NOT inserted" );
        }

        return C_TaxCategory_ID;
    }
    
    
    /**
     * Descripción de Método
     *
     *
     * @param Attribute
     * @param Value
     * @param AD_Window_ID
     */

    public void createPreference( String Attribute,String Value,int AD_Window_ID ) {
        int          AD_Preference_ID = getNextID( getAD_Client_ID(),"AD_Preference" );
        StringBuffer sqlCmd           = new StringBuffer( "INSERT INTO AD_Preference " );

        sqlCmd.append( "(AD_Preference_ID," ).append( m_stdColumns ).append( "," );
        sqlCmd.append( "Attribute,Value,AD_Window_ID) VALUES (" );
        sqlCmd.append( AD_Preference_ID ).append( "," ).append( m_stdValues ).append( "," );
        sqlCmd.append( "'" ).append( Attribute ).append( "','" ).append( Value ).append( "'," );

        if( AD_Window_ID == 0 ) {
            sqlCmd.append( "NULL)" );
        } else {
            sqlCmd.append( AD_Window_ID ).append( ")" );
        }

        int no = DB.executeUpdate( sqlCmd.toString(),m_trx.getTrxName());

        if( no != 1 ) {
            log.log( Level.SEVERE,"Preference NOT inserted - " + Attribute );
        }
    }    // createPreference

    /**
     * Descripción de Método
     *
     *
     * @param AD_Client_ID
     * @param TableName
     *
     * @return
     */

    public int getNextID( int AD_Client_ID,String TableName ) {

        // TODO: Exception

        return DB.getNextID( AD_Client_ID,TableName,m_trx.getTrxName());
    }    // getNextID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Client_ID() {
        return m_client.getAD_Client_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Org_ID() {
        return m_org.getAD_Org_ID();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_User_ID() {
        return AD_User_ID;
    }

    public void setM_ctx(Properties m_ctx) {
		this.m_ctx = m_ctx;
	}

	public Properties getM_ctx() {
		return m_ctx;
	}

	public void setM_trx(Trx m_trx) {
		this.m_trx = m_trx;
	}

	public Trx getM_trx() {
		return m_trx;
	}

	public void setM_info(StringBuffer m_info) {
		this.m_info = m_info;
	}

	public StringBuffer getM_info() {
		return m_info;
	}

	public void setM_as(MAcctSchema m_as) {
		this.m_as = m_as;
	}

	public MAcctSchema getM_as() {
		return m_as;
	}

	public void setM_calendar(MCalendar m_calendar) {
		this.m_calendar = m_calendar;
	}

	public MCalendar getM_calendar() {
		return m_calendar;
	}

	public void setM_client(MClient m_client) {
		this.m_client = m_client;
	}

	public MClient getM_client() {
		return m_client;
	}

	public void setM_lang(String m_lang) {
		this.m_lang = m_lang;
	}


	public String getM_lang() {
		return m_lang;
	}


	public void setM_stdColumns(String m_stdColumns) {
		this.m_stdColumns = m_stdColumns;
	}


	public String getM_stdColumns() {
		return m_stdColumns;
	}


	public void setM_stdValues(String m_stdValues) {
		this.m_stdValues = m_stdValues;
	}


	public String getM_stdValues() {
		return m_stdValues;
	}


	/**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getInfo() {
        return m_info.toString();
    }


	public void setM_stdValuesOrg(String m_stdValuesOrg) {
		this.m_stdValuesOrg = m_stdValuesOrg;
	}


	public String getM_stdValuesOrg() {
		return m_stdValuesOrg;
	}
	
	protected void setClientTransferDocTypes(int incomingTransferDT_ID, int outgoingTransferDT_ID) {
 		MClientInfo clientInfo = MClientInfo.get(m_ctx, m_client.getAD_Client_ID(), m_trx.getTrxName());
		clientInfo.setC_IncomingTransfer_DT_ID(incomingTransferDT_ID);
		clientInfo.setC_OutgoingTransfer_DT_ID(outgoingTransferDT_ID);
		clientInfo.save();
	}
}    // MSetup



/*
 *  @(#)MSetup.java   02.07.07
 *
 *  Fin del fichero MSetup.java
 *
 *  Versión 2.1
 *
 */
