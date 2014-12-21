-- Query para recuperar la conexión a eliminar bajo el siguiente criterio:
-- Recuperar el pid más antiguo con duración mayor a p_timeout segundos relacionado con bloqueos
SELECT procpid
FROM
(
	-- Casos con locks.  Seleccionar una conexion de las existentes, la mas antigua
	-- que presente locks que no finalizan luego de cierto umbral de tiempo
	SELECT	DISTINCT age(now(), query_start) AS age, procpid
	FROM 	pg_stat_activity, pg_locks
	WHERE 	pg_locks.pid = pg_stat_activity.procpid 
	AND	datname = :p_dbname
	AND current_query ilike '<IDLE> in transaction'
	AND 0 < (SELECT COUNT(1) FROM pg_locks WHERE granted = false)
	AND age(now(), query_start) > :p_timeout

) AS foo
ORDER BY age DESC
LIMIT 1