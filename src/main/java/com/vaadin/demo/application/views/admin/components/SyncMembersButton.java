package com.vaadin.demo.application.views.admin.components;

import com.vaadin.demo.application.application.service.MeetupApplicationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

/**
 * Button component for syncing members from Meetup.com
 */
public class SyncMembersButton extends Button {

    private final MeetupApplicationService meetupService;
    private Runnable afterSyncAction;
    
    /**
     * Create a button for syncing members by Meetup event ID
     */
    public SyncMembersButton(MeetupApplicationService meetupService, String meetupEventId) {
        super("Sync Members", new Icon(VaadinIcon.REFRESH));
        this.meetupService = meetupService;
        
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClickListener(e -> {
            syncMembersByMeetupId(meetupEventId);
            
            // Run refresh action after sync if provided
            if (afterSyncAction != null) {
                afterSyncAction.run();
            }
            
            // Refresh current page
            UI.getCurrent().getPage().reload();
        });
    }
    
    /**
     * Create a button for syncing members by database event ID
     */
    public SyncMembersButton(MeetupApplicationService meetupService, Long eventId) {
        super("Sync Members", new Icon(VaadinIcon.REFRESH));
        this.meetupService = meetupService;
        
        addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClickListener(e -> {
            syncMembers(eventId);
            
            // Run refresh action after sync if provided
            if (afterSyncAction != null) {
                afterSyncAction.run();
            }
            
            // Refresh current page
            UI.getCurrent().getPage().reload();
        });
    }
    
    /**
     * Set an action to run after sync completes
     */
    public void setAfterSyncAction(Runnable action) {
        this.afterSyncAction = action;
    }
    
    private void syncMembers(Long eventId) {
        try {
            setEnabled(false);
            setText("Syncing...");
            
            int count = meetupService.syncEventMembers(eventId);
            
            Notification.show("Members synced: " + count, 
                    3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Error syncing members: " + ex.getMessage(), 
                    5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } finally {
            setEnabled(true);
            setText("Sync Members");
        }
    }
    
    private void syncMembersByMeetupId(String meetupEventId) {
        try {
            setEnabled(false);
            setText("Syncing...");
            
            int count = meetupService.syncEventMembersByMeetupId(meetupEventId);
            
            Notification.show("Members synced: " + count, 
                    3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Error syncing members: " + ex.getMessage(), 
                    5000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } finally {
            setEnabled(true);
            setText("Sync Members");
        }
    }
}