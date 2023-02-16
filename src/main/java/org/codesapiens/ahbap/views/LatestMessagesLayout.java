package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

@Tag("vaadin-latest-messages-layout")
public class LatestMessagesLayout extends VerticalLayout {

    public LatestMessagesLayout(String userName, List<String> messages) {
        setSpacing(true);
        setWidthFull();

        // Create header label with user's name
        Label headerLabel = new Label("Latest Messages for " + userName);
        headerLabel.getStyle().set("font-size", "1.5em");
        add(headerLabel);

        // Create layout to hold messages
        VerticalLayout messagesLayout = new VerticalLayout();
        messagesLayout.setSpacing(true);
        messagesLayout.setWidthFull();
        messagesLayout.getStyle().set("overflow-y", "auto");
        messagesLayout.getStyle().set("max-height", "300px");

        // Add messages to layout
        for (String message : messages) {
            Label messageLabel = new Label(message);
            messageLabel.getStyle().set("font-size", "1.2em");
            messageLabel.getStyle().set("white-space", "pre-wrap");
            messagesLayout.add(messageLabel);
        }

        add(messagesLayout);
    }
}
