package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.openXpertya.util.DB;
import org.openXpertya.util.Util;

public class PercepcionIIBBSantaFe extends PercepcionStandard {

	public PercepcionIIBBSantaFe() {
		super();
		debug("Inicializa...");
		
		// dREHER Feb '25
		debug("Setea tasa de percepcion...");
		setRate();
	}
	
	public PercepcionIIBBSantaFe(PercepcionProcessorData percepcionData) {
		super(percepcionData);
		debug("Inicializa...");
		
		// dREHER Feb '25
		debug("Setea tasa de percepcion...");
		setRate();
	}

	/**
	 * Debug solo mostrar datos por consola
	 * dREHER Feb '25
	 */
	@Override
	protected void debug(String s) {
		System.out.println("====> PercepcionIIBBSantaFe." + s);
	}

	/**
	 * Si la entidad comercial tiene configurada una tasa para el calculo de esta retencion, tomarlo desde alli, 
	 * caso contrario trabaja como siempre
	 * @author dREHER Feb '25
	 */
	private void setRate() {
		
		// BigDecimal rate = getPercepcionPerc(getPercepcionData().getBpartner().getC_BPartner_ID(), null);
		
		BigDecimal rate = getAlicuotaFromEC(getPercepcionData().getBpartner().getC_BPartner_ID(), getPercepcionData().getTax().getC_Region_ID());

		if(!Util.isEmpty(rate, true)) {
			getPercepcionData().setAlicuota(rate);
			debug("Encontro alicuota seteada en el cliente, cambia la alicuota estandard por esta... Alicuota=" + rate);
		}
		
	}
	
	/**
	 * @param bpartnerID
	 * @param trxName
	 * @return tasa percepcion 
	 * @author dREHER Feb '25
	 */
    private BigDecimal getPercepcionPerc(Integer bpartnerID, String trxName){
		String sql = "SELECT COALESCE(alicuota,0) as porc " +
					"FROM c_bpartner_percepcion " +
					"WHERE c_bpartner_id = ? AND C_Region_ID=? AND isactive = 'Y' " +
					"LIMIT 1";
		
		int C_REGION_ID_SANTAFE = DB.getSQLValue(null, "SELECT C_Region_ID FROM C_Region WHERE Name=? AND IsActive='Y' AND C_Country_ID=119", "Santa Fe");
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		BigDecimal Perc = BigDecimal.ZERO;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, bpartnerID);
			ps.setInt(2, C_REGION_ID_SANTAFE);
			rs = ps.executeQuery();
			if(rs.next()){
				Perc = rs.getBigDecimal(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(ps != null) ps = null;
				if(rs != null) rs = null;
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return Perc;
    }
	

}
