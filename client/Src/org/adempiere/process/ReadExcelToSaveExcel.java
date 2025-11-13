package org.adempiere.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import jxl.BooleanCell;
import jxl.Cell;
import jxl.CellFormat;
import jxl.CellType;
import jxl.CellView;
import jxl.DateCell;
import jxl.LabelCell;
import jxl.NumberCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.DateFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import org.adempiere.utils.Miscfunc;
import org.adempiere.ws.ClientWS;
// import org.adempiere.ws.ClientWS;
import org.openXpertya.apps.ADialog;
import org.openXpertya.process.ProcessInfoParameter;
import org.compiere.swing.CDialog;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Ini;

import com.google.gson.JsonObject;

// import com.google.gson.JsonObject;

// dREHER, 11/02/2015
// Lee una planilla de excell, interpreta celdas con contenido @SQL= y llena su resultado
// Guarda la nueva planilla con la info interpretada
//
// @SQLNumber=
// @SQLTimestamp=
// @SQL=
// @SQLInteger=

public class ReadExcelToSaveExcel
	extends AbstractExcelRPT 
{
	public static final String RESULTADO_OK = "@ProcessOK@";
		
	private String p_path    = "";
	private boolean isFromServer = false;
	private String nameFileServer = "";
	private ProcessInfoParameter[] para = null;
	
	// Formatos de celda
	private WritableCellFormat formatTahoma8Underline;
	private WritableCellFormat formatTahoma8Bold;
	private WritableCellFormat formatTahoma9;
	private WritableCellFormat formatTahoma9Right;
	private WritableCellFormat cellCurrencyFormat;
	private WritableCellFormat cellDateFormat;
	private WritableCellFormat totalCellFormat;
	private WritableCellFormat cellNumberFormat;
	private WritableCellFormat cellNumberTotalFormat;
	private WritableCellFormat cellSeparador;

	private String fileNameFinal = "";
	
	private String xurl = Miscfunc.ValueFromSystem("URL_WS_ReadExcelToSaveExcel", "http://jorge-HP-ENVY-17-Notebook-PC:8090/adempiereREST/rest/serviciosventa/", true);
	
	protected void prepare() 
	{
		para = getParameter();
		debug("Parametros leidos en ReadExcelToSaveExcel=" + para);
		
		for (int i = 0; i < para.length; i++)
		{
			debug("Parametro leido " + i + " en ReadExcelToSaveExcel=" + para[i]);
			
			String name = para[i].getParameterName();
			
			if (para[i].getParameter() == null)
				;			
			else if (name.equalsIgnoreCase("Path"))
				p_path = ((String) para[i].getParameter()).toString();
			else if (name.equalsIgnoreCase("IsFromServer"))
				isFromServer = ((String) para[i].getParameter()).toString().equals("Y");
			else
				log.log(Level.WARNING, "Parametro Desconocido: " + name);
		}
	}

	protected String doIt() 
		throws Exception 
	{
		String ret = "Verifique la carpeta de reportes! " + RESULTADO_OK;

		// dREHER, chequeo si el entorno dice que esta en un cliente web
		String versionCliente = "";
		if(!isFromServer){
			
			try {
				if (Ini.isClient())
					versionCliente = "ClienteLY";
				else
					versionCliente = "WEBUI";
			} catch (Exception e) {
				versionCliente = "WEBUI";
			}

			// versionCliente = Env.getContext(Env.getCtx(), "#VersionCliente");
			if(versionCliente.equals("WEBUI")){
				isFromServer = true;
			}
		}

		debug("Path de la plantilla >" + String.valueOf(p_path) + "< Version Cliente=" + versionCliente + " from Server:" + isFromServer);


		if(p_path == null || p_path=="")
			return (isFromServer?"#ERROR#":"Error, no es valido el nombre de la plantilla!");
		
		boolean ok = ValidaArchivo(p_path);
		if(ok){
			
			// if(ProcesaArchivo(p_path)){ // Otro metodo

			if(CopyArchivo(p_path)){

				if(!isFromServer){
					
					debug("Se genero el archivo en forma correcta, nombre=" + fileNameFinal + " nombre desde server=" + nameFileServer);

					if(ADialog.ask(0, null, "Desea abrir la planilla resultante ?")){

						File tempFile = new File(fileNameFinal);

						try 
						{
							if (isWindows())
							{
								//	Runtime.getRuntime().exec ("rundll32 url.dll,FileProtocolHandler " + url);
								Process p = Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL \"" + tempFile + "\"");
							}
							else if (isMac())
							{
								String [] cmdArray = new String [] {"open", tempFile.getAbsolutePath()};
								Process p = Runtime.getRuntime ().exec (cmdArray);
							}
							else	//	other OS
							{
								System.out.println("Sistema Operativo no soportado, experimental!\n" + tempFile.getAbsolutePath());
								String[] command = {"gnome-open", tempFile.getAbsolutePath()};
								if(getLinuxDesktop().equals("kde"))
									command = new String[]{"kfmclient", "exec", tempFile.getAbsolutePath()};
								else
									command = new String[]{"gnome-open", tempFile.getAbsolutePath()};

								Process p = Runtime.getRuntime().exec(command);
								int returnCode = p.waitFor();
								System.out.println("Resultado exec=" + returnCode);
							}
						} 
						catch (Exception ex) 
						{
							System.out.println(ex);
						}

					}

				}else
					debug("Volvio desde el servidor, no abre planilla. Nombre desde server=" + nameFileServer);
				
				if(isFromServer)
					ret = "XLS_" + nameFileServer;

			}else
				ret = (isFromServer?"#ERROR#":"Error, no se termino de importar el archivo correctamente!");

		}else	
			ret = (isFromServer?"#ERROR#":"Se produjo un error al validador archivo. Archivo NO VALIDO!");
		
		
		return ret;

	}
	
	private boolean CopyArchivo(String fileName) throws IOException, BiffException {

		boolean ok = true;

		File file = new File(fileName);
		String toFileName = System.getProperty("user.home") + File.separator + "reportes" + File.separator +file.getName().substring(0, file.getName().indexOf(".")) + "_Processed_" + Miscfunc.HoyAMD() + ".xls";

		// Si viene desde el servidor debo cambiar la ruta a la temporal y ademas que no sea el mismo nombre
		if(isFromServer){
			toFileName = System.getProperty("java.io.tmpdir") + File.separator + file.getName().substring(0, file.getName().indexOf(".")) + "_Processed_" + Miscfunc.HoyAMD() + ".xls";
			//
			// Sino no sabe como calcular mismo nombre desde el servidor String.valueOf((new Timestamp(new Date().getTime())).getTime()) + ".xls";
			// toFileName = System.getProperty("java.io.tmpdir") + File.separator + file.getName().substring(0, file.getName().indexOf(".")) + "_Processed_" + String.valueOf((new Timestamp(new Date().getTime())).getTime()) + ".xls";
			nameFileServer = toFileName;
		}
		
		fileNameFinal = toFileName;
		
		debug("Comienza interpretacion del archivo=" + fileName + " nombre final:" + toFileName);

		try {

			crearFormatosCelda();

			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(0);

			WritableWorkbook copy = Workbook.createWorkbook(new File(toFileName), workbook);
			// WritableSheet sheetCopy = copy.getSheet(0);
			
			Sheet[] sheets = workbook.getSheets();
			for(int sh=0; sh<sheets.length; sh++){
				
				WritableSheet sheetCopy = copy.getSheet(sh);

				int filas = sheetCopy.getRows();
				int columnas = sheetCopy.getColumns();

				for(int row=0; row<filas; row++) {

					for(int column=0; column<columnas; column++){

						try{

							Cell cell = sheetCopy.getCell(column, row);
							jxl.format.CellFormat format = null;
							try{
								if(cell!=null)
									format = cell.getCellFormat();
							}catch(Exception exF){
								format = null;
							}
							CellType type = cell.getType();
							
							log.fine("Se detecto fila=" + row + " columna="+column + " del tipo=" + type);

							if(format==null)
								format = createFormat(type);
							
							boolean tablefila = false;
							int op = 0;
							
							// Si es label, verificar si no es una sentencia SQL
							if(type.equals(CellType.LABEL)){ // 

								LabelCell lc = (LabelCell)cell;
								
								// Limpio objeto
								cell = null;
								
								String data = lc.getString();

								if(data.indexOf("@SQL=") > -1){
									String sql = data.substring(data.indexOf("@SQL=")+5);
									if(sql.indexOf("@") > -1 || sql.indexOf("$") > -1)
										sql = reeplaceParams(sql, sheetCopy);

									String value = DB.getSQLValueString(get_TrxName(), sql, new Object[]{});
									if(value==null)
										value="@SIN RESULTADO@";
									
									this.addLabel(sheetCopy, column, row, value, format);
									//log.fine("Agregue resultado de label sql=" + sql);

								}else if(data.indexOf("@SQLNUMBER=") > -1){
									String sql = data.substring(data.indexOf("@SQLNUMBER=")+11);
									if(sql.indexOf("@") > -1 || sql.indexOf("$") > -1)
										sql = reeplaceParams(sql, sheetCopy);

									BigDecimal value = getSQLValueBD(get_TrxName(), sql);
									if(value==null)
										value=Env.ZERO;

									this.addNumber(sheetCopy, column, row, value, format);
									//log.info("Agregue resultado de number sql=" + sql);

								}else if(data.indexOf("@SQLDATE=") > -1){
									String sql = data.substring(data.indexOf("@SQLDATE=")+9);
									if(sql.indexOf("@") > -1 || sql.indexOf("$") > -1)
										sql = reeplaceParams(sql, sheetCopy);

									Timestamp value = DB.getSQLValueTimestamp(get_TrxName(), sql);
									if(value!=null)
										this.addDateTime(sheetCopy, column, row, value, format);

									//log.info("Agregue resultado de Datetime sql=" + sql);
								}else if(data.indexOf("@SQLINTEGER=") > -1){
									String sql = data.substring(data.indexOf("@SQLINTEGER=")+12);
									if(sql.indexOf("@") > -1 || sql.indexOf("$") > -1)
										sql = reeplaceParams(sql, sheetCopy);

									int value = DB.getSQLValue(get_TrxName(), sql);
									if(value < 0)
										value = 0;

									this.addNumber(sheetCopy, column, row, value, format);
									//log.info("Agregue resultado de integer sql=" + sql);

								}else if(data.indexOf("@SQLINT=") > -1){
									String sql = data.substring(data.indexOf("@SQLINT=")+8);
									if(sql.indexOf("@") > -1)
										sql = reeplaceParams(sql, sheetCopy);

									int value = DB.getSQLValue(get_TrxName(), sql);
									this.addNumber(sheetCopy, column, row, value, format);
									//log.info("Agregue resultado de integer sql=" + sql);

								}else if(data.indexOf("@SQLTABLE=") > -1)
								{
									tablefila = true;
									op = 1;
								}
								else if(data.indexOf("@SQLROWTABLE=") > -1)
								{
									tablefila = true;
									op = 2;
								}
								else if(data.indexOf("@SQLTABLETITLE=") > -1)
								{
									tablefila = true;
									op = 3;
								}
								
								if(tablefila && op > 0)
								{
									String sql = "";
									// StringBuffer buf = new StringBuffer();
									
									
									if(op == 1)
										sql = data.substring(data.indexOf("@SQLTABLE=")+10);
									else if(op == 2)
										sql = data.substring(data.indexOf("@SQLROWTABLE=")+13);
									else
										sql = data.substring(data.indexOf("@SQLTABLETITLE=")+15);
									
									if(sql.indexOf("@") > -1 || sql.indexOf("$") > -1)
										sql = reeplaceParams(sql, sheetCopy);
									
									
									if(sql.indexOf(";") > -1){
										
										String[]sqls = Miscfunc.SplitString(sql, ";");
										boolean isOK = true;
										
										if(sqls.length > 1){
										
											for(int i=0; i<sqls.length-1; i++){

												if(sqls[i] != null){
													String sqlx = sqls[i].trim();
													if(sqlx != ""){
														
														if(sqlx.toUpperCase().indexOf("SELECT") > -1 && sqlx.toUpperCase().indexOf("SETVAL") == -1){
															log.warning("Sentencias multiples con SELECT..." + sqlx);
															isOK = false;
															sql = sqlx;
															break;
															
														}else{

															log.fine("Va a Ejecutar sentencias intermedias sql=" + sqlx);
															int h = DB.getSQLValue(get_TrxName(), sqlx);
															log.fine("Sentencias intermedias result=" + h);

														}
													}

												}
											}

										}
										
										if(isOK)
											sql = sqls[sqls.length-1];
										
									}

									int index = sql.indexOf("#AGRUPAR_POR=");
									String agrupaPor = null;
									if( index > -1){
										agrupaPor = sql.substring(index).replace("#AGRUPAR_POR=", "");
										sql = sql.substring(0, index);
										agrupaPor = agrupaPor.toLowerCase().trim();
										log.info("Debe agrupar por la columna=" + agrupaPor);
										
									}
									
									
									debug("sql final=" + sql);

									PreparedStatement st = null;
									ResultSet rs = null;

									try{

										
																		
										if(op == 2) // SQLROWTABLE, dejar espacio en blanco y borrar el SQL ...
											this.addBlankCell(sheetCopy, column, row, format);
										
										// Limpiar objetos obsoletos...
										// System.gc();

										st = DB.prepareStatement(sql, null);
										//log.fine("sql: " + sql);
										rs = st.executeQuery();
										int fila = row;
										
										if(op == 2) // SQLROWTABLE deja un renglon de separacion, SQLTABLE no deja espacios entre titulo y datos
											fila = row + 1;
										
										ResultSetMetaData metaDatos = rs.getMetaData();
										
										int cantFields = metaDatos.getColumnCount();
										BigDecimal[] totales = new BigDecimal[cantFields];
										BigDecimal[] subtotales = new BigDecimal[cantFields];
										int indexAgrupa = -1;
										
										log.info("Inicializo vectores de totales... len=" + cantFields);
										
										Object valorAgrupa = null;
										int registros = 0;
										int subtotal = 0;
										
										int filaFormato = fila;
										
										//log.fine("Leyo metadata!");
										
										boolean isData = false;
										
										for(int i=1; i<=metaDatos.getColumnCount(); i++){
											log.info("Campo " + i + " name= " + metaDatos.getColumnName(i));
											if(agrupaPor!=null && agrupaPor.equalsIgnoreCase(metaDatos.getColumnName(i)))
												indexAgrupa = i;
										}

										while(rs.next()){
											
											// si tiene que agrupar y es la primera vez que lee un registro, inicializa campo de agrupacion
											if(agrupaPor!=null){
												
												boolean isCambioGrupo = false;
												Object valorAgrupacion = null;
												try{
													valorAgrupacion = rs.getObject(agrupaPor);
												}catch(Exception exc){
													log.warning("Error la leer columna de agrupacion= " + agrupaPor + "\n" + exc.toString());
													valorAgrupacion = rs.getObject(indexAgrupa);
												}
												
												if(valorAgrupa==null){
													valorAgrupa = valorAgrupacion;
												}else{
													
													// No es la primera vez que pasa, controlar si cambio el campo agrupador
													
													if(!valorAgrupa.equals(valorAgrupacion)){
														isCambioGrupo = true;
														log.info("cambio de grupo, agrega linea de subtotales...");
													}
												}
												
												
												if(isCambioGrupo){
													
													int columnaX = column;
													int heightInPoints = 26*20;
													sheetCopy.setRowView(fila, heightInPoints);
													this.addLabel(sheetCopy, 0, fila, "Items= " + subtotal, formatTahoma8Bold);
													
													for(int t=0; t<cantFields; t++){
														if(subtotales[t]!=null){
															this.addNumber(sheetCopy, columnaX, fila, subtotales[t], cellNumberTotalFormat);
														}
														columnaX++;
														
													}
													
													
													log.info("Debe inicializar subtotal de " + valorAgrupacion);
													valorAgrupa = valorAgrupacion;
													
													fila++;
													
													columnaX = column;
													for(int t=0; t<cantFields; t++){
														this.addBlankCell(sheetCopy, columnaX, fila, cellSeparador);
														columnaX++;
													}
													fila++;
													
													for(int t=0; t<cantFields; t++){
														if(subtotales[t]!=null){
															if(totales[t]==null)
																totales[t] = Env.ZERO;

															totales[t] = totales[t].add(subtotales[t]);
															subtotales[t] = Env.ZERO;
														}
													}
													subtotal = 0;
												}
												
											}

											isData = true;
											
											int cols = cantFields;
											int columnaX = column;
											
											
											int s = 0;
											for(int i=1; i<=cols; i++){

												//log.fine("Va a leer formato de celda col=" + (columnaX) + " fila=" + filaFormato);

												jxl.format.CellFormat formatt = format;
												try{
													Cell cellt = sheetCopy.getCell(columnaX, filaFormato);
													if(cellt != null)
														formatt = cellt.getCellFormat();
													if(formatt==null)
														formatt = format;

												}catch(Exception xx){
													//log.finest("Error al leer formato=" + xx.toString() + " fila=" + filaFormato + " col=" + columnaX);
													formatt = format;
												}
												//log.fine("Formato columna de la tabla=" + formatt);

												String tipoCol = metaDatos.getColumnTypeName(i).toUpperCase();
												// UNKNOWN es usado por ej si la columna fuera null, utlizada para dejar una columna con formulas y esas cosas
												if(tipoCol.equals("VARCHAR")){
													if(rs.getString(i)!=null){
														
														String dataTmp = rs.getString(i);
														
														if(dataTmp.indexOf("#WS=") > -1){
														
															dataTmp = ResuelveWS(dataTmp, fila, sheetCopy);
															
														}
														
														this.addLabel(sheetCopy, columnaX, fila, dataTmp, formatt);
													}
												}else if(tipoCol.equals("NUMERIC")){
													formatt = createFormat(CellType.NUMBER_FORMULA);
													this.addNumber(sheetCopy, columnaX, fila, rs.getBigDecimal(i), formatt);
													// debug log.info("s=" + s + " len=" + subtotales.length);
													if(subtotales[s]==null)
														subtotales[s] = Env.ZERO;
													if(rs.getBigDecimal(i)!=null)
														subtotales[s] = subtotales[s].add(rs.getBigDecimal(i));
													
												}else if(tipoCol.equals("BIGINT")){
													this.addNumber(sheetCopy, columnaX, fila, rs.getInt(i), formatt);
													
													if(subtotales[s]==null)
														subtotales[s] = Env.ZERO;
													subtotales[s] = subtotales[s].add(new BigDecimal(rs.getInt(i)));
													
												}else if(tipoCol.indexOf("INT") > -1){
													this.addNumber(sheetCopy, columnaX, fila, rs.getInt(i), formatt);
													
													if(subtotales[s]==null)
														subtotales[s] = Env.ZERO;
													subtotales[s] = subtotales[s].add(new BigDecimal(rs.getInt(i)));
													
												}else if(tipoCol.equals("CHAR")){
													if(rs.getString(i)!=null)
														this.addLabel(sheetCopy, columnaX, fila, rs.getString(i), formatt);
												}else if(tipoCol.indexOf("DATE") > -1){
													formatt = createFormat(CellType.DATE);
													this.addDateTime(sheetCopy, columnaX, fila, rs.getTimestamp(i), formatt);
												}else if(tipoCol.indexOf("TIMESTAMP") > -1){
													formatt = createFormat(CellType.DATE);
													this.addDateTime(sheetCopy, columnaX, fila, rs.getTimestamp(i), formatt);
												}else if(tipoCol.indexOf("BOOLEAN") > -1){
													this.addLabel(sheetCopy, columnaX, fila, (rs.getBoolean(i)?"Si":"No"), formatt);
												}else{
													if(rs.getString(i)!=null){
														String dataTmp = rs.getString(i);
														
														if(dataTmp.indexOf("#WS=") > -1){
														
															dataTmp = ResuelveWS(dataTmp, fila, sheetCopy);
															
														}
														this.addLabel(sheetCopy, columnaX, fila, dataTmp, formatt);
													}
												}
												//log.fine("Tipo de columna en resultSet=" + tipoCol);
												
												columnaX++;
												s++;

											}

											// Sumo una linea
											fila++;
											subtotal++;
											registros++;

											if(fila > sheetCopy.getRows()){
												log.warning("Se excedio el limite de filas permitidas!");
												break;
											}

										}
										
										if(!isData){
											this.addLabel(sheetCopy, column, fila, "#Vacio#", format);
										}else{
											
											
											if(agrupaPor!=null){
												
												log.info("Imprimo ultima linea de subtotales...");
												int columnaX = column;
												int heightInPoints = 26*20;
												sheetCopy.setRowView(fila, heightInPoints);
												this.addLabel(sheetCopy, 0, fila, "Items= " + subtotal, formatTahoma8Bold);
												
												for(int t=0; t<cantFields; t++){
													if(subtotales[t]!=null){
														this.addNumber(sheetCopy, columnaX, fila, subtotales[t], cellNumberTotalFormat);
														
														
														// Sumo la ultima linea de subtotales...
														totales[t] = totales[t].add(subtotales[t]);
														
													}
													columnaX++;
													
												}
												
												
												
												log.info("Calculo subtotales, debe imprimir linea de totales generales... registros=" + registros);
												fila++;
												columnaX = column;
												for(int t=0; t<cantFields; t++){
													this.addBlankCell(sheetCopy, columnaX, fila, cellSeparador);
													columnaX++;
												}
												
												fila++;
												
												sheetCopy.setRowView(fila, heightInPoints);
												this.addLabel(sheetCopy, 0, fila, "Total Items= " + registros, formatTahoma8Bold);
												columnaX = column;
												for(int t=0; t<cantFields; t++){
													
													if(totales[t]!=null){
														this.addNumber(sheetCopy, columnaX, fila, totales[t], cellNumberTotalFormat);
													}
													columnaX++;
													
												}
												
												
											}
											
											
										}

										//log.info("Agregue resultado de sqlTable sql=" + sql);

									}catch(Exception sqlex){

										log.warning("Error al ejecutar SQLTable. sql=\n" + sql + "\nError=" + sqlex.toString());

									}finally{

										DB.close(rs, st);
										rs= null; st = null;

									}


								}

							}

						}catch(Exception ex){
							log.warning("Error general=" + ex.toString());
							ok = false;
						}

					}

				}

			}

			// Escribo y cierro

			if (!copy.equals(null)){
				copy.write();
				copy.close();
			}

			if (!workbook.equals(null))
				workbook.close();

			// Limpio objetos
			copy = null;
			workbook = null;

			debug("Termino de crear el archivo destino!");

		} catch (Exception ex) {
			System.out.println("Error al prcesar archivo destino!");
			ex.printStackTrace();
			ok = false;
		} finally {
			;
		}

		debug("Termino de crear el archivo destino. Verifique la carpeta de reportes!");

		return ok;	

	}
	
	 /** El metodo de DB siempre exige un parametro, no es logico */
    public static BigDecimal getSQLValueBD( String trxName,String sql) {
        BigDecimal        retValue = null;
        PreparedStatement pstmt    = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName);
            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getBigDecimal( 1 );
            } else {
                System.out.println( "No Value " + sql );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            System.out.println(sql + " [" + trxName + "] " + e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }    // getSQLValueBD

	
	// Llama un servicio web y devuelve resultado
	// Formato: servicio,parametro,etiqueta,columna
	private String ResuelveWS(String dataTmp, int fila, WritableSheet sheetCopy) {
		String resultado = "";
		
		String tmp = dataTmp.replace("#WS=", "");
		String[] params = Miscfunc.SplitString(tmp, ",");
		
		if(params.length >= 3){

			try{

				String servicio = params[0];
				String parametro = params[1];
				String etiqueta = params[2];
				int columnaX = Integer.valueOf(params[3]);

				Cell cell = sheetCopy.getCell(columnaX, fila);
				String dato = cell.getContents();

				String name = "SuperUser";
				String password = "Maiden1969";

				ClientWS cws = new ClientWS(xurl + (xurl.endsWith("/")?"":"/") + servicio);
				cws.setUser(name);
				cws.setPass(password);
				cws.setMethod("POST");

				// con metodo POST
				String input = "{\"" + parametro + "\":\"" + dato + "\", \"ad_User_ID\":" + Env.getAD_User_ID(getCtx()) + "}";
				cws.setParameters(input);
				if(cws.ConnectAndExecute()){
					JsonObject jsonObj = cws.getResult();

					log.info("etiqueta: " + jsonObj.get(etiqueta));

					resultado = jsonObj.get(etiqueta).getAsString();

				}else{
					log.warning("Se produjo un error al ejecutar cliente de servicio web");
					resultado = cws.getErrorCode();
				}

			}catch(Exception ex){
				log.warning("Se produjo un error al leer servicio web. Error=" + ex.toString());
			}
			
		}else
			log.warning("Los parametros del servicio web estan incompletos. Formato=servicio,parametro,etiqueta,columna");

		return resultado;
	}

	private jxl.format.CellFormat createFormat(CellType type) {
		jxl.format.CellFormat format = null;

		if(type.equals(CellType.LABEL) || type.equals(CellType.STRING_FORMULA)){ 
			format = formatTahoma9;
		}else if(type.equals(CellType.NUMBER) || type.equals(CellType.NUMBER_FORMULA)){
			format = cellNumberFormat; // cellCurrencyFormat;
		}else if(type.equals(CellType.DATE)|| type.equals(CellType.DATE_FORMULA)){
			format = cellDateFormat;
		}else{
			format = formatTahoma9;
		}
		return format;
	}

	private String reeplaceParams(String sql, Sheet sheet){
		String query = sql;
		
		// Recorrer los parametros recibos e ir reemplazandolos en la query
		for(ProcessInfoParameter parametro: para){

			if(!parametro.getParameterName().equals("Path")){

				String paramName = "@" + parametro.getParameterName() + "@";
				Object o = parametro.getParameter();

				debug("Encontro parametro: " + parametro.getParameterName() + " del tipo " + o.getClass().getName() + " valor=" + o);

				if(o.getClass().getName().indexOf("String") > -1){
					String data = (String)o;
					log.fine("String=" + data);
					query = query.replaceAll(paramName, data);
				}else if(o.getClass().getName().indexOf("Integer") > -1){
					String data = String.valueOf((Integer)o);
					log.fine("Integer=" + data);
					query = query.replaceAll(paramName, data);
				}else if(o.getClass().getName().indexOf("BigDecimal") > -1){
					String data = String.valueOf((BigDecimal)o);
					log.fine("BigDecimal=" + data);
					query = query.replaceAll(paramName, data);
				}else if(o.getClass().getName().indexOf("Double") > -1){
					String data = String.valueOf((Double)o);
					log.fine("Double=" + data);
					query = query.replaceAll(paramName, data);
				}else if(o.getClass().getName().indexOf("Date") > -1){
					String data = Miscfunc.FechaAMD((Date)o);
					log.fine("Date=" + data);
					query = query.replaceAll(paramName, "'" + data + "'");
				}else if(o.getClass().getName().indexOf("Timestamp") > -1){
					String data = Miscfunc.FechaAMD(new Date(((Timestamp)o).getTime()));
					log.fine("Timestamp a fechasql=" + data);
					query = query.replaceAll(paramName, "'" + data + "'");
				}else if(o.getClass().getName().indexOf("Boolean") > -1)
					query = query.replaceAll(paramName, "'" + ((Boolean)o.equals(true)?"Y":"N") + "'");
				else{
					String data = (String)o;
					log.fine("String=" + data);
					query = query.replaceAll(paramName, "'" + data + "'");
				}
			}

		}
		
		// SI quedan mas variales, verifico si son del tipo @xxxx_ID@ y las reemplazo por cero
		if(query.indexOf("_ID@") > -1 ){
			log.config("1) Encontro variables del tipo _ID, reemplazar=" + query);
			query = Miscfunc.parseContext (query, "_ID", "0", true, true);
			log.config("2) Encontro variables del tipo _ID, reemplazar=" + query);
		}
		
		if(query.indexOf("@") > -1)
			query = Env.parseContext(getCtx(), -1, query, false, true);
		
		if(query.indexOf("$") > -1){
			
			query = Miscfunc.parseContextSheet(query, sheet, true);
			log.fine("query final=" + query);
		}
			
		debug("Query final=" + query);
		
		return query;
		
	}
	
	
	private void debug(String string) {
		System.out.println("--> ReadExcelToSaveExcel: " + string);
	}

	private boolean ProcesaPlanilla(String fileName) throws IOException, BiffException {

		boolean ok = true;
		
		File file = new File(fileName);
		String toFileName = System.getProperty("user.home") + File.separator + "reportes" + File.separator +file.getName().substring(0, file.getName().indexOf(".")) + "_Processed_" + Miscfunc.HoyAMD() + ".xls";

		log.warning("Comienza interpretacion del archivo=" + fileName);

		try {
			
			crearFormatosCelda();

			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(0);
			
			WritableWorkbook copy = Workbook.createWorkbook(new File(toFileName), workbook);
			WritableSheet sheetCopy = copy.getSheet(0);
			
			int filas = sheet.getRows();
			int columnas = sheet.getColumns();
			
			for(int row=0; row<filas; row++) {

				for(int column=0; column<columnas; column++){

					try{
						
						Cell cell = sheet.getCell(column, row);
						jxl.format.CellFormat format = null;
						if(cell!=null)
							format = cell.getCellFormat();
						
						CellType type = cell.getType();
						
						// Si es label, verificar si no es una sentencia SQL
						if(type.equals(CellType.LABEL)){
							
							LabelCell lc = (LabelCell)cell;
							String data = lc.getString();
							
							if(data.indexOf("@SQL=") > -1){
								String sql = data.substring(data.indexOf("@SQL=")+5);
								if(sql.indexOf("@") > -1)
									sql = Env.parseContext(getCtx(), -1, sql, false, true);
								
								String value = DB.getSQLValueString(get_TrxName(), sql, new Object[]{});
								if(value==null)
									value="@SIN RESULTADO@";
								
								this.addLabel(sheetCopy, column, row, value, format);
							}
							
							
						}
			
					}catch(NumberFormatException ex0){
						log.warning("Mal formado un numero=" + ex0.toString());
						ok = false;
					}catch(Exception ex){
						log.warning("Error general=" + ex.toString());
						ok = false;
					}

				}

			}
			
			// Escribo y cierro
			
			if (!copy.equals(null)){
				copy.write();
				copy.close();
			}

			if (!workbook.equals(null))
				workbook.close();
			

			System.out.println("Termino de crear el archivo destino!");
			
		} catch (Exception ex) {
			System.out.println("Error al prcesar archivo destino!");
			ex.printStackTrace();
			ok = false;
		} finally {
			;
		}

		System.out.println("Termino de crear el archivo destino!");

		return ok;
		
	
	}
	
	
	private boolean ProcesaArchivo(String fileName) throws IOException, BiffException {

		boolean ok = true;
		
		File file = new File(fileName);
		String toFileName = System.getProperty("user.home") + File.separator + "reportes" + File.separator +file.getName().substring(0, file.getName().indexOf(".")) + "_Processed_" + Miscfunc.HoyAMD() + ".xls";

		log.warning("Comienza interpretacion del archivo=" + fileName);

		try {
			
			crearFormatosCelda();

			Workbook workbook = Workbook.getWorkbook(new File(toFileName));
			Sheet sheet = workbook.getSheet(0);

			int filas = sheet.getRows();
			int columnas = sheet.getColumns();

			WritableWorkbook workbookTarget = this.crearWorkbook(toFileName);
			WritableSheet planilla = crearPlanilla(workbookTarget, sheet.getName());
			
			
			for(int row=0; row<filas; row++) {

				for(int column=0; column<columnas; column++){

					try{

						Cell cell = sheet.getCell(column, row);
						jxl.format.CellFormat format = cell.getCellFormat();
						CellType type = cell.getType();
						
						log.info("Celda row=" + row + " column=" + column + " contenido=" + cell.getContents() + " " +
								" type=" + type + " format=" + format);
						
						if(type.equals(CellType.DATE)){
							DateCell dc = (DateCell)cell;
							this.addDateTime(planilla, column, row, new Timestamp(dc.getDate().getTime()), cellDateFormat);
						}else if(type.equals(CellType.NUMBER)){
							NumberCell nc = (NumberCell)cell;
							this.addNumber(planilla, column, row, Miscfunc.Double2BigDecimal(nc.getValue()), formatTahoma9Right);
						}else if(type.equals(CellType.LABEL)){
							LabelCell lc = (LabelCell)cell;
							this.addLabel(planilla, column, row, lc.getString(), format);
						}else if(type.equals(CellType.STRING_FORMULA)){
							this.addFormula(planilla, column, row, cell.getContents(), format);
						}else if(type.equals(CellType.NUMBER_FORMULA))
							this.addFormula(planilla, column, row, cell.getContents(), formatTahoma9Right);
						else if(type.equals(CellType.BOOLEAN)){
							BooleanCell bc = (BooleanCell)cell;
							this.addLabel(planilla, column, row, (bc.getValue()==true?"Si":"No"), format);
						}else if(type.equals(CellType.EMPTY)){
							this.addBlankCell(planilla, column, row, format);
						}else
							this.addLabel(planilla, column, row, cell.getContents(), format);
						
						
					}catch(NumberFormatException ex0){
						log.warning("Mal formado un numero=" + ex0.toString());
						ok = false;
					}catch(Exception ex){
						log.warning("Error general=" + ex.toString());
						ok = false;
					}

				}

			}

			if (!workbook.equals(null))
				workbook.close();

			if (!workbookTarget.equals(null)){
				workbookTarget.write();
				workbookTarget.close();
			}
			
			
			System.out.println("Termino de crear el archivo destino!");
			
		} catch (Exception ex) {
			System.out.println("Error al prcesar archivo destino!");
			ex.printStackTrace();
			ok = false;
		} finally {
			;
		}

		System.out.println("Termino de crear el archivo destino!");

		return ok;
		
	
	}

	
	
	// TODO: agregar mas validaciones, totales por ej...
	private boolean ValidaArchivo(String archivo) throws BiffException, IOException{
		boolean ok = false;
		
		try {
			Workbook workbook = Workbook.getWorkbook(new java.io.File(archivo));
			Sheet sheet = workbook.getSheet(0);

			int fila = 0;
			Cell cell = sheet.getCell(0, fila);
			cell.getContents();
			ok = true;

			if (!workbook.equals(null))
				workbook.close();
			debug("Termino de crear el archivo temporal!");
		} catch (Exception ex) {
			System.out.println("Error al cerrar archivo temporal!");
			ex.printStackTrace();
		} finally {
			;
		}
		
		return ok;
		
	}
	
	private WritableWorkbook crearWorkbook(String fileName)
			throws IOException
	{
			return this.crearLibro(fileName);
	}
		
	private WritableSheet crearPlanilla(WritableWorkbook workbook, String tituloPlanilla)
	{
		return this.crearHoja(workbook, tituloPlanilla);
	}
	
	private void crearFormatosCelda() 
			throws WriteException 
	{
		WritableFont font8Bold = new WritableFont(WritableFont.TAHOMA, 8, WritableFont.BOLD, false);
		formatTahoma8Bold = new WritableCellFormat(font8Bold);

		WritableFont arial8ptUnderline = new WritableFont(WritableFont.TAHOMA, 8, WritableFont.NO_BOLD, false,
				UnderlineStyle.SINGLE);

		formatTahoma8Underline = new WritableCellFormat(arial8ptUnderline);
		formatTahoma8Underline.setBackground(Colour.GREY_25_PERCENT);
		formatTahoma8Underline.setBorder(Border.BOTTOM, BorderLineStyle.THIN);

		WritableFont font9 = new WritableFont(WritableFont.TAHOMA, 9);
		formatTahoma9 = new WritableCellFormat(font9);

		formatTahoma9Right = new WritableCellFormat(font9);
		formatTahoma9Right.setAlignment(Alignment.RIGHT);

		jxl.write.NumberFormat numfmt = new jxl.write.NumberFormat("$###,###,###.00");
		cellCurrencyFormat = new WritableCellFormat(numfmt);
		
		jxl.write.NumberFormat numfmt2 = new jxl.write.NumberFormat("###,###,###.00");
		cellNumberFormat = new WritableCellFormat(numfmt2);
		
		WritableFont font8ArialBold = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD);
		WritableFont font9ArialBold = new WritableFont(WritableFont.ARIAL, 9, WritableFont.BOLD);
		cellNumberTotalFormat = new WritableCellFormat(font9ArialBold, numfmt2);

		DateFormat customDateFormat = new DateFormat("dd-mm-yyyy");
		cellDateFormat = new WritableCellFormat(customDateFormat);
		totalCellFormat = new WritableCellFormat(font8ArialBold, numfmt);
		
		WritableCellFormat cellFormat = new WritableCellFormat();
		cellFormat.setBorder(Border.TOP, BorderLineStyle.THIN);
		cellSeparador = new WritableCellFormat(cellFormat);
	}
	
	private String getEnv(String envvar){
		try{
			Process p = Runtime.getRuntime().exec("/bin/sh echo $"+envvar);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String value = br.readLine();
			if(value==null) return "";
			else return value.trim ();
		}
		catch(Exception error){
			return "";
		}
	}
	
	private boolean isMac() 
   	{
   		String osName = System.getProperty ("os.name");
   		osName = osName.toLowerCase();
   		return osName.indexOf ("mac") != -1;
   	}	//	isMac
   	
   	/**
   	 * 	Do we run on Windows
   	 *	@return true if windows
   	 */
   	private boolean isWindows()
   	{
   		String osName = System.getProperty ("os.name");
   		osName = osName.toLowerCase();
   		return osName.indexOf ("windows") != -1;
   	}	//	isWindows

   	private String getLinuxDesktop(){
		//solo se averigua el entorno de escritorio una vez, despues se almacena en la variable estatica
		String linuxDesktop = "";
		
		if(!getEnv("KDE_FULL_SESSION").equals("") || !getEnv("KDE_MULTIHEAD").equals("")){
			linuxDesktop="kde";
		}
		else if(!getEnv("GNOME_DESKTOP_SESSION_ID").equals("") || !getEnv("GNOME_KEYRING_SOCKET").equals("")){
			linuxDesktop="gnome";
		}
		else linuxDesktop="";

		return linuxDesktop;
	}
	
}
