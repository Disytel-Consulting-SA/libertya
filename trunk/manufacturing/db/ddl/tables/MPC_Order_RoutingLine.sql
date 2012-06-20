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
 * $Id: MPC_Order_RoutingLine.sql,v 1.1 2004/02/11 19:07:53 vpj-cd Exp $
 ***
 * Title:	MPC_Order_RoutingLine
 * Description:
 ************************************************************************/
DROP TABLE MPC_Order_RoutingLine;
CREATE TABLE MPC_Order_RoutingLine
(
    MPC_Order_RoutingLine_ID         NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , IsActive                         CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , UpdatedBy                        NUMBER   (10)                   NOT NULL
  , MPC_Order_Plan_ID                NUMBER   (10)                   NOT NULL
  , Line                             NUMBER   (10)                   NOT NULL
  , M_Product_ID                     NUMBER   (10)                   NOT NULL
  , M_Locator_ID                     NUMBER   (10)                                   
  , M_AttributeSetInstance_ID        NUMBER   (10) 
  , A_Asset_ID                       NUMBER   (10)                   
  , C_BPartner_ID                    NUMBER   (10)                   
  , CyclesHour                       NUMBER                          
  , Description                      NVARCHAR2(1020)                 
  , Document                         NVARCHAR2(44)
  , MPC_StandardOperation_ID         NUMBER   (10)                 
  , MPC_Resource_ID                  NUMBER   (10)                   NOT NULL
  , IsMilestoneOperation             CHAR     (1)                    DEFAULT ('N') 
  , MoveTime                         NUMBER                          
  , NumberAsset                      NUMBER                           
  , NumberPeople                     NUMBER                          
  , OverlapUnits                     NUMBER                          
  , PercentEfficiency                NUMBER                          
  , QueueTime                        NUMBER                          
  , Revision                         NVARCHAR2(20)                  
  , RunCrew                          NUMBER                          
  , RunTime                          NUMBER                          
  , RunTimeActual                    NUMBER 
  , Setupcrew                        NUMBER                          
  , Setuptime                        NUMBER                          
  , Setuptimeactual                  NUMBER
  , QtyBatch                         NUMBER                          DEFAULT 0 NOT NULL
  , QtyMovement                      NUMBER                          DEFAULT 0 NOT NULL
  , QtyPostActual		     NUMBER			     DEFAULT 0 NOT NULL
  , QtyDelivered		     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyMoveOut			     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyOutAdjust		     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyOutScrap			     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyProccessed		     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyRecjetAdjust		     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyRejected			     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyRejectscrap		     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyInQueue 		             NUMBER 			     DEFAULT 0 NOT NULL
  , QtyBeginInQueue 		     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyOrdered		       	     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyOutQueue 	             NUMBER 			     DEFAULT 0 NOT NULL
  , QtyBeginOutQueue 	             NUMBER 			     DEFAULT 0 NOT NULL
  , QtyRework	 	             NUMBER 			     DEFAULT 0 NOT NULL
  , QtyWip	 	             NUMBER 			     DEFAULT 0 NOT NULL
  , QtyRejectQueue 	             NUMBER 			     DEFAULT 0 NOT NULL
  , QtyReworked 	             NUMBER 			     DEFAULT 0 NOT NULL
  , MPC_Order_ID		     NUMBER   (10)		     NOT NULL
  , Type                             CHAR     (1)                    
  , UnitsCycles                      NUMBER                          
  , Value                            NVARCHAR2 (80)                   
  , WaitTime                         NUMBER                          
  , Yield                            NUMBER                          
  , Name                             NVARCHAR2 (120)                  
  , IsSubcontracting                 CHAR     (1)                    DEFAULT ('N') NOT NULL
  , MPC_Routing_ID                    NUMBER   (10)                   
  , ValidFrom                        DATE                            NOT NULL
  , ValidTo                          DATE                            NULL
  , CHECK (IsActive in ('Y','N'))
  , CHECK (IsMilestoneOperation in ('Y','N'))
  , CHECK (IsSubContracting in ('Y','N')) 
  , CONSTRAINT MPC_Order_RoutingLine_Key PRIMARY KEY (MP_Order_RoutingLine_ID)                           
);

-- 
-- TABLE: MPC_Order_RoutingLine
--

ALTER TABLE MPC_Order_Routing ADD CONSTRAINT AD_OrgMPC_Order_Rout
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE MPC_Order_Routing ADD CONSTRAINT AD_ClientMPC_Order_Rout
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;
