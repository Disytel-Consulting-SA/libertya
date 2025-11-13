package org.openXpertya.server;

import org.openXpertya.api.accounting.PostService;
import org.openXpertya.model.PO;
import org.openXpertya.acct.Doc;
import org.openXpertya.model.MAcctSchema;
import org.openXpertya.util.Env;

import java.util.Properties;

public class PostServiceImpl implements PostService {

    @Override
    public String postImmediate(PO record, boolean force, String whereClause) {
        final Properties ctx   = record.getCtx();
        final int        table = record.get_Table_ID();
        final int        id    = record.getID();
        final String     trx   = record.get_TrxName();

        MAcctSchema[] as = MAcctSchema.getClientAcctSchema(ctx, Env.getAD_Client_ID(ctx));

        if (as != null && as.length > 0) {
            return Doc.postImmediate(as, table, id, force, trx, whereClause);
        }

        // Fallback
        try {
            return Doc.postImmediate(null, table, id, force, trx, whereClause);
        } catch (Throwable t) {
            return "NoAcctSchema";
        }
    }
}
