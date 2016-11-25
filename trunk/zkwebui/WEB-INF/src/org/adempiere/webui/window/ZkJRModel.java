package org.adempiere.webui.window;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.openXpertya.model.MUser;
import org.openXpertya.model.M_Table;
import org.openXpertya.model.PO;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class ZkJRModel {

	// Variables de instancia
	
	/** Interfase que maneja */
	
	private ZkJRViewer view;
	
	/** Jasper print impreso */
	
	private JasperPrint print;
	
	/** Contexto */
	
	private Properties ctx;
	
	/** Información del proceso actual, o sea, del Jasper que está corriendo */
	
	private ProcessInfo pi;
	
	/** PO involucrado en el record id del Process Info */
	
	private PO po;
	
	
	
	// Constructores
	
	public ZkJRModel( Properties ctx, ZkJRViewer view, ProcessInfo pi) {
		setCtx(ctx);
		setPi(pi);
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

        WEMailDialog emd = new WEMailDialog( getView(),Msg.getMsg( getCtx(),"SendMail" ),from,to,subject,message,attachment );
    }    // cmd_sendMail

    
    // Getters y Setters
    
	public void setView(ZkJRViewer view) {
		this.view = view;
	}

	public ZkJRViewer getView() {
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
