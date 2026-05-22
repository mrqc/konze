package net.masterstudios.konze.database

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PasswordGeneratorTest {

    private val generator = PasswordGenerator()

    @Test
    fun `should generate password of specified length`() {
        val length = 20
        val password = generator.generate(length)
        assertEquals(length, password.length)
    }

    @Test
    fun `should generate complex password with default settings`() {
        val password = generator.generate(16)
        
        assertTrue(password.any { it.isLowerCase() }, "Should contain lowercase")
        assertTrue(password.any { it.isUpperCase() }, "Should contain uppercase")
        assertTrue(password.any { it.isDigit() }, "Should contain digits")
        assertTrue(password.any { "!@#$%^&*()-_=+[]{}|;:,.<>?".contains(it) }, "Should contain symbols")
    }

    @Test
    fun `should generate password without symbols when requested`() {
        val password = generator.generate(16, useSymbols = false)
        
        assertTrue(password.any { it.isLowerCase() }, "Should contain lowercase")
        assertTrue(password.any { it.isUpperCase() }, "Should contain uppercase")
        assertTrue(password.any { it.isDigit() }, "Should contain digits")
        assertTrue(password.none { "!@#$%^&*()-_=+[]{}|;:,.<>?".contains(it) }, "Should NOT contain symbols")
    }

    @Test
    fun `should produce different passwords on subsequent calls`() {
        val p1 = generator.generate()
        val p2 = generator.generate()
        assertTrue(p1 != p2, "Passwords should be different")
    }
}
