package org.openXpertya.replication.filter;

/**
 * Filtrado de EC.
 * 
 *	- Replica a sucursales si (cliente o empleado) y no es C.F. (a central siempre)
 *	- Los proveedores no sufren modificaciones en las sucursales
 *	- No replique los campos totalopenbalance, so_creditstatus, so_creditused
 *
 *	Este filtro sirve tanto para la tabla C_BPartner, como para sus tablas relacionadas (C_BP_Customer_Acct, C_BPartner_Location, etc.)
 *
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import org.openXpertya.model.X_C_BPartner;
import org.openXpertya.model.X_C_Location;
import org.openXpertya.replication.ChangeLogGroupReplication;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class BPartnerReplicationFilter extends ReplicationFilter {
	
	/** AD_ComponentObjectUID de la entidad comercial Consumidor Final */
	public static final String BPARTNER_CONSUMIDOR_FINAL_AD_COMPONENTOBJECTUID = "CORE-C_BPartner-1012142";
	
	/** Si bien la mayoría de tablas relacionadas con C_BPartner que requieren el filtro se relacionan mediante C_BPartner_ID, 
	 *  hay algunas que necesitan un query especial para poder llegar a acceder a la información de la entidad comercial. 
	 *  Este map es: Nombre de Tabla => Query */
	protected static HashMap<String, String> queryForTable = new HashMap<String, String>();

	static {
		// Si es Location, es necesario obtenerlo a partir del C_BPartner_Location
		queryForTable.put(	X_C_Location.Table_Name.toLowerCase(), 	
							" SELECT C_BPartner_ID " +
							" FROM C_BPartner_Location BPL " +
							" INNER JOIN C_Location L ON L.C_Location_ID = BPL.C_Location_ID " +
							" WHERE L.retrieveuid = ?");
	}
	
	@Override
	public void applyFilter(String trxName, ChangeLogGroupReplication group) throws Exception {

		// Recuperar el BPartner
		X_C_BPartner aBPartner = getBPartner(group, trxName);
		if (aBPartner==null)
			return;
		
		// Si la EC es CF, o bien no es cliente o empleado, entonces replicar unicamente a central. 
		if ((!aBPartner.isCustomer() && !aBPartner.isEmployee()) || isConsumidorFinal(aBPartner)) {
			repArraySendToCentralOnly(group);
			return;
		}		

		// Si estamos gestionando C_BPartner, se deben quitar de la nomina de columnas a replicar los siguientes: 
		//	totalopenbalance, so_creditstatus, so_creditused
		if (X_C_BPartner.Table_Name.equalsIgnoreCase(group.getTableName())) {
			removeElement(group, "TotalOpenBalance");
			removeElement(group, "SOCreditStatus");
			removeElement(group, "SO_CreditUsed");
		}
		
	}

	/**
	 * Retorna true si la EC es la determinada Consumidor Final o false en caso contrario
	 * @param bPartner la EC a evaluar
	 * @return true o false
	 */
	protected boolean isConsumidorFinal(X_C_BPartner aBPartner) {
		return (BPARTNER_CONSUMIDOR_FINAL_AD_COMPONENTOBJECTUID.equals(aBPartner.getAD_ComponentObjectUID()));
	}
		
	/**
	 * Recuperar el C_BPartner_ID dependiendo de la tabla, o null en caso de no encontrarlo
	 */
	protected X_C_BPartner getBPartner(ChangeLogGroupReplication group, String trxName) throws Exception {
		
		// Recuperar el C_BPartner_ID a partir de la tabla que referencia a la EC.  
		String query = "SELECT C_BPartner_ID FROM " + group.getTableName() + " WHERE retrieveuid = ?";
		
		// Si es un query especial, recuperarlo y redefinir
		if (queryForTable.get(group.getTableName().toLowerCase()) != null)
			query = queryForTable.get(group.getTableName().toLowerCase());
		
		// Obtener el BPartner.
		PreparedStatement pstmt = DB.prepareStatement(query, trxName);
		pstmt.setString(1, group.getAd_componentObjectUID());
		ResultSet rs = pstmt.executeQuery();
		if (rs.next())
			return new X_C_BPartner(Env.getCtx(), rs.getInt("C_BPartner_ID"), trxName);;
		return null;
	}

}
