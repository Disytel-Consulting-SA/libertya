/*
 * @(#)MPInstancePara.java   12.oct 2007  Versión 2.2
 *
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son copyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  copyRight (c)  2003-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son copyRight (c) 2005-2006 de Dataware Sistemas S.L., otras son
 * copyright (c) 2005-2006 de Indeos Consultoría S.L., otras son copyright (c) 2005-2006 de Disytel
 * Servicios Digitales S.A., y otras  partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o 
 * mejoradas a partir de código original de  terceros, recogidos en el ADDENDUM  A, sección 3 (A.3)
 * de dicha licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará
 * sujeto a su respectiva licencia original.  
 *    Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.math.BigDecimal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.Date;
import java.util.Properties;

import org.openXpertya.plugin.common.PluginUtils;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;

/**
 *  Process Instance Parameter Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MPInstancePara.java,v 1.7 2005/05/08 15:17:13 jjanke Exp $
 */
public class MPInstancePara extends X_AD_PInstance_Para {

	/**
	 * Obtengo la instancia del parametro de la instancia del proceso.
	 * Si no existe, null.
	 * @param ctx contexto
	 * @param AD_PInstance_ID id de la instancia del proceso
	 * @param SeqNo número de secuencia del parámetro
	 * @param trxName nombre de la transacción utilizada
	 * @return la instancia del parámetro del proceso, null cc
	 */
	public static MPInstancePara get(Properties ctx, int AD_PInstance_ID, int SeqNo, String trxName) {
		String sql = "SELECT * " +
					 "FROM ad_pinstance_para " +
					 "WHERE (ad_client_id = ?) AND (ad_pinstance_id = ?) AND (seqno = ?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		MPInstancePara instancePara = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			ps.setInt(2, AD_PInstance_ID);
			ps.setInt(3, SeqNo);
			rs = ps.executeQuery();
			if(rs.next()){
				instancePara = new MPInstancePara(ctx,rs,trxName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try{
				if(ps != null)ps.close();
				if(rs != null)rs.close();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		return instancePara;
	}
	
	
	
    /**
     *      Parent Constructor
     *
     * @param instance
     *      @param SeqNo
     */
    public MPInstancePara(MPInstance instance, int SeqNo) {

        super(instance.getCtx(), 0, instance.get_TrxName());
        setAD_PInstance_ID(instance.getAD_PInstance_ID());
        setSeqNo(SeqNo);

    }		// MPInstance_Para

    /**
     *      Parent Constructor
     *      @param ctx
     *      @param AD_PInstance_ID
     *      @param SeqNo
     */
    public MPInstancePara(Properties ctx, int AD_PInstance_ID, int SeqNo) {

        super(ctx, 0, PluginUtils.getPluginInstallerTrxName());
        setAD_PInstance_ID(AD_PInstance_ID);
        setSeqNo(SeqNo);

    }		// MPInstance_Para

    
    /**
     *      Parent Constructor
     *      @param ctx
     *      @param AD_PInstance_ID
     *      @param SeqNo
     */
    public MPInstancePara(Properties ctx, int AD_PInstance_ID, int SeqNo, String trxName) {
        super(ctx, 0, trxName);
        setAD_PInstance_ID(AD_PInstance_ID);
        setSeqNo(SeqNo);

    }		// MPInstance_Para
    
    
    /**
     *      Persistency Constructor
     *      @param ctx context
     *      @param ignored ignored
     * @param trxName
     */
    public MPInstancePara(Properties ctx, int ignored, String trxName) {

        super(ctx, 0, trxName);

        if (ignored != 0) {
            throw new IllegalArgumentException("Multi-Key");
        }

    }		// MPInstance_Para

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MPInstancePara(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MPInstance_Para

    /**
     *      String Representation
     *      @return info
     */
    public String toString() {

        StringBuffer	sb	= new StringBuffer("MPInstance_Para[").append(getID()).append("-").append(getParameterName());

        if (getP_String() != null) {

            sb.append("(s)=").append(getP_String());

            if (getP_String_To() != null) {
                sb.append(" - ").append(getP_String_To());
            }
        }

        if (getP_Number() != null) {

            sb.append("(p)=").append(getP_Number());

            if (getP_Number_To() != null) {
                sb.append(" - ").append(getP_Number_To());
            }
        }

        if (getP_Date() != null) {

            sb.append("(d)=").append(getP_Date());

            if (getP_Date_To() != null) {
                sb.append(" - ").append(getP_Date_To());
            }
        }

        sb.append("]");

        return sb.toString();

    }		// toString

    //~--- set methods --------------------------------------------------------

    /**
     *      Set P_Number
     *      @param P_Number no
     */
    public void setP_Number(int P_Number) {
        setP_Number(new BigDecimal(P_Number));
    }		// setP_Number

    /**
     *      Set P_Number
     *      @param P_Number no
     */
    public void setP_Number(Integer P_Number) {

        if (P_Number == null) {
            setP_Number(0);
        } else {
            setP_Number(((Integer) P_Number).intValue());
        }

    }		// setP_Number

    /**
     *      Set P_Number To
     *      @param P_Number_To no to
     */
    public void setP_Number_To(int P_Number_To) {
        setP_Number_To(new BigDecimal(P_Number_To));
    }		// setP_Number_To

    /**
     *      Set P_Number To
     *      @param P_Number_To no to
     */
    public void setP_Number_To(Integer P_Number_To) {

        if (P_Number_To == null) {
            setP_Number_To(0);
        } else {
            setP_Number_To(((Integer) P_Number_To).intValue());
        }

    }		// setP_Number_To

    /**
     *      Set Number Parameter
     *      @param parameterName name
     *      @param bdParameter value
     */
    public void setParameter(String parameterName, BigDecimal bdParameter) {

        setParameterName(parameterName);
        setP_Number(bdParameter);

    }		// setParameter

    /**
     *      Set Number Parameter
     *      @param parameterName name
     * @param iParameter
     */
    public void setParameter(String parameterName, int iParameter) {

        setParameterName(parameterName);
        setP_Number(new BigDecimal(iParameter));

    }		// setParameter

    /**
     *      Set String Parameter
     *      @param parameterName name
     *      @param stringParameter value
     */
    public void setParameter(String parameterName, String stringParameter) {

        setParameterName(parameterName);
        setP_String(stringParameter);

    }		// setParameter
    
    /**
     * Set parameter by process parameter display type
     * @param displayType process parameter display type
     * @param value value to set
     */
    public void setParameter(int displayType,Object value,boolean isTo){
    	// Verify display type and obtain column name
    	String columnName = "";
    	Object realValue = value;
    	// Si es de tipo fecha
    	if(DisplayType.isDate(displayType)){
    		columnName = "P_Date";
    		if(value instanceof Integer){
    			realValue = new Timestamp(((Date)value).getTime());
    		}
    		else{
    			realValue = (Timestamp)value;
    		}    		
    	}
    	// Si es de tipo numerico
    	else if(DisplayType.isNumeric(displayType) || DisplayType.isID(displayType)){
    		columnName = "P_Number";
    		if(value instanceof Integer){
    			realValue = new BigDecimal((Integer)value);
    		}
    		else{
    			realValue = (BigDecimal)value;
    		}    		
    	}
    	// Si es de tipo texto
    	else if(DisplayType.isText(displayType) || (DisplayType.YesNo == displayType) || (DisplayType.List == displayType)){
    		columnName = "P_String";
    		realValue = (String)value;
    	}
    	// Si es si/no
    	/*else if(DisplayType.YesNo == displayType){
    		columnName = "P_String";
    		realValue = new String((Boolean)value?"Y":"N");
    	}*/

    	// Si es para TO, o sea, es el TO del rango
    	if(isTo){
    		columnName = columnName+"_To";
    	}
    	
    	// Seteo el valor
    	this.set_Value(columnName, realValue);
    }		// setParameter
    
}	// MPInstance_Para



/*
 * @(#)MPInstancePara.java   02.jul 2007
 * 
 *  Fin del fichero MPInstancePara.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
