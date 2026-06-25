package net.masterstudios.net.masterstudios.konze.cli

import java.io.File

/**
 * Parses an env-style file with key=value pairs.
 * Blank lines and lines starting with '#' are ignored.
 * Keys are normalized to lowercase.
 */
public class GuardFile(private val file: File) {

    private val entries: Map<String, String> by lazy { parse() }

    public constructor(path: String) : this(File(path))

    public fun exists(): Boolean = file.exists()

    public operator fun get(key: String): String? = entries[key.lowercase()]

    public fun require(key: String): String {
        val value = get(key)
        if (value.isNullOrBlank()) {
            throw IllegalStateException("Required key '$key' is not set in ${file.name}!")
        }
        return value
    }

    private fun parse(): Map<String, String> =
        file.readLines()
            .filter { line -> line.isNotBlank() && !line.trimStart().startsWith("#") }
            .mapNotNull { line ->
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) parts[0].trim().lowercase() to parts[1].trim() else null
            }
            .toMap()
}
