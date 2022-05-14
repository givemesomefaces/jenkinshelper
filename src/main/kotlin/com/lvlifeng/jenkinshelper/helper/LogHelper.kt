package com.lvlifeng.jenkinshelper.helper

import cn.hutool.core.date.DateUtil
import com.lvlifeng.jenkinshelper.Bundle
import com.lvlifeng.jenkinshelper.bean.FontAttrib
import javax.swing.JTextPane

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-14 22:46
 */
class LogHelper {

    companion object {
        fun append(textPane: JTextPane, log: String) {
            val doc = textPane.styledDocument
            var attr = FontAttrib(DateUtil.now() + "  " + log + Bundle.message("newLine"))
            attr?.let {
                doc.insertString(doc.length, attr.text, attr.getSattrSet())
                textPane.caretPosition = doc.length
            }
        }
    }
}