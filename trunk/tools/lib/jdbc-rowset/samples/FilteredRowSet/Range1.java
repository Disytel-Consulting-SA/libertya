import javax.sql.rowset.*;
import com.sun.rowset.*;
import java.util.*;
import java.lang.*;
import java.sql.*;
import javax.sql.RowSet;
import java.io.*;


public class Range1 implements Predicate, Serializable {

	private int idx;
	private int hi;
	private int lo;
	private String colName;
	
	public Range1(int lo, int hi, int idx) {
	   this.hi = hi;
	   this.lo = lo;
	   this.idx = idx;
	   colName = new String("");
	}
	
	public Range1(int lo , int hi , String colName , int idx) {
	   this.lo = lo;
	   this.hi = hi;
	   this.colName = colName;
	   this.idx = idx;
	}
	
	
	public boolean evaluate(RowSet rs) {
	   int comp,columnVal;
	   columnVal = 0;
	   boolean bool = false;
	   CachedRowSetImpl crs = (CachedRowSetImpl) rs;
	   
	   try {
	      columnVal = crs.getInt(idx);
         	   if(columnVal <= hi && columnVal >= lo) {
 	              bool = true;
	           } else {
	              bool = false;
	           }
	   } catch(SQLException e) {
	   
	   }
	   return bool;
       }
       
       
       public boolean evaluate(Object value, String columnName) {          
         
         int colVal;
         boolean bool = false;
         
         if(columnName.equals(colName)) {
            colVal = (Integer.parseInt(value.toString()));
            
            if( colVal <= hi && colVal >= lo) {
               bool = true;
            } else {
               bool = false;
            }
         } else {
            bool = true;
         }         
         
         return bool;
         
       }
       
       public boolean evaluate(Object value, int columnIndex) {
           
           int colVal;
           boolean bool = false;
           
           if(columnIndex == idx) {
              colVal = (Integer.parseInt(value.toString()));
              
              if( colVal <= hi && colVal >= lo) {
                 bool = true;
              } else {
                 bool = false;
              }
           } else {
               bool = true;
           }
           
           return bool;
       }
}       	   
