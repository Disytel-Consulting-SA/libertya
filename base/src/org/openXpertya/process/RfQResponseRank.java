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



package org.openXpertya.process;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.logging.Level;

import org.openXpertya.model.MRfQ;
import org.openXpertya.model.MRfQLine;
import org.openXpertya.model.MRfQLineQty;
import org.openXpertya.model.MRfQResponse;
import org.openXpertya.model.MRfQResponseLine;
import org.openXpertya.model.MRfQResponseLineQty;
import org.openXpertya.util.Env;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class RfQResponseRank extends SvrProcess {

    /** Descripción de Campos */

    private int p_C_RfQ_ID = 0;

    /**
     * Descripción de Método
     *
     */

    protected void prepare() {
        ProcessInfoParameter[] para = getParameter();

        for( int i = 0;i < para.length;i++ ) {
            String name = para[ i ].getParameterName();

            if( para[ i ].getParameter() == null ) {
                ;
            } else {
                log.log( Level.SEVERE,"Unknown Parameter: " + name );
            }
        }

        p_C_RfQ_ID = getRecord_ID();
    }    // prepare

    /**
     * Descripción de Método
     *
     *
     * @return
     *
     * @throws Exception
     */

    protected String doIt() throws Exception {
        MRfQ rfq = new MRfQ( getCtx(),p_C_RfQ_ID,get_TrxName());

        if( rfq.getID() == 0 ) {
            throw new IllegalArgumentException( "No RfQ found" );
        }

        log.info( rfq.toString());

        String error = rfq.checkQuoteTotalAmtOnly();

        if( (error != null) && (error.length() > 0) ) {
            throw new Exception( error );
        }

        // Get Completed, Active Responses

        MRfQResponse[] responses = rfq.getResponses( true,true );

        log.fine( "doIt - #Responses=" + responses.length );

        if( responses.length == 0 ) {
            throw new IllegalArgumentException( "No completed RfQ Responses found" );
        }

        if( responses.length == 1 ) {
            responses[ 0 ].setIsSelectedWinner( true );
            responses[ 0 ].save();

            throw new IllegalArgumentException( "Only one completed RfQ Response found" );
        }

        // Rank

        if( rfq.isQuoteTotalAmtOnly()) {
            rankResponses( rfq,responses );
        } else {
            rankLines( rfq,responses );
        }

        return "# " + responses.length;
    }    // doIt

    /**
     * Descripción de Método
     *
     *
     * @param rfq
     * @param responses
     */

    private void rankLines( MRfQ rfq,MRfQResponse[] responses ) {
        MRfQLine[] rfqLines = rfq.getLines();

        if( rfqLines.length == 0 ) {
            throw new IllegalArgumentException( "No RfQ Lines found" );
        }

        // for all lines

        for( int i = 0;i < rfqLines.length;i++ ) {

            // RfQ Line

            MRfQLine rfqLine = rfqLines[ i ];

            if( !rfqLine.isActive()) {
                continue;
            }

            log.fine( "rankLines - " + rfqLine );

            MRfQLineQty[] rfqQtys = rfqLine.getQtys();

            for( int j = 0;j < rfqQtys.length;j++ ) {

                // RfQ Line Qty

                MRfQLineQty rfqQty = rfqQtys[ j ];

                if( !rfqQty.isActive() ||!rfqQty.isRfQQty()) {
                    continue;
                }

                log.fine( "rankLines Qty - " + rfqQty );

                MRfQResponseLineQty[] respQtys = rfqQty.getResponseQtys( false );

                for( int kk = 0;kk < respQtys.length;kk++ ) {

                    // Response Line Qty

                    MRfQResponseLineQty respQty = respQtys[ kk ];

                    if( !respQty.isActive() ||!respQty.isValidAmt()) {
                        respQty.setRanking( 999 );
                        respQty.save();
                        log.fine( "  - ignored: " + respQty );
                    }
                }    // for all respones line qtys

                // Rank RfQ Line Qtys

                respQtys = rfqQty.getResponseQtys( false );

                if( respQtys.length == 0 ) {
                    log.fine( "  - No Qtys with valid Amounts" );
                } else {
                    Arrays.sort( respQtys,respQtys[ 0 ] );

                    int        lastRank = 1;    // multiple rank #1
                    BigDecimal lastAmt  = Env.ZERO;

                    for( int rank = 0;rank < respQtys.length;rank++ ) {
                        MRfQResponseLineQty qty = respQtys[ rank ];

                        if( !qty.isActive() || (qty.getRanking() == 999) ) {
                            continue;
                        }

                        BigDecimal netAmt = qty.getNetAmt();

                        if( netAmt == null ) {
                            qty.setRanking( 999 );
                            log.fine( "  - Rank 999: " + qty );
                        } else {
                            if( lastAmt.compareTo( netAmt ) != 0 ) {
                                lastRank = rank + 1;
                                lastAmt  = qty.getNetAmt();
                            }

                            qty.setRanking( lastRank );
                            log.fine( "  - Rank " + lastRank + ": " + qty );
                        }

                        qty.save();

                        //

                        if( rank == 0 )         // Update RfQ
                        {
                            rfqQty.setBestResponseAmt( qty.getNetAmt());
                            rfqQty.save();
                        }
                    }
                }
            }                                   // for all rfq line qtys
        }                                       // for all rfq lines

        // Select Winner based on line ranking

        MRfQResponse winner = null;

        for( int ii = 0;ii < responses.length;ii++ ) {
            MRfQResponse response = responses[ ii ];

            if( response.isSelectedWinner()) {
                response.setIsSelectedWinner( false );
            }

            int                ranking   = 0;
            MRfQResponseLine[] respLines = response.getLines( false );

            for( int jj = 0;jj < respLines.length;jj++ ) {

                // Response Line

                MRfQResponseLine respLine = respLines[ jj ];

                if( !respLine.isActive()) {
                    continue;
                }

                if( respLine.isSelectedWinner()) {
                    respLine.setIsSelectedWinner( false );
                }

                MRfQResponseLineQty[] respQtys = respLine.getQtys( false );

                for( int kk = 0;kk < respQtys.length;kk++ ) {

                    // Response Line Qty

                    MRfQResponseLineQty respQty = respQtys[ kk ];

                    if( !respQty.isActive()) {
                        continue;
                    }

                    ranking += respQty.getRanking();

                    if( (respQty.getRanking() == 1) && respQty.getRfQLineQty().isPurchaseQty()) {
                        respLine.setIsSelectedWinner( true );
                        respLine.save();

                        break;
                    }
                }
            }

            response.setRanking( ranking );
            response.save();
            log.fine( "- Response Ranking " + ranking + ": " + response );

            if( !rfq.isQuoteSelectedLines())    // no total selected winner if not all lines
            {
                if( (winner == null) && (ranking > 0) ) {
                    winner = response;
                }

                if( (winner != null) && (response.getRanking() > 0) && (response.getRanking() < winner.getRanking())) {
                    winner = response;
                }
            }
        }

        if( winner != null ) {
            winner.setIsSelectedWinner( true );
            winner.save();
            log.fine( "- Response Winner: " + winner );
        }
    }    // rankLines

    /**
     * Descripción de Método
     *
     *
     * @param rfq
     * @param responses
     */

    private void rankResponses( MRfQ rfq,MRfQResponse[] responses ) {
        int ranking = 1;

        // Responses Ordered by Price

        for( int ii = 0;ii < responses.length;ii++ ) {
            MRfQResponse response = responses[ ii ];

            if( (response.getPrice() != null) && (response.getPrice().compareTo( Env.ZERO ) > 0) ) {
                if( response.isSelectedWinner() != ( ranking == 1 )) {
                    response.setIsSelectedWinner( ranking == 1 );
                }

                response.setRanking( ranking );

                //

                ranking++;
            } else {
                response.setRanking( 999 );

                if( response.isSelectedWinner()) {
                    response.setIsSelectedWinner( false );
                }
            }

            response.save();
            log.fine( "rankResponse - " + response );
        }
    }    // rankResponses
}    // RfQResponseRank



/*
 *  @(#)RfQResponseRank.java   02.07.07
 * 
 *  Fin del fichero RfQResponseRank.java
 *  
 *  Versión 2.2
 *
 */
