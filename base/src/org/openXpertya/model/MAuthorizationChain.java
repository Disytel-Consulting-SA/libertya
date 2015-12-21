package org.openXpertya.model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class MAuthorizationChain extends X_M_AuthorizationChain {
	
	public MAuthorizationChain(Properties ctx, int M_AuthorizationChain_ID,
			String trxName) {
		super(ctx, M_AuthorizationChain_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public X_M_AuthorizationChainLink[] getLines( ) {
    	log.fine("getLines 1");
        ArrayList    list = new ArrayList();
        StringBuffer sql  = new StringBuffer( "SELECT * FROM M_AuthorizationChainLink WHERE M_AuthorizationChain_ID= ? AND AD_Org_ID = ? AND AD_Client_ID = ? " );

        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql.toString(),get_TrxName());
            pstmt.setInt( 1,getM_AuthorizationChain_ID());
            pstmt.setInt(2, getAD_Org_ID());
            pstmt.setInt(3, getAD_Client_ID());

            ResultSet rs = pstmt.executeQuery();

            while( rs.next()) {
            	X_M_AuthorizationChainLink ol = new X_M_AuthorizationChainLink( getCtx(),rs,get_TrxName());
                //Ader soporte para cache multi-documentos

                list.add( ol );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getLines - " + sql,e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        //

        X_M_AuthorizationChainLink[] lines = new X_M_AuthorizationChainLink[ list.size()];

        list.toArray( lines );

        return lines;
    }    // getLines
	
	@Override
	protected boolean beforeSave(boolean newRecord) {
		// Verificar que no exista una autorización para el mismo tipo de documento
		if (DB.getSQLValue(
				this.get_TrxName(),
				"SELECT M_AuthorizationChain_ID FROM M_AuthorizationChain WHERE C_DocType_ID = "
						+ this.getC_DocType_ID() 
						+ " AND AD_Org_ID = " + getAD_Org_ID()
						+ " AND AD_Client_ID = " + getAD_Client_ID() ) > 0) {
			log.saveError(Msg.getMsg(getCtx(), "AlreadyExistsAnAuthorization"),"");
			return false;
		}
		return true;
	}

}
