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

        final var random = new Random();
        final var requirements = requirementRepository.findAll();

        requirements.forEach(req -> {

            final var messages = IntStream.range(0, 2).mapToObj(index -> {

                final var randomChannel = random.nextInt(2);

                final var message = new MessageEntity();
                message.setSender(req.getPerson());
                message.setChannel(randomChannel % 2 == 0 ? "twitter" : "whatsapp");
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

    public Map<String, Double[]> generateCities() {
        final var cities = new HashMap<String, Double[]>(); // city, [latitude, longitude]
        cities.put("Istanbul", new Double[] { 41.0082, 28.9784 });
        cities.put("Ankara", new Double[] { 39.9208, 32.8541 });
        cities.put("Izmir", new Double[] { 38.4189, 27.1287 });
        cities.put("Bursa", new Double[] { 40.1824, 29.0616 });
        cities.put("Adana", new Double[] { 37.0017, 35.3289 });
        cities.put("Gaziantep", new Double[] { 37.0594, 37.3828 });
        cities.put("Konya", new Double[] { 37.8715, 32.4843 });
        cities.put("Antalya", new Double[] { 36.9081, 30.6956 });
        cities.put("Kayseri", new Double[] { 38.7312, 35.4784 });
        cities.put("Eskisehir", new Double[] { 39.7767, 30.5206 });
        cities.put("Samsun", new Double[] { 41.2928, 36.3313 });
        cities.put("Diyarbakir", new Double[] { 37.9146, 40.2306 });
        cities.put("Sanliurfa", new Double[] { 37.1671, 38.7939 });
        cities.put("Malatya", new Double[] { 38.3558, 38.3096 });
        cities.put("Gebze", new Double[] { 40.7997, 29.4308 });
        cities.put("Denizli", new Double[] { 37.7765, 29.0864 });
        cities.put("Manisa", new Double[] { 38.6138, 27.4216 });
        cities.put("Kahramanmaras", new Double[] { 37.5853, 36.9376 });
        cities.put("Mersin", new Double[] { 36.8, 34.6333 });
        cities.put("Sivas", new Double[] { 39.7452, 37.0168 });
        cities.put("Kocaeli", new Double[] { 40.8531, 29.8813 });
        cities.put("Adiyaman", new Double[] { 37.7648, 38.2786 });
        cities.put("Batman", new Double[] { 37.8814, 41.1357 });
        cities.put("Kirsehir", new Double[] { 39.1422, 34.1708 });
        cities.put("Kastamonu", new Double[] { 41.3888, 33.7825 });
        cities.put("Karaman", new Double[] { 37.175, 33.2281 });
        cities.put("Kilis", new Double[] { 36.7187, 37.1212 });
        cities.put("Osmaniye", new Double[] { 37.0742, 36.2461 });
        cities.put("Rize", new Double[] { 41.0208, 40.5236 });
        cities.put("Trabzon", new Double[] { 41.0053, 39.7267 });
        cities.put("Zonguldak", new Double[] { 41.4567, 31.7986 });
        cities.put("Aydin", new Double[] { 37.8444, 27.8456 });
        cities.put("Balikesir", new Double[] { 39.6484, 27.8826 });
        cities.put("Bilecik", new Double[] { 40.1539, 29.9792 });
        cities.put("Bingol", new Double[] { 38.8857, 40.4981 });
        cities.put("Bitlis", new Double[] { 38.3953, 42.124 });
        cities.put("Bolu", new Double[] { 40.7356, 31.6069 });
        cities.put("Burdur", new Double[] { 37.7217, 30.2881 });
        cities.put("Canakkale", new Double[] { 40.1539, 26.4064 });
        cities.put("Corum", new Double[] { 40.5503, 34.9556 });
        cities.put("Denizli", new Double[] { 37.7765, 29.0864 });
        cities.put("Duzce", new Double[] { 40.8431, 31.1565 });
        cities.put("Edirne", new Double[] { 41.6764, 26.5554 });
        cities.put("Elazig", new Double[] { 38.674, 39.2237 });
        cities.put("Erzincan", new Double[] { 39.7507, 39.4922 });
        cities.put("Erzurum", new Double[] { 39.9042, 41.2672 });
        cities.put("Giresun", new Double[] { 40.9128, 38.3895 });
        cities.put("Hatay", new Double[] { 36.4014, 36.349 });
        cities.put("Igdir", new Double[] { 39.9172, 44.0361 });
        cities.put("Iskenderun", new Double[] { 36.5833, 36.1667 });
        cities.put("Isparta", new Double[] { 37.7648, 30.5567 });
        cities.put("Kahramanmaras", new Double[] { 37.5853, 36.9376 });
        cities.put("Karabuk", new Double[] { 41.2, 32.6333 });
        cities.put("Karaman", new Double[] { 37.175, 33.2281 });
        cities.put("Kars", new Double[] { 40.6083, 43.0972 });
        cities.put("Kastamonu", new Double[] { 41.3888, 33.7825 });
        cities.put("Kayseri", new Double[] { 38.7312, 35.4784 });
        cities.put("Kilis", new Double[] { 36.7187, 37.1212 });
        cities.put("Kirikkale", new Double[] { 39.8467, 33.5158 });
        cities.put("Kirklareli", new Double[] { 41.7378, 27.2166 });
        cities.put("Kirsehir", new Double[] { 39.1422, 34.1708 });
        cities.put("Kocaeli", new Double[] { 40.8531, 29.8813 });
        cities.put("Konya", new Double[] { 37.8715, 32.4849 });
        cities.put("Kutahya", new Double[] { 39.4237, 29.9833 });
        cities.put("Malatya", new Double[] { 38.3557, 38.3096 });
        cities.put("Manisa", new Double[] { 38.6138, 27.4216 });
        cities.put("Mardin", new Double[] { 37.3111, 40.7425 });
        cities.put("Mersin", new Double[] { 36.8, 34.6333 });
        cities.put("Nevsehir", new Double[] { 38.6244, 34.7239 });
        cities.put("Nigde", new Double[] { 37.9761, 34.6853 });
        cities.put("Ordu", new Double[] { 40.9833, 37.8833 });
        cities.put("Osmaniye", new Double[] { 37.0742, 36.2461 });
        cities.put("Rize", new Double[] { 41.0208, 40.5236 });
        cities.put("Sakarya", new Double[] { 40.6944, 30.4383 });
        cities.put("Samsun", new Double[] { 41.2865, 36.3314 });
        cities.put("Siirt", new Double[] { 37.9442, 41.9325 });
        cities.put("Sinop", new Double[] { 42.0231, 35.1539 });
        cities.put("Sirnak", new Double[] { 37.5228, 42.4611 });
        cities.put("Sivas", new Double[] { 39.7456, 37.0172 });
        cities.put("Tekirdag", new Double[] { 40.9833, 27.5167 });
        cities.put("Tokat", new Double[] { 40.3167, 36.55 });
        cities.put("Trabzon", new Double[] { 41.0053, 39.7267 });
        cities.put("Tunceli", new Double[] { 39.1167, 39.5333 });
        cities.put("Usak", new Double[] { 38.6828, 29.4081 });
        cities.put("Van", new Double[] { 38.4947, 43.38 });
        cities.put("Yalova", new Double[] { 40.65, 29.2667 });
        cities.put("Yozgat", new Double[] { 39.8189, 34.8142 });
        cities.put("Zonguldak", new Double[] { 41.4567, 31.7989 });

        return cities;
    }

    public List<String[]> generateRandomTurkishNames() {
        
        // Ali Kuşçu, Ayşe Yılmaz, Mehmet Ak, Fatma Özdemir, Hasan Kaya, Özlem Çelik,
        // Mustafa Yıldız, Zeynep Korkmaz, Ahmet Yıldırım, Emine Kılıç, Mehmet Çetin,
        // Ayşe Kaya, Hasan Yılmaz, Özlem Yıldırım, Mustafa Korkmaz, Zeynep Çelik,
        // Ahmet Kılıç, Emine Çetin, Mehmet Kaya, Ayşe Yıldırım, Hasan Korkmaz,
        // Özlem Kılıç, Mustafa Çelik, Zeynep Çetin, Ahmet Yılmaz, Emine Yıldırım,
        // Mehmet Korkmaz, Ayşe Kılıç, Hasan Çelik, Özlem Çetin, Mustafa Yılmaz,
        // Zeynep Yıldırım, Ahmet Korkmaz, Emine Kılıç, Mehmet Çelik, Ayşe Çetin,
        // Hasan Yılmaz, Özlem Yıldırım, Mustafa Kılıç, Zeynep Çetin, Ahmet Çelik,
        // Emine Yılmaz, Mehmet Yıldırım, Ayşe Korkmaz, Hasan Kılıç, Özlem Çelik,
        // Mustafa Çetin, Zeynep Yılmaz, Ahmet Yıldırım, Emine Korkmaz, Mehmet Kılıç,
        // Ayşe Çelik, Hasan Çetin, Özlem Yılmaz, Mustafa Yıldırım, Zeynep Korkmaz,
        // Ahmet Kılıç, Emine Çetin, Mehmet Çelik, Ayşe Yılmaz, Hasan Yıldırım,
        // Özlem Korkmaz, Mustafa Kılıç, Zeynep Çelik, Ahmet Çetin, Emine Yılmaz,
        // Mehmet Yıldırım, Ayşe Korkmaz, Hasan Kılıç, Özlem Çelik, Mustafa Çetin,
        // Piri Reis, İsmail Hakkı Baltacıoğlu, Afet İnan, Hüseyin Avni Dilligil,
        // Muazzez İlmiye Çığ, Türkan Saylan, Aziz Sancar,

        final var names = new ArrayList<String[]>();

        names.add(new String[] { "Ali", "Kuşçu" });
        names.add(new String[] { "Ayşe", "Yılmaz" });
        names.add(new String[] { "Mehmet", "Ak" });
        names.add(new String[] { "Fatma", "Özdemir" });
        names.add(new String[] { "Hasan", "Kaya" });
        names.add(new String[] { "Özlem", "Çelik" });
        names.add(new String[] { "Mustafa", "Yıldız" });
        names.add(new String[] { "Zeynep", "Korkmaz" });
        names.add(new String[] { "Ahmet", "Yıldırım" });
        names.add(new String[] { "Emine", "Kılıç" });
        names.add(new String[] { "Mehmet", "Çetin" });
        names.add(new String[] { "Ayşe", "Kaya" });
        names.add(new String[] { "Hasan", "Yılmaz" });
        names.add(new String[] { "Özlem", "Yıldırım" });
        names.add(new String[] { "Mustafa", "Korkmaz" });
        names.add(new String[] { "Zeynep", "Çelik" });
        names.add(new String[] { "Ahmet", "Kılıç" });
        names.add(new String[] { "Emine", "Çetin" });
        names.add(new String[] { "Mehmet", "Kaya" });
        names.add(new String[] { "Ayşe", "Yıldırım" });
        names.add(new String[] { "Hasan", "Korkmaz" });
        names.add(new String[] { "Özlem", "Kılıç" });
        names.add(new String[] { "Mustafa", "Çelik" });
        names.add(new String[] { "Zeynep", "Çetin" });
        names.add(new String[] { "Ahmet", "Yılmaz" });
        names.add(new String[] { "Emine", "Yıldırım" });
        names.add(new String[] { "Mehmet", "Korkmaz" });
        names.add(new String[] { "Ayşe", "Kılıç" });
        names.add(new String[] { "Hasan", "Çelik" });
        names.add(new String[] { "Özlem", "Çetin" });
        names.add(new String[] { "Mustafa", "Yılmaz" });
        names.add(new String[] { "Zeynep", "Yıldırım" });
        names.add(new String[] { "Ahmet", "Korkmaz" });
        names.add(new String[] { "Emine", "Kılıç" });
        names.add(new String[] { "Mehmet", "Çelik" });
        names.add(new String[] { "Ayşe", "Çetin" });
        names.add(new String[] { "Hasan", "Yılmaz" });
        names.add(new String[] { "Özlem", "Yıldırım" });
        names.add(new String[] { "Mustafa", "Kılıç" });
        names.add(new String[] { "Zeynep", "Çelik" });
        names.add(new String[] { "Ahmet", "Çetin" });
        names.add(new String[] { "Emine", "Yılmaz" });
        names.add(new String[] { "Mehmet", "Yıldırım" });
        names.add(new String[] { "Ayşe", "Korkmaz" });
        names.add(new String[] { "Hasan", "Kılıç" });
        names.add(new String[] { "Özlem", "Çelik" });
        names.add(new String[] { "Mustafa", "Çetin" });
        names.add(new String[] { "Zeynep", "Yılmaz" });
        names.add(new String[] { "Ahmet", "Yıldırım" });
        names.add(new String[] { "Emine", "Korkmaz" });
        names.add(new String[] { "Mehmet", "Kılıç" });
        names.add(new String[] { "Ayşe", "Çelik" });
        names.add(new String[] { "Hasan", "Çetin" });
        names.add(new String[] { "Özlem", "Yılmaz" });
        names.add(new String[] { "Mustafa", "Yıldırım" });
        names.add(new String[] { "Zeynep", "Korkmaz" });
        names.add(new String[] { "Ahmet", "Kılıç" });
        names.add(new String[] { "Emine", "Çelik" });
        names.add(new String[] { "Mehmet", "Çetin" });
        names.add(new String[] { "Ayşe", "Yılmaz" });
        names.add(new String[] { "Hasan", "Yıldırım" });
        names.add(new String[] { "Özlem", "Korkmaz" });
        names.add(new String[] { "Mustafa", "Kılıç" });
        names.add(new String[] { "Zeynep", "Çelik" });
        names.add(new String[] { "Ahmet", "Çetin" });
        names.add(new String[] { "Emine", "Yılmaz" });
        names.add(new String[] { "Mehmet", "Yıldırım" });
        names.add(new String[] { "Ayşe", "Korkmaz" });
        names.add(new String[] { "Hasan", "Kılıç" });
        names.add(new String[] { "Özlem", "Çelik" });
        names.add(new String[] { "Mustafa", "Çetin" });
        names.add(new String[] { "Zeynep", "Yılmaz" });
        names.add(new String[] { "Ahmet", "Yıldırım" });
        names.add(new String[] { "Emine", "Korkmaz" });
        names.add(new String[] { "Mehmet", "Kılıç" });
        names.add(new String[] { "Ayşe", "Çelik" });
        names.add(new String[] { "Hasan", "Çetin" });
        names.add(new String[] { "Özlem", "Yılmaz" });
        names.add(new String[] { "Mustafa", "Yıldırım" });
        names.add(new String[] { "Zeynep", "Korkmaz" });
        names.add(new String[] { "Ahmet", "Kılıç" });
        names.add(new String[] { "Emine", "Çelik" });
        names.add(new String[] { "Mehmet", "Çetin" });
        names.add(new String[] { "Ayşe", "Yılmaz" });
        names.add(new String[] { "Hasan", "Yıldırım" });
        names.add(new String[] { "Özlem", "Korkmaz" });
        names.add(new String[] { "Mustafa", "Kılıç" });
        names.add(new String[] { "Zeynep", "Çelik" });
        names.add(new String[] { "Ahmet", "Çetin" });
        names.add(new String[] { "Emine", "Yılmaz" });
        names.add(new String[] { "Mehmet", "Yıldırım" });
        names.add(new String[] { "Ayşe", "Korkmaz" });
        names.add(new String[] { "Hasan", "Kılıç" });
        names.add(new String[] { "Özlem", "Çelik" });
        names.add(new String[] { "Mustafa", "Çetin" });
        names.add(new String[] { "Zeynep", "Yılmaz" });
        names.add(new String[] { "Ahmet", "Yıldırım" });
        names.add(new String[] { "Emine", "Korkmaz" });

        return names;
    }

    public void seedPeople() {
        final var avatar = "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=mp&f=y";
        final var random = new Random();
        final var people = new ArrayList<PersonEntity>();
        final var cities = generateCities();
        final var names = generateRandomTurkishNames();

        cities.forEach((c, l) -> {

            IntStream.rangeClosed(1, 100).forEach(i -> {

                final var person = new PersonEntity();
                person.setFirstName(names.get(random.nextInt(names.size()))[0]);
                person.setLastName(names.get(random.nextInt(names.size()))[1]);
                person.setImageUrl(avatar);
                person.setPhone("0532" + random.nextInt(9999999) + 1000000);
                final var lat = l[0] + random.nextDouble() - 0.5;
                final var lon = l[1] + random.nextDouble() - 0.5;
                person.setLatitude(lat);
                person.setLongitude(lon);
                person.setRegisteredAt(NOW);
                people.add(person);

            });

        });

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
