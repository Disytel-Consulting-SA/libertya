package org.adempiere.webui.window;

//import net.sf.jasperreports.engine.JRException;
//import net.sf.jasperreports.engine.JasperPrint;

import net.sf.jasperreports.engine.JasperPrint;

import org.adempiere.webui.component.Window;
import org.adempiere.webui.session.SessionManager;
import org.openXpertya.JasperReport.JRViewerProvider;
import org.openXpertya.process.ProcessInfo;

public class ZkJRViewerProvider implements JRViewerProvider {

	@Override
	public void openViewer(JasperPrint jasperPrint, String title, ProcessInfo pi) {
		Window viewer = new ZkJRViewer(jasperPrint, title, pi);
		
		viewer.setAttribute(Window.MODE_KEY, Window.MODE_EMBEDDED);
		viewer.setAttribute(Window.INSERT_POSITION_KEY, Window.INSERT_NEXT);
		SessionManager.getAppDesktop().showWindow(viewer);		
	}

}
