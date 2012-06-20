#
cd /ServidorOXP/utils
rm nohup.out
nohup ./IniciarServidor.sh & 
echo Tras el arranque del servidor puede utilizar: Ctrl-Z
echo seguido por el comando del shell: bg
echo Esperando ....
sleep 5
tail -f nohup.out
