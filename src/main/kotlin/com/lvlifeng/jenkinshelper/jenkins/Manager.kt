package com.lvlifeng.jenkinshelper.jenkins

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.model.Job
import java.util.*
import java.util.function.Supplier

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-15 15:50
 */
class Manager {

    private val BUILDHIVE_CLOUDBEES = "buildhive"

    private val project: Project? = null

    private var urlBuilder: UrlBuilder? = null
    private val rssParser: RssParser = RssParser()
    private val jsonParser: JenkinsParser = JenkinsJsonParser()
    private val securityClient: SecurityClient? = null
    private var jenkinsPlateform = JenkinsPlateform.CLASSIC
    private val jenkinsServer: JenkinsServer? = null

    fun RequestManager(project: Project?) {
        this.project = project
        urlBuilder = UrlBuilder.getInstance(project!!)
    }

    fun getInstance(project: Project): RequestManager? {
        return Optional.ofNullable(project.getService(RequestManager::class.java))
            .orElseGet(Supplier { RequestManager(project) })
    }

    private fun canContainNestedJobs(job: Job): Boolean {
        return job.getJobType().containNestedJobs()
    }

    fun loadJenkinsWorkspace(configuration: JenkinsAppSettings): Jenkins? {
        if (handleNotYetLoggedInState()) return null
        val url = urlBuilder!!.createJenkinsWorkspaceUrl(configuration)
        val jenkinsWorkspaceData: String = securityClient.execute(url)
        jenkinsPlateform =
            if (configuration.getServerUrl().contains(BUILDHIVE_CLOUDBEES)) { //TODO hack need to refactor
                JenkinsPlateform.CLOUDBEES
            } else {
                JenkinsPlateform.CLASSIC
            }
        val jenkins: Jenkins = jsonParser.createWorkspace(jenkinsWorkspaceData)
        val validationResult: ConfigurationValidator.ValidationResult = ConfigurationValidator.getInstance(project)
            .validate(configuration.getServerUrl(), jenkins.getServerUrl())
        if (!validationResult.isValid()) {
            throw ConfigurationException(validationResult.getFirstError())
        }
        return jenkins
    }
}