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



package org.openXpertya.model;

import java.awt.Color;
import java.util.Enumeration;
import java.util.logging.Level;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.openXpertya.util.CLogger;
import org.openXpertya.util.Env;
import org.openXpertya.wf.MWFNode;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public final class MTreeNode extends DefaultMutableTreeNode {

    /**
     * Constructor de la clase ...
     *
     *
     * @param node_ID
     * @param seqNo
     * @param name
     * @param description
     * @param parent_ID
     * @param isSummary
     * @param imageIndicator
     * @param onBar
     * @param color
     */

    public MTreeNode( int node_ID,int seqNo,String name,String description,int parent_ID,boolean isSummary,String imageIndicator,boolean onBar,Color color ) {
        super();

        // log.fine( "MTreeNode Node_ID=" + node_ID + ", Parent_ID=" + parent_ID + " - " + name);

        m_node_ID     = node_ID;
        m_seqNo       = seqNo;
        m_name        = name;
        m_description = description;

        if( m_description == null ) {
            m_description = "";
        }

        m_parent_ID = parent_ID;
        setSummary( isSummary );
        setImageIndicator( imageIndicator );
        m_onBar = onBar;
        m_color = color;
    }    // MTreeNode

    public MTreeNode( int node_ID,int seqNo,String name,String description,int parent_ID,boolean isSummary,String imageIndicator,boolean onBar,Color color, Icon aIcon ) {
        super();

        // log.fine( "MTreeNode Node_ID=" + node_ID + ", Parent_ID=" + parent_ID + " - " + name);

        m_node_ID     = node_ID;
        m_seqNo       = seqNo;
        m_name        = name;
        m_description = description;

        if( m_description == null ) {
            m_description = "";
        }

        m_parent_ID = parent_ID;
        setSummary( isSummary );
        setImageIndicator( imageIndicator );
        myIcon = aIcon;
        m_onBar = onBar;
        m_color = color;
    }    //    
    
    /** Descripción de Campos */

    private int m_node_ID;

    /** Descripción de Campos */

    private int m_seqNo;

    /** Descripción de Campos */

    private String m_name;

    /** Descripción de Campos */

    private String m_description;

    /** Descripción de Campos */

    private int m_parent_ID;

    /** Descripción de Campos */

    private boolean m_isSummary;

    /** Descripción de Campos */

    private String m_imageIndicator;

    private Icon myIcon;
    
    /** Descripción de Campos */

    private int m_imageIndex = 0;

    /** Descripción de Campos */

    private boolean m_onBar;

    /** Descripción de Campos */

    private Color m_color;

    /** Descripción de Campos */

    private static CLogger log = CLogger.getCLogger( MTreeNode.class );

    /** Descripción de Campos */

    public static int TYPE_WINDOW = 1;

    /** Descripción de Campos */

    public static int TYPE_REPORT = 2;

    /** Descripción de Campos */

    public static int TYPE_PROCESS = 3;

    /** Descripción de Campos */

    public static int TYPE_WORKFLOW = 4;

    /** Descripción de Campos */

    public static int TYPE_WORKBENCH = 5;

    /** Descripción de Campos */

    public static int TYPE_SETVARIABLE = 6;

    /** Descripción de Campos */

    public static int TYPE_USERCHOICE = 7;

    /** Descripción de Campos */

    public static int TYPE_DOCACTION = 8;

    /** Descripción de Campos */

    public static Icon[] IMAGES = new Icon[] {
        null,Env.getImageIcon( "mWindow.gif" ),Env.getImageIcon( "mReport.gif" ),Env.getImageIcon( "mProcess.gif" ),Env.getImageIcon( "mWorkFlow.gif" ),Env.getImageIcon( "mWorkbench.gif" ),Env.getImageIcon( "mSetVariable.gif" ),Env.getImageIcon( "mUserChoice.gif" ),Env.getImageIcon( "mDocAction.gif" )
    };

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getNode_ID() {
        return m_node_ID;
    }    // getID

    /**
     * Descripción de Método
     *
     *
     * @param name
     */

    public void setName( String name ) {
        if( name == null ) {
            m_name = "";
        } else {
            m_name = name;
        }
    }    // setName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getName() {
        return m_name;
    }    // setName

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getSeqNo() {
        String retValue = "0000" + m_seqNo;    // not more than 100,000 nodes

        if( m_seqNo > 99999 ) {
            log.log( Level.SEVERE,"MTreeNode.getIndex - TreeNode Index is higher than 99999" );
        }

        if( retValue.length() > 5 ) {
            retValue = retValue.substring( retValue.length() - 5 );    // last 5
        }

        return retValue;
    }    // getSeqNo

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getParent_ID() {
        return m_parent_ID;
    }    // getParent

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String toString() {
        return    // m_node_ID + "/" + m_parent_ID + " " + m_seqNo + " - " +
            m_name;
    }             // toString

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getDescription() {
        return m_description;
    }    // getDescription

    /**
     * Descripción de Método
     *
     *
     * @param isSummary
     */

    public void setSummary( boolean isSummary ) {
        m_isSummary = isSummary;
        super.setAllowsChildren( isSummary );
    }    // setSummary

    /**
     * Descripción de Método
     *
     *
     * @param isSummary
     */

    public void setAllowsChildren( boolean isSummary ) {
        super.setAllowsChildren( isSummary );
        m_isSummary = isSummary;
    }    // setAllowsChildren

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isSummary() {
        return m_isSummary;
    }    // isSummary

    /**
     * Descripción de Método
     *
     *
     * @param imageIndicator
     *
     * @return
     */

    public static int getImageIndex( String imageIndicator ) {
        int imageIndex = 0;

        if( imageIndicator == null ) {
            ;
        } else if( imageIndicator.equals( MWFNode.ACTION_UserWindow )       // Window
                   || imageIndicator.equals( MWFNode.ACTION_UserForm )) {
            imageIndex = TYPE_WINDOW;
        } else if( imageIndicator.equals( MWFNode.ACTION_AppsReport )) {    // Report
            imageIndex = TYPE_REPORT;
        } else if( imageIndicator.equals( MWFNode.ACTION_AppsProcess )    // Process
                   || imageIndicator.equals( MWFNode.ACTION_AppsTask )) {
            imageIndex = TYPE_PROCESS;
        } else if( imageIndicator.equals( MWFNode.ACTION_SubWorkflow )) {    // WorkFlow
            imageIndex = TYPE_WORKFLOW;
        } else if( imageIndicator.equals( MWFNode.ACTION_UserWorkbench )) {    // Workbench
            imageIndex = TYPE_WORKBENCH;
        } else if( imageIndicator.equals( MWFNode.ACTION_SetVariable )) {    // Set Variable
            imageIndex = TYPE_SETVARIABLE;
        } else if( imageIndicator.equals( MWFNode.ACTION_UserChoice )) {    // User Choice
            imageIndex = TYPE_USERCHOICE;
        } else if( imageIndicator.equals( MWFNode.ACTION_DocumentAction )) {    // Document Action
            imageIndex = TYPE_DOCACTION;
        } else if( imageIndicator.equals( MWFNode.ACTION_WaitSleep )) {    // Sleep
            ;
        }

        return imageIndex;
    }    // getImageIndex

    /**
     * Descripción de Método
     *
     *
     * @param imageIndicator
     */

    public void setImageIndicator( String imageIndicator ) {
        if( imageIndicator != null ) {
            m_imageIndicator = imageIndicator;
            m_imageIndex     = getImageIndex( m_imageIndicator );
        }
    }    // setImageIndicator

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public String getImageIndiactor() {
        return m_imageIndicator;
    }    // getImageIndiactor

    /**
     * Descripción de Método
     *
     *
     * @param index
     *
     * @return
     */

    public static Icon getIcon( int index ) {
        if( (index == 0) || (IMAGES == null) || (index > IMAGES.length) ) {
            return null;
        }

        return IMAGES[ index ];
    }    // getIcon

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Icon getIcon() {
    	if (myIcon!=null)
    		return myIcon;
    	else
    		return getIcon( m_imageIndex );
    }    // getIcon

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isOnBar() {
        return m_onBar;
    }    // isOnBar

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isProcess() {
        return X_AD_Menu.ACTION_Process.equals( m_imageIndicator );
    }    // isProcess

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isReport() {
        return X_AD_Menu.ACTION_Report.equals( m_imageIndicator );
    }    // isReport

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWindow() {
        return X_AD_Menu.ACTION_Window.equals( m_imageIndicator );
    }    // isWindow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWorkbench() {
        return X_AD_Menu.ACTION_Workbench.equals( m_imageIndicator );
    }    // isWorkbench

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isWorkFlow() {
        return X_AD_Menu.ACTION_WorkFlow.equals( m_imageIndicator );
    }    // isWorkFlow

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isForm() {
        return X_AD_Menu.ACTION_Form.equals( m_imageIndicator );
    }    // isForm

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public boolean isTask() {
        return X_AD_Menu.ACTION_Task.equals( m_imageIndicator );
    }    // isTask

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public Color getColor() {
        if( m_color != null ) {
            return m_color;
        }

        return Color.black;
    }    // getColor

    /** Descripción de Campos */

    private int m_lastID = -1;

    /** Descripción de Campos */

    private MTreeNode m_lastNode = null;

    /**
     * Descripción de Método
     *
     *
     * @param ID
     *
     * @return
     */

    public MTreeNode findNode( int ID ) {
        if( m_node_ID == ID ) {
            return this;
        }

        //

        if( (ID == m_lastID) && (m_lastNode != null) ) {
            return m_lastNode;
        }

        //

        Enumeration en = preorderEnumeration();

        while( en.hasMoreElements()) {
            MTreeNode nd = ( MTreeNode )en.nextElement();

            if( ID == nd.getNode_ID()) {
                m_lastID   = ID;
                m_lastNode = nd;

                return nd;
            }
        }

        return null;
    }    // findNode
}    // MTreeNode



/*
 *  @(#)MTreeNode.java   02.07.07
 * 
 *  Fin del fichero MTreeNode.java
 *  
 *  Versión 2.2
 *
 */
