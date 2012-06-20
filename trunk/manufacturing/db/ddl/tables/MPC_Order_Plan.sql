`/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CPM
 * Copyright (C) 1999-2004 Jorg Janke, ComPiere, Inc. 
 * Copyright (C) 2004 Victor Pérez, e-Evolution, S.C.
 * All Rights Reserved.
 * Contributor(s): Victor Pérez, e-Evolution, S.C.
 *************************************************************************
 * $Id: MPC_Order_Plan.sql,v 1.1 2004/02/11 19:06:51 vpj-cd Exp $
 ***
 * Title:	MPC_Order_Plan
 * Description:
 ************************************************************************/
DROP TABLE MP_Order_Plan;
CREATE TABLE MP_Order_Plan
(
    MPC_Order_Plan_ID          	     NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , IsActive                         CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL
  , IsSOTrx                          CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , DocumentNo                       NVARCHAR2(60)                   NOT NULL
  , DocStatus                        CHAR     (2)                    NOT NULL
  , DocAction                        CHAR     (2)                    NOT NULL
  , C_DocType_ID                     NUMBER   (10)                   NOT NULL
  , C_DocTypeTarget_ID               NUMBER   (10)                   NOT NULL
  , S_Resource_ID                    NUMBER   (10)  
  , MPC_Product_BOM_ID               NUMBER   (10)                   
  , MPC_Routing_ID                   NUMBER   (10)   
  , M_Product_ID                     NUMBER   (10) 
  , M_AttributeSetInstance_ID        NUMBER   (10)    
  , M_Warehouse_ID                   NUMBER   (10)                   NOT NULL
  , IsApproved                       CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , IsPrinted                        CHAR     (1)                    DEFAULT 'N' NOT NULL
  , IsSelected                       CHAR     (1)                    DEFAULT 'N' NOT NULL
  , Planner_ID                       NUMBER   (10) 		     NOT NULL	
  , MPC_Resource_ID                  NUMBER   (10)                   NOT NULL   
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
  , CONSTRAINT MPC_Order_Plan_Key PRIMARY KEY (MPC_Order_Plan_ID)           
);
-- 
-- TABLE: MPC_Order_Plan
--

ALTER TABLE MPC_Order_Plan ADD CONSTRAINT AD_OrgMPC_Order_Plan
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE MPC_Order_Plan ADD CONSTRAINT AD_ClientMPC_Order_Plan
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;


--ALTER TABLE MPC_Order_Plan ADD CONSTRAINT MPC_RoutingMPC_Order_Plan 
--    FOREIGN KEY (MP_Routing_ID)
--    REFERENCES MP_Routing(MP_Routing_ID)
--;

--ALTER TABLE MPC_Order_Plan ADD CONSTRAINT MPC_Product_BOMMPC_Order_Plan
--    FOREIGN KEY (MPC_Product_BOM_ID)
--    REFERENCES MPC_Product_BOM(MPC_Product_BOM_ID)
--;
   
 



