package org.openXpertya.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.openXpertya.util.Env;

import org.openXpertya.model.X_C_BPartner;
/**
 * Class Description
 * Prepare Beforesave
 *
 * @version    2.2, 12.10.07
 * @author     Zarius - Dataware -
 */
public class MBPartnerEmployee extends X_C_BPartner_Employee{
	/**
	 * Class Description
	 * Extend Model
	 *
	 * @version    2.2, 12.10.07
	 * @author     Zarius - Dataware -
	 */
    public MBPartnerEmployee( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    } // MBPartnerEmployee
	/**
	 * Class Description
	 * Extend Model
	 *
	 * @version    2.2, 12.10.07
	 * @author     Zarius - Dataware -
	 */
    public MBPartnerEmployee( Properties ctx,int C_BPartner_Employee_ID,String trxName ) {
    	super( ctx,C_BPartner_Employee_ID,trxName );
    } // MBPartnerEmployee
	/**
	 * Class Description
	 * Do Before Save
	 *
	 * @version    2.2, 12.10.07
	 * @author     Zarius - Dataware -
	 */
	protected boolean beforeSave (boolean newRecord)
	{
		log.info("// Begin beforeSave by Zarius - Dataware - ");
		X_C_BPartner bp = new X_C_BPartner(getCtx(), getC_BPartner_ID(), null);
				
		bp.setIsEmployee(isEmployee());
		bp.setIsSalesRep(isSalesRep());
		
		bp.save();
		log.info("// End beforeSave by Zarius - Dataware - ");
		return true;
	}
	
}
