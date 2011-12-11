<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  
  <xsl:template match="/">
    <html>
      <body>
        <h2>Weather Statisics</h2>
        <table>
          <tr>
            <th>Date</th>
            <th>Forecast</th>
          </tr>
          <xsl:for-each select="rss/channel/item">
            <tr>
              <td>
                <xsl:value-of select="title"></xsl:value-of>
              </td>
              <td>
                <xsl:value-of select="description"></xsl:value-of>
              </td>
            </tr>
          </xsl:for-each>
        </table>
      </body>
    </html>
  </xsl:template>
</xsl:stylesheet><!--Created by xsl:easy 4.0, (C) 2003-2010 SoftProject GmbH-->