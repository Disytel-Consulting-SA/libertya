@Rem	Print translation files

@Rem	Parameter - file to process otherwise default
SET FILE=%1%
IF '%FILE%'=='' SET FILE=AD_Element_Trl_es_ES.xml
IF '%FUENTES_OXP%'=='' SET FUENTES_OXP=C:\oxp2_2
IF '%OPENXPERTYA_TRL%'=='' SET OPENXPERTYA_TRL=es_ES

@Echo ... %FILE%

@java org.apache.xalan.xslt.Process -in %FUENTES_OXP%\data\%OPENXPERTYA_TRL%\%FILE% -xsl trl_Print.xsl -out %FUENTES_OXP%\data\%OPENXPERTYA_TRL%\p_%FILE%

@pause
