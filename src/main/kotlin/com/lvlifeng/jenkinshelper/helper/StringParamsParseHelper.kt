package com.lvlifeng.jenkinshelper.helper

import cn.hutool.core.collection.CollectionUtil
import org.apache.commons.lang.StringUtils
import java.util.*

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-09 16:59
 */
class StringParamsParseHelper {

    companion object {
        fun getParamsMap(paramsStr: String): HashMap<String, String> {
            val paramsMap: HashMap<String, String> = HashMap<String, String>()
            if (StringUtils.isNotBlank(paramsStr)) {
                val paramArr = StringUtils.split(paramsStr, ",，")
                if (CollectionUtil.isNotEmpty(listOf(*paramArr))) {
                    listOf(*paramArr).stream().forEach { o: String ->
                        val params = listOf(*o.split("=").toTypedArray())
                        if (CollectionUtil.isNotEmpty(params)) {
                            paramsMap.putIfAbsent(params[0], params[params.size - 1])
                        }
                    }
                }
            }
            return paramsMap

        }
    }
}