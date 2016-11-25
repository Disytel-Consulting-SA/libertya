package org.openXpertya.JasperReport;


import org.openXpertya.process.ProcessInfo;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public class SwingJRViewerProvider implements JRViewerProvider {

	public void openViewer(JasperPrint jasperPrint, String title, ProcessInfo pi) throws JRException {
		JasperViewer jasperViewer = new JasperViewer( jasperPrint, title);
		jasperViewer.setExtendedState(jasperViewer.getExtendedState() | javax.swing.JFrame.MAXIMIZED_BOTH);
        jasperViewer.setVisible(true);
	}
	
}
