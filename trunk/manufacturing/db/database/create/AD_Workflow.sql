/**************************************************************************************************
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c)  2003-2005 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 ***************************************************************************************************
 * $Id: MPC_Product_BOM.sql,v 1.3 2004/02/11 16:54:13 vpj-cd Exp $
 ***
 * Title:	Bill of Material
 * Description:
 ************************************************************************/
 
 ALTER TABLE M_Prduct ADD LowLevel NUMBER();

 ALTER TABLE AD_Workflow ADD DocumentNo NVARCHAR2(30);
 ALTER TABLE AD_Workflow ADD IsRouting  CHAR(1) DEFAULT('N');
 ALTER TABLE AD_Workflow ADD ProcessType CHAR(3);
 ALTER TABLE AD_Workflow ADD S_Resource_ID  NUMBER DEFAULT 0;
 
 
 
 
 ALTER TABLE AD_WF_Node ADD AD_WF_NodeStandard_ID NUMBER(10);
 ALTER TABLE AD_WF_Node ADD C_BPartner_ID NUMBER(10);
 ALTER TABLE AD_WF_Node ADD IsMilestone CHAR(1) DEFAULT ('N');
 ALTER TABLE AD_WF_Node ADD IsSubcontracting CHAR(1) DEFAULT ('N');
 ALTER TABLE AD_WF_Node ADD S_Resource_ID  NUMBER (10);|
 ALTER TABLE AD_WF_Node ADD UnitsCycles NUMBER DEFAULT 0;
 ALTER TABLE AD_WF_Node ADD ValidFrom DATE;
 ALTER TABLE AD_WF_Node ADD ValidTo DATE;
 ALTER TABLE AD_WF_Node MODIFY WorkingTime NUMBER;
 
  
 
  
  ALTER TABLE AD_WF_Node ADD Yield NUMBER DEFAULT 0
  
  ALTER TABLE AD_WF_NodeNext ADD MovingTime NUMBER DEFAULT 0;
  ALTER TABLE AD_WF_NodeNext ADD OverlapUnits NUMBER DEFAULT 0;
  ALTER TABLE AD_WF_NodeNext ADD QueuingTime NUMBER DEFAULT 0;

  
  ALTER TABLE S_Resource ADD IsManufacturingResource CHAR(1) DEFAULT ('N');
  ALTER TABLE S_Resource ADD DailyCapacity NUMBER;
  ALTER TABLE S_Resource ADD ManufacturingResourceType CHAR(2);
  ALTER TABLE S_Resource ADD PercentUtillization NUMBER;
  ALTER TABLE S_Resource ADD QueuingTime NUMBER;
  ALTER TABLE S_Resource ADD WaitingTime NUMBER;
  
 
  
 
 
