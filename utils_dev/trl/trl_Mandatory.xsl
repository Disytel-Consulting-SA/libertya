<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- This sample xslt is for transform an xml validated by openxpertyaTrl.dtd --
<!-- to destination xml, which removed optional attributes trl, orginal  -->
<!-- Author: Bui Chi Trung -->


<!-- get whole openxpertyaTrl element -->
<xsl:template match="/openxpertyaTrl">
	<openxpertyaTrl table="{@table}" language="{@language}">
		<xsl:apply-templates/>
	</openxpertyaTrl>
</xsl:template>

<!-- get row element without trl attribute -->
<xsl:template match="row">
	<row  id="{@id}">
		<xsl:apply-templates select="value"/>
	</row>
</xsl:template>

<!-- get value element without original attribute-->
<xsl:template match="value">
	<value column="{@column}">
		<xsl:value-of select="."/>
	</value>
</xsl:template>

</xsl:stylesheet>
