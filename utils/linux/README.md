# Servicios de Libertya para Linux

Archivos para registrar el servidor de aplicaciones de Libertya como servicio del sistema (`libertyad`).

Cada archivo se llama `<nombre de destino>.<servidor>`: al instalarlo se copia sacándole el sufijo.

| Sistema de inicio | Servidor | Archivo | Destino |
|---|---|---|---|
| systemd | Tomcat | `systemd/libertyad.service.tomcat` | `/etc/systemd/system/libertyad.service` |
| systemd | JBoss | `systemd/libertyad.service.jboss` | `/etc/systemd/system/libertyad.service` |
| SysV | Tomcat | `sysv/libertyad.tomcat` | `/etc/init.d/libertyad` |
| SysV | JBoss | `../unix/libertyad_<distro>.sh` (scripts anteriores) | `/etc/init.d/libertyad` |

El servicio se llama siempre `libertyad`, porque el deploy de Jenkins y `utils/restartPostgres.sh` lo buscan con ese nombre. Instalar una sola variante por servidor.

## systemd

```sh
cp /ServidorOXP/utils/linux/systemd/libertyad.service.tomcat /etc/systemd/system/libertyad.service
systemctl daemon-reload
systemctl enable --now libertyad
journalctl -u libertyad -f
```

Para JBoss es igual, con `libertyad.service.jboss`.

- Corre como el usuario `libertya`. Se pueden agregar variables en `/etc/sysconfig/libertyad` (RedHat) o `/etc/default/libertyad` (Debian).
- Si Libertya no está en `/ServidorOXP`, ajustar `WorkingDirectory`, `OXP_HOME` y la ruta del `ExecStart`.
- Con Tomcat, la salida va al journal y no a `catalina.out`, porque Tomcat corre en primer plano (`catalina.sh run`).

## SysV (sistemas sin systemd)

```sh
cp /ServidorOXP/utils/linux/sysv/libertyad.tomcat /etc/init.d/libertyad
chmod 755 /etc/init.d/libertyad

# CentOS/RHEL/Fedora
chkconfig --add libertyad && chkconfig libertyad on
# Debian/Ubuntu
update-rc.d libertyad defaults

service libertyad start
```

- Es un solo script para todas las distros: tiene los encabezados `chkconfig` y LSB.
- Guarda el PID en `/var/run/libertya/libertya.pid`, el mismo lugar que usaba el script de JBoss y que espera el deploy de Jenkins.
- `service libertyad status` imprime `1` si está corriendo y `0` si no, como espera `restartPostgres.sh`.
- Los logs quedan en `/ServidorOXP/tomcat/logs/catalina.out`.
- Variables opcionales en `/etc/sysconfig/libertyad` o `/etc/default/libertyad`: `OXP_HOME`, `LIBERTYA_USER`, `LIBERTYA_GROUP`, `PID_FILE`, `STOP_TIMEOUT`.

Para JBoss con SysV siguen valiendo los scripts de `utils/unix/`, que se dejaron sin cambios.

## Problemas frecuentes

- **El servicio falla con "Permiso denegado" en `tomcat/logs/catalina.out`.** Pasa cuando alguien levantó Tomcat a mano como root: quedan archivos de root en `tomcat/logs`, `work` y `temp`, y el servicio (usuario `libertya`) no puede escribirlos. Para repararlo: `chown -R libertya:libertya /ServidorOXP/tomcat/{logs,work,temp}`. Para probar a mano, entrar como el usuario del servicio (`sudo -iu libertya`) y ejecutar el script desde ahí.
- **Con systemd, Tomcat arranca y se detiene enseguida.** El `ExecStart` está usando `IniciarServidorTomcat.sh` o `startup.sh`: esos scripts dejan Java en segundo plano, systemd da el servicio por terminado y lo detiene. Usar `libertyad.service.tomcat`, que ejecuta `catalina.sh run`.
