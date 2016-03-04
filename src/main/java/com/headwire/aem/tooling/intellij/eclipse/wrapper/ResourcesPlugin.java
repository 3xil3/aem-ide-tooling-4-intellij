package com.headwire.aem.tooling.intellij.eclipse.wrapper;

import com.headwire.aem.tooling.intellij.eclipse.stub.IFile;
import com.headwire.aem.tooling.intellij.eclipse.stub.IFolder;
import com.headwire.aem.tooling.intellij.eclipse.stub.IPath;
import com.headwire.aem.tooling.intellij.eclipse.stub.IProject;
import com.headwire.aem.tooling.intellij.eclipse.stub.IResource;
import com.headwire.aem.tooling.intellij.explorer.ServerTreeSelectionHandler;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;

import javax.swing.SwingUtilities;

/**
 * Created by schaefa on 5/15/15.
 */
@Deprecated
public class ResourcesPlugin {
    private static final Workspace WORKSPACE = new Workspace();

    public static Workspace getWorkspace() {
        return WORKSPACE;
    }

    public static class Workspace {
        private static Root ROOT = new Root();

        public Root getRoot() {
            return ROOT;
        }
    }

    public static class Root {

        public IResource findMember(IPath childPath) {
            // I guess we need to figure out if that file is part of the project and if so return an IResource
            DataContext dataContext = DataManager.getInstance().getDataContextFromFocus().getResult();
            VirtualFile file = null;
            if(dataContext != null) {
                Project project = CommonDataKeys.PROJECT.getData(dataContext);
                String aPath = childPath.toOSString();
                VirtualFileSystem vfs = project.getProjectFile().getFileSystem();
                file = vfs.findFileByPath(aPath);
            } else {
                String message = "could not obtain data context";
            }
            //AS TODO: If this is only used as a marker then we are fine but otherwise we need to obtain the current module
            return file == null ? null :
                (file.isDirectory() ? new IFolder() : new IFile());
        }

        public IFile getFileForLocation(IProject project, IPath filterPath) {
            IFile ret = new IFile(project.getModule(), filterPath.toFile());
            return ret;
        }
    }
}
