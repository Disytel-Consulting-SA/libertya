/*************************************************************************
 * The contents of this file are subject to the Compiere License.  You may
 * obtain a copy of the License at    http://www.compiere.org/license.html 
 * Software is on an  "AS IS" basis,  WITHOUT WARRANTY OF ANY KIND, either 
 * express or implied. See the License for details. Code: Compiere ERP+CPM
 * Copyright (C) 1999-2004 Jorg Janke, ComPiere, Inc. 
 * Copyright (C) 2004 Victor Pérez, e-Evolution, S.C.
 * Copyright (C) 2004 Colorado Correctional Industries 
 * All Rights Reserved.
 *************************************************************************
 * Title:	 Create MPC Operation Table and Elements
 * Description:
 *	Creates and updates all Operation Elements as desired in the Compiere 
 *  AD_ Tables, Column, and Elements
 *
 *	Steps:
 *
 *  1. Remove the existing table and all dependancies	
 *	2. Create the Table in the database
 *  3. Create the Elements - D_Element_Check.sql
 *  4. Create the Column entries - 0_Add_New_Column.sql
 *  5. Update the AD_Element entries
 *	6. Update the AD_Column entries
 *
 *****************************************************************************/

-- -----------------------------------------------------------------------------
-- Remove the table, column, and element data
-- -----------------------------------------------------------------------------

DELETE FROM COMPIERE.AD_TABLE   WHERE UPPER(TABLENAME)  = 'MPC_OPERATION'
DELETE FROM COMPIERE.AD_ELEMENT WHERE UPPER(COLUMNNAME) = 'MPC_OPERATION_ID'

DROP TABLE MPC_Operation;

-- -----------------------------------------------------------------------------
-- Create the database table definition
-- -----------------------------------------------------------------------------

CREATE TABLE MPC_Operation(
    MPC_Operation_ID    NUMBER(10, 0)      NOT NULL,
    AD_Client_ID        NUMBER(10, 0)      NOT NULL,
    AD_Org_ID           NUMBER(10, 0)      NOT NULL,
    IsActive            CHAR(1)             DEFAULT 'Y' NOT NULL
                        CHECK (IsActive in ('Y','N')),
    Created             DATE               DEFAULT SYSDATE NOT NULL,
    CreatedBy           NUMBER(10, 0)      NOT NULL,
    Updated             DATE               DEFAULT SYSDATE NOT NULL,
    UpdatedBy           NUMBER(10, 0)      NOT NULL,
    Value               NVARCHAR2(40)      NOT NULL,
    Name                NVARCHAR2(60)      NOT NULL,
    Description         NVARCHAR2(255),
    Help                NVARCHAR2(2000),
    CONSTRAINT MPC_Operation_Key PRIMARY KEY (MPC_Operation_ID)
    USING INDEX
        PCTFREE 10
        INITRANS 2
        MAXTRANS 255
        STORAGE(INITIAL 10K
                NEXT 10K
                MINEXTENTS 1
                MAXEXTENTS 121
                PCTINCREASE 50
                )
)
PCTFREE 10
PCTUSED 40
INITRANS 1
MAXTRANS 255
STORAGE(INITIAL 10K
        NEXT 10K
        MINEXTENTS 1
        MAXEXTENTS 121
        PCTINCREASE 50
        )
;

COMMENT ON TABLE MPC_Operation IS 'Operations are logical definitions of what needs to be performed during one step of the manufacturing process.'
;

-- -----------------------------------------------------------------------------
-- Create the column and element entries
-- -----------------------------------------------------------------------------

@../../maintain/0_Add_New_Column.sql;
@../../maintain/AD_Element_Check.sql;

-- -----------------------------------------------------------------------------
-- Update the element entries
-- -----------------------------------------------------------------------------

UPDATE COMPIERE.AD_ELEMENT 
   SET ENTITYTYPE = 'A', 
             NAME = 'Operation',      
        PRINTNAME = 'Operation',   
       COLUMNNAME = 'MPC_Operation_ID',    
      DESCRIPTION = 'Manufacturing Operation', 
             HELP = 'Manufacturing Operation' 
 WHERE UPPER(COLUMNNAME)= 'MPC_OPERATION_ID';

-- -----------------------------------------------------------------------------
-- Update the table entry
-- -----------------------------------------------------------------------------

UPDATE COMPIERE.AD_TABLE   
   SET ENTITYTYPE = 'A', 
             NAME = 'MPC_Operation',    
        TABLENAME = 'MPC_Operation',
      ACCESSLEVEL = '1'                -- Organization
  WHERE UPPER(TABLENAME) = 'MPC_OPERATION';

-- -----------------------------------------------------------------------------
-- Update the column entries - Setup Unique Entries
-- -----------------------------------------------------------------------------

-- MPC_OPERATION_ID --------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 13,               -- ID
           ISMANDATORY = 'Y',
          ISUPDATEABLE = 'N',
                 ISKEY = 'Y'
 WHERE UPPER(COLUMNNAME) = 'MPC_OPERATION_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_OPERATION');

-- Product ---------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 30,                -- Search
       CALLOUT = 'org.compiere.model.CalloutInventory.product'
 WHERE COLUMNNAME = 'M_Product_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_OPERATION');



-- -----------------------------------------------------------------------------
-- Update the column entries - Setup Defaults
-- -----------------------------------------------------------------------------

-- AD_Client_ID ----------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 19,               -- TableDir
       AD_VAL_RULE_ID  = 103,              -- AD_ClientSecurityValidation
          ISUPDATEABLE = 'N',
          DEFAULTVALUE = '@AD_Client_ID@'
 WHERE COLUMNNAME   = 'AD_Client_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_OPERATION');

-- AD_Org_ID -------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 19,               -- TableDir
        AD_VAL_RULE_ID = 104,              -- AD_OrgSecurityValidation
          ISUPDATEABLE = 'N',
          DEFAULTVALUE = '@AD_Org_ID@'
 WHERE COLUMNNAME   = 'AD_Org_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_OPERATION');

-- Created ---------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 16,               -- DateTime
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'Created'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_OPERATION');

-- AD_CreatedBy ----------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 18,               -- Table
 AD_REFERENCE_VALUE_ID = 110,              -- AD_User
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'CreatedBy'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_OPERATION');

-- Updated ---------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 16,               -- DateTime
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'Updated'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_OPERATION');

-- AD_UpdatedBy ----------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 18,               -- Table
 AD_REFERENCE_VALUE_ID = 110,              -- AD_User
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'UpdatedBy'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_OPERATION');

-- Name ------------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET ISIDENTIFIER = 'Y',              
        ISMANDATORY = 'Y',
       ISUPDATEABLE = 'Y'
 WHERE COLUMNNAME  = 'Name'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_OPERATION');

COMMIT;