package org.openXpertya.report.jcalc;

public class CalculatorException extends Exception {
    public int location = -1;
    
    public CalculatorException(String s){
        super(s);
    }
    
    public CalculatorException(String s, int l){
        super(s);
        this.location = l;
    }
    
    public boolean equals(CalculatorException e){
        if(e.location!=this.location){
            return false;
        }
        if(!e.getMessage().equals(this.getMessage())){
            return false;
        }
        return true;
    }

    public boolean equals(Exception e){
        
        return true;
    }

    public boolean equals(String e){
        
        return true;
    }
    
    
}
