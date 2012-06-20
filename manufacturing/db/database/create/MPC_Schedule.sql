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
 * $Id: MPC_Order_Plan.sql,v 1.1 2004/02/11 19:06:51 vpj-cd Exp $
 ***
 * Title:	MPC_Order_Plan
 * Description:
 ************************************************************************/
DROP TABLE MPC_Schedule CASCADE CONSTRAINTS;
CREATE TABLE MPC_Schedule  
(
    MPC_Schedule_ID          	     NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , ScheduleType                     CHAR     (3)                    NOT NULL
  , IsActive                         CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL
  , IsSOTrx                          CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , DocumentNo                       NVARCHAR2(60)                   NOT NULL                 
  , S_Resource_ID                    NUMBER   (10)		     NOT NULL 
  , M_Warehouse_ID                   NUMBER   (10)                   NOT NULL
  , IsApproved                       CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , IsPrinted                        CHAR     (1)                    DEFAULT 'N' NOT NULL
  , IsSelected                       CHAR     (1)                    DEFAULT 'N' NOT NULL
  , Planner_ID                       NUMBER   (10) 		     NOT NULL	
  , DateStartShedule                 DATE                            DEFAULT SYSDATE NOT NULL
  , DateFinishShedule                DATE                            DEFAULT SYSDATE NOT NULL
  , DateStart                        DATE                            DEFAULT SYSDATE NOT NULL
  , DateFinish                       DATE                            DEFAULT SYSDATE NOT NULL
  , CopyFrom                         CHAR     (1)                           
  , Name                             NVARCHAR2(120)                  NOT NULL
  , Description                      NVARCHAR2(510)                  
  , IsCreated                        CHAR     (1)                    DEFAULT 'N' NOT NULL
  , Processed                        CHAR     (1)                    DEFAULT 'N' NOT NULL
  , Processing                       CHAR     (1)                    
  , AD_OrgTrx_ID                     NUMBER   (10)                   
  , C_Project_ID                     NUMBER   (10)                   
  , C_Campaign_ID                    NUMBER   (10)                   
  , C_Activity_ID                    NUMBER   (10)                   
  , User1_ID                         NUMBER   (10)                   
  , User2_ID                         NUMBER   (10)        
  , CHECK (IsActive in ('Y','N'))
  , CONSTRAINT MPC_Schedule_Key PRIMARY KEY (MPC_Schedule_ID)           
);
--
-- MPC_Schedule
--
ALTER TABLE MPC_Schedule ADD CONSTRAINT ADClientMPCSchedule
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE MPC_Schedule ADD CONSTRAINT ADOrgMPCSchedule
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;
ALTER TABLE MPC_Schedule ADD CONSTRAINT SResourceMPCSchedule
    FOREIGN KEY (S_Resource_ID)
    REFERENCES S_Resource(S_Resource_ID)
;
ALTER TABLE MPC_Schedule ADD CONSTRAINT MWarehouseMPCSchedule
    FOREIGN KEY (M_Warehouse_ID)
    REFERENCES M_Warehouse(M_Warehouse_ID)
;
ALTER TABLE MPC_Schedule ADD CONSTRAINT Planner_IDMPCSchedule
    FOREIGN KEY (Planner_ID)
    REFERENCES AD_User(AD_User_ID)
;
ALTER TABLE MPC_Schedule ADD CONSTRAINT ADOrgTrxMPCSchedule
    FOREIGN KEY (AD_OrgTrx_ID)
    REFERENCES AD_Org(AD_Org_ID)
;
ALTER TABLE MPC_Schedule ADD CONSTRAINT AD_OrgMPCSchedule
    FOREIGN KEY (C_Project_ID)
    REFERENCES C_Project(C_Project_ID)
;
ALTER TABLE MPC_Schedule ADD CONSTRAINT CCampaignMPCSchedule
    FOREIGN KEY (C_Campaign_ID)
    REFERENCES C_Campaign(C_Campaign_ID)
;
ALTER TABLE MPC_Schedule ADD CONSTRAINT CActivityMPCSchedule
    FOREIGN KEY (C_Activity_ID)
    REFERENCES C_Activity(C_Activity_ID)
;
ALTER TABLE MPC_Schedule ADD CONSTRAINT CElementValue1MPCSchedule 
    FOREIGN KEY (User2_ID)
    REFERENCES C_ElementValue(C_ElementValue_ID)
;

ALTER TABLE MPC_Schedule ADD CONSTRAINT CElementValue2MPCSchedule 
    FOREIGN KEY (User1_ID)
    REFERENCES C_ElementValue(C_ElementValue_ID)
; 
   
 



