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
 * $Id: MPC_Order_BOMLine.sql,v 1.1 2004/02/11 19:00:56 vpj-cd Exp $
 ***
 * Title:	MPC_Order_BOMLine
 * Description:
 ************************************************************************/
DROP TABLE MPC_Order_BOMLine;	
CREATE TABLE MPC_Order_BOMLine
(
    AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL
  , MPC_Order_ID                     NUMBER   (10)                   NOT NULL
  , MPC_Order_Plan_ID                NUMBER   (10)                   NOT NULL
  , Assay                            NUMBER
  , QtyBOMType                       CHAR     (1)                             
  , QtyBOM                           NUMBER                          DEFAULT 0 NOT NULL 
  , QtyBOMBatch                      NUMBER                          DEFAULT 0 NOT NULL
  , QtyRequiered		     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyDelivered		     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyReserved                      NUMBER 			     DEFAULT 0 NOT NULL
  , QtyReject			     NUMBER 			     DEFAULT 0 NOT NULL
  , QtyScrap                         NUMBER                          DEFAULT 0 NOT NULL
  , QtyPost			     NUMBER 			     DEFAULT 0 NOT NULL 
  , DeliverTo			     NUMBER   (10)		    
  , DateDelivered		     DATE		             
  , BatchPercent                     NUMBER                          DEFAULT 0 NOT NULL
  , M_Warehouse_ID                   NUMBER   (10)                   NOT NULL   
  , M_locator_ID                     NUMBER   (10)                      
  , ByProduct                        CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , C_UOM_ID                         NUMBER   (10)                   NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , IsCritical                       CHAR     (1)                    DEFAULT ('N') NOT NULL
  , DefualtOption                    CHAR     (1)                    DEFAULT ('N') NOT NULL
  , Description                      NVARCHAR2 (510)                  
  , Document                         NVARCHAR2 (20)                   
  , Ecn                              NVARCHAR2 (20)                   
  , Forecast                         NUMBER                          
  , IssueMethod                      CHAR (1)                    
  , LTOffSet                         NUMBER                          
  , Line                             NUMBER   (10)                   NOT NULL
  , Operation                        NUMBER   (10)                   
  , M_Product_ID                     NUMBER   (10)                   NOT NULL
  , Name                             VARCHAR2 (120)                  NOT NULL 
  , Value                            VARCHAR2 (80)                   NOT NULL 
  , Revision                         VARCHAR2 (10)                   NOT NULL
  , Scrap                            NUMBER                          
  , ValidFrom                        DATE                            NOT NULL
  , ValidTo                          DATE                            NULL 
  , BOMType                          CHAR(1)                         
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , updatedBy                        NUMBER   (10)                   NOT NULL
  , MPC_Componet_ID                  NUMBER   (10)                   NOT NULL
  , MPC_Order_BOMLine_ID                 NUMBER   (10)                   NOT NULL
  , M_AttributeSetInstance_ID        NUMBER(10, 0)
  , IsActive                         CHAR     (1)                    DEFAULT ('Y') NOT NULL
    CHECK (IsActive in ('Y','N')),
    CONSTRAINT MPC_Order_BOMLine_Key PRIMARY KEY (MPC_Order_BOMLine_ID)
);

