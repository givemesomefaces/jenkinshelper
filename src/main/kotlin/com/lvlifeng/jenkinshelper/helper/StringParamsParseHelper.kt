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
                if (CollectionUtil.isNotEmpty(Arrays.asList(*paramArr))) {
                    Arrays.asList(*paramArr).stream().forEach { o: String ->
                        val parames = Arrays.asList(*o.split("=").toTypedArray())
                        if (CollectionUtil.isNotEmpty(parames)) {
                            paramsMap.putIfAbsent(parames[0], parames[parames.size - 1])
                        }
                    }
                }
            }
            return paramsMap

        }
    }
}