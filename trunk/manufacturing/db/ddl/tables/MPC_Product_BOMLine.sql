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
 * $Id: MPC_Product_BOMLine.sql,v 1.2 2004/02/11 17:38:23 vpj-cd Exp $
 ***
 * Title:	Bill of Material
 * Description:
 ************************************************************************/
DROP TABLE MPC_Product_BOMLine;
CREATE TABLE MPC_Product_BOMLine
(
    MPC_Product_BOMLine_ID           NUMBER   (10)                   NOT NULL
  , MPC_Product_BOM_ID               NUMBER   (10)                   NOT NULL
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL     
  , Description                      NVARCHAR2 (510)                     
  , Line                             NUMBER   (10)                   NOT NULL
  , Assay                            NUMBER                          
  , QtyBOM                           NUMBER                          DEFAULT 0 NOT NULL 
  , QtyBOMType                       CHAR     (1)                    
  , BatchPercent                     NUMBER                          DEFAULT 0 NOT NULL
  , ByProduct                        CHAR     (1)                    DEFAULT 'Y' NOT NULL
  , C_UOM_ID                         NUMBER   (10)                   NOT NULL
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL
  , CreatedBy                        NUMBER   (10)                   NOT NULL
  , IsCritical                       CHAR     (1)                    DEFAULT ('N') NOT NULL
  , Forecast                         NUMBER                          
  , IssueMethod                      CHAR (1)  
  , BackflushGroup                   NVARCHAR2 (20)                                       
  , LTOffSet                         NUMBER                          
  , Operation                        NUMBER   (10)                   
  , M_Product_ID                     NUMBER   (10)                   
  , Product_BOM_ID                   NUMBER   (10)                   
  , M_AttributeSetInstance_ID        NUMBER(10, 0)
  , Scrap                            NUMBER                     
  , ValidFrom                        DATE                            NOT NULL
  , ValidTo                          DATE                            NULL 
  , Updated                          DATE                            DEFAULT SYSDATE NOT NULL
  , updatedBy                        NUMBER   (10)                   NOT NULL
  , IsActive                         CHAR     (1)                    DEFAULT ('Y') NOT NULL       
    CHECK (IsActive in ('Y','N')),
    CONSTRAINT MPC_Product_BOMLine_Key PRIMARY KEY (MPC_Product_BOMLine_ID)
);

-- 
-- TABLE: M_Product_BOMLine 
--


ALTER TABLE MPC_Product_BOMLine ADD CONSTRAINT M_Product_MPC_ProductBOMLine
    FOREIGN KEY (M_Product_ID)
    REFERENCES M_Product(M_Product_ID) ON DELETE CASCADE
;



