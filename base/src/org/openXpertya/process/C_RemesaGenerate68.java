/*
 * @(#)C_RemesaGenerate.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */


package org.openXpertya.process;

import java.sql.*;
import java.util.*;

import java.io.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.openXpertya.model.*;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.*;


/**
 *	Generador de Remesas
 *
 *  @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Jose A. Gonzalez, Conserti.
 * 
 *  @version $Id: C_RemesaGenerate.java,v 0.9 $
 * 
 *  @Colaborador $Id: Consultoria y Soporte en Redes y Tecnologias de la Informacion S.L.
 * 
 */
public class C_RemesaGenerate68 extends SvrProcess
{
	/**
	 * 	ExportData Constructor
	 */
	public C_RemesaGenerate68()
	{
		super();
		log.fine("C_RemesaGenerate68");
	}	//	ImportBPartner

	/**	Export record id to perform the export	*/
	private int				m_C_Remesa_ID = 0;
	/** MRemesa */
	private MRemesa 		remesa;
	/** Name	 */
	private String 			name = "";
	/**	File to export data				*/
	private File   			outFile;
	/** Writer to export data */
	private Writer 			writer;
	/** Window No */
	private int 			m_WindowNo = 0;
	/** Context */
	private Properties 		m_ctx;
	/** Separador */
	private String 			separador = "\t";
	/** Numero de registros de remesa */
	private int 			numRegistros = 0;
	/** Numero de lineas  de Pagos */
	private int 			numLineasPagos = 0;
	/** Calendar */
	private Calendar 		c;
	private String norma="";
	private String subnorma="";
	public static int remesa_id=0;
	/**
	 *  Prepare - e.g., get Parameters.
	 */
	public void set_c_remesa_id(int remesa){
		remesa_id=remesa;
	}
	public void prepare()
	{
		m_ctx = getCtx();
		m_WindowNo = Env.createWindowNo(null); //�null?
		
		m_C_Remesa_ID = getRecord_ID();
		if(m_C_Remesa_ID==0){
			m_C_Remesa_ID=remesa_id;
			log.fine("Llego aqui y m_C_Remesa_ID="+m_C_Remesa_ID);
		}
		
		log.fine("En prepare de C_RemesaGenerate68");
		remesa = new MRemesa(m_ctx, m_C_Remesa_ID,null);
		ProcessInfoParameter[] para = getParameter();
		for (int i = 0; i < para.length; i++)
		{
			name = para[i].getParameterName();
			log.fine("C_RemesaGenerate.prepare - Unknown Parameter: " + name);
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle(Msg.getMsg(m_ctx, "Remesa"));
		//
		if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) //�null?
			return;

		//	Create File
		outFile = ExtensionFileFilter.getFile(chooser.getSelectedFile(),
				chooser.getFileFilter());
		//ejecutar();
	}	//	prepare


	/**
	 *  Perrform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		log.fine("En C_Remesa68, en doIt");
		try { 
			outFile.createNewFile();
		} catch (IOException e) {
			log.saveError("C_RemesaGenerate68 - can not create file", e);
		}		
		try
		{
			FileWriter fwout = new FileWriter (outFile, false);
			writer = new BufferedWriter(fwout);
		}
		catch (FileNotFoundException fnfe)
		{
			log.fine("C_RemesaGenerate68 - " + fnfe.toString());
		}
		catch (IOException e)
		{
		}
		if(comprueba_Remesa()==0){
			return "Error";
		}
		asignarRemesa();
		//cabeceraPresentador();
		cabeceraOrdenante();
		try
		{
			writer.flush();
			writer.close();
		}
		catch (IOException e)
		{
			return e.toString();
		}
		return "";
	}	//	doIt
	
	
	/**
	* Escribe las lineas del segundo nivel de datos de la remesa
	* 
	* @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	*/
	private void cabeceraOrdenante()
	{
		log.fine("En la cabecera ordenante");
		ArrayList list = new ArrayList();
		int cont=0;
		String aux="";
		String sql = "SELECT nor.N_Cabecera_Ordenante, nor.N_Euros, oin.DUNS, nor.Cod_Ordenante, rem.ExecuteDate,";
		sql += " bac.AccountNo, rem.C_Remesa_ID, nor.subnorma, bac.iban, bac.oficina, bac.sucursal, bac.dc, bac.cc FROM C_Remesa rem";
		sql += " INNER JOIN C_Norma nor ON (rem.C_Norma_ID=nor.C_Norma_ID)";
		sql += " INNER JOIN AD_Org org ON (rem.AD_Org_ID=org.AD_Org_ID)";
		sql += " INNER JOIN AD_OrgInfo oin ON (rem.AD_Org_ID=oin.AD_Org_ID)";
		sql += " INNER JOIN C_Bank ban ON (rem.AD_Org_ID=ban.AD_Org_ID AND ban.IsOwnBank='Y')";
		sql += " INNER JOIN C_BankAccount bac ON (ban.C_Bank_ID=bac.C_Bank_ID)";
		sql += " WHERE rem.C_Remesa_ID=" + m_C_Remesa_ID;
		//
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				numRegistros++;
				StringBuffer sb = new StringBuffer("");
				sb.append(rs.getString(1)); //Codigo Reg 2 pos
				sb.append(rs.getString(2)); //Codigo Dato 2 pos
				aux=rs.getString(3);
				cont=rs.getString(3).length();
				if(aux.contains("-")){
					aux=aux.replace("-", "");
				}

				aux=aux+rs.getString(4);
				if(aux.contains(" ")){
					aux=aux.replace(" ", "");
				}
				cont=aux.length();
				if(cont<12){
					while(aux.length()<12){
						aux="0"+aux;
					}
				}
				sb.append(aux);//NIF+Sufijo=Codigo presentador de 12 pos ajustado a la derecha

				sb.append("            ");//Libre de 12 pos
				sb.append("001");//Número de dato
				c = Calendar.getInstance(); 
				c.setTime(Env.getContextAsDate(m_ctx, "#Date"));//Fecha confección soporte
				if(c.get(Calendar.DAY_OF_MONTH)<10){			//Dia con 2 digitos
					sb.append("0"+c.get(Calendar.DAY_OF_MONTH));
				}else{					
					sb.append(c.get(Calendar.DAY_OF_MONTH));
				}
				int mes=c.get(Calendar.MONTH)+1;
				if(c.get(Calendar.MONTH)+1<10){					//Mes con 2 digitos
					sb.append("0"+mes);
				}else{
					sb.append(mes);
				}
				sb.append(String.valueOf(c.get(Calendar.YEAR)).substring(2,4));//Año 2 digitos
				sb.append("         ");//Libre 9 pos
				
				sb.append(rs.getString(9));//Codigo IBAN
				sb.append(rs.getString(6));//Cuenta 
				//sb.append(rs.getString(10)+rs.getString(11)+rs.getString(12)+rs.getString(13));//Numero de cuenta
				sb.append("                              ");//Libre de 30 pos
				guardarLinea(sb.toString());
				
				int rm = rs.getInt(7);
				log.fine("En cabeceraOrdenante, para las lineas de pagos:"+rm);
				Beneficiario(rm);
				
				list.add(new MRemesa(m_ctx, rm,null));
			}
			resumenOrdenante();
			/*MRemesa[] rem = new MRemesa[list.size()];
			list.toArray(rem);
			for (int i = 0; i < rem.length; i++)
				resumenOrdenante(rem[i]);
			*/
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - cabeceraOrdenante", e);
		}
	}
	
	/**
	* Escribe las lineas del tercer nivel de datos de la remesa
	* 
	* @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	*/
	private void Beneficiario(int ID)
	{
		log.fine("En C_RemesaGenerate, en la funcion lineasPagos, con ID="+ID);
		String sql = "SELECT DISTINCT nor.N_Reg_Individual, nor.N_Euros, oin.DUNS, nor.Cod_Ordenante, bp.Name,";
		sql += " nor.c_norma_id, ips.DueAmt, nor.Description, bp.c_bpartner_id, ips.duedate, clo.address1, clo.postal, reg.name, trl.name,rem.c_remesa_id, cou.countrycode,ips.c_invoicepayschedule_id,clo.plaza FROM C_InvoicePaySchedule ips";
		sql += " INNER JOIN C_Invoice inv ON (ips.C_Invoice_ID=inv.C_Invoice_ID)";
		sql += " INNER JOIN C_BPartner bp ON (inv.C_BPartner_ID=bp.C_BPartner_ID)";
		sql += " INNER JOIN C_RemesaLine rli ON (inv.C_BPartner_ID=rli.C_BPartner_ID)";
		sql += " INNER JOIN C_Remesa rem ON (rli.C_Remesa_ID=rem.C_Remesa_ID)";
		sql += " INNER JOIN AD_Org org ON (rem.AD_Org_ID=org.AD_Org_ID)";
		sql += " INNER JOIN AD_OrgInfo oin ON (rem.AD_Org_ID=oin.AD_Org_ID)";
		sql += " INNER JOIN C_Norma nor ON (rem.C_Norma_ID=nor.C_Norma_ID)";
		//sql += " INNER JOIN C_BP_BankAccount bak ON (inv.C_BPartner_ID=bak.C_BPartner_ID)";//Será C_BankAccount en lugar de C_BP_BankAccount?
		sql += " INNER JOIN C_BPartner_Location bloc ON (bloc.c_bpartner_id=bp.c_bpartner_id)";
		sql += " INNER JOIN C_Location clo ON (clo.c_location_id=bloc.c_location_id)";
		sql += " INNER JOIN C_Country_trl trl ON (trl.c_country_id=clo.c_country_id)";
		sql += " INNER JOIN C_Region reg ON (reg.c_region_id=clo.c_region_id)";
		sql += " INNER JOIN C_Country cou ON (cou.c_country_id=clo.c_country_id)";
		//sql += " WHERE ips.IsSelected='Y' AND rem.C_Remesa_ID=" + ID;
		sql += " WHERE rem.C_Remesa_ID=" + ID + " AND ips.C_Remesa_ID="+ ID;
		sql += " AND bloc.c_bpartner_location_id=inv.c_bpartner_location_id ";
		//
		int cont=0; 
		String aux="";
		PreparedStatement pstmt = null;
		try
		{
			numRegistros++;
			pstmt = DB.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				StringBuffer sb = new StringBuffer("");
				sb.append(rs.getString(1));//Código Reg. 2 pos
				sb.append(rs.getString(2));//Código Operación 2 pos
				aux=rs.getString(3);
				cont=rs.getString(3).length();
				if(aux.contains("-")){
					aux=aux.replace("-", "");
				}

				aux=aux+rs.getString(4);
				if(aux.contains(" ")){
					aux=aux.replace(" ", "");
				}
				cont=aux.length();
				if(cont<12){
					while(aux.length()<12){
						aux="0"+aux;
					}
				}
				sb.append(aux);//NIF+Sufijo=Codigo presentador de 12 pos ajustado a la derecha

				//sb.append(numLineasPagos);
				cont=String.valueOf(rs.getInt(9)).length();
				aux=String.valueOf(rs.getInt(9));
				if(cont<12){
					while(aux.length()<12){
						aux=aux.concat("0");
					}
				}
				sb.append(aux);//Codigo referencia 12 pos
				sb.append("010");//Número de dato
				cont=rs.getString(5).length();
				aux=rs.getString(5);
				if(cont<40){		
					while(aux.length()<40){
						aux=aux.concat(" ");
					}
					log.fine("la cadena que devuelve es:"+aux+", y tamaño="+aux.length());
				}else if(cont>40){
					log.fine("Error, la cadena es mayor de 40");
					return;
				} 
				sb.append(aux);//Nombre titular domiciliacion
				sb.append("                             ");//Libre de 29 posiciones
				guardarLinea(sb.toString());
				/////Empieza segundo registro
				Beneficiario2(rs);
			}
			
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - lineasPagos", e);
		}
	}
	
	private void Beneficiario2(ResultSet rs)
	{		
		int cont=0;
		String aux="";
		try{
		numRegistros++;
		StringBuffer sb = new StringBuffer("");
		sb.append(rs.getString(1));//Código Reg. 2 pos
		sb.append(rs.getString(2));//Código Operación 2 pos
		aux=rs.getString(3);
		cont=rs.getString(3).length();
		if(aux.contains("-")){
			aux=aux.replace("-", "");
		}

		aux=aux+rs.getString(4);
		if(aux.contains(" ")){
			aux=aux.replace(" ", "");
		}
		cont=aux.length();
		if(cont<12){
			while(aux.length()<12){
				aux="0"+aux;
			}
		}
		sb.append(aux);//NIF+Sufijo=Codigo presentador de 12 pos ajustado a la derecha

		cont=String.valueOf(rs.getInt(9)).length();
		aux=String.valueOf(rs.getInt(9));
		if(cont<12){
			while(aux.length()<12){
				aux=aux.concat("0");
			}
		}
		sb.append(aux);//Codigo referencia 12 pos
		sb.append("011");//Número de dato
		cont=rs.getString(11).length();
		aux=rs.getString(11);
		if(cont<45){		
			while(aux.length()<45){
				aux=aux.concat(" ");
			}
			log.fine("la cadena que devuelve es:"+aux+", y tamaño="+aux.length());
		}else if(cont>45){
			log.fine("Error, la cadena es mayor de 40");
			return;
		} 
		sb.append(aux);//Domicilio beneficiario
		sb.append("                        ");//Libre de 24 posiciones
		guardarLinea(sb.toString());
		Beneficiario3(rs);
				/////Empieza segundo registro
		}catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - lineasPagos", e);
		}		
			
		
	}
	
	private void Beneficiario3(ResultSet rs)
	{		
		int cont=0;
		String aux="";
		try{
		numRegistros++;
		StringBuffer sb = new StringBuffer("");
		sb.append(rs.getString(1));//Código Reg. 2 pos
		sb.append(rs.getString(2));//Código Operación 2 pos
		aux=rs.getString(3);
		cont=rs.getString(3).length();
		if(aux.contains("-")){
			aux=aux.replace("-", "");
		}

		aux=aux+rs.getString(4);
		if(aux.contains(" ")){
			aux=aux.replace(" ", "");
		}
		cont=aux.length();
		if(cont<12){
			while(aux.length()<12){
				aux="0"+aux;
			}
		}
		sb.append(aux);//NIF+Sufijo=Codigo presentador de 12 pos ajustado a la derecha

		cont=String.valueOf(rs.getInt(9)).length();
		aux=String.valueOf(rs.getInt(9));
		if(cont<12){
			while(aux.length()<12){
				aux=aux.concat("0");
			}
		}
		sb.append(aux);//Codigo referencia 12 pos
		sb.append("012");//Número de dato
		sb.append(rs.getString(12));//Codigo postal
		//Aqui iria la plaza con 40 posiciones, buscar en que lugar se encuentra
		cont=rs.getString(18).length();
		aux=rs.getString(18);
		if(cont<40){		
			while(aux.length()<40){
				aux=aux.concat(" ");
			}
			log.fine("la cadena que devuelve es:"+aux+", y tamaño="+aux.length());
		}else if(cont>40){
			log.fine("Error, la cadena es mayor de 40");
			return;
		} 
		sb.append("                        ");//Libre de 24 posiciones
		guardarLinea(sb.toString());
		Beneficiario4(rs);
				/////Empieza segundo registro
		}catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - lineasPagos", e);
		}		
			
		
	}
	
	private void Beneficiario4(ResultSet rs)
	{		
		int cont=0;
		String aux="";
		try{
		numRegistros++;
		StringBuffer sb = new StringBuffer("");
		sb.append(rs.getString(1));//Código Reg. 2 pos
		sb.append(rs.getString(2));//Código Operación 2 pos
		aux=rs.getString(3);
		cont=rs.getString(3).length();
		if(aux.contains("-")){
			aux=aux.replace("-", "");
		}

		aux=aux+rs.getString(4);
		if(aux.contains(" ")){
			aux=aux.replace(" ", "");
		}
		cont=aux.length();
		if(cont<12){
			while(aux.length()<12){
				aux="0"+aux;
			}
		}
		sb.append(aux);//NIF+Sufijo=Codigo presentador de 12 pos ajustado a la derecha

		cont=String.valueOf(rs.getInt(9)).length();
		aux=String.valueOf(rs.getInt(9));
		if(cont<12){
			while(aux.length()<12){
				aux=aux.concat("0");
			}
		}
		sb.append(aux);//Codigo referencia 12 pos
		sb.append("013");//Número de dato
		sb.append(rs.getString(12));//Codigo postal
		
		cont=rs.getString(13).length();
		aux=rs.getString(13);
		if(cont<30){		
			while(aux.length()<30){
				aux=aux.concat(" ");
			}
			log.fine("la cadena que devuelve es:"+aux+", y tamaño="+aux.length());
		}else if(cont>30){
			log.fine("Error, la cadena es mayor de 30");
			return;
		} 
		sb.append(aux);//Provincia de 30 pos
		cont=rs.getString(14).length();
		aux=rs.getString(14);
		if(cont<20){		
			while(aux.length()<20){
				aux=aux.concat(" ");
			}
			log.fine("la cadena que devuelve es:"+aux+", y tamaño="+aux.length());
		}else if(cont>20){
			log.fine("Error, la cadena es mayor de 20");
			return;
		} 
		sb.append(aux);//Pais de 20 pos
		sb.append("          ");//Libre de 10 pos
		guardarLinea(sb.toString());
		// Empieza Cabecera pago
		CabeceraPago(rs);
		}catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - lineasPagos", e);
		}		
			
		
	}
	
	private void CabeceraPago(ResultSet rs)
	{		
		int cont=0;
		String aux="";
		try{
		numRegistros++;
		StringBuffer sb = new StringBuffer("");
		sb.append(rs.getString(1));//Código Reg. 2 pos
		sb.append(rs.getString(2));//Código Operación 2 pos
		aux=rs.getString(3);
		cont=rs.getString(3).length();
		if(aux.contains("-")){
			aux=aux.replace("-", "");
		}

		aux=aux+rs.getString(4);
		if(aux.contains(" ")){
			aux=aux.replace(" ", "");
		}
		cont=aux.length();
		if(cont<12){
			while(aux.length()<12){
				aux="0"+aux;
			}
		}
		sb.append(aux);//NIF+Sufijo=Codigo presentador de 12 pos ajustado a la derecha

		cont=String.valueOf(rs.getInt(9)).length();
		aux=String.valueOf(rs.getInt(9));
		if(cont<12){
			while(aux.length()<12){
				aux=aux.concat("0");
			}
		}
		sb.append(aux);//Codigo referencia 12 pos
		sb.append("014");//Número de dato
		sb.append("0"+rs.getInt(15));//El Numero de pago de 8 pos
		c = Calendar.getInstance(); //Fecha de Pago
		c.setTime(Env.getContextAsDate(m_ctx, "#Date"));//Fecha confección soporte
		if(c.get(Calendar.DAY_OF_MONTH)<10){			//Dia con 2 digitos
			sb.append("0"+c.get(Calendar.DAY_OF_MONTH));
		}else{					
			sb.append(c.get(Calendar.DAY_OF_MONTH));
		}
		int mes=c.get(Calendar.MONTH)+1;
		if(c.get(Calendar.MONTH)+1<10){					//Mes con 2 digitos
			sb.append("0"+mes);
		}else{
			sb.append(mes);
		}
		sb.append(c.get(Calendar.YEAR));//Año 4 digitos
		cont=String.valueOf(rs.getBigDecimal(7)).length();
		aux=String.valueOf(rs.getBigDecimal(7));
		if(aux.contains(".")){
			aux=aux.replace(".", "");
		}
		if(cont<12){
			while(aux.length()<10){
				aux="0"+aux;
			}
		}
		sb.append(aux);//Importe 12 pos
		sb.append("0");//Indicativo de presentación.Hecho para presentacion y no para anulación
		sb.append(rs.getString(16));//Código ISO del País beneficiario
		sb.append("      ");//Iria el Código Estadístico del concepto de pago
		sb.append("                                ");//Libre de 32 posiciones
		guardarLinea(sb.toString());
		DetallePago(rs);
		}catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - lineasPagos", e);
		}		
			
		
	}
	
	private void DetallePago(ResultSet rs)
	{		
		int cont=0;
		String aux="";
		try{
		numRegistros++;
		StringBuffer sb = new StringBuffer("");
		sb.append(rs.getString(1));//Código Reg. 2 pos
		sb.append(rs.getString(2));//Código Operación 2 pos
		aux=rs.getString(3);
		cont=rs.getString(3).length();
		if(aux.contains("-")){
			aux=aux.replace("-", "");
		}

		aux=aux+rs.getString(4);
		if(aux.contains(" ")){
			aux=aux.replace(" ", "");
		}
		cont=aux.length();
		if(cont<12){
			while(aux.length()<12){
				aux="0"+aux;
			}
		}
		sb.append(aux);//NIF+Sufijo=Codigo presentador de 12 pos ajustado a la derecha

		cont=String.valueOf(rs.getInt(9)).length();
		aux=String.valueOf(rs.getInt(9));
		if(aux.contains(".")){
			aux=aux.replace(".", "");
		}
		if(cont<12){
			while(aux.length()<12){
				aux=aux.concat("0");
			}
		}
		sb.append(aux);//Codigo referencia 12 pos
		sb.append("015");//Número de dato
		sb.append("0"+rs.getInt(15));//El Numero de pago de 8 pos
		cont=String.valueOf(rs.getInt(17)).length();
		aux=String.valueOf(rs.getInt(17));
		if(cont<12){
			while(aux.length()<12){
				aux=aux.concat("0");
			}
		}
		sb.append(aux);
		c = Calendar.getInstance(); //Fecha de Pago
		c.setTime(Env.getContextAsDate(m_ctx, "#Date"));//Fecha confección soporte
		if(c.get(Calendar.DAY_OF_MONTH)<10){			//Dia con 2 digitos
			sb.append("0"+c.get(Calendar.DAY_OF_MONTH));
		}else{					
			sb.append(c.get(Calendar.DAY_OF_MONTH));
		}
		int mes=c.get(Calendar.MONTH)+1;
		if(c.get(Calendar.MONTH)+1<10){					//Mes con 2 digitos
			sb.append("0"+mes);
		}else{
			sb.append(mes);
		}
		sb.append(c.get(Calendar.YEAR));//Año 4 digitos
		cont=String.valueOf(rs.getBigDecimal(7)).length();
		aux=String.valueOf(rs.getBigDecimal(7));
		if(aux.contains(".")){
			aux=aux.replace(".", "");
		}
		if(cont<12){
			while(aux.length()<10){
				aux="0"+aux;
			}
		}
		sb.append(aux);//Importe 12 pos
		if(String.valueOf(rs.getBigDecimal(7)).contains("-")){
			sb.append("D");//Si el importe es negativo
		}else{
			sb.append("H");//Si el importe es positivo
		}
		sb.append("                          ");//Libre de 26 posiciones
		guardarLinea(sb.toString());
		
		}catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - lineasPagos", e);
		}		
			
		
	}
	
	/**
	* Escribe las lineas de resumen del segundo nivel de datos de la remesa
	* 
	* @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	*/
	private void resumenOrdenante()
	{

		String sql = "SELECT nor.N_Total_Ordenante, nor.N_Euros, oin.DUNS, nor.Cod_Ordenante, rem.TotalAmt";
		sql += " FROM C_Remesa rem";
		sql += " INNER JOIN AD_Org org ON (rem.AD_Org_ID=org.AD_Org_ID)";
		sql += " INNER JOIN AD_OrgInfo oin ON (rem.AD_Org_ID=oin.AD_Org_ID)";
		sql += " INNER JOIN C_Norma nor ON (rem.C_Norma_ID=nor.C_Norma_ID)";
		sql += " WHERE rem.C_Remesa_ID=" + m_C_Remesa_ID;
		//
		int cont=0;
		String aux="";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next())
			{
				StringBuffer sb = new StringBuffer("");
				sb.append(rs.getString(1));//Código Registro 2 pos
				sb.append(rs.getString(2));//Código Dato 2 pos
				aux=rs.getString(3);
				cont=rs.getString(3).length();
				if(aux.contains("-")){
					aux=aux.replace("-", "");
				}

				aux=aux+rs.getString(4);
				if(aux.contains(" ")){
					aux=aux.replace(" ", "");
				}
				cont=aux.length();
				if(cont<12){
					while(aux.length()<12){
						aux="0"+aux;
					}
				}
				sb.append(aux);//NIF+Sufijo=Codigo presentador de 12 pos ajustado a la derecha

				sb.append("            ");//Libre de 12 posiciones
				sb.append("   ");//Libre de 3 posiciones
				cont=String.valueOf(rs.getBigDecimal(5)).length();
				aux=String.valueOf(rs.getBigDecimal(5));
				if(aux.contains(".")){
					aux=aux.replace(".", "");
				}
				if(cont<12){
					while(aux.length()<12){
						aux="0"+aux;
					}
				}
				
				log.fine("El importe despueesssssssssss="+aux);
				sb.append(aux);//Suma Importes ordenante 12 posiciones
				
				//sb.append(numLineasPagos);//Número de domiciliaciones ordenante
				log.fine("NumRegistrossssss="+numRegistros);
				cont=String.valueOf(numRegistros++).length();
				log.fine("NumRegistrossssss="+numRegistros);
				aux=String.valueOf(numRegistros);
				if(cont<10){
					while(aux.length()<10){
						aux="0"+aux;
					}
				}
				sb.append(aux);//Número de registros
				log.fine("El numero de registros despues es="+aux);
				//sb.append(numRegistros++);//Número de registros
				sb.append("                                          ");//Libre de 42 pos
				sb.append("     ");//Libre de 5 pos
				guardarLinea(sb.toString());
			}
			
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - resumenOrdenante", e);
		}
	}

	
	/**
	* Escribe una linea y salta a la siguiente
	* 
	* @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	* @param linea La linea a escribir
	*/
	private void guardarLinea(String linea)
	{
		try
		{
			if (linea != null)
				if (linea.length() > 0)
				{
					writer.write(linea);
					writer.write(Env.NL);
				}
		}
		catch (Exception e)
		{
			log.saveError("C_RemesaGenerate - guardarLinea", e);
		}
	}	
	
	/**
	* Escribe una linea y salta a la siguiente
	* 
	* @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	*/
	private int comprueba_Remesa(){
		
		String sql_comprueba="SELECT c_remesaline_id from c_remesaline where c_remesa_id="+m_C_Remesa_ID;
	
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql_comprueba);
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next())
			{
				JOptionPane.showMessageDialog( null,"La remesa ya ha sido generada, imposible modificar","Remesa ya generada", JOptionPane.ERROR_MESSAGE );
				return 0;
			}
			
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - comprueba_remesa", e);
		}
		return 1;
	}
	
	private void asignarRemesa()
	{
		
		String sql= "Select c_invoicepayschedule_id,c_bpartner_id,dueamt from c_invoicepayschedule,c_invoice where c_invoicepayschedule.c_invoice_id=c_invoice.c_invoice_id and c_remesa_id="+m_C_Remesa_ID;
		
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			remesa.setC_Remesa_ID(m_C_Remesa_ID);
			while (rs.next())
			{
				MRemesaLine linearemesa =new MRemesaLine(remesa);
				linearemesa.setC_BPartner_ID(rs.getInt(2));
				linearemesa.setLineNetAmt(rs.getBigDecimal(3));
				linearemesa.save();	
			}
			
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - resumenEmisor", e);
		}
		
		/*String sql = "UPDATE C_InvoicePaySchedule "
			+ " SET C_Remesa_ID=" + m_C_Remesa_ID
			//+ " FROM C_InvoicePaySchedule ips"
			+ " WHERE C_Invoice_ID IN (SELECT civ.C_Invoice_ID FROM C_Invoice civ"
										+ " WHERE civ.C_BPartner_ID IN (SELECT rml.C_BPartner_ID" 
																		+ " FROM C_RemesaLine rml" 
																		+ " WHERE rml.C_Remesa_ID=" + m_C_Remesa_ID 
																		+ ")"
										+ ")"
			//+ " AND IsSelected='Y'"
			+ " AND Processed='N'"
			+ " AND DueDate<(SELECT rem.ExecuteDate FROM C_Remesa rem WHERE rem.C_Remesa_ID=" + m_C_Remesa_ID + ")";
		int no = DB.executeUpdate(sql);
		log.info("En el update de c_invoicePayschedule, actualizo no="+no+" ,y la remesa_id q le pasa es="+m_C_Remesa_ID);
		log.fine("La sentencia que acaba de ejecutar en C_RemesaGenerate es:"+sql);*/
	}
}	//	ExportData
