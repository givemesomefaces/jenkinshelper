package com.lvlifeng.jenkinshelper.jenkins

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-08 03:30
 */
@State(name = "AccountState", storages = [Storage("\$APP_CONFIG$/jenkins-helper-settings-persistentstate.xml")])
class AccountState : PersistentStateComponent<AccountState?> {

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
}