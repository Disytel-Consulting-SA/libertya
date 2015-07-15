date ;
cd /ServidorOXP/utils ;
/sbin/service libertyad stop ;
sleep 5 ;
/ServidorOXP/utils/killEmAll.sh /ServidorOXP/jboss ;
rm -f /var/run/libertya.pid ; 
sleep 5 ; 
/sbin/service libertyad start ;
date ;
