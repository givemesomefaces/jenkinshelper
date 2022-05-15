/*
 * Copyright (c) 2013 David Boissier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lvlifeng.jenkinshelper.jenkins

import com.intellij.openapi.project.Project
import org.apache.commons.httpclient.util.URIUtil
import com.lvlifeng.jenkinshelper.jenkins.Jenkins
import com.lvlifeng.jenkinshelper.jenkins.JenkinsPlateform
import org.apache.commons.httpclient.URIException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.*

class UrlBuilder {
    //    public URL createRunJobUrl(String jobBuildUrl, Jenkins configuration) {
    //        try {
    //            String s = jobBuildUrl + URIUtil.encodePathQuery(String.format("%s?delay=%dsec", BUILD, configuration.getBuildDelay()));
    //            return new URL(s);
    //        } catch (Exception ex) {
    //            handleException(ex);
    //        }
    //        return null;
    //    }
    fun createStopBuildUrl(buildUrl: String): URL? {
        try { //http://jenkins.internal/job/it4em-it4em-DPD-GEOR-UAT-RO/27/stop
            return URL(buildUrl + URIUtil.encodePath("stop"))
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun createJenkinsWorkspaceUrl(configuration: Jenkins): URL? {
        try {
            return URL(URIUtil.encodePathQuery(configuration.apiUrl + API_JSON + TREE_PARAM + BASIC_JENKINS_INFO))
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun createViewUrl(jenkinsPlateform: JenkinsPlateform, viewUrl: String): URL? {
        var basicViewInfo = BASIC_VIEW_INFO
        if (JenkinsPlateform.CLOUDBEES == jenkinsPlateform) {
            basicViewInfo = CLOUDBEES_VIEW_INFO
        }
        try {
            return URL(viewUrl + URIUtil.encodePathQuery(API_JSON + TREE_PARAM + basicViewInfo))
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun createJobUrl(jobUrl: String): URL? {
        try {
            return URL(jobUrl + URIUtil.encodePathQuery(API_JSON + TREE_PARAM + BASIC_JOB_INFO))
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun createBuildUrl(buildUrl: String): URL? {
        try {
            return URL(buildUrl + URIUtil.encodePathQuery(API_JSON + TREE_PARAM + BASIC_BUILD_INFO))
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun createBuildsUrl(buildUrl: String): URL? {
        try {
            return URL(buildUrl + URIUtil.encodePathQuery(API_JSON + TREE_PARAM + BASIC_BUILDS_INFO))
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun createRssLatestUrl(serverUrl: String): URL? {
        try {
            return URL(serverUrl + RSS_LATEST)
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun createAuthenticationUrl(serverUrl: String): URL? {
        try {
            return URL(serverUrl + URIUtil.encodePathQuery(API_JSON + TEST_CONNECTION_REQUEST))
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun createServerUrl(serverUrl: String): URI? {
        try {
            return URL(getBaseUrl(serverUrl)).toURI()
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    private fun handleException(ex: Exception) {
        if (ex is MalformedURLException) {
            throw IllegalArgumentException("URL is malformed", ex)
        } else if (ex is URIException) {
            throw IllegalArgumentException(ERROR_DURING_URL_CREATION, ex)
        }
    }

    fun createNestedJobUrl(currentJobUrl: String): URL? {
        try {
            return URL(currentJobUrl + URIUtil.encodePathQuery(API_JSON + TREE_PARAM + NESTED_JOBS_INFO))
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun createComputerUrl(serverUrl: String): URL {
        return try {
            URL(
                serverUrl + URIUtil.encodePathQuery(
                    COMPUTER + API_JSON + TREE_PARAM +
                            COMPUTER_INFO
                )
            )
        } catch (ex: Exception) {
            handleException(ex)
            throw IllegalArgumentException(ERROR_DURING_URL_CREATION, ex)
        }
    }

    fun createConfigureUrl(serverUrl: String): URL {
        return try {
            URL(removeTrailingSlash(serverUrl) + "/configure")
        } catch (ex: Exception) {
            handleException(ex)
            throw IllegalArgumentException(ERROR_DURING_URL_CREATION, ex)
        }
    }

    fun toUrl(url: String): URL? {
        try {
            return URL(url)
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    fun removeTrailingSlash(url: String): String {
        val withoutTrailingSlash: String
        withoutTrailingSlash = if (url.endsWith("/")) {
            url.substring(0, url.length - 1)
        } else {
            url
        }
        return withoutTrailingSlash
    }

    fun createFillValueItemsUrl(jobUrl: String, className: String?, param: String?): URL? {
        try {
            return URL(jobUrl + URIUtil.encodePathQuery(String.format(FILL_VALUE_ITEMS, className, param)))
        } catch (ex: Exception) {
            handleException(ex)
        }
        return null
    }

    companion object {
        private const val API_JSON = "/api/json"
        private const val BUILD = "/build"
        private const val RSS_LATEST = "/rssLatest"
        private const val TREE_PARAM = "?tree="
        private const val URL = "url"
        private const val BASIC_JENKINS_INFO =
            URL + ",description,nodeName,nodeDescription,primaryView[name,url],views[name,url,views[name,url]]"
        private const val BASIC_BUILD_INFO = URL + ",id,building,result,number,displayName,fullDisplayName," +
                "timestamp,duration,actions[parameters[name,value]]"
        private const val BASIC_JOB_INFO =
            "name,fullName,displayName,fullDisplayName,jobs," + URL + ",color,buildable,inQueue,healthReport[description,iconUrl],lastBuild[" + BASIC_BUILD_INFO + "],lastFailedBuild[" + URL + "],lastSuccessfulBuild[" + URL + "],property[parameterDefinitions[name,type,defaultParameterValue[value],description,choices]]"
        private const val BASIC_VIEW_INFO = "name," + URL + ",jobs[" + BASIC_JOB_INFO + "]"
        private const val CLOUDBEES_VIEW_INFO = "name," + URL + ",views[jobs[" + BASIC_JOB_INFO + "]]"
        private const val TEST_CONNECTION_REQUEST = "?tree=nodeName,url,description,primaryView[name,url]"
        private const val BASIC_BUILDS_INFO = "builds[" + BASIC_BUILD_INFO + "]"
        private const val NESTED_JOBS_INFO = URL + "name,displayName,fullDisplayName,jobs[" + BASIC_JOB_INFO + "]"
        private const val COMPUTER = "/computer"
        private const val COMPUTER_INFO = "computer[displayName,description,offline,assignedLabels[name]]"

        /**
         * in git-parameter-plugin
         * also field 'allValueItems' could be used: property[parameterDefinitions[name,type,defaultParameterValue[value],description,allValueItems]]
         */
        private const val FILL_VALUE_ITEMS = "descriptorByName/%s/fillValueItems?param=%s"
        private const val ERROR_DURING_URL_CREATION = "Error during URL creation"
        fun getInstance(project: Project): UrlBuilder {
            return Optional.ofNullable(project.getService(UrlBuilder::class.java))
                .orElseGet { UrlBuilder() }
        }

        fun getBaseUrl(url: String): String {
            return url.replace("\\b/job\\b/.+|/\\bview\\b/.+".toRegex(), "")
        }
    }
}