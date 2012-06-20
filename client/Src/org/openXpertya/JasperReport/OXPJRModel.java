package org.openXpertya.JasperReport;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JFrame;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;

import org.openXpertya.apps.EMailDialog;
import org.openXpertya.model.MUser;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;
import org.openXpertya.util.Util;

public class OXPJRModel {

	// Variables de instancia
	
	/** Interfase que maneja */
	
	private JRViewer view;
	
	/** Jasper print impreso */
	
	private JasperPrint print;
	
	/** Contexto */
	
	private Properties ctx;
	
	/** Información del proceso actual, o sea, del Jasper que está corriendo */
	
	private ProcessInfo pi;
	
	/** PO involucrado en el record id del Process Info */
	
	private PO po;
	
	
	
	// Constructores
	
	public OXPJRModel(Properties ctx, ProcessInfo pi, JRViewer view) {
		setPi(pi);
		setCtx(ctx);
		setView(view);
		initPO();
	}	
	
	// Varios
	
	
	private void initPO(){
		// TODO Estos datos se deberían pasar a la clase interna
		// ProcessInfo.JasperReportDTO y modificar esta clase usando dicha clase
		M_Table table = M_Table.get(getCtx(),getPi().getTable_ID());
		setPo(table.getPO(getPi().getRecord_ID(),null));
	}
	
	/**
	 * Obtiene el Frame principal, en este caso, OXPJasperViewer.
	 * @return OXPJasperViewer.
	 */
	private JFrame getMainParentOfView(){
		return (JFrame)getView().getRootPane().getParent();
	}
	
	/**
	 * Obtiene el primer valor del contexto que es similar a la clave parámetro. 
	 * @param context clave del contexto
	 * @return valor del contexto similar al pasado como parámetro o "" cc
	 */
	private Object getContextSimilarTo(String context){
		String res = Env.getContext(getCtx(), context);
		if(Util.isEmpty(res)){
			Enumeration<Object> keys = getCtx().keys();
			boolean found = false;			
			String key = "";
			while(keys.hasMoreElements() && !found) {
				key = (String)keys.nextElement();
				found = key.indexOf(context) != -1;
			}
			if(found){
				res = Env.getContext(getCtx(), key);
			}	
		}
		return res;
	} 
	
	/**
	 * Obtiene el valor como String del contexto que es similar a la clave parámetro. 
	 * @param context clave del contexto
	 * @return valor del contexto similar al pasado como parámetro como string o "" cc
	 */
	private String getContextSimilarToAsString(String context){
		return String.valueOf(getContextSimilarTo(context));
	}
	
	/**
	 * Obtiene el valor como Integer del contexto que es similar a la clave parámetro. 
	 * @param context clave del contexto
	 * @return valor del contexto similar al pasado como parámetro como Integer o 0 cc
	 */
	private Integer getContextSimilarToAsInt(String context){
		Integer returno = 0;
		Object res = getContextSimilarTo(context);
		if(res != null){
			returno = Integer.parseInt(String.valueOf(res));
		}
		return returno;
	}
	
	/**
	 * Retorno el valor de la columna del PO
	 * @param columnName nombre de la columna
	 * @return el valor de la columna del PO, null cc
	 */
	private Object getValue(String columnName){
		Object obj = null;
		if(getPo() != null){
			obj = getPo().get_Value(columnName); 
		}
		return obj;
	}
	
	/**
	 * Retorno el valor como String de la columna del PO
	 * @param columnName nombre de la columna
	 * @return el valor como String de la columna del PO, "" cc
	 */
	private String getValueAsString(String columnName){
		String res = "";
		Object obj = getValue(columnName);
		if(obj != null){
			res = String.valueOf(obj);
		}
		return res;
	}
	
	/**
	 * Retorno el valor como Integer de la columna del PO
	 * @param columnName nombre de la columna
	 * @return el valor como Integer de la columna del PO, 0 cc
	 */
	private Integer getValueAsInt(String columnName){
		Integer res = 0;
		Object obj = getValue(columnName);
		if(obj != null){
			res = Integer.parseInt(String.valueOf(obj));
		}
		return res;
	}
	
	// Manejadores de eventos	
	
	/**
	 * Método disparado cuando se selecciona el botón Enviar Mail en el panel de botones.
	 */
    public void cmd_sendMail() {
        String to         = "";
//        Integer c_bpartner_id = getContextSimilarToAsInt("C_BPartner_ID");
        Integer c_bpartner_id = getValueAsInt("C_BPartner_ID");
        if(c_bpartner_id != 0){
        	MUser[] users = MUser.getOfBPartner(getCtx(), c_bpartner_id);
        	if(users != null){
        		for (int i = 0; (i < users.length) && (to.trim().length() == 0); i++) {
					to = (users[i].getEMail() == null)?"":users[i].getEMail();
				}
        	}
        }
        MUser  from       = MUser.get( getCtx(),Env.getAD_User_ID( getCtx()));
        String subject    = getValueAsString("Description");
        String message    = "";
        File   attachment = null;
        FileOutputStream fos = null;
        DataOutputStream dos = null;
        
        try {
            attachment = File.createTempFile( "mail_"+System.currentTimeMillis(),".pdf" );
            fos = new FileOutputStream(attachment);
			dos = new DataOutputStream(fos);
            JasperExportManager.exportReportToPdfStream(getPrint(), dos);
        } catch( Exception e ) {
            e.printStackTrace();
        } finally{
			try {
				if(dos != null) dos.close();
				if(fos != null) fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

        EMailDialog emd = new EMailDialog( getMainParentOfView(),Msg.getMsg( getCtx(),"SendMail" ),from,to,subject,message,attachment );
    }    // cmd_sendMail

    
    // Getters y Setters
    
	public void setView(JRViewer view) {
		this.view = view;
	}

	public JRViewer getView() {
		return view;
	}

	public void setPrint(JasperPrint print) {
		this.print = print;
	}

	public JasperPrint getPrint() {
		return print;
	}


	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}


	public Properties getCtx() {
		return ctx;
	}


	public void setPi(ProcessInfo pi) {
		this.pi = pi;
	}


	public ProcessInfo getPi() {
		return pi;
	}


	public void setPo(PO po) {
		this.po = po;
	}

	public PO getPo() {
		return po;
	}

}
