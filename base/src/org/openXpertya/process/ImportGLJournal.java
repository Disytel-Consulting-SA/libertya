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

package org.openXpertya.process;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;

import org.openXpertya.model.MJournal;
import org.openXpertya.model.MJournalBatch;
import org.openXpertya.model.MJournalLine;
import org.openXpertya.model.X_I_GLJournal;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.TimeUtil;

/**
 * Descripción de Clase
 * 
 * 
 * @version 2.2, 12.10.07
 * @author Equipo de Desarrollo de openXpertya
 */

public class ImportGLJournal extends SvrProcess {

	/** Descripción de Campos */

	private int m_AD_Client_ID = 0;

	/** Descripción de Campos */

	private int m_AD_Org_ID = 0;

	/** Descripción de Campos */

	private int m_C_AcctSchema_ID = 0;

	/** Descripción de Campos */

	private Timestamp m_DateAcct = null;

	/** Descripción de Campos */

	private boolean m_DeleteOldImported = false;

	/** Descripción de Campos */

	private boolean m_IsValidateOnly = false;

	/** Descripción de Campos */

	private boolean m_IsImportOnlyNoErrors = false;

	/**
	 * Descripción de Método
	 * 
	 */

	protected void prepare() {
		ProcessInfoParameter[] para = getParameter();

		for (int i = 0; i < para.length; i++) {
			String name = para[i].getParameterName();

			if (para[i].getParameter() == null) {
				;
			} else if (name.equals("AD_Client_ID")) {
				m_AD_Client_ID = ((BigDecimal) para[i].getParameter())
						.intValue();
			} else if (name.equals("AD_Org_ID")) {
				m_AD_Org_ID = ((BigDecimal) para[i].getParameter()).intValue();
			} else if (name.equals("C_AcctSchema_ID")) {
				m_C_AcctSchema_ID = ((BigDecimal) para[i].getParameter())
						.intValue();
			} else if (name.equals("DateAcct")) {
				m_DateAcct = (Timestamp) para[i].getParameter();
			} else if (name.equals("IsValidateOnly")) {
				m_IsValidateOnly = "Y".equals(para[i].getParameter());
			/*} else if (name.equals("IsImportOnlyNoErrors")) {
				m_IsImportOnlyNoErrors = "Y".equals(para[i].getParameter());*/
			} else if (name.equals("DeleteOldImported")) {
				m_DeleteOldImported = "Y".equals(para[i].getParameter());
			} else {
				log.log(Level.SEVERE, "prepare - Unknown Parameter: " + name);
			}
		}
	} // prepare

	/**
	 * Descripción de Método
	 * 
	 * 
	 * @return
	 * 
	 * @throws java.lang.Exception
	 */

	protected String doIt() throws java.lang.Exception {
		log.info("IsValidateOnly=" + m_IsValidateOnly
				+ ", IsImportOnlyNoErrors=" + m_IsImportOnlyNoErrors);

		StringBuffer sql = null;
		int no = 0;
		String clientCheck = " AND AD_Client_ID=" + m_AD_Client_ID;

		// **** Prepare ****

		// Delete Old Imported

		if (m_DeleteOldImported) {
			sql = new StringBuffer("DELETE I_GLJournal "
					+ "WHERE I_IsImported='Y'").append(clientCheck);
			no = DB.executeUpdate(sql.toString());
			log.fine("doIt - Delete Old Impored =" + no);
		}
		// Seteo los datos desde los cargados para que puedan importarse los
		// Asientos Contables

		// Setear account_id a partir del accountvalue
		sql = new StringBuffer("UPDATE I_GLJournal i " + "SET account_id = "
				+ "(SELECT c_elementvalue_id FROM c_elementvalue e "
				+ "	WHERE e.value = i.accountvalue "
				+ "	AND e.ad_client_id =  i.ad_client_id) "
				+ "WHERE i.accountvalue is not null AND i.ad_client_id = "
				+ getAD_Client_ID());
		no = DB.executeUpdate(sql.toString());
		log.info("doIt -account_id actualizados = " + no);

		// Error accountvalue
		sql = new StringBuffer("UPDATE I_GLJournal "
				+ "SET I_IsImported='E', I_ErrorMsg='I_ErrorMsg '||'"
				+ getMsg("ImportGLJournalInvalidAccountValue") + ". ' "
				+ "WHERE accountvalue NOT IN (SELECT value from c_elementvalue) "
				+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid Account Value=" + no);
		}

		// Error uno de los montos debe ser 0 (amtsourcedr,amtsourcecr)
		sql = new StringBuffer("UPDATE I_GLJournal "
				+ "SET I_IsImported='E', I_ErrorMsg='I_ErrorMsg '||'"
				+ getMsg("ImportGLJournalInvalidamtSource") + ". ' "
				+ "WHERE amtsourcedr <> 0 AND amtsourcecr <> 0 "
				+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid Amount =" + no);
		}

		// Setear c_doctype_id a partir del doctypekey
		sql = new StringBuffer("UPDATE I_GLJournal i " + "SET c_doctype_id = "
				+ "(SELECT c_doctype_id FROM c_doctype dt "
				+ "	WHERE dt.doctypekey = i.doctypekey "
				+ "	AND dt.ad_client_id =  i.ad_client_id) "
				+ "WHERE i.doctypekey is not null AND i.ad_client_id = "
				+ getAD_Client_ID());
		no = DB.executeUpdate(sql.toString());
		log.info("doIt -c_doctype_id actualizados = " + no);

		// Setear gl_category a partir del c_doctype_id
		sql = new StringBuffer("UPDATE I_GLJournal i "
				+ "SET gl_category_id = "
				+ "(SELECT gl_category_id FROM c_doctype dt "
				+ "	WHERE dt.c_doctype_id = i.c_doctype_id "
				+ "	AND dt.ad_client_id =  i.ad_client_id) "
				+ "WHERE i.c_doctype_id is not null AND i.ad_client_id = "
				+ getAD_Client_ID());
		no = DB.executeUpdate(sql.toString());
		log.info("doIt -gl_journal_id actualizados = " + no);

		// 2008-08-25 - Horacio - Modificado
		sql = new StringBuffer("UPDATE I_GLJournal "
				+ "SET AmtAcctDr = AmtAcctCr,  AmtAcctCr = 0 "
				+ "WHERE ProductValue like 'D' AND AmtAcctCr <> 0 ");
		no = DB.executeUpdate(sql.toString());
		log.info("doIt - Actualizar Debe Haber = " + no);

		// Set IsActive, Created/Updated
		sql = new StringBuffer("UPDATE I_GLJournal "
				+ "SET IsActive = COALESCE (IsActive, 'Y'),"
				+ " Created = COALESCE (Created, SysDate),"
				+ " CreatedBy = COALESCE (CreatedBy, 0),"
				+ " Updated = COALESCE (Updated, SysDate),"
				+ " UpdatedBy = COALESCE (UpdatedBy, 0)"//,"
				//+ " I_ErrorMsg = NULL," + " I_IsImported = 'N' "
				+ "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL");
		no = DB.executeUpdate(sql.toString());
		log.info("doIt - Reset=" + no);

		// Set Client from Name
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET AD_Client_ID=(SELECT c.AD_Client_ID FROM AD_Client c WHERE c.Value=i.ClientValue) "
						+ "WHERE (AD_Client_ID IS NULL OR AD_Client_ID=0) AND ClientValue IS NOT NULL"
						+ " AND I_IsImported<>'Y'");
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Client from Value=" + no);

		/*
		 * OXP FT no tiene M_CentroCosto //actualizado - 26-08-08 - Horacio
		 * Alvarez
		 * 
		 * // Set Org from Centro Costo sql = new StringBuffer
		 * ("UPDATE I_GLJournal i " +
		 * "SET AD_Org_ID=(SELECT c.AD_Org_ID FROM M_CentroCosto c WHERE c.codigo = i.OrgValue) "
		 * //+
		 * "WHERE (AD_Org_ID IS NULL OR AD_Org_ID = 0) AND OrgValue IS NOT NULL"
		 * + " WHERE I_IsImported<>'Y'"); no = DB.executeUpdate (sql.toString
		 * ()); log.debug ("doIt - Set Org from Value=" + no);
		 * 
		 * // Set Org from Centro Costo sql = new StringBuffer
		 * ("UPDATE I_GLJournal i " +
		 * "SET AD_OrgDoc_ID=(SELECT c.AD_Org_ID FROM M_CentroCosto c WHERE c.codigo = i.OrgValue) "
		 * //+
		 * "WHERE (AD_Org_ID IS NULL OR AD_Org_ID = 0) AND OrgValue IS NOT NULL"
		 * + " WHERE I_IsImported<>'Y'"); no = DB.executeUpdate (sql.toString
		 * ()); log.debug ("doIt - Set Org from Value=" + no);
		 */

		// Set Client, Doc Org, AcctSchema, DatAcct

		sql = new StringBuffer("UPDATE I_GLJournal "
				+ "SET AD_Client_ID = COALESCE (AD_Client_ID,")
				.append(m_AD_Client_ID)
				.append(")," + " AD_OrgDoc_ID = COALESCE (AD_OrgDoc_ID,")
				.append(m_AD_Org_ID).append("),");

		if (m_C_AcctSchema_ID != 0) {
			sql.append(" C_AcctSchema_ID = COALESCE (C_AcctSchema_ID,")
					.append(m_C_AcctSchema_ID).append("),");
		}

		if (m_DateAcct != null) {
			sql.append(" DateAcct = COALESCE (DateAcct,")
					.append(DB.TO_DATE(m_DateAcct)).append("),");
		}

		sql.append(" Updated = COALESCE (Updated, SysDate) "
				+ "WHERE I_IsImported<>'Y' OR I_IsImported IS NULL");
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Client/DocOrg/Default=" + no);

		// Error Doc Org

		sql = new StringBuffer(
				"UPDATE I_GLJournal o "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Doc Org, '"
						+ "WHERE (AD_OrgDoc_ID IS NULL OR AD_OrgDoc_ID=0"
						+ " OR EXISTS (SELECT * FROM AD_Org oo WHERE o.AD_Org_ID=oo.AD_Org_ID AND (oo.IsSummary='Y' OR oo.IsActive='N')))"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid Doc Org=" + no);
		}

		// Set AcctSchema

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET C_AcctSchema_ID=(SELECT a.C_AcctSchema_ID FROM C_AcctSchema a"
						+ " WHERE i.AcctSchemaName=a.Name AND i.AD_Client_ID=a.AD_Client_ID) "
						+ "WHERE C_AcctSchema_ID IS NULL AND AcctSchemaName IS NOT NULL"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set AcctSchema from Name=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET C_AcctSchema_ID=(SELECT c.C_AcctSchema1_ID FROM AD_ClientInfo c WHERE c.AD_Client_ID=i.AD_Client_ID) "
						+ "WHERE C_AcctSchema_ID IS NULL AND AcctSchemaName IS NULL"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set AcctSchema from Client=" + no);

		// Error AcctSchema

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid AcctSchema, '"
						+ "WHERE (C_AcctSchema_ID IS NULL OR C_AcctSchema_ID=0"
						+ " OR NOT EXISTS (SELECT * FROM C_AcctSchema a WHERE i.AD_Client_ID=a.AD_Client_ID))"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid AcctSchema=" + no);
		}

		// Set DateAcct (mandatory)

		sql = new StringBuffer("UPDATE I_GLJournal i "
				+ "SET DateAcct=SysDate " + "WHERE DateAcct IS NULL"
				+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set DateAcct=" + no);

		// Document Type
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET C_DocType_ID=(SELECT d.C_DocType_ID FROM C_DocType d"
						+ " WHERE d.Name=i.DocTypeName AND d.DocBaseType='GLJ' AND i.AD_Client_ID=d.AD_Client_ID) "
						// +
						// " WHERE d.Name like 'Asiento Manual' AND d.DocBaseType='GLJ' AND i.AD_Client_ID=d.AD_Client_ID) "
						+ "WHERE C_DocType_ID IS NULL AND DocTypeName IS NOT NULL"
						// + "WHERE C_DocType_ID IS NULL "
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set DocType=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid DocType, '"
						+ "WHERE (C_DocType_ID IS NULL OR C_DocType_ID=0"
						+ " OR NOT EXISTS (SELECT * FROM C_DocType d WHERE i.AD_Client_ID=d.AD_Client_ID AND d.DocBaseType='GLJ'))"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid DocType=" + no);
		}

		// GL Category
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET GL_Category_ID=(SELECT c.GL_Category_ID FROM GL_Category c"
						+ " WHERE c.Name=i.CategoryName AND i.AD_Client_ID=c.AD_Client_ID) "
						// +
						// " WHERE c.Name like 'Standard' AND i.AD_Client_ID=c.AD_Client_ID) "
						+ "WHERE GL_Category_ID IS NULL AND CategoryName IS NOT NULL"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set DocType=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Category, '"
						+ "WHERE (GL_Category_ID IS NULL OR GL_Category_ID=0)"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid Category=" + no);
		}

		// Set Currency

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET C_Currency_ID=(SELECT c.C_Currency_ID FROM C_Currency c"
						+ " WHERE c.ISO_Code=i.ISO_Code AND c.AD_Client_ID IN (0,i.AD_Client_ID)) "
						+ "WHERE C_Currency_ID IS NULL AND ISO_Code IS NOT NULL"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Currency from ISO=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET C_Currency_ID=(SELECT a.C_Currency_ID FROM C_AcctSchema a"
						+ " WHERE a.C_AcctSchema_ID=i.C_AcctSchema_ID AND a.AD_Client_ID=i.AD_Client_ID)"
						+ "WHERE C_Currency_ID IS NULL AND ISO_Code IS NULL"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Default Currency=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Currency, '"
						+ "WHERE (C_Currency_ID IS NULL OR C_Currency_ID=0)"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid Currency=" + no);
		}

		// Set Conversion Type
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET ConversionTypeValue='S' "
						+ "WHERE C_ConversionType_ID IS NULL AND ConversionTypeValue IS NULL"
						+ " AND I_IsImported='N'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set CurrencyType Value to Spot =" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET C_ConversionType_ID=(SELECT c.C_ConversionType_ID FROM C_ConversionType c"
						+ " WHERE c.Value=i.ConversionTypeValue AND c.AD_Client_ID IN (0,i.AD_Client_ID)) "
						+ "WHERE C_ConversionType_ID IS NULL AND ConversionTypeValue IS NOT NULL"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set CurrencyType from Value=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid CurrencyType, '"
						+ "WHERE (C_ConversionType_ID IS NULL OR C_ConversionType_ID=0) AND ConversionTypeValue IS NOT NULL"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid CurrencyTypeValue=" + no);
		}

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No ConversionType, '"
						+ "WHERE (C_ConversionType_ID IS NULL OR C_ConversionType_ID=0)"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - No CourrencyType=" + no);
		}

		// Set/Overwrite Home Currency Rate

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET CurrencyRate=1"
						+ "WHERE EXISTS (SELECT * FROM C_AcctSchema a"
						+ " WHERE a.C_AcctSchema_ID=i.C_AcctSchema_ID AND a.C_Currency_ID=i.C_Currency_ID)"
						+ " AND C_Currency_ID IS NOT NULL AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Home CurrencyRate=" + no);

		// Set Currency Rate

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET CurrencyRate=(SELECT r.MultiplyRate FROM C_Conversion_Rate r, C_AcctSchema s"
						+ " WHERE s.C_AcctSchema_ID=i.C_AcctSchema_ID AND s.AD_Client_ID=i.AD_Client_ID"
						+ " AND r.C_Currency_ID=i.C_Currency_ID AND r.C_Currency_ID_TO=s.C_Currency_ID"
						+ " AND r.AD_Client_ID=i.AD_Client_ID AND r.AD_Org_ID=i.AD_OrgDoc_ID"
						+ " AND r.C_ConversionType_ID=i.C_ConversionType_ID"
						+ " AND i.DateAcct BETWEEN r.ValidFrom AND r.ValidTo AND ROWNUM=1"

						// ORDER BY ValidFrom DESC

						+ ") WHERE CurrencyRate IS NULL OR CurrencyRate=0 AND C_Currency_ID>0"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Org Rate=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET CurrencyRate=(SELECT r.MultiplyRate FROM C_Conversion_Rate r, C_AcctSchema s"
						+ " WHERE s.C_AcctSchema_ID=i.C_AcctSchema_ID AND s.AD_Client_ID=i.AD_Client_ID"
						+ " AND r.C_Currency_ID=i.C_Currency_ID AND r.C_Currency_ID_TO=s.C_Currency_ID"
						+ " AND r.AD_Client_ID=i.AD_Client_ID"
						+ " AND r.C_ConversionType_ID=i.C_ConversionType_ID"
						+ " AND i.DateAcct BETWEEN r.ValidFrom AND r.ValidTo AND ROWNUM=1"

						// ORDER BY ValidFrom DESC

						+ ") WHERE CurrencyRate IS NULL OR CurrencyRate=0 AND C_Currency_ID>0"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Client Rate=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=No Rate, '"
						+ "WHERE CurrencyRate IS NULL OR CurrencyRate=0"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - No Rate=" + no);
		}

		// Set Period

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET C_Period_ID=(SELECT p.C_Period_ID FROM C_Period p"
						+ " INNER JOIN C_Year y ON (y.C_Year_ID=p.C_Year_ID)"
						+ " INNER JOIN AD_ClientInfo c ON (c.C_Calendar_ID=y.C_Calendar_ID)"
						+ " WHERE c.AD_Client_ID=i.AD_Client_ID"
						+ " AND i.DateAcct BETWEEN p.StartDate AND p.EndDate AND p.PeriodType='S' AND ROWNUM=1) "
						+ "WHERE C_Period_ID IS NULL"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Period=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Period, '"
						+ "WHERE C_Period_ID IS NULL OR C_Period_ID<>"
						+ "(SELECT C_Period_ID FROM C_Period p"
						+ " INNER JOIN C_Year y ON (y.C_Year_ID=p.C_Year_ID)"
						+ " INNER JOIN AD_ClientInfo c ON (c.C_Calendar_ID=y.C_Calendar_ID) "
						+ " WHERE c.AD_Client_ID=i.AD_Client_ID"
						+ " AND i.DateAcct BETWEEN p.StartDate AND p.EndDate AND p.PeriodType='S' AND ROWNUM=1)"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid Period=" + no);
		}

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_ErrorMsg=I_ErrorMsg||'WARN=Period Closed, ' "
						+ "WHERE C_Period_ID IS NOT NULL AND NOT EXISTS"
						+ " (SELECT * FROM C_PeriodControl pc WHERE pc.C_Period_ID=i.C_Period_ID AND DocBaseType='GLJ' AND PeriodStatus='O') "
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Period Closed=" + no);
		}

		// Posting Type

		sql = new StringBuffer("UPDATE I_GLJournal i " + "SET PostingType='A' "
				+ "WHERE PostingType IS NULL AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Actual PostingType=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid PostingType, ' "
						+ "WHERE PostingType IS NULL OR NOT EXISTS"
						+ " (SELECT * FROM AD_Ref_List r WHERE r.AD_Reference_ID=125 AND i.PostingType=r.Value)"
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid PostingTypee=" + no);
		}

		// ** Account Elements (optional) **
		// (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0)

		// Set Org from Name
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET AD_Org_ID=(SELECT o.AD_Org_ID FROM AD_Org o"
						+ " WHERE o.Value=i.OrgValue AND o.IsSummary='N' AND i.AD_Client_ID=o.AD_Client_ID) "
						+ "WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0) AND OrgValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'");
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Org from Value=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET AD_Org_ID=AD_OrgDoc_ID "
						+ "WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0) AND OrgValue IS NULL AND AD_OrgDoc_ID IS NOT NULL AND AD_OrgDoc_ID<>0"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Org from Doc Org=" + no);
		// Error Org
		sql = new StringBuffer(
				"UPDATE I_GLJournal o "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Org, '"
						+ "WHERE (AD_Org_ID IS NULL OR AD_Org_ID=0"
						+ " OR EXISTS (SELECT * FROM AD_Org oo WHERE o.AD_Org_ID=oo.AD_Org_ID AND (oo.IsSummary='Y' OR oo.IsActive='N')))"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		if (no != 0)
			log.warning("doIt - Invalid Org=" + no);

		// Set Account
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET Account_ID=(SELECT ev.C_ElementValue_ID FROM C_ElementValue ev"
						+ " INNER JOIN C_Element e ON (e.C_Element_ID=ev.C_Element_ID)"
						+ " INNER JOIN C_AcctSchema_Element ase ON (e.C_Element_ID=ase.C_Element_ID AND ase.ElementType='AC')"
						// //actualizado 21-08-08-Horacio
						// +
						// " WHERE ISNUMBER(substr(ev.Name,0,4)) = ISNUMBER(i.accountValue) AND ISNUMBER(i.accountValue) <> -1 AND ev.IsSummary='N' AND ev.IsActive = 'Y' "
						// +
						// " AND i.C_AcctSchema_ID=ase.C_AcctSchema_ID AND i.AD_Client_ID=ev.AD_Client_ID AND ROWNUM=1) "
						+ " WHERE ev.Value=i.AccountValue AND ev.IsSummary='N'"
						+ " AND i.C_AcctSchema_ID=ase.C_AcctSchema_ID AND i.AD_Client_ID=ev.AD_Client_ID AND ROWNUM=1) "
						+ "WHERE Account_ID IS NULL AND AccountValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Account from Value=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Account, '"
						+ "WHERE (Account_ID IS NULL OR Account_ID=0)"
						// +
						// " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'"
						// ).append( clientCheck );
						+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid Account=" + no);
		}

		// Set BPartner

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET C_BPartner_ID=(SELECT bp.C_BPartner_ID FROM C_BPartner bp"
						+ " WHERE bp.Value=i.BPartnerValue AND bp.IsSummary='N' AND i.AD_Client_ID=bp.AD_Client_ID) "
						+ "WHERE C_BPartner_ID IS NULL AND BPartnerValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set BPartner from Value=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid BPartner, '"
						+ "WHERE C_BPartner_ID IS NULL AND BPartnerValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid BPartner=" + no);
		}

		// Set Product
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET M_Product_ID=(SELECT p.M_Product_ID FROM M_Product p"
						+ " WHERE (p.Value=i.ProductValue OR p.UPC=i.UPC OR p.SKU=i.SKU)"
						+ " AND p.IsSummary='N' AND i.AD_Client_ID=p.AD_Client_ID AND ROWNUM=1) "
						+ "WHERE M_Product_ID IS NULL AND (ProductValue IS NOT NULL OR UPC IS NOT NULL OR SKU IS NOT NULL)"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Product from Value=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Product, '"
						+ "WHERE M_Product_ID IS NULL AND (ProductValue IS NOT NULL OR UPC IS NOT NULL OR SKU IS NOT NULL)"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		if (no != 0)
			log.fine("doIt - Invalid Product=" + no);

		// Set Project

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET C_Project_ID=(SELECT p.C_Project_ID FROM C_Project p"
						+ " WHERE p.Value=i.ProjectValue AND p.IsSummary='N' AND i.AD_Client_ID=p.AD_Client_ID) "
						+ "WHERE C_Project_ID IS NULL AND ProjectValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set Project from Value=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid Project, '"
						+ "WHERE C_Project_ID IS NULL AND ProjectValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid Project=" + no);
		}

		// Set TrxOrg
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET AD_OrgTrx_ID=(SELECT o.AD_Org_ID FROM AD_Org o"
						+ " WHERE o.Value=i.OrgValue AND o.IsSummary='N' AND i.AD_Client_ID=o.AD_Client_ID) "
						+ "WHERE AD_OrgTrx_ID IS NULL AND OrgTrxValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set OrgTrx from Value=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid OrgTrx, '"
						+ "WHERE AD_OrgTrx_ID IS NULL AND OrgTrxValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		if (no != 0)
			log.fine("doIt - Invalid OrgTrx=" + no);

		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET AD_OrgTrx_ID=(SELECT o.AD_Org_ID FROM AD_Org o"
						+ " WHERE o.Value=i.OrgValue AND o.IsSummary='N' AND i.AD_Client_ID=o.AD_Client_ID) "
						+ "WHERE AD_OrgTrx_ID IS NULL AND OrgTrxValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		log.fine("doIt - Set OrgTrx from Value=" + no);
		sql = new StringBuffer(
				"UPDATE I_GLJournal i "
						+ "SET I_IsImported='E', I_ErrorMsg=I_ErrorMsg||'ERR=Invalid OrgTrx, '"
						+ "WHERE AD_OrgTrx_ID IS NULL AND OrgTrxValue IS NOT NULL"
						+ " AND (C_ValidCombination_ID IS NULL OR C_ValidCombination_ID=0) AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());

		if (no != 0) {
			log.warning("doIt - Invalid OrgTrx=" + no);
		}

		// Source Amounts
		/*
		 * sql = new StringBuffer ("UPDATE I_GLJournal " +
		 * "SET AmtSourceDr = 0 " + "WHERE AmtSourceDr IS NULL" +
		 * " AND I_IsImported<>'Y'").append (clientCheck); no = DB.executeUpdate
		 * (sql.toString ()); log.debug ("doIt - Set 0 Source Dr=" + no); sql =
		 * new StringBuffer ("UPDATE I_GLJournal " + "SET AmtSourceCr = 0 " +
		 * "WHERE AmtSourceCr IS NULL" + " AND I_IsImported<>'Y'").append
		 * (clientCheck); no = DB.executeUpdate (sql.toString ()); log.debug
		 * ("doIt - Set 0 Source Cr=" + no); sql = new StringBuffer
		 * ("UPDATE I_GLJournal i " +
		 * "SET I_ErrorMsg=I_ErrorMsg||'WARN=Zero Source Balance, ' " +
		 * "WHERE (AmtSourceDr-AmtSourceCr)=0" +
		 * " AND I_IsImported<>'Y'").append (clientCheck); no = DB.executeUpdate
		 * (sql.toString ()); if (no != 0) log.warn
		 * ("doIt - Zero Source Balance=" + no);
		 */

		// Accounted Amounts (Only if No Error)
		/*
		 * sql = new StringBuffer ("UPDATE I_GLJournal " +
		 * "SET AmtAcctDr = ROUND(AmtSourceDr * CurrencyRate, 2) " // HARDCODED
		 * rounding + "WHERE AmtAcctDr IS NULL OR AmtAcctDr=0" +
		 * " AND I_IsImported='N'").append (clientCheck); no = DB.executeUpdate
		 * (sql.toString ());
		 * 
		 * log.debug ("doIt - Calculate Acct Dr=" + no); sql = new StringBuffer
		 * ("UPDATE I_GLJournal " +
		 * "SET AmtAcctCr = ROUND(AmtSourceCr * CurrencyRate, 2) " +
		 * "WHERE AmtAcctCr IS NULL OR AmtAcctCr=0" +
		 * " AND I_IsImported='N'").append (clientCheck); no = DB.executeUpdate
		 * (sql.toString ()); log.debug ("doIt - Calculate Acct Cr=" + no);
		 */

		sql = new StringBuffer("UPDATE I_GLJournal i "
				+ "SET I_ErrorMsg=I_ErrorMsg||'WARN=Zero Acct Balance, ' "
				// +
				// "WHERE (AmtSourceDr-AmtSourceCr)<>0 AND (AmtAcctDr-AmtAcctCr)=0"
				+ "WHERE (AmtAcctDr-AmtAcctCr)=0" + " AND I_IsImported<>'Y'")
				.append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		if (no != 0)
			log.warning("doIt - Zero Acct Balance=" + no);
		sql = new StringBuffer("UPDATE I_GLJournal i "
				+ "SET I_ErrorMsg=I_ErrorMsg||'WARN=Check Acct Balance, ' "
				+ "WHERE ABS(AmtAcctDr-AmtAcctCr)>100000000" // 100 mio
				+ " AND I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		if (no != 0)
			log.warning("doIt - Chack Acct Balance=" + no);

		// Set ValidCombination
		sql = new StringBuffer("UPDATE I_GLJournal i "
				+ "SET c_validcombination_id = "
				+ "(SELECT c_validcombination_id FROM c_validcombination vc "
				+ "	WHERE vc.account_id = i.account_id "
				+ "	AND vc.ad_client_id =  i.ad_client_id LIMIT 1) "
				+ "WHERE i.account_id is not null AND i.ad_client_id = "
				+ getAD_Client_ID());
		no = DB.executeUpdate(sql.toString());
		log.info("doIt -c_validcombination_id actualizados = " + no);

		/*********************************************************************/

		// Get Balance
		// sql = new StringBuffer
		// ("SELECT SUM(AmtSourceDr)-SUM(AmtSourceCr), SUM(AmtAcctDr)-SUM(AmtAcctCr) "
		sql = new StringBuffer("SELECT SUM(AmtAcctDr)-SUM(AmtAcctCr) "
				+ "FROM I_GLJournal " + "WHERE I_IsImported='N'")
				.append(clientCheck);
		PreparedStatement pstmt = null;
		try {
			pstmt = DB.prepareStatement(sql.toString());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// BigDecimal source = rs.getBigDecimal(1);
				BigDecimal acct = rs.getBigDecimal(1);
				// if (source != null && source.compareTo(Env.ZERO) == 0
				if (acct != null && acct.compareTo(Env.ZERO) == 0)
					log.info("doIt - Import Balance = 0");
				else
					// log.warn("doIt - Balance Source=" + source + ", Acct=" +
					// acct);
					log.warning("doIt - Balance Acct=" + acct);
				// if (source != null)
				// addLog (0, null, source, "@AmtSourceDr@ - @AmtSourceCr@");
				if (acct != null)
					addLog(0, null, acct, "@AmtAcctDr@ - @AmtAcctCr@");
			}

			rs.close();
			pstmt.close();
			pstmt = null;
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "doIt - get balance", ex);
		}

		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException ex1) {
		}

		pstmt = null;

		// Count Errors
		int errors = DB.getSQLValue(get_TrxName(),
				"SELECT COUNT(*) FROM I_GLJournal WHERE I_IsImported NOT IN ('Y','N')"
						+ clientCheck);

		if (errors != 0) {
			if (m_IsValidateOnly || m_IsImportOnlyNoErrors) {
				throw new Exception("@Errors@=" + errors);
			}
		} else if (m_IsValidateOnly) {
			return "@Errors@=" + errors;
		}

		log.info("doIt - Validation Errors=" + errors);

		/*********************************************************************/

		int noInsert = 0;
		int noInsertJournal = 0;
		int noInsertLine = 0;
		MJournalBatch batch = null; // Change Batch per Batch DocumentNo
		String BatchDocumentNo = "";
		MJournal journal = null;
		String JournalDocumentNo = "";
		Timestamp DateAcct = null;
		ArrayList batches = new ArrayList();

		// Go through Journal Records

		sql = new StringBuffer("SELECT * FROM I_GLJournal "
				+ "WHERE I_IsImported='N'")
				.append(clientCheck)
				.append(" ORDER BY BatchDocumentNo, JournalDocumentNo,"
						+ "C_AcctSchema_ID, PostingType, C_DocType_ID, GL_Category_ID, C_Currency_ID, TRUNC(DateAcct), Line, I_GLJournal_ID");

		try {
			pstmt = DB.prepareStatement(sql.toString());

			ResultSet rs = pstmt.executeQuery();

			//

			while (rs.next()) {
				X_I_GLJournal imp = new X_I_GLJournal(getCtx(), rs, null);

				// New Batch if Batch Document No changes

				String impBatchDocumentNo = imp.getBatchDocumentNo();

				if (impBatchDocumentNo == null) {
					impBatchDocumentNo = "";
				}

				if ((batch == null)
						|| !BatchDocumentNo.equals(impBatchDocumentNo)) {
					BatchDocumentNo = impBatchDocumentNo; // cannot compare real
															// DocumentNo
					batch = new MJournalBatch(getCtx(), 0, null);
					batch.setClientOrg(imp.getAD_Client_ID(),
							imp.getAD_OrgDoc_ID());

					if ((imp.getBatchDocumentNo() != null)
							&& (imp.getBatchDocumentNo().length() > 0)) {
						batch.setDocumentNo(imp.getBatchDocumentNo());
					}

					batch.setC_DocType_ID(imp.getC_DocType_ID());

					String description = imp.getDescription();

					if ((description == null) || (description.length() == 0)) {
						description = "*Import-"
								+ new Timestamp(System.currentTimeMillis());
					} /*
					 * else { description += " *Import-"; }
					 * 
					 * description += new Timestamp(
					 * System.currentTimeMillis());
					 */
					batch.setDescription(description);
					batch.setDateAcct(imp.getDateAcct());
					if (!batch.save()) {
						log.log(Level.SEVERE, "doIt - Batch not saved");

						break;
					}

					batches.add(batch);
					noInsert++;
					journal = null;
				}

				// Journal

				String impJournalDocumentNo = imp.getJournalDocumentNo();
				if (impJournalDocumentNo == null) {
					impJournalDocumentNo = "";
				}

				Timestamp impDateAcct = TimeUtil.getDay(imp.getDateAcct());

				if ((journal == null)
						|| !JournalDocumentNo.equals(impJournalDocumentNo)
						|| (journal.getC_AcctSchema_ID() != imp
								.getC_AcctSchema_ID())
						|| (journal.getC_DocType_ID() != imp.getC_DocType_ID())
						|| (journal.getGL_Category_ID() != imp
								.getGL_Category_ID())
						|| !journal.getPostingType().equals(
								imp.getPostingType())
						|| (journal.getC_Currency_ID() != imp
								.getC_Currency_ID())
						|| !impDateAcct.equals(DateAcct)) {
					JournalDocumentNo = impJournalDocumentNo; // cannot compare
																// real
																// DocumentNo
					DateAcct = impDateAcct;
					journal = new MJournal(getCtx(), 0, null);
					journal.setGL_JournalBatch_ID(batch.getGL_JournalBatch_ID());
					journal.setClientOrg(imp.getAD_Client_ID(),
							imp.getAD_OrgDoc_ID());

					//

					String description = imp.getBatchDescription();

					if ((description == null) || (description.length() == 0)) {
						description = "(Import)";
					}

					journal.setDescription(description);

					if ((imp.getJournalDocumentNo() != null)
							&& (imp.getJournalDocumentNo().length() > 0)) {
						journal.setDocumentNo(imp.getJournalDocumentNo());
					}

					//

					journal.setC_AcctSchema_ID(imp.getC_AcctSchema_ID());
					journal.setC_DocType_ID(imp.getC_DocType_ID());
					journal.setGL_Category_ID(imp.getGL_Category_ID());
					journal.setPostingType(imp.getPostingType());

					//

					journal.setCurrency(imp.getC_Currency_ID(),
							imp.getC_ConversionType_ID(), imp.getCurrencyRate());

					//

					journal.setC_Period_ID(imp.getC_Period_ID());
					journal.setDateAcct(imp.getDateAcct()); // sets Period if
															// not defined
					journal.setDateDoc(imp.getDateAcct());

					//

					if (!journal.save()) {
						log.log(Level.SEVERE, "doIt - Journal not saved");

						break;
					}

					noInsertJournal++;
				}

				// Lines

				MJournalLine line = new MJournalLine(journal);

				//

				line.setDescription(imp.getDescription());
				line.setCurrency(imp.getC_Currency_ID(),
						imp.getC_ConversionType_ID(), imp.getCurrencyRate());

				// Set/Get Account Combination
				/*
				 * if (imp.getC_ValidCombination_ID() == 0) { MAccount acct =
				 * MAccount.get(getCtx(), imp.getAD_Client_ID(),
				 * imp.getAD_Org_ID(), imp.getC_AcctSchema_ID(),
				 * imp.getAccount_ID(), imp.getM_Product_ID(),
				 * imp.getC_BPartner_ID(), imp.getAD_OrgTrx_ID(),
				 * imp.getC_LocFrom_ID(), imp.getC_LocTo_ID(),
				 * imp.getC_SalesRegion_ID(), imp.getC_Project_ID(),
				 * imp.getC_Campaign_ID(), imp.getC_Activity_ID(),
				 * imp.getUser1_ID(), imp.getUser2_ID()); if (acct != null &&
				 * acct.getID() == 0) acct.save(); if (acct == null ||
				 * acct.getID() == 0) {
				 * imp.setI_ErrorMsg("ERROR creating Account");
				 * imp.setI_IsImported(false); imp.save();
				 * 
				 * continue; } else {
				 * line.setC_ValidCombination_ID(acct.getID());
				 * imp.setC_ValidCombination_ID(acct.getID());
				 * 
				 * } } else line.setC_ValidCombination_ID
				 * (imp.getC_ValidCombination_ID());
				 */

				line.setC_ElementValue_ID(imp.getAccount_ID());
				//

				line.setLine(imp.getLine());

				line.setAmtSourceCr(imp.getAmtSourceCr());
				line.setAmtSourceDr(imp.getAmtSourceDr());
				line.setAmtAcct(imp.getAmtAcctDr(), imp.getAmtAcctCr()); // only
																			// if
																			// not
																			// 0
				line.setDateAcct(imp.getDateAcct());
				line.setC_ValidCombination_ID(imp.getC_ValidCombination_ID());
				//

				line.setC_UOM_ID(imp.getC_UOM_ID());
				line.setQty(imp.getQty());

				//

				if (line.save()) {
					imp.setGL_JournalBatch_ID(batch.getGL_JournalBatch_ID());
					imp.setGL_Journal_ID(journal.getGL_Journal_ID());
					imp.setGL_JournalLine_ID(line.getGL_JournalLine_ID());
					imp.setI_IsImported(true);
					imp.setProcessed(true);

					if (imp.save()) {
						noInsertLine++;
					}
				}

			} // if records

			// AGREGADO - HORACIO - 21/08/08
			StringBuffer update;
			PreparedStatement stmt = null;
			String translation = "";

			for (int i = 0; i < batches.size(); i++) {
				batch = (MJournalBatch) batches.get(i);
				if (!batch.completeIt().equals(
						MJournalBatch.DOCSTATUS_Completed)) {
					update = new StringBuffer(" UPDATE I_GLJournal ");
					update.append(" SET I_ErrorMsg = ? ");
					update.append(" WHERE Gl_JournalBatch_ID = ? ");

					stmt = DB.prepareStatement(update.toString());
					translation = Msg.parseTranslation(Env.getCtx(),
							batch.getProcessMsg());
					stmt.setString(1, "No se pudo completar el asiento: "
							+ translation);
					stmt.setInt(2, batch.getGL_JournalBatch_ID());
					stmt.executeUpdate();
				} else {
					batch.setDocStatus(MJournalBatch.DOCSTATUS_Completed);
					batch.setDocAction(MJournalBatch.DOCACTION_Void);
					batch.setProcessed(true);
					batch.save();
				}
			}
			if (stmt != null)
				stmt.close();
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			log.log(Level.SEVERE, "doIt", e);
		}

		// clean up

		try {
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException ex1) {
		}

		pstmt = null;

		// Set Error to indicator to not imported

		sql = new StringBuffer("UPDATE I_GLJournal "
				+ "SET I_IsImported='N', Updated=SysDate "
				+ "WHERE I_IsImported<>'Y'").append(clientCheck);
		no = DB.executeUpdate(sql.toString());
		addLog(0, null, new BigDecimal(no), "@Errors@");

		//

		addLog(0, null, new BigDecimal(noInsert),
				"@GL_JournalBatch_ID@: @Inserted@");
		addLog(0, null, new BigDecimal(noInsertJournal),
				"@GL_Journal_ID@: @Inserted@");
		addLog(0, null, new BigDecimal(noInsertLine),
				"@GL_JournalLine_ID@: @Inserted@");

		return "";
	} // doIt

	protected String getMsg(String msg) {
		return Msg.translate(getCtx(), msg);
	}
} // ImportGLJournal

/*
 * @(#)ImportGLJournal.java 02.07.07
 * 
 * Fin del fichero ImportGLJournal.java
 * 
 * Versión 2.2
 */
