package org.codesapiens.data.service;

import com.vaadin.flow.component.UI;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendNotification(String message) {
        UI.getCurrent().getPage().executeJs(
                "if (!(\"Notification\" in window)) {\n" +
                        "    // Check if the browser supports notifications\n" +
                        "    alert(\"This browser does not support desktop notification\");\n" +
                        "  } else if (Notification.permission === \"granted\") {\n" +
                        "    // Check whether notification permissions have already been granted;\n" +
                        "    // if so, create a notification\n" +
                        "    const notification = new Notification(\"" + message + "\");\n" +
                        "    // …\n" +
                        "  } else if (Notification.permission !== \"denied\") {\n" +
                        "    // We need to ask the user for permission\n" +
                        "    Notification.requestPermission().then((permission) => {\n" +
                        "      // If the user accepts, let's create a notification\n" +
                        "      if (permission === \"granted\") {\n" +
                        "        const notification = new Notification(\"" + message + "\");\n" +
                        "        // …\n" +
                        "      }\n" +
                        "    });\n" +
                        "  }"
        );
    }
}
