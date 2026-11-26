#!/usr/bin/env python3
"""Genera las planillas de migración de datos a Libertya (OP#8600).

Uso: python3 generar.py [carpeta_salida]
Las columnas se definen en especificacion.py.
"""
import os
import sys

from openpyxl import Workbook
from openpyxl.comments import Comment
from openpyxl.formatting.rule import FormulaRule
from openpyxl.styles import Alignment, Border, Font, PatternFill, Side
from openpyxl.utils import get_column_letter
from openpyxl.worksheet.datavalidation import DataValidation

from especificacion import LISTAS, LIBROS

PRIMERA = 6  # primera fila de datos (1 título, 2 encabezado, 3 oblig., 4 ayuda, 5 ejemplo)
FUENTE = "Arial"

AZUL = "17365D"
GRIS_AZUL = "5B7C99"


def fill(c):
    return PatternFill("solid", fgColor=c)


def font(**kw):
    return Font(name=FUENTE, **kw)


BORDE = Border(*(Side(style="thin", color="BFBFBF"),) * 4)
WRAP = Alignment(wrap_text=True, vertical="top")
CENTRO = Alignment(wrap_text=True, vertical="center", horizontal="center")

FORMATOS = {"texto": "@", "lista": "@", "ref": "@", "fecha": "dd/mm/yyyy",
            "importe": "#,##0.00", "cantidad": "#,##0.####", "entero": "0"}


def q(nombre):
    return "'" + nombre + "'"


def rango_lista(nombre):
    col = get_column_letter(list(LISTAS).index(nombre) + 1)
    return f"Listas!${col}$2:${col}${len(LISTAS[nombre]) + 1}"


class Hoja:
    def __init__(self, spec, hojas):
        self.spec = spec
        self.nombre = spec["nombre"]
        self.cols = spec["columnas"]
        self.ultima = PRIMERA + spec["filas"] - 1
        self.hojas = hojas
        # La columna A es la revisión automática; los datos empiezan en B.
        self.letra = {c["id"]: get_column_letter(i + 2) for i, c in enumerate(self.cols)}
        self.fin = get_column_letter(len(self.cols) + 1)

    def rango(self, cid):
        l = self.letra[cid]
        return f"${l}${PRIMERA}:${l}${self.ultima}"


def requerido(h, c, r):
    """Condición Excel bajo la cual la columna es obligatoria en la fila r (o None)."""
    if c["oblig"] is True:
        return "TRUE"
    if c["oblig"]:
        cond = c["oblig"][0]
        for cid, l in h.letra.items():
            cond = cond.replace("{" + cid + "}", f"${l}{r}")
        return cond
    return None


def controles(h, r):
    """Fragmentos de fórmula que devuelven el texto de cada problema de la fila."""
    partes = []
    for c in h.cols:
        x = f"{h.letra[c['id']]}{r}"
        t = c["titulo"]
        req = requerido(h, c, r)
        if req == "TRUE":
            partes.append(f'IF({x}="","Falta {t}. ","")')
        elif req:
            partes.append(f'IF(AND({req},{x}=""),"Falta {t}. ","")')
        tipo = c["tipo"]
        if tipo == "fecha":
            partes.append(f'IF({x}="","",IF(ISNUMBER({x}),"","{t}: escriba una fecha dd/mm/aaaa. "))')
        elif tipo in ("importe", "cantidad", "entero"):
            partes.append(f'IF({x}="","",IF(ISNUMBER({x}),'
                          + (f'IF({x}<0,"{t}: no puede ser negativo. ",""),' if not c.get("negativo") else '"",')
                          + f'"{t}: escriba solo un número. "))')
        elif tipo == "lista":
            partes.append(f'IF({x}="","",IF(ISNA(MATCH({x},{rango_lista(c["lista"])},0)),'
                          f'"{t}: elija una opción de la lista. ",""))')
        elif tipo == "ref":
            dest = h.hojas[c["ref"][0]]
            partes.append(f'IF({x}="","",IF(ISNA(MATCH({x},{q(dest.nombre)}!{dest.rango(c["ref"][1])},0)),'
                          f'"{t}: no existe en la pestaña {dest.nombre}. ",""))')
        if c.get("largo") and tipo in ("texto", "ref"):
            partes.append(f'IF(LEN({x})>{c["largo"]},"{t}: máximo {c["largo"]} caracteres. ","")')
    for cond, msg in h.spec.get("reglas", []):
        for cid, l in h.letra.items():
            cond = cond.replace("{" + cid + "}", f"{l}{r}")
        partes.append(f'IF({cond},"{msg}. ","")')
    clave = h.spec.get("clave")
    if clave:
        pares = ",".join(f'{h.rango(k)},{h.letra[k]}{r}&""' for k in clave)
        primero = f"{h.letra[clave[0]]}{r}"
        titulos = " + ".join(c["titulo"] for c in h.cols if c["id"] in clave)
        partes.append(f'IF({primero}="","",IF(COUNTIFS({pares})>1,"Repetido: {titulos}. ",""))')
    return partes


def validacion(h, c):
    tipo = c["tipo"]
    if tipo == "lista":
        dv = DataValidation(type="list", formula1=rango_lista(c["lista"]))
        error = "Elija una opción de la lista desplegable (flecha a la derecha de la celda)."
    elif tipo == "ref":
        dest = h.hojas[c["ref"][0]]
        dv = DataValidation(type="list", formula1=f"{q(dest.nombre)}!{dest.rango(c['ref'][1])}")
        error = f"Primero cargue este dato en la pestaña {dest.nombre} y después elíjalo de la lista."
    elif tipo == "fecha":
        dv = DataValidation(type="date", operator="between", formula1="1", formula2="73051")
        error = "Escriba una fecha válida con formato día/mes/año. Ejemplo: 31/12/2026"
    elif tipo in ("importe", "cantidad", "entero"):
        op = {} if c.get("negativo") else {"operator": "greaterThanOrEqual", "formula1": "0"}
        if c.get("negativo"):
            op = {"operator": "between", "formula1": "-999999999999", "formula2": "999999999999"}
        dv = DataValidation(type="whole" if tipo == "entero" else "decimal", **op)
        error = "Escriba solamente un número, sin letras ni el signo $."
    else:
        dv = DataValidation(type="textLength", operator="lessThanOrEqual", formula1=str(c.get("largo", 255)))
        error = f"El texto es demasiado largo. Máximo {c.get('largo', 255)} caracteres."
    dv.allow_blank = True
    dv.showDropDown = False
    dv.showInputMessage = True
    dv.showErrorMessage = True
    dv.promptTitle = c["titulo"][:32]
    marca = "OBLIGATORIO. " if c["oblig"] is True else ""
    dv.prompt = (marca + c["ayuda"])[:255]
    dv.errorTitle = "Dato no válido"
    dv.error = error[:225]
    return dv


def hoja_datos(wb, h):
    ws = wb.create_sheet(h.nombre)
    ws.sheet_properties.tabColor = h.spec.get("color", AZUL)
    ws["A1"] = f"{h.nombre.upper()}  —  {h.spec['para_que']}  Escriba sus datos a partir de la fila {PRIMERA}."
    ws["A1"].font = font(bold=True, size=12, color=AZUL)
    ws["A1"].alignment = Alignment(vertical="center")
    ws.row_dimensions[1].height = 24

    rotulos = {2: "Revisión automática\n(no escribir aquí)", 3: "¿Es obligatorio?", 4: "Qué escribir",
               5: "EJEMPLO\n(no se migra)"}
    for fila, texto in rotulos.items():
        cel = ws.cell(fila, 1, texto)
        cel.font = font(bold=True, size=9)
        cel.fill = fill("D9D9D9")
        cel.alignment = CENTRO
        cel.border = BORDE
    ws.column_dimensions["A"].width = 46

    for i, c in enumerate(h.cols, start=2):
        l = get_column_letter(i)
        ancho = c.get("ancho", max(18, min(34, len(c["titulo"]) + 6)))
        dim = ws.column_dimensions[l]
        dim.width = ancho
        dim.number_format = FORMATOS[c["tipo"]]
        dim.font = font(size=10)

        oblig = c["oblig"]
        if oblig is True:
            color, etiqueta, fondo = AZUL, "SÍ, obligatorio", "FCE4D6"
        elif oblig:
            color, etiqueta, fondo = GRIS_AZUL, oblig[1], "FFF2CC"
        else:
            color, etiqueta, fondo = "808080", "No (opcional)", "F2F2F2"
        enc = ws.cell(2, i, c["titulo"] + (" *" if oblig is True else ""))
        enc.font = font(bold=True, color="FFFFFF", size=10)
        enc.fill = fill(color)
        enc.alignment = CENTRO
        ob = ws.cell(3, i, etiqueta)
        ob.font = font(bold=oblig is True, size=9)
        ob.fill = fill(fondo)
        ob.alignment = CENTRO
        ay = ws.cell(4, i, c["ayuda"])
        ay.font = font(size=9)
        ay.alignment = WRAP
        ej = ws.cell(5, i, c.get("ej"))
        ej.font = font(italic=True, size=10, color="7F7F7F")
        ej.fill = fill("EDEDED")
        ej.number_format = FORMATOS[c["tipo"]]
        ej.alignment = Alignment(vertical="center")
        for cel in (enc, ob, ay, ej):
            cel.border = BORDE

        dv = validacion(h, c)
        dv.add(f"{l}{PRIMERA}:{l}{h.ultima}")
        ws.add_data_validation(dv)

        req = requerido(h, c, PRIMERA)
        if req:
            ws.conditional_formatting.add(
                f"{l}{PRIMERA}:{l}{h.ultima}",
                FormulaRule(formula=[f'AND(COUNTA($B{PRIMERA}:${h.fin}{PRIMERA})>0,{req},{l}{PRIMERA}="")'],
                            fill=PatternFill("solid", bgColor="F4B183")))

    largo_ayuda = max(len(c["ayuda"]) / (c.get("ancho", max(18, min(34, len(c["titulo"]) + 6)))) for c in h.cols)
    ws.row_dimensions[2].height = 32
    ws.row_dimensions[3].height = 28
    ws.row_dimensions[4].height = min(150, max(48, 13 * (int(largo_ayuda * 1.15) + 1)))
    ws.row_dimensions[5].height = 28

    fa = font(size=9, bold=True)
    for r in range(PRIMERA, h.ultima + 1):
        concat = "&".join(controles(h, r))
        f = (f'=IF(COUNTA(B{r}:{h.fin}{r})=0,"",SUBSTITUTE(SUBSTITUTE('
             f'"⚠ "&{concat}&"~","⚠ ~","✔ Correcto"),"~",""))')
        if len(f) > 8192:
            raise ValueError(f"Fórmula demasiado larga en {h.nombre}: {len(f)}")
        cel = ws.cell(r, 1, f)
        cel.font = fa
        cel.alignment = WRAP
    zona = f"A{PRIMERA}:A{h.ultima}"
    ws.conditional_formatting.add(zona, FormulaRule(
        formula=[f'LEFT(A{PRIMERA},1)="✔"'], fill=PatternFill("solid", bgColor="C6EFCE"),
        font=Font(color="006100")))
    ws.conditional_formatting.add(zona, FormulaRule(
        formula=[f'LEFT(A{PRIMERA},1)="⚠"'], fill=PatternFill("solid", bgColor="FFC7CE"),
        font=Font(color="9C0006")))
    ws.freeze_panes = f"C{PRIMERA}"
    ws.sheet_view.zoomScale = 100


def texto_hoja(ws, fila, texto, **kw):
    ws.merge_cells(start_row=fila, start_column=1, end_row=fila, end_column=5)
    c = ws.cell(fila, 1, texto)
    c.font = font(**kw)
    c.alignment = WRAP
    return c


def tabla(ws, fila, encabezados):
    for i, t in enumerate(encabezados, 1):
        c = ws.cell(fila, i, t)
        c.font = font(bold=True, color="FFFFFF")
        c.fill = fill(AZUL)
        c.alignment = CENTRO
        c.border = BORDE


def celda(ws, fila, col, valor, **kw):
    c = ws.cell(fila, col, valor)
    c.font = font(**kw)
    c.alignment = WRAP
    c.border = BORDE
    return c


def hoja_inicio(wb, libro, hojas):
    ws = wb.active
    ws.title = "LEER PRIMERO"
    ws.sheet_properties.tabColor = "C00000"
    for l, w in zip("ABCDE", (8, 36, 62, 16, 20)):
        ws.column_dimensions[l].width = w
    ws.sheet_view.showGridLines = False
    f = 1
    texto_hoja(ws, f, libro["titulo"], bold=True, size=18, color=AZUL)
    ws.row_dimensions[f].height = 30
    f += 1
    texto_hoja(ws, f, libro["intro"], size=11)
    ws.row_dimensions[f].height = 48
    f += 2

    texto_hoja(ws, f, "Cómo completar esta planilla", bold=True, size=13, color=AZUL)
    f += 1
    for i, (paso, alto) in enumerate(libro["pasos"], 1):
        celda(ws, f, 1, i, bold=True)
        ws.merge_cells(start_row=f, start_column=2, end_row=f, end_column=5)
        celda(ws, f, 2, paso)
        ws.row_dimensions[f].height = alto
        f += 1
    f += 1

    texto_hoja(ws, f, "Qué significan los colores", bold=True, size=13, color=AZUL)
    f += 1
    colores = [
        (AZUL, "Encabezado azul oscuro y con *", "Dato OBLIGATORIO. Si falta, la fila no se puede migrar."),
        (GRIS_AZUL, "Encabezado azul claro", "Obligatorio solo en algunos casos. La fila «¿Es obligatorio?» explica cuándo."),
        ("808080", "Encabezado gris", "Dato opcional. Complételo si lo tiene."),
        ("F4B183", "Celda naranja", "Falta un dato obligatorio en esa fila."),
        ("C6EFCE", "Revisión en verde (✔)", "La fila está completa."),
        ("FFC7CE", "Revisión en rojo (⚠)", "Hay algo para corregir. El texto dice qué es."),
    ]
    for color, nombre, desc in colores:
        celda(ws, f, 1, None).fill = fill(color)
        celda(ws, f, 2, nombre, bold=True)
        ws.merge_cells(start_row=f, start_column=3, end_row=f, end_column=5)
        celda(ws, f, 3, desc)
        ws.row_dimensions[f].height = 20
        f += 1
    f += 1

    texto_hoja(ws, f, "Pestañas a completar, en este orden", bold=True, size=13, color=AZUL)
    f += 1
    tabla(ws, f, ["Paso", "Pestaña (clic para ir)", "Para qué sirve", "Filas con datos", "Filas a corregir"])
    f += 1
    inicio = f
    for i, spec in enumerate(libro["hojas"], 1):
        h = hojas[spec["id"]]
        zona = f"{q(h.nombre)}!$A${PRIMERA}:$A${h.ultima}"
        celda(ws, f, 1, i)
        c = celda(ws, f, 2, h.nombre, color="0563C1", underline="single")
        c.hyperlink = f"#{q(h.nombre)}!B{PRIMERA}"
        celda(ws, f, 3, spec["para_que"])
        celda(ws, f, 4, f'=COUNTIF({zona},"✔*")+COUNTIF({zona},"⚠*")')
        celda(ws, f, 5, f'=COUNTIF({zona},"⚠*")', bold=True)
        ws.row_dimensions[f].height = max(28, 14 * (len(spec["para_que"]) // 62 + 1))
        f += 1
    ws.conditional_formatting.add(f"E{inicio}:E{f - 1}", FormulaRule(
        formula=[f"E{inicio}>0"], fill=PatternFill("solid", bgColor="FFC7CE")))
    ws.conditional_formatting.add(f"E{inicio}:E{f - 1}", FormulaRule(
        formula=[f"AND(D{inicio}>0,E{inicio}=0)"], fill=PatternFill("solid", bgColor="C6EFCE")))
    celda(ws, f, 1, None)
    celda(ws, f, 2, "TOTAL", bold=True)
    celda(ws, f, 3, None)
    celda(ws, f, 4, f"=SUM(D{inicio}:D{f - 1})", bold=True)
    celda(ws, f, 5, f"=SUM(E{inicio}:E{f - 1})", bold=True)
    f += 1
    texto_hoja(ws, f, "Antes de entregar el archivo, la columna «Filas a corregir» debe estar en 0.", bold=True, color="C00000")

    ws.page_setup.fitToWidth = 1
    ws.page_setup.fitToHeight = 0
    ws.sheet_properties.pageSetUpPr.fitToPage = True


def hoja_totales(wb, libro, hojas):
    ws = wb.create_sheet("Control de Totales", 1)
    ws.sheet_properties.tabColor = "548235"
    ws.sheet_view.showGridLines = False
    for l, w in zip("ABCDEF", (34, 20, 22, 24, 20, 30)):
        ws.column_dimensions[l].width = w
    ws["A1"] = "CONTROL DE TOTALES"
    ws["A1"].font = font(bold=True, size=16, color=AZUL)
    ws.merge_cells("A2:F2")
    ws["A2"] = ("Compare lo cargado con los reportes de su sistema actual a la fecha de corte. Escriba en las celdas "
                "AMARILLAS el total que muestra su sistema. La diferencia debe quedar en 0.")
    ws["A2"].font = font()
    ws["A2"].alignment = WRAP
    ws.row_dimensions[2].height = 34
    tabla(ws, 4, ["Concepto", "Moneda", "Total cargado en la planilla", "Total según su sistema actual",
                  "Diferencia", "Resultado"])
    f = 5
    for concepto, hid, formula, monedas in libro["totales"]:
        h = hojas[hid]
        for mon in monedas:
            expr = formula
            for cid in h.letra:
                expr = expr.replace("{" + cid + "}", f"{q(h.nombre)}!{h.rango(cid)}")
            expr = expr.replace("{MON}", f'"{mon}"')
            celda(ws, f, 1, concepto)
            celda(ws, f, 2, mon or "—")
            celda(ws, f, 3, "=" + expr).number_format = "#,##0.00"
            c = celda(ws, f, 4, None, color="0000FF")
            c.fill = fill("FFFF00")
            c.number_format = "#,##0.00"
            celda(ws, f, 5, f'=IF(D{f}="","",C{f}-D{f})').number_format = "#,##0.00;-#,##0.00;0.00"
            celda(ws, f, 6, f'=IF(D{f}="",IF(C{f}=0,"","Falta el total de su sistema"),'
                            f'IF(ROUND(E{f},2)=0,"✔ Coincide","⚠ Revisar diferencia"))', bold=True)
            f += 1
    zona = f"F5:F{f - 1}"
    ws.conditional_formatting.add(zona, FormulaRule(formula=['LEFT(F5,1)="✔"'], fill=PatternFill("solid", bgColor="C6EFCE")))
    ws.conditional_formatting.add(zona, FormulaRule(formula=['LEFT(F5,1)="⚠"'], fill=PatternFill("solid", bgColor="FFC7CE")))
    f += 1
    ws.cell(f, 1, "Fecha de corte de los saldos:").font = font(bold=True)
    c = ws.cell(f, 2)
    c.fill = fill("FFFF00")
    c.number_format = "dd/mm/yyyy"
    c.border = BORDE


def hoja_listas(wb):
    ws = wb.create_sheet("Listas")
    for i, (nombre, valores) in enumerate(LISTAS.items(), 1):
        ws.cell(1, i, nombre).font = font(bold=True)
        ws.column_dimensions[get_column_letter(i)].width = 30
        for j, v in enumerate(valores, 2):
            c = ws.cell(j, i, v)
            c.font = font()
            c.number_format = "@"
    ws.sheet_state = "hidden"


def generar(libro, salida):
    hojas = {}
    for spec in libro["hojas"]:
        hojas[spec["id"]] = Hoja(spec, hojas)
    wb = Workbook()
    wb._fonts[0] = font(size=10)  # fuente por defecto del libro
    hoja_inicio(wb, libro, hojas)
    for spec in libro["hojas"]:
        hoja_datos(wb, hojas[spec["id"]])
    if libro.get("totales"):
        hoja_totales(wb, libro, hojas)
    hoja_listas(wb)
    ruta = os.path.join(salida, libro["archivo"])
    wb.save(ruta)
    print(ruta)


def mapeo(salida):
    lineas = ["# Mapeo de planillas a Libertya (uso interno del equipo de implementación)", "",
              f"Los datos de cada pestaña empiezan en la fila {PRIMERA}; la columna A es la revisión automática "
              "y la fila 5 es un ejemplo: ninguna de las dos se migra.", "",
              "Los destinos son una propuesta basada en el esquema y los importadores actuales; "
              "validarlos al construir cada proceso de carga.", ""]
    for libro in LIBROS:
        lineas += [f"## {libro['archivo']}", ""]
        for spec in libro["hojas"]:
            lineas += [f"### {spec['nombre']}", "", spec["destino"], "",
                       "| Col. | Campo | Obligatorio | Destino |", "|---|---|---|---|"]
            for i, c in enumerate(spec["columnas"], 2):
                ob = "Sí" if c["oblig"] is True else ("Condicional" if c["oblig"] else "No")
                lineas.append(f"| {get_column_letter(i)} | {c['titulo']} | {ob} | {c['dest']} |")
            lineas.append("")
    with open(os.path.join(salida, "mapeo_libertya.md"), "w", encoding="utf-8") as fh:
        fh.write("\n".join(lineas))


if __name__ == "__main__":
    destino = sys.argv[1] if len(sys.argv) > 1 else os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    for libro in LIBROS:
        generar(libro, destino)
    mapeo(destino)
