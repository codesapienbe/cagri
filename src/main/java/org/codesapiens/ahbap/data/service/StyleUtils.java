package org.codesapiens.ahbap.data.service;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.dom.Element;

public class StyleUtils {

    private StyleUtils() {
    }


    public static void removeAllMarginAndPadding(Element element) {
        element.getStyle()
                .set("margin", "0")
                .set("padding", "0");
    }

    public static void footerButton(Button button, String position, String background) {
        button.getStyle()
                .set("background-color", background)
                .set("color", "#fff")
                .set("border", "none")
                .set("border-radius", "5px")
                .set(position, "0")
                .set("bottom", "0")
                .set("z-index", "1000");
    }

    public static void footerLayout(Element element) {
        element.getStyle()
                .set("position", "absolute")
                .set("bottom", "0")
                .set("width", "100%")
                .set("padding", "10px")
                .set("margin", "0")
                .set("align-items", "center")
                .set("justify-content", "space-between")
                .set("background-color", "transparent")
                .set("z-index", "1000");
    }

    public static void shareButtons(Element component) {
        component.getStyle()
                .set("bottom", "0")
                .set("width", "50%")
                .set("max-width", "75%")
                .set("padding", "10px")
                .set("margin", "0")
                .set("background-color", "transparent")
                .set("z-index", "1000");
    }

    public static void dialogButton(Element element) {
        element.getStyle()
                .set("margin-top", "1em")
                .set("color", "white")
                .set("border", "none");
    }

    public static void pageLayout(Element component) {
        component.getStyle()
                .set("background-color", "#f5f5f5")
                .set("padding", "0")
                .set("margin", "0")
                .set("spacing", "0")
                .set("border", "0")
                .set("font-family", "Roboto, sans-serif");
    }

    public static void pageBackground(Element element) {
        element.getStyle()
                .set("background", "url('https://images.unsplash.com/photo-1517436073-3b1b1b1b1b1b?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=1350&q=80') no-repeat center center fixed")
                .set("background-size", "cover")
                .set("margin", "0")
                .set("padding", "0")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("height", "100vh");
    }
}
