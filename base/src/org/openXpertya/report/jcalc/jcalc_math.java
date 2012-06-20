package org.openXpertya.report.jcalc;

import java.math.*;
import java.util.Vector;

public class jcalc_math {
    private int scale, safe_scale;
    
    public static final BigDecimal ONE = new BigDecimal("1");

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

    public BigDecimal execute(String function, Vector operans) {
        function = function.toLowerCase();
        BigDecimal bd = (BigDecimal)operans.elementAt(0);
        
        if(function.equals("ln")){
            //return this.ln(bd);
        }
        
        return null;
    }
    
    
    
    public jcalc_math(int scale){
        this.setScale(scale);
        
        for(int i=0; i<funcs.length; i++){
            functions.add(funcs[i]);
        }
    }    
    
    public static BigDecimal factorial(BigDecimal bd){
        BigInteger bi = bd.toBigInteger();
        
        if(bi.compareTo(BigInteger.ZERO)==0){
            return new BigDecimal("1");
        }

        BigInteger i = bi.subtract(BigInteger.ONE);
        BigInteger sum = bi;
        while(i.compareTo(BigInteger.ONE)>0){
            sum = sum.multiply(i);
            i=i.subtract(BigInteger.ONE);
        }
        return new BigDecimal(sum);
    }
    
    public static BigDecimal pow(BigDecimal left, BigDecimal right){
        BigDecimal orig = new BigDecimal(left.toString());
        for(int i=1; i<right.intValue(); i++){
            left = left.multiply(orig);
        }
        return left;
    }    
    
    /**
     *  Attempts to correct rounding errors during arithmetic. Trys to convert numbers
     *  like .500000000001 to .5 or .499999999999999 to .5
     *
     *
     */    
    public static BigDecimal dirtyRound(BigDecimal bd, int scl){
        //System.out.println(bd.scale() +"="+ scl);
        
        if(scl>=bd.scale())
            return bd;
        
        String num = bd.toString();
        if(num.indexOf('.')==-1){
            return bd;
        }
        
        
        int length = num.length()-1;
        
        int dot = num.indexOf('.');
        int last_spot = dot+scl+1;
        int test_length = scl >> 1; 
        
        
        num = num.substring(0,last_spot);

        //System.out.println("\t"+bd);
        //System.out.println("\t"+num);
        
        NINE: while(num.charAt(last_spot-2)=='9'){
            //System.out.println("might be 9");
            
            for(int i=0; i<test_length; i++){
                if(num.charAt(last_spot-2-i)!='9'){
                    break NINE;
                }
            }
            BigDecimal new_bd = new BigDecimal(num.substring(0, num.length()-1));
            
            //if(false){
            //    System.out.println("\twas a nine that needed rounded");
            //    System.out.println("\t"+num);
            //    System.out.println("\t"+new_bd);
            //    System.out.println("\t"+bd);
            //}
            
            BigDecimal increment = ONE.movePointLeft(scl-1);
            //System.out.println("\t"+increment);
            return new_bd.add(increment);
        }
        
        ZERO: while(num.charAt(last_spot-2)=='0'){
            for(int i=0; i<test_length; i++){
                if(num.charAt(last_spot-2-i)!='0'){
                    break ZERO;
                }
            }
            BigDecimal new_bd = new BigDecimal(num.substring(0, num.length()-1));
            //System.out.println("\t"+num);
            //System.out.println("\t"+new_bd);
            return new_bd;
        }
        //System.out.println(num);

        
        return new BigDecimal(num);
    }
    
    static BigDecimal sqrt(BigDecimal bd, int scale){
        scale *= 2;
        int safe_scale = scale + 5;
        
        BigDecimal prev = new BigDecimal(bd.toString());
        BigDecimal two = new BigDecimal("2");
        
        BigDecimal curr = new BigDecimal(bd.toString());
        curr = curr.divide(two,safe_scale,BigDecimal.ROUND_HALF_UP);
        
        
        while(prev.compareTo(curr)!=0){
            prev = curr;
            
            BigDecimal top = curr.multiply(curr).subtract(bd);
            BigDecimal bottom = two.multiply(curr);
            BigDecimal result = top.divide(bottom,safe_scale,BigDecimal.ROUND_HALF_UP);
            //BigDecimal result = top.divide(bottom,safe_scale,BigDecimal.ROUND_HALF_EVEN);
            
            curr = curr.subtract(result);
        }
        
        //System.out.println(curr);
        //System.out.println(curr.setScale(scale, BigDecimal.ROUND_HALF_DOWN));
        return curr.setScale(scale, BigDecimal.ROUND_HALF_DOWN);
        //return curr.setScale(scale, BigDecimal.ROUND_HALF_EVEN);
    }    
    
    public int setScale(int scl){
        if(scl<16)
            scl=16;
        
        this.scale = scl;
        this.safe_scale = scl + 5;
        
        return scale;
    }
    
}
