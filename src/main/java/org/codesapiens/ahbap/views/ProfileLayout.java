package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Objects;

@Tag("vaadin-user-profile-layout")
public class ProfileLayout extends HorizontalLayout {

    public ProfileLayout(String imageUrl, String fullName, String phoneNumber, String requestTime) {
        setSpacing(true);
        setWidthFull();

        getStyle().set("background", "rgba(255, 255, 255, 0.7)");
        getStyle().set("backdrop-filter", "blur(10px)");

        Image profileImage = new Image(
                Objects.requireNonNullElse(imageUrl, "https://i.imgur.com/1ZQZ1Yx.jpg"),
                Objects.requireNonNullElse(fullName, "Profile Image")
        );
        profileImage.setHeight("150px");
        profileImage.setWidth("150px");
        profileImage.getStyle().set("border-radius", "50%");
        add(profileImage);

        VerticalLayout userDetailsLayout = new VerticalLayout();
        userDetailsLayout.setSpacing(true);
        userDetailsLayout.setWidthFull();

        Label fullNameLabel = new Label("Full Name: " + fullName);
        Label phoneLabel = new Label("Phone: " + phoneNumber);
        Label requestTimeLabel = new Label("Request Time: " + requestTime);

        userDetailsLayout.add(fullNameLabel, phoneLabel, requestTimeLabel);
        add(userDetailsLayout);
    }
}
