package org.openXpertya.apps.form;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.apps.AEnv;
import org.openXpertya.apps.AWindow;
import org.openXpertya.grid.ed.VDate;
import org.openXpertya.grid.ed.VLookup;
import org.openXpertya.model.MLookup;
import org.openXpertya.model.MLookupFactory;
import org.openXpertya.model.MLookupInfo;
import org.openXpertya.model.MQuery;
import org.openXpertya.model.M_Column;
import org.openXpertya.pos.view.VPoSLookup;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.DisplayType;
import org.openXpertya.util.Env;
import org.openXpertya.util.Msg;

public class VComponentsFactory {

	private static CLogger	s_log	= CLogger.getCLogger(VComponentsFactory.class);
	
	private VComponentsFactory() {
		
	}
	
	public static VDate VDateFactory() {
		String columnName = "Date";
		return new VDate( columnName,false,false,true,DisplayType.Date,Msg.translate( Env.getCtx(),columnName ));
	}
	
	public static VLookup VLookupFactory(String ColumnName, String TableName, int WindowNo) {
		return VLookupFactory(ColumnName, TableName, WindowNo, DisplayType.TableDir, null);
	}
	
	public static VLookup VLookupFactory(String ColumnName, String TableName, int WindowNo, int displayType) {
		return VLookupFactory(ColumnName, TableName, WindowNo, displayType, null);
	}
	
	public static VLookup VLookupFactory(String ColumnName, String TableName, int WindowNo, int displayType, String whereClause) {
		return VLookupFactory(ColumnName, TableName, WindowNo, displayType, whereClause, true);
	}
	
	public static VLookup VLookupFactory(String ColumnName, String TableName, int WindowNo, int displayType, String whereClause, boolean mandatory) {
		return VLookupFactory(ColumnName, TableName, WindowNo, displayType, whereClause, mandatory, true);
	}
	
	public static VLookup VLookupFactory(String ColumnName, String TableName, int WindowNo, int displayType, String whereClause, boolean mandatory, boolean addSecurityValidation) {
		return VLookupFactory(ColumnName, TableName, WindowNo, displayType, whereClause, mandatory, addSecurityValidation, false);
	}
	
	public static VLookup VLookupFactory(String ColumnName, String TableName, int WindowNo, int displayType, String whereClause, boolean mandatory, boolean addSecurityValidation, boolean multiSelect) {
        // = 4917;    // C_BankStatement.C_BankAccount_ID, 
		int AD_Column_ID = DarColID(ColumnName, TableName);
		
		int TabNo = 0;

		MLookupInfo info = MLookupInfoFactory( Env.getCtx(),WindowNo,TabNo,AD_Column_ID, displayType, whereClause, addSecurityValidation );
		info.ZoomQuery = new MQuery();
		
        MLookup lookup       = new MLookup(info, TabNo);
        
        if (whereClause != null && whereClause.length() > 0)
        	info.ZoomQuery.addRestriction(whereClause);
        
        // String columnKeyName = lookup.getColumnName();
        // String columnName = columnKeyName.substring(columnKeyName.lastIndexOf(".") + 1);
        
        VLookup vl = new VLookup( ColumnName, mandatory, false, true, lookup, multiSelect );
        
        return vl;
	}
	
	public static VLookup VPoSLookupFactory(String ColumnName, String TableName, int WindowNo, int displayType) {
		return VPoSLookupFactory(ColumnName, TableName, WindowNo, displayType, null);
	}
	
	public static VLookup VPoSLookupFactory(String ColumnName, String TableName, int WindowNo, int displayType, String whereClause) {
        // = 4917;    // C_BankStatement.C_BankAccount_ID, 
		int AD_Column_ID = DarColID(ColumnName, TableName);
		
		int TabNo = 0;
		
		MLookupInfo info = MLookupInfoFactory( Env.getCtx(),WindowNo,TabNo,AD_Column_ID, displayType, whereClause );
		info.ZoomQuery = new MQuery();
		
        MLookup lookup       = new MLookup(info, TabNo);
        
        if (whereClause != null && whereClause.length() > 0)
        	info.ZoomQuery.addRestriction(whereClause);
        
        // String columnKeyName = lookup.getColumnName();
        // String columnName = columnKeyName.substring(columnKeyName.lastIndexOf(".") + 1);
        
        VLookup vl = new VPoSLookup( ColumnName, true, false, true, lookup );
        
        return vl;
	}

	public static void ZoomFactory(int AD_Column_ID, Object value, int WindowNo) {
		
        MLookup lookup       = MLookupFactory.get( Env.getCtx(),WindowNo,0,AD_Column_ID,DisplayType.TableDir );
        String columnKeyName = lookup.getColumnName();
        String columnName = columnKeyName.substring(columnKeyName.lastIndexOf(".") + 1);

        MQuery zoomQuery = new MQuery();
        zoomQuery.addRestriction( columnName, MQuery.EQUAL, value );
        int AD_Window_ID = lookup.getZoom( zoomQuery );
        
        AWindow frame = new AWindow();

        if( frame.initWindow( AD_Window_ID, zoomQuery )) {
        	AEnv.showCenterScreen( frame );
        }
	}
	
	public static int DarColID(String ColumnName, String TableName) {
		String sql = "select c.ad_column_id from ad_column c " +
					" inner join ad_table t on (c.ad_table_id=t.ad_table_id) " +
					" where c.columnname ilike '" + ColumnName + "' and t.tablename ilike '" + TableName + "' ";
		
		return DB.getSQLValue(null, sql);
	}
	
	public static MLookupInfo MLookupInfoFactory(Properties ctx, int WindowNo, int TabNo, String columnName, String tableName, int AD_Reference_ID, String manualValidationCode) {
		return MLookupInfoFactory(ctx, WindowNo, TabNo,
				M_Column.getColumnID(null, columnName, tableName),
				AD_Reference_ID, manualValidationCode);
	}
	
	public static MLookupInfo MLookupInfoFactory(Properties ctx, int WindowNo, int TabNo, int Column_ID, int AD_Reference_ID, String manualValidationCode) {
		return MLookupInfoFactory(ctx, WindowNo, TabNo, Column_ID, AD_Reference_ID, manualValidationCode, true);
	}
	
	public static MLookupInfo MLookupInfoFactory(Properties ctx, int WindowNo, int TabNo, int Column_ID, int AD_Reference_ID, String manualValidationCode, boolean addSecurityValidation) {

        String	ColumnName		= "";
        int	AD_Reference_Value_ID	= 0;
        boolean	IsParent		= false;
        String	ValidationCode		= "";

        //
        String	sql	= "SELECT c.ColumnName, c.AD_Reference_Value_ID, c.IsParent, vr.Code " + "FROM AD_Column c" + " LEFT OUTER JOIN AD_Val_Rule vr ON (c.AD_Val_Rule_ID=vr.AD_Val_Rule_ID) " + "WHERE c.AD_Column_ID=?";
        PreparedStatement	pstmt	= null;

        try {

            pstmt	= DB.prepareStatement(sql);
            pstmt.setInt(1, Column_ID);

            //
            ResultSet	rs	= pstmt.executeQuery();

            if (rs.next()) {

                ColumnName		= rs.getString(1);
                AD_Reference_Value_ID	= rs.getInt(2);
                IsParent		= "Y".equals(rs.getString(3));
                // Si se recibió una validación manual como parámetro se ignora la 
                // validación de los metadatos de la columna y se setea dicha validación
                // manual. De esta forma la instanción de VLookups con una claúsula Where
                // se hace correctamente tanto para el tipo Búsqueda (que estaba funcionando
                // correctamente) y el tipo Table o TableDir en los cuales no se estaba
                // aplicando la validación recibida como parámetro en el método.
                if (manualValidationCode != null && manualValidationCode.trim().length() > 0) {
                	ValidationCode = manualValidationCode;
                } else {
                	ValidationCode = rs.getString(4);
                }

            } else {
                s_log.log(Level.SEVERE, "Column Not Found - AD_Column_ID=" + Column_ID);
            }

            rs.close();

            //
            pstmt.close();
            pstmt	= null;

        } catch (SQLException ex) {
            s_log.log(Level.SEVERE, "create", ex);
        }

        try {

            if (pstmt != null) {
                pstmt.close();
            }

        } catch (SQLException ex1) {}

        pstmt	= null;

        //
		MLookupInfo info = MLookupFactory.getLookupInfo(ctx, WindowNo,
				Column_ID, AD_Reference_ID, Env.getLanguage(ctx), ColumnName,
				AD_Reference_Value_ID, IsParent, ValidationCode,
				addSecurityValidation);

        return info;

    }		
}

