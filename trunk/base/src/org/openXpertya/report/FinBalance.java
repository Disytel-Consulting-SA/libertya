/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2005 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.report;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.model.FactBalanceConfig;
import org.openXpertya.model.FactBalanceConfigVO;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.process.ProcessInfoParameter;
import org.openXpertya.process.SvrProcess;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

/**
 * Descripción de Clase
 *
 *
 * @version    2.0, 22.03.06
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class FinBalance extends SvrProcess {

	/** Log */
    protected static CLogger s_log = CLogger.getCLogger( FinBalance.class );

    /** Esquema contable */
    private int p_C_AcctSchema_ID = 0;

    /** Rango de fechas */
    private Timestamp p_dateFrom = null;
    private Timestamp p_dateTo = null;
    
    /** Organización */
    private int p_orgID = 0;
    
    /** Cuenta contable */
    private int p_elementValueID = 0;
    
    @Override
    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();
        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();
            if( para[ i ].getParameter() == null ) {
                ;
            } else if( name.equals( "C_AcctSchema_ID" )) {
                p_C_AcctSchema_ID = (( BigDecimal )para[ i ].getParameter()).intValue();
            } else if( name.equals( "Date" )) {
                p_dateFrom = (Timestamp)para[i].getParameter();
                p_dateTo = (Timestamp)para[i].getParameter_To();
            }
            else if( name.equals( "AD_Org_ID" )) {
            	p_orgID = (( BigDecimal )para[ i ].getParameter()).intValue();
            }
            else if( name.equals( "C_ElementValue_ID" )) {
            	p_elementValueID = (( BigDecimal )para[ i ].getParameter()).intValue();
            }
        }
        // Inicializar los datos
        FactBalanceConfig.initialize(getCtx());
    }

    
    protected String doIt() throws java.lang.Exception {
    	FactBalanceConfigVO balanceVO = createBalanceVO();
    	String msg = "@ProcessOK@ ";
        msg += updateBalance(balanceVO);
        return msg;
    }
    
    /**
	 * @return valores propios del balance contable
	 */
    protected FactBalanceConfigVO createBalanceVO(){
    	FactBalanceConfigVO balanceVO = new FactBalanceConfigVO();
    	balanceVO.ctx = getCtx();
    	balanceVO.clientID = getAD_Client_ID();
    	balanceVO.acctSchemaID = p_C_AcctSchema_ID;
    	balanceVO.orgID = p_orgID;
    	balanceVO.elementValueID = p_elementValueID;
    	balanceVO.dateFrom = p_dateFrom;
    	balanceVO.dateTo = p_dateTo;
    	balanceVO.trxName = get_TrxName();
    	return balanceVO;
    }
    
    /**
	 * Actualiza el balance contable por esquema contable
	 * 
	 * @param balanceVO
	 *            datos propios de esta actualización de balance
	 */
    public static String updateBalance(FactBalanceConfigVO balanceVO) {
        long start = System.currentTimeMillis();
        balanceVO.ctx = balanceVO.ctx == null?Env.getCtx():balanceVO.ctx;
        
        MAcctSchema as = MAcctSchema.get(balanceVO.ctx, balanceVO.acctSchemaID); 
        String msg = "@C_AcctSchema_ID@: "+as.getName()+". \n";
        
        // Sólo se actualiza el balance contable si así está configurado para este esquema
        if(!as.isFactAcctBalanceActive()){
        	msg += Msg.getMsg(balanceVO.ctx, "FactBalanceInactive");
        }
        else{
            // Por lo pronto siempre se elimina y luego se inserta
            // Eliminar los datos de la tabla
            msg += FactBalanceConfig.deleteBalance(balanceVO);
            // Insertar los faltantes
            msg += FactBalanceConfig.doInsert(balanceVO);
            
            start = System.currentTimeMillis() - start;
            msg += "@ElapsedTime@: " + ( start / 1000 ) + " @Seconds@";
        }
        
        return Msg.parseTranslation(balanceVO.ctx, msg);
    }
    
}    // FinBalance



/*
 *  @(#)FinBalance.java   22.03.06
 * 
 *  Fin del fichero FinBalance.java
 *  
 *  Versión 2.0
 *
 */
