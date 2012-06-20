/** Change Numeric for Integer   */
CREATE OR REPLACE FUNCTION ID (record NUMERIC) 
RETURNS INTEGER AS $$
DECLARE 
ID integer := 0;
BEGIN
    ID := CAST(record AS INTEGER);
    --RAISE NOTICE 'Quantity here is %', ID;
    RETURN ID;
END;
$$ LANGUAGE plpgsql;
  
CREATE OR REPLACE FUNCTION getdate()
RETURNS TIMESTAMP WITH TIME ZONE AS $$
BEGIN
    RETURN now();
END;
$$ LANGUAGE plpgsql;

/** Product     **/
CREATE OR REPLACE FUNCTION addDays (day TIMESTAMP WITH TIME ZONE, days DECIMAL)
RETURNS TIMESTAMP WITH TIME ZONE AS $$
BEGIN
    RETURN addDays(day,ID(days));
END;
$$ LANGUAGE plpgsql;

/** Product     **/
CREATE OR REPLACE FUNCTION subtractdays (day TIMESTAMP WITH TIME ZONE, days DECIMAL)
RETURNS TIMESTAMP WITH TIME ZONE AS $$
BEGIN
    RETURN addDays(day,ID(days * -1));
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION addDays (day TIMESTAMP , days DECIMAL)
RETURNS TIMESTAMP WITH TIME ZONE AS $$
BEGIN
    RETURN addDays(CAST (day AS TIMESTAMP WITH TIME ZONE),ID(days));
END;
$$ LANGUAGE plpgsql;


/** Product	**/
CREATE OR REPLACE FUNCTION productAttribute (M_AttributeSetInstance_ID NUMERIC) 
RETURNS VARCHAR AS $$
BEGIN    
    RETURN productAttribute(ID(M_AttributeSetInstance_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bomPriceLimit (M_Product_ID NUMERIC, M_PriceList_Version_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN bomPriceLimit(ID(M_Product_ID),ID(M_PriceList_Version_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bomPriceList (M_Product_ID NUMERIC, M_PriceList_Version_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN bomPriceList(ID(M_Product_ID),ID(M_PriceList_Version_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bomPriceStd (M_Product_ID NUMERIC, M_PriceList_Version_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN bomPriceStd(ID(M_Product_ID),ID(M_PriceList_Version_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bomQtyAvailable (M_Product_ID NUMERIC, M_Warehouse_ID NUMERIC, M_Locator_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN bomQtyAvailable(ID(M_Product_ID),ID(M_Warehouse_ID),ID(M_Locator_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bomQtyOnHand (M_Product_ID NUMERIC, M_Warehouse_ID NUMERIC, M_Locator_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN bomQtyOnHand(ID(M_Product_ID),ID(M_Warehouse_ID),ID(M_Locator_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bomQtyOrdered (M_Product_ID NUMERIC, M_Warehouse_ID NUMERIC, M_Locator_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN bomQtyOrdered(ID(M_Product_ID),ID(M_Warehouse_ID),ID(M_Locator_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bomQtyReserved (M_Product_ID NUMERIC, M_Warehouse_ID NUMERIC, M_Locator_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN bomQtyReserved(ID(M_Product_ID),ID(M_Warehouse_ID),ID(M_Locator_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION currencyBase (Amount NUMERIC, C_CurrencyFrom_ID NUMERIC, 
    	ConversionDate TIMESTAMP WITH TIME ZONE, AD_Client_ID NUMERIC, AD_Org_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN currencyBase (Amount,ID(C_CurrencyFrom_ID),ConversionDate,ID(AD_Client_ID),ID(AD_Org_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION currencyConvert (Amount NUMERIC, C_CurrencyFrom_ID NUMERIC, C_CurrencyTo_ID NUMERIC,
        ConversionDate TIMESTAMP WITH TIME ZONE, C_ConversionType_ID NUMERIC, AD_Client_ID NUMERIC, AD_Org_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN currencyConvert (Amount ,ID(C_CurrencyFrom_ID),ID(C_CurrencyTo_ID),ConversionDate,ID(C_ConversionType_ID),ID(AD_Client_ID),ID(AD_Org_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION currencyRate (C_CurrencyFrom_ID NUMERIC, C_CurrencyTo_ID NUMERIC,
        ConversionDate TIMESTAMP WITH TIME ZONE, C_ConversionType_ID NUMERIC, AD_Client_ID NUMERIC, AD_Org_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN currencyRate (ID(C_CurrencyFrom_ID), ID(C_CurrencyTo_ID),ConversionDate,ID(C_ConversionType_ID),ID(AD_Client_ID),ID(AD_Org_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION bpartnerRemitLocation (p_C_BPartner_ID NUMERIC)
RETURNS INTEGER AS $$
BEGIN
    RETURN partnerRemitLocation (ID(p_C_BPartner_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION invoiceOpen (p_C_Invoice_ID NUMERIC, p_C_InvoicePaySchedule_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN invoiceOpen (ID(p_C_Invoice_ID),ID(p_C_InvoicePaySchedule_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION invoicePaid (p_C_Invoice_ID NUMERIC, p_C_Currency_ID NUMERIC, p_MultiplierAP NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN invoicePaid (ID(p_C_Invoice_ID),ID(p_C_Currency_ID), ID(p_MultiplierAP));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION invoiceDiscount (p_C_Invoice_ID NUMERIC, p_PayDate TIMESTAMP WITH TIME ZONE, p_C_InvoicePaySchedule_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN invoiceDiscount (ID(p_C_Invoice_ID), p_PayDate , ID(p_C_InvoicePaySchedule_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION paymentTermDueDays (p_C_PaymentTerm_ID NUMERIC, p_DocDate TIMESTAMP WITH TIME ZONE, p_PayDate TIMESTAMP WITH TIME ZONE)
RETURNS INTEGER AS $$
BEGIN
    RETURN paymentTermDueDays (ID(p_C_PaymentTerm_ID), p_DocDate , p_PayDate);
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION paymentTermDueDate (p_C_PaymentTerm_ID NUMERIC, p_DocDate TIMESTAMP WITH TIME ZONE)
RETURNS TIMESTAMP WITH TIME ZONE AS $$
BEGIN
    RETURN paymentTermDueDays (ID(p_C_PaymentTerm_ID), p_DocDate );
END;    
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION paymentTermDiscount (p_Amount NUMERIC, p_C_PaymentTerm_ID NUMERIC, p_DocDate TIMESTAMP WITH TIME ZONE, p_PayDate TIMESTAMP WITH TIME ZONE)
RETURNS NUMERIC AS $$
BEGIN
    RETURN paymentTermDiscount (p_Amount, ID(p_C_PaymentTerm_ID), p_DocDate , p_PayDate );
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION paymentAllocated (p_C_Payment_ID NUMERIC, p_C_Currency_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN paymentAllocated (ID(p_C_Payment_ID),ID(p_C_Currency_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION paymentAvailable (p_C_Payment_ID NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN paymentAvailable (ID(p_C_Payment_ID));
END;    
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION acctBalance (p_Account_ID NUMERIC, p_AmtDr NUMERIC, p_AmtCr NUMERIC)
RETURNS NUMERIC AS $$
BEGIN
    RETURN acctBalance (ID(p_Account_ID), p_AmtDr , p_AmtCr);
END;    
$$ LANGUAGE plpgsql;	

DROP OPERATOR openxpertya.+ (timestamptz, numeric);
CREATE OPERATOR openxpertya.+ ( PROCEDURE = openxpertya.adddays,
LEFTARG = TIMESTAMPTZ, RIGHTARG = NUMERIC,
COMMUTATOR = +);
DROP OPERATOR openxpertya.- (timestamptz, numeric);
CREATE OPERATOR openxpertya.- ( PROCEDURE = openxpertya.subtractdays,
LEFTARG = TIMESTAMPTZ, RIGHTARG = NUMERIC,
COMMUTATOR = -);

/*CREATE OR REPLACE FUNCTION nextID ( p_AD_Sequence_ID NUMERIC, p_System CHAR)
RETURNS NUMERIC AS $$
DECLARE
 o_NextID INTEGER := -1;
BEGIN
    IF (p_System = 'Y') THEN
    
    LOCK TABLE AD_Sequence IN ACCESS EXCLUSIVE MODE;
    --LOCK TABLE films IN SHARE ROW EXCLUSIVE MODE;
        SELECT CurrentNextSys
            INTO o_NextID
        FROM AD_Sequence
        WHERE AD_Sequence_ID=p_AD_Sequence_ID;
        --FOR UPDATE OF CurrentNextSys;
        --
        UPDATE AD_Sequence
          SET CurrentNextSys = CurrentNextSys + IncrementNo
        WHERE AD_Sequence_ID=p_AD_Sequence_ID;
        --COMMIT;
        RETURN o_NextID;
    ELSE
    LOCK TABLE AD_Sequence IN ACCESS EXCLUSIVE MODE;
    --LOCK TABLE films IN SHARE ROW EXCLUSIVE MODE;
        SELECT CurrentNext
            INTO o_NextID
       FROM AD_Sequence
        WHERE AD_Sequence_ID=p_AD_Sequence_ID;
        --FOR UPDATE OF CurrentNext;
        --
        UPDATE AD_Sequence
          SET CurrentNext = CurrentNext + IncrementNo
        WHERE AD_Sequence_ID=p_AD_Sequence_ID;
	--COMMIT;
        RETURN o_NextID;
    END IF;
    --
RAICE EXCEPTION 'Failed to update' ;
RETURN null;
END;    
$$ LANGUAGE plpgsql;
*/
