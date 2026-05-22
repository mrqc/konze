package net.masterstudios.konze.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

public class YamlFileReader(@PublishedApi internal val filepath: String) {
    @PublishedApi
    internal val mapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerModule(kotlinModule())

    public fun read(): Map<String, Any?> {
        val file = File(filepath)
        if (!file.exists()) {
            throw IllegalArgumentException("File not found: $filepath")
        }
        return mapper.readValue(file)
    }

    public inline fun <reified T> readAs(): T {
        val file = File(filepath)
        if (!file.exists()) {
            throw IllegalArgumentException("File not found: $filepath")
        }
        return mapper.readValue(file)
    }
}

