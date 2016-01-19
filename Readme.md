### AEM Tooling for IntelliJ IDE Plugin

#### Updates:

**0.6 Beta 3**: Fixed an issue with the Console Log Settings to being disabled if no configuration is selected.
Fixed issues with Windows paths. Fixed an issue with Force Deploy doesn't apply to parent folders.
Fixed an issue with Windows paths and File Changes.

**0.6 Beta 2**: Checking a Selected Configuration is renamed to **Run** as it indicates a persistence of the connection
and even this isn't the case it is persistent for deployments.
Also renamed the Reset Current Configuration to **Purge Plugin Cache Data**.
Adjusted some of the Icon's Enabled status to work as intended.

**0.6 Beta 1**: Added the support to create new Projects through Maven Archetypes (AEM or Sling). The Builder
is geared towards that and only shows the available Archetypes and let the user set the
required properties. The rest remains the same as creating a Project through the Maven Builder
and using archetypes there

**0.5-BETA-2** is available which now tries to perform a Maven build for OSGi Modules during the deployment. This feature
can be switched off (on is the default) in the Server Connection Configuration of the Plugin. If the Edit Button is
disabled make sure you stop the connection first.

**Attention**: There is a little problem with IntelliJ where the first Maven build from the AEM Plugin will fail. There
is an Alert that pops up and it will suggest that you try once more. During the initial tests that did solve the issue.

#### Introduction

This projects creates an IntelliJ IDE plugin for version 14 and up providing the same features as the Eclipse AEM Tooling provided by the Adobe / Sling team.

#### Installation

Please have a look at the AEM Tooling Plugin for IntelliJ IDEA documentation that you can find the the **documentation** folder right here. It will explain how to install the Plugin and for OSGi deployment the Felix SCR Annotation plugin as well as using the plugin.

### Notes

This README uses Google Analytics for tracking site visits using: [![Analytics](https://ga-beacon.appspot.com/UA-72395016-3/headwirecom/aem-ide-tooling-4-intellij/readme)](https://github.com/igrigorik/ga-beacon)