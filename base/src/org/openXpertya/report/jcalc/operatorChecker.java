package org.openXpertya.report.jcalc;


public class operatorChecker {
    static final int BEGINNING   = 0;
    static final int BINARY_OP   = 1;
    static final int UNARY_LEFT  = 2; //neg number
    static final int UNARY_RIGHT = 3; //!
    static final int PAREN_OPEN  = 4;
    static final int PAREN_CLOSE = 5;
    static final int NUMBER      = 6;
    static final int FUNCTION    = 7; //sin, cos, and
    static final int VARIABLE    = 8;
    static final int COMMA       = 9;
    static final int END         = 10;
    static final int U_COMMAND   = 11; //clear print
    static final int U_CALL      = 12; //delete(INT)
    static final int R_CALL      = 13; //ans(INT) entry(INT)


                          /*        
                                    U     P    
                                 U  N  P  A    
                           B     N  A  A  R                 U
                           E     A  R  R  E     F  V        _
                           G     R  Y  E  N     U  A        C
                           I  B  Y  _  N  _  N  N  R        O  U  R
                           N  I  _  R  _  C  U  C  I  C     M  _  _
                           N  N  L  I  O  L  M  T  A  O     M  C  C
                           I  A  E  G  P  O  B  I  B  M  E  A  A  A
                           N  R  F  H  E  S  E  O  L  M  N  N  L  L
                           G  Y  T  T  N  E  R  N  E  A  D  D  L  L */
    final static int [][] legality = {                             
       /*BEGINNING  */    {0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1}, 
       /*BINARY     */    {0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1},
       /*UNARY_LEFT */    {0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1},
       /*UNARY_RIGHT*/    {0, 1, 0, 1, 2, 1, 2, 2, 2, 1, 1, 0, 0, 0},
       /*PAREN_OPEN */    {0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1},
       /*PAREN_CLOSE*/    {0, 1, 2, 1, 2, 1, 2, 2, 2, 1, 1, 0, 0, 2},
       /*NUMBER     */    {0, 1, 2, 1, 2, 1, 2, 2, 2, 1, 1, 0, 0, 2},
       /*FUNCTION   */    {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
       /*VARIABLE   */    {0, 1, 2, 1, 2, 1, 2, 2, 2, 1, 1, 0, 0, 2},
       /*COMMA      */    {0, 0, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1},
       /*END        */    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
       /*U_COMMAND  */    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
       /*U_CALL     */    {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
       /*R_CALL     */    {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };



    static public int isLegal(int left, int right){
        return legality[left][right];
    }


}//end - class operatorChecker

    

