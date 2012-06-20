/*
 * @(#)VCRP.java   14.jun 2007  Versión 2.2
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



package org.openXpertya.mfg.form;

import openXpertya.model.MMPCOrder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import org.openXpertya.apps.*;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.db.*;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLocator;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.grid.ed.VPAttribute;
import org.openXpertya.impexp.*;
import org.openXpertya.minigrid.*;
import org.openXpertya.model.*;
import org.openXpertya.print.*;
import org.openXpertya.util.*;

//import org.jfree.ui.ApplicationFrame;
//import org.jfree.ui.RefineryUtilities;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;

import java.text.DateFormat;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.plaf.*;
import org.compiere.swing.*;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 14.jun 2007
 * @autor     Fundesle    
 */
public class VCRP extends CPanel implements FormPanel, ActionListener {

    // begin vpj

    /** Descripción de Campo */
    private static CLogger	log	= CLogger.getCLogger(VCRP.class);

    /** Window No */
    private int	m_WindowNo	= 0;

    /** FormFrame */
    private FormFrame	m_frame;

    /**
     *      Initialize Panel
     *  @param WindowNo window
     *  @param frame frame
     */
    public void init(int WindowNo, FormFrame frame) {

        log.info("VCRP.init");
        m_WindowNo	= WindowNo;
        m_frame		= frame;

        try {

            fillPicks();
            jbInit();

            /* dynInit(); */
            frame.getContentPane().add(northPanel, BorderLayout.NORTH);
            frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
            frame.getContentPane().add(confirmPanel, BorderLayout.SOUTH);
            frame.pack();

            // frame.m_maximize=true;
            // frame.setMaximize(true);

        } catch (Exception e) {
            log.log(Level.SEVERE, "VCRP.init", e);
        }

    }		// init

    /** Descripción de Campo */
    private CPanel	northPanel	= new CPanel();

    /** Descripción de Campo */
    private CPanel	centerPanel	= new CPanel();

    /** Descripción de Campo */
    private BorderLayout	centerLayout	= new BorderLayout();

    /** Descripción de Campo */
    private ConfirmPanel	confirmPanel	= new ConfirmPanel(true);

    /** Descripción de Campo */
    private Hashtable	hash	= new Hashtable();

    /** Descripción de Campo */
    private VLookup	resource	= null;

    /** Descripción de Campo */
    private CLabel	resourceLabel	= new CLabel();

    /** Descripción de Campo */
    private VDate	dateFrom	= new VDate("DateFrom", true, false, true, DisplayType.Date, "DateFrom");

    /** Descripción de Campo */
    private CLabel	dateFromLabel	= new CLabel();

    /** Descripción de Campo */
    private int	AD_Client_ID	= Integer.parseInt(Env.getContext(Env.getCtx(), "#AD_Client_ID"));

    // private DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    // private JFreeChart chart;

    /** Descripción de Campo */
    private ChartPanel	chartPanel	= new ChartPanel(createChart(new DefaultCategoryDataset(), ""), false);

    // private CPanel chart = new CPanel();

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {

        northPanel.setLayout(new java.awt.GridBagLayout());
        resourceLabel.setText(Msg.translate(Env.getCtx(), "S_Resource_ID"));
        northPanel.add(resourceLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(resource, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        dateFromLabel.setText(Msg.translate(Env.getCtx(), "DateFrom"));
        northPanel.add(dateFromLabel, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        northPanel.add(dateFrom, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
        chartPanel.setPreferredSize(new Dimension(750, 550));
        centerPanel.add(chartPanel, BorderLayout.CENTER);
        confirmPanel.addActionListener(this);
    }

    /**
     *      Fill Picks
     *              Column_ID from C_Order
     *  @throws Exception if Lookups cannot be initialized
     */
    private void fillPicks() throws Exception {

        Properties	ctx	= Env.getCtx();

        // createChart(dataset);
        MLookup	resourceL	= MLookupFactory.get(ctx, m_WindowNo, 0, 1000148, DisplayType.TableDir);

        resource	= new VLookup("S_Resource_ID", false, false, true, resourceL);

    }		// fillPicks

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals(ConfirmPanel.A_OK)) {

            Timestamp	date		= (Timestamp) dateFrom.getValue();
            int		S_Resource_ID	= ((Integer) resource.getValue()).intValue();

            System.out.println("ConfirmPanel.A_OK");
            System.out.println("date" + date + " S_Resource_ID " + S_Resource_ID);

            if ((date != null) && (S_Resource_ID != 0)) {

                System.out.println("Call createDataset(date,S_Resource_ID)");

                MResource	r	= new MResource(Env.getCtx(), S_Resource_ID, null);
                CategoryDataset	dataset	= createDataset(date, r);

                // CategoryDataset dataset = createDataset();
                System.out.println("dataset.getRowCount:" + dataset.getRowCount());

                String	title	= (r.getName() != null)
                                  ? r.getName()
                                  : "";

                title	= (title + " " + r.getDescription() != null)
                          ? r.getDescription()
                          : "";

                JFreeChart	jfreechart	= createChart(dataset, title);

                centerPanel.removeAll();
                chartPanel	= new ChartPanel(jfreechart, false);
                centerPanel.add(chartPanel, BorderLayout.CENTER);
                centerPanel.setVisible(true);
                m_frame.pack();
            }
        }

        if (e.getActionCommand().equals(ConfirmPanel.A_CANCEL)) {
            dispose();
        }
    }

    /**
     *      Dispose
     */
    public void dispose() {

        if (m_frame != null) {
            m_frame.dispose();
        }

        m_frame	= null;

    }		// dispose

    /**
     * Descripción de Método
     *
     *
     * @param dataset
     * @param title
     *
     * @return
     */
    private JFreeChart createChart(CategoryDataset dataset, String title) {

        JFreeChart	chart	= null; /* = ChartFactory.createBarChart3D(title, Msg.translate(Env.getCtx(), "Days"),	// X-Axis label
            Msg.translate(Env.getCtx(), "Hours"),	// Y-Axis label
            dataset,					// Dataset
            PlotOrientation.VERTICAL,			// orientation
            true,					// include legend
            true,					// tooltips?
            false					// URLs?
                );
*/
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        /*
         * // get a reference to the plot for further customisation...
         * CategoryPlot plot = chart.getCategoryPlot();
         * plot.setBackgroundPaint(Color.lightGray);
         * plot.setDomainGridlinePaint(Color.white);
         * plot.setDomainGridlinesVisible(true);
         * plot.setRangeGridlinePaint(Color.white);
         *
         * // set the range axis to display integers only...
         * final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
         * rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
         *
         * // disable bar outlines...
         * BarRenderer renderer = (BarRenderer) plot.getRenderer();
         * renderer.setDrawBarOutline(false);
         *
         * // set up gradient paints for series...
         * GradientPaint gp0 = new GradientPaint(
         *   0.0f, 0.0f, Color.blue,
         *   0.0f, 0.0f, new Color(0, 0, 64)
         * );
         * GradientPaint gp1 = new GradientPaint(
         *   0.0f, 0.0f, Color.green,
         *   0.0f, 0.0f, new Color(0, 64, 0)
         * );
         * GradientPaint gp2 = new GradientPaint(
         *   0.0f, 0.0f, Color.red,
         *   0.0f, 0.0f, new Color(64, 0, 0)
         * );
         * renderer.setSeriesPaint(0, gp0);
         * renderer.setSeriesPaint(1, gp1);
         * renderer.setSeriesPaint(2, gp2);
         *
         * CategoryAxis domainAxis = plot.getDomainAxis();
         * domainAxis.setCategoryLabelPositions(
         *   CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
         * );
         */

        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;
    }

    /**
     * Descripción de Método
     *
     *
     * @param start
     * @param r
     *
     * @return
     */
    public CategoryDataset createDataset(Timestamp start, MResource r) {

        // System.out.println("Create new data set");
        GregorianCalendar	gc1	= new GregorianCalendar();

        gc1.setTimeInMillis(start.getTime());
        gc1.clear(Calendar.MILLISECOND);
        gc1.clear(Calendar.SECOND);
        gc1.clear(Calendar.MINUTE);
        gc1.clear(Calendar.HOUR_OF_DAY);

        Timestamp	date		= start;
        String		namecapacity	= Msg.translate(Env.getCtx(), "Capacity");
        String		nameload	= Msg.translate(Env.getCtx(), "Load");
        String		namesummary	= Msg.translate(Env.getCtx(), "Summary");
        MResourceType	t		= new MResourceType(Env.getCtx(), r.getS_ResourceType_ID(), null);
        int	days	= 1;
        long	hours	= 0;

        if (t.isTimeSlot()) {
            hours	= MMPCMRP.getHoursAvailable(t.getTimeSlotStart(), t.getTimeSlotEnd());
        } else {
            hours	= 24;
        }

        DefaultCategoryDataset	dataset	= new DefaultCategoryDataset();

        // Long Hours = new Long(hours);
        int	C_UOM_ID	= DB.getSQLValue(null, "SELECT C_UOM_ID FROM M_Product WHERE S_Resource_ID = ? ", r.getS_Resource_ID());
        MUOM	uom	= new MUOM(Env.getCtx(), C_UOM_ID, null);

        // System.out.println("um.isHour()"+ uom.isHour() );
        if (!uom.isHour()) {
            return dataset;
        }

        int	summary	= 0;

        while (days < 32) {

            // System.out.println("Day Number" + days);
        	Calendar cld = Calendar.getInstance();
			cld.setTime(date);
            String	day	= new String(new Integer(cld.get(Calendar.DAY_OF_MONTH)).toString());
            int		seconds	= getLoad(r.getS_Resource_ID(), date, date);
            Long	Hours	= new Long(hours);

            switch (gc1.get(Calendar.DAY_OF_WEEK)) {

            case Calendar.SUNDAY :
                days++;

                if (t.isOnSunday()) {		// System.out.println("si Sunday");

                    // Msg.translate(Env.getCtx(), "OnSunday");
                    dataset.addValue(hours, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary + Hours.intValue() - (seconds / 3600);	// + (Hours.intValue() - ((seconds / 3600)));
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                } else {	// System.out.println("no Sunday");

                    // String day = Msg.translate(Env.getCtx(), "OnSunday") ;
                    dataset.addValue(0, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                }
            case Calendar.MONDAY :
                days++;

                if (t.isOnMonday()) {		// System.out.println("si Monday");

                    // String day = Msg.translate(Env.getCtx(), "OnMonday") ;
                    dataset.addValue(hours, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary + Hours.intValue() - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                } else {

                    // System.out.println("no Monday");
                    // String day = Msg.translate(Env.getCtx(), "OnMonday")  ;
                    dataset.addValue(0, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                }
            case Calendar.TUESDAY :
                days++;

                if (t.isOnTuesday()) {		// System.out.println("si TuesDay");

                    // String day = Msg.translate(Env.getCtx(), "OnTuesday");
                    dataset.addValue(hours, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary + Hours.intValue() - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                } else {

                    // System.out.println("no TuesDay");
                    // String day = Msg.translate(Env.getCtx(), "OnTuesday");
                    dataset.addValue(0, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                }
            case Calendar.WEDNESDAY :
                days++;

                if (t.isOnWednesday()) {

                    // String day = Msg.translate(Env.getCtx(), "OnWednesday");
                    dataset.addValue(hours, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary + Hours.intValue() - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                } else {

                    // String day = Msg.translate(Env.getCtx(), "OnWednesday");
                    dataset.addValue(0, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                }
            case Calendar.THURSDAY :
                days++;

                if (t.isOnThursday()) {

                    // String day = Msg.translate(Env.getCtx(), "OnThursday");
                    dataset.addValue(hours, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary + Hours.intValue() - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                } else {

                    // String day = Msg.translate(Env.getCtx(), "OnThursday");
                    dataset.addValue(0, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                }
            case Calendar.FRIDAY :
                days++;

                if (t.isOnFriday()) {

                    // String day = Msg.translate(Env.getCtx(), "OnFriday");
                    dataset.addValue(hours, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary + Hours.intValue() - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                } else {

                    // String day = Msg.translate(Env.getCtx(), "OnFriday");
                    dataset.addValue(0, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                }
            case Calendar.SATURDAY :
                days++;

                if (t.isOnSaturday()) {

                    // String day = Msg.translate(Env.getCtx(), "OnSaturday");
                    dataset.addValue(hours, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary + Hours.intValue() - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                } else {

                    // String day = Msg.translate(Env.getCtx(), "OnSaturday");
                    dataset.addValue(0, namecapacity, day);
                    dataset.addValue(seconds / 3600, nameload, day);
                    dataset.addValue(summary, namesummary, day);
                    summary	= summary - (seconds / 3600);
                    gc1.add(Calendar.DATE, 1);
                    date	= new Timestamp(gc1.getTimeInMillis());

                    break;
                }
            }
        }

        return dataset;
    }

    /**
     * Descripción de Método
     *
     *
     * @param S_Resource_ID
     * @param start
     * @param end
     *
     * @return
     */
    int getLoad(int S_Resource_ID, Timestamp start, Timestamp end) {

        int	load	= 0;

        // String sql = "SELECT SUM( CASE WHEN ow.DurationUnit = 's'  THEN 1 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'm' THEN 60 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'h'  THEN 3600 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'Y'  THEN 31536000 *  (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'M' THEN 2592000 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'D' THEN 86400 END ) AS Load FROM MPC_Order_Node onode INNER JOIN MPC_Order_Workflow ow ON (ow.MPC_Order_Workflow_ID =  onode.MPC_Order_Workflow_ID) INNER JOIN MPC_Order o ON (o.MPC_Order_ID = onode.MPC_Order_ID)  WHERE onode.S_Resource_ID = ?  AND onode.AD_Client_ID = ? AND  ? BETWEEN onode.DateStartSchedule AND onode.DateFinishSchedule" ;
        String	sql	= "SELECT SUM( CASE WHEN ow.DurationUnit = 's'  THEN 1 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'm' THEN 60 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'h'  THEN 3600 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'Y'  THEN 31536000 *  (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'M' THEN 2592000 * (onode.QueuingTime + onode.SetupTime + (onode.Duration * (o.QtyOrdered - o.QtyDelivered - o.QtyScrap)) + onode.MovingTime + onode.WaitingTime) WHEN ow.DurationUnit = 'D' THEN 86400 END ) AS Load FROM MPC_Order_Node onode INNER JOIN MPC_Order_Workflow ow ON (ow.MPC_Order_Workflow_ID =  onode.MPC_Order_Workflow_ID) INNER JOIN MPC_Order o ON (o.MPC_Order_ID = onode.MPC_Order_ID)  WHERE onode.S_Resource_ID = ?  AND onode.AD_Client_ID = ? AND  TRUNC(onode.DateStartSchedule) = ?";

        System.out.println("SQL :" + sql);

        try {

            PreparedStatement	pstmt	= null;

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, S_Resource_ID);
            pstmt.setInt(2, AD_Client_ID);
            pstmt.setTimestamp(3, start);

            ResultSet	rs	= pstmt.executeQuery();

            while (rs.next()) {
                load	= rs.getInt(1);
            }

            rs.close();
            pstmt.close();

            return load;

        } catch (Exception e) {
            log.log(Level.SEVERE, "doIt - " + sql, e);
        }

        return 0;
    }
}



/*
 * @(#)VCRP.java   14.jun 2007
 * 
 *  Fin del fichero VCRP.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
