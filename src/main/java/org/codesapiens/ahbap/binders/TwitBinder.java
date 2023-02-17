package org.codesapiens.ahbap.binders;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.codesapiens.ahbap.data.entity.MessageEntity;
import org.codesapiens.ahbap.data.service.StyleUtils;
import org.codesapiens.ahbap.views.MessageLayout;

import java.util.List;

public class TwitBinder extends VerticalLayout {

    public TwitBinder(MessageEntity message) {

        StyleUtils.pageLayout(this.getElement());

        final var username = message.getSender().getFirstName() + " " + message.getSender().getLastName();
        final var messageText = List.of(message.getText().split("\n"));
        final var twitterLogo = new Image("https://www.freepnglogos.com/uploads/twitter-logo-png/twitter-logo-vector-png-clipart-1.png", "Twitter Logo");
        final var twitterLogoLayout = new VerticalLayout(twitterLogo);
        twitterLogoLayout.getStyle().set("margin-left", "auto");
        twitterLogoLayout.getStyle().set("margin-right", "auto");
        twitterLogoLayout.getStyle().set("margin-top", "auto");
        twitterLogoLayout.getStyle().set("margin-bottom", "auto");
        twitterLogoLayout.getStyle().set("width", "100%");
        twitterLogoLayout.getStyle().set("height", "100%");
        twitterLogoLayout.getStyle().set("background-color", "white");
        twitterLogoLayout.getStyle().set("border-radius", "50%");
        twitterLogoLayout.getStyle().set("padding", "10px");
        twitterLogoLayout.getStyle().set("box-shadow", "0 0 10px 0 rgba(0,0,0,0.2)");
        twitterLogoLayout.getStyle().set("cursor", "pointer");
        twitterLogoLayout.getStyle().set("transition", "all 0.3s ease-in-out");
        twitterLogoLayout.addClickListener(event -> {
            twitterLogoLayout.getStyle().set("transform", "scale(0.9)");
            twitterLogoLayout.getStyle().set("transition", "all 0.3s ease-in-out");
        });

        final var messageLayout = new MessageLayout(username, messageText);

        messageLayout.add(
                twitterLogoLayout
        );

        add(
                messageLayout
        );
    }
}
