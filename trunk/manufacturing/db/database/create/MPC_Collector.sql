DROP TABLE MPC_Cost_Collector CASCADE CONSTRAINTS;
-- 
-- TABLE: MPC_Collector 
--

CREATE TABLE MPC_Cost_Collector(    
    MPC_Order_ID                 NUMBER(10, 0)     NOT NULL,
    AD_OrgTrx_ID                 NUMBER(10, 0)             ,
    AD_Client_ID                 NUMBER(10, 0)     NOT NULL,
    AD_Org_ID                    NUMBER(10, 0)     NOT NULL,
    C_DocType_ID                 NUMBER(10, 0)     NOT NULL,
    C_DocTypeTarget_ID		 NUMBER(10, 0)     NOT NULL,
    IsActive                     CHAR(1)           DEFAULT 'Y' NOT NULL,
    Created                      DATE              DEFAULT SYSDATE NOT NULL,
    CreatedBy                    NUMBER(10, 0)     NOT NULL,
    Updated                      DATE              DEFAULT SYSDATE NOT NULL,
    UpdatedBy                    NUMBER(10, 0)     NOT NULL,
    MPC_Cost_Collector_ID        NUMBER(10, 0)     NOT NULL,
    MovementDate                 DATE              NOT NULL,
    DateAcct                  DATE                 NOT NULL,
    DocAction                    CHAR(2),
    DocStatus        		 CHAR(2),
    Posted           	         CHAR(1)            DEFAULT 'N' NOT NULL,
    Processed                    CHAR(1)            DEFAULT 'N' NOT NULL,
    Processing                   CHAR(1),  
    M_Warehouse_ID               NUMBER(10, 0)     NOT NULL,
    S_Resource_ID                NUMBER(10, 0)     NOT NULL,
    M_Locator_ID                 NUMBER(10, 0)     NOT NULL,
    M_Product_ID                 NUMBER(10, 0)     NOT NULL,
    M_AttributeSetInstance_ID    NUMBER(10, 0),
    C_Project_ID     NUMBER(10, 0),
    C_Campaign_ID    NUMBER(10, 0),
    C_Activity_ID    NUMBER(10, 0),
    User2_ID         NUMBER(10, 0),
    User1_ID         NUMBER(10, 0),
    MovementQty                  NUMBER             DEFAULT 0 NOT NULL,
    ScrappedQty                  NUMBER             DEFAULT 0,
    Description                  NVARCHAR2(255),    
    CHECK (IsActive in ('Y','N')),
    CONSTRAINT MPC_Cost_Collector_Key PRIMARY KEY (MPC_Cost_Collector_ID)
);


-- 
-- TABLE:  MPC_Cost_Element
--
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT ADOrgMPCCostCollector
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID) ON DELETE CASCADE ;
    
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT ADClientMPCCostCollector
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID) ON DELETE CASCADE;

ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT CDocTypeMPCCostCollector
    FOREIGN KEY (C_DocType_ID)
    REFERENCES C_DocType(C_DocType_ID) ON DELETE CASCADE;
    
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT MWarehouseMPCCostCollector
    FOREIGN KEY (M_Warehouse_ID)
    REFERENCES M_Warehouse(M_Warehouse_ID) ON DELETE CASCADE;
    
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT ADOrgMPCCostCollector
    FOREIGN KEY (AD_OrgTrx_ID)
    REFERENCES AD_Org(AD_Org_ID) ON DELETE CASCADE;

ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT CActivityMPCCostCollector
    FOREIGN KEY (C_Activity_ID)
    REFERENCES C_Activity(C_Activity_ID) ON DELETE CASCADE;

ALTER TABLE MPC_Cost_Collector  ADD CONSTRAINT CCampaignMPCCostCollector
    FOREIGN KEY (C_Campaign_ID)
    REFERENCES C_Campaign(C_Campaign_ID) ON DELETE CASCADE;    
    
    
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT CElementValue1MPCCostCollector 
    FOREIGN KEY (User2_ID)
    REFERENCES C_ElementValue(C_ElementValue_ID) ON DELETE CASCADE;

ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT CElementValue2MPCCostCollector 
    FOREIGN KEY (User1_ID)
    REFERENCES C_ElementValue(C_ElementValue_ID) ON DELETE CASCADE;    
    
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT CProjectMPCCostCollector
    FOREIGN KEY (C_Project_ID)
    REFERENCES C_Project(C_Project_ID) ON DELETE CASCADE;

ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT MAttributeSetIMPCCostCollector
    FOREIGN KEY (M_AttributeSetInstance_ID)
    REFERENCES M_AttributeSetInstance(M_AttributeSetInstance_ID) ON DELETE CASCADE; 
       
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT MProductMPCCostCollector
    FOREIGN KEY (M_Product_ID)
    REFERENCES M_Product(M_Product_ID) ON DELETE CASCADE; 
    
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT SResourceMPCCostCollector
    FOREIGN KEY (S_Resource_ID)
    REFERENCES S_Resource(S_Resource_ID) ON DELETE CASCADE;
    
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT MLocatorMPCCostCollector
    FOREIGN KEY (M_Locator_ID)
    REFERENCES M_Locator(M_Locator_ID) ON DELETE CASCADE;
    
ALTER TABLE MPC_Cost_Collector ADD CONSTRAINT MPCOrderMPCCostCollector
    FOREIGN KEY (MPC_Order_ID)
    REFERENCES MPC_Order(MPC_Order_ID) ON DELETE CASCADE;    
