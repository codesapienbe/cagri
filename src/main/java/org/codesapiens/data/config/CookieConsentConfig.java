package org.codesapiens.data.config;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConsentConfig {

    @Bean
    public VerticalLayout getCookieConsent() {
        VerticalLayout layout = new VerticalLayout();
        Span cookieConsent = new Span();
        cookieConsent.setText("" +
                "Sitemizin kullanımı sırasında gireceğiniz bilgiler hiçbir kurum ya da birey ile paylaşılmamaktadır." +
                "Sadece sizlere yardım edecek olanlar sisteme girip talebinizi görebilir." +
                "Uygulamanın herhangi bir kar amacı ya da reklam politikası yoktur." +
                "");

        layout.add(cookieConsent);
        return layout;
    }
}
