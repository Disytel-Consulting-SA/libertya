/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class Evaluator {

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( Evaluator.class );

    /**
     * Descripción de Método
     *
     *
     * @param source
     * @param logic
     *
     * @return
     */

    public static boolean isAllVariablesDefined( Evaluatee source,String logic ) {
        if( (logic == null) || (logic.length() == 0) ) {
            return true;
        }

        //

        int pos = 0;

        while( pos < logic.length()) {
            int first = logic.indexOf( '@',pos );

            if( first == -1 ) {
                return true;
            }

            int second = logic.indexOf( '@',first + 1 );

            if( second == -1 ) {
                s_log.severe( "No second @ in Logic: " + logic );

                return false;
            }

            String variable = logic.substring( first + 1,second - 1 );
            String eval     = source.get_ValueAsString( variable );

            s_log.finest( variable + "=" + eval );

            if( (eval == null) || (eval.length() == 0) ) {
                return false;
            }

            //

            pos = second + 1;
        }

        return true;
    }    // isAllVariablesDefined

    
    public static boolean evaluateComplexLogic( Evaluatee source,String logic ) {
    	int l = logic.length();
    	int i = 0;
    	List<StringBuffer> bloques = new ArrayList<StringBuffer>();
    	int currentIndexBloque = -1;
    	Boolean evalOP = null;
    	Character lastOP = null;
    	Boolean evaluation = null;
    	StringBuffer lastCondition = null;
        while(i < l) {
        	char c = logic.charAt(i);
        	if(c == ')') {
        		if(currentIndexBloque < 0 || bloques.get(currentIndexBloque) == null) {
        			s_log.severe( "Logic does not comply with complex format "+logic );
    				return false;
        		}
        		else {
        			// Aca cierra una condición 
					// Si seguimos teniendo anidadas, armamos una condición en base al resultado de
					// esta última para que se incorpore a la de afuera
        			// Si no tenemos anidadas, la dejamos como última condición
        			if(currentIndexBloque > 0) {
						boolean miniEval = evaluateLogic(source, bloques.get(currentIndexBloque).toString());
        				bloques.get(currentIndexBloque-1).append("1="+(miniEval?"1":"0"));
        			}
        			else {
        				lastCondition = new StringBuffer(bloques.get(currentIndexBloque).toString());
        			}
        			bloques.remove(currentIndexBloque);
        			currentIndexBloque--;
        		}
        	}
        	else if(c == '(') {
        		currentIndexBloque++;
        		bloques.add(new StringBuffer());
        	}
        	// Si el bloque es distinto de null, entonces hay que seguir guardando ahi
        	else if(currentIndexBloque > -1) {
        		bloques.get(currentIndexBloque).append(c);
        	}
        	// Si es un operador entonces evaluar lo que ya tiene la última condición
        	else if(c == '&' || c == '|'){
        		if(!Util.isEmpty(lastCondition.toString(), true)) {
        			evalOP = evaluateLogic(source, lastCondition.toString());
        		}
        		lastOP = c;
    			lastCondition = null;
        	}
        	else {
        		if(lastCondition == null) {
        			lastCondition = new StringBuffer();
        		}
        		lastCondition.append(c);
        	}
        	
        	// Si se evaluó algo
        	if(evalOP != null) {
        		if(evaluation == null) {
        			evaluation = evalOP;
        		}
        		else {
        			if(lastOP == '&') {
        				evaluation = evaluation && evalOP;
        			}
        			else {
        				evaluation = evaluation || evalOP;
        			}
        		}
        		evalOP = null;
        	}
        	
        	i++;
        }
        if(currentIndexBloque > -1) {
			s_log.severe( "Logic does not comply with complex format "+logic );
			return false;
		}
        // La última condición
        if(lastCondition != null) {
        	evalOP = evaluateLogic(source, lastCondition.toString());
        	if(lastOP == '&') {
				evaluation = evaluation & evalOP;
			}
			else {
				evaluation = evaluation | evalOP;
			}
        }
    	return evaluation;
    }
    
    /**
     * Descripción de Método
     *
     *
     * @param source
     * @param logic
     *
     * @return
     */

    public static boolean evaluateLogic( Evaluatee source,String logic ) {
    	
        // Conditional

        StringTokenizer st = new StringTokenizer( logic.trim(),"&|",true );
        int             it = st.countTokens();

        if((( it / 2 ) - (( it + 1 ) / 2 )) == 0 )    // only uneven arguments
        {
            s_log.severe( "Logic does not comply with format " + "'<expression> [<logic> <expression>]' => " + logic );

            return false;
        }
        
        // Evaluar lógica compleja basada en varias condiciones en paréntesis
        if(logic.indexOf("(") >= 0) {
        	return evaluateComplexLogic(source,logic);
        }

        boolean retValue = evaluateLogicTuple( source,st.nextToken());

        while( st.hasMoreTokens()) {
            String  logOp = st.nextToken().trim();
            boolean temp  = evaluateLogicTuple( source,st.nextToken());

            if( logOp.equals( "&" )) {
                retValue = retValue & temp;
            } else if( logOp.equals( "|" )) {
                retValue = retValue | temp;
            } else {
                s_log.log( Level.SEVERE,"Logic operant '|' or '&' expected => " + logic );

                return false;
            }
        }    // hasMoreTokens

        return retValue;
    }    // evaluateLogic

    /**
     * Descripción de Método
     *
     *
     * @param source
     * @param logic
     *
     * @return
     */

    private static boolean evaluateLogicTuple( Evaluatee source,String logic ) {
        StringTokenizer st = new StringTokenizer( logic.trim(),"!=^~><",true );

        if( st.countTokens() != 3 ) {
            s_log.log( Level.SEVERE,"Logic touple does not comply with format " + "'@context@=value' or '@context@!value' => " + logic );

            return false;
        }

        // First Part

        String first     = st.nextToken().trim();    // get '@tag@'
        String firstEval = first.trim();

        if( first.indexOf( '@' ) != -1 )                      // variable
        {
            first     = first.replace( '@',' ' ).trim();      // strip 'tag'
            firstEval = source.get_ValueAsString( first );    // replace with it's value
        }

        firstEval = firstEval.replace( '\'',' ' ).replace( '"',' ' ).trim();    // strip ' and "

        // Comperator

        String operand = st.nextToken();

        // Second Part

        String second     = st.nextToken();    // get value
        String secondEval = second.trim();

        if( second.indexOf( '@' ) != -1 )                       // variable
        {
            second     = second.replace( '@',' ' ).trim();      // strip tag
            secondEval = source.get_ValueAsString( second );    // replace with it's value
        }

        secondEval = secondEval.replace( '\'',' ' ).replace( '"',' ' ).trim();    // strip ' and "

        // Handling of ID compare (null => 0)

        if( (first.indexOf( "_ID" ) != -1) && (firstEval.length() == 0) ) {
            firstEval = "0";
        }

        if( (second.indexOf( "_ID" ) != -1) && (secondEval.length() == 0) ) {
            secondEval = "0";
        }

        // Logical Comparison

        boolean result = evaluateLogicTouple( firstEval,operand,secondEval );

        //

        if( CLogMgt.isLevelFinest()) {
            s_log.finest( logic + " => \"" + firstEval + "\" " + operand + " \"" + secondEval + "\" => " + result );
        }

        //

        return result;
    }    // evaluateLogicTouple

    /**
     * Descripción de Método
     *
     *
     * @param value1
     * @param operand
     * @param value2
     *
     * @return
     */

    private static boolean evaluateLogicTouple( String value1,String operand,String value2 ) {
        if( (value1 == null) || (operand == null) || (value2 == null) ) {
            return false;
        }

        BigDecimal value1bd = null;
        BigDecimal value2bd = null;

        try {
            if( !value1.startsWith( "'" )) {
                value1bd = new BigDecimal( value1 );
            }

            if( !value2.startsWith( "'" )) {
                value2bd = new BigDecimal( value2 );
            }
        } catch( Exception e ) {
            value1bd = null;
            value2bd = null;
        }

        //

        if( operand.equals( "=" )) {
            if( (value1bd != null) && (value2bd != null) ) {
                return value1bd.compareTo( value2bd ) == 0;
            }

            return value1.compareTo( value2 ) == 0;
        } else if( operand.equals( "<" )) {
            if( (value1bd != null) && (value2bd != null) ) {
                return value1bd.compareTo( value2bd ) < 0;
            }

            return value1.compareTo( value2 ) < 0;
        } else if( operand.equals( ">" )) {
            if( (value1bd != null) && (value2bd != null) ) {
                return value1bd.compareTo( value2bd ) > 0;
            }

            return value1.compareTo( value2 ) > 0;
        } else if( operand.equals( "~" )) {
        	return value1.contains(value2);
        } else    // interpreted as not
        {
            if( (value1bd != null) && (value2bd != null) ) {
                return value1bd.compareTo( value2bd ) != 0;
            }

            return value1.compareTo( value2 ) != 0;
        }
    }             // evaluateLogicTouple
    
    /**
	 *  Parse String and add variables with @ to the list.
	 *  @param list list to be added to
	 *  @param parseString string to parse for variables
	 */
	public static void parseDepends (ArrayList<String> list, String parseString)
	{
		if (parseString == null || parseString.length() == 0)
			return;
	//	log.fine( "MField.parseDepends", parseString);
		String s = parseString;
		//  while we have variables
		while (s.indexOf('@') != -1)
		{
			int pos = s.indexOf('@');
			s = s.substring(pos+1);
			pos = s.indexOf('@');
			if (pos == -1)
				continue;	//	error number of @@ not correct
			String variable = s.substring(0, pos);
			s = s.substring(pos+1);
		//	log.fine( variable);
			list.add(variable);
		}
	}   //  parseDepends

}    // Evaluator



/*
 *  @(#)Evaluator.java   25.03.06
 * 
 *  Fin del fichero Evaluator.java
 *  
 *  Versión 2.2
 *
 */
