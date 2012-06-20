/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CPM
 * Copyright (C) 2003-2003 Victor Pérez, e-Evolution S.C.,Inc. All Rights Reserved.
 *************************************************************************
 * $Id: MP_Product_BOM.sql,v 1 2002/10/21 04:49:46 vpj-cd Exp $
 ***
 * Title:	MP_Order
 * Description:
 ************************************************************************/
DROP TABLE MPC_Order CASCADE CONSTRAINTS;
CREATE TABLE MPC_Order
(
    MPC_Order_ID                     NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , S_Resource_ID                    NUMBER   (10)                   NOT NULL
  , IsActive                         CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL
  , Line                             NUMBER   (10)                   NOT NULL
  , M_Product_ID                     NUMBER   (10)                   NOT NULL
  , M_Warehouse_ID                   NUMBER   (10)                   NOT NULL
  , MPC_Product_BOM_ID               NUMBER   (10)                   NOT NULL
  , AD_Workflow_ID                   NUMBER   (10)                   NOT NULL
  , QtyOrdered                       NUMBER                          DEFAULT 0 NOT NULL
  , QtyDelivered                     NUMBER                          DEFAULT 0 NOT NULL
  , QtyReject                        NUMBER                          DEFAULT 0 NOT NULL
  , QtyScrap			             NUMBER                          DEFAULT 0 NOT NULL	
  , IsQtyPercentage                  CHAR     (1) 
  , Yield			                 NUMBER	             		     DEFAULT 0 
  , Assay 			                 NUMBER   		        	     DEFAULT 0 
  , Description                      NVARCHAR2(510)                  
  , DateStart                        DATE                            DEFAULT SYSDATE  
  , DateFinish                       DATE                            DEFAULT SYSDATE  
  , DateStartShedule                 DATE                            DEFAULT SYSDATE NOT NULL
  , DateFinishShedule                DATE                            DEFAULT SYSDATE NOT NULL
  , DateConfirm                      DATE                            DEFAULT SYSDATE  
  , DateOrdered                      DATE                            DEFAULT SYSDATE  
  , DatePromised                     DATE                            
  , DateDelivered                    DATE 
  , C_UOM_ID                         NUMBER   (10)                   NOT NULL                           
  , OrderType 			             CHAR     (1)		     
  , SheduleType 	                 CHAR     (1) 		     
  , PriorityRule                     CHAR     (1)                    
  , FloatBefored		             NUMBER
  , FloatAfter			             NUMBER
  , IsSoTrx                          CHAR      (1)                   DEFAULT 'Y' NOT NULL
  , DocumentNo                       NVARCHAR2(60)                   NOT NULL
  , DocStatus                        CHAR     (2)                    NOT NULL 
  , DocAction                        CHAR     (2)                    NOT NULL
  , C_DocType_ID                     NUMBER   (10)                   NOT NULL
  , C_DocTypeTarget_ID               NUMBER   (10)                   NOT NULL
  , IsApproved                       CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , IsPrinted                        CHAR     (1)                    DEFAULT 'N' NOT NULL
  , IsSelected                       CHAR     (1)                    DEFAULT 'N' NOT NULL
  , Planner_ID                       NUMBER   (10) 
  , CopyFrom                         CHAR     (1)   
  , Posted                           CHAR     (1)                    DEFAULT 'N' NOT NULL
  , Processed                        CHAR     (1)                    DEFAULT 'N' NOT NULL
  , Processing                       CHAR     (1)                    
  , AD_Orgtrx_ID                     NUMBER   (10)                   
  , C_Project_ID                     NUMBER   (10)                   
  , C_Campaign_ID                    NUMBER   (10)                   
  , C_Activity_ID                    NUMBER   (10)                   
  , User1_ID                         NUMBER   (10)                   
  , User2_ID			             NUMBER   (10)
  , Lot                              NVARCHAR2 (20)                   
  , SerNo                            NVARCHAR2 (20)                   
  , M_AttributeSetInstance_ID        NUMBER   (10)    
  , CHECK (IsActive in ('Y','N'))
  , CONSTRAINT MPC_Order_Key PRIMARY KEY (MPC_Order_ID)                      
);
-- 
-- TABLE: MPC_Order
--

ALTER TABLE MPC_Order ADD CONSTRAINT ADClientMPCOrder
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE MPC_Order ADD CONSTRAINT ADOrgMPCOrder
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE MPC_Order ADD CONSTRAINT CDocTypeMPCOrder
    FOREIGN KEY (C_DocType_ID)
    REFERENCES C_DocType(C_DocType_ID);
    
ALTER TABLE MPC_Order ADD CONSTRAINT MWarehouseMPCOrder
    FOREIGN KEY (M_Warehouse_ID)
    REFERENCES M_Warehouse(M_Warehouse_ID);
    
ALTER TABLE MPC_Order ADD CONSTRAINT ADOrgTrxMPCOrder
    FOREIGN KEY (AD_OrgTrx_ID)
    REFERENCES AD_Org(AD_Org_ID);

ALTER TABLE MPC_Order ADD CONSTRAINT CActivityMPCOrder
    FOREIGN KEY (C_Activity_ID)
    REFERENCES C_Activity(C_Activity_ID);

ALTER TABLE MPC_Order  ADD CONSTRAINT CCampaignMPCOrder
    FOREIGN KEY (C_Campaign_ID)
    REFERENCES C_Campaign(C_Campaign_ID);    
        
ALTER TABLE MPC_Order ADD CONSTRAINT CElementValue1MPCOrder
    FOREIGN KEY (User2_ID)
    REFERENCES C_ElementValue(C_ElementValue_ID);

ALTER TABLE MPC_Order ADD CONSTRAINT CElementValue2MPCOrder
    FOREIGN KEY (User1_ID)
    REFERENCES C_ElementValue(C_ElementValue_ID);    
    
ALTER TABLE MPC_Order ADD CONSTRAINT CProjectMPCOrder
    FOREIGN KEY (C_Project_ID)
    REFERENCES C_Project(C_Project_ID);

ALTER TABLE MPC_Order ADD CONSTRAINT MAttributeSetIMPCOrder
    FOREIGN KEY (M_AttributeSetInstance_ID)
    REFERENCES M_AttributeSetInstance(M_AttributeSetInstance_ID); 
       
ALTER TABLE MPC_Order ADD CONSTRAINT MProductMPCOrder
    FOREIGN KEY (M_Product_ID)
    REFERENCES M_Product(M_Product_ID); 
    
ALTER TABLE MPC_Order ADD CONSTRAINT SResourceMPCOrder
    FOREIGN KEY (S_Resource_ID)
    REFERENCES S_Resource(S_Resource_ID);    
    
--ALTER TABLE MPC_Order ADD CONSTRAINT MPCScheduleMPCOrder
--    FOREIGN KEY (MPC_Schedule_ID)
--    REFERENCES MPC_Schedule(MPC_Schedule_ID);
      
    
ALTER TABLE MPC_Order ADD CONSTRAINT MPCProductBOMMPCOrder
    FOREIGN KEY (MPC_Product_BOM_ID)
    REFERENCES MPC_Product_BOM(MPC_Product_BOM_ID);

ALTER TABLE MPC_Order ADD CONSTRAINT ADWorkflowMPCOrder
    FOREIGN KEY (AD_Workflow_ID)
    REFERENCES AD_Workflow(AD_Workflow_ID);
    
ALTER TABLE MPC_Order ADD CONSTRAINT CUOMMPCOrder
    FOREIGN KEY (C_UOM_ID)
    REFERENCES  C_UOM(C_UOM_ID);    
  

