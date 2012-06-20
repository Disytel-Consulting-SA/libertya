/**
 *	Create SQL Java Functions (Sybase)
 *
 *	$Header: createSQLJ.sql,v 2.0 Exp $
 */

IF OBJECT_ID('openxpertyaVersion') IS NOT NULL
    DROP FUNCTION openxpertyaVersion
go
CREATE FUNCTION openxpertyaVersion()
 	RETURNS nvarchar(60)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.OpenXpertya.getVersion()'
go

IF OBJECT_ID('openxpertyaProperties') IS NOT NULL
    DROP FUNCTION openxpertyaProperties
go
CREATE FUNCTION openxpertyaProperties()
 	RETURNS nvarchar(2000)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.OpenXpertya.getProperties()'
go

IF OBJECT_ID('openxpertyaProperty') IS NOT NULL
    DROP FUNCTION openxpertyaProperty
go
CREATE FUNCTION openxpertyaProperty(p_key nvarchar(60))
 	RETURNS nvarchar(2000)
 	language java RETURNS null on null input parameter style java
 	external name 'org.openxpertya.sqlj.OpenXpertya.getProperty(java.lang.String)'
go

--	Basic Stuff
IF OBJECT_ID('TRUNC') IS NOT NULL
    DROP FUNCTION TRUNC
go
CREATE FUNCTION TRUNC(dt datetime)
 	RETURNS datetime
 	language java RETURNS null on null input parameter style java
 	external name 'org.openxpertya.sqlj.OpenXpertya.trunc(java.sql.Timestamp)'
go

IF OBJECT_ID('firstOf') IS NOT NULL
    DROP FUNCTION firstOf
go
CREATE FUNCTION firstOf(dt datetime, xx varchar)
 	RETURNS datetime
 	language java RETURNS null on null input parameter style java
 	external name 'org.openxpertya.sqlj.OpenXpertya.firstOf(java.sql.Timestamp,java.lang.String)'
go

IF OBJECT_ID('addDays') IS NOT NULL
    DROP FUNCTION addDays
go
CREATE FUNCTION addDays(dt datetime, days integer)
 	RETURNS datetime
 	language java RETURNS null on null input parameter style java
 	external name 'org.openxpertya.sqlj.OpenXpertya.addDays(java.sql.Timestamp,int)'
go
IF OBJECT_ID('daysBetween') IS NOT NULL
    DROP FUNCTION daysBetween
go
CREATE FUNCTION daysBetween(dt1 datetime, dt2 datetime)
 	RETURNS integer
 	language java RETURNS null on null input parameter style java
 	external name 'org.openxpertya.sqlj.OpenXpertya.getDaysBetween(java.sql.Timestamp,java.sql.Timestamp)'
go

IF OBJECT_ID('charAt') IS NOT NULL
    DROP FUNCTION charAt
go
CREATE FUNCTION charAt(src varchar, pos integer)
 	RETURNS java.lang.String
 	language java RETURNS null on null input parameter style java
 	external name 'org.openxpertya.sqlj.OpenXpertya.charAt(java.lang.String,int)'
go

/** Product **/
IF OBJECT_ID('productAttribute') IS NOT NULL
    DROP FUNCTION productAttribute
go
CREATE FUNCTION productAttribute(M_AttributeSetInstance_ID integer)
 	RETURNS nvarchar(2000)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.Product.attributeName(int)'
go
	
IF OBJECT_ID('bomPriceLimit') IS NOT NULL
    DROP FUNCTION bomPriceLimit
go
CREATE FUNCTION bomPriceLimit(M_Product_ID integer, M_PriceList_Version_ID integer)
 	RETURNS numeric(18,2)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.Product.bomPriceLimit(int,int)'
go
IF OBJECT_ID('bomPriceList') IS NOT NULL
    DROP FUNCTION bomPriceList
go
CREATE FUNCTION bomPriceList(M_Product_ID integer, M_PriceList_Version_ID integer)
 	RETURNS numeric(18,2)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.Product.bomPriceList(int,int)'
go
IF OBJECT_ID('bomPriceStd') IS NOT NULL
    DROP FUNCTION bomPriceStd
go
CREATE FUNCTION bomPriceStd(M_Product_ID integer, M_PriceList_Version_ID integer)
 	RETURNS numeric(18,2)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.Product.bomPriceStd(int,int)'
go

IF OBJECT_ID('bomQtyAvailable') IS NOT NULL
    DROP FUNCTION bomQtyAvailable
go
CREATE FUNCTION bomQtyAvailable (M_Product_ID integer, M_Warehouse_ID integer, M_Locator_ID integer)
 	RETURNS numeric(18,2)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.Product.bomQtyAvailable(int,int,int)'
go
IF OBJECT_ID('bomQtyOnHand') IS NOT NULL
    DROP FUNCTION bomQtyOnHand
go
CREATE FUNCTION bomQtyOnHand (M_Product_ID integer, M_Warehouse_ID integer, M_Locator_ID integer)
 	RETURNS numeric(18,2)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.Product.bomQtyOnHand(int,int,int)'
go
IF OBJECT_ID('bomQtyOrdered') IS NOT NULL
    DROP FUNCTION bomQtyOrdered
go
CREATE FUNCTION bomQtyOrdered (M_Product_ID integer, M_Warehouse_ID integer, M_Locator_ID integer)
 	RETURNS numeric(18,2)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.Product.bomQtyOrdered(int,int,int)'
go
IF OBJECT_ID('bomQtyReserved') IS NOT NULL
    DROP FUNCTION bomQtyReserved
go
CREATE FUNCTION bomQtyReserved (M_Product_ID integer, M_Warehouse_ID integer, M_Locator_ID integer)
 	RETURNS numeric(18,2)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.Product.bomQtyReserved(int,int,int)'
go

/** Currency **/
IF OBJECT_ID('currencyBase') IS NOT NULL
    DROP FUNCTION currencyBase
go
CREATE FUNCTION currencyBase (Amount numeric(18,2), C_CurrencyFrom_ID integer, 
    ConversionDate datetime, AD_Client_ID integer, AD_Org_ID integer)
 	RETURNS numeric(18,2)
 	language java parameter style java
 	external name 'org.openxpertya.sqlj.Currency.base(java.math.BigDecimal,int,java.sql.Timestamp,int,int)'
go
IF OBJECT_ID('currencyConvert') IS NOT NULL
    DROP FUNCTION currencyConvert
go
CREATE FUNCTION currencyConvert (Amount numeric(18,2), C_CurrencyFrom_ID integer, C_CurrencyTo_ID integer,
    ConversionDate datetime, C_ConversionType_ID integer, AD_Client_ID integer, AD_Org_ID integer)
	RETURNS numeric(18,2)
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.Currency.convert(java.math.BigDecimal,int,int,java.sql.Timestamp,int,int,int)'
go
IF OBJECT_ID('currencyRate') IS NOT NULL
    DROP FUNCTION currencyRate
go
CREATE FUNCTION currencyRate (C_CurrencyFrom_ID integer, C_CurrencyTo_ID integer,
    ConversionDate datetime, C_ConversionType_ID integer, AD_Client_ID integer, AD_Org_ID integer)
	RETURNS numeric(18,2)
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.Currency.rate(int,int,java.sql.Timestamp,int,int,int)'
go

/** BPartner **/
IF OBJECT_ID('bpartnerRemitLocation') IS NOT NULL
    DROP FUNCTION bpartnerRemitLocation
go
CREATE FUNCTION bpartnerRemitLocation (p_C_BPartner_ID integer)
	RETURNS integer
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.BPartner.remitLocation(int)'
go

/** Invoice **/
IF OBJECT_ID('invoiceOpen') IS NOT NULL
    DROP FUNCTION incoiceOpen
go
CREATE FUNCTION invoiceOpen (p_C_Invoice_ID integer, p_C_InvoicePaySchedule_ID integer)
	RETURNS numeric(18,2)
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.Invoice.open(int,int)'
go
IF OBJECT_ID('invoicePaid') IS NOT NULL
    DROP FUNCTION invcoicePaid
go
CREATE FUNCTION invoicePaid (p_C_Invoice_ID integer, p_C_Currency_ID integer, p_MultiplierAP integer)
	RETURNS numeric(18,2)
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.Invoice.paid(int,int,int)'
go
IF OBJECT_ID('invoiceDiscount') IS NOT NULL
    DROP FUNCTION incoiceDiscount
go
CREATE FUNCTION invoiceDiscount (p_C_Invoice_ID integer, p_PayDate datetime, p_C_InvoicePaySchedule_ID integer)
	RETURNS numeric(18,2)
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.Invoice.discount(int,java.sql.Timestamp,int)'
go


/** Payment Term **/
IF OBJECT_ID('paymentTermDueDays') IS NOT NULL
    DROP FUNCTION paymentTermDueDays
go
CREATE FUNCTION paymentTermDueDays (p_C_PaymentTerm_ID integer, p_DocDate datetime, p_PayDate datetime)
	RETURNS integer
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.PaymentTerm.dueDays(int,java.sql.Timestamp,java.sql.Timestamp)'
go
IF OBJECT_ID('paymentTermDiscount') IS NOT NULL
    DROP FUNCTION paymentTermDiscount
go
CREATE FUNCTION paymentTermDiscount (p_Amount numeric(18,2), p_C_PaymentTerm_ID integer, p_DocDate datetime, p_PayDate datetime)
	RETURNS numeric(18,2)
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.PaymentTerm.discount(java.math.BigDecimal,int,java.sql.Timestamp,java.sql.Timestamp)'
go


/** Payment */
IF OBJECT_ID('paymentAllocated') IS NOT NULL
    DROP FUNCTION paymentAllocated
go
CREATE FUNCTION paymentAllocated (p_C_Payment_ID integer, p_C_Currency_ID integer)
	RETURNS numeric(18,2)
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.Payment.allocated(int,int)'
go
IF OBJECT_ID('paymentAvailable') IS NOT NULL
    DROP FUNCTION paymentAvailable
go
CREATE FUNCTION paymentAvailable (p_C_Payment_ID integer)
	RETURNS numeric(18,2)
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.Payment.available(int)'
go


/** Account **/
IF OBJECT_ID('acctBalance') IS NOT NULL
    DROP FUNCTION acctBalance
go
CREATE FUNCTION acctBalance (p_Account_ID integer, p_AmtDr numeric(18,2), p_AmtCr numeric(18,2))
	RETURNS numeric(18,2)
 	language java parameter style java
  	external name 'org.openxpertya.sqlj.Account.balance(int,java.math.BigDecimal,java.math.BigDecimal)'
go


	
SELECT openxpertyaVersion(), openxpertyaProperty('java.vendor'), TRUNC(getdate())
go
	