-- 
-- TABLE: MPC_WF_NodeNext 
--
DROP TABLE MPC_Order_NodeNext CASCADE CONSTRAINT;
CREATE TABLE MPC_Order_NodeNext
(
    AD_WF_Node_ID     NUMBER(10, 0)      NOT NULL,
    MPC_Order_Node_ID    NUMBER(10, 0)      NOT NULL,
    MPC_Order_ID         NUMBER(10, 0)      NOT NULL,
    MPC_Order_Next_ID     NUMBER(10, 0)      NOT NULL,
    AD_Client_ID      NUMBER(10, 0)      NOT NULL,
    AD_Org_ID         NUMBER(10, 0)      NOT NULL,
    IsActive          CHAR(1)             DEFAULT 'Y' NOT NULL,
    Created           DATE                DEFAULT SYSDATE NOT NULL,
    CreatedBy         NUMBER(10, 0)      NOT NULL,
    Updated           DATE                DEFAULT SYSDATE NOT NULL,
    UpdatedBy         NUMBER(10, 0)      NOT NULL,
    Description       NVARCHAR2(255),
    SeqNo             NUMBER(10, 0)      NOT NULL,
    EntityType        CHAR(1)             DEFAULT 'D' NOT NULL,
    UserOverwrite     CHAR(1)             DEFAULT 'N' NOT NULL,
    IsStdUserWorkflow CHAR(1)             DEFAULT 'N' NOT NULL,
    TransitionCode    NVARCHAR2(2000),
    MovingTime        NUMBER DEFAULT 0,
    QueuingTime       NUMBER DEFAULT 0,
    OverlapUnits      NUMBER DEFAULT 0,
    CHECK (IsActive in ('Y','N')),
    CHECK (UserOverwrite in ('Y','N')),
    CONSTRAINT MPC_Order_NodeNext_Key PRIMARY KEY (MPC_Order_Node_ID, MPC_Order_Next_ID)
)
;

-- 
-- TABLE: AD_WF_NodeNext 
--


ALTER TABLE MPC_Order_NodeNext ADD CONSTRAINT MPCOrdeNextMPCOrdeNex 
    FOREIGN KEY (MPC_Order_Next_ID)
    REFERENCES MPC_Order_Node(MPC_Order_Node_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Order_NodeNext ADD CONSTRAINT MPCOrderNodeMPCOrdeNext 
    FOREIGN KEY (MPC_Order_Node_ID)
    REFERENCES MPC_Order_Node(MPC_Order_Node_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Order_NodeNext ADD CONSTRAINT ADClientMPCOrdeNext 
    FOREIGN KEY (AD_Client_ID)
    REFERENCES AD_Client(AD_Client_ID) ON DELETE CASCADE
;

ALTER TABLE MPC_Order_NodeNext ADD CONSTRAINT ADOrgMPCOrdeNext 
    FOREIGN KEY (AD_Org_ID)
    REFERENCES AD_Org(AD_Org_ID) ON DELETE CASCADE
;
ALTER TABLE MPC_Order_NodeNext ADD CONSTRAINT ADWFNodeMPCOrdeNext
    FOREIGN KEY (AD_WF_Node_ID)
    REFERENCES AD_WF_Node(AD_WF_Node_ID) ON DELETE CASCADE
;