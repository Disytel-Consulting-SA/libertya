package org.openXpertya.apps.form;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.openXpertya.model.MCurrency;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.CPreparedStatement;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;

public class VModelHelper {
	
	protected static CLogger s_log = CLogger.getCLogger(VModelHelper.class);
	
	public static BigDecimal currencyConvert(BigDecimal fromAmt, int fromCurency, int toCurrency, Timestamp convDate)  {
		// TODO: Si converto varias veces, que hago?
		// return Currency.convert(fromAmt, fromCurency, toCurrency, convDate, 0, Env.getAD_Client_ID(m_ctx), Env.getAD_Org_ID(m_ctx));
		
		// HACK: Can't invoke Currency.convert directly !!
		
		if (fromCurency == toCurrency)
			return fromAmt;
		
		CPreparedStatement pp = DB.prepareStatement(" SELECT currencyConvert(?, ?, ?, ?, ?, ?, ?) ");
		ResultSet rs = null;
		
		try {
			pp.setBigDecimal(1, fromAmt);
			pp.setInt(2, fromCurency);
			pp.setInt(3, toCurrency);
			pp.setTimestamp(4, convDate);
			pp.setInt(5, 0); // TODO: tipo de conversion ?
			pp.setInt(6, Env.getAD_Client_ID(Env.getCtx()));
			pp.setInt(7, Env.getAD_Org_ID(Env.getCtx()));
			
			rs = pp.executeQuery();
			
			if (rs.next())
				return rs.getBigDecimal(1);
			
		} catch (Exception e) {
			s_log.log(Level.SEVERE, "currencyConvert", e);
		} finally{
			try {
				if(rs != null)rs.close();
				if(pp != null)pp.close();
			} catch (Exception e2) {
				s_log.log(Level.SEVERE, "currencyConvert", e2);
			}
		}
		
		return null;
	}
	
	public static BigDecimal GetRedondeoMoneda(Properties ctx, int C_Currency_ID) {
		int scale = MCurrency.getStdPrecision(ctx, C_Currency_ID);
		return BigDecimal.ONE.divide(BigDecimal.TEN.pow(scale), scale, BigDecimal.ROUND_HALF_UP);	
	}
	
	public static ResultSet GetReferenceTrlFromColumn(String TableName, String ColumnName, String AD_Language, String ReferenceValue) throws SQLException {
		StringBuffer sql = new StringBuffer();
		int i = 1;
		
		sql.append("select coalesce( rt.name, r.name ) as name, coalesce( rt.description, r.description ) as description, r.value " + 
			" from ad_table t " + 
			" inner join ad_column c on (t.ad_table_id=c.ad_table_id) " + 
			" inner join ad_ref_list r on (r.ad_reference_id=c.ad_reference_value_id) " + 
			" left join ad_ref_list_trl rt on (r.ad_ref_list_id=rt.ad_ref_list_id) " + 
			" where t.tablename = ? and c.columnname = ? and rt.ad_language = ? ");
		
		if (ReferenceValue != null && ReferenceValue.length() > 0)
			sql.append(" and r.value = ? ");
		else
			ReferenceValue = null;
		
		CPreparedStatement p = DB.prepareStatement(sql.toString());
		
		p.setString(i++, TableName);
		p.setString(i++, ColumnName);
		p.setString(i++, AD_Language);

		if (ReferenceValue != null)
			p.setString(i++, ReferenceValue);
		
		return p.executeQuery();
	}
	
	public static String GetReferenceValueTrlFromColumn(String TableName, String ColumnName, String ReferenceValue, String WichReturn) {
		ResultSet rs = null;
		String value = null;
		
		try {
			rs = VModelHelper.GetReferenceTrlFromColumn(TableName, ColumnName, Env.getAD_Language(Env.getCtx()), ReferenceValue);
			
			if (rs != null && rs.next()) {
				value = rs.getString(WichReturn);
				rs.close();
			}
		} catch (SQLException e) {
			s_log.log(Level.SEVERE, "GetReferenceTrlFromColumn", e );
		}
		
		return value;
	}
	
	public static Timestamp getSQLValueTimestamp( String trxName,String sql,int int_param1 ) {
        Timestamp retValue = null;
        PreparedStatement pstmt    = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,int_param1 );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getTimestamp( 1 );
            } else {
                s_log.info( "No Value " + sql + " - Param1=" + int_param1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
        	s_log.log( Level.SEVERE,sql + " - Param1=" + int_param1 + " [" + trxName + "]",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }
	
	public static String getSQLValueString( String trxName,String sql,int int_param1 ) {
        String retValue = null;
        PreparedStatement pstmt    = null;

        try {
            pstmt = DB.prepareStatement( sql,trxName );
            pstmt.setInt( 1,int_param1 );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = rs.getString( 1 );
            } else {
                s_log.info( "No Value " + sql + " - Param1=" + int_param1 );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
        	s_log.log( Level.SEVERE,sql + " - Param1=" + int_param1 + " [" + trxName + "]",e );
        } finally {
            try {
                if( pstmt != null ) {
                    pstmt.close();
                }
            } catch( Exception e ) {
            }

            pstmt = null;
        }

        return retValue;
    }
	
	private static VModelHelper s_instance = new VModelHelper();
	
	public static VModelHelper GetInstance() {
		return new VModelHelper();
	}
	
	public static ResultItem ResultItemFactory(ResultSet rs) throws Exception {
		return GetInstance().new ResultItem(rs);
	}
	
	public static TableModel HideColumnsTableModelFactory(TableModel tm) {
		return GetInstance().new HideColumnsTableModel(tm);
	}
	
	public class ResultItem {
    	public ResultItem(ResultSet rs) throws SQLException, ClassNotFoundException {
    		if (rs != null) {
	    		colCount = rs.getMetaData().getColumnCount();
	    		for (int i = 1; i<= colCount; i++) {
	    			data.add(rs.getObject(i));
	    			dataClass.add(Class.forName(rs.getMetaData().getColumnClassName(i)));
	    			colNames.put(rs.getMetaData().getColumnName(i), new Integer(i));
	    		}
	    		m_isValid = true;
    		} else {
    			colCount = 0;
    			m_isValid = false;
    		}
    	}
    	
    	public boolean isValid() {
    		return m_isValid;
    	}
    	
    	public int getColIdx(String colName) {
    		return colNames.get(colName).intValue();
    	}
    	
    	public Object getItem(int i) {
    		return data.get(i);
    	}
    	
    	public Object getItem(String colName) {
    		return getItem(getColIdx(colName));
    	}
    	
    	public Class<?> getItemClass(int i) {
    		return dataClass.get(i);
    	}
    	
    	public Class<?> getItemClass(String colName) {
    		return getItemClass(getColIdx(colName));
    	}
    	
    	public int getColCount() {
    		return colCount;
    	}
    	
    	private int colCount;
    	private boolean m_isValid;
    	private Vector<Object> data = new Vector<Object>();
    	private Vector<Class<?>> dataClass = new Vector<Class<?>>();
    	private HashMap<String, Integer> colNames = new HashMap<String, Integer>();
    }

    public class ResultItemTableModel extends AbstractTableModel {
        public ResultItemTableModel() {
            
        }
        
        public ResultItemTableModel(Vector<ResultItem> ri) {
        	setResultItem(ri);
        }
        
        public void setResultItem(Vector<ResultItem> ri) {
        	item = ri;
        }
        
        public int getRowCount() {
        	if (item != null)
        		return item.size();
        	return 0;
        }
        
        public int getColumnCount() { 
        	if (columnNames != null)
        		return columnNames.size();
        	
        	if (item != null && item.size() > 0)
        		return item.get(0).getColCount();
        	
        	return 0;
        }
        
        public Class<?> getColumnClass(int columnIndex) {
        	if (item != null && item.size() > 0 && columnIndex < item.get(0).getColCount())
        		return item.get(0).getItemClass(columnIndex);
        	return String.class;
        } 
        
        public Object getValueAt(int row, int column) {
        	if (item != null)
        		return item.get(row).getItem(column);
        	return "";
        }
        
       	public String getColumnName(int column) {
    		return columnNames.get(column);
    	}
    	
       	public void fireChanged(boolean structure) {
       		if (structure)
            	fireTableStructureChanged();
            else
            	fireTableDataChanged();
       	}
       	
        protected Vector<ResultItem> item = null;
    	protected Vector<String> columnNames = null;
    }
    
    public class HideColumnsTableModel implements TableModel, TableModelListener {
    	public HideColumnsTableModel(TableModel tm) {
    		m_tm = tm;
    		updateHiddenColumns();
    	}
    	
    	private TableModel m_tm;
    	private boolean[] visibleCol;
    	private int[] mapCol;
    	private int cantCols;
    	
    	private void updateHiddenColumns() {
    		int i;
    		int j=0;

    		cantCols = m_tm.getColumnCount();

    		visibleCol = new boolean[cantCols];
    		mapCol = new int[cantCols];
    		
    		for (i=0; i<cantCols; i++) {
    			visibleCol[i] = !m_tm.getColumnName(i).startsWith("#$#");
    			
    			if (visibleCol[i]) {
        			mapCol[j++] = i;
    			} 
    		}
    		
    		cantCols = j;
    	}
    	
		public int getRowCount() {
			return m_tm.getRowCount();
		}

		public int getColumnCount() {
			return cantCols; 
		}

		public String getColumnName(int col) {
			return m_tm.getColumnName(mapCol[col]);
		}

		public Class<?> getColumnClass(int col) {
			return m_tm.getColumnClass(mapCol[col]);
		}

		public boolean isCellEditable(int row, int col) {
			return m_tm.isCellEditable(row, mapCol[col]);
		}

		public Object getValueAt(int row, int col) {
			return m_tm.getValueAt(row, mapCol[col]);
		}

		public void setValueAt(Object arg0, int row, int col) {
			m_tm.setValueAt(arg0, row, mapCol[col]);
		}

		public void addTableModelListener(TableModelListener arg0) {
			m_tm.addTableModelListener(arg0);
		}

		public void removeTableModelListener(TableModelListener arg0) {
			m_tm.removeTableModelListener(arg0);
		}

		public void tableChanged(TableModelEvent arg0) {
			updateHiddenColumns();
		}
    }
}

