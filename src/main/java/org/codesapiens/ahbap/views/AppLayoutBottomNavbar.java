package org.codesapiens.ahbap.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouterLink;

public class AppLayoutBottomNavbar extends AppLayout {

    public AppLayoutBottomNavbar() {
        H1 title = new H1("Konum");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "var(--lumo-space-m) var(--lumo-space-l)");

        Tabs tabs = getTabs();

        H2 viewTitle = new H2("Acil Yardımlaşma Platformu");
        Paragraph viewContent = new Paragraph("Bu platform hızlı olarak geliştirmek amaçlı yazılım testleri yapılmadan paylaşılmıştır." +
                "Hala geliştirme sürecinde olduğumuz için lütfen kullanım esnasında bir problem oluşur ise" +
                " hemen bizlere bildiriniz. İletişim e-posta adresimiz: dev@mail.be" +
                "");

        Div content = new Div();
        content.add(viewTitle, viewContent);

        addToNavbar(title);
        addToNavbar(true, tabs);

        setContent(content);
    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        Tab requirementsTab = createTab(VaadinIcon.EXCLAMATION_CIRCLE, "Yardım Çağır", EmergencyCallView.class);
        Tab emergencyTab = createTab(VaadinIcon.PACKAGE, "Temel İhtiyaçlar Listesi", MapCallView.class);
        tabs.add(requirementsTab, emergencyTab);
        tabs.addThemeVariants(TabsVariant.LUMO_CENTERED, TabsVariant.LUMO_EQUAL_WIDTH_TABS);
        tabs.setSelectedTab(requirementsTab);
        return tabs;
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName, Class<? extends Component> navigationTarget) {
        Icon icon = viewIcon.create();
        icon.setSize("var(--lumo-icon-size-s)");
        icon.getStyle().set("margin", "auto");

        RouterLink link = new RouterLink();
        link.add(icon);
        link.setRoute(navigationTarget);
        link.setTabIndex(-1);

        return new Tab(link);
    }

}

