package org.openXpertya.report.jcalc;

import java.math.*;
import java.util.Vector;

public class jcalc_trig {
    static int scale;
    static int safe_scale;
    
    static final BigDecimal ZERO = new BigDecimal("0");    
    static final BigDecimal ONE = new BigDecimal("1");
    static final BigDecimal TWO = new BigDecimal("2");
    
    PI pi_class;
    jcalc_math jmath;

    
    String []funcs = {"sin", "cos", "tan", "csc", "sec", "cot"};
    Vector functions = new Vector(funcs.length);
    
    
    public boolean isFunction(String s){
        if(functions.contains(s))
            return true;
        else
            return false;
    }
    
    public String[] getFunctions(){
        return funcs;
    }
    
    public int numOperans(String s){
        return 1;
    }
    
    
    public jcalc_trig(int scale, jcalc_math jm){
        this.jmath = jm;
        
        this.scale = scale;
        this.safe_scale = this.scale + 5;
        pi_class = new PI(scale);
        
        for(int i=0; i<funcs.length; i++){
            functions.add(funcs[i]);
        }

    }
    
    public BigDecimal execute(String function, Vector operans) {
        function = function.toLowerCase();
        BigDecimal bd = (BigDecimal)operans.elementAt(0);
        
        if(function.equals("sin")){
            return this.sin(bd);
        }else if(function.equals("cos")){
            return this.cos(bd);
        }else if(function.equals("tan")){
            return this.tan(bd);
        }else if(function.equals("csc")){
            return this.csc(bd);
        }else if(function.equals("sec")){
            return this.sec(bd);
        }else if(function.equals("cot")){
            return this.cot(bd);
        }
        return null;
    }
    
    public BigDecimal sin(BigDecimal bd){
        
        //System.out.print(bd +"=>");
        
        if(bd.compareTo(ZERO)>0){
            while(bd.compareTo(pi_class.getValue(scale))>0){
                bd = bd.subtract(pi_class.getValue(scale).multiply(TWO) );
            }
        }else if(bd.compareTo(ZERO)<0){
            while(bd.compareTo(pi_class.getValue(scale).negate().multiply(TWO))<0){
                bd = bd.add(pi_class.getValue(scale));
            }
        }
        
        //System.out.print(bd+"=>");
        
        bd = taylor(bd);

        //System.out.println(bd);
        
        return bd;
    }

    public BigDecimal cos(BigDecimal bd){
        //bd = pi_class.getValue().divide(TWO,scale,BigDecimal.ROUND_HALF_UP).add(bd);
        bd = pi_class.getValue().divide(TWO,scale,BigDecimal.ROUND_HALF_UP).subtract(bd);
        //bd = bd.subtract(pi_class.getValue().divide(TWO,scale,BigDecimal.ROUND_HALF_UP));
        return sin(bd);
    }
    
    public BigDecimal tan(BigDecimal bd){
        BigDecimal top = sin(bd);
        BigDecimal bottom = cos(bd);
        //System.out.println(top);
        //System.out.println(bottom + "\n");
        
        //if(bottom.compareTo(ZERO)==0){
        //    return bd.
        //}
        
        return top.divide(bottom, 2*scale, BigDecimal.ROUND_HALF_UP);
    }
    
    public BigDecimal csc(BigDecimal bd){
        return ONE.divide( sin(bd), 2*scale, BigDecimal.ROUND_HALF_UP);
    }
    
    public BigDecimal sec(BigDecimal bd){
        return ONE.divide( cos(bd), 2*scale, BigDecimal.ROUND_HALF_UP);
    }    
    
    public BigDecimal cot(BigDecimal bd){
        return ONE.divide( tan(bd), 2*scale, BigDecimal.ROUND_HALF_UP);
    }    
    

    public int setScale(int scl){
        if(scl<16)
            scl=16;
        
        this.scale = scl;
        this.safe_scale = scl + 5;
        
        return scale;
    }
    
    /**
     *  Returns the Taylor expansion of the input. Used to compute values in trig, and
     *  as a function used by the Calculator itself. This is as accurate as need be, 
     *  useing the scale value that is set when this class is initialized.
     *
     *  @param BigDecimal
     *  @returns BigDecimal
     */     
    public BigDecimal taylor(BigDecimal bd){
        //1 3 5 7 9 
        BigDecimal prev = new BigDecimal(bd.longValue()+1);
        BigDecimal curr = new BigDecimal(bd.toString());

        //System.out.println("taylor: " + bd);
        //System.out.println("using safe: " + safe_scale);
        //System.out.println("scale: " + scale);
        
        long iteration = 2;
        while(curr.compareTo(prev)!=0){
            
            prev = curr;
            
            BigDecimal iter = new BigDecimal(iteration);
            BigDecimal pow = iter.multiply(TWO).subtract(ONE);
            
            BigDecimal top    = jmath.pow(bd, pow);
            BigDecimal bottom = jmath.factorial(pow);
            
            BigDecimal add_this = top.divide(bottom,safe_scale,BigDecimal.ROUND_HALF_UP);
            
            /*
            System.out.println(iter);
            System.out.println(pow);
            System.out.println(top);
            System.out.println(bottom);
            System.out.println();
             */
            
            if(iteration%2==0)
                curr = curr.subtract(add_this);
            else
                curr = curr.add(add_this);
            
            iteration++;
            //System.out.println(curr);
            //System.out.println(prev);
            //System.out.println();
        }//end - while(curr.compareto(prev)!=0)
        
        return curr.setScale(scale, BigDecimal.ROUND_HALF_UP);
    }
    
    public static void main (String xyz[]){
        jcalc_trig jm = new jcalc_trig(128, new jcalc_math(128));
    }
        
}


