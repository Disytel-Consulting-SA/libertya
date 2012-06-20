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
import java.util.logging.Level;

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
public class C_RemesaGenerate extends SvrProcess
{
	/**
	 * 	ExportData Constructor
	 */
	public C_RemesaGenerate()
	{
		super();
		log.fine("C_RemesaGenerate");
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
	protected void prepare()
	{
		m_ctx = getCtx();
		m_WindowNo = Env.createWindowNo(null); //�null?
		
		m_C_Remesa_ID = getRecord_ID();
		if(m_C_Remesa_ID==0){
			m_C_Remesa_ID=remesa_id;
			log.fine("Llego aqui y m_C_Remesa_ID="+m_C_Remesa_ID);
		}
		String sql="SELECT name,subnorma from C_Norma where c_norma_id=(SELECT c_norma_id from C_Remesa where c_remesa_id="+m_C_Remesa_ID+")";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				norma= rs.getString(1);
				subnorma = rs.getString(2);
				log.fine("En la eleccion de norma y subnorma, norma="+norma+", y subnorma="+subnorma);
			}
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - Prepare", e);
		}
		log.fine("En prepare de C_RemesaGenerate");
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

	}	//	prepare


	/**
	 *  Perrform process.
	 *  @return Message
	 *  @throws Exception
	 */
	protected String doIt() throws java.lang.Exception
	{
		log.fine("En doIt La norma que saca esssss="+Env.getContext(m_ctx, m_WindowNo, "C_Norma_ID"));
		log.fine("En doIt La norma de otra manera essss="+Env.getContext(Env.getCtx(), m_WindowNo, "C_Norma_ID"));

		try { 
			outFile.createNewFile();
		} catch (IOException e) {
			log.saveError("C_RemesaGenerate - can not create file", e);
		}		
		try
		{
			FileWriter fwout = new FileWriter (outFile, false);
			writer = new BufferedWriter(fwout);
		}
		catch (FileNotFoundException fnfe)
		{
			log.fine("C_RemesaGenerate - " + fnfe.toString());
		}
		catch (IOException e)
		{
		}
		if(comprueba_Remesa()==0){
			return "Error";
		}
		asignarRemesa();
		cabeceraPresentador();
		
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
	* Escribe la primera linea de la remesa
	* 
	* @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	*/
	private void cabeceraPresentador()
	{
		log.fine("En la cabecera presentador");
		String sql = "SELECT nor.N_Cabecera_Presentador, nor.N_Euros, oin.DUNS, nor.Cod_Presentador, org.Name, bc.sucursal, bc.oficina, ban.RoutingNo";
		sql += " FROM C_Remesa rem";
		sql += " INNER JOIN C_Norma nor ON (rem.C_Norma_ID=nor.C_Norma_ID)";
		sql += " INNER JOIN AD_Org org ON (rem.AD_Org_ID=org.AD_Org_ID)";
		sql += " INNER JOIN AD_OrgInfo oin ON (rem.AD_Org_ID=oin.AD_Org_ID)";
		sql += " INNER JOIN C_Bank ban ON (rem.AD_Org_ID=ban.AD_Org_ID AND ban.IsOwnBank='Y')";
		sql += " INNER JOIN C_BankAccount bc ON (bc.c_bank_id=ban.c_bank_id) ";
		sql += " WHERE rem.C_Remesa_ID=" + m_C_Remesa_ID;
		//
		int cont=0;
		String aux="";
		PreparedStatement pstmt = null;
		try
		{
			pstmt = DB.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
			{
				numRegistros++;

				StringBuffer sb = new StringBuffer("");
				
				sb.append(rs.getString(1));//Codigo registro 2pos
				sb.append(rs.getString(2));//Codigo Dato 2 pos
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
				c = Calendar.getInstance();
				c.setTime(Env.getContextAsDate(m_ctx, "#Date"));
				if(c.get(Calendar.DAY_OF_MONTH)<10){			//Dia con 2 digitos
					sb.append("0"+c.get(Calendar.DAY_OF_MONTH));
				}else{					
					sb.append(c.get(Calendar.DAY_OF_MONTH));
				}
				int mes=c.get(Calendar.MONTH)+1;
				if(c.get(Calendar.MONTH)+1<10){
					//Mes con 2 digitos
					sb.append("0"+mes);
				}else{
					sb.append(mes);
				}
				
				sb.append(String.valueOf(c.get(Calendar.YEAR)).substring(2,4));//Año 2 digitos
				sb.append("      ");//Libre de 6 posiciones
				cont=rs.getString(5).length();
				aux=rs.getString(5);
				if(cont<40){		
					while(aux.length()<40){
						aux=aux.concat(" ");
					}
				}else if(cont>40){
					log.fine("Error, la cadena es mayor de 40");
					return;
				} 
				log.fine("La longitud de aux es="+aux.length());
				sb.append(aux);//Nombre con 40 posiciones
				sb.append("                    ");//Libre de 20 posiciones
				sb.append(rs.getString(7)); //Oficina--->Entidad Receptora
				sb.append(rs.getString(6)); //Sucursal--->Oficina
				sb.append("            ");	//Libre de 12 posiciones		
				sb.append("                                        ");//Libre de 40 posiciones
				sb.append("              ");//Libre de 14 posiciones
				guardarLinea(sb.toString());
				cabeceraOrdenante();
			}
			resumenEmisor();
						
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - cabeceraEmisor", e);
		}
	}
	
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
		sql += " org.Name, bac.AccountNo, nor.Adeudo, rem.C_Remesa_ID, nor.subnorma, bac.oficina, bac.sucursal, bac.dc, bac.cc, oin.cod_ine, nor.domiciliado FROM C_Remesa rem";
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
				
				/*sb.append(c.get(Calendar.DAY_OF_MONTH));
				sb.append((c.get(Calendar.MONTH) + 1));
				sb.append(c.get(Calendar.YEAR));   */
				c.setTime(rs.getDate(5));//Fecha Cargo
				if(c.get(Calendar.DAY_OF_MONTH)<10){			//Dia con 2 digitos
					sb.append("0"+c.get(Calendar.DAY_OF_MONTH));
				}else{					
					sb.append(c.get(Calendar.DAY_OF_MONTH));
				}
				mes=c.get(Calendar.MONTH)+1;
				if(c.get(Calendar.MONTH)+1<10){					//Mes con 2 digitos
					sb.append("0"+mes);
				}else{
					sb.append(mes);
				}
				sb.append(String.valueOf(c.get(Calendar.YEAR)).substring(2,4));//Año 2 digitos
				/*sb.append(c.get(Calendar.DAY_OF_MONTH));
				sb.append((c.get(Calendar.MONTH) + 1));
				sb.append(c.get(Calendar.YEAR));	*/
				cont=rs.getString(6).length();
				aux=rs.getString(6);
				if(cont<40){		
					while(aux.length()<40){
						aux=aux.concat(" ");
					}
					log.fine("la cadena que devuelve es:"+aux+", y tamaño="+aux.length());
				}else if(cont>40){
					log.fine("Error, la cadena es mayor de 40");
					return;
				} 
				sb.append(aux);//Nombre Cliente Ordenante
				sb.append(rs.getString(7));//Numero de cuenta
				//sb.append(rs.getString(11)+rs.getString(12)+rs.getString(13)+rs.getString(14));//Numero de cuenta
				sb.append("        "); //Libre 8 posiciones
				sb.append(rs.getString(10));//Procedimiento
				sb.append("          ");//Libre de 10 posiciones
				sb.append("                                        ");//Libre de 40 posiciones
				if(norma.contains("58")){
					sb.append("  ");//Libre 2 pos
					sb.append(rs.getString(15));//Codigo INE
					sb.append("   ");//Libre 3 pos
				}
				if(norma.contains("19")){
					sb.append("              ");//Libre de 14 posiciones
				}
				guardarLinea(sb.toString());
				
				int rm = rs.getInt(9);
				log.fine("En cabeceraOrdenante, para las lineas de pagos:"+rm);
				
				lineasPagos(rm);
				log.fine("la norma antes de entrar="+norma+", y rs.getString(16)="+rs.getString(16));
				if(norma.contains("58")&& rs.getString(16).equalsIgnoreCase("N")){
					log.fine("entro en norma58 para ir a no_domiciliados");
					no_domiciliados();
				}
				list.add(new MRemesa(m_ctx, rm,null));
			}
			
			MRemesa[] rem = new MRemesa[list.size()];
			list.toArray(rem);
			for (int i = 0; i < rem.length; i++)
				resumenOrdenante(rem[i]);
			
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
	private void lineasPagos(int ID)
	{
		log.fine("En C_RemesaGenerate, en la funcion lineasPagos, con ID="+ID);
		String sql = "SELECT DISTINCT nor.N_Reg_Individual, nor.N_Euros, oin.DUNS, nor.Cod_Ordenante, bp.Name,";
		sql += " bak.RoutingNo, ips.DueAmt, nor.Description, bp.c_bpartner_id, ips.duedate FROM C_InvoicePaySchedule ips";
		sql += " INNER JOIN C_Invoice inv ON (ips.C_Invoice_ID=inv.C_Invoice_ID)";
		sql += " INNER JOIN C_BPartner bp ON (inv.C_BPartner_ID=bp.C_BPartner_ID)";
		sql += " INNER JOIN C_RemesaLine rli ON (inv.C_BPartner_ID=rli.C_BPartner_ID)";
		sql += " INNER JOIN C_Remesa rem ON (rli.C_Remesa_ID=rem.C_Remesa_ID)";
		sql += " INNER JOIN AD_Org org ON (rem.AD_Org_ID=org.AD_Org_ID)";
		sql += " INNER JOIN AD_OrgInfo oin ON (rem.AD_Org_ID=oin.AD_Org_ID)";
		sql += " INNER JOIN C_Norma nor ON (rem.C_Norma_ID=nor.C_Norma_ID)";
		sql += " INNER JOIN C_BP_BankAccount bak ON (inv.C_BPartner_ID=bak.C_BPartner_ID)";//Será C_BankAccount en lugar de C_BP_BankAccount?
		//sql += " WHERE ips.IsSelected='Y' AND rem.C_Remesa_ID=" + ID;
		sql += " WHERE rem.C_Remesa_ID=" + ID + " AND ips.C_Remesa_ID="+ ID;
		//sql += " AND ips.DueDate<rem.ExecuteDate ";
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
				numLineasPagos++;
				numRegistros++;

				StringBuffer sb = new StringBuffer("");
				sb.append(rs.getString(1));//Código Reg. 2 pos
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

				//sb.append(numLineasPagos);
				cont=String.valueOf(rs.getInt(9)).length();
				aux=String.valueOf(rs.getInt(9));
				if(cont<12){
					while(aux.length()<12){
						aux=aux.concat("0");
					}
				}
				sb.append(aux);//Codigo referencia 12 pos
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
				sb.append(rs.getString(6));//Numero Cuenta
				cont=String.valueOf(rs.getBigDecimal(7)).length();
				aux=String.valueOf(rs.getBigDecimal(7));
				if(norma.contains("58")){//Si la norma es la 58, quitamos los decimales
					aux=aux.substring(0,aux.indexOf("."));
					log.fine("ahora aux= "+aux);
					cont=aux.length();
				}
				if(cont<10){
					while(aux.length()<10){
						aux="0"+aux;
					}
				}
				
				sb.append(aux);//Importe
				sb.append("                ");//Libre  16 posiciones
				if(rs.getString(8)==null){ //Compruebo que la descripción no es nula
					cont=1;
					aux=" ";
					log.fine("Entro aqui y aux="+aux+", cont="+cont);
				}else{
					cont=rs.getString(8).length();
					aux=rs.getString(8);
					log.fine("Entro aqui2 y aux="+aux+", cont="+cont);
				}
				if(cont<40){		
					while(aux.length()<40){
						aux=aux.concat(" ");
					}
					log.fine("la cadena que devuelve en descripcion es:"+aux+", y tamaño="+aux.length());
				}else if(cont>40){
					log.fine("Error, la cadena es mayor de 40");
					return;
				} 
				sb.append(aux);//Primer campo de concepto=Descripcion¿?
				if(norma.contains("19")){
					sb.append("        ");//Libre 8 posiciones
				}
				if(norma.contains("58")){
					c.setTime(rs.getDate(10));//Fecha Cargo
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
					sb.append("  ");//Libre 2 posicioens
				}
				guardarLinea(sb.toString());
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
	private void no_domiciliados()
	{
		String sql = "SELECT nor.N_Cabecera_Ordenante, nor.N_Euros, oin.DUNS, nor.Cod_Ordenante, rem.ExecuteDate,";
		sql += " org.Name, bac.AccountNo, nor.Adeudo, rem.C_Remesa_ID, nor.domiciliado, nor.domicilio_deudor, nor.plaza_deudor, nor.postal_code,loc.city  FROM C_Remesa rem";
		sql += " INNER JOIN C_Norma nor ON (rem.C_Norma_ID=nor.C_Norma_ID)";
		sql += " INNER JOIN AD_Org org ON (rem.AD_Org_ID=org.AD_Org_ID)";
		sql += " INNER JOIN AD_OrgInfo oin ON (rem.AD_Org_ID=oin.AD_Org_ID)";
		sql += " INNER JOIN C_Bank ban ON (rem.AD_Org_ID=ban.AD_Org_ID AND ban.IsOwnBank='Y')";
		sql += " INNER JOIN C_BankAccount bac ON (ban.C_Bank_ID=bac.C_Bank_ID)";
		sql += " INNER JOIN C_Location loc ON (loc.c_location_id=oin.c_location_id)";
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
				
				numRegistros++;

				StringBuffer sb = new StringBuffer("");
				sb.append("56");//Código Reg. 2 pos
				sb.append("76");//Código Dato 2 pos
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
				cont=rs.getString(11).length();
				aux=rs.getString(11);
				if(cont<40){
					while(aux.length()<40){
						aux=aux.concat(" ");
					}
				}
				sb.append(aux);//Domicilio del deudor 40 pos
				cont=rs.getString(12).length();
				aux=rs.getString(12);
				if(cont<35){
					while(aux.length()<35){
						aux=aux.concat(" ");
					}
				}
				sb.append(aux);//Plaza del domicilio del deudor 35 pos
				sb.append(rs.getString(13));//Codigo postal deudor 5 pos
				cont=rs.getString(14).length();
				aux=rs.getString(14);
				if(cont<38){
					while(aux.length()<38){
						aux=aux.concat(" ");
					}
				}
				sb.append(aux);//Localidad del ordenante
				sb.append(rs.getString(13).substring(0, 2));//Codigo localidad
				c.setTime(rs.getDate(5));//Fecha Cargo
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
				sb.append("        ");//Libre de 8 posiciones
				guardarLinea(sb.toString());
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
	/**
	* Escribe las lineas de resumen del segundo nivel de datos de la remesa
	* 
	* @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	*/
	private void resumenOrdenante(MRemesa rem)
	{

		String sql = "SELECT nor.N_Total_Ordenante, nor.N_Euros, oin.DUNS, nor.Cod_Ordenante, rem.TotalAmt";
		sql += " FROM C_Remesa rem";
		sql += " INNER JOIN AD_Org org ON (rem.AD_Org_ID=org.AD_Org_ID)";
		sql += " INNER JOIN AD_OrgInfo oin ON (rem.AD_Org_ID=oin.AD_Org_ID)";
		sql += " INNER JOIN C_Norma nor ON (rem.C_Norma_ID=nor.C_Norma_ID)";
		sql += " WHERE rem.C_Remesa_ID=" + rem.getC_Remesa_ID();
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
				sb.append("                                        ");//Libre de 40 posiciones
				sb.append("                    ");//Libre de 20 posiciones
				cont=String.valueOf(rs.getBigDecimal(5)).length();
				aux=String.valueOf(rs.getBigDecimal(5));
				if(norma.contains("58")){//Si la norma es la 58 quitamos los decimales
					aux=aux.substring(0,aux.indexOf("."));
					log.fine("ahora aux= "+aux);
					cont=aux.length();
				}
				if(cont<10){
					while(aux.length()<10){
						aux="0"+aux;
					}
				}
				
				log.fine("El importe despueesssssssssss="+aux);
				sb.append(aux);//Suma Importes ordenante 10 posiciones
				//sb.append(rs.getBigDecimal(5)); //Suma Importes ordenante 10 posiciones
				sb.append("      ");//Libre de 6 posiciones
				cont=String.valueOf(numLineasPagos).length();
				aux=String.valueOf(numLineasPagos);
				if(cont<10){
					while(aux.length()<10){
						aux="0"+aux;
					}
				}
				sb.append(aux);//Número de domiciliaciones ordenante
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
				sb.append("                    ");//Libre de 20 posiciones
				sb.append("                  ");//Libre de 18 posiciones
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
	* Escribe la linea de resumen de total de la remesa
	* 
	* @author Comunidad de Desarrollo OpenXpertya 
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         *Copyright � ConSerTi
	*/
	private void resumenEmisor()
	{
		StringBuffer sb = new StringBuffer("");

		String sql = "SELECT nor.N_Total_General, nor.N_Euros, oin.DUNS, nor.Cod_Presentador, rem.TotalAmt";
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
				numRegistros++;
				
				sb.append(rs.getString(1));//Cod Reg 2 pos
				sb.append(rs.getString(2));//Cod Dato 2 pos
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
				sb.append("                                        ");//Libre de 40 posiciones
				sb.append("0001");//Número de ordenantes aqui, no seria siempre 1???¿¿¿??
				sb.append("                ");//Libre de 16 posiciones
				cont=String.valueOf(rs.getBigDecimal(5)).length();
				aux=String.valueOf(rs.getBigDecimal(5));
				if(norma.contains("58")){//Si la norma es la 58, quitamos los decimales
					aux=aux.substring(0,aux.indexOf("."));
					log.fine("ahora aux= "+aux);
					cont=aux.length();
				}
				if(cont<10){
					while(aux.length()<10){
						aux="0"+aux;
					}
				}
				sb.append(aux);//Total de importes 10 pos
				//sb.append(rs.getBigDecimal(5));
				sb.append("      ");//Libre de 6 posiciones
				cont=String.valueOf(numLineasPagos).length();
				aux=String.valueOf(numLineasPagos);
				if(cont<10){
					while(aux.length()<10){
						aux="0"+aux;
					}
				}
				sb.append(aux);//Numero total de domiciliaciones 10 pos
				//sb.append(numLineasPagos);
				cont=String.valueOf(numRegistros).length();
				aux=String.valueOf(numRegistros);
				if(cont<10){
					while(aux.length()<10){
						aux="0"+aux;
					}
				}
				sb.append(aux);//Numero total de registros del soporte 10 pos
				//sb.append(numRegistros); 
				sb.append("                    ");//Libre de 20 posiciones
				sb.append("                  ");//Libre de 18 posiciones
				guardarLinea(sb.toString());
			}
			
			rs.close();
			pstmt.close();
			pstmt = null;
		}
		catch (Exception e)
		{ 
			log.saveError("C_RemesaGenerate - resumenEmisor", e);
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
