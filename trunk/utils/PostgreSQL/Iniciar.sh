echo Arranca la base de datos de PostgreSQL


# para cygwin podría ser necesario IPC daemon
# ipc-daemon&

# pg_ctl -o "-i" -l $PGLOG start
service postgresql start
