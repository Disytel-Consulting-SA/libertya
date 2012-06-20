package org.openXpertya.report.jcalc;

//import java.util.regex.*;
import java.util.StringTokenizer;

import java.util.Stack;
import java.util.Vector;

import java.math.*;

import java.util.*;

//compiler used to be set to: {jdk.home}{/}bin{/}javac
//changed it to: 

/**
 *  
 * TODO.
 *
 *  <PRE>
 *
 *  FAULTS
 *      pi-.5*pi^2+(1/3)pi^3 doesn't do the correct substitution
 *
 *  < > >= <= need lasy rounding also, currently only != and ==  have it, make sure
 *      this is consistent, write up about 20-30 test cases using the var/scale tests
 *
 *  need to create a undef class. when ever zero is the denominator during a 
 *      divide this should be returned. any operation done on undef will return
 *      undef
 *
 *  get rid of all BigDecimal.valudeOf(LONG) and replace with string
 *      don't want any funny rounding issues out there
 *
 *  rounding needs fixed!!!!! using pi, sqrts, trig functions things get goofy
 *      create an internal function that will determine if a number should be rounded or not
 *
 *  test and other commands need to return in a more fashionable kind of way, clear needs to return nothing
 *
 *  write a test program that'll take a range of scales, and a range of numbers
 *      for each one of those it'll run the test harnes, substituting the vars
 *      with the current number of the test harness iteration, enable multiple variables
 *
 *  test the heck out of trig (see above)
 *      (both of these need to be tested with all ranges
 *      do all equalities on that one web site
 *      do all sin(pi/N) for {1..12} for those that make sense
 *
 *  should i have some type of pi function to enable REALLY long values of pi? would need to
 *      avoid having extra scale chopped of upton returning to the Calculator class. maybe 
 *      pass back a string?
 *
 *  enable commands to change following from the command line:
 *  (this would need a clean return type)
 *      toggle
 *          lazy equality
 *          dirty rounding
 *  
 *  get rid of repeating functions in PI class and then clean up
 *      the jcalc_math class
 *
 *  add some function to return number of numbers left of decimal place
 *  add some function to return the Nth number left of the decimal place
 *  add some function to return the Nth number right of the decimal place
 *  
 *  implement checking for isInteger by just looking at the scale
 *  need to do error checking for non existent file in the CalculatorTester
 *
 *  AFTER ALL OF THE FOLLOWING IS ADDED
 *
 *  e = 1/0! + 1/1! + 1/2! + 1/3! + 1/4! + ... 
 *  If you need K decimal places, compute each term to K+3 decimal places and add them up. You can stop adding after the term 1/n! where n! > 10K+3
 *
 *  need to test
 *      setscale(INT)==(true||false)
 *      sin(pi/3)==.86602540378443864676372317075293618347140262690519031402790348972596650845440001854057309337862428783781307070770335151498497255
 *      sin(pi/3)*2==1.7320508075688772935274463415058723669428052538103806280558069794519330169088000370811461867572485756756261414154067030299699451
 *
 *  lists need .. to create a range, only works with integers {-2 .. 9 }, or maybe createList(-2,9)
 *      this would need to alter the " " deletetion 
 *  
 *  RESUBMIT
 *
 *
 *
 *  RELATED TO HELP
 *      put in a side note related to !, ==, 5!==120
 *      not related functions only work on boolean values
 *           as this operation makes little sense in combination with the "infinite word size" abstraction provided by this class
 *      11xb==3==binary(11xb) failes because you are mixing numbers and booleans during evaluation
 *      make side note for purists: &|^~ can not be used in ints
 *          | is for defineing temp vars
 *          ^ is for powers
 *          ~ is only for boolean
 *          && is for boolean and integers
 *      make a note about the fact that operan count checking is doine AFTER the equation has been put into rpn
 *          this will be put into the infix to rpn conversion eventually, this giving the user the ability to
 *          see WHERE the error is
 *      entry does a direct substitution 
 *          so:
 *              9+8 followed by
 *              1+3entry(1)3   => 1+38+83       => 123
 *              1+3(entry(1))3 => 1+3(1+39+83)3 => 1108
 *          1+3*del(INT) will give a "unknown string" because the valculator only looks
 *              for del at the BEGINNING of the equation
 *      << and >> can't have negatives because that bit gives freaked out answers
 *
 *      mod and % with negatives
 *          in perl: (-10)%3 ==  2
 *          in java: (-10)%3 == -1 
 *                mod(-10,3) ==  2
 *          jcalc:   (-10)%3 ==  2
 *
 *          -27%8 == 5
 *      
 *
 *      setScale
 *          cannot go less then 16
 *          if a variable has a scale of 30, goes down to 16, then back up to 30, it'll be recalculated, 
 *          so as to save memory
 *
 *  COMMANDS TO ADD
 *      printenv, printall, startover, 
 *
 *
 *  FUNCTIONS TO ADD  
 *      rpn reverse_polish_notation
 *
 *</PRE>
 */
public class Calculator {
    
    
    /**
     *  Converts an infix equation to a reverse polish notation equation. Currently
     *  only used internally, but developers could use this to display an equation
     *  in rpn.
     *
     *  @param String - infix equation
     *  @returns Vector - reverse polish notation equation
     */    
    public Vector infix_to_rpn(String equation) throws CalculatorException {

        Stack op_stack       = new Stack();
        Vector polish_vector = new Vector();
        Vector tokens        = new Vector();

        //this shouldn't be hardcoded... (?)
        String delims = "+/-*!^&=()<>|,%~";
        
        //
        // checking for correct number of parens here
        //
        {
            int parns=0;
            for(int i=0; i<equation.length(); i++){
                char c = equation.charAt(i);
                if(c==')'){
                    parns--;
                    if(parns<0){
                        throw new CalculatorException("unmatched parenthesis", i);
                    }
                }else if(c=='('){
                    parns++;
                }
            }
            
            if(parns>0){
                throw new CalculatorException("unclosed parenthesis");
            }
        }


        
        //
        // checking for and fixing 2+ character operators
        //
        {
            StringTokenizer equ_tokens = new StringTokenizer(equation, delims, true);
            TOKENS: while(equ_tokens.hasMoreTokens()){
                tokens.add(equ_tokens.nextToken());
            }
            
            Vector temp_tokens = new Vector();
            temp_tokens.add(tokens.elementAt(0));
            
            for(int i=1; i<tokens.size(); i++){
                String prev = (String)temp_tokens.lastElement();
                String curr = (String)tokens.elementAt(i);
                
                if(false){
                    System.out.println("temp tokens: " + temp_tokens);
                    System.out.println("prev: " + prev);
                    System.out.println("curr: " + curr);
                    System.out.println();
                }

                
                if(opCon.isOperator(prev+curr)){
                    temp_tokens.set(temp_tokens.size()-1, prev+curr);
                }else{
                    temp_tokens.add(curr);
                }
            }
            
            tokens = temp_tokens;
            //System.out.println("infix tokens: " + temp_tokens);
        }
        

        int prev = operatorChecker.BEGINNING;
        int cursor_pointer = -1;
        
        //keeps strack of how many parameters each function has
        //does this by counting commas inside its ()'s
        //delete this...
        Stack function_track = new Stack();
        class _func {
            public String name;
            public int count;
            public _func(String s, int i){
                name=s;
                count = i;
            }
        }
        
        TOKENS: for(int i=0; i<tokens.size(); i++){

            //String token = equ_tokens.nextToken();            
            String token = (String)tokens.elementAt(i);
            cursor_pointer+=token.length();

            if(false){
                System.out.println("tokens: " + tokens);
                System.out.println("here is the polish_vector: " + polish_vector);
                System.out.println("here is the op_stack: " + op_stack);
                System.out.println("token: " + token);
                System.out.println();
            }

            
            if(token.equals("(")){
                if(operatorChecker.isLegal(prev, operatorChecker.PAREN_OPEN)==2){
                    tokens.insertElementAt("*", i);
                    i--;
                    cursor_pointer-=2;
                    continue TOKENS;
                }else if(operatorChecker.isLegal(prev, operatorChecker.PAREN_OPEN)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }else if(prev != operatorChecker.FUNCTION && i<tokens.size() && ((String)tokens.elementAt(i+1)).equals(")")){
                    //testing for the odd chance the user will enter "()" not proceeded by a function
                    throw new CalculatorException("syntax error", cursor_pointer+1);
                }
                
                if(prev != operatorChecker.FUNCTION){
                    //checking to see if the previous element is a function
                    //if it was don't do anything, as the function name will
                    //be used as the opening paren in the operator stack
                    op_stack.push(token);   
                }
                
                prev = operatorChecker.PAREN_OPEN;
                //System.out.println("setting prev for (: " + prev);
                continue TOKENS;
            }else if(token.equals(")")){
                if(operatorChecker.isLegal(prev, operatorChecker.PAREN_CLOSE)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }
                prev = operatorChecker.PAREN_CLOSE;
                
                clearOpStack_For_CLOSE_PAREN(op_stack, polish_vector);
                
                //System.out.println("just set prev to " + prev);
                continue TOKENS;
            }else if(opCon.isFunction(token)){
                if(operatorChecker.isLegal(prev, operatorChecker.FUNCTION)==2){
                    tokens.insertElementAt("*", i);
                    i--;
                    continue TOKENS;
                }
                if(operatorChecker.isLegal(prev, operatorChecker.FUNCTION)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }
                
                //added for list functions (functions that can take unlimited args, like min
                if(opCon.operandsRequired(token)==-1){
                    //polish_vector.add( "(" );
                }
                
                polish_vector.add("(");
                
                op_stack.push(token);
                
                prev = operatorChecker.FUNCTION;
                continue TOKENS;
            }
            
            
            //determine if you are dealing with a delim or operator
            else if(opCon.isOperator(token)){
                //System.out.println("operator token: " + token);
                int current = opCon.typeOfOperator(token);
                
                //System.out.println("seeing prev,curr,cursor_pointer " + prev +","+ current +","+ cursor_pointer);
                
                if(operatorChecker.isLegal(prev, current)==0){
                    //if there was an error here there is the chance that
                    //it was a negative sign                    
                    if(token.equals("-") && operatorChecker.isLegal(prev, operatorChecker.UNARY_LEFT)==1){
                        token = "#";
                        current = operatorChecker.UNARY_LEFT;
                    }else{
                        throw new CalculatorException("syntax error", cursor_pointer);
                    }
                }
                
                POSITION_OP: while(true){

                    if(op_stack.isEmpty()){
                        op_stack.push(token);
                        break POSITION_OP;
                    }

                    int op_of_token = opCon.getPrecedence(token);
                    int op_of_top = opCon.getPrecedence((String)op_stack.peek());
                    
                    //System.out.println("???: "+ (100%17%8));
                    
                    //if(op_of_token.compareTo(op_of_top)>0){
                    if(op_of_token>op_of_top){
                        op_stack.push(token);
                        break POSITION_OP;
                    }
                    else if(op_of_token<=op_of_top){
                        //System.out.println(polish_vector);
                        if(opCon.rightToLeft(token) && op_of_token==op_of_top){
                            op_stack.push(token);
                            break POSITION_OP;
                        }
                        
                        while(!op_stack.isEmpty() && op_of_token<=opCon.getPrecedence((String)op_stack.peek())){
                            //System.out.println("in: " + polish_vector);
                            //System.out.println("??");
                            polish_vector.add( op_stack.pop() );
                        }
                        op_stack.push(token);
                        break POSITION_OP;
                        //System.out.println(polish_vector);
                    }
                }//end - POSITION_OP: while(true)
                
                prev = current;
                continue TOKENS;
            }//end - if(delims.indexOf(token)>-1){
            else if(variables.isVariable(token)){
                if(operatorChecker.isLegal(prev, operatorChecker.VARIABLE)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }
                polish_vector.add(token);
                prev = operatorChecker.VARIABLE;
                continue TOKENS;                
            }
            
            
            else if(token.equals(",")){
                if(operatorChecker.isLegal(prev, operatorChecker.COMMA)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }
                //this.clearOpStack_For_CLOSE_PAREN(op_stack, polish_vector);
                EMPTY: while(!op_stack.empty() && 
                             //!op_stack.peek().equals("(") && 
                             !opCon.isFunction((String)op_stack.peek())
                ){
                    polish_vector.add(op_stack.pop());
                }
                
                if(op_stack.empty()){
                    throw new CalculatorException("syntax error: , appears to have been used illegally", cursor_pointer);
                }
                
                prev = operatorChecker.COMMA;
                continue TOKENS;
            }
            else if(unary_commands.contains(token)){
                if(operatorChecker.isLegal(prev, operatorChecker.U_COMMAND)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }
                polish_vector.add(token);
                prev = operatorChecker.U_COMMAND;
                continue TOKENS;
            }
            /*
            else if(unary_calls.contains(token)){
                if(operatorChecker.isLegal(prev, operatorChecker.U_CALL)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }
                polish_vector.add(token);
                prev = operatorChecker.U_CALL;
                continue TOKENS;
            }
            else if(return_calls.contains(token)){
                if(operatorChecker.isLegal(prev, operatorChecker.R_CALL)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }
                polish_vector.add(token);
                prev = operatorChecker.R_CALL;
                continue TOKENS;
            } 
             */           
            else if(CalcMachineNumber.isMachineNumber(token)){
                //System.out.println(token + " is a machine number");
                if(operatorChecker.isLegal(prev, operatorChecker.NUMBER)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }

                tokens.setElementAt(new CalcMachineNumber(token), i);
                polish_vector.add(tokens.elementAt(i));
                prev = operatorChecker.NUMBER;
                continue TOKENS;
            }
            
            //determine if you are dealing with a number, if so, put it in num_stack
            try {
                BigDecimal num = new BigDecimal(token);
                
                //System.out.println("num: " + num);
                if(operatorChecker.isLegal(prev, operatorChecker.NUMBER)==2){
                    tokens.insertElementAt("*", i);
                    i--;
                    cursor_pointer-= token.length();
                    continue TOKENS;
                }
                
                if(operatorChecker.isLegal(prev, operatorChecker.NUMBER)==0){
                    throw new CalculatorException("syntax error", cursor_pointer);
                }
                
                polish_vector.add(num);
                prev = operatorChecker.NUMBER;
                continue TOKENS;
            } catch (NumberFormatException exception){
                //not a number
                //this could be where you look for speical cases
                //  10E-1  1.23E+3  9.23e2
            }

            
            
            //this is where i have to convert things like:
            //1max(1,2) and 3pi
            throw new CalculatorException("unknown string: " + token);
            
        }//end - TOKENS
        //this is the end of reading in infix and converting it to rpn
        
        if(operatorChecker.isLegal(prev, operatorChecker.END)==0){
            //not sure about this whole +1 crap...
            throw new CalculatorException("syntax error", cursor_pointer+1);
        }        

        while(!op_stack.isEmpty()){
            polish_vector.add(op_stack.pop());
        }

        //System.err.println("here is the final polish vector: " + polish_vector);
        return polish_vector;
    }//end - public Vector convert(String equation, Calculator calc)
    
    
    private void clearOpStack_For_CLOSE_PAREN(Stack s, Vector v){
        while(!s.empty()){
            if(opCon.isFunction(s.peek().toString())){
                v.add( s.pop());
                return;
            }else if(s.peek().equals("(")){
                s.pop();
                return;
            }else{
                v.add(s.pop());
            }
        }
    }
    
    /**
     *  Converts an equation, in reverse polish notation, into a string. The String
     *  could be a number, or a boolean, or a CalcMachineNumber.<BR>
     *  <BR>
     *  Currently only called within the Calculator class, but it could be used
     *  by other developers.
     *
     *  @param Vector - an equation in reverse polilsh notation
     *  @returns String - the result
     *
     */
    public String evaluate_rpn(Vector polish_vector) throws CalculatorException {

        Stack num_stack = new Stack();
        
        for(int i=0; i<polish_vector.size(); i++){
            Object obj = polish_vector.elementAt(i);
            
            if(obj instanceof BigDecimal        || 
               obj.equals("(")                  ||
               obj instanceof CalcMachineNumber || 
               variables.isVariable(obj.toString())
            ){
                //|| obj.equals("(") added for list functions
                num_stack.push(obj);
            }
            else if(opCon.isOperator(obj.toString()) || opCon.isFunction(obj.toString())){
                String operator = (String)obj;
                Vector operans = new Vector();
                
                int need = opCon.operandsRequired(operator);
                
                if(need>0 && num_stack.size()< need){
                    throw new CalculatorException(obj + " does not have enough parameters");
                }
                
                if(opCon.isFunction(obj.toString())){
                    while(!num_stack.peek().equals("(")){
                        operans.add(num_stack.pop());
                    }
                    num_stack.pop();
                } else {
                    for(;need>0; need--){
                        operans.add(num_stack.pop());
                    }
                }
                
                Object return_value;
                try {
                    //System.out.println("operator,operands " + operator +","+ operans);
                    return_value = opCon.evaluate_operator(operator, operans);
                    
                    if(return_value instanceof BigDecimal){
                        //System.out.println(return_value);
                        //return_value = jcalc_math.dirtyRound((BigDecimal)return_value, opCon.getScale());
                        //System.out.println(return_value);
                    }
                    //System.out.println("return: " + return_value + "\n");
                       
                } catch(CalculatorException ce){
                    if(ce.getMessage().equals(operator + " is an unknown function")){
                        throw new CalculatorException("PROGRAM ERROR: " + operator + " is not defined properly. " +
                                                      "Please contact author.");
                    }else{
                        throw ce;
                    }
                } 

                num_stack.push(return_value);
            }//end - if(operators.contains(obj))
            else if(unary_commands.contains(obj.toString())){
                Boolean return_value = new Boolean(this.execute_command(obj.toString()));
                return return_value.toString();
            }
            
            else {
                throw new CalculatorException("PROGRAM ERROR: " + obj + " caused error when converting rpn");
            }
        }//end - for(int i=0; i<polish_vector.size(); i++)
        
        
        
        //
        // these need to be converted into CalculatorExceptions, thrown, and caught
        //
        if(num_stack.size()>1){
            throw new CalculatorException("PROGRAM ERROR: the current equation executed faulty code, please contact the author"); 
        }else if(num_stack.size()==0){
            throw new CalculatorException("PROGRAM ERROR: the current equation executed faulty code, please contact the author");
        }

        Object obj = num_stack.pop();
        
        if(variables.isVariable(obj.toString())){
            return variables.variableValue(obj.toString(), opCon.getScale() ).toString();
        }

        if(obj instanceof CalcMachineNumber && polish_vector.size()==1){
            return ((CalcMachineNumber)obj).getValue().toString();
        }
        
        if(obj instanceof BigDecimal){
            BigDecimal bd = (BigDecimal)obj;
            //System.out.println("scale:  " + opCon.getScale());
            //System.out.println(bd);
            bd = jcalc_math.dirtyRound(bd, opCon.getScale());
            //System.out.println(bd);
            return bd.toString();
        }
        
        return obj.toString();
    }//end - static public String evaluate(Vector polish_vector)
    
    
    /**
     *  Executes calculator specific commands. Currently, the only commands
     *  are clear and print. More should be added. How and where this gets 
     *  called will be changing as the code gets cleaned up.
     *
     */    
    public boolean execute_command(String command){
        
        if(command.equals("clear")){
            entries.clear();
            return true;
        }
        else if(command.equals("print")){
            Vector ents = this.entries.getAllEntries();
            for(int i=0; i<ents.size(); i++){
                if(i%2==0){
                    System.out.print((String)ents.elementAt(i)+"==");
                }else{
                    System.out.println((String)ents.elementAt(i));
                }
            }
            return true;
        }
        else if(command.equals("test")){
            CalculatorTester tester = new CalculatorTester();
            if(tester.test()){
                System.out.println("pass");
                return true;
            }else{
                System.out.println("fail");
                return false;
            }            
        }
        
        return false;
    }

    
    private String commandSubstitution(String equation) throws CalculatorException {
        //
        // doing ans/entry/del substitution here
        // this is ugly, but don't know a better way...
        // maybe i'll hit my head sometime and i'll figure it out
        //

        if(equation.startsWith("del(")){
            int closer = equation.indexOf(")", 4);

            String value = equation.substring(4,closer);

            if(equation.length()>(4+value.length()+1)){
                throw new CalculatorException("syntax error", (5+value.length()));
            }
                
            
            int getThis = -1;

            try {
                getThis = (new Integer(value)).intValue();
            } catch (Exception e){
                throw new CalculatorException("del requires one integer");
            }
            
            entries.delete(getThis);
            
            equation = "";
            
        }
        
        while(equation.indexOf("ans(")>-1 || equation.indexOf("entry(")>-1){

            //System.out.println("debug: " + entries.getEntry(1));

            String replacing;
            int position;
            if(equation.indexOf("ans(")>-1){
                replacing = "ans(";
                position = equation.indexOf("ans(");
            }else{
                replacing = "entry(";
                position = equation.indexOf("entry(");
            }


            int closer = equation.indexOf(")", position);

            String value = equation.substring(position+replacing.length(),closer);

            int getThis = -1;

            try {
                getThis = (new Integer(value)).intValue();
            } catch (Exception e){
                throw new CalculatorException(replacing + " requires one integer");
            }

            String new_string;
            if(replacing.equals("ans(")){
                new_string = entries.getAns(getThis);
            }else{
                new_string = entries.getEntry(getThis);
            }

            StringBuffer sb = new StringBuffer(equation);
            sb.delete(position,closer+1);
            sb.insert(position,new_string);


            equation = sb.toString();
        }

        
        
        return equation;
    }
    
    
    
    /**
     *  Given an expression, as a string, this will return the result, as a string.
     *  This may be the only method you will need to use from this entire package. <BR>
     *  <BR>
     *  This will always return a result, unless an empty string is supplied, in
     *  which case, an empty string will be returned.<BR>
     *  <BR>
     *  The only exception that should be thrown is a CalculatorException. If any other
     *  exception is thrown it is a program error and the author would be very interested
     *  in seeing the input that caused this.
     *
     *  @param String - an expression
     *  @return String - the result
     *  @throws CalculatorException
     *  @see evaluate_equation_and_add
     *  
     */    
    public String evaluate_equation(String equation) throws CalculatorException {
        if(equation==null || equation.equals("")){
            return "";
        }
        
        equation = equation.toLowerCase();
        
        //killing white space (this is sloppy but short)
        StringBuffer sb = new StringBuffer(equation);
        while(equation.indexOf(" ")>=0){
            sb.deleteCharAt(equation.indexOf(" "));
            equation = sb.toString();
        }
        
        equation = this.commandSubstitution(equation);

        Vector polish_vector = this.infix_to_rpn(equation);
        //System.out.println("rpn: " + polish_vector);

        String result        = this.evaluate_rpn(polish_vector);
        //System.out.println("non modified result: " + result);

        return this.formatResult(result);
    }//end - public String evaluate(String equation)
    
    
    
    /**
     *  Similar to evaluate_equation, with the exception that the answer and the result
     *  are kept by the Calculator. This enables users to use entry(INT), answer(INT)
     *  and del(INT), and allows GUI's to display the history of user input.
     *
     *  @param String - an expression
     *  @return String - the result
     *  @throws CalculatorException
     *  @see evaluate_equation
     */    
    public String evaluate_equation_and_add(String equation) throws CalculatorException {
        if(equation==null || equation.equals("")){
            return "";
        }        
        
        equation = this.commandSubstitution(equation);
        String result = this.evaluate_equation(equation);
        if(result.equals("")){
            return "";
        }
        entries.addEntry(equation,result);
        return this.formatResult(result);
    }//end - public String evaluate(String equation)    
    
    /* Attempts to format the result in a purrty way, by stripping leading and trailing zeros.
     *
     *  @param String
     *  @return String
     */
    public String formatResult(String result){
        //could use reg expressions here but it wasn't worth it at the time...
        
        if(result==null || result.equals("")){
            return result;
        }
        
        
        //1 get rid of tailing zeros
        //2 get rid of tailing period
        //3 get rid of leadign zero
        StringBuffer s = new StringBuffer(result);
        
        //1
        int pointer=s.length()-1;
        if(result.indexOf(".")>0){
            while(pointer>0 && s.charAt(pointer)=='0'){
                s.deleteCharAt(pointer);
                pointer--;
            }
        }
        
        //2
        pointer = s.length()-1;
        if(s.charAt(pointer)=='.'){
            s.deleteCharAt(pointer);
        }
        
        //3
        if(s.length()>1 && s.charAt(0)=='0'){
            s.deleteCharAt(0);
        }
        
        return s.toString();
    }//end - public formatResult(String result)
    
    
    /**
     *  Used to add entries into the history list of the current Calculator object. <BR>
     *  <BR>
     *  Currently, this is only being called within the Calculator class, but this could
     *  be used, for example, by a GUI to read in saved sessions.
     */
    public void addEntry(String equation, String result){
        entries.addEntry(equation,result);
    }
    
    
    /**
     *  Enables other classes to be alerted to any changes in the history list. <BR>
     *  <BR>
     *  This is provided to take the headache out of keeping track of when there have
     *  been any updates (additions of deletions) from the history list.
     * 
     *  @param Object - the object interested in a change of status to the history list.
     *
     */
    public void tellMeAboutEntries(Observer obj){
        entries.addObserver(obj);
    }    
    
    
    /** 
     *  Main is in most of my classes, for no other reason than I am lazy. Main contains
     *  code to execute the test harness for the Calculator class. While developing I like
     *  to hit one button, (no matter which class I am editing) and run the test harness.
     */
    public static void main(String[] args) {
        //System.out.println( (1^3<<100>>99|2 ) );
        boolean runTester = true;
        
        if(runTester){
            CalculatorTester tester = new CalculatorTester();
            if(tester.test()){
                System.out.println("pass");
            }else{
                System.out.println("fail");
            }
        }else{
            BigDecimal bd = new BigDecimal("1.0015");
            System.out.println(bd.setScale(3,BigDecimal.ROUND_HALF_DOWN));
            System.out.println(bd.setScale(3,BigDecimal.ROUND_HALF_EVEN));
            System.out.println(bd.setScale(3,BigDecimal.ROUND_HALF_UP));
            System.out.println(bd.setScale(3,BigDecimal.ROUND_UP));
            
            Calculator calc = new Calculator();
            try{
                String s= "10^-1";
                System.out.println(calc.evaluate_equation_and_add(s));
                s= "ans(1)";
                System.out.println(calc.evaluate_equation(s));

                
                
            }catch (Exception e){
                System.out.println("exception was caught: " + e);
                e.printStackTrace();
            }
        }
    }//end - public static void main(String[] args)    


    /**
     *  I think this is going to be private...
     */
    public Entries entries = new Entries();

    Vector unary_commands = new Vector();
    String[] u_commands = {"clear", "print", "printenv", "printall", "startover", "test"};

    VariableTable variables;
    OperatorControlCenter opCon;

    public Calculator(){
        this(32);
        //System.out.println("empty");
    }//end - public Calculator
    
    public Calculator(int scl){
        //System.out.println("full");
        int scale = scl;
        for(int i=0; i<u_commands.length; i++){
            unary_commands.add(u_commands[i]);
        }
        
        variables = new VariableTable(scale);
        opCon = new OperatorControlCenter(variables,scale);        

        /*
        BigDecimal oone = new BigDecimal(".01");
        BigDecimal HUNDRED = new BigDecimal("100");
        
        for(BigDecimal holder=oone; holder.compareTo(HUNDRED)<1; holder=holder.add(oone)){
            System.out.println("("+holder+")^2=="+holder);
        }
        System.exit(0);
         */
    }

    
    
}//end - class Calculator

