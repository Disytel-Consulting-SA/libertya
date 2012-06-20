package org.openXpertya.report.jcalc;


import java.math.BigDecimal;

public class E implements variable_interface{
    //also of note: e^z = 1 + z + z2/2! + z3/3! + z4/4! + z5/5! + z6/6! +
    //              e^(-y) = 1 - y + y^2/2! - y^3/3! + y^4/4! - y^5/5! + ...
    //this will be useful with sympolic notation
    
    int scale;
    BigDecimal value;
    
    public E(int scl){
        this.scale = scl;
        value = this.calculate_value(scl);
    }
    
    public BigDecimal calculate_value(int scl){
        //System.out.println("calculating for: " + scl);
        if(value==null){
            value = new BigDecimal("2.5");
        }
        
        BigDecimal prev = value.subtract( BigDecimal.valueOf(1));
        BigDecimal ONE = new BigDecimal("1");
        
        int iteration = 3;
        
        while(prev.compareTo(value)!=0){
            prev = value;
            
            BigDecimal fact = new BigDecimal(iteration);
            value = value.add( ONE.divide( jcalc_math.factorial(fact), scale*2, BigDecimal.ROUND_HALF_UP));
            iteration++;
            
            //System.out.println(value+"=="+prev);
        }
        return value;
    }//end - public BigDecimal calculate_value(int scl)
    
    
    public BigDecimal getValue(int scl){
        if(scl>15 && scl!=scale){
            this.scale=scl;
        }else if(scl<16){
            return value;
        }
        
        if(scl>value.scale()){
            //System.out.println("get new pi");
            value = calculate_value(scl);
        }else if(scl<value.scale()){
            value = value.setScale(scl,BigDecimal.ROUND_HALF_UP);
        }

        return value;        
    }
    
    
    BigDecimal pow(BigDecimal left, BigDecimal right){
        BigDecimal orig = new BigDecimal(left.toString());
        for(int i=1; i<right.intValue(); i++){
            left = left.multiply(orig);
        }
        return left;
    }
    
}
