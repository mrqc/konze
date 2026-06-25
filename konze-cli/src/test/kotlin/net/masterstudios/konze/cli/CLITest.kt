package net.masterstudios.net.masterstudios.konze.cli

import kotlin.test.Test

class CLITest {
    @Test
    fun testCliWithQuery() {
        CLI.main(arrayOf("-q", "hello_world_test_query"))
    }
}
