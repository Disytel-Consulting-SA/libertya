package org.openXpertya.JasperReport;

import org.openXpertya.process.ProcessInfo;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

public interface JRViewerProvider {

	public void openViewer(JasperPrint jasperPrint, String title, ProcessInfo pi) throws JRException;

}
