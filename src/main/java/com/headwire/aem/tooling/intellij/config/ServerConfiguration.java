package com.headwire.aem.tooling.intellij.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by schaefa on 3/19/15.
 */
//@State(
//name = "SlingServerConfiguration",
//    storages = {
//        @Storage(file = StoragePathMacros.WORKSPACE_FILE),
//        @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/slingServerConfiguration.xml", scheme = StorageScheme.DIRECTORY_BASED)
//    }
//)
public class ServerConfiguration
    implements PersistentStateComponent<ServerConfiguration>
{

    public static final String DEFAULT_DESCRIPTION = "No Description";
    public static final String DEFAULT_NAME = "Default Configuration";
    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_CONNECTION_PORT = 4502;
    public static final int DEFAULT_DEBUG_CONNECTION_PORT = 30303;
    public static final String DEFAULT_USER_NAME = "admin";
    public static final String DEFAULT_CONTEXT_PATH = "/";
    public static final int DEFAULT_START_CONNECTION_TIMEOUT_IN_SECONDS = 30;
    public static final int DEFAULT_STOP_CONNECTION_TIMEOUT_IN_SECONDS = 15;
    public static final PublishType DEFAULT_PUBLISH_TYPE = PublishType.automaticallyOnChange;
    public static final InstallationType DEFAULT_INSTALL_TYPE = InstallationType.installViaBundleUpload;
    public static final ServerStatus DEFAULT_SERVER_STATUS = ServerStatus.notConnected;
    public static final SynchronizationStatus DEFAULT_SERVER_SYNCHRONIZATION_STATUS = SynchronizationStatus.notChecked;

    protected static final String COMPONENT_NAME = "ServerConfiguration";

    public enum PublishType {never, automaticallyOnChange, getAutomaticallyOnBuild};
    public enum InstallationType {installViaBundleUpload, installFromFilesystem};
    public enum SynchronizationStatus {
        /** Module was not checked against Sling server **/
        notChecked("not checked"),
        /** Module Deployment or Synchronization failed **/
        failed,
        /** Bundle was successfully deployed **/
        bundleDeployed("deployed"),
        /** Content Module is synchronized with Sling server **/
        upToDate("synchronized"),
        /** Content Module is out of data (needs synchronization) **/
        outdated("out of date"),
        /** Module is not part of the build **/
        excluded,
        /** Module is not supported but is still part of the build **/
        unsupported;

        private String name;

        SynchronizationStatus() {
            this.name = name();
        }

        SynchronizationStatus(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    }

    public enum ServerStatus {
//        notConnected("not connected"), connecting, connected, disconnecting, disconnected, failed, upToDate("synchronized"), outdated("out dated");
        notConnected("not connected"), connecting, connected, disconnecting, disconnected, failed;

        private String name;

        ServerStatus() {
            this.name = name();
        }

        ServerStatus(String name) {
            this.name = name;
        }

        public String getName() { return name; }
    };

    private String name = "";
    private String host = "";
    private String description = "";
    private int connectionPort = DEFAULT_CONNECTION_PORT;
    private int connectionDebugPort = DEFAULT_DEBUG_CONNECTION_PORT;
    private String userName = DEFAULT_USER_NAME;
    private char[] password = DEFAULT_USER_NAME.toCharArray();
    private String contextPath = DEFAULT_CONTEXT_PATH;
    private int startConnectionTimeout = DEFAULT_START_CONNECTION_TIMEOUT_IN_SECONDS;
    private int stopConnectionTimeout = DEFAULT_STOP_CONNECTION_TIMEOUT_IN_SECONDS;
    private PublishType publishType = DEFAULT_PUBLISH_TYPE;
    private InstallationType installationType = DEFAULT_INSTALL_TYPE;

    // Don't store Server Status as it is reset when the Configuration is loaded again
    //AS TODO: Not sure about this -> Check if that works
    private transient ServerStatus serverStatus = DEFAULT_SERVER_STATUS;
    private transient SynchronizationStatus synchronizationStatus = DEFAULT_SERVER_SYNCHRONIZATION_STATUS;
    // Modules must be stored because they carry the info if a project is part of the deployment build
    private List<Module> moduleList = new ArrayList<Module>();

    public ServerConfiguration() {
    }

    /** Copy Constructor **/
    public ServerConfiguration(ServerConfiguration source) {
        copy(source);
    }

    void copy(ServerConfiguration source) {
        name = source.name;
        host = source.host;
        description = source.description;
        connectionPort = source.connectionPort;
        connectionDebugPort = source.connectionDebugPort;
        userName = source.userName;
        password = source.password;
        contextPath = source.contextPath;
        startConnectionTimeout = source.startConnectionTimeout;
        stopConnectionTimeout = source.stopConnectionTimeout;
        publishType = source.publishType;
        installationType = source.installationType;
        serverStatus = source.serverStatus;
    }

    public String verify() {
        String ret = null;
        // First make sure all mandatory fields are provided
        if(StringUtils.isBlank(name)) {
            ret = "server.configuration.missing.name";
        } else if(StringUtils.isBlank(host)) {
            ret = "server.configuration.missing.host";
        } else if(connectionPort <= 0) {
            ret = "server.configuration.invalid.port";
        } else if(connectionDebugPort <= 0 || connectionPort == connectionDebugPort) {
            ret = "server.configuration.invalid.debug.port";
        } else if(StringUtils.isBlank(userName)) {
            ret = "server.configuration.missing.user.name";
        } else if(StringUtils.isBlank(contextPath)) {
            ret = "server.configuration.missing.context.path";
        } else if(!contextPath.startsWith("/")) {
            ret = "server.configuration.invalid.context.path";
        }
        return ret;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.isNotBlank(name) ? name : DEFAULT_NAME;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = StringUtils.isNotBlank(host) ? host : DEFAULT_HOST;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.isNotBlank(description) ? description : DEFAULT_DESCRIPTION;
    }

    public int getConnectionPort() {
        return connectionPort;
    }

    public void setConnectionPort(int connectionPort) {
        this.connectionPort = connectionPort > 0 ? connectionPort : DEFAULT_CONNECTION_PORT;
    }

    public int getConnectionDebugPort() {
        return connectionDebugPort;
    }

    public void setConnectionDebugPort(int connectionDebugPort) {
        this.connectionDebugPort = connectionDebugPort > 0 ? connectionDebugPort : DEFAULT_DEBUG_CONNECTION_PORT;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = StringUtils.isNotBlank(userName) ? userName : DEFAULT_USER_NAME;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password == null || password.length == 0 ? DEFAULT_USER_NAME.toCharArray() : password;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = StringUtils.isNotBlank(contextPath) ? contextPath : DEFAULT_CONTEXT_PATH;
    }

    public int getStartConnectionTimeoutInSeconds() {
        return startConnectionTimeout;
    }

    public void setStartConnectionTimeoutInSeconds(int startConnectionTimeoutInSeconds) {
        this.startConnectionTimeout = startConnectionTimeoutInSeconds > 0 ?
            startConnectionTimeoutInSeconds :
            DEFAULT_START_CONNECTION_TIMEOUT_IN_SECONDS;
    }

    public int getStopConnectionTimeoutInSeconds() {
        return stopConnectionTimeout;
    }

    public void setStopConnectionTimeoutInSeconds(int stopConnectionTimeoutInSeconds) {
        this.stopConnectionTimeout = stopConnectionTimeoutInSeconds > 0 ?
            stopConnectionTimeoutInSeconds :
            DEFAULT_STOP_CONNECTION_TIMEOUT_IN_SECONDS;
    }

    public PublishType getPublishType() {
        return publishType;
    }

    public void setPublishType(PublishType publishType) {
        this.publishType = publishType != null ? publishType : DEFAULT_PUBLISH_TYPE;
    }

    public InstallationType getInstallationType() {
        return installationType;
    }

    public void setInstallationType(InstallationType installationType) {
        this.installationType = installationType != null ? installationType : DEFAULT_INSTALL_TYPE;
    }

    public ServerStatus getServerStatus() { return serverStatus; }

    public void setServerStatus(ServerStatus serverStatus) {
        this.serverStatus = serverStatus != null ? serverStatus : DEFAULT_SERVER_STATUS;
    }

    public SynchronizationStatus getSynchronizationStatus() {
        return synchronizationStatus;
    }

    public void setSynchronizationStatus(SynchronizationStatus synchronizationStatus) {
        this.synchronizationStatus = synchronizationStatus != null ? synchronizationStatus : DEFAULT_SERVER_SYNCHRONIZATION_STATUS;
    }

    @Nullable
    @Override
    public ServerConfiguration getState() {
        return this;
    }

    public List<Module> getModuleList() { return moduleList; }

    public Module obtainModuleBySymbolicName(String symbolicName) {
        Module ret = null;
        if(symbolicName != null) {
            for(Module module : moduleList) {
                if(symbolicName.equals(module.getSymbolicName())) {
                    ret = module;
                    break;
                }
            }
        }
        return ret;
    }

    public boolean addModule(Module module) {
        boolean ret = false;
        Module existing = obtainModuleBySymbolicName(name);
        if(existing == null) {
            moduleList.add(module);
            ret = true;
        }
        return ret;
    }

    public Module addModule(Project project, MavenProject mavenProject) {
        Module ret = obtainModuleBySymbolicName(name);
        if(ret == null) {
            ret = new Module(this, project, mavenProject);
            moduleList.add(ret);
        }
        return ret;
    }

    @Override
    public void loadState(ServerConfiguration serverConfiguration) {
        XmlSerializerUtil.copyBean(serverConfiguration, this);
    }

    public static class Module {
        private ServerConfiguration parent;
        private String symbolicName;
        private boolean partOfBuild = true;
        private transient Project project;
        private transient MavenProject mavenProject;
        private transient SynchronizationStatus status = SynchronizationStatus.notChecked;

        public Module(@NotNull ServerConfiguration parent, @NotNull String symbolicName, boolean partOfBuild) {
            this.parent = parent;
            this.symbolicName = symbolicName;
            setPartOfBuild(partOfBuild);
        }

        private Module(@NotNull ServerConfiguration parent, @NotNull Project project, @NotNull MavenProject mavenProject) {
            this.parent = parent;
            this.symbolicName = getSymbolicName(mavenProject);
            rebind(project, mavenProject);
        }

        public static String getSymbolicName(MavenProject project) {
            return project.getMavenId().getGroupId() + "." + project.getMavenId().getArtifactId();
        }

        public boolean isPartOfBuild() {
            return partOfBuild;
        }

        public void setPartOfBuild(boolean partOfBuild) {
            this.partOfBuild = partOfBuild;
            if(!partOfBuild) {
                status = SynchronizationStatus.excluded;
            }
        }

        public ServerConfiguration getParent() {
            return parent;
        }

        public String getSymbolicName() {
            return symbolicName;
        }

        public String getName() {
            return project == null ? "No Project" : mavenProject.getName();
        }

        public String getArtifactId() {
            return project == null ? "No Project" : mavenProject.getMavenId().getArtifactId();
        }

        public String getVersion() {
            return project == null ? "No Project" : mavenProject.getMavenId().getVersion();
        }

        public Project getProject() {
            return project;
        }

        public MavenProject getMavenProject() {
            return mavenProject;
        }

        public SynchronizationStatus getStatus() {
            return status;
        }

        public boolean isOSGiBundle() {
            return project != null && mavenProject.getPackaging().equalsIgnoreCase("bundle");
        }

        public boolean isSlingPackage() {
            return project != null && mavenProject.getPackaging().equalsIgnoreCase("content-package");
        }

        public boolean rebind(@NotNull Project project, @NotNull MavenProject mavenProject) {
            boolean ret = false;
            // Check if the Symbolic Name match
            String symbolicName = getSymbolicName(mavenProject);
            if(this.symbolicName.equals(symbolicName)) {
                this.project = project;
                this.mavenProject = mavenProject;
                if(!isOSGiBundle() && !isSlingPackage()) {
                    setStatus(SynchronizationStatus.unsupported);
                } else {
                    setStatus(SynchronizationStatus.notChecked);
//AS TODO: Where to set this and shouldn't it true by default?
                    setPartOfBuild(true);
                }
                ret = true;
            }
            return ret;
        }

        public void setStatus(SynchronizationStatus status) {
            if(status != null) {
                this.status = status;
            }
        }
    }
}
