package com.lvlifeng.jenkinshelper.bean

import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import org.apache.commons.lang.StringUtils
import javax.swing.JTextArea

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-10 13:38
 */
class Jpopup(values: List<*>?, var logTextarea: JTextArea? = null) : BaseListPopupStep<Any?>(null, values) {

    override fun onChosen(selectedValue: Any?, finalChoice: Boolean): PopupStep<*>? {
        if (StringUtils.equalsIgnoreCase(selectedValue.toString(), "Clear log")) {
            doFinalStep {
                logTextarea!!.text = ""
            }
        }
        return super.onChosen(selectedValue, finalChoice)
    }
}