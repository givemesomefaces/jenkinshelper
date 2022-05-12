package com.lvlifeng.jenkinshelper.jenkins

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.lvlifeng.jenkinshelper.Bundle
import org.jetbrains.annotations.Nullable


/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-12 22:17
 */
class Credentials {

    companion object {


        fun getPassword(urlAndUserName: String): @Nullable String? {
//        val key: String? = null // e.g. serverURL, accountID
            val credentialAttributes = createCredentialAttributes(urlAndUserName)

//        val credentials: @Nullable Credentials? = credentialAttributes?.let { PasswordSafe.instance.get(it) }
//        if (credentials != null) {
//            val password: @NotNull String? = credentials.getPasswordAsString()
//        }
            return credentialAttributes?.let { PasswordSafe.instance.getPassword(it) }
        }

        fun getCredentials(urlAndUserName: String): @Nullable Credentials? {
            val credentialAttributes = createCredentialAttributes(urlAndUserName)
            return credentialAttributes?.let { PasswordSafe.instance.get(it) }
//            if (credentials != null) {
//                val password = credentials.getPasswordAsString()
//            }
//            val password: String? = credentialAttributes?.let { PasswordSafe.instance.getPassword(it) }
        }

        fun saveCredential(urlAndUserName: String, username: String?, password: String?) {
            val credentialAttributes = createCredentialAttributes(urlAndUserName) // see previous sample
            val credentials = Credentials(username, password)
            credentialAttributes?.let { PasswordSafe.instance.set(it, credentials) }
        }

        fun removeCredential(urlAndUserName: String) {
            val credentialAttributes = createCredentialAttributes(urlAndUserName) // see previous sample
//            val credentials = Credentials(username, password)
            credentialAttributes?.let { PasswordSafe.instance.set(it, null) }
        }

        private fun createCredentialAttributes(key: String): CredentialAttributes? {
            return CredentialAttributes(
                generateServiceName(Bundle.message("subSystem"), key)
            )
        }
    }
}