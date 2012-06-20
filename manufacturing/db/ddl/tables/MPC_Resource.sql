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
 * $Id: MPC_Resource.sql,v 1.2 2004/02/11 17:02:49 vpj-cd Exp $
 ***
 * Title:	Manufacturing Resource
 * Description:
 ************************************************************************/
DROP TABLE MPC_Resource CASCADE CONSTRAINT;
CREATE TABLE MPC_Resource 
(
    MPC_Resource_ID                  NUMBER   (10)                   NOT NULL
  , ResourceType                     CHAR     (1)                    NOT NULL
  , S_Resource_ID                    NUMBER   (10)                   NOT NULL 	
  , AD_Client_ID                     NUMBER   (10)                   NOT NULL 
  , AD_Org_ID                        NUMBER   (10)                   NOT NULL 
  , IsActive                         CHAR     (1)                    DEFAULT ('Y') NOT NULL
  , DailyCapacity                    NUMBER                          
  , NumberPeople                     NUMBER                          
  , NumberAsset                      NUMBER                          
  , PercentEfficiency                NUMBER                          
  , PercentUtillization              NUMBER                          
  , QueueTime                        NUMBER                          
  , RunCrew                          NUMBER                          
  , SetupCrew                        NUMBER                          
  , Updated                          DATE   			     DEFAULT SYSDATE NOT NULL                         
  , UpdatedBy                        NUMBER   (10)                   NOT NULL 
  , WaitTime                         NUMBER                          
  , Created                          DATE                            DEFAULT SYSDATE NOT NULL 
  , CreatedBy                        NUMBER   (10)                   NOT NULL 
  ,  CHECK (IsActive in ('Y','N'))
  ,  CONSTRAINT MP_WorkCenter_Key PRIMARY KEY (MP_WorkCenter_ID)
);

ALTER TABLE MP_WorkCenter ADD CONSTRAINT MP_WorkCenter_Client 
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE MP_WorkCenter ADD CONSTRAINT MP_WorkCenter_Org 
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;


ALTER TABLE MP_WorkCenter ADD CONSTRAINT MP_WorkCenter_Resource 
    FOREIGN KEY (S_Resource_ID)
    REFERENCES S_Resource(S_Resource_ID)
;