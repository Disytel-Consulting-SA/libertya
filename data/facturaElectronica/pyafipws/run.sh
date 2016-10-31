#!/bin/bash

RUTAWSFE=$HOME"/Documentos/pyafipws_prod"
RUTAPYTHON="/usr/bin/python"

cd $RUTAWSFE
$RUTAPYTHON $RUTAWSFE/wsfev1.py --archivo --debug > $RUTAWSFE/wsfev1.log 
