package com.lvlifeng.jenkinshelper.bean

import java.awt.Color
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants


/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-14 23:00
 */
class FontAttrib {

    /**
     * 属性集
     */
    /**
     * 属性集
     */
    var attrSet: SimpleAttributeSet? = SimpleAttributeSet()
        get() = field
        set(value) {
            field = value
        }


    /**
     * 要输入的文本和字体名称
     */
    var text: String? = null
        get() = field
        set(value) {
            field = value
        }

    var name: String? = null
        get() = field
        set(value) {
            field = value
        }


    /**
     * 样式和字号
     */
    var style = 0
        get() = field
        set(value) {
            field = value
        }

    var size: Int = 0
        get() = field
        set(value) {
            field = value
        }

    /**
     * 文字颜色和背景颜色
     */
    private var color: Color? = null
        get() = field
        set(value) {
            field = value
        }

    private var backColor: Color? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(text: String) {
        this.text = text
        this.name = "白色"
        this.size = 12
        val temp_style = "常规"
        if (temp_style == "常规") {
            this.style = GENERAL
        } else if (temp_style == "粗体") {
            this.style = BOLD
        } else if (temp_style == "斜体") {
            this.style = ITALIC
        } else if (temp_style == "粗斜体") {
            this.style = BOLD_ITALIC
        }
        val temp_color = "白色"
        if (temp_color == "黑色") {
            this.color = Color(0, 0, 0)
        } else if (temp_color == "红色") {
            this.color = Color(255, 0, 0)
        } else if (temp_color == "蓝色") {
            this.color = Color(0, 0, 255)
        } else if (temp_color == "黄色") {
            this.color = Color(255, 255, 0)
        } else if (temp_color == "绿色") {
            this.color = Color(0, 255, 0)
        }
        val temp_backColor = "无色"
        if (temp_backColor != "无色") {
            if (temp_backColor == "灰色") {
                this.backColor = Color(200, 200, 200)
            } else if (temp_backColor == "淡红") {
                this.backColor = Color(255, 200, 200)
            } else if (temp_backColor == "淡蓝") {
                this.backColor = Color(200, 200, 255)
            } else if (temp_backColor == "淡黄") {
                this.backColor = Color(255, 255, 200)
            } else if (temp_backColor == "淡绿") {
                this.backColor = Color(200, 255, 200)
            }
        }
    }

    /**
     * 一个空的构造（可当做换行使用）
     */
    fun FontAttrib() {}


    companion object {
        /**
         * 常规
         */
        val GENERAL = 0


        /**
         * 粗体
         */
        val BOLD = 1

        /**
         * 斜体
         */
        val ITALIC = 2


        /**
         * 粗斜体
         */
        val BOLD_ITALIC = 3
    }

    fun getSattrSet(): SimpleAttributeSet {
        attrSet = SimpleAttributeSet()
        if (name != null) {
            StyleConstants.setFontFamily(attrSet, name)
        }
        if (style == GENERAL) {
            StyleConstants.setBold(attrSet, false)
            StyleConstants.setItalic(attrSet, false)
        } else if (style == BOLD) {
            StyleConstants.setBold(attrSet, true)
            StyleConstants.setItalic(attrSet, false)
        } else if (style == ITALIC) {
            StyleConstants.setBold(attrSet, false)
            StyleConstants.setItalic(attrSet, true)
        } else if (style == BOLD_ITALIC) {
            StyleConstants.setBold(attrSet, true)
            StyleConstants.setItalic(attrSet, true)
        }
        StyleConstants.setFontSize(attrSet, size)
        if (color != null) StyleConstants.setForeground(attrSet, color)
        if (backColor != null) StyleConstants.setBackground(attrSet, backColor)
        return attrSet as SimpleAttributeSet
    }
}