package org.codesapiens.data.config;

import org.codesapiens.data.entity.*;
import org.codesapiens.data.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.*;
import java.time.*;
import java.time.format.*;
import java.util.Random;

@Configuration
public class MockDataLoader implements CommandLineRunner {

    /**
     *
     */
    private static final String NOW = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private PersonRepository personRepository;
    private TagRepository tagRepository;
    private MessageRepository messageRepository;
    private ItemRepository itemRepository;
    private RequirementRepository requirementRepository;

    @Autowired
    public MockDataLoader(PersonRepository personRepository, TagRepository tagRepository,
            MessageRepository messageRepository, ItemRepository itemRepository,
            RequirementRepository requirementRepository) {
        this.personRepository = personRepository;
        this.tagRepository = tagRepository;
        this.messageRepository = messageRepository;
        this.itemRepository = itemRepository;
        this.requirementRepository = requirementRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (personRepository.count() == 0) {
            seedPeople();
        }

        if (itemRepository.count() == 0) {
            seedItems();
        }

        if (tagRepository.count() == 0) {
            seedTags();
        }
        
        if (requirementRepository.count() == 0) {
            seedRequirements();
        }
        
        if (messageRepository.count() == 0) {
            seedMessages();
        }
    }

    public void seedRequirements() {
        final var people = personRepository.findAll();
        final var items = itemRepository.findAll();

        people.forEach(person -> {
            IntStream.range(0, 10).forEach(index -> {
                final var requirement = new RequirementEntity();
                requirement.setPerson(people.get(index));
                // select a random item from the items and add it to the requirement
                requirement.setItem(items.get(new Random().nextInt(items.size())));
                requirement.setQuantity(1.00);
                requirement.setSessionId(UUID.randomUUID().toString());
                requirementRepository.save(requirement);
            });
        });

    }

    public void seedTags() {
        // Tags with @ annotation
        TagEntity annotation01 = new TagEntity("afad", '@');

        // Tags with # hashtag
        TagEntity hashtag01 = new TagEntity("depremzede", '#');
        TagEntity hashtag02 = new TagEntity("deprem", '#');
        TagEntity hashtag03 = new TagEntity("earthquake", '#');
        TagEntity hashtag04 = new TagEntity("uzaktan", '#');

        saveTagsToDB(annotation01, hashtag01, hashtag02, hashtag03, hashtag04);
    }

    public void saveTagsToDB(TagEntity... tags) {
        tagRepository.saveAll(Arrays.asList(tags));
    }

    public void seedMessages() {

        final var requirements = requirementRepository.findAll();

        requirements.forEach(req -> {

            final var messages = IntStream.range(0, 4).mapToObj(index -> {

                final var message = new MessageEntity();
                message.setSender(req.getPerson());
                message.setChannel(index % 2 == 0 ? "twitter" : "whatsapp");
                message.setText("Message: I need " + req.getItem().getTitle());
                message.setDate(LocalDate.now());
                message.setTime(LocalTime.now());

                return message;
        
            }).toArray(MessageEntity[]::new);

            saveMessagesToDB(messages);

        });

    }

    public void saveMessagesToDB(MessageEntity... messages) {
        messageRepository.saveAll(Arrays.asList(messages));
    }

    public void seedPeople() {
        // Generate 10.000 people with random geolocation for each person and save them to DB using an Iterable
        final var avatar = "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y";
        final var random = new Random();
        final var people = new ArrayList<PersonEntity>();

        for (int i = 0; i < 100; i++) {
            final var person = new PersonEntity();
            person.setFirstName("FirstName" + i);
            person.setLastName("LastName" + i);
            person.setImageUrl(avatar);
            person.setPhone("0532" + random.nextInt(9999999) + 1000000);
            // generate a random geo location within Türkiye (37.000000, 35.321333) and (42.000000, 44.000000)
            // add at least 1000 meters to the latitude and longitude to avoid the same location
            person.setLatitude(random.nextDouble() * (42.000000 - 37.000000) + 37.000000 + 0.010000);
            person.setLongitude(random.nextDouble() * (44.000000 - 35.321333) + 35.321333 + 0.010000);
            person.setRegisteredAt(NOW);
            people.add(person);
        }

        personRepository.saveAll(people);
    }

    public void savePeopleToDB(PersonEntity... people) {
        personRepository.saveAll(Arrays.asList(people));
    }

    public void seedItems() {
        // sheltering supplies
        final var sheltering01 = new ItemEntity("Cadir", "Barinma");
        final var sheltering02 = new ItemEntity("Battaniye", "Barinma");
        final var sheltering03 = new ItemEntity("Atki/Bere/Eldiven/Corap", "Barinma");
        final var sheltering04 = new ItemEntity("Termal iclik", "Barinma");
        final var sheltering05 = new ItemEntity("Uyku tulumu", "Barinma");
        final var sheltering06 = new ItemEntity("Termos", "Barinma");
        final var sheltering07 = new ItemEntity("Şisme/sunger yatak", "Barinma");
        final var sheltering08 = new ItemEntity("Isitici", "Barinma");
        final var sheltering09 = new ItemEntity("El feneri", "Barinma");
        final var sheltering10 = new ItemEntity("Yastik", "Barinma");

        saveItemsToDB(sheltering01, sheltering02, sheltering03, sheltering04, sheltering05, sheltering06, sheltering07,
                sheltering08, sheltering09, sheltering10);

        // Food and drink supplies
        final var food01 = new ItemEntity("Makarna", "Gida");
        final var food02 = new ItemEntity("Piric/Mercimek", "Gida");
        final var food03 = new ItemEntity("Fasulye/Nohut", "Gida");
        final var food04 = new ItemEntity("Konserve Yemek", "Gida");
        final var food05 = new ItemEntity("Hazir Yemek", "Gida");
        final var food06 = new ItemEntity("Sandvic", "Gida");
        final var food07 = new ItemEntity("Biskuvi/Kek", "Gida");
        final var food08 = new ItemEntity("Ekmek", "Gida");
        final var food09 = new ItemEntity("Enerji bari/Cikolata", "Gida");
        final var food10 = new ItemEntity("Su/Cay/Kahve/Sut", "Gida");

        saveItemsToDB(food01, food02, food03, food04, food05, food06, food07, food08, food09, food10);

        // Clothes supplies
        final var clothes01 = new ItemEntity("Cocuk kiyafeti", "Kiyafet");
        final var clothes02 = new ItemEntity("Erkek kiyafeti", "Kiyafet");
        final var clothes03 = new ItemEntity("Kadin kiyafeti", "Kiyafet");
        final var clothes04 = new ItemEntity("Ayakkabi", "Kiyafet");
        final var clothes05 = new ItemEntity("Canta", "Kiyafet");
        final var clothes07 = new ItemEntity("Atki/Bere", "Kiyafet");
        final var clothes08 = new ItemEntity("Eldiven", "Kiyafet");
        final var clothes09 = new ItemEntity("Corap", "Kiyafet");
        final var clothes10 = new ItemEntity("Ic camasiri", "Kiyafet");

        saveItemsToDB(clothes01, clothes02, clothes03, clothes04, clothes05, clothes07, clothes08, clothes09,
                clothes10);

        // Baby supplies
        final var baby01 = new ItemEntity("Bebege ozel yemek", "Bebek");
        final var baby02 = new ItemEntity("Bebege ozel su", "Bebek");
        final var baby03 = new ItemEntity("Bebege ozel bez", "Bebek");
        final var baby04 = new ItemEntity("Bebege ozel sampuan", "Bebek");
        final var baby05 = new ItemEntity("Bebege ozel kremler", "Bebek");
        final var baby06 = new ItemEntity("Bebege ozel mendil", "Bebek");
        final var baby07 = new ItemEntity("Bebege ozel yastik", "Bebek");
        final var baby08 = new ItemEntity("Bebege ozel yatak", "Bebek");
        final var baby09 = new ItemEntity("Bebege ozel battaniye", "Bebek");
        final var baby10 = new ItemEntity("Bebege ozel termal iclik", "Bebek");

        saveItemsToDB(baby01, baby02, baby03, baby04, baby05, baby06, baby07, baby08, baby09, baby10);

        // Disabled/Elderly supplies
        final var disabled01 = new ItemEntity("Tekerlekli sandalye", "Engelli");
        final var disabled02 = new ItemEntity("Kronik ilaclar", "Engelli");
        final var disabled03 = new ItemEntity("Yurutec", "Engelli");
        final var disabled04 = new ItemEntity("Protezler", "Engelli");
        final var disabled05 = new ItemEntity("Ses cihazlari", "Engelli");
        final var disabled06 = new ItemEntity("Termal kamera", "Engelli");

        saveItemsToDB(disabled01, disabled02, disabled03, disabled04, disabled05, disabled06);

        // Elderly supplies
        final var elderly01 = new ItemEntity("Gida paketleri", "Yasli");
        final var elderly02 = new ItemEntity("Bel destegi", "Yasli");
        final var elderly03 = new ItemEntity("Koltuk/Sandalye", "Yasli");
        final var elderly04 = new ItemEntity("Kuvet", "Yasli");
        final var elderly05 = new ItemEntity("Tuvalet", "Yasli");

        saveItemsToDB(elderly01, elderly02, elderly03, elderly04, elderly05);

        // Medical supplies
        final var med01 = new ItemEntity("Antibiyotik", "Medikal");
        final var med02 = new ItemEntity("Agri kesici", "Medikal");
        final var med03 = new ItemEntity("Yara pomadi", "Medikal");
        final var med04 = new ItemEntity("Tetanoz asisi", "Medikal");
        final var med05 = new ItemEntity("Termometre", "Medikal");
        final var med06 = new ItemEntity("Kan", "Medikal");
        final var med07 = new ItemEntity("Kolonyali Mendil", "Medikal");
        final var med08 = new ItemEntity("Yara bakim jeli", "Medikal");
        final var med09 = new ItemEntity("Gazli bez", "Medikal");
        final var med10 = new ItemEntity("Yara dikis gerecleri", "Medikal");

        saveItemsToDB(med01, med02, med03, med04, med05, med06, med07, med08, med09, med10);

        // Hygene supplies
        final var hygene01 = new ItemEntity("Islak/Kuru mendil", "Hijyen");
        final var hygene02 = new ItemEntity("El/vucut sabunu", "Hijyen");
        final var hygene03 = new ItemEntity("Kadin/Erkek sampuan", "Hijyen");
        final var hygene04 = new ItemEntity("Tuvalet kagidi", "Hijyen");
        final var hygene05 = new ItemEntity("Hijyenik ped", "Hijyen");
        final var hygene06 = new ItemEntity("Hijyenik tampon", "Hijyen");
        final var hygene07 = new ItemEntity("Jilet/tras kopugu", "Hijyen");
        final var hygene08 = new ItemEntity("Dis macunu ve dis fircasi", "Hijyen");
        final var hygene09 = new ItemEntity("Havlu", "Hijyen");
        final var hygene10 = new ItemEntity("Kozmetik", "Hijyen");

        saveItemsToDB(hygene01, hygene02, hygene03, hygene04, hygene05, hygene06, hygene07, hygene08, hygene09,
                hygene10);

        // Pet supplies
        final var pet01 = new ItemEntity("Kedi mama", "Evcil Hayvan");
        final var pet02 = new ItemEntity("Kopek mama", "Evcil Hayvan");
        final var pet03 = new ItemEntity("Kedi Ilac", "Evcil Hayvan");
        final var pet04 = new ItemEntity("Kopek Ilac", "Evcil Hayvan");

        saveItemsToDB(pet01, pet02, pet03, pet04);

        // Other supplies
        final var other01 = new ItemEntity("Kirtasiye", "Diger");
        final var other02 = new ItemEntity("Kitap", "Diger");
        final var other03 = new ItemEntity("Muzik", "Diger");
        final var other04 = new ItemEntity("Oyun", "Diger");
        final var other05 = new ItemEntity("Spor malzemeleri", "Diger");
        final var other06 = new ItemEntity("Arac", "Diger");
        final var other07 = new ItemEntity("Ev esyasi", "Diger");
        final var other08 = new ItemEntity("Elektronik Telefon", "Diger");
        final var other09 = new ItemEntity("Elektronik Bilgisayar", "Diger");

        saveItemsToDB(other01, other02, other03, other04, other05, other06, other07, other08, other09);
    }

    private void saveItemsToDB(ItemEntity... item) {
        itemRepository.saveAll(Arrays.asList(item));
    }

}
