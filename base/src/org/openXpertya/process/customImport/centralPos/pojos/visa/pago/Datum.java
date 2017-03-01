
package org.openXpertya.process.customImport.centralPos.pojos.visa.pago;

import org.openXpertya.process.customImport.centralPos.pojos.GenericDatum;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum extends GenericDatum {

	@SerializedName("id")
	@Expose
	private String id;
	@SerializedName("archivo_id")
	@Expose
	private String archivoId;
	@SerializedName("empresa")
	@Expose
	private String empresa;
	@SerializedName("fpres")
	@Expose
	private String fpres;
	@SerializedName("tipo_reg")
	@Expose
	private String tipoReg;
	@SerializedName("moneda")
	@Expose
	private String moneda;
	@SerializedName("num_com")
	@Expose
	private String numCom;
	@SerializedName("num_est")
	@Expose
	private String numEst;
	@SerializedName("nroliq")
	@Expose
	private String nroliq;
	@SerializedName("fpag")
	@Expose
	private String fpag;
	@SerializedName("tipoliq")
	@Expose
	private String tipoliq;
	@SerializedName("impbruto")
	@Expose
	private String impbruto;
	@SerializedName("signo_1")
	@Expose
	private String signo1;
	@SerializedName("impret")
	@Expose
	private String impret;
	@SerializedName("signo_2")
	@Expose
	private String signo2;
	@SerializedName("impneto")
	@Expose
	private String impneto;
	@SerializedName("signo_3")
	@Expose
	private String signo3;
	@SerializedName("retesp")
	@Expose
	private String retesp;
	@SerializedName("signo_4")
	@Expose
	private String signo4;
	@SerializedName("retiva_esp")
	@Expose
	private String retivaEsp;
	@SerializedName("signo_5")
	@Expose
	private String signo5;
	@SerializedName("percep_ba")
	@Expose
	private String percepBa;
	@SerializedName("signo_6")
	@Expose
	private String signo6;
	@SerializedName("retiva_d1")
	@Expose
	private String retivaD1;
	@SerializedName("signo_7")
	@Expose
	private String signo7;
	@SerializedName("retiva_d2")
	@Expose
	private String retivaD2;
	@SerializedName("signo_8")
	@Expose
	private String signo8;
	@SerializedName("cargo_pex")
	@Expose
	private String cargoPex;
	@SerializedName("signo_9")
	@Expose
	private String signo9;
	@SerializedName("retiva_pex1")
	@Expose
	private String retivaPex1;
	@SerializedName("signo_10")
	@Expose
	private String signo10;
	@SerializedName("retiva_pex2")
	@Expose
	private String retivaPex2;
	@SerializedName("signo_11")
	@Expose
	private String signo11;
	@SerializedName("costo_cuoemi")
	@Expose
	private String costoCuoemi;
	@SerializedName("signo_12")
	@Expose
	private String signo12;
	@SerializedName("retiva_cuo1")
	@Expose
	private String retivaCuo1;
	@SerializedName("signo_13")
	@Expose
	private String signo13;
	@SerializedName("retiva_cuo2")
	@Expose
	private String retivaCuo2;
	@SerializedName("signo_14")
	@Expose
	private String signo14;
	@SerializedName("imp_serv")
	@Expose
	private String impServ;
	@SerializedName("signo_15")
	@Expose
	private String signo15;
	@SerializedName("iva1_xlj")
	@Expose
	private String iva1Xlj;
	@SerializedName("signo_16")
	@Expose
	private String signo16;
	@SerializedName("iva2_xlj")
	@Expose
	private String iva2Xlj;
	@SerializedName("signo_17")
	@Expose
	private String signo17;
	@SerializedName("cargo_edc_e")
	@Expose
	private String cargoEdcE;
	@SerializedName("signo_18")
	@Expose
	private String signo18;
	@SerializedName("iva1_edc_e")
	@Expose
	private String iva1EdcE;
	@SerializedName("signo_19")
	@Expose
	private String signo19;
	@SerializedName("iva2_edc_e")
	@Expose
	private String iva2EdcE;
	@SerializedName("signo_20")
	@Expose
	private String signo20;
	@SerializedName("cargo_edc_b")
	@Expose
	private String cargoEdcB;
	@SerializedName("signo_21")
	@Expose
	private String signo21;
	@SerializedName("iva1_edc_b")
	@Expose
	private String iva1EdcB;
	@SerializedName("signo_22")
	@Expose
	private String signo22;
	@SerializedName("iva2_edc_b")
	@Expose
	private String iva2EdcB;
	@SerializedName("signo_23")
	@Expose
	private String signo23;
	@SerializedName("cargo_cit_e")
	@Expose
	private String cargoCitE;
	@SerializedName("signo_24")
	@Expose
	private String signo24;
	@SerializedName("iva1_cit_e")
	@Expose
	private String iva1CitE;
	@SerializedName("signo_25")
	@Expose
	private String signo25;
	@SerializedName("iva2_cit_e")
	@Expose
	private String iva2CitE;
	@SerializedName("signo_26")
	@Expose
	private String signo26;
	@SerializedName("cargo_cit_b")
	@Expose
	private String cargoCitB;
	@SerializedName("signo_27")
	@Expose
	private String signo27;
	@SerializedName("iva1_cit_b")
	@Expose
	private String iva1CitB;
	@SerializedName("signo_28")
	@Expose
	private String signo28;
	@SerializedName("iva2_cit_b")
	@Expose
	private String iva2CitB;
	@SerializedName("signo_29")
	@Expose
	private String signo29;
	@SerializedName("ret_iva")
	@Expose
	private String retIva;
	@SerializedName("signo_30")
	@Expose
	private String signo30;
	@SerializedName("ret_gcias")
	@Expose
	private String retGcias;
	@SerializedName("signo_31")
	@Expose
	private String signo31;
	@SerializedName("ret_ingbru")
	@Expose
	private String retIngbru;
	@SerializedName("signo_32")
	@Expose
	private String signo32;
	@SerializedName("aster")
	@Expose
	private String aster;
	@SerializedName("casacta")
	@Expose
	private String casacta;
	@SerializedName("tipcta")
	@Expose
	private String tipcta;
	@SerializedName("ctabco")
	@Expose
	private String ctabco;
	@SerializedName("cf_exento_iva")
	@Expose
	private String cfExentoIva;
	@SerializedName("signo_04_1")
	@Expose
	private String signo041;
	@SerializedName("ley_25063")
	@Expose
	private String ley25063;
	@SerializedName("signo_04_2")
	@Expose
	private String signo042;
	@SerializedName("ali_ingbru")
	@Expose
	private String aliIngbru;
	@SerializedName("dto_campania")
	@Expose
	private String dtoCampania;
	@SerializedName("signo_04_3")
	@Expose
	private String signo043;
	@SerializedName("iva1_dto_campania")
	@Expose
	private String iva1DtoCampania;
	@SerializedName("signo_04_4")
	@Expose
	private String signo044;
	@SerializedName("ret_ingbru2")
	@Expose
	private String retIngbru2;
	@SerializedName("signo_04_5")
	@Expose
	private String signo045;
	@SerializedName("ali_ingbru2")
	@Expose
	private String aliIngbru2;
	@SerializedName("tasa_pex")
	@Expose
	private String tasaPex;
	@SerializedName("cargo_x_liq")
	@Expose
	private String cargoXLiq;
	@SerializedName("signo_04_8")
	@Expose
	private String signo048;
	@SerializedName("iva1_cargo_x_liq")
	@Expose
	private String iva1CargoXLiq;
	@SerializedName("signo_04_9")
	@Expose
	private String signo049;
	@SerializedName("dealer")
	@Expose
	private String dealer;
	@SerializedName("imp_db_cr")
	@Expose
	private String impDbCr;
	@SerializedName("signo_04_10")
	@Expose
	private String signo0410;
	@SerializedName("cf_no_reduce_iva")
	@Expose
	private String cfNoReduceIva;
	@SerializedName("signo_04_11")
	@Expose
	private String signo0411;
	@SerializedName("percep_ib_agip")
	@Expose
	private String percepIbAgip;
	@SerializedName("signo_04_12")
	@Expose
	private String signo0412;
	@SerializedName("alic_percep_ib_agip")
	@Expose
	private String alicPercepIbAgip;
	@SerializedName("reten_ib_agip")
	@Expose
	private String retenIbAgip;
	@SerializedName("signo_04_13")
	@Expose
	private String signo0413;
	@SerializedName("alic_reten_ib_agip")
	@Expose
	private String alicRetenIbAgip;
	@SerializedName("subtot_retiva_rg3130")
	@Expose
	private String subtotRetivaRg3130;
	@SerializedName("signo_04_14")
	@Expose
	private String signo0414;
	@SerializedName("prov_ingbru")
	@Expose
	private String provIngbru;
	@SerializedName("adic_plancuo")
	@Expose
	private String adicPlancuo;
	@SerializedName("signo_04_15")
	@Expose
	private String signo0415;
	@SerializedName("iva1_ad_plancuo")
	@Expose
	private String iva1AdPlancuo;
	@SerializedName("signo_04_16")
	@Expose
	private String signo0416;
	@SerializedName("adic_opinter")
	@Expose
	private String adicOpinter;
	@SerializedName("signo_04_17")
	@Expose
	private String signo0417;
	@SerializedName("iva1_ad_opinter")
	@Expose
	private String iva1AdOpinter;
	@SerializedName("signo_04_18")
	@Expose
	private String signo0418;
	@SerializedName("adic_altacom")
	@Expose
	private String adicAltacom;
	@SerializedName("signo_04_19")
	@Expose
	private String signo0419;
	@SerializedName("iva1_ad_altacom")
	@Expose
	private String iva1AdAltacom;
	@SerializedName("signo_04_20")
	@Expose
	private String signo0420;
	@SerializedName("adic_cupmanu")
	@Expose
	private String adicCupmanu;
	@SerializedName("signo_04_21")
	@Expose
	private String signo0421;
	@SerializedName("iva1_ad_cupmanu")
	@Expose
	private String iva1AdCupmanu;
	@SerializedName("signo_04_22")
	@Expose
	private String signo0422;
	@SerializedName("adic_altacom_bco")
	@Expose
	private String adicAltacomBco;
	@SerializedName("signo_04_23")
	@Expose
	private String signo0423;
	@SerializedName("iva1_ad_altacom_bco")
	@Expose
	private String iva1AdAltacomBco;
	@SerializedName("signo_04_24")
	@Expose
	private String signo0424;
	@SerializedName("revisado")
	@Expose
	private String revisado;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getArchivoId() {
		return archivoId;
	}

	public void setArchivoId(String archivoId) {
		this.archivoId = archivoId;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public String getFpres() {
		return fpres;
	}

	public void setFpres(String fpres) {
		this.fpres = fpres;
	}

	public String getTipoReg() {
		return tipoReg;
	}

	public void setTipoReg(String tipoReg) {
		this.tipoReg = tipoReg;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getNumCom() {
		return numCom;
	}

	public void setNumCom(String numCom) {
		this.numCom = numCom;
	}

	public String getNumEst() {
		return numEst;
	}

	public void setNumEst(String numEst) {
		this.numEst = numEst;
	}

	public String getNroliq() {
		return nroliq;
	}

	public void setNroliq(String nroliq) {
		this.nroliq = nroliq;
	}

	public String getFpag() {
		return fpag;
	}

	public void setFpag(String fpag) {
		this.fpag = fpag;
	}

	public String getTipoliq() {
		return tipoliq;
	}

	public void setTipoliq(String tipoliq) {
		this.tipoliq = tipoliq;
	}

	public String getImpbruto() {
		return impbruto;
	}

	public void setImpbruto(String impbruto) {
		this.impbruto = impbruto;
	}

	public String getSigno1() {
		return signo1;
	}

	public void setSigno1(String signo1) {
		this.signo1 = signo1;
	}

	public String getImpret() {
		return impret;
	}

	public void setImpret(String impret) {
		this.impret = impret;
	}

	public String getSigno2() {
		return signo2;
	}

	public void setSigno2(String signo2) {
		this.signo2 = signo2;
	}

	public String getImpneto() {
		return impneto;
	}

	public void setImpneto(String impneto) {
		this.impneto = impneto;
	}

	public String getSigno3() {
		return signo3;
	}

	public void setSigno3(String signo3) {
		this.signo3 = signo3;
	}

	public String getRetesp() {
		return retesp;
	}

	public void setRetesp(String retesp) {
		this.retesp = retesp;
	}

	public String getSigno4() {
		return signo4;
	}

	public void setSigno4(String signo4) {
		this.signo4 = signo4;
	}

	public String getRetivaEsp() {
		return retivaEsp;
	}

	public void setRetivaEsp(String retivaEsp) {
		this.retivaEsp = retivaEsp;
	}

	public String getSigno5() {
		return signo5;
	}

	public void setSigno5(String signo5) {
		this.signo5 = signo5;
	}

	public String getPercepBa() {
		return percepBa;
	}

	public void setPercepBa(String percepBa) {
		this.percepBa = percepBa;
	}

	public String getSigno6() {
		return signo6;
	}

	public void setSigno6(String signo6) {
		this.signo6 = signo6;
	}

	public String getRetivaD1() {
		return retivaD1;
	}

	public void setRetivaD1(String retivaD1) {
		this.retivaD1 = retivaD1;
	}

	public String getSigno7() {
		return signo7;
	}

	public void setSigno7(String signo7) {
		this.signo7 = signo7;
	}

	public String getRetivaD2() {
		return retivaD2;
	}

	public void setRetivaD2(String retivaD2) {
		this.retivaD2 = retivaD2;
	}

	public String getSigno8() {
		return signo8;
	}

	public void setSigno8(String signo8) {
		this.signo8 = signo8;
	}

	public String getCargoPex() {
		return cargoPex;
	}

	public void setCargoPex(String cargoPex) {
		this.cargoPex = cargoPex;
	}

	public String getSigno9() {
		return signo9;
	}

	public void setSigno9(String signo9) {
		this.signo9 = signo9;
	}

	public String getRetivaPex1() {
		return retivaPex1;
	}

	public void setRetivaPex1(String retivaPex1) {
		this.retivaPex1 = retivaPex1;
	}

	public String getSigno10() {
		return signo10;
	}

	public void setSigno10(String signo10) {
		this.signo10 = signo10;
	}

	public String getRetivaPex2() {
		return retivaPex2;
	}

	public void setRetivaPex2(String retivaPex2) {
		this.retivaPex2 = retivaPex2;
	}

	public String getSigno11() {
		return signo11;
	}

	public void setSigno11(String signo11) {
		this.signo11 = signo11;
	}

	public String getCostoCuoemi() {
		return costoCuoemi;
	}

	public void setCostoCuoemi(String costoCuoemi) {
		this.costoCuoemi = costoCuoemi;
	}

	public String getSigno12() {
		return signo12;
	}

	public void setSigno12(String signo12) {
		this.signo12 = signo12;
	}

	public String getRetivaCuo1() {
		return retivaCuo1;
	}

	public void setRetivaCuo1(String retivaCuo1) {
		this.retivaCuo1 = retivaCuo1;
	}

	public String getSigno13() {
		return signo13;
	}

	public void setSigno13(String signo13) {
		this.signo13 = signo13;
	}

	public String getRetivaCuo2() {
		return retivaCuo2;
	}

	public void setRetivaCuo2(String retivaCuo2) {
		this.retivaCuo2 = retivaCuo2;
	}

	public String getSigno14() {
		return signo14;
	}

	public void setSigno14(String signo14) {
		this.signo14 = signo14;
	}

	public String getImpServ() {
		return impServ;
	}

	public void setImpServ(String impServ) {
		this.impServ = impServ;
	}

	public String getSigno15() {
		return signo15;
	}

	public void setSigno15(String signo15) {
		this.signo15 = signo15;
	}

	public String getIva1Xlj() {
		return iva1Xlj;
	}

	public void setIva1Xlj(String iva1Xlj) {
		this.iva1Xlj = iva1Xlj;
	}

	public String getSigno16() {
		return signo16;
	}

	public void setSigno16(String signo16) {
		this.signo16 = signo16;
	}

	public String getIva2Xlj() {
		return iva2Xlj;
	}

	public void setIva2Xlj(String iva2Xlj) {
		this.iva2Xlj = iva2Xlj;
	}

	public String getSigno17() {
		return signo17;
	}

	public void setSigno17(String signo17) {
		this.signo17 = signo17;
	}

	public String getCargoEdcE() {
		return cargoEdcE;
	}

	public void setCargoEdcE(String cargoEdcE) {
		this.cargoEdcE = cargoEdcE;
	}

	public String getSigno18() {
		return signo18;
	}

	public void setSigno18(String signo18) {
		this.signo18 = signo18;
	}

	public String getIva1EdcE() {
		return iva1EdcE;
	}

	public void setIva1EdcE(String iva1EdcE) {
		this.iva1EdcE = iva1EdcE;
	}

	public String getSigno19() {
		return signo19;
	}

	public void setSigno19(String signo19) {
		this.signo19 = signo19;
	}

	public String getIva2EdcE() {
		return iva2EdcE;
	}

	public void setIva2EdcE(String iva2EdcE) {
		this.iva2EdcE = iva2EdcE;
	}

	public String getSigno20() {
		return signo20;
	}

	public void setSigno20(String signo20) {
		this.signo20 = signo20;
	}

	public String getCargoEdcB() {
		return cargoEdcB;
	}

	public void setCargoEdcB(String cargoEdcB) {
		this.cargoEdcB = cargoEdcB;
	}

	public String getSigno21() {
		return signo21;
	}

	public void setSigno21(String signo21) {
		this.signo21 = signo21;
	}

	public String getIva1EdcB() {
		return iva1EdcB;
	}

	public void setIva1EdcB(String iva1EdcB) {
		this.iva1EdcB = iva1EdcB;
	}

	public String getSigno22() {
		return signo22;
	}

	public void setSigno22(String signo22) {
		this.signo22 = signo22;
	}

	public String getIva2EdcB() {
		return iva2EdcB;
	}

	public void setIva2EdcB(String iva2EdcB) {
		this.iva2EdcB = iva2EdcB;
	}

	public String getSigno23() {
		return signo23;
	}

	public void setSigno23(String signo23) {
		this.signo23 = signo23;
	}

	public String getCargoCitE() {
		return cargoCitE;
	}

	public void setCargoCitE(String cargoCitE) {
		this.cargoCitE = cargoCitE;
	}

	public String getSigno24() {
		return signo24;
	}

	public void setSigno24(String signo24) {
		this.signo24 = signo24;
	}

	public String getIva1CitE() {
		return iva1CitE;
	}

	public void setIva1CitE(String iva1CitE) {
		this.iva1CitE = iva1CitE;
	}

	public String getSigno25() {
		return signo25;
	}

	public void setSigno25(String signo25) {
		this.signo25 = signo25;
	}

	public String getIva2CitE() {
		return iva2CitE;
	}

	public void setIva2CitE(String iva2CitE) {
		this.iva2CitE = iva2CitE;
	}

	public String getSigno26() {
		return signo26;
	}

	public void setSigno26(String signo26) {
		this.signo26 = signo26;
	}

	public String getCargoCitB() {
		return cargoCitB;
	}

	public void setCargoCitB(String cargoCitB) {
		this.cargoCitB = cargoCitB;
	}

	public String getSigno27() {
		return signo27;
	}

	public void setSigno27(String signo27) {
		this.signo27 = signo27;
	}

	public String getIva1CitB() {
		return iva1CitB;
	}

	public void setIva1CitB(String iva1CitB) {
		this.iva1CitB = iva1CitB;
	}

	public String getSigno28() {
		return signo28;
	}

	public void setSigno28(String signo28) {
		this.signo28 = signo28;
	}

	public String getIva2CitB() {
		return iva2CitB;
	}

	public void setIva2CitB(String iva2CitB) {
		this.iva2CitB = iva2CitB;
	}

	public String getSigno29() {
		return signo29;
	}

	public void setSigno29(String signo29) {
		this.signo29 = signo29;
	}

	public String getRetIva() {
		return retIva;
	}

	public void setRetIva(String retIva) {
		this.retIva = retIva;
	}

	public String getSigno30() {
		return signo30;
	}

	public void setSigno30(String signo30) {
		this.signo30 = signo30;
	}

	public String getRetGcias() {
		return retGcias;
	}

	public void setRetGcias(String retGcias) {
		this.retGcias = retGcias;
	}

	public String getSigno31() {
		return signo31;
	}

	public void setSigno31(String signo31) {
		this.signo31 = signo31;
	}

	public String getRetIngbru() {
		return retIngbru;
	}

	public void setRetIngbru(String retIngbru) {
		this.retIngbru = retIngbru;
	}

	public String getSigno32() {
		return signo32;
	}

	public void setSigno32(String signo32) {
		this.signo32 = signo32;
	}

	public String getAster() {
		return aster;
	}

	public void setAster(String aster) {
		this.aster = aster;
	}

	public String getCasacta() {
		return casacta;
	}

	public void setCasacta(String casacta) {
		this.casacta = casacta;
	}

	public String getTipcta() {
		return tipcta;
	}

	public void setTipcta(String tipcta) {
		this.tipcta = tipcta;
	}

	public String getCtabco() {
		return ctabco;
	}

	public void setCtabco(String ctabco) {
		this.ctabco = ctabco;
	}

	public String getCfExentoIva() {
		return cfExentoIva;
	}

	public void setCfExentoIva(String cfExentoIva) {
		this.cfExentoIva = cfExentoIva;
	}

	public String getSigno041() {
		return signo041;
	}

	public void setSigno041(String signo041) {
		this.signo041 = signo041;
	}

	public String getLey25063() {
		return ley25063;
	}

	public void setLey25063(String ley25063) {
		this.ley25063 = ley25063;
	}

	public String getSigno042() {
		return signo042;
	}

	public void setSigno042(String signo042) {
		this.signo042 = signo042;
	}

	public String getAliIngbru() {
		return aliIngbru;
	}

	public void setAliIngbru(String aliIngbru) {
		this.aliIngbru = aliIngbru;
	}

	public String getDtoCampania() {
		return dtoCampania;
	}

	public void setDtoCampania(String dtoCampania) {
		this.dtoCampania = dtoCampania;
	}

	public String getSigno043() {
		return signo043;
	}

	public void setSigno043(String signo043) {
		this.signo043 = signo043;
	}

	public String getIva1DtoCampania() {
		return iva1DtoCampania;
	}

	public void setIva1DtoCampania(String iva1DtoCampania) {
		this.iva1DtoCampania = iva1DtoCampania;
	}

	public String getSigno044() {
		return signo044;
	}

	public void setSigno044(String signo044) {
		this.signo044 = signo044;
	}

	public String getRetIngbru2() {
		return retIngbru2;
	}

	public void setRetIngbru2(String retIngbru2) {
		this.retIngbru2 = retIngbru2;
	}

	public String getSigno045() {
		return signo045;
	}

	public void setSigno045(String signo045) {
		this.signo045 = signo045;
	}

	public String getAliIngbru2() {
		return aliIngbru2;
	}

	public void setAliIngbru2(String aliIngbru2) {
		this.aliIngbru2 = aliIngbru2;
	}

	public String getTasaPex() {
		return tasaPex;
	}

	public void setTasaPex(String tasaPex) {
		this.tasaPex = tasaPex;
	}

	public String getCargoXLiq() {
		return cargoXLiq;
	}

	public void setCargoXLiq(String cargoXLiq) {
		this.cargoXLiq = cargoXLiq;
	}

	public String getSigno048() {
		return signo048;
	}

	public void setSigno048(String signo048) {
		this.signo048 = signo048;
	}

	public String getIva1CargoXLiq() {
		return iva1CargoXLiq;
	}

	public void setIva1CargoXLiq(String iva1CargoXLiq) {
		this.iva1CargoXLiq = iva1CargoXLiq;
	}

	public String getSigno049() {
		return signo049;
	}

	public void setSigno049(String signo049) {
		this.signo049 = signo049;
	}

	public String getDealer() {
		return dealer;
	}

	public void setDealer(String dealer) {
		this.dealer = dealer;
	}

	public String getImpDbCr() {
		return impDbCr;
	}

	public void setImpDbCr(String impDbCr) {
		this.impDbCr = impDbCr;
	}

	public String getSigno0410() {
		return signo0410;
	}

	public void setSigno0410(String signo0410) {
		this.signo0410 = signo0410;
	}

	public String getCfNoReduceIva() {
		return cfNoReduceIva;
	}

	public void setCfNoReduceIva(String cfNoReduceIva) {
		this.cfNoReduceIva = cfNoReduceIva;
	}

	public String getSigno0411() {
		return signo0411;
	}

	public void setSigno0411(String signo0411) {
		this.signo0411 = signo0411;
	}

	public String getPercepIbAgip() {
		return percepIbAgip;
	}

	public void setPercepIbAgip(String percepIbAgip) {
		this.percepIbAgip = percepIbAgip;
	}

	public String getSigno0412() {
		return signo0412;
	}

	public void setSigno0412(String signo0412) {
		this.signo0412 = signo0412;
	}

	public String getAlicPercepIbAgip() {
		return alicPercepIbAgip;
	}

	public void setAlicPercepIbAgip(String alicPercepIbAgip) {
		this.alicPercepIbAgip = alicPercepIbAgip;
	}

	public String getRetenIbAgip() {
		return retenIbAgip;
	}

	public void setRetenIbAgip(String retenIbAgip) {
		this.retenIbAgip = retenIbAgip;
	}

	public String getSigno0413() {
		return signo0413;
	}

	public void setSigno0413(String signo0413) {
		this.signo0413 = signo0413;
	}

	public String getAlicRetenIbAgip() {
		return alicRetenIbAgip;
	}

	public void setAlicRetenIbAgip(String alicRetenIbAgip) {
		this.alicRetenIbAgip = alicRetenIbAgip;
	}

	public String getSubtotRetivaRg3130() {
		return subtotRetivaRg3130;
	}

	public void setSubtotRetivaRg3130(String subtotRetivaRg3130) {
		this.subtotRetivaRg3130 = subtotRetivaRg3130;
	}

	public String getSigno0414() {
		return signo0414;
	}

	public void setSigno0414(String signo0414) {
		this.signo0414 = signo0414;
	}

	public String getProvIngbru() {
		return provIngbru;
	}

	public void setProvIngbru(String provIngbru) {
		this.provIngbru = provIngbru;
	}

	public String getAdicPlancuo() {
		return adicPlancuo;
	}

	public void setAdicPlancuo(String adicPlancuo) {
		this.adicPlancuo = adicPlancuo;
	}

	public String getSigno0415() {
		return signo0415;
	}

	public void setSigno0415(String signo0415) {
		this.signo0415 = signo0415;
	}

	public String getIva1AdPlancuo() {
		return iva1AdPlancuo;
	}

	public void setIva1AdPlancuo(String iva1AdPlancuo) {
		this.iva1AdPlancuo = iva1AdPlancuo;
	}

	public String getSigno0416() {
		return signo0416;
	}

	public void setSigno0416(String signo0416) {
		this.signo0416 = signo0416;
	}

	public String getAdicOpinter() {
		return adicOpinter;
	}

	public void setAdicOpinter(String adicOpinter) {
		this.adicOpinter = adicOpinter;
	}

	public String getSigno0417() {
		return signo0417;
	}

	public void setSigno0417(String signo0417) {
		this.signo0417 = signo0417;
	}

	public String getIva1AdOpinter() {
		return iva1AdOpinter;
	}

	public void setIva1AdOpinter(String iva1AdOpinter) {
		this.iva1AdOpinter = iva1AdOpinter;
	}

	public String getSigno0418() {
		return signo0418;
	}

	public void setSigno0418(String signo0418) {
		this.signo0418 = signo0418;
	}

	public String getAdicAltacom() {
		return adicAltacom;
	}

	public void setAdicAltacom(String adicAltacom) {
		this.adicAltacom = adicAltacom;
	}

	public String getSigno0419() {
		return signo0419;
	}

	public void setSigno0419(String signo0419) {
		this.signo0419 = signo0419;
	}

	public String getIva1AdAltacom() {
		return iva1AdAltacom;
	}

	public void setIva1AdAltacom(String iva1AdAltacom) {
		this.iva1AdAltacom = iva1AdAltacom;
	}

	public String getSigno0420() {
		return signo0420;
	}

	public void setSigno0420(String signo0420) {
		this.signo0420 = signo0420;
	}

	public String getAdicCupmanu() {
		return adicCupmanu;
	}

	public void setAdicCupmanu(String adicCupmanu) {
		this.adicCupmanu = adicCupmanu;
	}

	public String getSigno0421() {
		return signo0421;
	}

	public void setSigno0421(String signo0421) {
		this.signo0421 = signo0421;
	}

	public String getIva1AdCupmanu() {
		return iva1AdCupmanu;
	}

	public void setIva1AdCupmanu(String iva1AdCupmanu) {
		this.iva1AdCupmanu = iva1AdCupmanu;
	}

	public String getSigno0422() {
		return signo0422;
	}

	public void setSigno0422(String signo0422) {
		this.signo0422 = signo0422;
	}

	public String getAdicAltacomBco() {
		return adicAltacomBco;
	}

	public void setAdicAltacomBco(String adicAltacomBco) {
		this.adicAltacomBco = adicAltacomBco;
	}

	public String getSigno0423() {
		return signo0423;
	}

	public void setSigno0423(String signo0423) {
		this.signo0423 = signo0423;
	}

	public String getIva1AdAltacomBco() {
		return iva1AdAltacomBco;
	}

	public void setIva1AdAltacomBco(String iva1AdAltacomBco) {
		this.iva1AdAltacomBco = iva1AdAltacomBco;
	}

	public String getSigno0424() {
		return signo0424;
	}

	public void setSigno0424(String signo0424) {
		this.signo0424 = signo0424;
	}

	public String getRevisado() {
		return revisado;
	}

	public void setRevisado(String revisado) {
		this.revisado = revisado;
	}

}
