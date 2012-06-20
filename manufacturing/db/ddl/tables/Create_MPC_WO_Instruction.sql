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
 * Title:	 Create MPC Work Order Instruction Table and Elements
 * Description:
 *	Creates and updates all Work Order Instruction elements as desired in the 
 *  Compiere AD_ Tables, Column, and Elements
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

DELETE FROM COMPIERE.AD_TABLE   WHERE UPPER(TABLENAME)  = 'MPC_WO_Instruction'
DELETE FROM COMPIERE.AD_ELEMENT WHERE UPPER(COLUMNNAME) = 'MPC_WO_Instruction_ID'

DROP TABLE MPC_WO_Instruction;

-- -----------------------------------------------------------------------------
-- Create the database table definition
-- -----------------------------------------------------------------------------

CREATE TABLE MPC_WO_Instruction(
    MPC_WO_Instruction_ID    NUMBER(10, 0)      NOT NULL,
    MPC_WO_Task_ID           NUMBER(10, 0)      NOT NULL,
    AD_Client_ID             NUMBER(10, 0)      NOT NULL,
    AD_Org_ID                NUMBER(10, 0)      NOT NULL,
    IsActive                 CHAR(1)             DEFAULT 'Y' NOT NULL
                             CHECK (IsActive in ('Y','N')),
    Created                  DATE               DEFAULT SYSDATE NOT NULL,
    CreatedBy                NUMBER(10, 0)      NOT NULL,
    Updated                  DATE               DEFAULT SYSDATE NOT NULL,
    UpdatedBy                NUMBER(10, 0)      NOT NULL,
    Value                    NVARCHAR2(40)      NOT NULL,
    Name                     NVARCHAR2(60)      NOT NULL,
    Qty                      NUMBER             DEFAULT 0 NOT NULL,
    InstructionType          CHAR(2)            NOT NULL,
    Text                     NVARCHAR2(2000)    NOT NULL,
    C_UOM_ID                 NUMBER(10, 0)      NOT NULL,
    CONSTRAINT MPC_WO_Instruction_Key PRIMARY KEY (MPC_WO_Instruction_ID)
    USING INDEX
        PCTFREE 10
        INITRANS 2
        MAXTRANS 255
        STORAGE(INITIAL 10K
                NEXT 10K
                MINEXTENTS 1
                MAXEXTENTS 121
                PCTINCREASE 50
                ), 
    CONSTRAINT RefC_UOM2157 FOREIGN KEY (C_UOM_ID)
    REFERENCES C_UOM(C_UOM_ID),
    CONSTRAINT RefMPC_WO_Task2183 FOREIGN KEY (MPC_WO_Task_ID)
    REFERENCES MPC_WO_Task(MPC_WO_Task_ID)
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

COMMENT ON TABLE MPC_WO_Instruction IS 'An Instruction is the specific description of what specificially needs to be accomplished for a Task/Operation. '
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
             NAME = 'Instruction',      
        PRINTNAME = 'Instruction',   
       COLUMNNAME = 'MPC_WO_Instruction_ID',    
      DESCRIPTION = 'Manufacturing Work Order Instruction', 
             HELP = 'Manufacturing Work Order Instruction' 
 WHERE UPPER(COLUMNNAME)= 'MPC_WO_INSTRUCTION_ID';

UPDATE COMPIERE.AD_ELEMENT 
   SET ENTITYTYPE = 'A', 
             NAME = 'Instruction Type',      
        PRINTNAME = 'Instruction Type',   
       COLUMNNAME = 'InstructionType',    
      DESCRIPTION = 'Manufacturing Instruction Type', 
             HELP = 'Manufacturing Instruction Type' 
 WHERE UPPER(COLUMNNAME)= 'INSTRUCTIONTYPE';

-- -----------------------------------------------------------------------------
-- Update the table entry
-- -----------------------------------------------------------------------------

UPDATE COMPIERE.AD_TABLE   
   SET ENTITYTYPE = 'A', 
     ISDELETEABLE = 'N', 
             NAME = 'MPC_WO_Instruction',    
        TABLENAME = 'MPC_WO_Instruction',
      ACCESSLEVEL = '1'                -- Organization
  WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION';

-- -----------------------------------------------------------------------------
-- Update the column entries - Setup Unique Entries
-- -----------------------------------------------------------------------------

-- MPC_WO_Instruction_ID -------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 13,               -- ID
           ISMANDATORY = 'Y',
              ISPARENT = 'N',
          ISUPDATEABLE = 'N',
                 ISKEY = 'Y'
 WHERE UPPER(COLUMNNAME) = 'MPC_WO_INSTRUCTION_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- MPC_WO_Task_ID --------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 19,               -- TableDir
           ISMANDATORY = 'Y',
          ISUPDATEABLE = 'N',
              ISPARENT = 'Y'
 WHERE UPPER(COLUMNNAME) = 'MPC_WO_TASK_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- C_UOM_ID --------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 19,               -- TableDir
           ISMANDATORY = 'Y',
          ISUPDATEABLE = 'Y',
              ISPARENT = 'Y'
 WHERE UPPER(COLUMNNAME) = 'C_UOM_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- Text ------------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 14,               -- Text
           ISMANDATORY = 'N',
          ISUPDATEABLE = 'Y'
 WHERE UPPER(COLUMNNAME) = 'TEXT'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- Instruction Type ------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 17,               -- List
 AD_REFERENCE_VALUE_ID = 1000000,          -- Instruction Type
           ISMANDATORY = 'N',
          ISUPDATEABLE = 'Y'
 WHERE UPPER(COLUMNNAME) = 'INSTRUCTIONTYPE'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

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
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- AD_Org_ID -------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 19,               -- TableDir
        AD_VAL_RULE_ID = 104,              -- AD_OrgSecurityValidation
          ISUPDATEABLE = 'N',
          DEFAULTVALUE = '@AD_Org_ID@'
 WHERE COLUMNNAME   = 'AD_Org_ID'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- Created ---------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 16,               -- DateTime
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'Created'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- AD_CreatedBy ----------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 18,               -- Table
 AD_REFERENCE_VALUE_ID = 110,              -- AD_User
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'CreatedBy'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- Updated ---------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 16,               -- DateTime
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'Updated'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- AD_UpdatedBy ----------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET AD_REFERENCE_ID = 18,               -- Table
 AD_REFERENCE_VALUE_ID = 110,              -- AD_User
          ISUPDATEABLE = 'N'
 WHERE COLUMNNAME   = 'UpdatedBy'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

-- Name ------------------------------------------------------------------------

UPDATE COMPIERE.AD_COLUMN
   SET ISIDENTIFIER = 'Y',              
        ISMANDATORY = 'Y',
       ISUPDATEABLE = 'Y'
 WHERE COLUMNNAME  = 'Name'
   AND AD_TABLE_ID = (SELECT AD_TABLE_ID 
                        FROM COMPIERE.AD_TABLE 
                       WHERE UPPER(TABLENAME) = 'MPC_WO_INSTRUCTION');

COMMIT;