package com.lvlifeng.jenkinshelper.helper

import cn.hutool.core.collection.CollectionUtil
import com.lvlifeng.jenkinshelper.Bundle.message
import com.offbytwo.jenkins.model.Job
import org.apache.commons.lang.StringUtils
import java.util.*
import java.util.function.Predicate
import java.util.stream.Collectors

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-12 15:50
 */
class WindowHelper {

    companion object {
        fun filterJob(searchWord: String?, allJobs: List<Job>): List<Job> {
            return allJobs
                .stream()
                .filter(Predicate { o: Job ->
                    filterJobs(
                        o,
                        searchWord
                    )
                })
                .collect(Collectors.toList())
        }

        private fun filterJobs(o: Job, searchWord: String?): Boolean {
            var searchJobList: Set<String?> = HashSet()
            if (StringUtils.isNotBlank(searchWord)) {
                searchJobList = Arrays.stream(StringUtils.split(searchWord, message("commaSeparator"))).collect(
                    Collectors.toSet()
                )
            }
            return ((CollectionUtil.isNotEmpty(searchJobList)
                    && (searchJobList.size == 1 && o.name.toLowerCase().contains(ArrayList(searchJobList)[0]!!)
                    || searchJobList.size != 1 && searchJobList.stream().anyMatch { s: String? ->
                StringUtils.equals(
                    o.name.toLowerCase(),
                    s!!.toLowerCase()
                )
            })) || CollectionUtil.isEmpty(searchJobList))
        }
    }
}