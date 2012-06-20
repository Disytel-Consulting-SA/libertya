package org.openXpertya.JasperReport;

import java.awt.Dimension;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRViewer;

import org.openXpertya.JasperReport.DataSource.JasperReportsUtil;
import org.openXpertya.process.ProcessInfo;
import org.openXpertya.util.Msg;


public class OXPJRViewer extends JRViewer {

	// Variables de instancia
	
	/** Manejador de los eventos del panel */
	
	private OXPJRModel model;
		
	/** Botón agregado para el envío del documento por mail */
	
	protected JButton btnMail;
	
	/** Contexto */
	
	private Properties ctx;

	// Constructores
	
	public OXPJRViewer(Properties ctx, ProcessInfo pi, JasperPrint arg0, Locale arg1){
		super(arg0,arg1);
		setCtx(ctx);
		initModel(pi,arg0,arg1);
		init();
	}
	
	// Varios

	private void initModel(ProcessInfo pi, JasperPrint arg0, Locale arg1){
		// Modelo
		setModel(new OXPJRModel(getCtx(), pi,this));
		getModel().setPrint(arg0);			
	}
	
	private void init() {
		// GUI
		JPanel separator = new JPanel();
		separator.setSize(new Dimension(10,10));
		
		btnMail = new JButton();

		btnMail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/openXpertya/images/EMailSupport16.gif")));
		btnMail.setToolTipText( Msg.getMsg( getCtx(), "SendMail" ) );
		btnMail.setMargin(new java.awt.Insets(2, 2, 2, 2));
		btnMail.setMaximumSize(new java.awt.Dimension(23, 23));
		btnMail.setMinimumSize(new java.awt.Dimension(23, 23));
		btnMail.setPreferredSize(new java.awt.Dimension(23, 23));
		btnMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	getModel().cmd_sendMail();
            }
        });
		
		tlbToolBar.add(separator);
        tlbToolBar.add(btnMail);
        btnPrint.removeActionListener(btnPrint.getActionListeners()[0]);
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	showPrintDialog();
            }
        });
        this.setPreferredSize(new Dimension(800, 600));
	}

	/**
	 * Muestra el dialog de impresión cuando se selecciona el botón 
	 */
	private void showPrintDialog(){
		try{
			JasperReportsUtil.printJasperReport(getCtx(), getModel().getPrint(), getModel().getPi(), true);
		} catch(Exception e){
			
		}
	}
	
	// Getters y Setters
	
	protected void setModel(OXPJRModel model) {
		this.model = model;
	}

	protected OXPJRModel getModel() {
		return model;
	}

	public void setCtx(Properties ctx) {
		this.ctx = ctx;
	}

	public Properties getCtx() {
		return ctx;
	}
}
