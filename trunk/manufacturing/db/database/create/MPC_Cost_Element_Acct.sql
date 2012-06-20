
DROP TABLE MPC_Cost_Element_Acct CASCADE CONSTRAINT;
CREATE TABLE MPC_Cost_Element_Acct(
    MPC_Cost_Element_ID             NUMBER(10, 0)    NOT NULL,
    C_AcctSchema_ID                 NUMBER(10, 0)    NOT NULL,
    AD_Client_ID                    NUMBER(10, 0)    NOT NULL,
    AD_Org_ID                       NUMBER(10, 0)    NOT NULL,    
    Created                         DATE             DEFAULT SYSDATE NOT NULL,
    CreatedBy                       NUMBER(10, 0)    NOT NULL,
    Updated                         DATE             DEFAULT SYSDATE NOT NULL,
    UpdatedBy                       NUMBER(10, 0)    NOT NULL,
    MPC_VariationUse_Acct           NUMBER(10, 0)    NOT NULL,
    MPC_VariationMethod_Acct        NUMBER(10, 0)    NOT NULL,
    MPC_VariationRate_Acct          NUMBER(10, 0)    NOT NULL,
    MPC_AbsorptionCost_Acct         NUMBER(10, 0)    NOT NULL,
    MPC_COGS_Acct                   NUMBER(10, 0)    NOT NULL
    IsActive                        CHAR(1)          DEFAULT 'Y' NOT NULL,
    CHECK (IsActive in ('Y','N')),
    CONSTRAINT MPC_Cost_Element_Acct_Key PRIMARY KEY (MPC_Cost_Element_ID, C_AcctSchema_ID)
)
;

-- 
-- TABLE:  MPC_Cost_Element
--

ALTER TABLE MPC_Cost_Element_Acct ADD CONSTRAINT ADOrgMPCCostAcct
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID)
;

ALTER TABLE MPC_Cost_Element_Acct ADD CONSTRAINT ADClientMPCCostEAcct
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID)
;

ALTER TABLE MPC_Cost_Element_Acct ADD CONSTRAINT CAcctSchemaMPCCostEAcct
    FOREIGN KEY (C_AcctSchema_ID)
    REFERENCES C_AcctSchema(C_AcctSchema_ID)
;

ALTER TABLE MPC_Cost_Element_Acct ADD CONSTRAINT MPCCostElementMPCCostEAcct
    FOREIGN KEY (MPC_Cost_Element_ID)
    REFERENCES MPC_Cost_Element(MPC_Cost_Element_ID)
;