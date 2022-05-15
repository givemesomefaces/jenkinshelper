package com.lvlifeng.jenkinshelper.jenkins

import com.github.cliftonlabs.json_simple.JsonArray
import com.github.cliftonlabs.json_simple.JsonKey
import com.github.cliftonlabs.json_simple.JsonObject
import com.github.cliftonlabs.json_simple.Jsoner
import org.apache.commons.lang.StringUtils
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Function

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-15 16:14
 */
class JsonParser {

    var SERVER_DESCRIPTION = "description"
    var SERVER_URL = "url"
    var JOBS = "jobs"
    var JOB_NAME = "name"
    var JOB_FULL_NAME = "fullName"
    var JOB_DISPLAY_NAME = "displayName"
    var JOB_FULL_DISPLAY_NAME = "fullDisplayName"
    var JOB_HEALTH = "healthReport"
    var JOB_HEALTH_ICON = "iconUrl"
    var JOB_HEALTH_DESCRIPTION = "description"
    var JOB_URL = "url"
    var JOB_COLOR = "color"
    var JOB_LAST_BUILD = "lastBuild"
    var JOB_LAST_COMPLETED_BUILD = "lastCompletedBuild"
    var JOB_LAST_SUCCESSFUL_BUILD = "lastSuccessfulBuild"
    var JOB_LAST_FAILED_BUILD = "lastFailedBuild"
    var JOB_IS_BUILDABLE = "buildable"
    var JOB_IS_IN_QUEUE = "inQueue"
    var VIEWS = "views"
    var PRIMARY_VIEW = "primaryView"
    var VIEW_NAME = "name"
    var VIEW_URL = "url"
    var BUILDS = "builds"
    var BUILD_IS_BUILDING = "building"
    var BUILD_ID = "id"
    var BUILD_RESULT = "result"
    var BUILD_URL = "url"
    var BUILD_NUMBER = "number"
    var BUILD_DISPLAY_NAME = "displayName"
    var BUILD_FULL_DISPLAY_NAME = "fullDisplayName"
    var BUILD_TIMESTAMP = "timestamp"
    var BUILD_DURATION = "duration"
    var PARAMETER_PROPERTY = "property"
    var PARAMETER_DEFINITIONS = "parameterDefinitions"
    var PARAMETER_NAME = "name"
    var PARAMETER_DESCRIPTION = "description"
    var PARAMETER_TYPE = "type"
    var PARAMETER_DEFAULT_PARAM = "defaultParameterValue"
    var PARAMETER_DEFAULT_PARAM_VALUE = "value"
    var PARAMETER_CHOICE = "choices"
    var CLASS = "_class"
    var COMPUTER = "computer"
    var ACTIONS = "actions"
    var PARAMETERS = "parameters"

    private val workspaceDateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")

    companion object {
        private fun getBoolean(obj: Any): Boolean {
            return java.lang.Boolean.TRUE == obj
        }

        private fun parseJson(jsonData: String): JsonObject {
            return Jsoner.deserialize(jsonData, JsonObject())
        }

        private fun createJsonKey(key: String): JsonKey {
            return createJsonKey(key, null)
        }

        private fun createJsonKey(key: String, value: Any?): JsonKey {
            return Jsoner.mintJsonKey(key, value)
        }
    }

    fun createWorkspace(jsonData: String): JenkinsData? {
        checkJsonDataAndThrowExceptionIfNecessary(jsonData)
        val jsonObject: JsonObject = parseJson(jsonData)
        val primaryView: Optional<View> = Optional.ofNullable<Any>(jsonObject.get(PRIMARY_VIEW) as JsonObject).map(
            Function { viewObject: Any? -> this.getView(viewObject as JsonObject) })
        val description: String = jsonObject.getStringOrDefault(createJsonKey(SERVER_DESCRIPTION, ""))
        val jenkinsUrl: String = getServerUrl(jsonObject)
        val jenkins = JenkinsData(description, jenkinsUrl)
        primaryView.ifPresent { jenkins.primaryView = it }
        val viewsObject: JsonArray = jsonObject.get(VIEWS) as JsonArray
        if (viewsObject != null) {
            jenkins.views = getViews(viewsObject) as List<View>?
        }
        return jenkins
    }

    private fun getViews(viewsObjects: JsonArray): List<View?>? {
        val views: MutableList<View?> = LinkedList()
        for (obj in viewsObjects) {
            val viewObject = obj as JsonObject
            views.add(getView(viewObject))
        }
        return views
    }

    private fun getView(viewObject: JsonObject): View? {
        val viewBuilder: View.ViewBuilder<*, *> = View.builder()
        viewBuilder.isNested(false)
        val unknownViewName = "Unknown"
        viewBuilder.name(
            viewObject.getStringOrDefault(
                createJsonKey(
                    VIEW_NAME,
                    unknownViewName
                )
            )
        )
        val url = viewObject.getString(createJsonKey(VIEW_URL))
        viewBuilder.url(url)
        val subViewObjs = viewObject[VIEWS] as JsonArray?
        if (subViewObjs != null) {
            for (obj in subViewObjs) {
                val subviewObj = obj as JsonObject
                val nestedViewBuilder: View.ViewBuilder<*, *> = View.builder()
                nestedViewBuilder.isNested(true)
                val currentName = subviewObj.getStringOrDefault(
                    createJsonKey(
                        VIEW_NAME,
                        unknownViewName
                    )
                )
                nestedViewBuilder.name(currentName)
                val subViewUrl =
                    subviewObj.getString(JsonParser.createJsonKey(VIEW_URL))
                nestedViewBuilder.url(subViewUrl)
                viewBuilder.subView(nestedViewBuilder.build())
            }
        }
        return viewBuilder.build()
    }

    private fun getServerUrl(jsonObject: JsonObject): String {
        val primaryView: Optional<View> = Optional.ofNullable(jsonObject[PRIMARY_VIEW] as JsonObject?).map(
            Function { viewObject: JsonObject? ->
                getView(
                    viewObject!!
                )
            })
        val primaryViewUrl: String = primaryView.map<Any>(View::url).orElse("") as String
        return Optional.ofNullable(
            jsonObject.getStringOrDefault(
                JsonParser.createJsonKey(
                    SERVER_URL,
                    primaryViewUrl
                )
            )
        ).orElse(
            StringUtils.EMPTY
        )
    }

    private fun checkJsonDataAndThrowExceptionIfNecessary(jsonData: String) {
        if (StringUtils.isEmpty(jsonData) || "{}" == jsonData) {
            val message = "Empty JSON data!"
            throw Exception(message)
        }
    }
}