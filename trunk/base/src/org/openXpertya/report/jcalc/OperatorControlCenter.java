package org.openXpertya.report.jcalc;


import java.util.Stack;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Random;

import java.math.*;


public class OperatorControlCenter {

    
    public int getPrecedence(String s){
        return ((Integer)precedenses.get(s)).intValue();
    }
    
    public int operandsRequired(String s){
        return ((Integer)operandsRequired.get(s)).intValue();
    }
    
    public int typeOfOperator(String s){
        return ((Integer)type_of_op.get(s)).intValue();
    }
    
    public boolean isOperator(String s){
        return operators.contains(s);
    }
    
    public boolean isFunction(String s){
        return functions.contains(s);
    }
    
    private int getLegalOperands(String s){
        return ((Integer)legalOperands.get(s)).intValue();
    }
    
    private int noNegatives(String s){
        return ((Integer)no_negatives.get(s)).intValue();
    }
    
    public boolean rightToLeft(String s){
        if(s.equals("^")||
           //s.equals("!")||
           s.equals("#")||
           s.equals("~")
        ){
            return true;
        }
        return false;
    }

    public int getScale(){
        return scale;
    }
    
    public boolean setScale(int i) {
        boolean good = true;
        if(i<16){
            scale = 16;
            good = false;
        }
        else
            scale = i;
        
        jtrig.setScale(scale);
        
        return good;
    }//end - public int setScale(int i)
    
    Hashtable precedenses = new Hashtable();
    
    //how many operands are required for each operator/funtion
    Hashtable operandsRequired = new Hashtable();
    
    //binary (l|r)unary
    Hashtable type_of_op = new Hashtable();
    
    Hashtable no_negatives = new Hashtable();
    
    Hashtable legalOperands = new Hashtable();
    
    Vector operators = new Vector(0,1);
    Vector functions = new Vector(0,1);
    
    VariableTable variables;
    
    int scale;

    
    jcalc_math jmath;// = new jcalc_math(scale);
    jcalc_trig jtrig;// = new jcalc_trig(scale,jmath);
    
    //take ( and ) out of here
    //might cause problems when editing code for infix to rpn
    //DON'T PAY ANY ATTENTION TO ops_type, AS THESE VALUES ARE CONVERTED
    //  INTO THE operatorChecker VALUES WHEN THE CALCULATOR IS INITIALIZED
    String[] ops             = {"!", "^",  "~", "#", "*", "/", "%",  "+", "-",  ">>", "<<", ">=", "<=", ">", "<",  "==", "!=",  "&&", "^^", "||",   "(",  ")"};
    int[] ops_pres           = { 10,   9,    8,   9,   7,   7,   7,    6,   6,     5,    5,    3,     3,  3,   3,     2,    2,     4,	 4,    4,    -2,   -2};
    int[] nums_needed        = {  1,   2,    1,   1,   2,   2,   2,    2,   2, 	   2,    2,    2,     2,  2,   2,     2,    2,     2,	 2,    2,     0,    0};
    int[] ops_legal_operands = {  1,   0,    4,   0,   0,   0,   1,    0,   0, 	   1,    1,    0,     0,  0,   0,     3,    3,     2,	 2,    2,     0,    0};
    int[] ops_type           = {  2,   0,    1,   1,   0,   0,   0,    0,   0, 	   0,    0,    0,     0,  0,   0,     0,    0,     0,	 0,    0,     3,    4};    
    int[] no_neges           = {  1,   0,    0,   0,   0,   0,   1,    0,   0,     2,    2,    0,     0,  0,   0,     0,    0,     1,    1,    1,     0,    0};
    
    String[] funcs             = {"abs", "and", "andnot", "avg", "min", "max", "sum", "negate", "or", "nor", "xor", "onebits", "bitlength", "flipbit", "not", "setbit", "clearbit", "testbit", "gcd", "mod", "pow", "remainder", "random", "round", "binary", "octal", "hex", "roman", "sqrt", "scale", "setscale"};
    int[] funcs_nums_needed    = {    1,    -2,        2,    -1,    -1,    -1,    -1,        1,   -2,    -2,    -2,         1,           1,         2,     1,        2,          2,         2,     2,     2,     2,           2,        1,       1,        1,       1,     1,       1,      1,       1,          1};
    int[] funcs_legal_operands = {    0,     2,        4,     0,     0,     0,     0,        0,    2,     4,     2,         1,           1,         1,     4,        1,          1,         1,     1,     1,     0,           0,        1,       0,        1,       1,     1,       1,      0,       0,          1};
    int[] no_negs              = {    0,     1,        1,     0,     0,     0,     0,        0,    1,     1,     1,         1,           1,         1,     1,        1,          1,         1,     0,     1,     0,           0,        1,       0,        1,       1,     1,       1,      1,       0,          1};
    
    final int NUM         = 0;
    final int INTS        = 1;
    final int INT_BOOLEAN = 2;
    final int NUM_BOOLEAN = 3;
    final int BOOLEAN     = 3;

    /*
     *ops_legal_operands
     *  0 - any number
     *  1 - ints only
     *  2 - int or boolean
     *  3 - num or boolean
     *  4 - boolean 
     *
     *ops_type
     *  0 - binary
     *  1 - unary left
     *  2 - unary right 
     */
    
    
    private String legal_operan_to_string(int i){
        if(i==0) return "Any number";
        if(i==1) return "Integers";
        if(i==2) return "Integers or booleans";
        if(i==3) return "Any number or boolean";
        if(i==4) return "Boolean";
        return "UNKNOWN";
    }
    private String operator_type_to_string(int i){
        if(i==0) return "binary";
        if(i==1) return "unary left";
        if(i==2) return "unary right";
        return "UNKNOWN";
    }
     
    private String allows_negatives_to_string(int i){
        if(i==0) return "True";
        if(i==1) return "False";
        if(i==2) return "First operan must be positive";
        return "UNKNOWN";
    }
    
    
    public OperatorControlCenter(VariableTable vt, int scale){
        this.scale = scale;
        
        jmath = new jcalc_math(scale);
        jtrig = new jcalc_trig(scale,jmath);

        
        if(!true){
            boolean xml = false;
            for(int i=0; i<ops.length; i++){
                if(xml){
                        System.out.println("<OPERATOR>");
                        System.out.println("\t<NAME> " +ops[i]+ " <\\NAME>");
                        System.out.println("\t<OPERANS> " +nums_needed[i]+ " <\\OPERANS>");
                        System.out.println("\t<LEGAL_OPERANS> " + legal_operan_to_string(ops_legal_operands[i]) + " <\\LEGAL_OPERANS>");
                        System.out.println("\t<NEGATIVES> " + allows_negatives_to_string(no_neges[i]) + " <\\NEGATIVES>");
                        System.out.println("\t<PRECEDENCE> " +ops_pres[i]+ " <\\PRECEDENCE>");
                        System.out.println("\t<TYPE> " +operator_type_to_string(ops_type[i])+ " <\\TYPE>");
                        System.out.println("<\\OPERATOR>");
                        System.out.println();
                } else {
                        System.out.println("OPERATOR: " + ops[i]);
                        System.out.println("\tOPERANS: " +nums_needed[i]);
                        System.out.println("\tLEGAL_OPERANS: " + legal_operan_to_string(ops_legal_operands[i]));
                        System.out.println("\tNEGATIVES:     " + allows_negatives_to_string(no_neges[i]));
                        System.out.println("\tPRECEDENCE:    " +ops_pres[i]);
                        System.out.println("\tTYPE:          " +operator_type_to_string(ops_type[i]));
                        System.out.println();                    
                }
            }
            
            for(int i=0; i<funcs.length; i++){
                if(xml){
                    System.out.println("<FUNCTION>");
                    System.out.println("\t<NAME> " +funcs[i]+ " <\\NAME>");
                    System.out.println("\t<OPERANS> " +funcs_nums_needed[i]+ " <\\OPERANS>");
                    System.out.println("\t<LEGAL_OPERANS> " + legal_operan_to_string(funcs_legal_operands[i]) + " <\\LEGAL_OPERANS>");
                    System.out.println("\t<NEGATIVES> " + allows_negatives_to_string(no_negs[i]) + " <\\NEGATIVES>");
                    System.out.println("<\\FUNCTION>");
                    System.out.println();
                } else {
                    System.out.println("FUNCTION: " +funcs[i]);
                    System.out.print("\tOPERANS: ");
                    if(funcs_nums_needed[i]<0){
                        System.out.println( Math.abs(funcs_nums_needed[i]) +"+");
                    }else{
                        System.out.println(funcs_nums_needed[i]);
                    }
                    System.out.println("\tLEGAL_OPERANS: " + legal_operan_to_string(funcs_legal_operands[i]));
                    System.out.println("\tNEGATIVES: " + allows_negatives_to_string(no_negs[i]));
                    System.out.println();                    
                }
            }
            //System.exit(0);
        }        
        
        
        variables = vt;
        
        for(int i=0; i<ops.length; i++){
            operators.add(ops[i]);
            precedenses.put(ops[i],    new Integer(ops_pres[i]));
            operandsRequired.put(ops[i], new Integer(nums_needed[i]));
             
            legalOperands.put(ops[i], new Integer(ops_legal_operands[i]));
            
            //this needs off set by one because of the look up table in 
            //the operatorChecker has the zero-throws element as being something
            //else, then it matches up with this scheme
            type_of_op.put(ops[i],     new Integer(ops_type[i]+1));
            
            no_negatives.put(ops[i], new Integer(no_neges[i]));
        }
        
        
        for(int i=0; i<funcs.length; i++){
            functions.add(funcs[i]);
            operandsRequired.put(funcs[i], new Integer(funcs_nums_needed[i]));
            precedenses.put(funcs[i],    new Integer(-99));

            legalOperands.put(funcs[i], new Integer(funcs_legal_operands[i]));
            
            no_negatives.put(funcs[i], new Integer(no_negs[i]));
        }
        
        
        //
        // adding trig functions
        //
        String [] tfuncs = jtrig.getFunctions();
        for(int i=0; i<tfuncs.length; i++){
            functions.add(tfuncs[i]);
            operandsRequired.put(tfuncs[i], new Integer("1"));
            precedenses.put(     tfuncs[i], new Integer(-99));
            legalOperands.put(   tfuncs[i], new Integer("0"));
            no_negatives.put(    tfuncs[i], new Integer("0"));
            
        }
        
    }//end - public OperatorControlCenter()

    

    public Object evaluate_operator(String operator, Vector operans) throws CalculatorException {
        
        int required_params = operandsRequired(operator);
        if(required_params==-1 && operans.size()<1){
            throw new CalculatorException(operator + " requires at least 1 parameter");
        }
        else if(required_params==-2 && operans.size()<2){
            throw new CalculatorException(operator + " requires at least 2 parameters");

        }
        else if(required_params>0 && required_params!=operans.size()){
            String throw_me = operator + " requires " + required_params + " parameter";
            if(required_params>1){
                throw_me += "s";
            }
            throw new CalculatorException(throw_me);
        }
        
        
        BigDecimal[] nums = null;
        BigInteger[] big_ints = null;
        boolean[] bools = null;
        
        boolean usingBooleans = true;
        boolean booleanFound = false;
        
        int current_operator_type = this.getLegalOperands(operator);
        
        
        //
        // doing variable substitution here
        //
        for(int i=0; i<operans.size(); i++){
            if(operans.elementAt(i) instanceof String && variables.isVariable((String)operans.elementAt(i))){
                String s = (String)operans.elementAt(i);
                operans.set(i, variables.variableValue(s,scale));
            }
        }
        
        //
        // looking for and converting machine numbers
        //
        for(int i=0; i<operans.size(); i++){
            if(operans.elementAt(i) instanceof CalcMachineNumber){
                CalcMachineNumber cmn = (CalcMachineNumber)operans.elementAt(i);
                operans.set(i, cmn.getValue());
            }
        }
        
        
        //
        // if everything isn't a boolean
        //     usingBooleans = false
        // if one boolean is found
        //     booleanFound = true
        //
        BOOL: for(int i=0; i<operans.size(); i++){
            Object obj = operans.elementAt(i);
            if(!(obj instanceof Boolean)){
                usingBooleans = false;
            }else{
                booleanFound = true;
            }   
        }
        
        if(!usingBooleans && booleanFound){
            throw new CalculatorException("booleans cannot be mixed with nonbooleans");
        }
        
        //i should actually using the class i set up for this...
        if(usingBooleans && (current_operator_type==this.NUM || current_operator_type==this.INTS) ){
            throw new CalculatorException(operator + " does not accept booleans");
        }
        
        
        //if an operator that only accepts booleans is being used make sure all operand ARE booleans
        if(!usingBooleans && current_operator_type==4){
            throw new CalculatorException(operator + " only accepts booleans");
        }
        
        
        //if not usuing booleans initialize the number array
        //otherwise initialzed the boolean array
        if(!usingBooleans){
            nums = new BigDecimal[operans.size()];
            for(int i=0; i<nums.length; i++){
                nums[i] = (BigDecimal)operans.elementAt(i);
                //System.err.print(nums[i] + " ");
            }
            
            if(noNegatives(operator)==1){
                for(int i=0; i<nums.length; i++){
                    if(nums[i].abs().compareTo(nums[i])!=0){
                        throw new CalculatorException(operator + " requires positive numbers only");
                    }
                }
            }
            if(noNegatives(operator)==2){
                if(nums[1].abs().compareTo(nums[1])!=0){
                    throw new CalculatorException(operator + " requires a positive number for the first parameter");
                }
            }
        }else{
            bools = new boolean[operans.size()];
            for(int i=0; i<bools.length; i++){
                bools[i] = ((Boolean)operans.elementAt(i)).booleanValue();
                //System.err.print(nums[i] + " ");
            }
        }

        
        //
        // if you aren't using booleans AND the operator type takes 
        //  booleans or numbers round the numbers with a (scale-1) for the compare
        //
        if(!usingBooleans && (current_operator_type==this.NUM_BOOLEAN)){
            for(int i=0; i<nums.length; i++){
                if(nums[i].scale()>=scale){
                    //System.out.println("taking off last element for compare....");
                    String num_string = nums[i].toString();
                    //System.out.println(nums[i]);
                    //nums[i] = new BigDecimal(num_string.substring(0, num_string.length()-2));
                    nums[i] = jcalc_math.dirtyRound(nums[i], scale-1);
                    //System.out.println(nums[i]);
                }
            }
        }        

        //if ints are required make sure they are all ints 
        //and then initialize the big_ints array
        if(current_operator_type==this.INTS ||
           (current_operator_type==this.INT_BOOLEAN && !usingBooleans)
        
        ){
            big_ints = new BigInteger[nums.length];
            for(int i=0; i<nums.length; i++){
                if(!nums[i].setScale(0, BigDecimal.ROUND_DOWN).equals(nums[i].setScale(0, BigDecimal.ROUND_UP))){
                    if(operandsRequired(operator)>1 ||operandsRequired(operator)<-1){
                        throw new CalculatorException("all numbers must be integers when using " + operator);
                    }else{
                        throw new CalculatorException(operator + " only accepts integers");
                    }
                }                               
                big_ints[i] = nums[i].toBigInteger();
            }
        }

        
        
        if(operator.equals("+")){
            return nums[1].add( nums[0] );
        }
        else if(jtrig.isFunction(operator)){
            return jtrig.execute(operator, operans);
        }
        else if(jmath.isFunction(operator)){
            return jmath.execute(operator, operans);
        }        
        else if(operator.equals("sqrt")){
            return jmath.sqrt(nums[0],scale);
        }        
        else if(operator.equals("-")){
            return nums[1].subtract( nums[0] );
        }
        else if(operator.equals("#")){
            return nums[0].negate();
        }        
        else if(operator.equals("*")){
            return nums[1].multiply( nums[0] );
        }
        else if(operator.equals("/")){
            if(nums[0].equals(BigDecimal.valueOf(0))){
                throw new CalculatorException("illegal divide by zero");
            }
            return nums[1].divide( nums[0], scale*2, BigDecimal.ROUND_HALF_UP  );
        }
        else if(operator.equals("^") || operator.equals("pow")){
            
            /*
             *implement it this way sometime...
             *
             *  double value = 1.0;

              if(n < 0) {
                x = 1.0/x;
                n = -n;
            }

            do {
             if(n & 1) value *= x;  //for odd
             n >>= 1;
             x *= x;
            } while (n);
            return value;
        */

            //System.out.println("coming into ^, scale: " + nums[0].scale());

            
            if(!nums[0].setScale(0, BigDecimal.ROUND_DOWN).equals(nums[0].setScale(0, BigDecimal.ROUND_UP))){
                throw new CalculatorException("non-integer exponents are currently not supported");
            }
            
            
            BigInteger integer_power = nums[0].toBigInteger();
            int int_power = nums[0].abs().intValue();
            //because i am using an int this is no longer "infinite"
            //granted 1.001^2300000 has about 1000 digits in it....

            
            if(integer_power.compareTo(BigInteger.ZERO)<1){
                if(integer_power.compareTo(BigInteger.ZERO)==0){
                    return new BigDecimal("1");
                }else{
                    //redunant else
                    //throw new CalculatorException("non-negative exponents are not supported");
                    nums[1] = BigDecimal.valueOf(1).divide(nums[1], scale, BigDecimal.ROUND_HALF_UP );
                    integer_power = integer_power.abs();
                    
                    //System.out.println("now: " + nums[1] + "^" + integer_power);
                }
            }
            
            BigDecimal left = new BigDecimal(nums[1].toString());
            BigDecimal last = new BigDecimal(nums[1].toString());
            
            BigInteger maxValue = BigInteger.valueOf((long)Integer.MAX_VALUE);
            if(maxValue.compareTo(integer_power)>0){
                for(; int_power>1; int_power--){
                //for(; integer_power.compareTo(BigInteger.ONE)>=1; integer_power = integer_power.subtract(BigInteger.ONE)){
                    //last = last.multiply(left).setScale((5+scale), BigDecimal.ROUND_HALF_EVEN);
                    //last = last.multiply(left);
                    //last = jcalc_math.dirtyRound(last.multiply(left), scale+15);
                    int this_scale = last.scale() + left.scale() + 5;
                    
                    //System.out.println(last.scale() +" "+left.scale());
                    //System.out.println(last.multiply(left));
                    
                    last = jcalc_math.dirtyRound(last.multiply(left), this_scale);

                    String num = last.toString();
                    if(last.scale()>scale){
                        FIX: while(num.indexOf('.')>-1 && num.charAt(num.indexOf('.')+1+scale)=='0'){
                            //System.out.println("trying to fix");
                            for(int i=1; i< (scale>>2); i++){
                                if(num.charAt(num.indexOf('.')+1+scale-i)!='0'){
                                    break FIX;
                                }
                            }
                            //System.out.println("fixing");
                            //System.out.println(last);
                            last = new BigDecimal(num.substring(0,num.indexOf('.')+1+scale));
                            //System.out.println(last);
                            break FIX;
                        }

                        FIX: while(num.indexOf('.')>-1 && num.charAt(num.indexOf('.')+1+scale)=='9'){
                            //System.out.println("trying to fix 9 ");
                            for(int i=1; i< (scale>>2); i++){
                                if(num.charAt(num.indexOf('.')+1+scale-i)!='9'){
                                    break FIX;
                                }
                            }
                            //System.out.println("fixing 9");
                            //System.out.println(last);
                            last = new BigDecimal(num.substring(0,num.indexOf('.')+1+scale));
                            BigDecimal shit = new BigDecimal("1");
                            last = last.add(shit.movePointLeft(scale));

                            //System.out.println(last);
                            break FIX;
                        }
                    }
                    //System.out.println(last);
                    //System.out.println(last.toString().substring(0, last.toString().indexOf(".")+scale));
                    //System.out.println(last.toString().substring(0, last.toString().indexOf(".")+scale+5));
                    //System.out.println(last.scale());
                    //System.out.println();
                    
                }
            }else{
                //System.out.println(integer_power + " is too big");
                System.err.println("WARNING: " + last +"^"+integer_power+ " may take a while");
                for(; integer_power.compareTo(BigInteger.ONE)>0; integer_power = integer_power.subtract(BigInteger.ONE)){
                    last = last.multiply(left).setScale((5+scale), BigDecimal.ROUND_HALF_UP);
                }                
            }
            
            //System.out.println(last);
            //System.out.println(last.setScale(scale, BigDecimal.ROUND_HALF_EVEN));
            //System.out.println(last.setScale(scale, BigDecimal.ROUND_UNNECESSARY));
            //System.out.println(last.setScale(scale, BigDecimal.ROUND_HALF_EVEN));
            //System.out.println(last.setScale(scale, BigDecimal.ROUND_HALF_EVEN));
            
            if(true){
                BigDecimal temp = last;
                for(int i=0; i<(scale-1); i++){
                    temp = temp.multiply(BigDecimal.valueOf(10));
                }
                //System.out.println("temp: " + temp);
                //System.out.println("last: " + last);
                //System.out.println("llll: " + last.setScale(scale, BigDecimal.ROUND_HALF_EVEN));
                //System.out.println("llll: " + last.setScale(scale, BigDecimal.ROUND_HALF_UP));
            }
            
            //return last.setScale(scale, BigDecimal.ROUND_HALF_UP);
            return last;
        }else if(operator.equals("!")){
            //this needs rewritten, it's lame
            if(big_ints[0].compareTo(BigInteger.ZERO)==0){
                return new BigDecimal("1");
            }
            
            BigInteger i = big_ints[0].subtract(BigInteger.ONE);
            BigInteger sum = big_ints[0];
            while(i.compareTo(BigInteger.ONE)>0){
                sum = sum.multiply(i);
                i=i.subtract(BigInteger.ONE);
            }
            return new BigDecimal(sum);
        }
        
        else if(operator.equals("<<")){
            if(big_ints[0].abs().compareTo( new BigInteger(String.valueOf(Integer.MAX_VALUE)) )>=0){
                throw new CalculatorException("shifting by more then " + Integer.MAX_VALUE + " currently not supported");
            }            
            return new BigDecimal(big_ints[1].shiftLeft(big_ints[0].intValue()));
        }
        else if(operator.equals(">>")){
            if(big_ints[0].abs().compareTo( new BigInteger(String.valueOf(Integer.MAX_VALUE)) )>=0){
                throw new CalculatorException("shifting by more then " + Integer.MAX_VALUE + " currently not supported");
            }            
            return new BigDecimal(big_ints[1].shiftRight(big_ints[0].intValue()));
        }
        else if(operator.equals("||")){
            if(!usingBooleans){
                return new BigDecimal(big_ints[1].or(big_ints[0]));
            }else{
                return new Boolean(bools[0] || bools[1]);
            }
        }
        else if(operator.equals("&&")){
            if(!usingBooleans){
                return new BigDecimal(big_ints[1].and(big_ints[0]));
            }else{
                return new Boolean(bools[0] && bools[1]);
            }
        }
        else if(operator.equals("^^")){
            if(!usingBooleans){
                return new BigDecimal(big_ints[1].xor(big_ints[0]));
            }else{
                return new Boolean(bools[0] ^ bools[1]);
            }
        }        
        
        
        else if(operator.equals("abs")){
            return nums[0].abs();
        }
        else if(operator.equals("and")){
            if(!usingBooleans){
                BigInteger anded = big_ints[0];
                for(int i=0; i<big_ints.length; i++){
                    anded = anded.and(big_ints[i]);
                }
                return new BigDecimal(anded);
            }else{
                for(int i=0; i<bools.length; i++){
                    if(bools[i]==false){
                        return new Boolean(false);
                    }
                }
                return new Boolean(true);
            }
        }
        else if(operator.equals("andnot")){
            //this would need to go in if it supported INTEGERS
            //return new BigDecimal(big_ints[1].andNot(big_ints[0]));
            return new Boolean(bools[1] && !bools[0]);
        }
        else if(operator.equals("avg")){
            BigDecimal sum = new BigDecimal(0);
            for(int i=0; i<nums.length; i++){
               sum = sum.add(nums[i]);
            }
            return sum.divide(BigDecimal.valueOf((long)nums.length), scale, BigDecimal.ROUND_UNNECESSARY);

        }
        else if(operator.equals("sum")){
            BigDecimal sum = new BigDecimal(0);
            for(int i=0; i<nums.length; i++){
               sum = sum.add(nums[i]);
            }
            return sum;

        }        
        else if(operator.equals("min")){
            BigDecimal min = nums[0];
            for(int i=0; i<nums.length; i++){
               if(nums[i].compareTo(min)<0){
                   min = nums[i];
               }
            }
            return min;
        }
        else if(operator.equals("max")){
            BigDecimal max = nums[0];
            for(int i=0; i<nums.length; i++){
               if(nums[i].compareTo(max)>0){
                   max = nums[i];
               }
            }
            return max;
        }
        else if(operator.equals("negate")){
            return nums[0].negate();
        }
        else if(operator.equals("or")){
            if(!usingBooleans){
                BigInteger current = big_ints[0];
                for(int i=0; i<big_ints.length; i++){
                    current = current.or(big_ints[i]);
                }
                return new BigDecimal(current);
            }else{
                for(int i=0; i<bools.length; i++){
                    if(bools[i]==true){
                        return new Boolean(true);
                    }
                }
                return new Boolean(false);
            }
        }
        else if(operator.equals("nor")){
            for(int i=0; i<bools.length; i++){
                if(bools[i]==true){
                    return new Boolean(false);
                }
            }
            return new Boolean(true);
        }
        else if(operator.equals("xor")){
            if(!usingBooleans){
                BigInteger current = big_ints[0];
                for(int i=0; i<big_ints.length; i++){
                    current = current.xor(big_ints[i]);
                }
                return new BigDecimal(current);
            }else{
                int numTrues = 0;
                for(int i=0; i<bools.length; i++){
                    if(bools[i]==true){
                        numTrues++;
                    }
                }
                if(numTrues==1){
                    return new Boolean(true);
                }else{
                    return new Boolean(false);
                }
            }
        }
        
        else if(operator.equals("onebits")){
            return new BigDecimal(big_ints[0].bitCount());
        }
        else if(operator.equals("bitlength")){
            return new BigDecimal(big_ints[0].bitLength());
        }
        else if(operator.equals("flipbit")){
            int location = big_ints[0].intValue();
            return new BigDecimal(big_ints[1].flipBit(location));
        }
        else if(operator.equals("negate")){
            return new BigDecimal(big_ints[0].negate());
        }
        else if(operator.equals("not")){
            //uncomment if not will accept INTEGERS
            //if(!usingBooleans){
            //    return new BigDecimal(big_ints[0].not());
            //}else{
                return new Boolean(!bools[0]);
            //}
        }
        else if(operator.equals("setbit")){
            return new BigDecimal(big_ints[1].setBit(big_ints[0].intValue()));
        }
        else if(operator.equals("clearbit")){
            return new BigDecimal(big_ints[1].clearBit(big_ints[0].intValue()));
        }
        else if(operator.equals("testbit")){
            //returning boolean
            return new Boolean(big_ints[1].testBit(big_ints[0].intValue()));
        }
        else if(operator.equals("gcd")){
            //System.out.println("1: " + big_ints[1]);
            //System.out.println("0: " + big_ints[0]);            
            return new BigDecimal(big_ints[1].gcd(big_ints[0]));
        }
        /*
        else if(operator.equals("mod") || operator.equals("%") ){
            return new BigDecimal(big_ints[1].mod(big_ints[0]));
        }
         */
        else if(operator.equals("remainder") || operator.equals("mod") || operator.equals("%")){

            BigDecimal left = nums[1].abs();
            BigDecimal right = nums[0].abs();
            
            if(nums[0].compareTo(BigDecimal.valueOf(0))==0){
                throw new CalculatorException(operator + " cannot use zero as the denominator");
            }
            
            if(nums[0].compareTo(nums[1])==0){
                return new BigDecimal(0);
            }
            
            while(left.compareTo(right)>=0){
                left = left.subtract(right);
            }

            if(left.compareTo(nums[1])>0){
               left = left.negate();
            }
            
            return left;
        }
        else if(operator.equals("random")){
            BigInteger max = new BigInteger( String.valueOf(Integer.MAX_VALUE));
            
            //this has GOT to be too long...
            if(big_ints[0].compareTo(max)>0){
                throw new CalculatorException("random range is 1 to " + Integer.MAX_VALUE);
            }else if(big_ints[0].compareTo(BigInteger.ZERO)==0){
                throw new CalculatorException("random range is 1 to " + Integer.MAX_VALUE);
            }
            
            Random ran = new Random();
            
            return new BigDecimal(String.valueOf(ran.nextInt(big_ints[0].intValue())));            
        }
        else if(operator.equals("round")){
            return nums[0].setScale(0,BigDecimal.ROUND_HALF_UP);
        }

        
        //
        // boolean operators: == != >= <= > <
        //
        else if(operator.equals("==")){
            if(usingBooleans){
                return new Boolean(bools[1]==bools[0]);
            }else{
                if(nums[1].compareTo(nums[0])==0){
                    return new Boolean(true);
                }else{
                    return new Boolean(false);
                }
            }
        }
        else if(operator.equals("!=")){
            if(usingBooleans){
                return new Boolean(bools[1]!=bools[0]);
            }else{
                if(nums[1].compareTo(nums[0])!=0){
                    return new Boolean(true);
                }else{
                    return new Boolean(false);
                }
            }
            
        }else if(operator.equals(">=")){
            if(nums[1].compareTo(nums[0])>=0){
                return new Boolean(true);
            }else{
                return new Boolean(false);
            }
        }else if(operator.equals("<=")){
            if(nums[1].compareTo(nums[0])<=0){
                return new Boolean(true);
            }else{
                return new Boolean(false);
            }
        }else if(operator.equals(">")){
            if(nums[1].compareTo(nums[0])>0){
                return new Boolean(true);
            }else{
                return new Boolean(false);
            }            
        }else if(operator.equals("<")){
            if(nums[1].compareTo(nums[0])<0){
                return new Boolean(true);
            }else{
                return new Boolean(false);
            }            
        }else if(operator.equals("~")){
                return new Boolean(!bools[0]);
        }
        

        else if(operator.equals("scale")){
            return new BigDecimal(nums[0].scale());
        }
        else if(operator.equals("setscale")){
            boolean pass = this.setScale(big_ints[0].intValue());
            return new Boolean(pass);            
        }
        
        else if(operator.equals("binary")){
            return CalcMachineNumber.binary(big_ints[0]);
        }
        else if(operator.equals("octal")){
            return CalcMachineNumber.octal(big_ints[0]);
        }
        else if(operator.equals("hex")){
            return CalcMachineNumber.hex(big_ints[0]);
        }
        else if(operator.equals("roman")){
            return CalcMachineNumber.arabicToRoman(big_ints[0]);
        }


        //i decided to leave "unknown operator here
        //  this way any class can call this and get a logical sounding error
        //as it is, only the Calculator class calls this,
        //  and if it catches this exception it'll convert it to
        //  a PROGRAM ERROR, as it shouldn't have sent the operator in here
        //  if it wasn't known to the OperatorControlCenter class
        //the Calculator class should only call operators/functions that the
        //  OperatorControlCenter said was legal
        throw new CalculatorException(operator + " is an unknown function");
        
    }//end - public Object evaluate_operator
    

    //take this out, it should be in here
    //just tired of switching windows to compile and run
    public static void main(String[] args) {

        CalculatorTester tester = new CalculatorTester();
        if(tester.test()){
            System.out.println("pass");
        }else{
            System.out.println("fail");
        }
    }//end - public static void main(String[] args)    
    
    
}//end - public class OperatorControlCenter

