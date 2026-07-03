package com.kmp.pyr.roam.util

import android.util.Base64
import com.kmp.pyr.roam.localdata.SingleMap.map
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object BlowfishEncryption {
    private const val KEY_LENGTH_BITS = 128
    private const val PBKDF2_ITERATIONS = 256
    private const val SALT_LENGTH = 16
    private const val IV_LENGTH = 8

    /**
     * Derives a key using PBKDF2 with SHA-256
     */
    private fun deriveKey(password: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            PBKDF2_ITERATIONS,
            KEY_LENGTH_BITS
        )
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return factory.generateSecret(spec).encoded
    }

    /**
     * Encrypts plaintext using Blowfish/CBC/PKCS5Padding.
     * Returns base64-encoded: salt || iv || ciphertext
     *
     * Blowfish is used instead of ChaCha20-Poly1305 because the latter is not
     * supported by the AndroidOpenSSL provider on API level 28. Blowfish is not
     * authenticated, so unlike ChaCha20-Poly1305 there is no tag appended to
     * the ciphertext.
     */
    fun encrypt(plaintext: String, password: String): String {
        // Generate random salt and IV
        val salt = ByteArray(SALT_LENGTH)
        val iv = ByteArray(IV_LENGTH)
        SecureRandom().apply {
            nextBytes(salt)
            nextBytes(iv)
        }

        // Derive key (note: password is reversed, matching PHP strrev())
        val key = deriveKey(password.reversed(), salt)
        val secretKey = SecretKeySpec(key, "Blowfish")

        // Encrypt using Blowfish in CBC mode with PKCS5 padding
        val cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val ciphertext = cipher.doFinal(plaintext.toByteArray(StandardCharsets.UTF_8))

        // Combine: salt || iv || ciphertext
        val combined = salt + iv + ciphertext

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }
}

object PayloadEncoder {
    private val EXTRA_PARAM_1 = map["p1"]
    private val EXTRA_PARAM_3 = map["p3"]
    private val EXTRA_PARAM_9 = map["p9"]

    private val jsonSecret = "$EXTRA_PARAM_3$EXTRA_PARAM_9$EXTRA_PARAM_1"

    /**
     * Encodes payload compatible with the backend decode_payload function
     *
     * @param data Map of key-value pairs to encode
     * @return Base64-encoded encrypted payload
     */
    fun encodePayload(data: Map<String, String>): String {
        // Convert to JSON, then encrypt the entire JSON
        val jsonString = JSONObject(data).toString()
        return BlowfishEncryption.encrypt(jsonString, jsonSecret)
    }
}
