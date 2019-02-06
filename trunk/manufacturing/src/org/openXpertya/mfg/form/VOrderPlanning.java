/*
 * @(#)VOrderPlanning.java   14.jun 2007  Versión 2.2
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

import java.awt.*;
import java.awt.event.*;

import java.beans.*;

import java.math.*;

import java.sql.*;

import java.util.*;
import java.util.logging.*;

import javax.swing.*;
import javax.swing.event.*;

import openXpertya.model.*;

import org.compiere.plaf.*;
import org.compiere.swing.*;
import org.compiere.plaf.*;
import org.compiere.swing.*;

import org.openXpertya.apps.*;
import org.openXpertya.apps.form.*;
import org.openXpertya.apps.search.*;
import org.openXpertya.grid.*;
import org.openXpertya.grid.ed.*;
import org.openXpertya.minigrid.*;
import org.openXpertya.model.*;
import org.openXpertya.print.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 *
 * @author  vpj-cd
 */
public class VOrderPlanning extends CPanel implements FormPanel, ActionListener, VetoableChangeListener, ChangeListener, ListSelectionListener, TableModelListener, ASyncProcess {

    /** Creates new form VOrderPlanning */
    public VOrderPlanning() {
        initComponents();
    }

    /**
     *         Initialize Panel
     *     @param WindowNo window
     *     @param frame frame
     */
    public void init(int WindowNo, FormFrame frame) {

        m_WindowNo	= WindowNo;
        m_frame		= frame;

        // Log.trace(Log.l1_User, "VOrderReceipIssue.init - WinNo=" + m_WindowNo,
        // "AD_Client_ID=" + m_AD_Client_ID + ", AD_Org_ID=" + m_AD_Org_ID);
        Env.setContext(Env.getCtx(), m_WindowNo, "IsSOTrx", "N");

        try {

            // UI
            AD_Client_ID	= Env.getContextAsInt(Env.getCtx(), "#AD_Client_ID");
            fillPicks();
            jbInit();

            //
            dynInit();
            m_frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
            m_frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Info", e);
        }

    }		// init

    /** Window No */
    private int	m_WindowNo	= 0;

    /** FormFrame */
    private FormFrame	m_frame;

    /** Descripción de Campo */
    private StatusBar	statusBar	= new StatusBar();

    /** Descripción de Campo */
    private int	AD_Table_ID	= 1000018;

    /** Descripción de Campo */
    private M_Table	table	= null;

    /** Descripción de Campo */
    private int	AD_Window_ID	= 1000013;

    /** Descripción de Campo */
    private int	AD_Tab_ID	= 1000031;

    /** Descripción de Campo */
    private int	AD_Client_ID	= 0;

    /** Master (owning) Window */
    protected int	p_WindowNo;

    /** Table Name */
    private String	p_tableName	= getTableName();

    /** Key Column Name */
    protected String	p_keyColumn;

    /** Enable more than one selection */
    protected boolean	p_multiSelection	= true;

    /** Initial WHERE Clause */
    protected String	p_whereClause	= "";

    /** Table */
    protected MiniTable	p_table	= new MiniTable();

    /** Model Index of Key Column */
    private int	m_keyColumnIndex	= -1;

    /** OK pressed */
    private boolean	m_ok	= false;

    /** Cancel pressed - need to differentiate between OK - Cancel - Exit */
    private boolean	m_cancel	= false;

    /** Result IDs */
    private ArrayList	m_results	= new ArrayList(3);

    /** Layout of Grid */
    protected Info_Column[]	p_layout;

    /** Main SQL Statement */
    private String	m_sqlMain;

    /** Order By Clause */
    private String	m_sqlAdd;

    /** Descripción de Campo */
    protected int	row	= 0;

    /** Loading success indicator */
    protected boolean	p_loadedOK	= false;

    /** SO Zoom Window */
    private int	m_SO_Window_ID	= -1;

    /** PO Zoom Window */
    private int	m_PO_Window_ID	= -1;

    /** Worker */
    private Worker	m_worker	= null;

    /** Logger */
    protected CLogger	log	= CLogger.getCLogger(VOrderPlanning.class);

    /** Static Layout */
    private CPanel	southPanel	= new CPanel();

    /** Descripción de Campo */
    private BorderLayout	southLayout	= new BorderLayout();

    /** Descripción de Campo */
    ConfirmPanel	confirmPanel	= new ConfirmPanel(true, true, true, true, true, true, true);

    /** Descripción de Campo */
    protected CPanel	parameterPanel	= new CPanel();

    /** Descripción de Campo */
    private JScrollPane	scrollPane	= new JScrollPane();

    //

    /** Descripción de Campo */
    private JPopupMenu	popup	= new JPopupMenu();

    /** Descripción de Campo */
    private JMenuItem	calcMenu	= new JMenuItem();

    /** Window Width */
    static final int	INFO_WIDTH	= 800;

    // public IDColumn id = new IDColumn(0);
    // id.setSelected(true);

    /** Array of Column Info */
    private Info_Column[]	m_layout	= {

        // new ColumnInfo(" "," ", IDColumn.class, true, true, ""),
        new Info_Column(Msg.translate(Env.getCtx(), "Select"), p_tableName + ".MPC_Order_ID", IDColumn.class), new Info_Column(Msg.translate(Env.getCtx(), "DocumentNo"), p_tableName + ".DocumentNo", String.class), new Info_Column(Msg.translate(Env.getCtx(), "DocumentNo"), p_tableName + ".Line", Integer.class), new Info_Column(Msg.translate(Env.getCtx(), "M_Product_ID"), "(SELECT Name FROM M_Product p WHERE p.M_Product_ID=" + p_tableName + ".M_Product_ID)", String.class), new Info_Column(Msg.translate(Env.getCtx(), "C_UOM_ID"), "(SELECT Name FROM C_UOM u WHERE u.C_UOM_ID=" + p_tableName + ".C_UOM_ID)", String.class), new Info_Column(Msg.translate(Env.getCtx(), "QtyEntered"), p_tableName + ".QtyEntered", BigDecimal.class), new Info_Column(Msg.translate(Env.getCtx(), "QtyOrdered"), p_tableName + ".QtyOrdered", BigDecimal.class), new Info_Column(Msg.translate(Env.getCtx(), "DateOrdered"), p_tableName + ".DateOrdered", Timestamp.class), new Info_Column(Msg.translate(Env.getCtx(), "DateStartSchedule"), p_tableName + ".DateStartSchedule", Timestamp.class), new Info_Column(Msg.translate(Env.getCtx(), "DateFinishSchedule"), p_tableName + ".DateFinishSchedule", Timestamp.class)

        // new Info_Column(Msg.translate(Env.getCtx(), "DateOrdered"), "o.DatePromided", Timestamp.class),
        // new Info_Column(Msg.translate(Env.getCtx(), "ConvertedAmount"), "C_Base_Convert(o.GrandTotal,o.C_Currency_ID,o.AD_Client_ID,o.DateAcct, o.AD_Org_ID)", BigDecimal.class),
        // new Info_Column(Msg.translate(Env.getCtx(), "IsSOTrx"), "o.IsSOTrx", Boolean.class),
        // new Info_Column(Msg.translate(Env.getCtx(), "Description"), "o.Description", String.class),
        // new Info_Column(Msg.translate(Env.getCtx(), "POReference"), "o.POReference", String.class)
    };

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {	// GEN-BEGIN:initComponents

        mainPanel	= new javax.swing.JPanel();
        OrderPlanning	= new javax.swing.JTabbedPane();
        PanelOrder	= new javax.swing.JPanel();
        PanelFind	= new javax.swing.JPanel();
        PanelCenter	= new javax.swing.JPanel();
        PanelBottom	= new javax.swing.JPanel();
        Results		= new javax.swing.JPanel();
        setLayout(new java.awt.BorderLayout());
        mainPanel.setLayout(new java.awt.BorderLayout());
        PanelOrder.setLayout(new java.awt.BorderLayout());
        PanelOrder.add(PanelFind, java.awt.BorderLayout.NORTH);
        PanelOrder.add(PanelCenter, java.awt.BorderLayout.CENTER);
        PanelOrder.add(PanelBottom, java.awt.BorderLayout.SOUTH);
        OrderPlanning.addTab("Order", PanelOrder);
        OrderPlanning.addTab("Results", Results);
        mainPanel.add(OrderPlanning, java.awt.BorderLayout.CENTER);
        add(mainPanel, java.awt.BorderLayout.CENTER);

    }		// GEN-END:initComponents

    /**
     *      Static Init
     *  @throws Exception
     */
    protected void jbInit() throws Exception {

        // this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainPanel.setLayout(new java.awt.BorderLayout());
        setLayout(new java.awt.BorderLayout());
        southPanel.setLayout(southLayout);
        southPanel.add(confirmPanel, BorderLayout.CENTER);
        southPanel.add(statusBar, BorderLayout.SOUTH);

        /*
         * m_frame.getContentPane().add(southPanel, BorderLayout.SOUTH);
         * m_frame.getContentPane().add(parameterPanel, BorderLayout.NORTH);
         * m_frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
         */
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.add(parameterPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        /*
         * add(southPanel, BorderLayout.SOUTH);
         * add(parameterPanel, BorderLayout.NORTH);
         * add(scrollPane, BorderLayout.CENTER);
         */
        scrollPane.getViewport().add(p_table, null);

        //
        confirmPanel.addActionListener(this);
        confirmPanel.getResetButton().setVisible(hasReset());
        confirmPanel.getCustomizeButton().setVisible(hasCustomize());
        confirmPanel.getHistoryButton().setVisible(hasHistory());
        confirmPanel.getZoomButton().setVisible(hasZoom());

        //
        JButton	print	= ConfirmPanel.createPrintButton(true);

        print.addActionListener(this);
        confirmPanel.addButton(print);

        //
        popup.add(calcMenu);
        calcMenu.setText(Msg.getMsg(Env.getCtx(), "Calculator"));
        calcMenu.setIcon(new ImageIcon(org.openXpertya.OpenXpertya.class.getResource("images/Calculator16.gif")));
        calcMenu.addActionListener(this);

        //
        p_table.getSelectionModel().addListSelectionListener(this);

        /*
         * ListSelectionModel rowSM = p_table.getSelectionModel();
         * rowSM.addListSelectionListener(new ListSelectionListener() {
         * public void valueChanged(ListSelectionEvent e) {
         *   //Ignore extra messages.
         *   if (e.getValueIsAdjusting()) return;
         *
         *   ListSelectionModel lsm = (ListSelectionModel)e.getSource();
         *   if (lsm.isSelectionEmpty())
         *   {
         *       //int row = 0;
         *
         *   } else
         *   {
         *       int row = lsm.getMinSelectionIndex();
         *       System.out.println("Row Select" + row);
         *   }
         * }
         * });
         */
        enableButtons();
    }		// jbInit

    /**
     *  Dynamic Init.
     *  Table Layout, Visual, Listener
     */
    private void dynInit() {}

    /**
     *      Fill Picks
     *              Column_ID from C_Order
     *  @throws Exception if Lookups cannot be initialized
     */
    private void fillPicks() throws Exception {

        prepareTable(m_layout, getTableName(), " DocStatus='NA' " + find(), "2");
        executeQuery();
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        // Confirm Panel
        String	cmd	= e.getActionCommand();

        if (cmd.equals(ConfirmPanel.A_OK)) {

            m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            System.out.println("Row ...ID " + row);

            int	rows[]	= p_table.getSelectedRows();

            for (int r = 0; r < rows.length; r++) {

                IDColumn	id	= (IDColumn) p_table.getValueAt(rows[r], 0);

                if (id != null) {

                    Integer	MPC_Order_ID	= id.getRecord_ID();
                    int		C_DocType_ID	= MMPCMRP.getDocType("MOP", false);
                    MMPCOrder	order		= new MMPCOrder(Env.getCtx(), MPC_Order_ID.intValue(), null);

                    order.setDocStatus(order.prepareIt());
                    order.setDocAction(order.DOCACTION_Complete);
                    order.save();
                }
            }

            ADialog.info(m_WindowNo, this, "ProcessOK");
            executeQuery();
            m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            executeQuery();
        }
    }

    /**
     * Descripción de Método
     *
     */
    public void dispose() {

        if (m_frame != null) {
            m_frame.dispose();
        }

        m_frame	= null;
    }

    /**
     * Descripción de Método
     *
     *
     * @param processInfo
     */
    public void executeASync(org.openXpertya.process.ProcessInfo processInfo) {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    public boolean isUILocked() {
        return false;
    }

    /**
     * Descripción de Método
     *
     *
     * @param processInfo
     */
    public void lockUI(org.openXpertya.process.ProcessInfo processInfo) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void stateChanged(ChangeEvent e) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void tableChanged(TableModelEvent e) {}

    /**
     * Descripción de Método
     *
     *
     * @param processInfo
     */
    public void unlockUI(org.openXpertya.process.ProcessInfo processInfo) {}

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void valueChanged(ListSelectionEvent e) {}

    /**
     * Descripción de Método
     *
     *
     * @param evt
     *
     * @throws PropertyVetoException
     */
    public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {}

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    private String find() {

        // StringBuffer select =  new StringBuffer("");
        Find	find	= new Find(Env.getFrame(this), AD_Window_ID, this.getName(), AD_Table_ID, getTableName(), "", getFields(), 1);
        
        MQuery	query	= find.getQuery();

        // select.append("SELECT * FROM " + table.getName() + " WHERE " + query.toString());

        return query.getWhereClause();
    }

    /**
     * Descripción de Método
     *
     *
     * @return
     */
    private MField[] getFields() {

        ArrayList	list	= new ArrayList();
        M_Column[]	cols	= table.getColumns(true);

        for (int c = 0; c < cols.length; c++) {

            StringBuffer	sql	= new StringBuffer("SELECT * FROM AD_Column WHERE AD_Column_ID = " + cols[c].getAD_Column_ID());

            try {

                PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());
                ResultSet		rs	= pstmt.executeQuery();

                while (rs.next()) {

                    MFieldVO	vo	= MFieldVO.create(Env.getCtx(), m_WindowNo, AD_Tab_ID, AD_Window_ID, true, rs, false);
                    MField	field	= new MField(vo);

                    // System.out.println("Columna -------:" + field.getColumnName());
                    list.add(field);
                }

                rs.close();
                pstmt.close();

            } catch (SQLException e) {
                log.log(Level.SEVERE, "InfoGeneral.initInfoTable 1", e);
            }
        }

        //
        MField[]	lines	= new MField[list.size()];

        list.toArray(lines);

        return lines;
    }

    /**
     *  Reset Parameters
     *      To be overwritten by concrete classes
     */
    void doReset() {}

    /**
     *  Has Reset (false)
     *      To be overwritten by concrete classes
     *  @return true if it has reset (default false)
     */
    boolean hasReset() {
        return false;
    }

    /**
     *  History dialog
     *      To be overwritten by concrete classes
     */
    void showHistory() {}

    /**
     *  Has History (false)
     *      To be overwritten by concrete classes
     *  @return true if it has history (default false)
     */
    boolean hasHistory() {
        return false;
    }

    /**
     *  Customize dialog
     *      To be overwritten by concrete classes
     */
    void customize() {}

    /**
     *  Has Customize (false)
     *      To be overwritten by concrete classes
     *  @return true if it has customize (default false)
     */
    boolean hasCustomize() {
        return false;
    }

    /**
     *  Zoom action
     *      To be overwritten by concrete classes
     */
    void zoom() {}

    /**
     *  Has Zoom (false)
     *      To be overwritten by concrete classes
     *  @return true if it has zoom (default false)
     */
    boolean hasZoom() {
        return false;
    }

    /**
     *  Enable OK, History, Zoom if row selected
     */
    void enableButtons() {

        boolean	enable	= true;		// p_table.getSelectedRow() != -1;

        confirmPanel.getOKButton().setEnabled(true);

        if (hasHistory()) {
            confirmPanel.getHistoryButton().setEnabled(enable);
        }

        if (hasZoom()) {
            confirmPanel.getZoomButton().setEnabled(enable);
        }

    }		// enableButtons

    /**
     *  Execute Query
     */
    void executeQuery() {

        // ignore when running
        if ((m_worker != null) && m_worker.isAlive()) {
            return;
        }

        m_worker	= new Worker();
        m_worker.start();
    }		// executeQuery

    /**
     *  Prepare Table, Construct SQL (m_m_sqlMain, m_sqlAdd)
     *  and size Window
     *  @param layout layout array
     *  @param from from clause
     *  @param staticWhere where clause
     *  @param orderBy order by clause
     */
    protected void prepareTable(Info_Column[] layout, String from, String staticWhere, String orderBy) {

        p_layout	= layout;

        StringBuffer	sql	= new StringBuffer("SELECT ");

        // add columns & sql
        for (int i = 0; i < layout.length; i++) {

            if (i > 0) {
                sql.append(", ");
            }

            sql.append(layout[i].getColSQL());

            // adding ID column
            if (layout[i].isIDcol()) {
                sql.append(",").append(layout[i].getIDcolSQL());
            }

            // add to model
            p_table.addColumn(layout[i].getColHeader());

            if (layout[i].isColorColumn()) {
                p_table.setColorColumn(i);
            }

            if (layout[i].getColClass() == IDColumn.class) {
                m_keyColumnIndex	= i;
            }
        }

        // set editors (two steps)
        for (int i = 0; i < layout.length; i++) {
            p_table.setColumnClass(i, layout[i].getColClass(), layout[i].isReadOnly(), layout[i].getColHeader());
        }

        sql.append(" FROM ").append(from);

        //
        if (!staticWhere.equals("")) {
            sql.append(" WHERE ").append(staticWhere);
        }

        m_sqlMain	= sql.toString();
        m_sqlAdd	= "";

        if ((orderBy != null) && (orderBy.length() > 0)) {
            m_sqlAdd	= " ORDER BY " + orderBy;
        }

        if (m_keyColumnIndex == -1) {
            log.log(Level.SEVERE, "Info.prepareTable - No KeyColumn - " + sql);
        }

        // Table Selection
        p_table.setRowSelectionAllowed(true);

        // p_table.addMouseListener(this);
        p_table.setMultiSelection(false);
        p_table.setEditingColumn(0);
        p_table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Window Sizing
        parameterPanel.setPreferredSize(new Dimension(INFO_WIDTH, parameterPanel.getPreferredSize().height));
        scrollPane.setPreferredSize(new Dimension(INFO_WIDTH, 400));

    }		// prepareTable

    /**
     *  Get the key of currently selected row
     *  @return selected key
     */
    protected Integer getSelectedRowKey() {

        int	row	= p_table.getSelectedRow();

        if ((row != -1) && (m_keyColumnIndex != -1)) {

            Object	data	= p_table.getModel().getValueAt(row, m_keyColumnIndex);

            if (data instanceof IDColumn) {
                data	= ((IDColumn) data).getRecord_ID();
            }

            if (data instanceof Integer) {
                return (Integer) data;
            }
        }

        return null;

    }		// getSelectedRowKey

    /**
     *  Get Table name Synonym
     *  @return table name
     */
    String getTableName() {

        table		= new M_Table(Env.getCtx(), AD_Table_ID, "AD_Table");
        p_tableName	= table.getTableName();

        return p_tableName;

    }		// getTableName

    // Variables declaration - do not modify//GEN-BEGIN:variables

    /** Descripción de Campo */
    private javax.swing.JTabbedPane	OrderPlanning;

    /** Descripción de Campo */
    private javax.swing.JPanel	PanelBottom;

    /** Descripción de Campo */
    private javax.swing.JPanel	PanelCenter;

    /** Descripción de Campo */
    private javax.swing.JPanel	PanelFind;

    /** Descripción de Campo */
    private javax.swing.JPanel	PanelOrder;

    /** Descripción de Campo */
    private javax.swing.JPanel	Results;

    /** Descripción de Campo */
    private javax.swing.JPanel	mainPanel;

    // End of variables declaration//GEN-END:variables

    /**
     *      Worker
     */
    class Worker extends Thread {

        /**
         *  Do Work (load data)
         */
        public void run() {

            log.fine("Info.Worker.run");
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            // setStatusLine(Msg.getMsg(Env.getCtx(), "StartSearch"), false);

            // Clear Table
            p_table.setRowCount(0);

            //
            // System.out.println("########" + m_sqlMain);

            StringBuffer	sql		= new StringBuffer(m_sqlMain);
            String		dynWhere	= "";		// find ();

            if (dynWhere.length() > 0) {
                sql.append(dynWhere);		// includes first AND
            }

            sql.append(m_sqlAdd);

            String	xSql	= Msg.parseTranslation(Env.getCtx(), sql.toString());		// Variables

            xSql	= MRole.getDefault().addAccessSQL(xSql, getTableName(), MRole.SQL_FULLYQUALIFIED, MRole.SQL_RO);

            try {

                PreparedStatement	pstmt	= DB.prepareStatement(xSql);

                log.fine("SQL=" + xSql);

                // setParameters (pstmt);
                // Log.trace(Log.l6_Database, "Info.Worker.run - start query");
                ResultSet	rs	= pstmt.executeQuery();

                // Log.trace(Log.l6_Database, "Info.Worker.run - end query");
                while (!isInterrupted() & rs.next()) {

                    int	row	= p_table.getRowCount();

                    p_table.setRowCount(row + 1);

                    int	colOffset	= 1;	// columns start with 1

                    for (int col = 0; col < p_layout.length; col++) {

                        Object	data		= null;
                        Class	c		= p_layout[col].getColClass();
                        int	colIndex	= col + colOffset;

                        if (c == IDColumn.class) {

                            IDColumn	id	= new IDColumn(rs.getInt(colIndex));

                            id.setSelected(true);

                            // data = new IDColumn(rs.getInt(colIndex));
                            data	= id;
                            p_table.setColumnReadOnly(0, false);

                            // p_table.setColumnReadOnly(1, false);

                        } else if (c == Boolean.class) {
                            data	= new Boolean("Y".equals(rs.getString(colIndex)));
                        } else if (c == Timestamp.class) {
                            data	= rs.getTimestamp(colIndex);
                        } else if (c == BigDecimal.class) {
                            data	= rs.getBigDecimal(colIndex);
                        } else if (c == Double.class) {
                            data	= new Double(rs.getDouble(colIndex));
                        } else if (c == Integer.class) {
                            data	= new Integer(rs.getInt(colIndex));
                        } else if (c == KeyNamePair.class) {

                            String	display	= rs.getString(colIndex);
                            int		key	= rs.getInt(colIndex + 1);

                            data	= new KeyNamePair(key, display);
                            colOffset++;

                        } else {
                            data	= rs.getString(colIndex);
                        }

                        // store
                        p_table.setValueAt(data, row, col);

                        // Log.trace(Log.l6_Database, "r=" + row + ", c=" + col + " " + m_layout[col].getColHeader(),
                        // "data=" + data.toString() + " " + data.getClass().getName() + " * " + m_table.getCellRenderer(row, col));
                    }
                }

                log.config("Interrupted=" + isInterrupted());
                rs.close();
                pstmt.close();

            } catch (SQLException e) {
                log.log(Level.SEVERE, xSql, e);
            }

            p_table.autoSize();

            //
            setCursor(Cursor.getDefaultCursor());

            int	no	= p_table.getRowCount();

            // setStatusLine(Integer.toString(no) + " " + Msg.getMsg(Env.getCtx(), "SearchRows_EnterQuery"), false);
            // setStatusDB(Integer.toString(no));

        }	// run
    }		// Worker
}



/*
 * @(#)VOrderPlanning.java   14.jun 2007
 * 
 *  Fin del fichero VOrderPlanning.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
