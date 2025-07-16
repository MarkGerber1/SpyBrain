package com.example.spybrain.util

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Утилита для обеспечения базовой безопасности приложения.
 * Использует Android Keystore для безопасного хранения ключей шифрования.
 */
@Singleton
class SecurityUtil @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "SpyBrainSecretKey"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_LENGTH = 12
        private const val TAG_LENGTH = 128
    }

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    init {
        generateKeyIfNotExists()
    }

    /**
     * Генерирует ключ шифрования, если он не существует
     */
    private fun generateKeyIfNotExists() {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setUserAuthenticationRequired(false)
                .setRandomizedEncryptionRequired(true)
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            keyGenerator.generateKey()
        }
    }

    /**
     * Шифрует строку используя ключ из Android Keystore
     * @param plainText Текст для шифрования
     * @return Зашифрованная строка в формате Base64
     */
    fun encrypt(plainText: String): String {
        try {
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            
            val iv = cipher.iv
            val cipherText = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            
            // Объединяем IV и зашифрованный текст
            val combined = ByteArray(iv.size + cipherText.size)
            System.arraycopy(iv, 0, combined, 0, iv.size)
            System.arraycopy(cipherText, 0, combined, iv.size, cipherText.size)
            
            return Base64.encodeToString(combined, Base64.DEFAULT)
        } catch (e: Exception) {
            throw SecurityException("Failed to encrypt data", e)
        }
    }

    /**
     * Расшифровывает строку используя ключ из Android Keystore
     * @param encryptedText Зашифрованная строка в формате Base64
     * @return Расшифрованный текст
     */
    fun decrypt(encryptedText: String): String {
        try {
            val combined = Base64.decode(encryptedText, Base64.DEFAULT)
            
            // Извлекаем IV и зашифрованный текст
            val iv = ByteArray(IV_LENGTH)
            val cipherText = ByteArray(combined.size - IV_LENGTH)
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH)
            System.arraycopy(combined, IV_LENGTH, cipherText, 0, cipherText.size)
            
            val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            
            val plainText = cipher.doFinal(cipherText)
            return String(plainText, Charsets.UTF_8)
        } catch (e: Exception) {
            throw SecurityException("Failed to decrypt data", e)
        }
    }

    /**
     * Хеширует пароль с использованием salt
     * @param password Пароль для хеширования
     * @return Хешированный пароль
     */
    fun hashPassword(password: String): String {
        // В реальном приложении используйте bcrypt или argon2
        // Это упрощенная версия для демонстрации
        val salt = "SpyBrain2024"
        val salted = password + salt
        return Base64.encodeToString(
            salted.toByteArray(Charsets.UTF_8).map { it.xor(0x5A) }.toByteArray(),
            Base64.DEFAULT
        )
    }

    /**
     * Очищает чувствительные данные из памяти
     * @param data Массив байтов для очистки
     */
    fun clearSensitiveData(data: ByteArray) {
        data.fill(0)
    }

    /**
     * Проверяет, является ли устройство рутованным
     * @return true если устройство рутовано
     */
    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        
        return paths.any { java.io.File(it).exists() }
    }
}