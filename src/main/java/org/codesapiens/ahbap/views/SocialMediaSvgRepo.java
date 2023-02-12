package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.icon.IconFactory;

import java.util.Locale;

@JsModule("./social-media-svgrepo.js")
public enum SocialMediaSvgRepo implements IconFactory {
    FACEBOOK, SMS, TWITTER, WHATSAPP;

    public Icon create() {
        return new Icon(this.name().toLowerCase(Locale.ENGLISH).replace('_', '-').replaceAll("^-", ""));
    }

    public static final class Icon extends com.vaadin.flow.component.icon.Icon {
        Icon(String icon) {
            super("social-media-svgrepo", icon);
        }
    }
}