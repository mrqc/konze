package net.masterstudios.konze.driver.postgres

import net.masterstudios.konze.schemadiscovery.SchemaDiscovery
import schemacrawler.schemacrawler.*
import schemacrawler.tools.utility.SchemaCrawlerUtility
import us.fatehi.utility.datasource.DatabaseConnectionSources
import us.fatehi.utility.datasource.MultiUseUserCredentials

public class PostgresSchemaDiscovery (
    public val username: String,
    public val password: String,
    public val connectionString: String,
) : SchemaDiscovery() {
    public override fun getSchemaDumpAsString(): String {
        val dataSource = DatabaseConnectionSources.newDatabaseConnectionSource(
            connectionString,
            MultiUseUserCredentials(username, password)
        )

        val limitOptions = LimitOptionsBuilder.builder()
            .includeTables { true }
            .toOptions()
        
        val loadOptions = LoadOptionsBuilder.builder()
            .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard())
            .toOptions()

        val options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptions)
            .withLoadOptions(loadOptions)

        val catalog = SchemaCrawlerUtility.getCatalog(dataSource, options)
        val sb = StringBuilder()

        for (table in catalog.tables) {
            sb.append("Table: ${table.name}\n")
            sb.append("Columns:\n")
            for (column in table.columns) {
                sb.append("  - ${column.name} (${column.columnDataType})")
                if (column.isPartOfPrimaryKey) sb.append(" [PK]")
                sb.append("\n")
            }
            if (table.importedForeignKeys.isNotEmpty()) {
                sb.append("Relationships:\n")
                for (fk in table.importedForeignKeys) {
                    for (mapping in fk.columnReferences) {
                        sb.append("  - ${fk.name}: ${mapping.foreignKeyColumn.name} -> ${mapping.primaryKeyColumn.name}\n")
                    }
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}
