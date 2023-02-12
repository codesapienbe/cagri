package org.codesapiens.ahbap.data.config;

import org.codesapiens.ahbap.data.entity.ItemEntity;
import org.codesapiens.ahbap.data.service.ItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.stream.Stream;

@Configuration
public class ItemConfig implements CommandLineRunner {

    private final ItemRepository itemRepository;

    public ItemConfig(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // Medical supplies
        ItemEntity med01 = new ItemEntity("Antibiyotik", "Medikal");
        ItemEntity med02 = new ItemEntity("Ağrı kesici", "Medikal");
        ItemEntity med03 = new ItemEntity("Yara pomadı", "Medikal");
        ItemEntity med04 = new ItemEntity("Tetanoz aşısı", "Medikal");
        ItemEntity med05 = new ItemEntity("Termometre", "Medikal");
        ItemEntity med06 = new ItemEntity("Kan", "Medikal");
        ItemEntity med07 = new ItemEntity("Kolonyalı Mendil", "Medikal");
        ItemEntity med08 = new ItemEntity("Yara bakım jeli", "Medikal");
        ItemEntity med09 = new ItemEntity("Gazlı bez", "Medikal");
        ItemEntity med10 = new ItemEntity("Yara dikiş gereçleri", "Medikal");

        saveItemsToDB(med01, med02, med03, med04, med05, med06, med07, med08, med09, med10);

        // Food and drink supplies
        ItemEntity food01 = new ItemEntity("Makarna", "Gıda");
        ItemEntity food02 = new ItemEntity("Piriç/Mercimek", "Gıda");
        ItemEntity food03 = new ItemEntity("Fasulye/Nohut", "Gıda");
        ItemEntity food04 = new ItemEntity("Konserve Yemek", "Gıda");
        ItemEntity food05 = new ItemEntity("Hazır Yemek", "Gıda");
        ItemEntity food06 = new ItemEntity("Sandviç", "Gıda");
        ItemEntity food07 = new ItemEntity("Bisküvi/Kek", "Gıda");
        ItemEntity food08 = new ItemEntity("Ekmek", "Gıda");
        ItemEntity food09 = new ItemEntity("Enerji barı/Çikolata", "Gıda");
        ItemEntity food10 = new ItemEntity("Su/Çay/Kahve/Süt", "Gıda");

        saveItemsToDB(food01, food02, food03, food04, food05, food06, food07, food08, food09, food10);


        // sheltering supplies
        ItemEntity sheltering01 = new ItemEntity("Çadır", "Barınma");
        ItemEntity sheltering02 = new ItemEntity("Battaniye", "Barınma");
        ItemEntity sheltering03 = new ItemEntity("Atkı/Bere/Eldiven/Çorap", "Barınma");
        ItemEntity sheltering04 = new ItemEntity("Termal içlik", "Barınma");
        ItemEntity sheltering05 = new ItemEntity("Uyku tulumu", "Barınma");
        ItemEntity sheltering06 = new ItemEntity("Termos", "Barınma");
        ItemEntity sheltering07 = new ItemEntity("Şişme/sünger yatak", "Barınma");
        ItemEntity sheltering08 = new ItemEntity("Isıtıcı", "Barınma");
        ItemEntity sheltering09 = new ItemEntity("El feneri", "Barınma");
        ItemEntity sheltering10 = new ItemEntity("Yastık", "Barınma");

        saveItemsToDB(sheltering01, sheltering02, sheltering03, sheltering04, sheltering05, sheltering06, sheltering07, sheltering08, sheltering09, sheltering10);


        // Hygene supplies

        ItemEntity hygene01 = new ItemEntity("Islak/Kuru mendil", "Hijyen");
        ItemEntity hygene02 = new ItemEntity("El/vücut sabunu", "Hijyen");
        ItemEntity hygene03 = new ItemEntity("Kadın/Erkek şampuan", "Hijyen");
        ItemEntity hygene04 = new ItemEntity("Tuvalet kağıdı", "Hijyen");
        ItemEntity hygene05 = new ItemEntity("Hijyenik ped", "Hijyen");
        ItemEntity hygene06 = new ItemEntity("Hijyenik tampon", "Hijyen");
        ItemEntity hygene07 = new ItemEntity("Jilet/traş köpüğü", "Hijyen");
        ItemEntity hygene08 = new ItemEntity("Diş macunu ve diş fırçası", "Hijyen");
        ItemEntity hygene09 = new ItemEntity("İç çamaşırı", "Hijyen");
        ItemEntity hygene10 = new ItemEntity("Havlu", "Hijyen");

        saveItemsToDB(hygene01, hygene02, hygene03, hygene04, hygene05, hygene06, hygene07, hygene08, hygene09, hygene10);

        // Baby supplies

        ItemEntity baby01 = new ItemEntity("Bebek maması", "Bebek");
        ItemEntity baby02 = new ItemEntity("Bebek bezi", "Bebek");
        ItemEntity baby03 = new ItemEntity("Bebek diş kaşıma jeli", "Bebek");
        ItemEntity baby04 = new ItemEntity("Bebek pişik önleyici krem", "Bebek");
        ItemEntity baby05 = new ItemEntity("Bebek biberon", "Bebek");
        ItemEntity baby06 = new ItemEntity("Bebek kıyafeti", "Bebek");
        ItemEntity baby07 = new ItemEntity("Bebek bisküvisi", "Bebek");
        ItemEntity baby08 = new ItemEntity("Bebek diş kaşıma aparatı", "Bebek");
        ItemEntity baby09 = new ItemEntity("Bebek sarılacak oyuncak", "Bebek");
        ItemEntity baby10 = new ItemEntity("Bebek için özel ıslak mendil", "Bebek");

        saveItemsToDB(baby01, baby02, baby03, baby04, baby05, baby06, baby07, baby08, baby09, baby10);
    }

    private void saveItemsToDB(ItemEntity sheltering01, ItemEntity sheltering02, ItemEntity sheltering03, ItemEntity sheltering04,
                               ItemEntity sheltering05, ItemEntity sheltering06, ItemEntity sheltering07, ItemEntity sheltering08,
                               ItemEntity sheltering09, ItemEntity sheltering10) {
        Stream.of(
                sheltering01,
                sheltering02,
                sheltering03,
                sheltering04,
                sheltering05,
                sheltering06,
                sheltering07,
                sheltering08,
                sheltering09,
                sheltering10
        ).forEach(item -> {
            Optional<ItemEntity> foundItem = itemRepository.findByTitle(item.getTitle());
            if (foundItem.isEmpty()) {
                itemRepository.save(item);
            }
        });
    }
}
