package com.lvlifeng.jenkinshelper.jenkins

import lombok.Getter
import lombok.Setter
import lombok.Singular
import lombok.Value
import lombok.experimental.NonFinal
import lombok.experimental.SuperBuilder

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-15 16:00
 */
@Getter
@Setter
@SuperBuilder
@Value
@NonFinal
class View {

    var name: String? = null

    var url: String? = null

    var isNested = false

    @Singular
    var subViews: List<View>? = null

    fun hasNestedView(): Boolean {
        return !subViews?.isEmpty()!!
    }
}