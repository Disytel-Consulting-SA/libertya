/*
 * @(#)VQueryBuilder.java   14.jun 2007  Versión 2.2
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

import nickyb.fqb.*;
import nickyb.fqb.ext.ViewBuildReport;
import nickyb.fqb.util.*;
import nickyb.fqb.runtime.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Hashtable;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.compiere.swing.CPanel;
import org.compiere.plaf.*;
import org.compiere.swing.*;

import org.openXpertya.apps.*;
import org.openXpertya.apps.form.FormFrame;
import org.openXpertya.apps.form.FormPanel;
import org.openXpertya.db.*;
import org.openXpertya.impexp.*;
import org.openXpertya.minigrid.*;
import org.openXpertya.model.*;
import org.openXpertya.print.*;
import org.openXpertya.process.*;
import org.openXpertya.util.*;

/**
 * Descripción de Clase
 *
 *
 * @version 2.2, 14.jun 2007
 * @autor     Fundesle    
 */
public class VQueryBuilder extends CPanel implements FormPanel, ActionListener {

    /** Descripción de Campo */
    private static CLogger	log	= CLogger.getCLogger(VQueryBuilder.class);

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

        log.info("VQueryBuilder.init");
        m_WindowNo	= WindowNo;
        m_frame		= frame;

        try {

            CConnection	conn	= CConnection.get();

            querybuilder	= new SystemWindow("oracle.jdbc.OracleDriver", conn.getConnectionURL(), conn.getDbUid(), conn.getDbPwd());
            jbInit();

            /* dynInit(); */
            frame.getContentPane().add(northPanel, BorderLayout.NORTH);
            frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
            frame.getContentPane().add(confirmPanel, BorderLayout.SOUTH);
            frame.pack();
            frame.m_maximize	= true;
            frame.setMaximize(true);

        } catch (Exception e) {
            log.log(Level.SEVERE, "VFileImport.init", e);
        }

    }		// init

    /** Descripción de Campo */
    SystemWindow	querybuilder	= null;

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

    /**
     * Descripción de Método
     *
     *
     * @throws Exception
     */
    private void jbInit() throws Exception {

        centerPanel.setLayout(centerLayout);
        centerPanel.add(querybuilder, BorderLayout.CENTER);
    }

    /**
     * Descripción de Método
     *
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand().equals(ConfirmPanel.A_OK)) {

            // System.out.println("Comfirm panel OK ");
            m_frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            confirmPanel.setEnabled(false);
            m_frame.setBusy(true);

            String	QueryS		= "";
            String	QueryP		= "";
            String	st1		= "";
            String	st2		= "";
            boolean	IsDirectPrint	= false;

            QueryS	= Env.getContext(Env.getCtx(), "Peticion");

            for (int i = 0; i < QueryS.length(); i++) {

                String	temp	= "";
                int	j1	= 0;

                if ((QueryS.charAt(i) == 'A') && (QueryS.charAt(i + 1) == 'S') && (QueryS.charAt(i - 1) == ' ') && (QueryS.charAt(i + 2) == ' ')) {

                    for (int j = i + 3; j < QueryS.length(); j++) {

                        if ((QueryS.charAt(j) == ',') || (QueryS.charAt(j) == ' ')) {
                            break;
                        } else {
                            temp	= temp + QueryS.charAt(j);
                        }

                        j1	= j - 1;
                    }

                    if (hash.get(temp) == null) {
                        hash.put(temp, temp);
                    } else {

                        hash.put(temp + 2, temp);
                        st1	= QueryS.substring(0, j1) + "2";
                        st2	= QueryS.substring(j1, QueryS.length());
                    }
                }
            }

            System.out.println("String corregido por hashtable " + st1 + st2);
            System.out.println("hash table ************* " + hash.toString());
            System.out.println("view query ********** " + Env.getContext(Env.getCtx(), "Query"));

            String	Dialog	= "Nombre de la Vista";
            String	queryss	= QueryS.substring(7);
            String	querya	= "";

            for (int i = 0; i < queryss.length(); i++) {

                if (queryss.charAt(i) != '.') {
                    querya	= querya + queryss.charAt(i);
                } else {
                    break;
                }
            }

            System.out.println("QUERYACUM ******" + querya);

            String	VName		= "";
            int		AD_Table_ID	= 0;

            VName	= JOptionPane.showInputDialog(Dialog);
            QueryS	= "CREATE OR REPLACE VIEW " + VName + " AS Select " + querya + ".AD_Client_ID," + querya + ".AD_Org_ID," + querya + ".Created," + querya + ".CreatedBy," + querya + ".IsActive," + querya + ".Updated," + querya + ".UpdatedBy," + QueryS.substring(7);
            QueryP	= "SELECT AD_Client_ID,AD_Org_ID,Created,CreatedBy,IsActive,Updated,UpdatedBy,DocumentNo FROM C_Order";
            System.out.println("create view ********** " + QueryS);
            System.out.println("Bien ******" + DB.executeUpdate(QueryS, "AD_Table"));

            try {

                String	sql	= "SELECT AD_Table_ID From AD_Table Where TableName='" + VName + "'";
                PreparedStatement	pstmt	= null;

                pstmt	= DB.prepareStatement(sql);

                ResultSet	rs	= pstmt.executeQuery();

                while (rs.next()) {
                    AD_Table_ID	= rs.getInt(1);
                }

                rs.close();
                pstmt.close();
                pstmt	= null;

            } catch (SQLException exc) {}

            // System.out.println("Dialog " +JOptionPane);
            M_Table	RV_Table	= new M_Table(Env.getCtx(), AD_Table_ID, null);

            RV_Table.setName(VName);
            RV_Table.setTableName(VName);
            RV_Table.setIsView(true);
            RV_Table.setAccessLevel(RV_Table.ACCESSLEVEL_ClientPlusOrganization);
            RV_Table.setReplicationType(RV_Table.REPLICATIONTYPE_Local);
            RV_Table.save();

            int	Seq	= 0;

            try {

                String	sqlt	= "SELECT AD_Table_ID From AD_Table Where TableName='" + VName + "'";
                PreparedStatement	pstmtt	= null;

                pstmtt	= DB.prepareStatement(sqlt);

                ResultSet	rst	= pstmtt.executeQuery();

                while (rst.next()) {

                    Seq	= rst.getInt(1);

                    String	USequence	= "Update AD_Table set AD_Client_ID=0, AD_Org_ID=0 where AD_Table_ID=" + Seq;

                    System.out.println("Sentencia " + USequence);
                    DB.executeUpdate(USequence, "AD_Table");
                    DB.commit(true, "AD_Table");
                }

                rst.close();
                pstmtt.close();
                pstmtt	= null;

                String	Columns	= "SELECT  Column_Name, DECODE(Data_Type, 'NUMBER', 12, 'CHAR', 20, 'DATE', 15, 10), COALESCE(Char_Col_Decl_Length, Data_Length) AS Data_Length,Nullable, AD_Table_ID,   Table_Name, 'U' FROM    User_Tab_Columns uc, AD_Table t WHERE   uc.Table_Name=UPPER(t.TableName) AND NOT EXISTS (SELECT * FROM AD_Table t, AD_Column c WHERE t.AD_Table_ID=c.AD_Table_ID AND uc.Table_Name=UPPER(t.TableName) AND uc.Column_Name=UPPER(c.ColumnName)) and  t.AD_Table_ID=" + Seq;

                System.out.println("Columnas tabla " + Columns);

                PreparedStatement	pstmtc	= null;

                pstmtc	= DB.prepareStatement(Columns);

                ResultSet	rsc	= pstmtc.executeQuery();

                while (rsc.next()) {

                    String	SqlE	= "Select AD_Element_ID from AD_Element where ColumnName='" + rsc.getString(1) + "'";

                    System.out.println("Elemento " + SqlE + " de columna " + rsc.getString(1));

                    PreparedStatement	pstmte	= null;

                    pstmte	= DB.prepareStatement(SqlE);

                    ResultSet	rse		= pstmte.executeQuery();
                    int		AD_Element_ID	= 0;

                    while (rse.next()) {
                        AD_Element_ID	= rse.getInt(1);
                    }

                    rse.close();
                    pstmte.close();
                    pstmte	= null;

                    String	Ccolumns	= "Select ColumnName, AD_Element_ID, Name, AD_Reference_ID,AD_Table_ID, AD_Val_Rule_ID,AD_Reference_Value_ID, IsMandatory, IsUpdateable FROM AD_Column WHERE UPPER(ColumnName)=UPPER('" + rsc.getString(1) + "')";

                    System.out.println("Columna " + Ccolumns + " de columna " + rsc.getString(1));

                    PreparedStatement	pstmtcc	= null;

                    pstmtcc	= DB.prepareStatement(Ccolumns);

                    ResultSet	rscc	= pstmtcc.executeQuery();

                    if (rscc.next()) {

                        System.out.println(" de columna " + rscc.getString(1) + " y tabla " + Seq);

                        M_Column	RV_Columns	= new M_Column(Env.getCtx(), 0, null);

                        RV_Columns.setAD_Table_ID(Seq);
                        RV_Columns.setColumnName(rscc.getString(1));
                        RV_Columns.setName(rscc.getString(3));
                        RV_Columns.setAD_Element_ID(rscc.getInt(2));
                        RV_Columns.setAD_Reference_ID(rscc.getInt(4));
                        RV_Columns.setEntityType("U");
                        RV_Columns.setVersion(Env.ONE);
                        RV_Columns.setFieldLength(rsc.getInt(3));

//                      if (rscc.g(8)=='Y')
//                              RV_Columns.setIsMandatory(true);
//                      if (rscc.getString(9).equals('Y'))
//                              RV_Columns.setIsUpdateble(true);

                        if (rscc.getInt(6) != 0) {
                            RV_Columns.setAD_Val_Rule_ID(rscc.getInt(6));
                        }

                        if (rscc.getInt(7) != 0) {
                            RV_Columns.setAD_Reference_Value_ID(rscc.getInt(7));
                        }

                        RV_Columns.save();

                    } else {

                        M_Column	RV_Columns	= new M_Column(Env.getCtx(), 0, null);

                        RV_Columns.setAD_Table_ID(Seq);
                        RV_Columns.setColumnName(rsc.getString(1));
                        RV_Columns.setName(rsc.getString(1));
                        RV_Columns.setVersion(Env.ONE);

                        if (AD_Element_ID != 0) {
                            RV_Columns.setAD_Element_ID(AD_Element_ID);
                        }

                        RV_Columns.setAD_Reference_ID(rsc.getInt(2));
                        RV_Columns.setEntityType("U");
                        RV_Columns.setFieldLength(rsc.getInt(3));
                        RV_Columns.save();
                    }

                    rscc.close();
                    pstmtcc.close();
                    pstmtcc	= null;

                    String	UColumn	= "Update AD_Column set AD_Client_ID=0, AD_Org_ID=0 where AD_Table_ID=" + Seq;

                    System.out.println("Sentencia " + UColumn);
                    DB.executeUpdate(UColumn, "AD_Column");
                    DB.commit(true, "AD_Column");

//                  M_Column RV_Columns = new M_Column(Env.getCtx(),0);
//                  RV_Columns.setAD_Table_ID(rsc.getInt(5));
//                  RV_Columns.setColumnName(rsc.getString(1));
//                  RV_Columns.setName(rsc.getString(1));
//                  if (AD_Element_ID!=0)
//                          RV_Columns.setAD_Element_ID(AD_Element_ID);
//
//                  if (rsc.getString(1).endsWith("ID"))
//                  {
//                          RV_Columns.setAD_Reference_ID(19);
//                          if (rsc.getString(1).equalsIgnoreCase("AD_Org_ID"))
//                                  RV_Columns.setAD_Val_Rule_ID(104);
//
//                  }
//                  else if(rsc.getString(1).equalsIgnoreCase("CreatedBy") || rsc.getString(1).equalsIgnoreCase("UpdatedBy"))
//                  {
//                          RV_Columns.setAD_Reference_ID(18);
//                          RV_Columns.setAD_Table_ID(114);
//                  }
//                  else
//                          RV_Columns.setAD_Reference_ID(rsc.getInt(2));
//                  RV_Columns.setEntityType("U");
//                  RV_Columns.setFieldLength(rsc.getInt(3));
//                  RV_Columns.save();
                }

                rsc.close();
                pstmtc.close();
                pstmtc	= null;

                org.openXpertya.model.X_AD_ReportView	View	= new org.openXpertya.model.X_AD_ReportView(Env.getCtx(), 0, null);

                View.setName(VName);
                View.setAD_Table_ID(Seq);
                View.setEntityType("U");
                View.save();

                String	URView	= "Update AD_ReportView set AD_Client_ID=0, AD_Org_ID=0 where AD_Table_ID=" + Seq;

                System.out.println("Sentencia " + URView);
                DB.executeUpdate(URView, "AD_ReportView");
                DB.commit(true, "AD_ReportView");

                // Proceso
                org.openXpertya.model.MProcess	Process	= new org.openXpertya.model.MProcess(Env.getCtx(), 0, null);

                Process.setName(VName);
                Process.setValue(VName);
                Process.setIsReport(true);
                Process.setAccessLevel(Process.ACCESSLEVEL_ClientPlusOrganization);
                Process.setEntityType("U");

                String	SqlRV	= "Select AD_ReportView_ID from AD_ReportView where Name='" + VName + "'";

                System.out.println("Report View " + SqlRV);

                PreparedStatement	pstmtrv	= null;

                pstmtrv	= DB.prepareStatement(SqlRV);

                ResultSet	rsrv			= pstmtrv.executeQuery();
                int		AD_ReportView_ID	= 0;

                while (rsrv.next()) {
                    AD_ReportView_ID	= rsrv.getInt(1);
                }

                rsrv.close();
                pstmtrv.close();
                pstmtrv	= null;
                Process.setAD_ReportView_ID(AD_ReportView_ID);
                Process.save();

                String	UProcess	= "Update AD_Process set AD_Client_ID=0, AD_Org_ID=0 where Name='" + VName + "'";

                System.out.println("Sentencia " + UProcess);
                DB.executeUpdate(UProcess, "AD_Process");
                DB.commit(true, "AD_Process");

                // Menu
                org.openXpertya.model.MMenu	Menu	= new org.openXpertya.model.MMenu(Env.getCtx(), 0, null);

                Menu.setName(VName);
                Menu.setAction(Menu.ACTION_Process);

                String	SqlP	= "Select AD_Process_ID from AD_Process where Name='" + VName + "'";

                System.out.println("Process " + SqlP);

                PreparedStatement	pstmtp	= null;

                pstmtp	= DB.prepareStatement(SqlP);

                ResultSet	rsp		= pstmtp.executeQuery();
                int		AD_Process_ID	= 0;

                while (rsp.next()) {
                    AD_Process_ID	= rsp.getInt(1);
                }

                rsp.close();
                pstmtp.close();
                pstmtp	= null;
                Menu.setAD_Process_ID(AD_Process_ID);
                Menu.save();

                String	UMenu	= "Update AD_Menu set AD_Client_ID=0, AD_Org_ID=0 where Name='" + VName + "'";

                System.out.println("Sentencia " + UMenu);
                DB.executeUpdate(UMenu, "AD_Menu");
                DB.commit(true, "AD_Menu");

                // print

                /*
                 *                                        MPrintFormat format = null;
                 *                                   Language language = Language.getLoginLanguage();
                 *                                   System.out.println("valor del adreport " +AD_ReportView_ID);
                 *                                       format = MPrintFormat.get(Env.getCtx(), 0, Seq); // formato carga vimifos
                 *                                       //format.setLanguage(language);
                 *                                       //format.setTranslationLanguage(language);
                 *                                       System.out.println("valor del print format " +format.getAD_ReportView_ID());
                 *
                 *                                       MQuery query = new MQuery(Seq);
                 *                                       PrintInfo PInfo = new PrintInfo("Print",Seq,AD_ReportView_ID);
                 *                                       //      Engine
                 *                                       ReportEngine re = new ReportEngine(Env.getCtx(), format, query,PInfo);
                 *                                       //new Viewer(re);
                 *
                 *                                   if (IsDirectPrint)
                 *                                   {
                 *                                  // re.print (false, 1, false, re.getPrintFormat().getPrinterName()); //      prints only original
                 *                                       re.print();
                 *                                   //ReportEngine.printConfirm ( 1000282 , Record_ID);
                 *                                   }
                 *                                   else
                 *                                   new Viewer(re);
                 */

            } catch (SQLException exc) {}

            // RV_Table.;
            m_frame.setBusy(false);
            m_frame.dispose();

            //
            //                      SwingWorker worker = new SwingWorker()
            //                      {
            //                              public Object construct()
            //                          {
            //                              cmd_process();
            //                                      return Boolean.TRUE;
            //                              }
            //                      };
            //                      worker.start();
            // when you need the result:
            // x = worker.get();   //  this blocks the UI !!
        } else if (e.getActionCommand().equals(ConfirmPanel.A_CANCEL)) {
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
}



/*
 * @(#)VQueryBuilder.java   14.jun 2007
 * 
 *  Fin del fichero VQueryBuilder.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 14.jun 2007
