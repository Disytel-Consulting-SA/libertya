package org.openXpertya.JasperReport;

import java.util.Properties;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.openXpertya.process.ProcessInfo;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;
import net.sf.jasperreports.view.JasperViewer;

public class OXPJasperViewer extends JasperViewer {

	// Variables de instancia
	
	/** Nombre del reporte */
	
	private String reportName;
	
	/** Contexto */
	
	private Properties ctx;
	
	// Métodos estáticos
	
	public static OXPJasperViewer viewReport(Properties ctx, ProcessInfo pi, JasperPrint print, boolean exitOnClose){
		OXPJRViewer v = new OXPJRViewer(ctx,pi,print,null);
		OXPJasperViewer oxpjv = new OXPJasperViewer(ctx,print,v,exitOnClose);
		oxpjv.setVisible(true);
		return oxpjv;
	} 
	
	
	// Constructores
	
	public OXPJasperViewer(JasperPrint jasperPrint,boolean exit) {
		super(jasperPrint,exit);
	}
	
	public OXPJasperViewer(JasperPrint jasperPrint, JRViewer v,boolean exit) {
		super(jasperPrint,exit);
		setViewer(v);
	}

	public OXPJasperViewer(Properties ctx, JasperPrint jasperPrint, JRViewer v,boolean exit) {
		super(jasperPrint,exit);
		setViewer(v);
		setCtx(ctx);
	}
	
	// Varios
	
	public void setViewer(JRViewer v){
		this.viewer = v;
		((JPanel)((JPanel)((JLayeredPane)this.rootPane.getComponent(1)).getComponents()[0]).getComponent(0)).remove(0);
		((JPanel)((JPanel)((JLayeredPane)this.rootPane.getComponent(1)).getComponents()[0]).getComponent(0)).add(v);
	}


	// Getters y Setters
	
	protected void setReportName(String reportName) {
		this.reportName = reportName;
	}


	protected String getReportName() {
		return reportName;
	}


	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}


	public Properties getCtx() {
		return ctx;
	}
	

}
