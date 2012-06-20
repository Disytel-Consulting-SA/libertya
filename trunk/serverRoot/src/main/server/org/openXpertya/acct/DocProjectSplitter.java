package org.openXpertya.acct;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import org.openXpertya.model.MAcctSchema;
import org.openXpertya.model.PO;
import org.openXpertya.model.X_C_Invoice;
import org.openXpertya.util.DB;

public class DocProjectSplitter {

	DocProjectSplitterInterface m_doc = null;
	
	public DocProjectSplitter(DocProjectSplitterInterface aDoc)
	{
		m_doc = aDoc;
	}
	
    /**
     * Realiza el split de una linea totalizada proporcionalmente respecto de los proyectos eincluidos en las lineas de la misma
     * @param fact
     */
    public boolean splitLinesByProject(Fact fact) 
    {
    	// Tenemos lineas?
    	if (fact.getLines().length == 0)
    		return true;
    	
    	// Obtener la precision del schema
    	FactLine factLine = fact.getLines()[0];
    	MAcctSchema as = MAcctSchema.get( factLine.getCtx(), factLine.getC_AcctSchema_ID());
		int precision = as.getStdPrecision();

    	// Iterar por todas las lineas de la fact, spliteando cada una de estas
    	int length = fact.getLines().length;
    	for (int i = 0; i < length; i++)
    	{
    		// ¿Es una linea que deba splitearse?
    		factLine = fact.getLines()[i];
    		
        	// Es necesario splitear?
        	int projectsNo = m_doc.getProjectsInLinesQuery(factLine);
        	if (projectsNo < 2)
        		continue;
    		
        	// Verificar si es necesario realizar el split segun el tipo de documento
    		if (m_doc.requiresSplit(factLine))
    		{
    	    	// Obtener la nomina de proyectos y porcentajes correspondiente
    			HashMap<Integer, BigDecimal> map =  m_doc.getProjectPercentageQuery(factLine);
    			
    			// Hubo un error en la obtencion del map... imposible continuar
    			if (map == null)
    				return false;
    			
    			// Determinar porcentajes de cada linea para cada proyecto
    	    	Vector<Integer> projects = new Vector<Integer>(map.keySet());
    	    	Vector<BigDecimal> percents = new Vector<BigDecimal>(map.values());   	
    			
    			// Generar los "clones" de la line original (proyecto 1 en adelante) 
    			for (int k = 1; k < projectsNo; k++)
    			{
    				// Copiar los valores de la linea original
    				FactLine newFactLine = new FactLine(factLine.getCtx(), factLine.getAD_Table_ID(), factLine.getRecord_ID(), factLine.getLine_ID(), factLine.get_TrxName());
        			PO.deepCopyValues(factLine, newFactLine);
        			newFactLine.setAccount(fact.getAcctSchema(), factLine.getAccount());
        			newFactLine.setC_Project_ID(projects.get(k));
        			
        			// Asignar el proporcional, y agregar la nueva linea
        			setProportionalValue(newFactLine, percents.get(k), precision, BigDecimal.ROUND_HALF_EVEN);
        			fact.add(newFactLine);	
    			}

    			// Deberemos setear también los valores para la linea original.  La misma sera para el proyecto 0.  
    			setProportionalValue(factLine, percents.get(0), precision, BigDecimal.ROUND_HALF_EVEN);
    			factLine.setC_Project_ID(projects.get(0));
    		}
    			
    	}

    	// Todo ok
    	return true;
    }
    
    
    /**
     * Setea el valor de la linea por el indicado por parametro
     * @param factLine a modificar
     * @param value es la proporcion a asignar
     * @param precision decimales de precision
     * @param round metodo de redondeo
     */
    private void setProportionalValue(FactLine factLine, BigDecimal value, int precision, int roundMode) 
    {
		factLine.setAmtSource(factLine.getC_Currency_ID(), factLine.getAmtSourceDr().multiply(value).setScale(precision, roundMode), factLine.getAmtSourceCr().multiply(value).setScale(precision, roundMode));
		factLine.setAmtAcct(factLine.getAmtAcctDr().multiply(value).setScale(precision, roundMode), factLine.getAmtAcctCr().multiply(value).setScale(precision, roundMode));
    }
    
	
}
