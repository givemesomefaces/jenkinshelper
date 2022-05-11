package com.lvlifeng.jenkinshelper.bean

import javax.swing.table.DefaultTableModel

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-11 23:53
 */
class ReadOnlyTableModel : DefaultTableModel {

    constructor(data: Array<Array<Any?>?>?, columnNames: Array<Any?>?) : super(data, columnNames) {}

    override fun isCellEditable(row: Int, column: Int): Boolean {
        return false
    }
}