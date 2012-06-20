package org.openXpertya.report.jcalc;


import java.util.Hashtable;
import java.math.*;

public class VariableTable {
    private Hashtable variables = new Hashtable();

    //pi and e, others may be added later.
    private Hashtable variable_variables = new Hashtable();
    private int scale;
    
    public VariableTable(int scl){
        variables.put("true",  new Boolean(true));
        variables.put("false", new Boolean(false));
        variables.put("brian", new BigDecimal(25));
        
        PI p_class = new PI(scl);
        E e_class = new E(scl);
        //E e_class = new E(scl);
        variable_variables.put("pi", p_class);
        variable_variables.put("e", e_class);
    }
 
    public boolean isVariable(String s){
        if(variables.containsKey(s) ||
           variable_variables.containsKey(s)
        ){
            return true;
        }
        return false;
    }
    
    public Object variableValue(String s, int scl){
        if(variables.containsKey(s)){
            return variables.get(s);
        }else{
            variable_interface vio = (variable_interface)variable_variables.get(s);
            return vio.getValue(scl);
            //return null;
        }
    }
 
    
    
}
