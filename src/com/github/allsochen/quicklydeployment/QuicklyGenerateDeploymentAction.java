package com.github.allsochen.quicklydeployment;

import com.github.allsochen.quicklydeployment.configuration.Configuration;
import com.github.allsochen.quicklydeployment.configuration.JsonConfig;
import com.github.allsochen.quicklydeployment.configuration.JsonConfigBuilder;
import com.github.allsochen.quicklydeployment.configuration.Properties;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class QuicklyGenerateDeploymentAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getProject();
        String basePath = project.getBasePath();

        String json = Properties.get(Configuration.JSON_STR);
        if (json == null || json.isEmpty()) {
            json = JsonConfigBuilder.getInstance().create();
        }
        JsonConfig jsonConfig;
        try {
            jsonConfig = JsonConfigBuilder.getInstance().deserialize(json);
            if (jsonConfig.getRootPath().isEmpty()) {
                throw new RuntimeException("configuration error");
            }
        } catch (Exception e) {
            Messages.showInfoMessage(project,
                    "Please config the target deployment root path(e.g. D:/Codes/deployment). "
                            + "Settings->Quickly Sync Deployment",
                    "configuration error");
            return;
        }
        String rootPath = jsonConfig.getRootPath();
        rootPath = rootPath.replace("\\", File.separator);
        if (rootPath.endsWith("/")) {
            rootPath = rootPath.substring(0, rootPath.length() - 1);
        }
        try {
            File file = new File(rootPath);
            file.mkdirs();
            handleDeployment(project.getName(), basePath);
        } catch (IOException e) {
            Messages.showInfoMessage(project,
                    "generate deployment.xml file error. maybe the root path is wrong.",
                    "error");
            return;
        }
        try {
            handleWebServers(project.getName(), basePath, rootPath);
        } catch (IOException e) {
            Messages.showInfoMessage(project,
                    "generate webServer.xml file error. maybe the root path is wrong.",
                    "error");
            return;
        }
        Messages.showInfoMessage(project,
                "project will synchronize to: " + rootPath + "/" + project.getName(),
                "config success");
        VfsUtil.markDirtyAndRefresh(true, true, true, ProjectRootManager.getInstance(project).getContentRoots());
        LocalFileSystem.getInstance().refresh(true);
        AnAction anAction = ActionManager.getInstance().getAction("RemoteServers.EditDeploymentConfig");
        if (anAction != null) {
            anAction.update(event);
            anAction.beforeActionPerformedUpdate(event);
            anAction.actionPerformed(event);
        }
        anAction = ActionManager.getInstance().getAction("Servers.Deploy");
        if (anAction != null) {
            anAction.update(event);
            anAction.beforeActionPerformedUpdate(event);
            anAction.actionPerformed(event);
        }
        anAction = ActionManager.getInstance().getAction("RemoteServers.EditServerConfig");
        if (anAction != null) {
            anAction.update(event);
            anAction.beforeActionPerformedUpdate(event);
            anAction.actionPerformed(event);
        }
        System.out.println(anAction.toString());
    }

    private void handleDeployment(String projectName, String basePath)
            throws IOException {
        InputStream in = getClass().getResourceAsStream("/deployment.xml");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        File file = new File(basePath + File.separator + ".idea" + File.separator + "deployment.xml");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8));

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String newLine = line.replace("${projectName}", projectName);
                bufferedWriter.write(newLine);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bufferedReader.close();
        bufferedWriter.close();
    }

    private void handleWebServers(String projectName, String basePath, String deploymentRootPath)
            throws IOException {
        InputStream in = getClass().getResourceAsStream("/webServers.xml");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

        File file = new File(basePath + File.separator + ".idea" + File.separator + "webServers.xml");
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8));

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String newLine = line.replace("${projectName}", projectName);
                newLine = newLine.replace("${rootPath}", deploymentRootPath);
                bufferedWriter.write(newLine);
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bufferedReader.close();
        bufferedWriter.close();
    }
}
