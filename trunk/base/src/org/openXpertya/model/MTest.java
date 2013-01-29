/*
 * @(#)MTest.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.OpenXpertya;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.math.BigDecimal;

import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.Properties;

/**
 *      Test Model
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: MTest.java,v 1.8 2005/03/11 20:28:38 jjanke Exp $
 */
public class MTest extends X_Test {

    /**
     *      Constructor
     *      @param ctx context
     *      @param Test_ID
     * @param trxName
     */
    public MTest(Properties ctx, int Test_ID, String trxName) {
        super(ctx, Test_ID, trxName);
    }		// MTest

    /**
     *      Load Constructor
     *      @param ctx context
     *      @param rs result set
     * @param trxName
     */
    public MTest(Properties ctx, ResultSet rs, String trxName) {
        super(ctx, rs, trxName);
    }		// MTest

    /**
     *      Test Object Constructor
     *      @param ctx context
     *      @param testString test string
     * @param testNo
     */
    public MTest(Properties ctx, String testString, int testNo) {

        super(ctx, 0, null);
        testString	= testString + "_" + testNo;
        setName(testString);
        setDescription(testString + " " + testString + " " + testString);
        setHelp(getDescription() + " - " + getDescription());
        setT_Date(new Timestamp(System.currentTimeMillis()));
        setT_DateTime(new Timestamp(System.currentTimeMillis()));
        setT_Integer(testNo);
        setT_Amount(new BigDecimal(testNo));
        setT_Number(Env.ONE.divide(new BigDecimal(testNo), BigDecimal.ROUND_HALF_UP));

        //
        setC_Currency_ID(100);		// USD
        setC_Location_ID(109);		// Monroe
        setC_UOM_ID(100);		// Each

        // setC_BPartner_ID(C_BPartner_ID);
        // setC_Payment_ID(C_Payment_ID);
        // setM_Locator_ID(M_Locator_ID);
        // setM_Product_ID(M_Product_ID);

    }		// MTest

    /**
     *      After Delete
     *      @param success
     *      @return
     */
    protected boolean afterDelete(boolean success) {

        log.info("afterDelete *** Success=" + success);

        return success;
    }

    /**
     *      After Save
     *      @param newRecord
     *      @param success
     *
     * @return
     */
    protected boolean afterSave(boolean newRecord, boolean success) {

        log.info("afterSave - New=" + newRecord + ", Seccess=" + success + " ***");

        return success;

    }		// afterSave

    /**
     *      Before Delete
     *      @return true if it can be deleted
     */
    protected boolean beforeDelete() {

        log.info("beforeDelete ***");

        return true;
    }

    /**
     *      Before Save
     *      @param newRecord
     *      @return
     */
    protected boolean beforeSave(boolean newRecord) {

        log.info("beforeSave - New=" + newRecord + " ***");

        return true;
    }

    /**
     *      Test
     *      @param args
     */
    public static void main(String[] args) {

        OpenXpertya.startup(true);

        Properties	ctx	= Env.getCtx();

        /** Test CLOB */
        MTest	t1	= new MTest(ctx, 0, null);

        t1.setName("Test1");
        System.out.println("->" + t1.getCharacterData() + "<-");
        t1.save();
        t1.setCharacterData("Long Text JJ");
        t1.save();

        int	Test_ID	= t1.getTest_ID();

        //
        MTest	t2	= new MTest(Env.getCtx(), Test_ID, null);

        System.out.println("->" + t2.getCharacterData() + "<-");
        t2.delete(true);

        /**
         *     Volume Test
         * for (int i = 1; i < 20000; i++)
         * {
         *       new MTest (ctx, "test", i).save();
         * }
         * /** 
         */

    }		// main
}	// MTest



/*
 * @(#)MTest.java   02.jul 2007
 * 
 *  Fin del fichero MTest.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
