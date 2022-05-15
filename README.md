# JenkinsHelper

![Build](https://github.com/Lv-lifeng/JenkinsHelper/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/19155)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/19155)

<!-- Plugin description -->
### Supports selecting multiple jobs at once to operate for jenkins 
The JenkinsHelper window is at the bottom of the ide. it support select multiple jobs to operate.
* build   
  * build with params
  * circular build if last build was failed 
  * choose last build failed jobs to build
* update
  * git branch name
  * update value of string params
  * add new string param
* error log
  * filter error log 

![O6iPjU.gif](https://s1.ax1x.com/2022/05/14/O6iPjU.gif)
<!-- Plugin description end -->

### TODO LIST
1. Job list add build status and health status 
2. Support more parameter types
3. ...
## Installation

- Using IDE built-in plugin system:

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "JenkinsHelper"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/Lv-lifeng/JenkinsHelper/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
