#!/bin/bash

RUTAWSFE=$HOME"/Documentos/pyafipws_prod"
RUTAPYTHON="/usr/bin/python"
#RUTAPYTHON=`which python`

cd $RUTAWSFE
$RUTAPYTHON $RUTAWSFE/wsfev1.py --archivo --debug > $RUTAWSFE/wsfev1.log 2> $RUTAWSFE/wsfev1.log
