package org.openXpertya.report.jcalc;

import java.math.*;
import java.util.*;

/**
 *  CalcMachineNumber is a JCalc specific class created for converting machine numbers.
 *  
 */
public class CalcMachineNumber {
    String machine;
    BigDecimal human = null;
    
    
    public CalcMachineNumber(String s){
        setValue(s);
        
    }
    
    public CalcMachineNumber(String s, BigInteger b){
        this.machine = s;
        this.human = new BigDecimal(b);
    }
    
    
    private void setValue(String s){
        //takes a machine number, such as 10101xb or FFxh or IVxR
        //and sets this machines values to it
        this.machine = s;
        if(s.charAt(s.length()-1)=='b'){
            this.human = binaryToDecimal(s.substring(0,s.length()-2));
        }else if(s.charAt(s.length()-1)=='o'){
            this.human = machineToDecimal(s.substring(0,s.length()-2), "3");
        }
        else if(s.charAt(s.length()-1)=='h'){
            this.human = machineToDecimal(s.substring(0,s.length()-2), "16");
        }else if(s.charAt(s.length()-1)=='r'){
            this.human = romanToArabic(s.substring(0,s.length()-2));
        }
    }
    
    public String toString(){
        return machine;
    }
    
    public BigDecimal getValue(){
        return human;
    }

    
    BigDecimal binaryToDecimal(String s){
        //takes a string of ones and zeros
        //expects nothign else, exception will be thrown otherwise
        BigDecimal result = new BigDecimal(0);
        BigInteger two = new BigInteger("2");
        StringBuffer reversed = new StringBuffer(s);
        reversed = reversed.reverse();
        int length = reversed.length();
        
        for(int i=0; i<length; i++){
            if(reversed.charAt(i)=='1'){
                result = result.add( new BigDecimal(two.pow(i)));
            }
        }
        
        return result;
    }

    
    BigDecimal machineToDecimal(String s, String shift_amount){
        BigDecimal result = new BigDecimal(0);
        BigInteger shift = new BigInteger(shift_amount);
        StringBuffer reversed = new StringBuffer(s);
        reversed = reversed.reverse();
        int length = reversed.length();
        
        //int return_this = 0;
        BigInteger return_this = new BigInteger("0");
        
        
        //getting value for first bit
        
        return_this = new BigInteger(String.valueOf(reversed.charAt(0)-48));
        if(return_this.compareTo(new BigInteger("10"))>0){
            return_this = return_this.subtract( new BigInteger(String.valueOf(39)) );
        }
        
        for(int i=1; i<length; i++){
            if(reversed.charAt(i)!='0'){
                int this_value = reversed.charAt(i)-48;
                if(this_value>10){
                    this_value-=39;
                }
                BigInteger this_v = new BigInteger( String.valueOf(this_value) );
                
                //in english, this says:
                //return_this += this_v * shift^i
                return_this = return_this.add(this_v.multiply(shift.pow(i)));
            }
        }
        
        return new BigDecimal(return_this);
    }


    static public CalcMachineNumber binary(BigInteger num) throws CalculatorException {
        if(num.compareTo(new BigInteger(String.valueOf(Integer.MAX_VALUE)))>0){
            throw new CalculatorException("converting to machine numbers over " + Integer.MAX_VALUE + " is currently not supported");
        }
        String h = Integer.toBinaryString(num.intValue());
        return new CalcMachineNumber(h+"xb", num);
    }
    
    
    static public CalcMachineNumber octal(BigInteger num) throws CalculatorException {
        if(num.compareTo(new BigInteger(String.valueOf(Integer.MAX_VALUE)))>0){
            throw new CalculatorException("converting to machine numbers over " + Integer.MAX_VALUE + " is currently not supported");
        }        
        String h = Integer.toOctalString(num.intValue());
        return new CalcMachineNumber(h+"xo", num);
    }
    
    
    static public CalcMachineNumber hex(BigInteger num) throws CalculatorException {
        if(num.compareTo(new BigInteger(String.valueOf(Integer.MAX_VALUE)))>0){
            throw new CalculatorException("converting to machine numbers over " + Integer.MAX_VALUE + " is currently not supported");
        }        
        String h = Integer.toHexString(num.intValue());
        return new CalcMachineNumber(h+"xh", num);
    }
    
    
    
    static boolean isMachineNumber(String s){

        int length = s.length();
        
        if(length<3){
            return false;
        }

        
        if(s.charAt(length-2)!='x'){
            return false;
        }
        
        if( !(s.charAt(length-1)=='b' || s.charAt(length-1)=='o' ||  s.charAt(length-1)=='h'||  s.charAt(length-1)=='r')){
            return false;
        }
        
        if(s.charAt(length-1)=='b'){
            for(int i=0; i<(length-2); i++){
                if( !(s.charAt(i)=='0' || s.charAt(i)=='1') ){
                    return false;
                }
            }
        }
        if(s.charAt(length-1)=='o'){
            for(int i=0; i<(length-2); i++){
                if( !(s.charAt(i)=='0' || s.charAt(i)=='1' || s.charAt(i)=='2') ){
                    return false;
                }
            }
        }        
        if(s.charAt(length-1)=='h'){
            for(int i=0; i<(length-2); i++){
                if( !(
                      (s.charAt(i)>='0' && s.charAt(i)<='9' ) 
                      ||
                      (s.charAt(i)>='a' && s.charAt(i)<='f' ) 
                     )
                ){
                    return false;
                }
            }
        }        
        if(s.charAt(length-1)=='r'){
            for(int i=0; i<(length-2); i++){
                if( !( s.charAt(i)=='m' ||
                       s.charAt(i)=='d' ||
                       s.charAt(i)=='c' ||
                       s.charAt(i)=='l' ||
                       s.charAt(i)=='x' ||
                       s.charAt(i)=='v' ||
                       s.charAt(i)=='i'
                     )
                ){
                    return false;
                }
            }            
        }
        
        return true;
    }


    static public CalcMachineNumber arabicToRoman(BigInteger arabic){
        int[]    numbers = { 1000,  900,  500,  400,  100,    90,  50,    40,    10,    9,   5,     4,   1 };
        String[] letters = {  "M", "CM",  "D",  "CD", "C",  "XC", "L",  "XL",  "X",  "IX", "V",  "IV", "I" };
        
        String roman = "";
        BigInteger original_num = new BigInteger(arabic.toString());
        
        for(int i=0; i<numbers.length; i++){
            while(arabic.compareTo(  new BigInteger(String.valueOf(numbers[i])) )>=0){
                roman += letters[i];
                arabic = arabic.subtract(new BigInteger(String.valueOf(numbers[i])));
           }
        }
        
        return new CalcMachineNumber(roman + "xR", original_num);
    }
    
    static public BigDecimal romanToArabic(String roman){
        int[]    numbers = { 1000,  900,  500,  400,  100,    90,  50,    40,    10,    9,   5,     4,   1 };
        String[] letters = {  "M", "CM",  "D",  "CD", "C",  "XC", "L",  "XL",  "X",  "IX", "V",  "IV", "I" };

        roman = roman.toUpperCase();  // Convert to upper case letters.
          
        int i = 0;       // A position in the string, roman;
        int arabic = 0;  // Arabic numeral equivalent of the part of the string that has
                           //    been converted so far.
          
        while (i < roman.length()) {
          
            char letter = roman.charAt(i);        // Letter at current position in string.
            int number = letterToNumber(letter);  // Numerical equivalent of letter.

            if (number < 0){
                //throw new CalculatorException("Illegal character \"" + letter + "\" in roman numeral.");
            }

            i++;  // Move on to next position in the string

            if (i == roman.length()) {
                // There is no letter in the string following the one we have just processed.
                // So just add the number corresponding to the single letter to arabic.
                arabic += number;
            }
            else {
                // Look at the next letter in the string.  If it has a larger Roman numeral
                // equivalent than number, then the two letters are counted together as
                // a Roman numeral with value (nextNumber - number).
                int nextNumber = letterToNumber(roman.charAt(i));
                if (nextNumber > number) {
                    // Combine the two letters to get one value, and move on to next position in string.
                    arabic += (nextNumber - number);
                    i++;
                }
                else {
                    // Don't combine the letters.  Just add the value of the one letter onto the number.
                    arabic += number;
                }
            }
        }

        
        //return BigDecimal(String.valueOf(arabic));
        return new BigDecimal(arabic);
    }
    

    static private int letterToNumber(char letter) {
             // Find the integer value of letter considered as a Roman numeral.  Return
             // -1 if letter is not a legal Roman numeral.  The letter must be upper case.
          switch (letter) {
             case 'I':  return 1;
             case 'V':  return 5;
             case 'X':  return 10;
             case 'L':  return 50;
             case 'C':  return 100;
             case 'D':  return 500;
             case 'M':  return 1000;
             default:   return -1;
          }
       }    
    
    
}
    

