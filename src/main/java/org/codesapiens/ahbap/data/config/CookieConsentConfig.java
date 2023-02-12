package org.codesapiens.ahbap.data.config;

import com.vaadin.flow.component.cookieconsent.CookieConsent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConsentConfig {

    @Bean
    public CookieConsent getCookieConsent() {
        CookieConsent cookieConsent = new CookieConsent();
        cookieConsent.setMessage("" +
                "Sitemizin kullanımı sırasında gireceğiniz bilgiler hiçbir kurum ya da birey ile paylaşılmamaktadır." +
                "Sadece sizlere yardım edecek olanlar sisteme girip talebinizi görebilir." +
                "Uygulamanın herhangi bir kar amacı ya da reklam politikası yoktur." +
                "");

        return cookieConsent;
    }
}
