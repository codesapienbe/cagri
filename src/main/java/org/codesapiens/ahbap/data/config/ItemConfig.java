package org.codesapiens.ahbap.data.config;

import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.service.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class ItemConfig implements CommandLineRunner {

    private final ItemRepository itemRepository;

    public ItemConfig(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (itemRepository.count() == 0) {
            seedItems();
        }
    }

    private void seedItems() {
        // sheltering supplies
        final var sheltering01 = new ItemEntity("Çadır", "Barınma");
        final var sheltering02 = new ItemEntity("Battaniye", "Barınma");
        final var sheltering03 = new ItemEntity("Atkı/Bere/Eldiven/Çorap", "Barınma");
        final var sheltering04 = new ItemEntity("Termal içlik", "Barınma");
        final var sheltering05 = new ItemEntity("Uyku tulumu", "Barınma");
        final var sheltering06 = new ItemEntity("Termos", "Barınma");
        final var sheltering07 = new ItemEntity("Şişme/sünger yatak", "Barınma");
        final var sheltering08 = new ItemEntity("Isıtıcı", "Barınma");
        final var sheltering09 = new ItemEntity("El feneri", "Barınma");
        final var sheltering10 = new ItemEntity("Yastık", "Barınma");

        saveItemsToDB(sheltering01, sheltering02, sheltering03, sheltering04, sheltering05, sheltering06, sheltering07, sheltering08, sheltering09, sheltering10);

        // Food and drink supplies
        final var food01 = new ItemEntity("Makarna", "Gıda");
        final var food02 = new ItemEntity("Piriç/Mercimek", "Gıda");
        final var food03 = new ItemEntity("Fasulye/Nohut", "Gıda");
        final var food04 = new ItemEntity("Konserve Yemek", "Gıda");
        final var food05 = new ItemEntity("Hazır Yemek", "Gıda");
        final var food06 = new ItemEntity("Sandviç", "Gıda");
        final var food07 = new ItemEntity("Bisküvi/Kek", "Gıda");
        final var food08 = new ItemEntity("Ekmek", "Gıda");
        final var food09 = new ItemEntity("Enerji barı/Çikolata", "Gıda");
        final var food10 = new ItemEntity("Su/Çay/Kahve/Süt", "Gıda");

        saveItemsToDB(food01, food02, food03, food04, food05, food06, food07, food08, food09, food10);

        // Clothes supplies
        final var clothes01 = new ItemEntity("Çocuk kıyafeti", "Kıyafet");
        final var clothes02 = new ItemEntity("Erkek kıyafeti", "Kıyafet");
        final var clothes03 = new ItemEntity("Kadın kıyafeti", "Kıyafet");
        final var clothes04 = new ItemEntity("Ayakkabı", "Kıyafet");
        final var clothes05 = new ItemEntity("Çanta", "Kıyafet");
        final var clothes07 = new ItemEntity("Atkı/Bere", "Kıyafet");
        final var clothes08 = new ItemEntity("Eldiven", "Kıyafet");
        final var clothes09 = new ItemEntity("Çorap", "Kıyafet");
        final var clothes10 = new ItemEntity("İç çamaşırı", "Kıyafet");

        saveItemsToDB(clothes01, clothes02, clothes03, clothes04, clothes05, clothes07, clothes08, clothes09, clothes10);

        // Baby supplies
        final var baby01 = new ItemEntity("Bebeğe özel yemek", "Bebek");
        final var baby02 = new ItemEntity("Bebeğe özel su", "Bebek");
        final var baby03 = new ItemEntity("Bebeğe özel bez", "Bebek");
        final var baby04 = new ItemEntity("Bebeğe özel şampuan", "Bebek");
        final var baby05 = new ItemEntity("Bebeğe özel kremler", "Bebek");
        final var baby06 = new ItemEntity("Bebeğe özel mendil", "Bebek");
        final var baby07 = new ItemEntity("Bebeğe özel yastık", "Bebek");
        final var baby08 = new ItemEntity("Bebeğe özel yatak", "Bebek");
        final var baby09 = new ItemEntity("Bebeğe özel battaniye", "Bebek");
        final var baby10 = new ItemEntity("Bebeğe özel termal içlik", "Bebek");

        saveItemsToDB(baby01, baby02, baby03, baby04, baby05, baby06, baby07, baby08, baby09, baby10);

        // Disabled/Elderly supplies
        final var disabled01 = new ItemEntity("Tekerlekli sandalye", "Engelli");
        final var disabled02 = new ItemEntity("Kronik ilaçlar", "Engelli");
        final var disabled03 = new ItemEntity("Yürüteç", "Engelli");
        final var disabled04 = new ItemEntity("Protezler", "Engelli");
        final var disabled05 = new ItemEntity("Ses cihazları", "Engelli");
        final var disabled06 = new ItemEntity("Termal kamera", "Engelli");

        saveItemsToDB(disabled01, disabled02, disabled03, disabled04, disabled05, disabled06);

        // Elderly supplies
        final var elderly01 = new ItemEntity("Gıda paketleri", "Yaşlı");
        final var elderly02 = new ItemEntity("Bel desteği", "Yaşlı");
        final var elderly03 = new ItemEntity("Koltuk/Sandalye", "Yaşlı");
        final var elderly04 = new ItemEntity("Küvet", "Yaşlı");
        final var elderly05 = new ItemEntity("Tuvalet", "Yaşlı");

        saveItemsToDB(elderly01, elderly02, elderly03, elderly04, elderly05);

        // Medical supplies
        final var med01 = new ItemEntity("Antibiyotik", "Medikal");
        final var med02 = new ItemEntity("Ağrı kesici", "Medikal");
        final var med03 = new ItemEntity("Yara pomadı", "Medikal");
        final var med04 = new ItemEntity("Tetanoz aşısı", "Medikal");
        final var med05 = new ItemEntity("Termometre", "Medikal");
        final var med06 = new ItemEntity("Kan", "Medikal");
        final var med07 = new ItemEntity("Kolonyalı Mendil", "Medikal");
        final var med08 = new ItemEntity("Yara bakım jeli", "Medikal");
        final var med09 = new ItemEntity("Gazlı bez", "Medikal");
        final var med10 = new ItemEntity("Yara dikiş gereçleri", "Medikal");

        saveItemsToDB(med01, med02, med03, med04, med05, med06, med07, med08, med09, med10);

        // Hygene supplies
        final var hygene01 = new ItemEntity("Islak/Kuru mendil", "Hijyen");
        final var hygene02 = new ItemEntity("El/vücut sabunu", "Hijyen");
        final var hygene03 = new ItemEntity("Kadın/Erkek şampuan", "Hijyen");
        final var hygene04 = new ItemEntity("Tuvalet kağıdı", "Hijyen");
        final var hygene05 = new ItemEntity("Hijyenik ped", "Hijyen");
        final var hygene06 = new ItemEntity("Hijyenik tampon", "Hijyen");
        final var hygene07 = new ItemEntity("Jilet/traş köpüğü", "Hijyen");
        final var hygene08 = new ItemEntity("Diş macunu ve diş fırçası", "Hijyen");
        final var hygene09 = new ItemEntity("Havlu", "Hijyen");
        final var hygene10 = new ItemEntity("Kozmetik", "Hijyen");

        saveItemsToDB(hygene01, hygene02, hygene03, hygene04, hygene05, hygene06, hygene07, hygene08, hygene09, hygene10);

        // Pet supplies
        final var pet01 = new ItemEntity("Kedi mama", "Evcil Hayvan");
        final var pet02 = new ItemEntity("Köpek mama", "Evcil Hayvan");
        final var pet03 = new ItemEntity("Kedi İlaç", "Evcil Hayvan");
        final var pet04 = new ItemEntity("Köpek İlaç", "Evcil Hayvan");

        saveItemsToDB(pet01, pet02, pet03, pet04);

        // Other supplies
        final var other01 = new ItemEntity("Kırtasiye", "Diğer");
        final var other02 = new ItemEntity("Kitap", "Diğer");
        final var other03 = new ItemEntity("Müzik", "Diğer");
        final var other04 = new ItemEntity("Oyun", "Diğer");
        final var other05 = new ItemEntity("Spor malzemeleri", "Diğer");
        final var other06 = new ItemEntity("Araç", "Diğer");
        final var other07 = new ItemEntity("Ev eşyası", "Diğer");
        final var other08 = new ItemEntity("Elektronik Telefon", "Diğer");
        final var other09 = new ItemEntity("Elektronik Bilgisayar", "Diğer");

        saveItemsToDB(other01, other02, other03, other04, other05, other06, other07, other08, other09);
    }

    private void saveItemsToDB(ItemEntity... item) {
        itemRepository.saveAll(Arrays.asList(item));
    }
}
