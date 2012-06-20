package org.openXpertya.process;

/**
 * Interfaz para metodos adicionales de interaccion con SvrProcess
 */

import java.io.File;
import java.util.Properties;

public interface SvrProcessExtendInterface {

	
    public void setRecordId(int record_id);
    
    public void setJasperReportId(int jasperReport_ID);  
    
    public void setCtx(Properties value);
    
    public File getPDFFile();
    
    public int getJasperReportID(String jasperName);
    
}
