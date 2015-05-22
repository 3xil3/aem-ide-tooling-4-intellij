package com.headwire.aem.tooling.intellij.console;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.impl.NotificationsConfigurationImpl;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.messages.Topic;
import com.intellij.util.ui.UIUtil;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by schaefa on 5/6/15.
 */
public class ConsoleLogModel
    implements Disposable
{
    public static final Topic<Runnable> LOG_MODEL_CHANGED = Topic.create("LOG_MODEL_CHANGED", Runnable.class, Topic.BroadcastDirection.NONE);

    private final List<Notification> myNotifications = new ArrayList<Notification>();
    private final Map<Notification, Long> myStamps = Collections.synchronizedMap(new WeakHashMap<Notification, Long>());
    private final Map<Notification, String> myStatuses = Collections.synchronizedMap(new WeakHashMap<Notification, String>());
    private Trinity<Notification, String, Long> myStatusMessage;
    private final Project myProject;
    final Map<Notification, Runnable> removeHandlers = new THashMap<Notification, Runnable>();

    ConsoleLogModel(@Nullable Project project, @NotNull Disposable parentDisposable) {
        myProject = project;
        Disposer.register(parentDisposable, this);
    }

    void addNotification(Notification notification) {
        long stamp = System.currentTimeMillis();
        NotificationDisplayType type = NotificationsConfigurationImpl.getSettings(notification.getGroupId()).getDisplayType();
        if (notification.isImportant() || (type != NotificationDisplayType.NONE && type != NotificationDisplayType.TOOL_WINDOW)) {
            synchronized (myNotifications) {
                myNotifications.add(notification);
            }
        }
        myStamps.put(notification, stamp);
        myStatuses.put(notification, ConsoleLog.formatForLog(notification, "").status);
        setStatusMessage(notification, stamp);
        fireModelChanged();
    }

    private static void fireModelChanged() {
        ApplicationManager.getApplication().getMessageBus().syncPublisher(LOG_MODEL_CHANGED).run();
    }

    List<Notification> takeNotifications() {
        final ArrayList<Notification> result;
        synchronized (myNotifications) {
            result = getNotifications();
            myNotifications.clear();
        }
        fireModelChanged();
        return result;
    }

    void setStatusMessage(@Nullable Notification statusMessage, long stamp) {
        synchronized (myNotifications) {
            if (myStatusMessage != null && myStatusMessage.first == statusMessage) return;
            if (myStatusMessage == null && statusMessage == null) return;

            myStatusMessage = statusMessage == null ? null : Trinity.create(statusMessage, myStatuses.get(statusMessage), stamp);
        }
        StatusBar.Info.set("", myProject, ConsoleLog.LOG_REQUESTOR);
    }

    @Nullable
    Trinity<Notification, String, Long> getStatusMessage() {
        synchronized (myNotifications) {
            return myStatusMessage;
        }
    }

    void logShown() {
        for (Notification notification : getNotifications()) {
            if (!notification.isImportant()) {
                removeNotification(notification);
            }
        }
        setStatusToImportant();
    }

    public ArrayList<Notification> getNotifications() {
        synchronized (myNotifications) {
            return new ArrayList<Notification>(myNotifications);
        }
    }

    @Nullable
    public Long getNotificationTime(Notification notification) {
        return myStamps.get(notification);
    }

    public void removeNotification(Notification notification) {
        synchronized (myNotifications) {
            myNotifications.remove(notification);
        }

        Runnable handler = removeHandlers.remove(notification);
        if (handler != null) {
            UIUtil.invokeLaterIfNeeded(handler);
        }

        Trinity<Notification, String, Long> oldStatus = getStatusMessage();
        if (oldStatus != null && notification == oldStatus.first) {
            setStatusToImportant();
        }
        fireModelChanged();
    }

    private void setStatusToImportant() {
        ArrayList<Notification> notifications = getNotifications();
        Collections.reverse(notifications);
        Notification message = ContainerUtil.find(notifications, new Condition<Notification>() {
            @Override
            public boolean value(Notification notification) {
                return notification.isImportant();
            }
        });
        if (message == null) {
            setStatusMessage(null, 0);
        }
        else {
            Long notificationTime = getNotificationTime(message);
            assert notificationTime != null;
            setStatusMessage(message, notificationTime);
        }
    }

    public Project getProject() {
        //noinspection ConstantConditions
        return myProject;
    }

    @Override
    public void dispose() {
    }
}