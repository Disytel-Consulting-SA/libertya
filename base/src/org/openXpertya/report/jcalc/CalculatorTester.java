package org.openXpertya.report.jcalc;



import java.util.Vector;
import java.math.BigDecimal;
import java.io.*;


/*TODO
 *  enable this to read in from a file equation==string
 *  do some , error testing: (1,2) 1,2 
 *  do some too many paramet error checking: pow(1,2,3)
 */

public class CalculatorTester {
    
    boolean showpass = false;
 
    class CalculatorTest {
        public String equation = "";
        public String result   = "";
        public Exception exception;
        
        public CalculatorTest(String equation, String result){
            this.equation=equation;
            this.result=result;
        }
        
        public CalculatorTest(String equation, Exception exception){
        this.equation = equation;
            this.exception = exception;
        }
    }//end - class CalculatorTest
    
    
    /**
     *  Given a string, returns a CalculaotTest object. Strips out comments (defined as
     *  anythign following # or //. If no CalculatorTest is found in the String either a
     *  generic exception will be thrown, or null;
     *
     *  @param String
     *  @returns CalculatorTEst
     */
    private CalculatorTest getCalculatorTest(String line){
        if(line.startsWith("error:")){
            int splitAt = line.indexOf("=>");
            String equation  = line.substring(6,splitAt);
            String exception = line.substring(splitAt+2);

            if(exception.indexOf("=>")>-1){
                int p = exception.indexOf("=>");
                String location = exception.substring(p+2);
                exception = exception.substring(0,p);

                Integer fixthis = new Integer(location);
                int another_hack = fixthis.intValue();
                
                return new CalculatorTest(equation, new CalculatorException(exception, another_hack));
                //tests.add(new CalculatorTest(equation, new CalculatorException(exception, another_hack)));

            }else{
                return new CalculatorTest(equation, new CalculatorException(exception));
                //tests.add(new CalculatorTest(equation, new CalculatorException(exception)));
            }
        }

        if(line.equals("")){
            return null;
        }
        
        line = line.trim();
        
        if(line.charAt(0)==' ' || line.charAt(0)=='\t'){
            StringBuffer sb = new StringBuffer(line);
            int i=0;
            while(sb.charAt(i)==' ' || sb.charAt(i)=='\t'){
                sb.deleteCharAt(i);
            }
        }
        if(line.indexOf("//")>-1){
            line = line.substring(0, line.indexOf("//"));
        }
        if(line.equals("")){
            return null;
        }

        int location = line.lastIndexOf("==");

        String equation = line.substring(0,location).trim();
        String result = line.substring(location+2).trim();

        return new CalculatorTest(equation, result);
    }//end - private CalculatorTest getCalculatorTest(String line)
    
    
    private Vector getTests(String[] fileNames ){
        Vector tests = new Vector();
        int prev_tests = 0;
        for(int file_pointer=0; file_pointer<fileNames.length; file_pointer++){
            prev_tests = tests.size();
            try{
                //new
                InputStream is = this.getClass().getResourceAsStream("tests/"+fileNames[file_pointer]);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader input = new BufferedReader(isr);

                //original
                //BufferedReader input = new BufferedReader(new FileReader(fileNames[file_pointer]));

                String line;
                LINE: while( (line = input.readLine()) != null ) {
                    try {
                        CalculatorTest ct = getCalculatorTest(line);
                        
                        if(ct!=null)
                            tests.add(ct);
                    } catch(Exception e){
                        System.out.println("TESTING FILE ERROR: " + line + " appears to be in an incorrect format, it was discarded");
                    }
                }//end - while

            } catch (java.io.FileNotFoundException e){
                System.out.println(fileNames[file_pointer] + " not found, no tests used from it");
            } catch (java.io.IOException e){
                System.out.println(fileNames[file_pointer] + " caused an IOException, the file may be corrupt");
            }
            
            if(tests.size()==prev_tests){
                System.out.println("no tests were found for " + fileNames[file_pointer]);
            }            
        }//end - for(int i=0; i<fileNames.length; i++){
        
        return tests;
    }
    
    
    public boolean test(){
        boolean pass = true;
        
        Vector tests = new Vector();
        
        
        
        //it is assumed this files will be in the CLASSPATH
        String []fileNames = {"test_archives.txt", "test_temp.txt", "test_trig.txt"};
        tests = this.getTests(fileNames);

        if(!false){
        if(tests.size()==0)
            System.out.println("no regular tests were found");
        else {
            if(!testTests(tests)) {
                pass = false;
            }
        }
        }
        
        //
        // loop tests, this is where i test different scale and
        // rounding issue
        //        
        if(!false){
        String[] loopNames = {"test_loop_archive.txt"};
        tests = this.getTests(loopNames);
        if(tests.size()==0){
            System.out.println("no loop tests were found");
        } else {
            if(!loopTests(tests,16,17)){
                pass = false;
            }
            if(!loopTests(tests,32,33)){
                pass = false;
            }
            if(!loopTests(tests,64,65)){
                pass = false;
            }
        }
        }
        
        
        //
        //  scale/var tests test for a range of scales and a range of values
        //  when testing different values you have to test equalities, like
        //  sqrt(2)^2==2. While not the best method, this has helped find rounding
        //  errors and incorrect implementations, and has helped to prevent mistakes
        //  being introduced into the code
        //        
        String[] varTestFiles = {"test_var_archive.txt"};
        tests= this.getTests(varTestFiles);
        if(tests.size()==0){
            System.out.println("no variable tests were found");
        } else {
            System.out.println("starting scale/var tests");
            //for(int i=1; i<3100; i++){
            for(int i=1; i<10; i++){
                Vector varTests = new Vector();
                
                
                String subMe = String.valueOf(i);
                //System.out.println(subMe);
                BigDecimal bd = new BigDecimal(subMe);
                bd = bd.movePointLeft(3);
                subMe = bd.toString();
                
                //System.out.println(subMe);
                
                for(int j=0; j<tests.size(); j++){
                    CalculatorTest ctt = (CalculatorTest)tests.elementAt(j);
                    CalculatorTest ct = new CalculatorTest(ctt.equation, ctt.result);

                    ct.equation  = subVar(ct.equation, subMe);
                    ct.result    = subVar(ct.result, subMe);

                    //maybe i could get this to work some day
                    //ct.exception = subVar(ct.exception, subMe);
                    varTests.addElement(ct);
                    //varTests.set(j, ct);
                }
                if(!testTests(varTests)) {
                    pass = false;
                }   
                varTests.clear();
            }
        }        
        
        return pass;
    }//end - public boolean test

    
    private String subVar(String s, String putIt){
        if(s==null)
            return s;
        
        while(s.indexOf("$var")>-1){
            StringBuffer sb = new StringBuffer(s);
            int loc = s.indexOf("$var");
            sb.delete(loc,loc+4);
            sb.insert(loc,putIt);
            s = sb.toString();
        }
        
        return s;        
    }
        
        
    private boolean loopTests(Vector tests, int start, int stop){
        boolean pass = true;
        for(int i=start; i<stop; i++){
            System.out.println("trying scale " + i);
            if(!testTests(tests,i)) {
                pass = false;
            }
        }
        return pass;
    }
    
    private boolean testTests(Vector tests){
        return testTests(tests, -1);
    }
    
    private boolean testTests(Vector tests, int scale){
        Calculator calc;
        
        if(scale>0){
            calc = new Calculator(scale);
        } else {
            calc = new Calculator();
        }
        
        boolean pass = true;
        
        TEST: for(int i=0; i<tests.size(); i++){
            CalculatorTest thistest = (CalculatorTest)tests.elementAt(i);
            try{
                String result         = calc.evaluate_equation(thistest.equation);                
                String correct_result = calc.formatResult(thistest.result);
                
                
                if(thistest.exception!=null){
                    System.out.print("error: "+ thistest.equation + " expected exception \"" + thistest.exception + "\"");
                    System.out.println(" instead, returned: " + result);
                    pass = false;
                }else if(!result.equals(correct_result)){
                    System.out.println("error: "+ thistest.equation +" == "+ correct_result +" != "+ result);
                    pass = false;
                }else{
                    if(showpass)
                        System.out.println("pass: "+thistest.equation+" == "+thistest.result);
                }
            
            }catch(Exception e){
                Exception expected_exception = thistest.exception;
                
                if(expected_exception==null){
                    System.out.println(thistest.equation +" => unexpected exception thrown => \"" + e + "\"");
                    pass=false;
                    continue TEST;
                }
                
                
                if(!expected_exception.getClass().equals(e.getClass())){
                    System.out.print(thistest.equation +"wrong exception thrown: correct VS. received ==> ");
                    pass=false;
                }
                
                if(e instanceof CalculatorException){
                    CalculatorException received_exception = (CalculatorException)e;
                    CalculatorException exp_exception = (CalculatorException)expected_exception;
                    
                    if(!e.getMessage().equals(expected_exception.getMessage())){
                        System.out.print("equation=="+thistest.equation+" => ");
                        System.out.print("wrong exception message: correct VS. received ==> " + expected_exception.getMessage());
                        System.out.println(" vs. " + e.getMessage());
                        pass=false;
                    }
                    
                    if(received_exception.location!=-1 && received_exception.location!=exp_exception.location){
                        System.out.print("equation=="+thistest.equation+" => ");
                        System.out.print("wrong error location: correct VS. received ==> " + exp_exception.location);
                        System.out.println(" vs. " + received_exception.location);
                        pass=false;                        
                    }
                }
                
                continue TEST;
            }//end - catch(Exception e) 
        }//end - TEST:
        
        return pass;        
        
    }
    
    public static void main(String[] args) {

        CalculatorTester tester = new CalculatorTester();
        if(tester.test()){
            System.out.println("pass");
        }else{
            System.out.println("fail");
        }
        
    }//end - public static void main(String[] args)        
    
}
