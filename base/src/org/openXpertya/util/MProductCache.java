package org.openXpertya.util;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.model.MProduct;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.StringUtil;

/**
 * Cache simple de productos asociado a una trxName. La idea principal de esta clase
 * es que sea usada y precargada desde processo "multi-documentos" (procesos
 * que tratan la creación de más de un documento dentro de la misma transacción);
 * permitiendo a estos documentos compartir la misma cache, aunque tambien permite
 * mejorar el procesamiento tradicional de docuemntos.
 * Esta cache nunca registra MProducts sin id asociado (en particular
 * nuevos, o no accesibles via la actual trxName).
 * <br>
 * NOTA: se asume que los MProduct's en esta cache NO son modificados
 * en el transcurso del procesamiento;  es responsabilidad de la logica de 
 * procesamiento mantener las instancias (via metodo put()) actualizadas 
 * en caso que tales modificaciones sean llevadas a cabo via PO.save() 
 * y/o updates directos sobre la base de datos.
 * <br>
 * @author Ader Javier
 *
 */
public class MProductCache {
	
	private CLogger log = CLogger.getCLogger(MProduct.class);
	
	/** usada en caso de no existir en cache */
	private String trxName;
	private Properties ctx;
	private HashMap<Integer,MProduct> cache = new HashMap<Integer,MProduct>();
	
	public MProductCache(Properties ctx,String trxName)
	{
		this.ctx = ctx;
		this.trxName = trxName;
		
	}

	public boolean contains(int M_Product_ID)
	{
		return cache.containsKey(M_Product_ID);
	}
	
	public void put(MProduct p)
	{
		if (p == null)
			return;
		if (p.getM_Product_ID() <=0)
			return;
		cache.put(p.getM_Product_ID(),p);
		
	}
	
	public void remove(int M_Product_ID)
	{
		cache.remove(M_Product_ID);
	}
	
	/**
	 * Retonra el producto asociado a la clave M_Product_ID; si no esta
	 * en la cache lo accede via new MProdutc(....) y cachea el resultado
	 * SI TAL MProduct existe. Jamás rentorna ni cachea un producto
	 * con id <= 0; esto es, un producto no salavo o no visible
	 * desd la actual trxName.
	 * 
	 * @param M_Product_ID
	 * @return el MProduct asociado , o null SI NO EXISTE en la DB.
	 */
	public MProduct get(int M_Product_ID)
	{
	   if (M_Product_ID <=0)
		   return null;
	   MProduct p = cache.get(M_Product_ID);
	
	   if (p!= null)
		   return p;
	   
	   p = new MProduct(ctx,M_Product_ID,trxName);
	   if (p.getM_Product_ID() <= 0) //NO existe tal producto,desde trx, en la base de datos!
		   return null;
	   
	   cache.put(p.getM_Product_ID(),p);
	   return p;
	}
	
	/**
	 * Carga desde DB los MProducts a partir de los ids en el la lista (generando un solo
	 * acceso a DB). La entradas preexistentes en cach con igual id son reemplazadas.
	 * 
	 * @param ids lista de ids de productos; no puede tener elementos null
	 * @return la cantidad de MProduct's cargados o -1 si ocurre algún error de acceso
	 */
	public int loadMasive(List<Integer> ids)
	{
		if (ids == null ) 
			return 0;
		if (ids.size()<=0) 
			return 0;
		
		
		String querySelect = 
			"SELECT * FROM M_Product WHERE M_Product_ID IN " +
			StringUtil.implodeForUnion(ids);
	
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int qtyLoaded = 0;
		try
		{
			int M_Product_ID;
			pstmt = DB.prepareStatement(querySelect,trxName);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				MProduct p = new MProduct(ctx,rs,trxName);
				M_Product_ID = p.getM_Product_ID();
				if (M_Product_ID <=0)
				{
					//no deberia pasar...
					log.log(Level.SEVERE, "MProductCache.loadMasive: sin id valido");
					continue;
				}
				qtyLoaded++;
				cache.put(M_Product_ID,p);
			}
			rs.close();
			pstmt.close();
		}catch(Exception ex) 
		{
		  log.log(Level.SEVERE, "MProductCache.loadMasive ", ex);
		  return -1;
		}finally
		{
			try{
				if (rs != null)
					rs.close();
				if (pstmt != null)
					pstmt.close();
				rs = null; pstmt = null;
			}catch(Exception ex2){}
		}
		
		return qtyLoaded;
		
	}
}
