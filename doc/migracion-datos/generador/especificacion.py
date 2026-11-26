"""Definición de pestañas y columnas de las planillas de migración (OP#8600)."""

SN = ["Sí", "No"]

LISTAS = {
    "sino": SN,
    "moneda": ["Pesos", "Dólares", "Euros"],
    "tipo_doc": ["CUIT", "CUIL", "DNI", "CDI", "Pasaporte", "CI extranjera", "Sin identificar"],
    "cond_iva": ["Responsable Inscripto", "Monotributo", "Consumidor Final", "Exento", "No Responsable",
                 "No Categorizado", "IVA No Alcanzado", "Responsable Inscripto Factura M",
                 "Responsable M con CBU informado", "Comercio Exterior"],
    "iibb": ["Local", "Convenio Multilateral", "No inscripto", "Régimen Simplificado"],
    "provincia": ["Buenos Aires", "Ciudad Autónoma de Buenos Aires", "Catamarca", "Chaco", "Chubut", "Córdoba",
                  "Corrientes", "Entre Ríos", "Formosa", "Jujuy", "La Pampa", "La Rioja", "Mendoza", "Misiones",
                  "Neuquén", "Río Negro", "Salta", "San Juan", "San Luis", "Santa Cruz", "Santa Fe",
                  "Santiago del Estero", "Tierra del Fuego", "Tucumán", "Exterior"],
    "tipo_articulo": ["Producto", "Servicio", "Gasto"],
    "unidad": ["Unidad", "Kilogramo", "Gramo", "Litro", "Metro", "Metro cuadrado", "Metro cúbico",
               "Metro lineal", "Caja", "Hora", "Día", "Mes"],
    "alicuota": ["21%", "10,5%", "27%", "5%", "2,5%", "0%", "Exento", "No gravado"],
    "estado_credito": ["Normal", "Sin crédito", "Suspendido", "Bloqueado", "Inhabilitado"],
    "tipo_cuenta": ["Cuenta Corriente", "Caja de Ahorro"],
    "tarjeta": ["Visa", "MasterCard", "American Express", "Cabal", "Naranja", "Maestro", "Diners", "Discover",
                "First Data", "Nativa", "Patagonia 365", "Confiable", "TDF", "UnionPay", "Otra"],
    "tipo_caja": ["Caja General", "Caja Diaria"],
    "tecnologia": ["Factura electrónica ARCA (AFIP)", "Controlador fiscal", "Factura electrónica de exportación",
                   "Talonario manual", "No emite comprobantes fiscales"],
    "comprobante_pv": ["Factura A", "Factura B", "Factura C", "Factura M", "Factura E", "Nota de Crédito A",
                       "Nota de Crédito B", "Nota de Crédito C", "Nota de Débito A", "Nota de Débito B",
                       "Nota de Débito C", "Factura de Crédito MiPyME A", "Remito", "Recibo"],
    "comprobante_cc": ["Factura", "Nota de Débito", "Nota de Crédito", "Recibo o pago a cuenta", "Anticipo",
                       "Saldo inicial"],
    "letra": ["A", "B", "C", "E", "M", "X", "Sin letra"],
    "tipo_cheque": ["Físico", "Electrónico (e-cheq)"],
}


def C(id, titulo, tipo, oblig, ayuda, ej=None, dest="", **kw):
    return dict(id=id, titulo=titulo, tipo=tipo, oblig=oblig, ayuda=ayuda, ej=ej, dest=dest, **kw)


def T(id, titulo, oblig, ayuda, ej=None, dest="", largo=255, **kw):
    return C(id, titulo, "texto", oblig, ayuda, ej, dest, largo=largo, **kw)


def L(id, titulo, lista, oblig, ayuda, ej=None, dest="", **kw):
    return C(id, titulo, "lista", oblig, ayuda, ej, dest, lista=lista, **kw)


def R(id, titulo, ref, oblig, ayuda, ej=None, dest="", largo=40, **kw):
    return C(id, titulo, "ref", oblig, ayuda, ej, dest, ref=ref, largo=largo, **kw)


def codigo(dest, que, ej, largo=40):
    return T("codigo", "Código", True,
             f"Código que identifica {que}. No se puede repetir. Use el mismo que en su sistema actual.",
             ej, dest, largo)


def H(id, nombre, para_que, columnas, destino, filas, clave=("codigo",), **kw):
    return dict(id=id, nombre=nombre, para_que=para_que, columnas=columnas, destino=destino,
                filas=filas, clave=list(clave) if clave else None, **kw)


RELEV = "BF8F00"  # color de las pestañas de relevamiento
PRODUCTO = "2E75B6"
PERSONAS = "7030A0"
FINANZAS = "548235"


def direccion(oblig=False):
    return [
        T("domicilio", "Domicilio", oblig, "Calle y número. Ejemplo: Av. San Martín 1250", "Av. San Martín 1250",
          "C_Location.Address1", 60, ancho=30),
        T("localidad", "Localidad", oblig, "Ciudad o localidad.", "Neuquén", "C_Location.City", 60),
        T("cp", "Código Postal", False, "Código postal.", "8300", "C_Location.Postal", 10),
        L("provincia", "Provincia", "provincia", oblig, "Elija la provincia de la lista. Si es otro país, elija Exterior.",
          "Neuquén", "C_Location.C_Region_ID"),
        T("pais", "País", False, "Completar solo si NO es Argentina.", None, "C_Location.C_Country_ID", 60),
    ]


# ---------------------------------------------------------------- MAESTROS
sucursales = H(
    "suc", "Sucursales", "Las sucursales o locales de la empresa.",
    [codigo("AD_Org.Value", "la sucursal", "CENTRAL"),
     T("nombre", "Nombre", True, "Nombre de la sucursal.", "Casa Central", "AD_Org.Name", 60, ancho=30),
     *direccion(True),
     T("telefono", "Teléfono", False, "Teléfono de la sucursal.", "299 4455667", "AD_OrgInfo / ubicación", 40),
     T("email", "Correo electrónico", False, "Correo de la sucursal.", "central@miempresa.com.ar", "AD_OrgInfo", 60),
     T("responsable", "Responsable", False, "Persona a cargo.", "Ana Pérez", "Dato informativo", 60)],
    "Alta manual de organizaciones (AD_Org / AD_OrgInfo).", 100, color=RELEV)

depositos = H(
    "dep", "Depósitos", "Los lugares donde se guarda mercadería.",
    [codigo("M_Warehouse.Value", "el depósito", "DEP01"),
     T("nombre", "Nombre", True, "Nombre del depósito.", "Depósito Principal", "M_Warehouse.Name", 60, ancho=30),
     R("sucursal", "Sucursal", ("suc", "codigo"), True, "Elija el código de la sucursal (pestaña Sucursales).",
       "CENTRAL", "M_Warehouse.AD_Org_ID"),
     *direccion(False),
     L("venta", "¿Se vende desde aquí?", "sino", True, "Sí: su mercadería está disponible para la venta.",
       "Sí", "M_Warehouse.StockAvailableForSale"),
     T("obs", "Observaciones", False, "Aclaraciones.", None, "M_Warehouse.Description", 255)],
    "Alta manual de M_Warehouse, C_Location y M_Locator (ubicación predeterminada).", 200, color=RELEV)

puntos_venta = H(
    "pv", "Puntos de Venta", "Puntos de venta, cómo facturan y su numeración. Una fila por punto de venta y tipo de comprobante.",
    [C("numero", "Número de punto de venta", "entero", True,
       "El número que aparece delante del número de factura. En 0003-00001234 es el 3.", 3, "C_POS / C_DocType (punto de venta)"),
     L("comprobante", "Tipo de comprobante", "comprobante_pv", True,
       "Elija el comprobante. Cargue una fila por cada tipo que emite este punto de venta.", "Factura A", "C_DocType"),
     R("sucursal", "Sucursal", ("suc", "codigo"), True, "Sucursal donde se usa este punto de venta.", "CENTRAL", "AD_Org_ID"),
     L("tecnologia", "¿Cómo factura?", "tecnologia", True,
       "Cómo se emite el comprobante. Si no lo sabe, consulte con su contador.", "Factura electrónica ARCA (AFIP)",
       "Configuración fiscal del tipo de documento", ancho=34),
     C("ultimo", "Último número emitido", "entero", True,
       "Último número usado a la fecha de corte. Si nunca se usó, escriba 0.", 1234, "AD_Sequence.CurrentNext = valor + 1"),
     T("modelo", "Marca y modelo del controlador fiscal",
       ('{tecnologia}="Controlador fiscal"', "Solo si usa controlador fiscal"),
       "Marca y modelo de la impresora fiscal.", None, "C_Controlador_Fiscal", 60, ancho=30),
     T("obs", "Observaciones", False, "Aclaraciones: quién lo usa, desde qué computadora, etc.", None, "Informativo")],
    "Relevamiento. Definir tipos de documento, secuencias y puntos de venta.", 500,
    clave=("numero", "comprobante"), color=RELEV)

libros_caja = H(
    "caja", "Libros de Caja", "Las cajas donde la empresa maneja efectivo.",
    [codigo("C_CashBook (clave propia de la planilla)", "la caja", "CAJA01"),
     T("nombre", "Nombre", True, "Nombre de la caja.", "Caja Administración", "C_CashBook.Name", 60, ancho=30),
     L("tipo", "Tipo de caja", "tipo_caja", True,
       "Caja Diaria: la de mostrador, que se abre y cierra cada día. Caja General: tesorería o administración.",
       "Caja General", "C_CashBook.CashBookType"),
     R("sucursal", "Sucursal", ("suc", "codigo"), True, "Sucursal a la que pertenece.", "CENTRAL", "C_CashBook.AD_Org_ID"),
     L("moneda", "Moneda", "moneda", True, "Moneda de la caja. Si maneja dos monedas, cargue dos cajas.", "Pesos",
       "C_CashBook.C_Currency_ID"),
     T("responsable", "Responsable", False, "Persona a cargo de la caja.", "Ana Pérez", "C_CashBook.Description", 60)],
    "Alta de C_CashBook.", 200, color=RELEV)

lineas = H(
    "lin", "Líneas de Producto", "El primer nivel (el más general) para clasificar sus artículos.",
    [codigo("M_Product_Lines.Value", "la línea", "FER"),
     T("nombre", "Nombre", True, "Nombre de la línea.", "Ferretería", "M_Product_Lines.Name", 60, ancho=34),
     T("descripcion", "Descripción", False, "Aclaración.", None, "M_Product_Lines.Description", 255, ancho=40)],
    "Alta de M_Product_Lines.", 300, color=PRODUCTO)

familias = H(
    "fam", "Familias", "El segundo nivel: grupos dentro de cada línea.",
    [codigo("M_Product_Gamas.Value", "la familia", "FER-HER"),
     T("nombre", "Nombre", True, "Nombre de la familia.", "Herrajes", "M_Product_Gamas.Name", 60, ancho=34),
     R("linea", "Código de línea", ("lin", "codigo"), True, "Línea a la que pertenece (pestaña Líneas de Producto).",
       "FER", "M_Product_Gamas.M_Product_Lines_ID"),
     T("descripcion", "Descripción", False, "Aclaración.", None, "M_Product_Gamas.Description", 255, ancho=40)],
    "Alta de M_Product_Gamas.", 1000, color=PRODUCTO)

subfamilias = H(
    "sub", "Subfamilias", "El tercer nivel: grupos dentro de cada familia. Cada artículo pertenece a una subfamilia.",
    [codigo("M_Product_Category.Value", "la subfamilia", "FER-HER-CER"),
     T("nombre", "Nombre", True, "Nombre de la subfamilia.", "Cerraduras y candados", "M_Product_Category.Name", 60, ancho=34),
     R("familia", "Código de familia", ("fam", "codigo"), True, "Familia a la que pertenece (pestaña Familias).",
       "FER-HER", "M_Product_Category.M_Product_Gamas_ID"),
     T("descripcion", "Descripción", False, "Aclaración.", None, "M_Product_Category.Description", 255, ancho=40)],
    "ImportMProductCategory (I_MProduct_Category).", 3000, color=PRODUCTO)

marcas = H(
    "mar", "Marcas", "Las marcas de los artículos.",
    [codigo("M_Product_Family.Value", "la marca", "STAN"),
     T("nombre", "Nombre", True, "Nombre de la marca.", "Stanley", "M_Product_Family.Name", 60, ancho=34),
     T("descripcion", "Descripción", False, "Aclaración.", None, "M_Product_Family.Description", 255, ancho=40)],
    "Alta de M_Product_Family (en Libertya se usa como Marca).", 2000, color=PRODUCTO)

grupos = H(
    "gru", "Grupos de Entidad Comercial",
    "Categorías para agrupar clientes y proveedores (ej.: Minoristas, Mayoristas, Proveedores locales).",
    [codigo("C_BP_Group.Value", "el grupo", "MIN"),
     T("nombre", "Nombre", True, "Nombre del grupo.", "Clientes minoristas", "C_BP_Group.Name", 60, ancho=34),
     T("descripcion", "Descripción", False, "Aclaración.", None, "C_BP_Group.Description", 255, ancho=40)],
    "Alta de C_BP_Group (antes de importar entidades comerciales).", 300, color=PERSONAS)

proveedor_cols = [
    T("codigo", "Código", True,
      "Código del proveedor. Si además es cliente, use el MISMO código en las dos pestañas.",
      "P0001", "I_BPartner.Value", 40),
    T("nombre", "Razón social o nombre", True, "Nombre completo como figura en las facturas.",
      "Distribuidora del Sur S.A.", "I_BPartner.Name", 60, ancho=34),
    T("fantasia", "Nombre de fantasía", False, "Nombre comercial.", "DiSur", "I_BPartner.Name2", 60, ancho=28),
]


def fiscales(ejtipo="CUIT", ejnro="30712345671"):
    return [
        R("grupo", "Código de grupo", ("gru", "codigo"), True,
          "Grupo al que pertenece (pestaña Grupos de Entidad Comercial).", "MIN", "I_BPartner.GroupValue"),
        L("iva", "Condición de IVA", "cond_iva", True, "Situación frente al IVA.", "Responsable Inscripto",
          "I_BPartner.C_Categoria_Iva_Codigo", ancho=30),
        L("tipo_doc", "Tipo de documento", "tipo_doc", True, "Para empresas elija CUIT. Para personas, DNI o CUIL.",
          ejtipo, "I_BPartner.TaxIDType"),
        T("documento", "Número de documento",
          ('{iva}<>"Consumidor Final"', "Sí, salvo Consumidor Final"),
          "Solo números, sin guiones, puntos ni espacios. El CUIT tiene 11 números.", ejnro, "I_BPartner.TaxID", 20),
        L("iibb_tipo", "Situación en Ingresos Brutos", "iibb", False, "Régimen de Ingresos Brutos.",
          "Convenio Multilateral", "I_BPartner.IIBBType", ancho=28),
        T("iibb", "Número de Ingresos Brutos", False, "Número de inscripción.", "9011234567", "I_BPartner.IIBB", 128),
        *direccion(True),
        T("telefono", "Teléfono", False, "Teléfono.", "299 4455667", "I_BPartner.Phone", 40),
        T("email", "Correo electrónico", False, "Un solo correo.", "compras@empresa.com.ar", "I_BPartner.EMail", 60, ancho=30),
        T("contacto", "Persona de contacto", False, "Nombre y apellido.", "Juan Gómez", "I_BPartner.ContactName", 60),
    ]


REGLAS_DOC = [
    ('AND(OR({tipo_doc}="CUIT",{tipo_doc}="CUIL"),{documento}<>"",'
     'OR(LEN({documento})<>11,NOT(ISNUMBER(-{documento}))))', "El CUIT o CUIL debe tener 11 números sin guiones"),
]

clientes = H(
    "cli", "Clientes", "Todos sus clientes, con datos fiscales y comerciales.",
    [T("codigo", "Código", True,
       "Código del cliente. Si además es proveedor, use el MISMO código en las dos pestañas.",
       "C0001", "I_BPartner.Value", 40),
     T("nombre", "Razón social o nombre", True, "Nombre completo como figura en las facturas.",
       "Ferretería El Tornillo S.R.L.", "I_BPartner.Name", 60, ancho=34),
     T("fantasia", "Nombre de fantasía", False, "Nombre comercial.", "El Tornillo", "I_BPartner.Name2", 60, ancho=28),
     *fiscales(),
     C("limite", "Límite de crédito", "importe", False,
       "Monto máximo en pesos que puede deber. Vacío si no tiene límite.", 500000, "I_BPartner.SO_CreditLimit"),
     L("credito", "Estado de crédito", "estado_credito", False, "Si no sabe, elija Normal.", "Normal",
       "C_BPartner.SOCreditStatus"),
     T("condicion", "Condición de pago habitual", False, "Ejemplo: Contado, 30 días.", "30 días",
       "C_BPartner.C_PaymentTerm_ID", 60),
     R("vendedor", "Código de vendedor", ("emp", "codigo"), False,
       "Código del empleado que lo atiende (pestaña Empleados).", "E001", "C_BPartner.SalesRep_ID")],
    "ImportBPartner (I_BPartner) con IsCustomer=Y. Condición de pago, vendedor y estado de crédito se cargan aparte.",
    10000, reglas=REGLAS_DOC, color=PERSONAS)

proveedores = H(
    "pro", "Proveedores", "Todos sus proveedores, con datos fiscales y bancarios.",
    [*proveedor_cols, *fiscales(),
     T("condicion", "Condición de pago habitual", False, "Ejemplo: Contado, 30 días.", "30 días",
       "C_BPartner.PO_PaymentTerm_ID", 60),
     T("cbu", "CBU", False, "Los 22 números de la CBU para transferirle, sin espacios.", "0970001230000012345678",
       "C_BP_BankAccount", 22, ancho=28),
     T("alias", "Alias bancario", False, "Alias de la cuenta.", "DISUR.PAGOS", "C_BP_BankAccount", 40),
     T("cheque_orden", "Cheques a nombre de", False, "Completar solo si es distinto a la razón social.", None,
       "C_BPartner.A_Name_Check", 60)],
    "ImportBPartner (I_BPartner) con IsVendor=Y. Condición de pago y datos bancarios se cargan aparte.",
    5000,
    reglas=REGLAS_DOC + [('AND({cbu}<>"",LEN({cbu})<>22)', "La CBU debe tener 22 números")], color=PERSONAS)

empleados = H(
    "emp", "Empleados", "El personal de la empresa, incluidos vendedores y cajeros.",
    [codigo("I_BPartner.Value", "al empleado", "E001"),
     T("nombre", "Apellido y nombre", True, "Apellido y nombre completos.", "Pérez, Ana", "I_BPartner.Name", 60, ancho=30),
     L("tipo_doc", "Tipo de documento", "tipo_doc", True, "CUIL o DNI.", "CUIL", "I_BPartner.TaxIDType"),
     T("documento", "Número de documento", True, "Solo números, sin guiones ni puntos.", "27301112223",
       "I_BPartner.TaxID", 20),
     R("grupo", "Código de grupo", ("gru", "codigo"), True,
       "Grupo (cree uno llamado Empleados en la pestaña Grupos de Entidad Comercial).", "EMP", "I_BPartner.GroupValue"),
     R("sucursal", "Sucursal", ("suc", "codigo"), True, "Sucursal donde trabaja.", "CENTRAL", "C_BPartner.AD_Org_Belonging_ID"),
     L("vendedor", "¿Es vendedor?", "sino", True, "Sí: figura como vendedor en las ventas.", "Sí", "C_BPartner.IsSalesRep"),
     L("usuario", "¿Usará el sistema?", "sino", True, "Sí: necesita usuario para ingresar a Libertya.", "Sí", "AD_User"),
     T("email", "Correo electrónico", ('{usuario}="Sí"', "Sí, si usará el sistema"), "Correo personal de trabajo.",
       "ana.perez@miempresa.com.ar", "I_BPartner.EMail", 60, ancho=30),
     T("telefono", "Teléfono", False, "Teléfono.", "299 154455667", "I_BPartner.Phone", 40),
     *direccion(False)],
    "ImportBPartner con IsEmployee=Y; usuarios y roles se definen aparte.", 1000,
    reglas=REGLAS_DOC, color=PERSONAS)

articulos = H(
    "art", "Artículos", "Todos los productos y servicios que compra o vende.",
    [codigo("I_Product.Value", "el artículo", "ART0001"),
     T("nombre", "Nombre", True, "Nombre del artículo (máximo 60 letras).", "Candado de bronce 40 mm",
       "I_Product.Name", 60, ancho=38),
     T("descripcion", "Descripción", False, "Detalle más largo.", None, "I_Product.Description", 255, ancho=30),
     L("tipo", "Tipo", "tipo_articulo", True,
       "Producto: mercadería. Servicio: trabajo que se cobra o paga. Gasto: luz, alquiler, etc.", "Producto",
       "I_Product.ProductType (I/S/E)"),
     R("subfamilia", "Código de subfamilia", ("sub", "codigo"), True,
       "Subfamilia del artículo (pestaña Subfamilias).", "FER-HER-CER", "I_Product.ProductCategory_Value"),
     R("marca", "Código de marca", ("mar", "codigo"), False, "Marca (pestaña Marcas).", "STAN",
       "I_Product.ProductFamily_Value"),
     L("unidad", "Unidad de medida", "unidad", True, "En qué unidad lo vende o cuenta.", "Unidad", "I_Product.X12DE355"),
     L("iva", "IVA", "alicuota", True, "Porcentaje de IVA que lleva.", "21%", "I_Product.C_TaxCategory_Name"),
     L("vende", "¿Se vende?", "sino", True, "Sí: aparece en las ventas.", "Sí", "I_Product.IsSold"),
     L("compra", "¿Se compra?", "sino", True, "Sí: aparece en las compras.", "Sí", "I_Product.IsPurchased"),
     T("barras", "Código de barras", False, "El número debajo de las barras.", "7791234567890", "I_Product.UPC", 30),
     R("proveedor", "Código del proveedor habitual", ("pro", "codigo"), False, "Pestaña Proveedores.", "P0001",
       "I_Product.BPartner_Value"),
     T("cod_prov", "Código que usa el proveedor", False, "Cómo identifica el proveedor a este artículo.", "DS-4471",
       "I_Product.VendorProductNo", 30),
     C("costo", "Precio de compra sin IVA", "importe", False, "Último precio de compra, en pesos, sin IVA.", 4500,
       "I_Product.PricePO"),
     C("peso", "Peso (kg)", "cantidad", False, "Peso de una unidad en kilos.", 0.25, "I_Product.Weight"),
     L("activo", "¿Está activo?", "sino", True, "No: ya no se usa.", "Sí", "I_Product.IsActive")],
    "ImportProduct (I_Product). Los códigos de barras deben ser únicos.", 10000,
    reglas=[('AND({barras}<>"",COUNTIF($L$6:$L$10005,{barras}&"")>1)', "Código de barras repetido")],
    color=PRODUCTO)

cuentas = H(
    "cta", "Cuentas Bancarias", "Las cuentas bancarias de la empresa.",
    [codigo("Clave propia de la planilla", "la cuenta (invéntelo, ej.: NACION-CC)", "NACION-CC"),
     T("banco", "Banco", True, "Nombre del banco.", "Banco de la Nación Argentina", "C_Bank.Name", 60, ancho=32),
     T("sucursal_banco", "Sucursal del banco", False, "Nombre o número de la sucursal.", "Neuquén Centro",
       "C_BankAccount.Sucursal", 40),
     L("tipo", "Tipo de cuenta", "tipo_cuenta", True, "Tipo de cuenta.", "Cuenta Corriente",
       "C_BankAccount.BankAccountType (C/S)"),
     T("numero", "Número de cuenta", True, "Como figura en el resumen del banco.", "1234567/8",
       "C_BankAccount.AccountNo", 20),
     T("cbu", "CBU o CVU", True, "Los 22 números, sin espacios.", "0110001230000012345678",
       "C_BankAccount.BBAN (confirmar)", 22, ancho=28),
     T("alias", "Alias", False, "Alias de la cuenta.", "MIEMPRESA.NACION", "C_BankAccount.Description", 40),
     L("moneda", "Moneda", "moneda", True, "Moneda de la cuenta.", "Pesos", "C_BankAccount.C_Currency_ID"),
     R("sucursal", "Sucursal de la empresa", ("suc", "codigo"), False,
       "Completar solo si la cuenta la usa una única sucursal.", "CENTRAL", "C_BankAccount.AD_Org_ID"),
     C("descubierto", "Límite de descubierto", "importe", False, "Monto autorizado en descubierto.", 0,
       "C_BankAccount.CreditLimit")],
    "Alta de C_Bank y C_BankAccount.", 200,
    reglas=[('AND({cbu}<>"",LEN({cbu})<>22)', "La CBU debe tener 22 números")], color=FINANZAS)

comercios = H(
    "com", "Tarjetas y Comercios",
    "Cada tarjeta con la que cobra, con su número de comercio. Una fila por tarjeta y número de comercio.",
    [codigo("M_EntidadFinanciera.Value", "esta tarjeta/comercio (ej.: VISA-CENTRAL)", "VISA-CENTRAL"),
     T("nombre", "Nombre", True, "Nombre para reconocerla.", "Visa Crédito Casa Central",
       "M_EntidadFinanciera.Name", 255, ancho=32),
     L("tarjeta", "Tarjeta", "tarjeta", True, "Marca de la tarjeta.", "Visa", "M_EntidadFinanciera.CreditCardType"),
     T("comercio", "Número de comercio o establecimiento", True,
       "El número que asigna la tarjeta o procesadora. Figura en las liquidaciones.", "12345678",
       "M_EntidadFinanciera.EstablishmentNumber", 45, ancho=28),
     T("liquida", "Empresa que liquida", True,
       "Quién le paga los cupones. Ejemplo: Prisma, Fiserv, Naranja X.", "Prisma Medios de Pago",
       "M_EntidadFinanciera.C_BPartner_ID", 60, ancho=28),
     T("cuit_liquida", "CUIT de la empresa que liquida", True, "11 números sin guiones.", "30598910045",
       "C_BPartner.TaxID", 11),
     R("cuenta", "Cuenta donde acreditan", ("cta", "codigo"), True,
       "Cuenta bancaria donde le depositan (pestaña Cuentas Bancarias).", "NACION-CC",
       "M_EntidadFinanciera.C_BankAccount_Settlement_ID"),
     R("sucursal", "Sucursal", ("suc", "codigo"), False, "Completar solo si el número de comercio es de una sucursal.",
       "CENTRAL", "M_EntidadFinanciera.AD_Org_ID")],
    "Alta de M_EntidadFinanciera (requiere además una cuenta de cupones, C_BankAccount_ID).", 300, color=FINANZAS)

planes = H(
    "pla", "Planes de Tarjetas", "Los planes de cuotas que ofrece con cada tarjeta.",
    [R("comercio", "Código de tarjeta/comercio", ("com", "codigo"), True,
       "Pestaña Tarjetas y Comercios.", "VISA-CENTRAL", "M_EntidadFinancieraPlan.M_EntidadFinanciera_ID"),
     T("codigo", "Código de plan", True, "Número o código del plan. No repetir dentro de la misma tarjeta.", "3C",
       "M_EntidadFinancieraPlan.Value", 60),
     T("nombre", "Nombre del plan", True, "Nombre para elegirlo al vender.", "3 cuotas sin interés",
       "M_EntidadFinancieraPlan.Name", 60, ancho=30),
     C("cuotas_cliente", "Cuotas que paga el cliente", "entero", True, "Cantidad de cuotas del cliente. Un pago: 1.",
       3, "M_EntidadFinancieraPlan.CuotasPago"),
     C("cuotas_cobro", "Cuotas en que usted cobra", "entero", True,
       "En cuántos pagos le acreditan a usted. Si es todo junto: 1.", 1, "M_EntidadFinancieraPlan.CuotasCobro"),
     C("recargo", "Recargo o descuento (%)", "cantidad", False,
       "Recargo en positivo (10), descuento en negativo (-5). Sin cambios: vacío.", 0,
       "M_EntidadFinancieraPlan.M_DiscountSchema_ID", negativo=True),
     C("dias", "Días para acreditación", "entero", True, "Cuántos días tarda en acreditarse en su cuenta.", 18,
       "M_EntidadFinancieraPlan.AccreditationDays"),
     C("desde", "Vigente desde", "fecha", True, "Fecha de inicio (día/mes/año).", 46023,
       "M_EntidadFinancieraPlan.DateFrom"),
     C("hasta", "Vigente hasta", "fecha", True, "Fecha de fin. Si no vence, escriba 31/12/2099.", 73050,
       "M_EntidadFinancieraPlan.DateTo")],
    "Alta de M_EntidadFinancieraPlan; el recargo/descuento requiere un esquema de descuento.", 2000,
    clave=("comercio", "codigo"),
    reglas=[('AND(ISNUMBER({desde}),ISNUMBER({hasta}),N({hasta})<N({desde}))',
             "La fecha hasta es anterior a la fecha desde")], color=FINANZAS)

stock_param = H(
    "stp", "Stock", "Dónde se ubica cada artículo y cuánto conviene tener. Las cantidades actuales van en la planilla de Saldos.",
    [R("deposito", "Código de depósito", ("dep", "codigo"), True, "Pestaña Depósitos.", "DEP01", "M_Replenish.M_Warehouse_ID"),
     R("articulo", "Código de artículo", ("art", "codigo"), True, "Pestaña Artículos.", "ART0001", "M_Replenish.M_Product_ID"),
     T("ubicacion", "Ubicación", False, "Pasillo, estante, sector.", "P2-E4", "M_Locator.Value", 40),
     C("minimo", "Stock mínimo", "cantidad", False, "Cantidad por debajo de la cual hay que reponer.", 5, "M_Replenish.Level_Min"),
     C("maximo", "Stock máximo", "cantidad", False, "Cantidad máxima que conviene tener.", 50, "M_Replenish.Level_Max")],
    "Alta de M_Locator y M_Replenish.", 10000, clave=("deposito", "articulo"),
    reglas=[('AND(ISNUMBER({minimo}),ISNUMBER({maximo}),N({maximo})<N({minimo}))',
             "El máximo es menor que el mínimo")], color=PRODUCTO)

# ---------------------------------------------------------------- SALDOS


def fecha_corte():
    return C("fecha", "Fecha de corte", "fecha", True,
             "Día al que corresponde el saldo. Debe ser la misma en toda la planilla.", 46387, "Fecha del movimiento / asiento inicial")


def cod(id, titulo, ayuda, ej, dest, oblig=True):
    return T(id, titulo, oblig, ayuda, ej, dest, 40)


COT = ('AND({moneda}<>"",{moneda}<>"Pesos")', "Sí, si la moneda no es Pesos")


def cotizacion():
    return C("cotizacion", "Cotización", "cantidad", COT,
             "Cuántos pesos vale 1 unidad de la moneda extranjera. Vacío si es en Pesos.", None, "C_Conversion_Rate")


caja = H(
    "scaja", "Caja Efectivo", "El efectivo contado en cada caja a la fecha de corte.",
    [fecha_corte(),
     cod("caja", "Código de caja", "El mismo código de la pestaña Libros de Caja (planilla de Maestros).", "CAJA01", "C_Cash.C_CashBook_ID"),
     L("moneda", "Moneda", "moneda", True, "Moneda del efectivo.", "Pesos", "C_CashLine.C_Currency_ID"),
     C("importe", "Importe en efectivo", "importe", True, "Total contado. No incluya cheques ni cupones.", 250000, "C_CashLine.Amount"),
     cotizacion(),
     T("obs", "Observaciones", False, "Aclaraciones.", None, "C_CashLine.Description")],
    "Saldo inicial de cada Libro de Caja.", 200, clave=("caja", "moneda"), color=FINANZAS)

bancos = H(
    "sban", "Bancos", "El saldo de cada cuenta bancaria a la fecha de corte.",
    [fecha_corte(),
     cod("cuenta", "Código de cuenta", "El mismo código de la pestaña Cuentas Bancarias (planilla de Maestros).",
         "NACION-CC", "C_BankStatement.C_BankAccount_ID"),
     C("saldo_banco", "Saldo según el banco", "importe", True,
       "El saldo del resumen bancario. Si está en descubierto, con signo menos.", 1500000,
       "C_BankStatement (saldo extracto)", negativo=True),
     C("saldo_sistema", "Saldo según su contabilidad", "importe", True,
       "El saldo de su sistema actual. Puede diferir del banco por cheques todavía no debitados.", 1320000,
       "Saldo contable inicial", negativo=True),
     L("moneda", "Moneda", "moneda", True, "Moneda de la cuenta.", "Pesos", "C_Currency_ID"),
     cotizacion(),
     T("obs", "Observaciones", False, "Detalle de la diferencia entre ambos saldos.", "Cheque 4455 sin debitar", "Description")],
    "Saldo inicial por cuenta bancaria. La diferencia banco/sistema son partidas pendientes de conciliar.",
    200, clave=("cuenta",), color=FINANZAS)

stock_saldo = H(
    "sstk", "Stock", "La cantidad de cada artículo que hay en cada depósito a la fecha de corte.",
    [fecha_corte(),
     cod("deposito", "Código de depósito", "Como en la pestaña Depósitos (Maestros).", "DEP01", "I_Inventory.WarehouseValue"),
     cod("articulo", "Código de artículo", "Como en la pestaña Artículos (Maestros).", "ART0001", "I_Inventory.Value"),
     C("cantidad", "Cantidad", "cantidad", True, "Cantidad contada. No cargue artículos con cantidad 0.", 24, "I_Inventory.QtyCount"),
     C("costo", "Costo unitario sin IVA", "importe", True, "Cuánto le costó cada unidad, sin IVA.", 4500, "Costo inicial (M_Product_Costing / lista de costo)"),
     L("moneda", "Moneda del costo", "moneda", True, "Moneda del costo.", "Pesos", "C_Currency_ID"),
     cotizacion(),
     T("lote", "Lote", False, "Solo si controla lotes.", None, "I_Inventory.Lot", 20),
     T("serie", "Número de serie", False, "Solo si controla series: una fila por cada unidad.", None, "I_Inventory.SerNo", 20),
     C("vence", "Fecha de vencimiento", "fecha", False, "Solo si el artículo vence.", None, "M_AttributeSetInstance.GuaranteeDate")],
    "ImportInventory (I_Inventory). El costo se carga aparte.", 15000,
    clave=("deposito", "articulo", "lote", "serie"), color=PRODUCTO)


def cuenta_corriente(id, nombre, quien, ejcod, venta):
    return H(
        id, nombre,
        f"Una fila por cada comprobante que {'sus clientes le deben' if venta else 'usted debe a sus proveedores'} "
        "(total o parcialmente) a la fecha de corte.",
        [cod("entidad", f"Código de {quien}", f"Como en la pestaña {quien.capitalize()}es (Maestros)." if quien == "proveedor"
             else "Como en la pestaña Clientes (Maestros).", ejcod, "I_Invoice.BPartnerValue"),
         L("tipo", "Tipo de comprobante", "comprobante_cc", True,
           "Notas de Crédito, anticipos y pagos a cuenta son saldos A FAVOR: escríbalos igual en positivo.",
           "Factura", "I_Invoice.DocTypeName (define el signo)", ancho=26),
         L("letra", "Letra", "letra", True, "Letra del comprobante.", "A", "Tipo de documento"),
         C("pv", "Punto de venta", "entero", True, "En 0003-00001234 es 3. Si no tiene, escriba 0.", 3, "I_Invoice.DocumentNo (parte 1)"),
         C("numero", "Número de comprobante", "entero", True, "En 0003-00001234 es 1234.", 1234, "I_Invoice.DocumentNo (parte 2)"),
         C("emision", "Fecha del comprobante", "fecha", True, "Fecha de emisión.", 46350, "I_Invoice.DateInvoiced"),
         C("vto", "Fecha de vencimiento", "fecha", True,
           "Cuándo vence. Si tiene varias cuotas, una fila por cuota.", 46380, "C_InvoicePaySchedule.DueDate"),
         C("cuota", "Número de cuota", "entero", False, "Solo si el comprobante tiene cuotas: 1, 2, 3…", None, "C_InvoicePaySchedule"),
         L("moneda", "Moneda", "moneda", True, "Moneda del comprobante.", "Pesos", "I_Invoice.C_Currency_ID"),
         cotizacion(),
         C("total", "Importe original", "importe", True, "Total del comprobante (o de la cuota) con impuestos.", 121000, "Informativo"),
         C("saldo", "Saldo pendiente", "importe", True, "Lo que falta " + ("cobrar" if venta else "pagar") + " de este comprobante.",
           71000, "I_Invoice.PriceActual (comprobante de saldo inicial)"),
         cod("sucursal", "Código de sucursal", "Como en la pestaña Sucursales (Maestros).", "CENTRAL", "I_Invoice.OrgValue"),
         T("obs", "Observaciones", False, "Aclaraciones.", None, "I_Invoice.Description")],
        f"ImportInvoice (I_Invoice) con IsSOTrx={'Y' if venta else 'N'}, usando tipos de documento de saldo inicial.",
        10000, clave=("entidad", "tipo", "letra", "pv", "numero", "cuota"),
        reglas=[('AND(ISNUMBER({total}),ISNUMBER({saldo}),N({saldo})>N({total}))',
                 "El saldo pendiente es mayor que el importe original"),
                ('AND(ISNUMBER({saldo}),N({saldo})=0)', "Saldo 0: no cargue comprobantes ya cancelados"),
                ('AND(ISNUMBER({emision}),ISNUMBER({vto}),N({vto})<N({emision}))',
                 "El vencimiento es anterior a la emisión")],
        color=PERSONAS)


cc_cli = cuenta_corriente("scli", "Cta Cte Clientes", "cliente", "C0001", True)
cc_pro = cuenta_corriente("spro", "Cta Cte Proveedores", "proveedor", "P0001", False)

cheques = H(
    "schq", "Cheques en Cartera",
    "Cheques de terceros que tiene en su poder a la fecha de corte, sin depositar ni entregar.",
    [T("banco", "Banco emisor", True, "Banco del cheque.", "Banco Galicia", "C_Payment.A_Bank", 60, ancho=28),
     T("numero", "Número de cheque", True, "Número impreso en el cheque.", "00012345", "C_Payment.CheckNo", 20),
     L("tipo", "Tipo", "tipo_cheque", True, "Físico o electrónico.", "Físico", "Medio de cobro"),
     C("emision", "Fecha de emisión", "fecha", True, "Fecha en que se firmó.", 46350, "C_Payment.DateEmissionCheck"),
     C("pago", "Fecha de pago", "fecha", True, "Fecha desde la que se puede cobrar.", 46400, "C_Payment.DueDate"),
     C("importe", "Importe", "importe", True, "Importe del cheque.", 180000, "C_Payment.PayAmt"),
     L("moneda", "Moneda", "moneda", True, "Moneda del cheque.", "Pesos", "C_Payment.C_Currency_ID"),
     cotizacion(),
     T("librador", "Nombre del firmante", True, "Quién firmó el cheque (librador).", "Ferretería El Tornillo S.R.L.",
       "C_Payment.A_Name", 60, ancho=32),
     T("cuit", "CUIT del firmante", True, "11 números sin guiones.", "30712345671", "C_Payment.A_CUIT", 11),
     cod("cliente", "Código de cliente", "Cliente que se lo entregó (pestaña Clientes, Maestros).", "C0001", "C_Payment.C_BPartner_ID"),
     cod("sucursal", "Código de sucursal", "Sucursal donde está guardado.", "CENTRAL", "C_Payment.AD_Org_ID"),
     T("obs", "Observaciones", False, "Aclaraciones.", None, "C_Payment.Description")],
    "Cobros con cheque (C_Payment TenderType=K) sobre la cuenta «Cheques en cartera». "
    "Definir contrapartida para no duplicar el saldo de cuenta corriente.", 3000,
    clave=("banco", "numero", "cuit"),
    reglas=[('AND({cuit}<>"",OR(LEN({cuit})<>11,NOT(ISNUMBER(-{cuit}))))', "El CUIT debe tener 11 números sin guiones"),
            ('AND(ISNUMBER({emision}),ISNUMBER({pago}),N({pago})<N({emision}))',
             "La fecha de pago es anterior a la de emisión")], color=FINANZAS)

# ---------------------------------------------------------------- LIBROS
PASOS_COMUNES = [
    ("Complete las pestañas de izquierda a derecha, en el orden de la tabla de abajo. Algunas necesitan datos "
     "cargados en las anteriores.", 30),
    ("En cada pestaña: la fila 2 es el nombre del dato, la fila 3 indica si es obligatorio, la fila 4 explica qué "
     "escribir y la fila 5 muestra un EJEMPLO. No modifique ni borre esas filas.", 30),
    ("Escriba sus datos desde la fila 6 hacia abajo, una fila por cada elemento. No deje filas vacías en el medio.", 20),
    ("Si la celda muestra una flecha, elija una opción de la lista. Al hacer clic en una celda aparece una ayuda.", 20),
    ("Mire la columna A (Revisión automática). Si dice «✔ Correcto» la fila está bien; si dice «⚠», lea el mensaje "
     "y corrija. Nunca escriba en esa columna.", 30),
    ("Si copia y pega desde otro sistema, pegue solo VALORES (clic derecho > Pegado especial > Valores) a partir "
     "de la columna B.", 30),
    ("No agregue, quite, mueva ni cambie el nombre de columnas o pestañas. Si le falta lugar para un dato, use "
     "Observaciones o consulte.", 30),
    ("Guarde el archivo en formato Excel (.xlsx).", 18),
]

MAESTROS = dict(
    archivo="Libertya_Migracion_1_Maestros.xlsx",
    titulo="Migración a Libertya — 1. Datos Maestros",
    intro="En este archivo se cargan los datos básicos de su empresa: sucursales, artículos, clientes, proveedores, "
          "empleados, bancos y tarjetas. Complételo ANTES que la planilla de Saldos, porque los saldos usan estos códigos.",
    pasos=PASOS_COMUNES + [
        ("Los CÓDIGOS son la clave para unir la información. Use los de su sistema actual y escríbalos siempre "
         "exactamente igual.", 30),
        ("Las pestañas de color mostaza (Sucursales, Depósitos, Puntos de Venta y Libros de Caja) conviene "
         "completarlas junto con la persona que lo asiste en la implementación.", 30),
    ],
    hojas=[sucursales, depositos, puntos_venta, libros_caja, lineas, familias, subfamilias, marcas, grupos,
           empleados, clientes, proveedores, articulos, cuentas, comercios, planes, stock_param],
)

SALDOS = dict(
    archivo="Libertya_Migracion_2_Saldos.xlsx",
    titulo="Migración a Libertya — 2. Saldos para la Puesta en Marcha",
    intro="En este archivo se carga la «foto» de su empresa a la FECHA DE CORTE (el día en que deja de usar el sistema "
          "anterior): efectivo, bancos, mercadería, deudas y cheques.",
    pasos=PASOS_COMUNES + [
        ("Antes de empezar, acuerde la FECHA DE CORTE. Todos los saldos deben ser a ese mismo día.", 20),
        ("Los códigos (cliente, proveedor, artículo, depósito, caja, cuenta, sucursal) deben ser EXACTAMENTE los de "
         "la planilla de Maestros. Esta planilla no puede comprobarlo: revíselo con cuidado.", 30),
        ("Al terminar, complete la pestaña «Control de Totales» para comparar con los reportes de su sistema actual.", 20),
    ],
    hojas=[caja, bancos, stock_saldo, cc_cli, cc_pro, cheques],
    totales=[
        ("Efectivo en cajas", "scaja", "SUMIFS({importe},{moneda},{MON})", LISTAS["moneda"]),
        ("Saldo de bancos (según banco)", "sban", "SUMIFS({saldo_banco},{moneda},{MON})", LISTAS["moneda"]),
        ("Saldo de bancos (según contabilidad)", "sban", "SUMIFS({saldo_sistema},{moneda},{MON})", LISTAS["moneda"]),
        ("Stock: cantidad total de unidades", "sstk", "SUM({cantidad})", [""]),
        ("Stock: valor total a costo", "sstk", "SUMPRODUCT((" + "{moneda}={MON})*1,{cantidad},{costo})", LISTAS["moneda"]),
    ] + [
        (f"{nombre}: saldo neto", hid,
         'SUMIFS({saldo},{moneda},{MON})-2*(SUMIFS({saldo},{moneda},{MON},{tipo},"Nota de Crédito")'
         '+SUMIFS({saldo},{moneda},{MON},{tipo},"Recibo o pago a cuenta")'
         '+SUMIFS({saldo},{moneda},{MON},{tipo},"Anticipo"))', LISTAS["moneda"])
        for nombre, hid in (("Cuentas corrientes clientes", "scli"), ("Cuentas corrientes proveedores", "spro"))
    ] + [("Cheques en cartera", "schq", "SUMIFS({importe},{moneda},{MON})", LISTAS["moneda"])],
)

LIBROS = [MAESTROS, SALDOS]
