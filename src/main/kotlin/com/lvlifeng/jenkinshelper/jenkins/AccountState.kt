package com.lvlifeng.jenkinshelper.jenkins

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.lvlifeng.jenkinshelper.Bundle
import com.lvlifeng.jenkinshelper.jenkins.Jenkins.Companion.server
import com.offbytwo.jenkins.JenkinsServer
import org.apache.commons.lang3.StringUtils
import org.jetbrains.annotations.Nullable


/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-08 03:30
 */
@State(name = "AccountState", storages = [Storage("\$APP_CONFIG$/jenkins-helper-settings-persistentstate.xml")])
class AccountState : PersistentStateComponent<AccountState> {

    private var nickName: String? = null
        get() = field
        set(value) {
            field = value
        }
    private var apiUrl: String? = null
        get() = field
        set(value) {
            field = value
        }
    private var userName: String? = null
        get() = field
        set(value) {
            field = value
        }
    private var password: String? = null
        get() = field
        set(value) {
            field = value
        }

    var jks = mutableSetOf<Jenkins>()
        get() = field

    override fun loadState(state: AccountState) {
        XmlSerializerUtil.copyBean<AccountState>(state, this)
    }

    companion object {
        val instance: AccountState
            get() = ServiceManager.getService(AccountState::class.java)

        val defaultAc: Jenkins
            get() = Jenkins(Bundle.message("defaultAccount"), "", "","")

        fun addAccount(newJenkins: Jenkins, currentJenkins: Jenkins?): Boolean {
            val newJenkinsServer: JenkinsServer = server(newJenkins)
            val version = newJenkinsServer.version
            if (StringUtils.isBlank(version.literalVersion) || version.literalVersion === "-1") {
                return false
            }
            if (null != currentJenkins) {
                this.instance.removeAccount(currentJenkins)
            }
            this.instance.addAccount(newJenkins)
            return true
        }
        fun validAccount(jenkins: Jenkins): Boolean {
            val server = server(jenkins)
            val version = server.version
            if (StringUtils.isBlank(version.literalVersion) || version.literalVersion === "-1") {
                return false
            }
            jenkins.server = server
            return true
        }
    }

    override fun getState(): AccountState? {
        return this
    }

    fun addAccount(jk: Jenkins) {
        removeAccount(jk)
        jks.add(jk)
    }

    fun removeAccount(jk: Jenkins) {
        jks.remove(jk)
    }

    fun getPassword(urlAndUserName: String): @Nullable String? {
//        val key: String? = null // e.g. serverURL, accountID
        val credentialAttributes = createCredentialAttributes(urlAndUserName)

//        val credentials: @Nullable Credentials? = credentialAttributes?.let { PasswordSafe.instance.get(it) }
//        if (credentials != null) {
//            val password: @NotNull String? = credentials.getPasswordAsString()
//        }
        return credentialAttributes?.let { PasswordSafe.instance.getPassword(it) }
    }

    private fun createCredentialAttributes(key: String): CredentialAttributes? {
        return CredentialAttributes(generateServiceName(Bundle.message("subSytem"), key)
        )
    }
    fun savePassword() {
        val credentialAttributes = createCredentialAttributes(serverId) // see previous sample
        val credentials = Credentials(username, password)
        getInstance.getInstance().set(credentialAttributes, credentials)
    }

}