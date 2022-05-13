package com.lvlifeng.jenkinshelper.jenkins

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.lvlifeng.jenkinshelper.Bundle
import org.apache.commons.lang.StringUtils


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

    var jks = mutableSetOf<Jenkins>()
        get() = field

    override fun loadState(state: AccountState) {
        XmlSerializerUtil.copyBean<AccountState>(state, this)
    }

    companion object {
        val instance: AccountState
            get() = ServiceManager.getService(AccountState::class.java)

        val defaultAc: Jenkins
            get() = Jenkins(Bundle.message("defaultAccount"), "", "")

        fun addAccount(newJenkins: Jenkins, password: String, currentJenkins: Jenkins?): Boolean {
            if (StringUtils.isBlank(newJenkins.nickName)) {
                newJenkins.nickName = newJenkins.apiUrl
            }
            currentJenkins?.let {
                this.instance.removeAccount(currentJenkins)
            }
            this.instance.addAccount(newJenkins, password)
            return true
        }
    }

    override fun getState(): AccountState? {
        return this
    }

    fun addAccount(jk: Jenkins, password: String) {
        Credentials.saveCredential(jk.apiUrl + jk.userName, jk.userName, password)
        jks.add(jk)
    }

    fun removeAccount(jk: Jenkins) {
        Credentials.removeCredential(jk.apiUrl + jk.userName)
        jks.remove(jk)
    }

}