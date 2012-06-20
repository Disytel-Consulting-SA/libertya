/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.impexp;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.openXpertya.model.MBankStatementLoader;

/**
 * Descripción de Interface
 *
 *
 * @version    2.2, 12.10.07
 * @author         Equipo de Desarrollo de openXpertya    
 */

public interface BankStatementLoaderInterface {

    /**
     * Descripción de Método
     *
     *
     * @param controller
     *
     * @return
     */

    public boolean init( MBankStatementLoader controller );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isValid();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean loadLines();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getLastErrorMessage();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getLastErrorDescription();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getDateLastRun();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getRoutingNo();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getBankAccountNo();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getStatementReference();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStatementDate();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTrxID();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getReference();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCheckNo();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPayeeName();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getPayeeAccountNo();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getStatementLineDate();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Timestamp getValutaDate();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getTrxType();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean getIsReversal();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getCurrency();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getStmtAmt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getTrxAmt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getInterestAmt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getMemo();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getChargeName();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getChargeAmt();
}    // BankStatementLoaderInterface



/*
 *  @(#)BankStatementLoaderInterface.java   02.07.07
 * 
 *  Fin del fichero BankStatementLoaderInterface.java
 *  
 *  Versión 2.2
 *
 */
