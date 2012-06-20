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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MProject extends X_C_Project {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param C_Project_ID
     * @param dateDoc
     * @param trxName
     *
     * @return
     */

    public static MProject copyFrom( Properties ctx,int C_Project_ID,Timestamp dateDoc,String trxName ) {
        MProject from = new MProject( ctx,C_Project_ID,trxName );

        if( from.getC_Project_ID() == 0 ) {
            throw new IllegalArgumentException( "From Project not found C_Project_ID=" + C_Project_ID );
        }

        //

        MProject to = new MProject( ctx,0,trxName );

        PO.copyValues( from,to,from.getAD_Client_ID(),from.getAD_Org_ID());
        to.setC_Project_ID( 0 );

        // Set Value with Time

        String Value  = to.getValue() + " ";
        String Time   = dateDoc.toString();
        int    length = Value.length() + Time.length();

        if( length <= 40 ) {
            Value += Time;
        } else {
            Value += Time.substring( length - 40 );
        }

        to.setValue( Value );
        to.setInvoicedAmt( Env.ZERO );
        to.setProjectBalanceAmt( Env.ZERO );
        to.setProcessed( false );

        //

        if( !to.save()) {
            throw new IllegalStateException( "Could not create Project" );
        }

        if( to.copyDetailsFrom( from ) == 0 ) {
            throw new IllegalStateException( "Could not create Project Details" );
        }

        return to;
    }    // copyFrom

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param C_Project_ID
     * @param trxName
     */

    public MProject( Properties ctx,int C_Project_ID,String trxName ) {
        super( ctx,C_Project_ID,trxName );

        if( C_Project_ID == 0 ) {

            // setC_Project_ID(0);
            // setValue (null);
            // setC_Currency_ID (0);

            setCommittedAmt( Env.ZERO );
            setCommittedQty( Env.ZERO );
            setInvoicedAmt( Env.ZERO );
            setInvoicedQty( Env.ZERO );
            setPlannedAmt( Env.ZERO );
            setPlannedMarginAmt( Env.ZERO );
            setPlannedQty( Env.ZERO );
            setProjectBalanceAmt( Env.ZERO );

            // setProjectCategory(PROJECTCATEGORY_General);

            setIsCommitCeiling( false );
            setIsCommitment( false );
            setIsSummary( false );
            setProcessed( false );
        }
    }    // MProject

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MProject( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MProject

    /** Descripción de Campos */

    private int m_M_PriceList_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_ProjectType_ID_Int() {
        String pj = super.getC_ProjectType_ID();

        if( pj == null ) {
            return 0;
        }

        int C_ProjectType_ID = 0;

        try {
            C_ProjectType_ID = Integer.parseInt( pj );
        } catch( Exception ex ) {
            log.log( Level.SEVERE,"getC_ProjectType_ID_Int - " + pj,ex );
        }

        return C_ProjectType_ID;
    }    // getC_ProjectType_ID_Int

    /**
     * Descripción de Método
     *
     *
     * @param C_ProjectType_ID
     */

    public void setC_ProjectType_ID( int C_ProjectType_ID ) {
        if( C_ProjectType_ID == 0 ) {
            super.setC_ProjectType_ID( null );
        } else {
            super.setC_ProjectType_ID( String.valueOf( C_ProjectType_ID ));
        }
    }    // setC_ProjectType_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        StringBuffer sb = new StringBuffer( "MProject[" ).append( getID()).append( "-" ).append( getValue()).append( ",ProjectCategory=" ).append( getProjectCategory()).append( "]" );

        return sb.toString();
    }    // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getM_PriceList_ID() {
        if( getM_PriceList_Version_ID() == 0 ) {
            return 0;
        }

        if( m_M_PriceList_ID > 0 ) {
            return m_M_PriceList_ID;
        }

        //

        String sql = "SELECT M_PriceList_ID FROM M_PriceList_Version WHERE M_PriceList_Version_ID=?";

        m_M_PriceList_ID = DB.getSQLValue( null,sql,getM_PriceList_Version_ID());

        return m_M_PriceList_ID;
    }    // getM_PriceList_ID

    /**
     * Descripción de Método
     *
     *
     * @param M_PriceList_Version_ID
     */

    public void setM_PriceList_Version_ID( int M_PriceList_Version_ID ) {
        super.setM_PriceList_Version_ID( M_PriceList_Version_ID );
        m_M_PriceList_ID = 0;    // reset
    }                            // setM_PriceList_Version_ID

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProjectLine[] getLines() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_ProjectLine WHERE C_Project_ID=? ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Project_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProjectLine( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getLines",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProjectLine[] retValue = new MProjectLine[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getLines

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProjectIssue[] getIssues() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_ProjectIssue WHERE C_Project_ID=? ORDER BY Line";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Project_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProjectIssue( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getIssues",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProjectIssue[] retValue = new MProjectIssue[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getIssues

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public MProjectPhase[] getPhases() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_ProjectPhase WHERE C_Project_ID=? ORDER BY SeqNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Project_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProjectPhase( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getPhases",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProjectPhase[] retValue = new MProjectPhase[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getPhases

    /**
     * Descripción de Método
     *
     *
     * @param project
     *
     * @return
     */

    public int copyDetailsFrom( MProject project ) {
        if( isProcessed() || (project == null) ) {
            return 0;
        }

        int count = copyLinesFrom( project ) + copyPhasesFrom( project );

        return count;
    }    // copyDetailsFrom

    /**
     * Descripción de Método
     *
     *
     * @param project
     *
     * @return
     */

    public int copyLinesFrom( MProject project ) {
        if( isProcessed() || (project == null) ) {
            return 0;
        }

        int            count     = 0;
        MProjectLine[] fromLines = project.getLines();

        for( int i = 0;i < fromLines.length;i++ ) {
            MProjectLine line = new MProjectLine( getCtx(),0,project.get_TrxName());

            PO.copyValues( fromLines[ i ],line,getAD_Client_ID(),getAD_Org_ID());
            line.setC_Project_ID( getC_Project_ID());
            line.setInvoicedAmt( Env.ZERO );
            line.setInvoicedQty( Env.ZERO );
            line.setC_OrderPO_ID( 0 );
            line.setC_Order_ID( 0 );
            line.setProcessed( false );

            if( line.save()) {
                count++;
            }
        }

        if( fromLines.length != count ) {
            log.log( Level.SEVERE,"copyLinesFrom - Lines difference - Project=" + fromLines.length + " <> Saved=" + count );
        }

        return count;
    }    // copyLinesFrom

    /**
     * Descripción de Método
     *
     *
     * @param fromProject
     *
     * @return
     */

    public int copyPhasesFrom( MProject fromProject ) {
        if( isProcessed() || (fromProject == null) ) {
            return 0;
        }

        int count     = 0;
        int taskCount = 0;

        // Get Phases

        MProjectPhase[] myPhases   = getPhases();
        MProjectPhase[] fromPhases = fromProject.getPhases();

        // Copy Phases

        for( int i = 0;i < fromPhases.length;i++ ) {

            // Check if Phase already exists

            int     C_Phase_ID = fromPhases[ i ].getC_Phase_ID();
            boolean exists     = false;

            if( C_Phase_ID == 0 ) {
                exists = false;
            } else {
                for( int ii = 0;ii < myPhases.length;ii++ ) {
                    if( myPhases[ ii ].getC_Phase_ID() == C_Phase_ID ) {
                        exists = true;

                        break;
                    }
                }
            }

            // Phase exist

            if( exists ) {
                log.info( "copyPhasesFrom - Phase already exists here, ignored - " + fromPhases[ i ] );
            } else {
                MProjectPhase toPhase = new MProjectPhase( getCtx(),0,get_TrxName());

                PO.copyValues( fromPhases[ i ],toPhase,getAD_Client_ID(),getAD_Org_ID());
                toPhase.setC_Project_ID( getC_Project_ID());
                toPhase.setC_Order_ID( 0 );
                toPhase.setIsComplete( false );

                if( toPhase.save()) {
                    count++;
                    taskCount += toPhase.copyTasksFrom( fromPhases[ i ] );
                }
            }
        }

        if( fromPhases.length != count ) {
            log.warning( "copyPhasesFrom - Count difference - Project=" + fromPhases.length + " <> Saved=" + count );
        }

        return count + taskCount;
    }    // copyPhasesFrom

    /**
     * Descripción de Método
     *
     *
     * @param type
     */

    public void setProjectType( MProjectType type ) {
        if( type == null ) {
            return;
        }

        setC_ProjectType_ID( type.getC_ProjectType_ID());
        setProjectCategory( type.getProjectCategory());

        if( PROJECTCATEGORY_ServiceChargeProject.equals( getProjectCategory())) {
            copyPhasesFrom( type );
        }
    }    // setProjectType

    /**
     * Descripción de Método
     *
     *
     * @param type
     *
     * @return
     */

    public int copyPhasesFrom( MProjectType type ) {

        // create phases

        int                 count      = 0;
        int                 taskCount  = 0;
        MProjectTypePhase[] typePhases = type.getPhases();

        for( int i = 0;i < typePhases.length;i++ ) {
            MProjectPhase toPhase = new MProjectPhase( this,typePhases[ i ] );

            if( toPhase.save()) {
                count++;
                taskCount += toPhase.copyTasksFrom( typePhases[ i ] );
            }
        }

        log.fine( "copyPhaseFrom - #" + count + "/" + taskCount + " - " + type );

        if( typePhases.length != count ) {
            log.log( Level.SEVERE,"copyPhasesFrom - Count difference - Type=" + typePhases.length + " <> Saved=" + count );
        }

        return count;
    }    // copyPhasesFrom

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
        if( newRecord ) {
            insert_Accounting( "C_Project_Acct","C_AcctSchema_Default",null );
            insert_Tree( MTree_Base.TREETYPE_Project );
        }

        // Value/Name change

        if( !newRecord && ( is_ValueChanged( "Value" ) || is_ValueChanged( "Name" ))) {
            MAccount.updateValueDescription( getCtx(),"C_Project_ID=" + getC_Project_ID(),get_TrxName());
        }

        return success;
    }    // afterSave

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    protected boolean beforeDelete() {
        return delete_Accounting( "C_Project_Acct" );
    }    // beforeDelete

    /**
     * Descripción de Método
     *
     *
     * @param success
     *
     * @return
     */

    protected boolean afterDelete( boolean success ) {
        if( success ) {
            delete_Tree( MTree_Base.TREETYPE_Project );
        }

        return true;
    }    // afterDelete
    //Añadida por ConSerTi.
    public MProjectTask[] getTasks() {
        ArrayList list = new ArrayList();
        String    sql  = "SELECT * FROM C_ProjectTask WHERE C_ProjectPhase_ID=? ORDER BY SeqNo";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getC_Project_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
                list.add( new MProjectTask( getCtx(),rs,get_TrxName()));
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( SQLException ex ) {
            log.log( Level.SEVERE,"getTasks",ex );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }
        } catch( SQLException ex1 ) {
        }

        pstmt = null;

        //

        MProjectTask[] retValue = new MProjectTask[ list.size()];

        list.toArray( retValue );

        return retValue;
    }    // getTasks
}    // MProject



/*
 *  @(#)MProject.java   02.07.07
 * 
 *  Fin del fichero MProject.java
 *  
 *  Versión 2.2
 *
 */
