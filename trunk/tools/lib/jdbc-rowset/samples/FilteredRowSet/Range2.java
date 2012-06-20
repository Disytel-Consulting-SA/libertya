
import javax.sql.rowset.*;
import com.sun.rowset.*;
import java.util.*;
import java.lang.*;
import java.sql.*;
import javax.sql.RowSet;
import java.io.Serializable;


public class Range2 implements Predicate, Serializable {

	private int idx;
	private Object hi;
	private Object lo;
	private String colName;
	
	public Range2(Object lo, Object hi, int idx) {
	   this.hi = hi;
	   this.lo = lo;
	   this.idx = idx;
	   this.colName = new String("");
	}
	
	public Range2(Object lo , Object hi , String colName, int idx) {
	   this.lo = lo;
	   this.hi = hi;
	   this.colName = colName;
	   this.idx = idx;
	}
	
	public boolean evaluate(RowSet rs) {
	   int comp;
	   String columnVal = "";
	   boolean bool = false;
	   FilteredRowSetImpl crs = (FilteredRowSetImpl) rs;
	   
	   try {
	        columnVal = crs.getString(idx);
		
		//System.out.println("Value is :"+columnVal);
		comp = columnVal.compareTo(lo.toString());
		
		//System.out.println("comp1 :"+comp);
		if(comp < 0) {
		    return false;
		}
		         	   
		comp = columnVal.compareTo(hi.toString());
		//System.out.println("comp2 :"+comp);
		if(comp > 0) {
		    return false;
		}
		
	   } catch(SQLException e) {
	   
	   } //end catch
	   
	 return true;   
       }
       
       public boolean evaluate(Object value, String columnName) {
           
           int comp;
           
           if(!(columnName.equals(colName))) {
              return true;
           }
           
           comp = (value.toString()).compareTo(lo.toString());
           
           if ( comp < 0 ) {
              return false;
           }
           
           comp = (value.toString()).compareTo(hi.toString());
           
           if ( comp > 0 ) {
               return false;
           }
           
           return true;
       }
       
       public boolean evaluate(Object value, int columnIndex) {
           
           int comp;           
           
           if(columnIndex != idx) {
              return true;
           }
           
           comp = (value.toString()).compareTo(lo.toString());           
           
           if( comp < 0 ) {
              return false;
           }
           
           comp = (value.toString()).compareTo(hi.toString());
           
           if ( comp > 0 ) {
               return false;
           }
           
           return true;
       }
       
       
}       	   
