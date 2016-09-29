package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;

import org.openXpertya.model.MTransfer;
import org.openXpertya.model.MTransferLine;
import org.openXpertya.model.PO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Msg;

public class CopyFromMTransfer extends SvrProcess {

	/** ID de documento desde el que se copiará. */
	protected int p_M_Transfer_ID = 0;

	@Override
	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else if (name.equals("M_Transfer_ID")) {
				p_M_Transfer_ID = ((BigDecimal) para[i].getParameter()).intValue();
			} else {
				log.log(Level.SEVERE, "Unknown Parameter: " + name);
			}
		}
	}

	@Override
	protected String doIt() throws Exception {
		int to_M_Transfer_ID = getRecord_ID();

		// Valido que se haya recuperado bien los IDs de los documentos.
		if (to_M_Transfer_ID == 0) {
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "DestinationDocumentError"));
		}
		if (p_M_Transfer_ID == 0) {
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "OriginDocumentError"));
		}
		// Documentos de origen y destino no pueden ser el mismo.
		if (p_M_Transfer_ID == to_M_Transfer_ID) {
			throw new IllegalArgumentException(Msg.getMsg(getCtx(), "CopyIdenticalException"));
		}

		MTransfer from = new MTransfer(getCtx(), p_M_Transfer_ID, get_TrxName());
		MTransfer to = new MTransfer(getCtx(), to_M_Transfer_ID, get_TrxName());

		List<MTransferLine> lines = from.getLines();
		for (MTransferLine line : lines) {
			MTransferLine newLine = new MTransferLine(to);
			// Copio la linea completa
			PO.copyValues(line, newLine);

			// Corrijo los campos que varían
			newLine.setM_Transfer_ID(to_M_Transfer_ID);
			newLine.setM_Locator_ID(getMLocatorId(from, to, line, true));
			newLine.setM_Locator_To_ID(getMLocatorId(from, to, line, false));

			if (!newLine.save()) {
				throw new Exception(CLogger.retrieveErrorAsString());
			}
		}

		StringBuffer result = new StringBuffer();

		result.append(Msg.getMsg(getCtx(), "CopySuccessful"));
		result.append(". " + Msg.getMsg(getCtx(), "LinesCopied") + ": ");
		result.append(lines != null ? lines.size() : 0);

		return result.toString();
	}

	private int getMLocatorId(MTransfer from, MTransfer to, MTransferLine line, boolean origin) {
		if (origin) {
			// Si el almacen origen de ambas Transferencias coinciden, copio la ubicación de origen
			if (from.getM_Warehouse_ID() == to.getM_Warehouse_ID()) {
				return line.getM_Locator_ID();
			}
		} else {
			// Si el almacen destino de ambas Transferencias coinciden, copio la ubicación de destino
			if (from.getM_WarehouseTo_ID() == to.getM_WarehouseTo_ID()) {
				return line.getM_Locator_To_ID();
			}
		}
		int mLocatorId = 0;

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT m_locator_id ");
		sql.append("FROM m_locator ");
		sql.append("WHERE m_warehouse_id = ? ");
		sql.append("ORDER BY isdefault DESC ");
		sql.append("LIMIT 1");

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql.toString(), get_TrxName());
			if (origin) {
				ps.setInt(1, to.getM_Warehouse_ID());
			} else {
				ps.setInt(1, to.getM_WarehouseTo_ID());
			}
			rs = ps.executeQuery();
			if (rs.next()) {
				mLocatorId = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mLocatorId;
	}

}
