# Mapeo de planillas a Libertya (uso interno del equipo de implementación)

Los datos de cada pestaña empiezan en la fila 6; la columna A es la revisión automática y la fila 5 es un ejemplo: ninguna de las dos se migra.

Los destinos son una propuesta basada en el esquema y los importadores actuales; validarlos al construir cada proceso de carga.

## Libertya_Migracion_1_Maestros.xlsx

### Sucursales

Alta manual de organizaciones (AD_Org / AD_OrgInfo).

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | AD_Org.Value |
| C | Nombre | Sí | AD_Org.Name |
| D | Domicilio | Sí | C_Location.Address1 |
| E | Localidad | Sí | C_Location.City |
| F | Código Postal | No | C_Location.Postal |
| G | Provincia | Sí | C_Location.C_Region_ID |
| H | País | No | C_Location.C_Country_ID |
| I | Teléfono | No | AD_OrgInfo / ubicación |
| J | Correo electrónico | No | AD_OrgInfo |
| K | Responsable | No | Dato informativo |

### Depósitos

Alta manual de M_Warehouse, C_Location y M_Locator (ubicación predeterminada).

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | M_Warehouse.Value |
| C | Nombre | Sí | M_Warehouse.Name |
| D | Sucursal | Sí | M_Warehouse.AD_Org_ID |
| E | Domicilio | No | C_Location.Address1 |
| F | Localidad | No | C_Location.City |
| G | Código Postal | No | C_Location.Postal |
| H | Provincia | No | C_Location.C_Region_ID |
| I | País | No | C_Location.C_Country_ID |
| J | ¿Se vende desde aquí? | Sí | M_Warehouse.StockAvailableForSale |
| K | Observaciones | No | M_Warehouse.Description |

### Puntos de Venta

Relevamiento. Definir tipos de documento, secuencias y puntos de venta.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Número de punto de venta | Sí | C_POS / C_DocType (punto de venta) |
| C | Tipo de comprobante | Sí | C_DocType |
| D | Sucursal | Sí | AD_Org_ID |
| E | ¿Cómo factura? | Sí | Configuración fiscal del tipo de documento |
| F | Último número emitido | Sí | AD_Sequence.CurrentNext = valor + 1 |
| G | Marca y modelo del controlador fiscal | Condicional | C_Controlador_Fiscal |
| H | Observaciones | No | Informativo |

### Libros de Caja

Alta de C_CashBook.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | C_CashBook (clave propia de la planilla) |
| C | Nombre | Sí | C_CashBook.Name |
| D | Tipo de caja | Sí | C_CashBook.CashBookType |
| E | Sucursal | Sí | C_CashBook.AD_Org_ID |
| F | Moneda | Sí | C_CashBook.C_Currency_ID |
| G | Responsable | No | C_CashBook.Description |

### Líneas de Producto

Alta de M_Product_Lines.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | M_Product_Lines.Value |
| C | Nombre | Sí | M_Product_Lines.Name |
| D | Descripción | No | M_Product_Lines.Description |

### Familias

Alta de M_Product_Gamas.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | M_Product_Gamas.Value |
| C | Nombre | Sí | M_Product_Gamas.Name |
| D | Código de línea | Sí | M_Product_Gamas.M_Product_Lines_ID |
| E | Descripción | No | M_Product_Gamas.Description |

### Subfamilias

ImportMProductCategory (I_MProduct_Category).

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | M_Product_Category.Value |
| C | Nombre | Sí | M_Product_Category.Name |
| D | Código de familia | Sí | M_Product_Category.M_Product_Gamas_ID |
| E | Descripción | No | M_Product_Category.Description |

### Marcas

Alta de M_Product_Family (en Libertya se usa como Marca).

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | M_Product_Family.Value |
| C | Nombre | Sí | M_Product_Family.Name |
| D | Descripción | No | M_Product_Family.Description |

### Grupos de Entidad Comercial

Alta de C_BP_Group (antes de importar entidades comerciales).

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | C_BP_Group.Value |
| C | Nombre | Sí | C_BP_Group.Name |
| D | Descripción | No | C_BP_Group.Description |

### Empleados

ImportBPartner con IsEmployee=Y; usuarios y roles se definen aparte.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | I_BPartner.Value |
| C | Apellido y nombre | Sí | I_BPartner.Name |
| D | Tipo de documento | Sí | I_BPartner.TaxIDType |
| E | Número de documento | Sí | I_BPartner.TaxID |
| F | Código de grupo | Sí | I_BPartner.GroupValue |
| G | Sucursal | Sí | C_BPartner.AD_Org_Belonging_ID |
| H | ¿Es vendedor? | Sí | C_BPartner.IsSalesRep |
| I | ¿Usará el sistema? | Sí | AD_User |
| J | Correo electrónico | Condicional | I_BPartner.EMail |
| K | Teléfono | No | I_BPartner.Phone |
| L | Domicilio | No | C_Location.Address1 |
| M | Localidad | No | C_Location.City |
| N | Código Postal | No | C_Location.Postal |
| O | Provincia | No | C_Location.C_Region_ID |
| P | País | No | C_Location.C_Country_ID |

### Clientes

ImportBPartner (I_BPartner) con IsCustomer=Y. Condición de pago, vendedor y estado de crédito se cargan aparte.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | I_BPartner.Value |
| C | Razón social o nombre | Sí | I_BPartner.Name |
| D | Nombre de fantasía | No | I_BPartner.Name2 |
| E | Código de grupo | Sí | I_BPartner.GroupValue |
| F | Condición de IVA | Sí | I_BPartner.C_Categoria_Iva_Codigo |
| G | Tipo de documento | Sí | I_BPartner.TaxIDType |
| H | Número de documento | Condicional | I_BPartner.TaxID |
| I | Situación en Ingresos Brutos | No | I_BPartner.IIBBType |
| J | Número de Ingresos Brutos | No | I_BPartner.IIBB |
| K | Domicilio | Sí | C_Location.Address1 |
| L | Localidad | Sí | C_Location.City |
| M | Código Postal | No | C_Location.Postal |
| N | Provincia | Sí | C_Location.C_Region_ID |
| O | País | No | C_Location.C_Country_ID |
| P | Teléfono | No | I_BPartner.Phone |
| Q | Correo electrónico | No | I_BPartner.EMail |
| R | Persona de contacto | No | I_BPartner.ContactName |
| S | Límite de crédito | No | I_BPartner.SO_CreditLimit |
| T | Estado de crédito | No | C_BPartner.SOCreditStatus |
| U | Condición de pago habitual | No | C_BPartner.C_PaymentTerm_ID |
| V | Código de vendedor | No | C_BPartner.SalesRep_ID |

### Proveedores

ImportBPartner (I_BPartner) con IsVendor=Y. Condición de pago y datos bancarios se cargan aparte.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | I_BPartner.Value |
| C | Razón social o nombre | Sí | I_BPartner.Name |
| D | Nombre de fantasía | No | I_BPartner.Name2 |
| E | Código de grupo | Sí | I_BPartner.GroupValue |
| F | Condición de IVA | Sí | I_BPartner.C_Categoria_Iva_Codigo |
| G | Tipo de documento | Sí | I_BPartner.TaxIDType |
| H | Número de documento | Condicional | I_BPartner.TaxID |
| I | Situación en Ingresos Brutos | No | I_BPartner.IIBBType |
| J | Número de Ingresos Brutos | No | I_BPartner.IIBB |
| K | Domicilio | Sí | C_Location.Address1 |
| L | Localidad | Sí | C_Location.City |
| M | Código Postal | No | C_Location.Postal |
| N | Provincia | Sí | C_Location.C_Region_ID |
| O | País | No | C_Location.C_Country_ID |
| P | Teléfono | No | I_BPartner.Phone |
| Q | Correo electrónico | No | I_BPartner.EMail |
| R | Persona de contacto | No | I_BPartner.ContactName |
| S | Condición de pago habitual | No | C_BPartner.PO_PaymentTerm_ID |
| T | CBU | No | C_BP_BankAccount |
| U | Alias bancario | No | C_BP_BankAccount |
| V | Cheques a nombre de | No | C_BPartner.A_Name_Check |

### Artículos

ImportProduct (I_Product). Los códigos de barras deben ser únicos.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | I_Product.Value |
| C | Nombre | Sí | I_Product.Name |
| D | Descripción | No | I_Product.Description |
| E | Tipo | Sí | I_Product.ProductType (I/S/E) |
| F | Código de subfamilia | Sí | I_Product.ProductCategory_Value |
| G | Código de marca | No | I_Product.ProductFamily_Value |
| H | Unidad de medida | Sí | I_Product.X12DE355 |
| I | IVA | Sí | I_Product.C_TaxCategory_Name |
| J | ¿Se vende? | Sí | I_Product.IsSold |
| K | ¿Se compra? | Sí | I_Product.IsPurchased |
| L | Código de barras | No | I_Product.UPC |
| M | Código del proveedor habitual | No | I_Product.BPartner_Value |
| N | Código que usa el proveedor | No | I_Product.VendorProductNo |
| O | Precio de compra sin IVA | No | I_Product.PricePO |
| P | Peso (kg) | No | I_Product.Weight |
| Q | ¿Está activo? | Sí | I_Product.IsActive |

### Cuentas Bancarias

Alta de C_Bank y C_BankAccount.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | Clave propia de la planilla |
| C | Banco | Sí | C_Bank.Name |
| D | Sucursal del banco | No | C_BankAccount.Sucursal |
| E | Tipo de cuenta | Sí | C_BankAccount.BankAccountType (C/S) |
| F | Número de cuenta | Sí | C_BankAccount.AccountNo |
| G | CBU o CVU | Sí | C_BankAccount.BBAN (confirmar) |
| H | Alias | No | C_BankAccount.Description |
| I | Moneda | Sí | C_BankAccount.C_Currency_ID |
| J | Sucursal de la empresa | No | C_BankAccount.AD_Org_ID |
| K | Límite de descubierto | No | C_BankAccount.CreditLimit |

### Tarjetas y Comercios

Alta de M_EntidadFinanciera (requiere además una cuenta de cupones, C_BankAccount_ID).

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código | Sí | M_EntidadFinanciera.Value |
| C | Nombre | Sí | M_EntidadFinanciera.Name |
| D | Tarjeta | Sí | M_EntidadFinanciera.CreditCardType |
| E | Número de comercio o establecimiento | Sí | M_EntidadFinanciera.EstablishmentNumber |
| F | Empresa que liquida | Sí | M_EntidadFinanciera.C_BPartner_ID |
| G | CUIT de la empresa que liquida | Sí | C_BPartner.TaxID |
| H | Cuenta donde acreditan | Sí | M_EntidadFinanciera.C_BankAccount_Settlement_ID |
| I | Sucursal | No | M_EntidadFinanciera.AD_Org_ID |

### Planes de Tarjetas

Alta de M_EntidadFinancieraPlan; el recargo/descuento requiere un esquema de descuento.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código de tarjeta/comercio | Sí | M_EntidadFinancieraPlan.M_EntidadFinanciera_ID |
| C | Código de plan | Sí | M_EntidadFinancieraPlan.Value |
| D | Nombre del plan | Sí | M_EntidadFinancieraPlan.Name |
| E | Cuotas que paga el cliente | Sí | M_EntidadFinancieraPlan.CuotasPago |
| F | Cuotas en que usted cobra | Sí | M_EntidadFinancieraPlan.CuotasCobro |
| G | Recargo o descuento (%) | No | M_EntidadFinancieraPlan.M_DiscountSchema_ID |
| H | Días para acreditación | Sí | M_EntidadFinancieraPlan.AccreditationDays |
| I | Vigente desde | Sí | M_EntidadFinancieraPlan.DateFrom |
| J | Vigente hasta | Sí | M_EntidadFinancieraPlan.DateTo |

### Stock

Alta de M_Locator y M_Replenish.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código de depósito | Sí | M_Replenish.M_Warehouse_ID |
| C | Código de artículo | Sí | M_Replenish.M_Product_ID |
| D | Ubicación | No | M_Locator.Value |
| E | Stock mínimo | No | M_Replenish.Level_Min |
| F | Stock máximo | No | M_Replenish.Level_Max |

## Libertya_Migracion_2_Saldos.xlsx

### Caja Efectivo

Saldo inicial de cada Libro de Caja.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Fecha de corte | Sí | Fecha del movimiento / asiento inicial |
| C | Código de caja | Sí | C_Cash.C_CashBook_ID |
| D | Moneda | Sí | C_CashLine.C_Currency_ID |
| E | Importe en efectivo | Sí | C_CashLine.Amount |
| F | Cotización | Condicional | C_Conversion_Rate |
| G | Observaciones | No | C_CashLine.Description |

### Bancos

Saldo inicial por cuenta bancaria. La diferencia banco/sistema son partidas pendientes de conciliar.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Fecha de corte | Sí | Fecha del movimiento / asiento inicial |
| C | Código de cuenta | Sí | C_BankStatement.C_BankAccount_ID |
| D | Saldo según el banco | Sí | C_BankStatement (saldo extracto) |
| E | Saldo según su contabilidad | Sí | Saldo contable inicial |
| F | Moneda | Sí | C_Currency_ID |
| G | Cotización | Condicional | C_Conversion_Rate |
| H | Observaciones | No | Description |

### Stock

ImportInventory (I_Inventory). El costo se carga aparte.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Fecha de corte | Sí | Fecha del movimiento / asiento inicial |
| C | Código de depósito | Sí | I_Inventory.WarehouseValue |
| D | Código de artículo | Sí | I_Inventory.Value |
| E | Cantidad | Sí | I_Inventory.QtyCount |
| F | Costo unitario sin IVA | Sí | Costo inicial (M_Product_Costing / lista de costo) |
| G | Moneda del costo | Sí | C_Currency_ID |
| H | Cotización | Condicional | C_Conversion_Rate |
| I | Lote | No | I_Inventory.Lot |
| J | Número de serie | No | I_Inventory.SerNo |
| K | Fecha de vencimiento | No | M_AttributeSetInstance.GuaranteeDate |

### Cta Cte Clientes

ImportInvoice (I_Invoice) con IsSOTrx=Y, usando tipos de documento de saldo inicial.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código de cliente | Sí | I_Invoice.BPartnerValue |
| C | Tipo de comprobante | Sí | I_Invoice.DocTypeName (define el signo) |
| D | Letra | Sí | Tipo de documento |
| E | Punto de venta | Sí | I_Invoice.DocumentNo (parte 1) |
| F | Número de comprobante | Sí | I_Invoice.DocumentNo (parte 2) |
| G | Fecha del comprobante | Sí | I_Invoice.DateInvoiced |
| H | Fecha de vencimiento | Sí | C_InvoicePaySchedule.DueDate |
| I | Número de cuota | No | C_InvoicePaySchedule |
| J | Moneda | Sí | I_Invoice.C_Currency_ID |
| K | Cotización | Condicional | C_Conversion_Rate |
| L | Importe original | Sí | Informativo |
| M | Saldo pendiente | Sí | I_Invoice.PriceActual (comprobante de saldo inicial) |
| N | Código de sucursal | Sí | I_Invoice.OrgValue |
| O | Observaciones | No | I_Invoice.Description |

### Cta Cte Proveedores

ImportInvoice (I_Invoice) con IsSOTrx=N, usando tipos de documento de saldo inicial.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Código de proveedor | Sí | I_Invoice.BPartnerValue |
| C | Tipo de comprobante | Sí | I_Invoice.DocTypeName (define el signo) |
| D | Letra | Sí | Tipo de documento |
| E | Punto de venta | Sí | I_Invoice.DocumentNo (parte 1) |
| F | Número de comprobante | Sí | I_Invoice.DocumentNo (parte 2) |
| G | Fecha del comprobante | Sí | I_Invoice.DateInvoiced |
| H | Fecha de vencimiento | Sí | C_InvoicePaySchedule.DueDate |
| I | Número de cuota | No | C_InvoicePaySchedule |
| J | Moneda | Sí | I_Invoice.C_Currency_ID |
| K | Cotización | Condicional | C_Conversion_Rate |
| L | Importe original | Sí | Informativo |
| M | Saldo pendiente | Sí | I_Invoice.PriceActual (comprobante de saldo inicial) |
| N | Código de sucursal | Sí | I_Invoice.OrgValue |
| O | Observaciones | No | I_Invoice.Description |

### Cheques en Cartera

Cobros con cheque (C_Payment TenderType=K) sobre la cuenta «Cheques en cartera». Definir contrapartida para no duplicar el saldo de cuenta corriente.

| Col. | Campo | Obligatorio | Destino |
|---|---|---|---|
| B | Banco emisor | Sí | C_Payment.A_Bank |
| C | Número de cheque | Sí | C_Payment.CheckNo |
| D | Tipo | Sí | Medio de cobro |
| E | Fecha de emisión | Sí | C_Payment.DateEmissionCheck |
| F | Fecha de pago | Sí | C_Payment.DueDate |
| G | Importe | Sí | C_Payment.PayAmt |
| H | Moneda | Sí | C_Payment.C_Currency_ID |
| I | Cotización | Condicional | C_Conversion_Rate |
| J | Nombre del firmante | Sí | C_Payment.A_Name |
| K | CUIT del firmante | Sí | C_Payment.A_CUIT |
| L | Código de cliente | Sí | C_Payment.C_BPartner_ID |
| M | Código de sucursal | Sí | C_Payment.AD_Org_ID |
| N | Observaciones | No | C_Payment.Description |
