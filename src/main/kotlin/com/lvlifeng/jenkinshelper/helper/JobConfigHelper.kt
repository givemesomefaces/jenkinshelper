package com.lvlifeng.jenkinshelper.helper

import cn.hutool.core.collection.CollectionUtil
import com.lvlifeng.jenkinshelper.bean.StringParamsConfig
import com.lvlifeng.jenkinshelper.bean.UpdateConfig
import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.Job
import org.apache.commons.lang.StringUtils
import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.dom4j.Node
import java.util.function.Consumer

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-10 10:50
 */
class JobConfigHelper {

    companion object {

        fun addParams(config: StringParamsConfig, jk: JenkinsServer, jb: Job) {
            jk.addStringParam(jb.name, config.name, config.description, config.defaultValue)
        }

        fun updateJobConfig(config: UpdateConfig, jk: JenkinsServer, jb: Job) {
            var jobXml = jk.getJobXml(jb.name)
            var document = DocumentHelper.parseText(jobXml)
            var updateBranch = updateGitBranch(document, config.newGitBranchName)
            var updateParams = updateStringParams(document, config.stringParamsMap)
            if (updateBranch or updateParams) {
                jk.updateJob(jb.name, document.rootElement.asXML(), true)
            }

        }

        private fun updateStringParams(document: Document, stringParamsMap: HashMap<String, String>?): Boolean {
            var update = false
            if (stringParamsMap == null) {
                return update
            }
            val nodes: List<Node?> = document.rootElement.selectNodes(
                "//properties" +
                        "/hudson.model.ParametersDefinitionProperty" +
                        "/parameterDefinitions" +
                        "/hudson.model.StringParameterDefinition"
            )
            if (CollectionUtil.isNotEmpty(nodes)) {
                nodes.stream().forEach { node: Node? ->
                    val parent = node!!.parent
                    val elements = parent.elements()
                    if (CollectionUtil.isNotEmpty(elements)) {
                        elements.forEach(Consumer { element: Element? ->
                            if (element != null) {
                                val name = element.element("name")
                                if (name != null && stringParamsMap.keys.contains(name.text)) {
                                    if (StringUtils.isNotBlank(stringParamsMap[name.text])) {
                                        val defaultValue = element.element("defaultValue")
                                        if (defaultValue != null) {
                                            defaultValue.text = stringParamsMap[name.text]
                                        } else {
                                            val newDefaultValue = element.addElement("defaultValue")
                                            newDefaultValue.text = stringParamsMap[name.text]
                                        }
                                        update = true
                                    }
                                }
                            }
                        })
                    }
                }
            }
            return update
        }

        private fun updateGitBranch(document: Document, newGitBranchName: String?): Boolean {
            var update = false
            if (StringUtils.isBlank(newGitBranchName)) {
                return update
            }
            val nodes: List<Node?> = document.rootElement.selectNodes("//hudson.plugins.git.BranchSpec")
            if (CollectionUtil.isNotEmpty(nodes)) {
                nodes.stream().forEach { node: Node? ->
                    val parent = node!!.parent
                    val elements = parent.elements()
                    if (CollectionUtil.isNotEmpty(elements)) {
                        elements.forEach(Consumer { element: Element? ->
                            if (element != null) {
                                val name = element.element("name")
                                if (name != null) {
                                    name.text = newGitBranchName
                                    update = true
                                }
                            }
                        })
                    }
                }
            }
            return update
        }
    }
}