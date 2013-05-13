/*
 * @(#)MWindowVO.java   12.oct 2007  Versión 2.2
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

import org.openXpertya.model.MTabVO;
import org.openXpertya.model.MWindowVO;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

//~--- Importaciones JDK ------------------------------------------------------

import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

/**
 *  Model Window Value Object
 *
 *  @author Comunidad de Desarrollo openXpertya
 *         *Basado en Codigo Original Modificado, Revisado y Optimizado de:
 *         * Jorg Janke
 *  @version  $Id: MWindowVO.java,v 1.12 2005/03/11 20:28:38 jjanke Exp $
 */
public class MWindowVO implements Serializable {

    /** Descripción de Campo */
    static final long	serialVersionUID	= 3802628212531678981L;

    /** Descripción de Campo */
    public static final String	WINDOWTYPE_TRX	= "T";

    /** Descripción de Campo */
    public static final String	WINDOWTYPE_QUERY	= "Q";

    /** Descripción de Campo */
    public static final String	WINDOWTYPE_MMAINTAIN	= "M";

    // Database fields

    /** Descripción de Campo */
    public int	AD_Window_ID	= 0;

    /** Descripción de Campo */
    public String	Name	= "";

    /** Descripción de Campo */
    public String	Help	= "";

    /** Descripción de Campo */
    public String	Description	= "";

    /** Descripción de Campo */
    public String	WindowType	= "";

    /** Descripción de Campo */
    public int	WinWidth	= 0;

    /** Descripción de Campo */
    public int	WinHeight	= 0;

    /** Tabs contains MTabVO elements */
    public ArrayList<MTabVO>	Tabs = null;

	/** Base Table		*/
	public int 			AD_Table_ID = 0;

    /** Descripción de Campo */
    public boolean	IsSOTrx	= false;

    /** Descripción de Campo */
    public String	IsReadWrite	= null;

    /** Descripción de Campo */
    public int	AD_Image_ID	= 0;

    /** Descripción de Campo */
    public int	AD_Color_ID	= 0;

    /** Descripción de Campo */
    public int	WindowNo;

    /** Properties */
    public Properties	ctx;

    /**
     *  protected Constructor
     *  @param ctx context
     *  @param WindowNo window no
     */
    protected MWindowVO(Properties ctx, int WindowNo) {

        this.ctx	= ctx;
        this.WindowNo	= WindowNo;

    }		// MWindowVO

    /**
     *  Create Window Value Object
     *
     *  @param ctx context
     *  @param WindowNo window no
     *  @param AD_Window_ID window id
     *  @return MWindowVO
     */
    public static MWindowVO create(Properties ctx, int WindowNo, int AD_Window_ID) {
        return create(ctx, WindowNo, AD_Window_ID, 0);
    }		// create

    /**
     *  Create Window Value Object
     *
     *  @param ctx context
     *  @param WindowNo window no
     *  @param AD_Window_ID window id
     *  @param AD_Menu_ID menu id
     *  @return MWindowVO
     */
    public static MWindowVO create(Properties ctx, int WindowNo, int AD_Window_ID, int AD_Menu_ID) {

        CLogger.get().config("#" + WindowNo + " - AD_Window_ID=" + AD_Window_ID + "; AD_Menu_ID=" + AD_Menu_ID);

        MWindowVO	vo	= new MWindowVO(ctx, WindowNo);

        vo.AD_Window_ID	= AD_Window_ID;

        // Get Window_ID if required   - (used by HTML UI)
        if ((vo.AD_Window_ID == 0) && (AD_Menu_ID != 0)) {

            String	sql	= "SELECT AD_Window_ID, IsSOTrx, IsReadOnly FROM AD_Menu " + "WHERE AD_Menu_ID=? AND Action='W'";

            try {

                PreparedStatement	pstmt	= DB.prepareStatement(sql);

                pstmt.setInt(1, AD_Menu_ID);

                ResultSet	rs	= pstmt.executeQuery();

                if (rs.next()) {

                    vo.AD_Window_ID	= rs.getInt(1);

                    String	IsSOTrx	= rs.getString(2);

                    Env.setContext(ctx, WindowNo, "IsSOTrx", ((IsSOTrx != null) && IsSOTrx.equals("Y")));

                    //
                    String	IsReadOnly	= rs.getString(3);

                    if ((IsReadOnly != null) && IsReadOnly.equals("Y")) {
                        vo.IsReadWrite	= "Y";
                    } else {
                        vo.IsReadWrite	= "N";
                    }
                }

                rs.close();
                pstmt.close();

            } catch (SQLException e) {

                CLogger.get().log(Level.SEVERE, "Menu", e);

                return null;
            }

            CLogger.get().config("AD_Window_ID=" + vo.AD_Window_ID);
        }

        // --  Get Window
        StringBuffer	sql	= new StringBuffer("SELECT Name,Description,Help,WindowType, " + "AD_Color_ID,AD_Image_ID, a.IsReadWrite, WinHeight,WinWidth, " + "IsSOTrx ");

        if (Env.isBaseLanguage(vo.ctx, "AD_Window")) {
            sql.append("FROM AD_Window w, AD_Window_Access a " + "WHERE w.AD_Window_ID=?" + " AND w.AD_Window_ID=a.AD_Window_ID AND a.AD_Role_ID=?" + " AND w.IsActive='Y' AND a.IsActive='Y'");
        } else {
            sql.append("FROM AD_Window_vt w, AD_Window_Access a " + "WHERE w.AD_Window_ID=?" + " AND w.AD_Window_ID=a.AD_Window_ID AND a.AD_Role_ID=?" + " AND a.IsActive='Y'").append(" AND AD_Language='").append(Env.getAD_Language(vo.ctx)).append("'");
        }

        int	AD_Role_ID	= Env.getContextAsInt(vo.ctx, "#AD_Role_ID");

        try {

            // create statement
            PreparedStatement	pstmt	= DB.prepareStatement(sql.toString());

            pstmt.setInt(1, vo.AD_Window_ID);
            pstmt.setInt(2, AD_Role_ID);

            // get data
            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {

                vo.Name		= rs.getString(1);
                vo.Description	= rs.getString(2);

                if (vo.Description == null) {
                    vo.Description	= "";
                }

                vo.Help	= rs.getString(3);

                if (vo.Help == null) {
                    vo.Help	= "";
                }

                vo.WindowType	= rs.getString(4);

                //
                vo.AD_Color_ID	= rs.getInt(5);
                vo.AD_Image_ID	= rs.getInt(6);
                vo.IsReadWrite	= rs.getString(7);

                //
                vo.WinHeight	= rs.getInt(8);
                vo.WinWidth	= rs.getInt(9);

                //
                vo.IsSOTrx	= "Y".equals(rs.getString(10));

            } else {
                vo	= null;
            }

            rs.close();
            pstmt.close();
        } catch (SQLException ex) {

            CLogger.get().log(Level.SEVERE, sql.toString(), ex);

            return null;
        }

        // Not found
        if (vo == null) {

            CLogger.get().log(Level.SEVERE, "No Window - AD_Window_ID=" + AD_Window_ID + ", AD_Role_ID=" + AD_Role_ID + " - " + sql);
            CLogger.get().saveError("AccessTableNoView", "(Not found)");

            return null;
        }

        // Read Write
        if (vo.IsReadWrite == null) {

            CLogger.get().saveError("AccessTableNoView", "(found)");

            return null;
        }

        // Create Tabs
        createTabs(vo);

        if ((vo.Tabs == null) || (vo.Tabs.size() == 0)) {
            return null;
        }

        return vo;

    }		// create

    /**
     *  Create Window Tabs
     *  @param mWindowVO Window Value Object
     *  @return true if tabs were created
     */
    protected static boolean createTabs(MWindowVO mWindowVO) {

        mWindowVO.Tabs	= new ArrayList();

        String	sql		= MTabVO.getSQL(mWindowVO.ctx);
        int	TabNo		= 0;
        int	AD_Table_ID	= 0;

        try {

            // create statement
            PreparedStatement	pstmt	= DB.prepareStatement(sql);

            pstmt.setInt(1, mWindowVO.AD_Window_ID);

            ResultSet	rs		= pstmt.executeQuery();
            boolean	firstTab	= true;

            while (rs.next()) {

                if (AD_Table_ID == 0) {
                    AD_Table_ID	= rs.getInt("AD_Table_ID");
                }

                // Create TabVO
                MTabVO	mTabVO	= MTabVO.create(mWindowVO, TabNo, rs, mWindowVO.WindowType.equals(WINDOWTYPE_QUERY) || "N".equals(mWindowVO.IsReadWrite),	// isRO
                                                mWindowVO.WindowType.equals(WINDOWTYPE_TRX));		// onlyCurrentRows

                if ((mTabVO == null) && firstTab) {
                    break;	// don't continue if first tab is null
                }

                if (mTabVO != null) {

                    if (!mTabVO.IsReadOnly && "N".equals(mWindowVO.IsReadWrite)) {
                        mTabVO.IsReadOnly	= true;
                    }

                    mWindowVO.Tabs.add(mTabVO);
                    TabNo++;	// must be same as mWindow.getTab(x)
                    firstTab	= false;
                }
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {

            CLogger.get().log(Level.SEVERE, "createTabs", e);

            return false;
        }

        // No Tabs
        if ((TabNo == 0) || (mWindowVO.Tabs.size() == 0)) {

            CLogger.get().log(Level.SEVERE, "No Tabs - AD_Window_ID=" + mWindowVO.AD_Window_ID + " - " + sql);

            return false;
        }

        // Put base table of window in ctx (for VDocAction)
        Env.setContext(mWindowVO.ctx, mWindowVO.WindowNo, "BaseTable_ID", AD_Table_ID);

        return true;

    }		// createTabs

    //~--- set methods --------------------------------------------------------

    /**
     *  Set Context including contained elements
     *  @param newCtx context
     */
    public void setCtx(Properties newCtx) {

        ctx	= newCtx;

        for (int i = 0; i < Tabs.size(); i++) {

            MTabVO	tab	= (MTabVO) Tabs.get(i);

            tab.setCtx(newCtx);
        }

    }		// setCtx
    
	/**
	 * 	Clone
	 * 	@param windowNo no
	 *	@return WindowVO
	 */
	public MWindowVO clone (int windowNo)
	{
		MWindowVO clone = null;
		try
		{
			clone = new MWindowVO(ctx, windowNo);
			clone.AD_Window_ID = AD_Window_ID;
			clone.Name = Name;
			clone.Description = Description;
			clone.Help = Help;
			clone.WindowType = WindowType;
			clone.AD_Image_ID = AD_Image_ID;
			clone.AD_Color_ID = AD_Color_ID;
			clone.IsReadWrite = IsReadWrite;
			clone.WinWidth = WinWidth;
			clone.WinHeight = WinHeight;
			clone.IsSOTrx = IsSOTrx;
			Env.setContext(ctx, windowNo, "IsSOTrx", clone.IsSOTrx);
			clone.AD_Table_ID = AD_Table_ID;
			Env.setContext(ctx, windowNo, "BaseTable_ID", clone.AD_Table_ID);
			//
			clone.Tabs = new ArrayList<MTabVO>();
			for (int i = 0; i < Tabs.size(); i++)
			{
				MTabVO tab = Tabs.get(i);
				MTabVO cloneTab = tab.clone(clone.ctx, windowNo);
				if (cloneTab == null)
					return null;
				clone.Tabs.add(cloneTab);
			}
		}
		catch (Exception e)
		{
			clone = null;
		}
		return clone;
	}	//	clone

}	// MWindowVO



/*
 * @(#)MWindowVO.java   02.jul 2007
 * 
 *  Fin del fichero MWindowVO.java
 *  
 *  Versión 2.2  - Fundesle (2007)
 *
 */


//~ Formateado de acuerdo a Sistema Fundesle en 02.jul 2007
