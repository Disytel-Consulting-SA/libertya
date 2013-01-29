/*
 * @(#)AccessSqlParserTest.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.OpenXpertya;

/**
 *      AccessSqlParserTest tests the class
 *      AccessSqlParser
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version $Id: AccessSqlParserTest.java,v 1.7 2005/05/17 05:30:16 jjanke Exp $
 */
public class AccessSqlParserTest extends TestCase {

    /**
     * Construct new test instance
     *
     * @param name the test name
     */
    public AccessSqlParserTest(String name) {
        super(name);
    }

    /**
     * Launch the test.
     *
     * @param args String[]
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(AccessSqlParserTest.class);
    }

    /**
     * Run the embeddedFrom test
     */
    public void testEmbeddedFrom() {

        String	sql	= "SELECT t.AD_Table_ID, t.TableName, cc.CCount " + "FROM AD_Table t," + "(SELECT COUNT(ColumnName) AS CCount FROM AD_Column) cc " + "WHERE t.IsActive='Y'";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Column|AD_Table=t,(##)=cc|1]", fixture.toString());
    }

    /**
     * Run the embeddedSelect test
     */
    public void testEmbeddedSelect() {

        String	sql	= "SELECT t.AD_Table_ID, t.TableName," + "(SELECT COUNT(c.ColumnName) FROM AD_Column c WHERE t.AD_Table_ID=c.AD_Table_ID) " + "FROM AD_Table t WHERE t.IsActive='Y'";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Column=c|AD_Table=t|1]", fixture.toString());
    }

    /**
     * Run the exists test
     */
    public void testExists() {

        String	sql	= "SELECT AD_Table.AD_Table_ID, AD_Table.TableName " + "FROM AD_Table " + "WHERE EXISTS (SELECT * FROM AD_Column c WHERE AD_Table.AD_Table_ID=c.AD_Table_ID)";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Column=c|AD_Table|1]", fixture.toString());
    }

    /**
     * Run the exists test with syn
     */
    public void testExistsSyn() {

        String	sql	= "SELECT t.AD_Table_ID, t.TableName " + "FROM AD_Table t " + "WHERE EXISTS (SELECT * FROM AD_Column c WHERE t.AD_Table_ID=c.AD_Table_ID)";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Column=c|AD_Table=t|1]", fixture.toString());
    }

    /**
     * Run the joinInner test
     */
    public void testJoinInner() {

        String	sql	= "SELECT t.AD_Table_ID, t.TableName, c.AD_Column_ID, c.ColumnName " + "FROM AD_Table t INNER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID) WHERE t.IsActive='Y'";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Table=t,AD_Column=c|0]", fixture.toString());
    }

    /**
     * Run the joinOuter test
     */
    public void testJoinOuter() {

        String	sql	= "SELECT t.AD_Table_ID, t.TableName, c.AD_Column_ID, c.ColumnName " + "FROM AD_Table t LEFT OUTER JOIN AD_Column c ON (t.AD_Table_ID=c.AD_Table_ID) WHERE t.IsActive='Y'";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Table=t,AD_Column=c|0]", fixture.toString());
    }

    /**
     * Run the oneTable test
     */
    public void testOneTable() {

        String	sql	= "SELECT AD_Table_ID, TableName FROM AD_Table WHERE IsActive='Y'";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Table|0]", fixture.toString());
    }

    /**
     * Run the oneTableSyn test
     */
    public void testOneTableSyn() {

        String	sql	= "SELECT t.AD_Table_ID, t.TableName FROM AD_Table t WHERE t.IsActive='Y'";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Table=t|0]", fixture.toString());
    }

    /**
     * Run the oneTableSyn test
     */
    public void testOneTableSynAS() {

        String	sql	= "SELECT t.AD_Table_ID, t.TableName FROM AD_Table AS t WHERE t.IsActive='Y'";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Table=t|0]", fixture.toString());
    }

    /**
     * Run the Product Attribute Query
     */
    public void testProductAttributeQuery() {

        String	sql	= "SELECT p.M_Product_ID, p.Discontinued, p.Value, p.Name, BOM_Qty_Available(p.M_Product_ID,?) AS QtyAvailable, bomQtyList(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceList, bomQtyStd(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceStd, BOM_Qty_OnHand(p.M_Product_ID,?) AS QtyOnHand, BOM_Qty_Reserved(p.M_Product_ID,?) AS QtyReserved, BOM_Qty_Ordered(p.M_Product_ID,?) AS QtyOrdered, bomQtyStd(p.M_Product_ID, pr.M_PriceList_Version_ID)-bomQtyLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS Margin, bomQtyLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceLimit, pa.IsInstanceAttribute FROM M_Product p INNER JOIN M_ProductPrice pr ON (p.M_Product_ID=pr.M_Product_ID) LEFT OUTER JOIN M_AttributeSet pa ON (p.M_AttributeSet_ID=pa.M_AttributeSet_ID) WHERE p.IsSummary='N' AND p.IsActive='Y' AND pr.IsActive='Y' AND pr.M_PriceList_Version_ID=? AND p.M_AttributeSetInstance_ID IN (SELECT M_AttributeSetInstance_ID FROM M_AttributeInstance WHERE (M_Attribute_ID=100 AND M_AttributeValue_ID=101)) ORDER BY QtyAvailable DESC, Margin DESC";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[M_AttributeInstance|M_Product=p,M_ProductPrice=pr,M_AttributeSet=pa|1]", fixture.toString());
    }

    /**
     * Run the Product & Instance Attribute Query
     */
    public void testProductInstanceAttributeQuery() {

        String	sql	= "SELECT p.M_Product_ID, p.Discontinued, p.Value, p.Name, BOM_Qty_Available(p.M_Product_ID,?) AS QtyAvailable, bomQtyList(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceList, bomQtyStd(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceStd, BOM_Qty_OnHand(p.M_Product_ID,?) AS QtyOnHand, BOM_Qty_Reserved(p.M_Product_ID,?) AS QtyReserved, BOM_Qty_Ordered(p.M_Product_ID,?) AS QtyOrdered, bomQtyStd(p.M_Product_ID, pr.M_PriceList_Version_ID)-bomQtyLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS Margin, bomQtyLimit(p.M_Product_ID, pr.M_PriceList_Version_ID) AS PriceLimit, pa.IsInstanceAttribute FROM M_Product p INNER JOIN M_ProductPrice pr ON (p.M_Product_ID=pr.M_Product_ID) LEFT OUTER JOIN M_AttributeSet pa ON (p.M_AttributeSet_ID=pa.M_AttributeSet_ID) WHERE p.IsSummary='N' AND p.IsActive='Y' AND pr.IsActive='Y' AND pr.M_PriceList_Version_ID=? AND EXISTS (SELECT * FROM M_Storage s INNER JOIN M_AttributeSetInstance asi ON (s.M_AttributeSetInstance_ID=asi.M_AttributeSetInstance_ID) WHERE s.M_Product_ID=p.M_Product_ID AND asi.SerNo LIKE '33' AND asi.Lot LIKE '33' AND asi.M_Lot_ID=101 AND TRUNC(asi.GuaranteeDate)<TO_DATE('2003-10-16','YYYY-MM-DD') AND asi.M_AttributeSetInstance_ID IN (SELECT M_AttributeSetInstance_ID FROM M_AttributeInstance WHERE (M_Attribute_ID=103 AND Value LIKE '33') AND (M_Attribute_ID=102 AND M_AttributeValue_ID=106))) AND p.M_AttributeSetInstance_ID IN (SELECT M_AttributeSetInstance_ID FROM M_AttributeInstance WHERE (M_Attribute_ID=101 AND M_AttributeValue_ID=105) AND (M_Attribute_ID=100 AND M_AttributeValue_ID=102)) AND p.AD_Client_ID IN(0,11) AND p.AD_Org_ID IN(0,11,12) ORDER BY QtyAvailable DESC, Margin DESC";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[M_AttributeInstance|M_Storage=s,M_AttributeSetInstance=asi|M_AttributeInstance|M_Product=p,M_ProductPrice=pr,M_AttributeSet=pa|3]", fixture.toString());
    }

    /**
     * Run the twoTable test
     */
    public void testTwoTable() {

        String	sql	= "SELECT t.AD_Table_ID, t.TableName, c.AD_Column_ID, c.ColumnName FROM AD_Table t, AD_Column c WHERE t.AD_Table_ID=c.AD_Table_ID AND t.IsActive='Y'";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Table=t,AD_Column=c|0]", fixture.toString());
    }

    /**
     * Run the twoTableSyn test
     */
    public void testTwoTableSyn() {

        String	sql	= "SELECT t.AD_Table_ID, t.TableName, c.AD_Column_ID, c.ColumnName FROM AD_Table as t, AD_Column AS c WHERE t.AD_Table_ID=c.AD_Table_ID AND t.IsActive='Y'";
        AccessSqlParser	fixture	= new AccessSqlParser(sql);

        assertEquals("AccessSqlParser[AD_Table=t,AD_Column=c|0]", fixture.toString());
    }

    //~--- set methods --------------------------------------------------------

    /**
     * Perform pre-test initialization
     *
     * @throws Exception
     *
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {

        super.setUp();
        OpenXpertya.startup(true);
    }

    /**  */
}



/*
 * @(#)AccessSqlParserTest.java   02.jul 2007
 * 
 *  Fin del fichero AccessSqlParserTest.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
