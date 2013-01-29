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



package org.openXpertya.process;

import java.math.BigDecimal;

/**
 * Descripción de Interface
 *
 *
 * @version 2.2, 25.03.06
 * @author         Equipo de Desarrollo de openXpertya    
 */

public interface DocAction {

    /** Descripción de Campos */

    public static final String ACTION_Complete = "CO";

    /** Descripción de Campos */

    public static final String ACTION_WaitComplete = "WC";

    /** Descripción de Campos */

    public static final String ACTION_Approve = "AP";

    /** Descripción de Campos */

    public static final String ACTION_Reject = "RJ";

    /** Descripción de Campos */

    public static final String ACTION_Post = "PO";

    /** Descripción de Campos */

    public static final String ACTION_Void = "VO";

    /** Descripción de Campos */

    public static final String ACTION_Close = "CL";

    /** Descripción de Campos */

    public static final String ACTION_Reverse_Correct = "RC";

    /** Descripción de Campos */

    public static final String ACTION_Reverse_Accrual = "RA";

    /** Descripción de Campos */

    public static final String ACTION_ReActivate = "RE";

    /** Descripción de Campos */

    public static final String ACTION_None = "--";

    /** Descripción de Campos */

    public static final String ACTION_Prepare = "PR";

    /** Descripción de Campos */

    public static final String ACTION_Unlock = "XL";

    /** Descripción de Campos */

    public static final String ACTION_Invalidate = "IN";

    /** Descripción de Campos */

    public static final String ACTION_ReOpen = "OP";

    /** Descripción de Campos */

    public static final String STATUS_Drafted = "DR";

    /** Descripción de Campos */

    public static final String STATUS_Completed = "CO";

    /** Descripción de Campos */

    public static final String STATUS_Approved = "AP";

    /** Descripción de Campos */

    public static final String STATUS_Invalid = "IN";

    /** Descripción de Campos */

    public static final String STATUS_NotApproved = "NA";

    /** Descripción de Campos */

    public static final String STATUS_Voided = "VO";

    /** Descripción de Campos */

    public static final String STATUS_Reversed = "RE";

    /** Descripción de Campos */

    public static final String STATUS_Closed = "CL";

    /** Descripción de Campos */

    public static final String STATUS_Unknown = "??";

    /** Descripción de Campos */

    public static final String STATUS_InProgress = "IP";

    /** Descripción de Campos */

    public static final String STATUS_WaitingPayment = "WP";

    /** Descripción de Campos */

    public static final String STATUS_WaitingConfirmation = "WC";

    /**
     * Descripción de Método
     *
     *
     * @param newStatus
     */

    public void setDocStatus( String newStatus );

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocStatus();

    /**
     * Descripción de Método
     *
     *
     * @param action
     *
     * @return
     *
     * @throws Exception
     */

    public boolean processIt( String action ) throws Exception;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean unlockIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean invalidateIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String prepareIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean approveIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean rejectIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String completeIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean postIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean voidIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean closeIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseCorrectIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reverseAccrualIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean reActivateIt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSummary();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getProcessMsg();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getDoc_User_ID();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Currency_ID();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public BigDecimal getApprovalAmt();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getAD_Org_ID();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDocAction();

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean save();
    
	/** DocAction Ref_List values **/
	public static final int AD_REFERENCE_ID = 135;
}    // DocAction



/*
 *  @(#)DocAction.java   25.03.06
 * 
 *  Fin del fichero DocAction.java
 *  
 *  Versión 2.2
 *
 */
