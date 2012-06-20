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

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class BankStatementMatchInfo {

    /**
     * Constructor de la clase ...
     *
     */

    public BankStatementMatchInfo() {
        super();
    }    // BankStatementMatchInfo

    /** Descripción de Campos */

    private int m_C_BPartner_ID = 0;

    /** Descripción de Campos */

    private int m_C_Payment_ID = 0;

    /** Descripción de Campos */

    private int m_C_Invoice_ID = 0;

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isMatched() {
        return (m_C_BPartner_ID > 0) || (m_C_Payment_ID > 0) || (m_C_Invoice_ID > 0);
    }    // isValid

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_BPartner_ID() {
        return m_C_BPartner_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param C_BPartner_ID
     */

    public void setC_BPartner_ID( int C_BPartner_ID ) {
        m_C_BPartner_ID = C_BPartner_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Payment_ID() {
        return m_C_Payment_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param C_Payment_ID
     */

    public void setC_Payment_ID( int C_Payment_ID ) {
        m_C_Payment_ID = C_Payment_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getC_Invoice_ID() {
        return m_C_Invoice_ID;
    }

    /**
     * Descripción de Método
     *
     *
     * @param C_Invoice_ID
     */

    public void setC_Invoice_ID( int C_Invoice_ID ) {
        m_C_Invoice_ID = C_Invoice_ID;
    }
}    // BankStatementMatchInfo



/*
 *  @(#)BankStatementMatchInfo.java   02.07.07
 * 
 *  Fin del fichero BankStatementMatchInfo.java
 *  
 *  Versión 2.2
 *
 */
