/*
 * @(#)MRoleTest.java   12.oct 2007  Versión 2.2
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

import junit.framework.TestCase;

import org.openXpertya.util.Env;

/**
 * The class <code>MRoleTest</code> contains tests for the class MRole
 * <p>
 * @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 * @version $Id: MRoleTest.java,v 1.5 2005/05/17 05:30:16 jjanke Exp $
 */
public class MRoleTest extends TestCase {

    /** Descripción de Campo */
    private MRole	m_role	= null;

    /**
     * Construct new test instance
     * @param name the test name
     */
    public MRoleTest(String name) {
        super(name);
    }

    /**
     * Launch the test.
     * @param args String[]
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(MRoleTest.class);
    }

    /**
     * Perform post-test clean up
     *
     * @throws Exception
     *
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Run the String addAccessSQL(String, String, boolean, boolean) method
     * test
     */
    public void testAddAccessSQL() {

        // add test code here
        String	sql	= m_role.addAccessSQL("SELECT r.a,r.b,r.c FROM AD_Role r WHERE EXISTS " + "(SELECT AD_Column c WHERE c.a=c.b) ORDER BY 1", "r", MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);

        System.out.println(sql);
        assertEquals(sql, "SELECT r.a,r.b,r.c FROM AD_Role r WHERE EXISTS (SELECT AD_Column c WHERE c.a=c.b) AND r.AD_Client_ID=0 AND r.AD_Org_ID=0 ORDER BY 1");
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Perform pre-test initialization
     * @throws Exception
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception, Exception {

        org.openXpertya.OpenXpertya.startupEnvironment(true);
        m_role	= MRole.getDefault(Env.getCtx(), false);
        super.setUp();
    }
}	// MRoleTest



/*
 * @(#)MRoleTest.java   02.jul 2007
 * 
 *  Fin del fichero MRoleTest.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
