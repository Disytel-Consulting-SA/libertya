#!/bin/bash

RUTAWSFE=$HOME"/Documentos/pyafipws_prod"
RUTAPYTHON="/usr/bin/python"
#RUTAPYTHON=`which python`

cd $RUTAWSFE
$RUTAPYTHON $RUTAWSFE/wsaa.py > $RUTAWSFE/wsaa.log