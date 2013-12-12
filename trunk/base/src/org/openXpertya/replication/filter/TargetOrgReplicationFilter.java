package org.openXpertya.replication.filter;

import org.openXpertya.replication.ChangeLogGroupReplication;

/**
 * Realiza un filtrado con igual logica que la superclase, 
 * pero basado en la oclumna AD_TargetOrg_ID en lugar de AD_Org_ID
 * @author fcristina
 */
public class TargetOrgReplicationFilter extends OrgReplicationFilter {
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {
		super.applyFilter(trxName, group);
	}
	
	protected String getFilterColumnName() {
		return "AD_TargetOrg_ID";
	}


}
