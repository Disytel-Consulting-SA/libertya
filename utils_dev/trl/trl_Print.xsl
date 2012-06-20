<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- This sample xslt is for transform an xml validated by openxpertyaTrl.dtd -->
<!-- to destination xml, which printed optional attributes trl as an sub-element of row element-->
<!-- orginal  as sub-element of value element-->
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
		<xsl:apply-templates select="@trl"/>
	</row>
</xsl:template>

<xsl:template match="@trl">
	<xsl:element name="trl">
		<xsl:value-of select="."/>
	</xsl:element>
</xsl:template>

<!-- get value element without original attribute-->
<xsl:template match="value">
	<value column="{@column}">
		<xsl:value-of select="."/>
		<xsl:apply-templates select="@original"/>
	</value>
</xsl:template>

<xsl:template match="@original">
	<xsl:element name="original">
		<xsl:value-of select="."/>
	</xsl:element>
</xsl:template>

</xsl:stylesheet>
