package org.openXpertya.apps;

import java.util.Properties;
import java.awt.*;

import org.openXpertya.model.*;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.*;
import org.openXpertya.process.*;

public class Files extends SvrProcess {
	
	/** Descripcion de Campos */

    private int p_Table_ID = 0;
    private int p_Record_ID = 0;
    private Properties m_ctx = Env.getCtx();

	protected void prepare() {
		p_Record_ID = getRecord_ID();
		p_Table_ID = getTable_ID();
	}

	protected String doIt() throws Exception {
		
		// Escribimos las variables en el contexto
		m_ctx = Env.getCtx();
		String str_p_Record_ID = Integer.toString(p_Record_ID);
		m_ctx.setProperty("p_Record_ID",str_p_Record_ID);
		String str_p_Table_ID = Integer.toString(p_Table_ID);
		m_ctx.setProperty("p_Table_ID",str_p_Table_ID);
		
		// Abrimos la ventana "Files" con restricciones mediante el zoom
		// AWindow ventana = new AWindow();
		
		// Hacemos zoom
		MQuery m_query = new MQuery("D_File");
		m_query.addRestriction("Table_ID",MQuery.EQUAL,p_Table_ID);
		m_query.addRestriction("Record_ID",MQuery.EQUAL,p_Record_ID);
		AEnv.zoom(m_query);

		// Aqui hacemos un zoom manual 
		//ventana.initWindow(1000040,m_query);
		//AEnv.showCenterScreen( ventana );
		//ventana = null;
		
		// Recojemos la ventana
		//ProcessInfo pi = getProcessInfo();
		//int ventana =  pi.getWindowNo();
		
		//	Cerramos la ventana
		//Window frame = Env.getWindow(ventana);
		//frame.dispose();
		
		return "Abriendo documento asociado";
	}
}
