package org.openXpertya.api.accounting;

import org.openXpertya.model.PO;

public interface PostService {
    /**
     * Postea el comprobante asociado a 'record'.
     * Debe encargarse de resolver lo necesario (schema, ctx, trx).
     * Devuelve un mensaje/código de resultado (o null si OK) igual que Doc.postImmediate suele hacer.
     */
    String postImmediate(PO record, boolean force, String whereClause);
}
