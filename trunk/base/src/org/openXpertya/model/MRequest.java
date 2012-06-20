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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.EMail;
import org.openXpertya.util.EMailUtil;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MRequest extends X_R_Request {

    /**
     * Descripción de Método
     *
     *
     * @param mailText
     *
     * @return
     */

    public static int getR_Request_ID( String mailText ) {
        if( mailText == null ) {
            return 0;
        }

        int indexStart = mailText.indexOf( TAG_START );

        if( indexStart == -1 ) {
            return 0;
        }

        int indexEnd = mailText.indexOf( TAG_END,indexStart );

        if( indexEnd == -1 ) {
            return 0;
        }

        //

        indexStart += 5;

        String idString     = mailText.substring( indexStart,indexEnd );
        int    R_Request_ID = 0;

        try {
            R_Request_ID = Integer.parseInt( idString );
        } catch( Exception e ) {
            s_log.severe( "Cannot parse " + idString );
        }

        return R_Request_ID;
    }    // getR_Request_ID

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MRequest.class );

    /** Descripción de Campos */

    private static final String TAG_START = "[Req@";

    /** Descripción de Campos */

    private static final String TAG_END = "@ID]";

    /** Descripción de Campos */

    private static final String SEPARATOR = "---------.----------.----------.----------.----------.----------";

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param R_Request_ID
     * @param trxName
     */

    public MRequest( Properties ctx,int R_Request_ID,String trxName ) {
        super( ctx,R_Request_ID,trxName );

        if( R_Request_ID == 0 ) {
            setDueType( DUETYPE_Due );

            // setSalesRep_ID (0);
            // setDocumentNo (null);

            setConfidentialType( CONFIDENTIALTYPE_CustomerConfidential );              // A
            setConfidentialTypeEntry( CONFIDENTIALTYPEENTRY_CustomerConfidential );    // A
            setProcessed( false );
            setRequestAmt( Env.ZERO );
            setPriorityUser( PRIORITY_Low );

            // setR_RequestType_ID (0);
            // setSummary (null);

            setIsEscalated( false );
            setIsSelfService( false );
            setIsInvoiced( false );
        }
    }    // MRequest

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param SalesRep_ID
     * @param R_RequestType_ID
     * @param Summary
     * @param isSelfService
     * @param trxName
     */

    public MRequest( Properties ctx,int SalesRep_ID,int R_RequestType_ID,String Summary,boolean isSelfService,String trxName ) {
        this( ctx,0,trxName );
        setSalesRep_ID( SalesRep_ID );
        setR_RequestType_ID( R_RequestType_ID );
        setSummary( Summary );
        setIsSelfService( isSelfService );
        getRequestType();

        if( m_requestType != null ) {
            String ct = m_requestType.getConfidentialType();

            if( ct != null ) {
                setConfidentialType( ct );
                setConfidentialTypeEntry( ct );
            }
        }
    }    // MRequest

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MRequest( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MRequest

    /** Descripción de Campos */

    private MRequestType m_requestType = null;

    /** Descripción de Campos */

    private boolean m_changed = false;

    /** Descripción de Campos */

    private MBPartner m_partner = null;

    /** Descripción de Campos */

    private MUser m_user = null;

    /**
     * Descripción de Método
     *
     */

    public void setR_RequestType_ID() {
        m_requestType = MRequestType.getDefault( getCtx());

        if( m_requestType == null ) {
            log.warning( "No default found" );
        } else {
            super.setR_RequestType_ID( m_requestType.getR_RequestType_ID());
        }
    }    // setR_RequestType_ID

    /**
     * Descripción de Método
     *
     */

    public void setR_Status_ID() {
        MStatus status = MStatus.getDefault( getCtx());

        if( status == null ) {
            log.warning( "No default found" );
        } else {
            super.setR_Status_ID( status.getR_Status_ID());
        }
    }    // setR_Status_ID

    /**
     * Descripción de Método
     *
     *
     * @param Result
     */

    public void addToResult( String Result ) {
        String oldResult = getResult();

        if( (Result == null) || (Result.length() == 0) ) {
            ;
        } else if( (oldResult == null) || (oldResult.length() == 0) ) {
            setResult( Result );
        } else {
            setResult( oldResult + "\n-\n" + Result );
        }
    }    // addToResult

    /**
     * Descripción de Método
     *
     */

    public void setDueType() {
        Timestamp due = getDateNextAction();

        if( due == null ) {
            return;
        }

        //

        Timestamp overdue = TimeUtil.addDays( due,getRequestType().getDueDateTolerance());
        Timestamp now = new Timestamp( System.currentTimeMillis());

        //

        String DueType = DUETYPE_Due;

        if( now.before( due )) {
            DueType = DUETYPE_Scheduled;
        } else if( now.after( overdue )) {
            DueType = DUETYPE_Overdue;
        }

        super.setDueType( DueType );
    }    // setDueType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRequestAction[] getActions() {
        String sql = "SELECT * FROM R_RequestAction " + "WHERE R_Request_ID=? " + "ORDER BY Created DESC";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getR_Request_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MRequestAction( getCtx(),rs,get_TrxName()));
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

        MRequestAction[] retValue = new MRequestAction[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getActions

    /**
     * Descripción de Método
     *
     *
     * @param confidentialType
     *
     * @return
     */

    public MRequestUpdate[] getUpdates( String confidentialType ) {
        String sql = "SELECT * FROM R_RequestUpdate " + "WHERE R_Request_ID=? " + "ORDER BY Created DESC";
        ArrayList         list  = new ArrayList();
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getR_Request_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                MRequestUpdate ru = new MRequestUpdate( getCtx(),rs,get_TrxName());

                if( confidentialType != null ) {

                    // Private only if private

                    if( ru.getConfidentialTypeEntry().equals( CONFIDENTIALTYPEENTRY_PrivateInformation ) &&!confidentialType.equals( CONFIDENTIALTYPEENTRY_PrivateInformation )) {
                        continue;
                    }

                    // Internal not if Customer/Public

                    if( ru.getConfidentialTypeEntry().equals( CONFIDENTIALTYPEENTRY_Internal ) && ( confidentialType.equals( CONFIDENTIALTYPEENTRY_CustomerConfidential ) || confidentialType.equals( CONFIDENTIALTYPEENTRY_PublicInformation ))) {
                        continue;
                    }

                    // No Customer if public

                    if( ru.getConfidentialTypeEntry().equals( CONFIDENTIALTYPEENTRY_CustomerConfidential ) && confidentialType.equals( CONFIDENTIALTYPEENTRY_PublicInformation )) {
                        continue;
                    }
                }

                list.add( ru );
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

        MRequestUpdate[] retValue = new MRequestUpdate[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getUpdates

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRequestUpdate[] getUpdatesPublic() {
        return getUpdates( CONFIDENTIALTYPE_PublicInformation );
    }    // getUpdatesPublic

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRequestUpdate[] getUpdatesCustomer() {
        return getUpdates( CONFIDENTIALTYPE_CustomerConfidential );
    }    // getUpdatesCustomer

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MRequestType getRequestType() {
        if( m_requestType == null ) {
            int R_RequestType_ID = getR_RequestType_ID();

            if( R_RequestType_ID == 0 ) {
                setR_RequestType_ID();
                R_RequestType_ID = getR_RequestType_ID();
            }

            m_requestType = MRequestType.get( getCtx(),R_RequestType_ID );
        }

        return m_requestType;
    }    // getRequestType

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getRequestTypeName() {
        if( m_requestType == null ) {
            getRequestType();
        }

        if( m_requestType == null ) {
            return "??";
        }

        return m_requestType.getName();
    }    // getRequestTypeText

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MCategory getCategory() {
        if( getR_Category_ID() == 0 ) {
            return null;
        }

        return MCategory.get( getCtx(),getR_Category_ID());
    }    // getCategory

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCategoryName() {
        MCategory cat = getCategory();

        if( cat == null ) {
            return "";
        }

        return cat.getName();
    }    // getCategoryName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MGroup getGroup() {
        if( getR_Group_ID() == 0 ) {
            return null;
        }

        return MGroup.get( getCtx(),getR_Group_ID());
    }    // getGroup

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getGroupName() {
        MGroup grp = getGroup();

        if( grp == null ) {
            return "";
        }

        return grp.getName();
    }    // getGroupName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MStatus getStatus() {
        if( getR_Status_ID() == 0 ) {
            return null;
        }

        return MStatus.get( getCtx(),getR_Status_ID());
    }    // getStatus

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getStatusName() {
        MStatus sta = getStatus();

        if( sta == null ) {
            return "?";
        }

        return sta.getName();
    }    // getStatusName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MResolution getResolution() {
        if( getR_Resolution_ID() == 0 ) {
            return null;
        }

        return MResolution.get( getCtx(),getR_Resolution_ID());
    }    // getResolution

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getResolutionName() {
        MResolution res = getResolution();

        if( res == null ) {
            return "";
        }

        return res.getName();
    }    // getResolutionName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDueTypeText() {
        return MRefList.getListName( getCtx(),DUETYPE_AD_Reference_ID,getDueType());
    }    // getDueTypeText

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPriorityText() {
        return MRefList.getListName( getCtx(),PRIORITY_AD_Reference_ID,getPriority());
    }    // getPriorityText

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPriorityUserText() {
        return MRefList.getListName( getCtx(),PRIORITYUSER_AD_Reference_ID,getPriorityUser());
    }    // getPriorityUserText

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getConfidentialText() {
        return MRefList.getListName( getCtx(),CONFIDENTIALTYPE_AD_Reference_ID,getConfidentialType());
    }    // getConfidentialText

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getConfidentialEntryText() {
        return MRefList.getListName( getCtx(),CONFIDENTIALTYPEENTRY_AD_Reference_ID,getConfidentialTypeEntry());
    }    // getConfidentialTextEntry

    /**
     * Descripción de Método
     *
     */

    public void setDateLastAlert() {
        super.setDateLastAlert( new Timestamp( System.currentTimeMillis()));
    }    // setDateLastAlert

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MUser getSalesRep() {
        if( getSalesRep_ID() == 0 ) {
            return null;
        }

        return MUser.get( getCtx(),getSalesRep_ID());
    }    // getSalesRep

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSalesRepName() {
        MUser sr = getSalesRep();

        if( sr == null ) {
            return "n/a";
        }

        return sr.getName();
    }    // getSalesRepName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCreatedByName() {
        MUser user = MUser.get( getCtx(),getCreatedBy());

        return user.getName();
    }    // getCreatedByName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MUser getUser() {
        if( getAD_User_ID() == 0 ) {
            return null;
        }

        if( (m_user != null) && (m_user.getAD_User_ID() != getAD_User_ID())) {
            m_user = null;
        }

        if( m_user == null ) {
            m_user = new MUser( getCtx(),getAD_User_ID(),get_TrxName());
        }

        return m_user;
    }    // getUser

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MBPartner getBPartner() {
        if( getC_BPartner_ID() == 0 ) {
            return null;
        }

        if( (m_partner != null) && (m_partner.getC_BPartner_ID() != getC_BPartner_ID())) {
            m_partner = null;
        }

        if( m_partner == null ) {
            m_partner = new MBPartner( getCtx(),getC_BPartner_ID(),get_TrxName());
        }

        return m_partner;
    }    // getSalesRep

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWebCanUpdate() {
        if( isProcessed()) {
            return false;
        }

        MStatus status = MStatus.get( getCtx(),getR_Status_ID());

        return status.isWebCanUpdate();
    }    // isWebCanUpdate

    /**
     * Descripción de Método
     *
     */

    private void setPriority() {
        if( getPriorityUser() == null ) {
            setPriorityUser( PRIORITYUSER_Low );
        }

        //

        if( getBPartner() != null ) {
            MBPGroup bpg = MBPGroup.get( getCtx(),getBPartner().getC_BP_Group_ID());
            String prioBase = bpg.getPriorityBase();

            if( (prioBase != null) &&!prioBase.equals( MBPGroup.PRIORITYBASE_Same )) {
                char targetPrio = getPriorityUser().charAt( 0 );

                if( prioBase.equals( MBPGroup.PRIORITYBASE_Lower )) {
                    targetPrio += 2;
                } else {
                    targetPrio -= 2;
                }

                if( targetPrio < PRIORITY_High.charAt( 0 )) {    // 1
                    targetPrio = PRIORITY_High.charAt( 0 );
                }

                if( targetPrio > PRIORITY_Low.charAt( 0 )) {     // 9
                    targetPrio = PRIORITY_Low.charAt( 0 );
                }

                if( getPriority() == null ) {
                    setPriority( String.valueOf( targetPrio ));
                } else                                           // previous priority
                {
                    if( targetPrio < getPriority().charAt( 0 )) {
                        setPriority( String.valueOf( targetPrio ));
                    }
                }
            }
        }

        // Same if nothing else

        if( getPriority() == null ) {
            setPriority( getPriorityUser());
        }
    }    // setPriority

    /**
     * Descripción de Método
     *
     *
     * @param result
     *
     * @return
     */

    public boolean webUpdate( String result ) {
        MStatus status = MStatus.get( getCtx(),getR_Status_ID());

        if( !status.isWebCanUpdate()) {
            return false;
        }

        if( status.getUpdate_Status_ID() > 0 ) {
            setR_Status_ID( status.getUpdate_Status_ID());
        }

        setResult( result );

        return true;
    }    // webUpdate

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     *
     * @return
     */

    protected boolean beforeSave( boolean newRecord ) {

        // Request Type

        if( getR_RequestType_ID() == 0 ) {
            setR_RequestType_ID();    // sets default
        }

        getRequestType();

        if( newRecord || is_ValueChanged( "R_RequestType_ID" )) {
            if( m_requestType != null ) {
                if( isInvoiced() != m_requestType.isInvoiced()) {
                    setIsInvoiced( m_requestType.isInvoiced());
                }

                if( (getDateNextAction() == null) && (m_requestType.getAutoDueDateDays() > 0) ) {
                    setDateNextAction( TimeUtil.addDays( new Timestamp( System.currentTimeMillis()),m_requestType.getAutoDueDateDays()));
                }
            }
        }

        // Request Status

        if( getR_Status_ID() == 0 ) {
            setR_Status_ID();
        }

        // Validate/Update Due Type

        setDueType();

        MStatus status = MStatus.get( getCtx(),getR_Status_ID());

        // Close/Open

        if( status != null ) {
            if( status.isOpen()) {
                if( getStartDate() == null ) {
                    setStartDate( new Timestamp( System.currentTimeMillis()));
                }

                if( getCloseDate() != null ) {
                    setCloseDate( null );
                }
            }

            if( status.isClosed() && (getCloseDate() == null) ) {
                setCloseDate( new Timestamp( System.currentTimeMillis()));
            }

            if( status.isFinalClose()) {
                setProcessed( true );
            }
        }

        // Confidential Info

        if( getConfidentialType() == null ) {
            getRequestType();

            if( m_requestType != null ) {
                String ct = m_requestType.getConfidentialType();

                if( ct != null ) {
                    setConfidentialType( ct );
                }
            }

            if( getConfidentialType() == null ) {
                setConfidentialType( CONFIDENTIALTYPEENTRY_CustomerConfidential );
            }
        }

        if( getConfidentialTypeEntry() == null ) {
            setConfidentialTypeEntry( getConfidentialType());
        }

        // Importance / Priority

        setPriority();

        // New

        if( newRecord ) {
            return true;
        }

        // Change Log

        m_changed = false;

        ArrayList      sendInfo = new ArrayList();
        MRequestAction ra       = new MRequestAction( this,false );

        //

        if( checkChange( ra,"R_RequestType_ID" )) {
            sendInfo.add( "R_RequestType_ID" );
        }

        if( checkChange( ra,"R_Group_ID" )) {
            sendInfo.add( "R_Group_ID" );
        }

        if( checkChange( ra,"R_Category_ID" )) {
            sendInfo.add( "R_Category_ID" );
        }

        if( checkChange( ra,"R_Status_ID" )) {
            sendInfo.add( "R_Status_ID" );
        }

        if( checkChange( ra,"R_Resolution_ID" )) {
            sendInfo.add( "R_Resolution_ID" );
        }

        //

        if( checkChange( ra,"SalesRep_ID" )) {

            // Sender

            int AD_User_ID = Env.getContextAsInt( p_ctx,"#AD_User_ID" );

            if( AD_User_ID == 0 ) {
                AD_User_ID = getUpdatedBy();
            }

            // Old

            Object oo             = get_ValueOld( "SalesRep_ID" );
            int    oldSalesRep_ID = 0;

            if( oo instanceof Integer ) {
                oldSalesRep_ID = (( Integer )oo ).intValue();
            }

            // RequestActionTransfer - Request {0} was transfered by {1} from {2} to {3}

            Object[] args = new Object[]{ getDocumentNo(),EMailUtil.getNameOfUser( AD_User_ID ),EMailUtil.getNameOfUser( oldSalesRep_ID ),EMailUtil.getNameOfUser( getSalesRep_ID())};
            String msg = Msg.getMsg( getCtx(),"RequestActionTransfer",args );

            addToResult( msg );
            sendInfo.add( "SalesRep_ID" );
        }

        checkChange( ra,"AD_Role_ID" );

        //

        checkChange( ra,"Priority" );

        if( checkChange( ra,"PriorityUser" )) {
            sendInfo.add( "PriorityUser" );
        }

        if( checkChange( ra,"IsEscalated" )) {
            sendInfo.add( "IsEscalated" );
        }

        //

        checkChange( ra,"ConfidentialType" );
        checkChange( ra,"Summary" );
        checkChange( ra,"IsSelfService" );
        checkChange( ra,"C_BPartner_ID" );
        checkChange( ra,"AD_User_ID" );
        checkChange( ra,"C_Project_ID" );
        checkChange( ra,"A_Asset_ID" );
        checkChange( ra,"C_Order_ID" );
        checkChange( ra,"C_Invoice_ID" );
        checkChange( ra,"M_Product_ID" );
        checkChange( ra,"C_Payment_ID" );
        checkChange( ra,"M_InOut_ID" );
        checkChange( ra,"M_RMA_ID" );

        // checkChange(ra, "C_Campaign_ID");
        // checkChange(ra, "RequestAmt");

        checkChange( ra,"IsInvoiced" );
        checkChange( ra,"C_Activity_ID" );
        checkChange( ra,"DateNextAction" );

        //

        if( m_changed ) {
            ra.save();
        }

        // Current Info

        MRequestUpdate update = new MRequestUpdate( this );

        if( update.isNewInfo()) {
            update.save();
        } else {
            update = null;
        }

        //

        if( (update != null) || (sendInfo.size() > 0) ) {
            sendNotices( sendInfo );

            // Update

            setDateLastAction( getUpdated());
            setLastResult( getResult());
            setDueType();

            // Reset

            setConfidentialTypeEntry( getConfidentialType());
            setStartDate( null );
            setEndTime( null );
            setR_StandardResponse_ID( 0 );
            setR_MailText_ID( 0 );
            setResult( null );

            // setQtySpent(null);
            // setQtyInvoiced(null);

        }

        return true;
    }    // beforeSave

    /**
     * Descripción de Método
     *
     *
     * @param ra
     * @param columnName
     *
     * @return
     */

    private boolean checkChange( MRequestAction ra,String columnName ) {
        if( is_ValueChanged( columnName )) {
            Object value = get_ValueOld( columnName );

            if( value == null ) {
                ra.addNullColumn( columnName );
            } else {
                ra.set_ValueNoCheck( columnName,value );
            }

            m_changed = true;

            return true;
        }

        return false;
    }    // checkChange

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    private String checkEMail() {

        // Mail Host

        MClient client = MClient.get( getCtx());

        if( (client == null) || (client.getSMTPHost() == null) || (client.getSMTPHost().length() == 0) ) {
            return "RequestActionEMailNoSMTP";
        }

        // Mail To

        MUser to = new MUser( getCtx(),getAD_User_ID(),get_TrxName());

        if( (to == null) || (to.getEMail() == null) || (to.getEMail().length() == 0) ) {
            return "RequestActionEMailNoTo";
        }

        // Mail From real user

        MUser from = MUser.get( getCtx(),Env.getAD_User_ID( getCtx()));

        if( (from == null) || (from.getEMail() == null) || (from.getEMail().length() == 0) ) {
            return "RequestActionEMailNoFrom";
        }

        // Check that UI user is Request User
//              int realSalesRep_ID = Env.getContextAsInt (getCtx(), "#AD_User_ID");
//              if (realSalesRep_ID != getSalesRep_ID())
//                      setSalesRep_ID(realSalesRep_ID);

        // RequestActionEMailInfo - EMail from {0} to {1}
//              Object[] args = new Object[] {emailFrom, emailTo};
//              String msg = Msg.getMsg(getCtx(), "RequestActionEMailInfo", args);
//              setLastResult(msg);
        //

        return null;
    }    // checkEMail

    /**
     * Descripción de Método
     *
     *
     * @param newRecord
     * @param success
     *
     * @return
     */

    protected boolean afterSave( boolean newRecord,boolean success ) {

        // Create Update

        if( newRecord && (getResult() != null) ) {
            MRequestUpdate update = new MRequestUpdate( this );

            update.save();
        }

        // Initial Mail

        if( newRecord ) {
            sendNotices( new ArrayList());
        }

        // ChangeRequest - created in Request Processor

        if( (getM_ChangeRequest_ID() != 0) && is_ValueChanged( "R_Group_ID" ))    // different ECN assignment?
        {
            int oldID = get_ValueOldAsInt( "R_Group_ID" );

            if( getR_Group_ID() == 0 ) {
                setM_ChangeRequest_ID( 0 );    // not effective as in afterSave
            } else {
                MGroup oldG = MGroup.get( getCtx(),oldID );
                MGroup newG = MGroup.get( getCtx(),getR_Group_ID());

                if( (oldG.getM_BOM_ID() != newG.getM_BOM_ID()) || (oldG.getM_ChangeNotice_ID() != newG.getM_ChangeNotice_ID())) {
                    MChangeRequest ecr = new MChangeRequest( getCtx(),getM_ChangeRequest_ID(),get_TrxName());

                    if( !ecr.isProcessed() || (ecr.getM_FixChangeNotice_ID() == 0) ) {
                        ecr.setM_BOM_ID( newG.getM_BOM_ID());
                        ecr.setM_ChangeNotice_ID( newG.getM_ChangeNotice_ID());
                        ecr.save();
                    }
                }
            }
        }

        return true;
    }    // afterSave

    /**
     * Descripción de Método
     *
     */

    private void sendTransferMessage() {

        // Sender

        int AD_User_ID = Env.getContextAsInt( p_ctx,"#AD_User_ID" );

        if( AD_User_ID == 0 ) {
            AD_User_ID = getUpdatedBy();
        }

        // Old

        Object oo             = get_ValueOld( "SalesRep_ID" );
        int    oldSalesRep_ID = 0;

        if( oo instanceof Integer ) {
            oldSalesRep_ID = (( Integer )oo ).intValue();
        }

        // RequestActionTransfer - Request {0} was transfered by {1} from {2} to {3}

        Object[] args = new Object[]{ getDocumentNo(),EMailUtil.getNameOfUser( AD_User_ID ),EMailUtil.getNameOfUser( oldSalesRep_ID ),EMailUtil.getNameOfUser( getSalesRep_ID())};
        String  subject = Msg.getMsg( getCtx(),"RequestActionTransfer",args );
        String  message = subject + "\n" + getSummary();
        MClient client  = MClient.get( getCtx());
        MUser   from    = MUser.get( getCtx(),AD_User_ID );
        MUser   to      = MUser.get( getCtx(),getSalesRep_ID());

        //

        EMail  email = new EMail( client,from,to,subject,message );
        String msg   = email.send();
    }    // afterSaveTransfer

    /**
     * Descripción de Método
     *
     *
     * @param list
     */

    public void sendNotices( ArrayList list ) {

        // Subject

        String subject = Msg.translate( getCtx(),"R_Request_ID" ) + " " + Msg.getMsg( getCtx(),"Updated" ) + ": " + getDocumentNo();

        // Message

        StringBuffer message = new StringBuffer();

        // UpdatedBy: Joe

        int   UpdatedBy = Env.getAD_User_ID( getCtx());
        MUser from      = MUser.get( getCtx(),UpdatedBy );

        if( from != null ) {
            message.append( Msg.translate( getCtx(),"UpdatedBy" )).append( ": " ).append( from.getName());
        }

        // LastAction/Created: ...

        if( getDateLastAction() != null ) {
            message.append( "\n" ).append( Msg.translate( getCtx(),"DateLastAction" )).append( ": " ).append( getDateLastAction());
        } else {
            message.append( "\n" ).append( Msg.translate( getCtx(),"Created" )).append( ": " ).append( getCreated());
        }

        // Changes

        for( int i = 0;i < list.size();i++ ) {
            String columnName = ( String )list.get( i );

            message.append( "\n" ).append( Msg.getElement( getCtx(),columnName )).append( ": " ).append( get_DisplayValue( columnName,false )).append( " -> " ).append( get_DisplayValue( columnName,true ));
        }

        // NextAction

        if( getDateNextAction() != null ) {
            message.append( "\n" ).append( Msg.translate( getCtx(),"DateNextAction" )).append( ": " ).append( getDateNextAction());
        }

        message.append( "\n" ).append( SEPARATOR ).append( "\n" ).append( getSummary());

        if( getResult() != null ) {
            message.append( "\n\n" ).append( getResult());
        }

        message.append( getMailTrailer( null ));
        log.finer( message.toString());

        // Prepare sending Notice/Mail

        MClient client = MClient.get( getCtx());

        // Reset from if external

        if( (from.getEMailUser() == null) || (from.getEMailUserPW() == null) ) {
            from = null;
        }

        int success = 0;
        int failure = 0;
        int notices = 0;

        //

        String sql = "SELECT u.AD_User_ID, u.NotificationType, u.EMail, u.Name, MAX(r.AD_Role_ID) " + "FROM RV_RequestUpdates_Only ru" + " INNER JOIN AD_User u ON (ru.AD_User_ID=u.AD_User_ID)" + " INNER JOIN AD_User_Roles r ON (u.AD_User_ID=r.AD_User_ID) " + "WHERE ru.R_Request_ID=? " + "GROUP BY u.AD_User_ID, u.NotificationType, u.EMail, u.Name";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql );
            pstmt.setInt( 1,getR_Request_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                int    AD_User_ID       = rs.getInt( 1 );
                String NotificationType = rs.getString( 2 );

                if( NotificationType == null ) {
                    NotificationType = MUser.NOTIFICATIONTYPE_EMail;
                }

                String email      = rs.getString( 3 );
                String Name       = rs.getString( 4 );
                int    AD_Role_ID = rs.getInt( 5 );

                if( rs.wasNull()) {
                    AD_Role_ID = -1;
                }

                // Don't send mail to oneself
                // if (AD_User_ID == UpdatedBy)
                // continue;

                // No confidential to externals

                if( (AD_Role_ID == -1) && ( getConfidentialTypeEntry().equals( CONFIDENTIALTYPE_Internal ) || getConfidentialTypeEntry().equals( CONFIDENTIALTYPE_PrivateInformation ))) {
                    continue;
                }

                if( MUser.NOTIFICATIONTYPE_None.equals( NotificationType )) {
                    log.config( "Opt out: " + Name );

                    continue;
                }

                if(( MUser.NOTIFICATIONTYPE_EMail.equals( NotificationType ) || MUser.NOTIFICATIONTYPE_EMailPlusNotice.equals( NotificationType )) && ( (email == null) || (email.length() == 0) ) ) {
                    if( AD_Role_ID >= 0 ) {
                        NotificationType = MUser.NOTIFICATIONTYPE_Notice;
                    } else {
                        log.config( "No EMail: " + Name );

                        continue;
                    }
                }

                if( MUser.NOTIFICATIONTYPE_Notice.equals( NotificationType ) && (AD_Role_ID >= 0) ) {
                    log.config( "No internal User: " + Name );

                    continue;
                }

                MUser to = MUser.get( getCtx(),AD_User_ID );

                //

                if( MUser.NOTIFICATIONTYPE_EMail.equals( NotificationType ) || MUser.NOTIFICATIONTYPE_EMailPlusNotice.equals( NotificationType )) {
                    EMail mail = new EMail( client,from,to,subject,message.toString());
                    String msg = mail.send();

                    if( EMail.SENT_OK.equals( msg )) {
                        success++;
                    } else {
                        log.warning( "Failed: " + Name );
                        failure++;
                        NotificationType = MUser.NOTIFICATIONTYPE_Notice;
                    }
                }

                if( MUser.NOTIFICATIONTYPE_Notice.equals( NotificationType ) || MUser.NOTIFICATIONTYPE_EMailPlusNotice.equals( NotificationType )) {
                    int   AD_Message_ID = 834;
                    MNote note          = new MNote( getCtx(),AD_Message_ID,AD_User_ID,MRequest.Table_ID,getR_Request_ID(),subject,message.toString(),get_TrxName());

                    if( note.save()) {
                        notices++;
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

        log.info( "EMail Success=" + success + ", Failure=" + failure + " - Notices=" + notices );
    }    // sendNotice

    /**
     * Descripción de Método
     *
     *
     * @param serverAddress
     *
     * @return
     */

    public String getMailTrailer( String serverAddress ) {
        StringBuffer sb = new StringBuffer( "\n\n" ).append( SEPARATOR ).append( "\n" ).append( Msg.translate( getCtx(),"R_Request_ID" )).append( ": " ).append( getDocumentNo()).append( "  " ).append( getMailTag()).append( "\nSent by CorreoOxp" );

        if( serverAddress != null ) {
            sb.append( " from " ).append( serverAddress );
        }

        return sb.toString();
    }    // getMailTrailer

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMailTag() {
        return TAG_START + getID() + TAG_END;
    }    // getMailTag
}    // MRequest



/*
 *  @(#)MRequest.java   02.07.07
 * 
 *  Fin del fichero MRequest.java
 *  
 *  Versión 2.2
 *
 */
