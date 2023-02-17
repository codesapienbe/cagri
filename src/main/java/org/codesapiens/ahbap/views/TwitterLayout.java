package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.codesapiens.ahbap.binders.TwitBinder;
import org.codesapiens.ahbap.data.entity.MessageEntity;
import org.codesapiens.ahbap.data.service.StyleUtils;

import java.util.List;

@Tag("vaadin-twitter-layout")
public class TwitterLayout extends VerticalLayout {

    public TwitterLayout(List<MessageEntity> messages) {
        StyleUtils.pageLayout(this.getElement());

        messages.forEach(message -> {
            TwitBinder twitBinder = new TwitBinder(message);
            add(twitBinder);
        });

    }
}
