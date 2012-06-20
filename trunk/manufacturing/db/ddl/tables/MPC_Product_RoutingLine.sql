/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CPM
 * Copyright (C) 1999-2004 Jorg Janke, ComPiere, Inc. 
 * Copyright (C) 2004 Victor Pérez, e-Evolution, S.C.
 * All Rights Reserved.
 * Contributor(s): Victor Pérez, e-Evolution, S.C.
 *************************************************************************
 * $Id: MPC_Product_RoutingLine.sql,v 1.2 2004/02/11 19:08:17 vpj-cd Exp $
 ***
 * Title:	Operation
 * Description:
 ************************************************************************/
DROP TABLE MPC_Product_RoutingLine;
CREATE TABLE MPC_Product_RoutingLine
(
    MPC_Product_RoutingLine_ID       NUMBER   (10)                   NOT NULL
  , MPC_Product_Routing_ID           NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , Value                            VARCHAR2 (80)                   NOT NULL
  , Name                             VARCHAR2 (120)                  NOT NULL 
  , Description                      NVARCHAR2 (510)
  , IsActive                         CHAR     (1)                    DEFAULT ('Y') NOT NULL
  , A_Asset_ID                       NUMBER   (10)                   NOT NULL
  , BatchQty                         NUMBER                          DEFAULT 0 NOT NULL
  , C_BPartner_ID                    NUMBER   (10)                   
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL  
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL  
  , CyclesHour                       NUMBER                                                      
  , MPC_StandardOperation_ID          NUMBER   (10)                   
  , S_Resource_ID                    NUMBER   (10)                   NOT NULL
  , IsMilestoneOperation             CHAR     (1)                    DEFAULT ('N') NOT NULL 
  , MoveTime                         NUMBER                          
  , NumberAsset                      NUMBER                           
  , NumberPeople                     NUMBER                          
  , OverlapUnits                     NUMBER                          
  , PercentEfficiency                NUMBER                          
  , QueueTime                        NUMBER                                        
  , RunCrew                          NUMBER                          
  , RunTime                          NUMBER                          
  , SetupCrew                        NUMBER                          
  , SetupTime                        NUMBER                                      
  , UnitsCycles                      NUMBER                          
  , WaitTime                         NUMBER                          
  , Yield                            NUMBER                          
  , IsSubContracting                 CHAR     (1)                    DEFAULT ('N') NOT NULL
  , Line                             NUMBER                          NOT NULL
  , ValidFrom                        DATE                            NOT NULL
  , ValidTo                          DATE                            NULL
  , CHECK (IsActive in ('Y','N'))
  , CHECK (IsMilestoneOperation in ('Y','N'))
  , CHECK (IsSubContracting in ('Y','N')) 
  , CONSTRAINT MPC_Product_RoutingLine_Key PRIMARY KEY (MPC_Product_RoutingLine_ID)
);

-- 
-- TABLE: MPC_Product_RoutingLine
--

ALTER TABLE MPC_Product_Product_RoutingLine ADD CONSTRAINT AD_OrgMPC_Product_Product_RoutingLine
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE MPC_Product_Product_RoutingLine ADD CONSTRAINT AD_ClientMPC_Product_Product_RoutingLine
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE MPC_Product_Product_RoutingLine ADD CONSTRAINT A_AssetMPC_Product_Product_RoutingLine
    FOREIGN KEY (A_Asset_ID)
    REFERENCES A_Asset(A_Asset_ID)
;

   
 
