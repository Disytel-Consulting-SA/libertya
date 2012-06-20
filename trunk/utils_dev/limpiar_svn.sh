#!/bin/sh
#	Este guión limpia las carpetas de la información del repositorio de subversion
#	realizado por el equipo de Desarrollo de openXpertya. i-Cubo. 2006

LISTA=`find . |grep .svn`
for i in $LISTA
	do
	echo Borrando recursivamente $i
	rm -fR $i
	done

