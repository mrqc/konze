package net.masterstudios.konze.database

import java.security.SecureRandom

/**
 * Utility class for generating secure, random passwords.
 */
public class PasswordGenerator {
    private val secureRandom = SecureRandom()

    public companion object {
        private const val LOWERCASE = "abcdefghijklmnopqrstuvwxyz"
        private const val UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val DIGITS = "0123456789"
        private const val SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?"
        private const val ALL_CHARS = LOWERCASE + UPPERCASE + DIGITS + SYMBOLS
    }

    /**
     * Generates a secure random password.
     * @param length The length of the password (default is 16).
     * @param useSymbols Whether to include symbols in the password (default is true).
     * @return A cryptographically secure random password.
     */
    public fun generate(length: Int = 16, useSymbols: Boolean = true): String {
        require(length > 0) { "Password length must be greater than zero." }
        
        val allowedChars = if (useSymbols) ALL_CHARS else LOWERCASE + UPPERCASE + DIGITS
        val password = StringBuilder(length)

        // Ensure we have at least one character from each required group if possible
        if (length >= 4) {
            password.append(LOWERCASE[secureRandom.nextInt(LOWERCASE.length)])
            password.append(UPPERCASE[secureRandom.nextInt(UPPERCASE.length)])
            password.append(DIGITS[secureRandom.nextInt(DIGITS.length)])
            if (useSymbols) {
                password.append(SYMBOLS[secureRandom.nextInt(SYMBOLS.length)])
            } else {
                password.append(allowedChars[secureRandom.nextInt(allowedChars.length)])
            }
        }

        // Fill the rest
        while (password.length < length) {
            password.append(allowedChars[secureRandom.nextInt(allowedChars.length)])
        }

        // Shuffle the result
        return password.toString().toCharArray().let { chars ->
            for (i in chars.size - 1 downTo 1) {
                val j = secureRandom.nextInt(i + 1)
                val temp = chars[i]
                chars[i] = chars[j]
                chars[j] = temp
            }
            String(chars)
        }
    }
}
