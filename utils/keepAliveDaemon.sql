SELECT procpid
FROM
(
	SELECT	DISTINCT age(now(), query_start) AS age, procpid
	FROM 	pg_stat_activity, ad_keepalive
	WHERE 	pg_stat_activity.procpid = ad_keepalive.pid
	AND		datname = :p_dbname
	AND 	current_query not in ('<IDLE> in transaction', '<IDLE>')
	AND     procpid <> pg_backend_pid()
	AND		age(now(), ad_keepalive.updated)  > :p_timeout
	ORDER BY AGE DESC
) 
AS foo
ORDER BY age DESC
LIMIT 1