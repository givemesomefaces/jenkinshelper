package com.lvlifeng.jenkinshelper.renderer

import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.components.JBLabel
import com.offbytwo.jenkins.model.Job
import javax.swing.Icon
import javax.swing.JList

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-15 12:05
 */
class JobListRenderer: ColoredListCellRenderer<Job>() {

    var theIcon: Icon? = null

    override fun customizeCellRenderer(
        list: JList<out Job>,
        value: Job?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
    ) {
//        val renderer = getListCellRendererComponent(list, value, index, selected, hasFocus)
        val details = value!!.details()
        if (details.lastBuild != null && details.lastFailedBuild != null && details.lastBuild.number == details.lastFailedBuild.number) {
            append("failed")
        }

    }


}