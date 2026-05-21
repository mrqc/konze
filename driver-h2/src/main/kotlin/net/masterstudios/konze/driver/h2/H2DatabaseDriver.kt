package net.masterstudios.konze.driver.h2

import net.masterstudios.konze.database.DatabaseDriver
import java.sql.Connection

public class H2DatabaseDriver(connection: Connection) : DatabaseDriver(connection) {
}
