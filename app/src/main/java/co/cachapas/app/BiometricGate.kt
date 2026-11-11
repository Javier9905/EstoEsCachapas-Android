package co.cachapas.app

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** La clave se usa únicamente tras autenticación biométrica del sistema. No se leen huellas. */
class BiometricGate(private val context: Context) {
 private val prefs = context.getSharedPreferences("biometric", Context.MODE_PRIVATE)
 private val alias = "cachapas_local_session"
 fun available() = BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
 fun enabled(uid: String?) = uid != null && prefs.getString("uid", null) == uid && prefs.contains("payload")
 fun clear() { prefs.edit().clear().apply(); val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }; if (ks.containsAlias(alias)) ks.deleteEntry(alias) }
 private fun key(create: Boolean): SecretKey {
  val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
  if (create) {
   if (ks.containsAlias(alias)) ks.deleteEntry(alias)
   val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
   generator.init(KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
    .setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
    .setUserAuthenticationRequired(true).setInvalidatedByBiometricEnrollment(true).build())
   return generator.generateKey()
  }
  return ks.getKey(alias, null) as SecretKey
 }
 fun enable(activity: FragmentActivity, uid: String, result: (String?) -> Unit) {
  runCatching {
   require(available()) { "Configura una huella o biometría fuerte en el dispositivo para activar esta opción." }
   val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, key(true)) }
   prompt(activity, cipher, { c ->
    runCatching {
     val payload = c.doFinal(uid.toByteArray(Charsets.UTF_8))
     prefs.edit().putString("uid",uid).putString("iv",Base64.encodeToString(c.iv, Base64.NO_WRAP)).putString("payload",Base64.encodeToString(payload, Base64.NO_WRAP)).apply()
    }.fold({ result(null) }, { result("No fue posible activar la biometría. ${it.message}") })
   }, result)
  }.onFailure { result(it.message ?: "Biometría no disponible.") }
 }
 fun unlock(activity: FragmentActivity, expectedUid: String, result: (String?) -> Unit) {
  runCatching {
   require(available() && enabled(expectedUid)) { "Ingresa con tu correo y contraseña y activa la biometría desde Perfil." }
   val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
    init(Cipher.DECRYPT_MODE, key(false), GCMParameterSpec(128, Base64.decode(prefs.getString("iv", ""), Base64.NO_WRAP)))
   }
   prompt(activity, cipher, { c -> runCatching {
    val uid = String(c.doFinal(Base64.decode(prefs.getString("payload", ""), Base64.NO_WRAP)), Charsets.UTF_8)
    require(uid == expectedUid) { "La cuenta guardada cambió. Ingresa con contraseña." }
   }.fold({ result(null) }, { result("La sesión biométrica requiere activarse de nuevo.") }) }, result)
  }.onFailure { result("Ingresa con contraseña y vuelve a activar la biometría. ${it.message.orEmpty()}") }
 }
 private fun prompt(activity: FragmentActivity, cipher: Cipher, ok: (Cipher) -> Unit, error: (String?) -> Unit) {
  val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), object: BiometricPrompt.AuthenticationCallback() {
   override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) { val c = result.cryptoObject?.cipher; if (c != null) ok(c) else error("La autenticación no devolvió una clave válida.") }
   override fun onAuthenticationError(errorCode: Int, errString: CharSequence) { error(errString.toString()) }
  })
  prompt.authenticate(BiometricPrompt.PromptInfo.Builder().setTitle(context.getString(R.string.biometric_title)).setSubtitle(context.getString(R.string.biometric_subtitle)).setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG).setNegativeButtonText(context.getString(R.string.cancel)).build(), BiometricPrompt.CryptoObject(cipher))
 }
}
