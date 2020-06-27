package pl.dawid.kaszyca.config;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.dawid.kaszyca.model.*;
import pl.dawid.kaszyca.model.auction.*;
import pl.dawid.kaszyca.repository.*;
import pl.dawid.kaszyca.service.AttachmentService;
import pl.dawid.kaszyca.vm.AttachmentToSaveVM;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Component
class DbSeeder implements CommandLineRunner {

    private CategoryRepository categoryRepository;
    private AuthorityRepository authorityRepository;
    private AuctionRepository auctionRepository;
    private AttachmentService attachmentService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ConversationRepository conversationRepository;
    Map<String, List<String>> categoriesMap;
    Map<String, List<String>> attributesMap;
    Map<String, List<String>> attachmentMap;
    List<City> cityList;
    List<String> categoryList;
    Random random;
    @Autowired
    PasswordEncoder encoder;

    public DbSeeder(AuthorityRepository authorityRepository, CategoryRepository categoryRepository,
                    AuctionRepository auctionRepository, PasswordEncoder password, UserRepository userRepository,
                    AttachmentService attachmentService, ConversationRepository conversationRepository) {
        this.authorityRepository = authorityRepository;
        this.categoryRepository = categoryRepository;
        this.auctionRepository = auctionRepository;
        this.passwordEncoder = password;
        this.userRepository = userRepository;
        this.attachmentService = attachmentService;
        this.conversationRepository = conversationRepository;
        this.random = new Random();
        categoryList = getCategoryList();
    }

    @Override
    public void run(String... args) throws IOException, InterruptedException {
        categoriesMap = new HashMap<>();
        attributesMap = new HashMap<>();
        attachmentMap = getAttachments();
        cityList = getCityList();
        User sender = addRolesAndUser("testowy@wp.pl", "Dawid", " Kaszyca", "admin", "admin11");
        User recipient = addRolesAndUser("testowy12@wp.pl", "Stefan", " Wąs", "admin1", "admin11");
        //createExampleMessage(sender, recipient);
        recipient = addRolesAndUser("testowy123@wp.pl", "Janusz", " Kors", "admin2", "admin11");
        //createExampleMessage(sender, recipient);
        createCategories();
        createExampleAuctions();
        System.out.println("Initialized database");
    }

    private void createExampleMessage(User sender, User recipient) throws InterruptedException {
        Conversation recipientChat = new Conversation();
        Conversation senderChat = new Conversation();
        Message message = new Message();
        message.setConversation(senderChat);
        message.setContent("Hi!!!");
        message.setDisplayed(true);
        senderChat.setSender(sender);
        senderChat.setRecipient(recipient);
        senderChat.setRecipientMessage(recipientChat);
        senderChat.setSentMessages(Arrays.asList(message));
        conversationRepository.save(senderChat);
        TimeUnit.SECONDS.sleep(2);
        Message msg = new Message();
        msg.setConversation(recipientChat);
        msg.setContent("Yo !!!");
        recipientChat.setSender(recipient);
        recipientChat.setRecipient(sender);
        recipientChat.setRecipientMessage(senderChat);
        senderChat.setSentMessages(Arrays.asList(msg));
        conversationRepository.save(recipientChat);
    }

    private User addRolesAndUser(String email, String firstName, String lastName, String login, String password) {
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        authorityRepository.save(authority);
        Set<Authority> authorities = new HashSet<>(authorityRepository.findAll());
        User user = new User();
        user.setActivated(true);
        user.setAuthorities(authorities);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);
        user.setLogin(login);
        userRepository.save(user);
        return user;
    }

    private void createCategories() {
        categoryRepository.save(createCategory("Telefony"));
        categoryRepository.save(createCategory("Tablety"));
        categoryRepository.save(createCategory("Telewizory"));
        categoryRepository.save(createCategory("Laptopy"));
        categoryRepository.save(createCategory("Gry i konsole"));
        categoryRepository.save(new Category("Fotografia"));
    }

    private Category createCategory(String value) {
        Category category = new Category(value);
        category.setCategoryAttributes(getAttributeArray(value, category));
        return category;
    }

    private List<CategoryAttributes> getAttributeArray(String attr, Category category) {
        List<String> attributes;
        List<CategoryAttributes> categoryAttributes = new ArrayList<>();
        switch (attr) {
            case ("Telefony"):
            case ("Tablety"):
                attributes = Arrays.asList("Marka", "Ram", "Aparat");
                break;
            case ("Laptopy"):
                attributes = Arrays.asList("Marka", "Ram", "Wyświetlacz", "Dysk", "Grafika");
                break;
            case ("Telewizory"):
                attributes = Arrays.asList("Marka", "Wyświetlacz", "Roździelczośc");
                break;
            case ("Gry i konsole"):
                attributes = Arrays.asList("Wersja gry", "Tryb gry", "Platforma", "Kategoria");
                break;
            default:
                attributes = new ArrayList<>();
                break;
        }
        categoriesMap.put(attr, attributes);
        for (String value : attributes) {
            CategoryAttributes categoryAttribute = new CategoryAttributes();
            categoryAttribute.setAttribute(value);
            categoryAttribute.setCategory(category);
            categoryAttribute.setAttributeValues(getValuesByKey(value, categoryAttribute));
            categoryAttributes.add(categoryAttribute);
        }
        return categoryAttributes;
    }

    private List<AttributeValues> getValuesByKey(String value, CategoryAttributes categoryAttribute) {
        List<AttributeValues> attributeValues = new ArrayList<>();
        List<String> listOfValues;
        switch (value) {
            case ("Marka"):
                listOfValues = Arrays.asList("Samsung ", "Apple", "Huawei", "LG", "Sony", "Xiaomi", "HTC");
                break;
            case ("Grafika"):
                listOfValues = Arrays.asList("1GB", "2GB", "4GB", "8GB");
                break;
            case ("Ram"):
                listOfValues = Arrays.asList("1GB", "2GB", "4GB", "8GB", "16GB", "32GB");
                break;
            case ("Wyświetlacz"):
                listOfValues = Arrays.asList("15 ", "17", "19");
                break;
            case ("Aparat"):
                listOfValues = Arrays.asList("3px", "5px", "10px", "13px", "15px", "20px", ">20px");
                break;
            case ("Dysk"):
                listOfValues = Arrays.asList("64GB", "128GB", "256GB", "512GB", "1TB", "2TB", "4TB");
                break;
            case ("Roździelczośc"):
                listOfValues = Arrays.asList("1024×768", "1280×720", "1920×1080", "4096×2304", "4096×2048", "4096×2048");
                break;
            case ("Wersja gry"):
                listOfValues = Arrays.asList("pudełkowa", "cyfrowa");
                break;
            case ("Tryb gry"):
                listOfValues = Arrays.asList("singleplayer", "multiplayer");
                break;
            case ("Platforma"):
                listOfValues = Arrays.asList("Pc", "Ps1", "Ps2", "Ps3 ", "Ps4", "Xbox", "Xbox 360", "Xbox One", "PSP", "Nintendo Switch");
                break;
            case ("Kategoria"):
                listOfValues = Arrays.asList("Bijatyki", "Strzelanki", "MMORPG", "Gry wyścigowe", "Gry sportowe", "Strategiczne", "Muzyczne", "Przygodowe", "Dla dzieci");
                break;
            default:
                listOfValues = new ArrayList<>();
        }
        attributesMap.put(value, listOfValues);
        for (String val : listOfValues) {
            AttributeValues att = new AttributeValues();
            att.setValue(val);
            att.setCategoryAttributes(categoryAttribute);
            attributeValues.add(att);
        }
        return attributeValues;
    }

    private void createExampleAuctions() throws IOException {
        Auction auction;
        List<MultipartFile> list;
        List<AuctionDetails> auctionDetails;
        for (int i = 0; i < 100; i++) {
            auction = new Auction();
            Condition condition;
            if (i % 2 == 0)
                condition = new Condition("Używany");
            else
                condition = new Condition("Nowy");
            String categoryString = getRandomCategory();
            Optional<Category> category = categoryRepository.findFirstByCategory(categoryString);
            if (category.isPresent())
                auction.setCategory(category.get());
            auction.setCondition(condition.getCondition());
            Optional<User> user = userRepository.findOneByLogin("admin");
            if (user.isPresent())
                auction.setUser(user.get());
            auction.setCity(getRandomCity());
            auction.setPhone(getRandomPhoneNumber());
            auction.setPrice(getRandomPrice());
            auction.setViewers(getRandomPrice());
            auction.setDescription(getDescription());
            auction.setTitle(getTitle());
            if (!categoryString.equals("Fotografia")) {
                auctionDetails = getDetailsByCategory(categoryString, auction);
                auction.setAuctionDetails(auctionDetails);
            }
            auction = auctionRepository.save(auction);
            AttachmentToSaveVM attachmentToSaveVM = new AttachmentToSaveVM();
            attachmentToSaveVM.setMainPhotoId(0L);
            attachmentToSaveVM.setAuctionId(auction.getId());
            list = new ArrayList<>();
            list.add(getMultipartFile(getRandomImageNameByCategory(categoryString)));
            list.add(getMultipartFile(getRandomImageNameByCategory(categoryString)));
            attachmentService.saveAuctionAttachments(list, attachmentToSaveVM);
        }
    }

    private String getTitle() {
        String lorrem = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        List<String> arr = Arrays.asList(lorrem.split(" "));
        return arr.get(random.nextInt(arr.size())) + arr.get(random.nextInt(arr.size()));
    }

    private String getRandomPhoneNumber() {
        String phone = "";
        for (int i = 0; i < 9; i++) {
            phone += String.valueOf(random.nextInt(9));
        }
        return phone;
    }

    private MultipartFile getMultipartFile(String name) throws IOException {
        File file = new File("src/main/resources/attachment/" + name);
        FileInputStream input = new FileInputStream(file);
        return new MockMultipartFile("file",
                file.getName(), "image/png", IOUtils.toByteArray(input));
    }

    private List<City> getCityList() {
        List<City> list = new ArrayList<>();
        list.add(new City("Katowice", 19.02378149999999, 50.26489189999999));
        list.add(new City("Myslowice", 19.1660513, 50.2080466));
        list.add(new City("Zabrze", 18.7857186, 50.3249278));
        list.add(new City("Krakow", 19.9449799, 50.06465009999999));
        list.add(new City("Chorzow", 18.9545728, 50.2974884));
        list.add(new City("Sosnowiec", 19.1040791, 50.28626380000001));
        list.add(new City("Warsaw", 21.0122287, 52.2296756));
        list.add(new City("Siemianowice Slaskie", 19.0295714, 50.3264314));
        list.add(new City("Augustow", 22.9796024, 53.84344309999999));
        list.add(new City("Kalisz", 18.0853462, 51.7672799));
        list.add(new City("Szczecin", 14.5528116, 53.4285438));
        return list;
    }

    private List<AuctionDetails> getDetailsByCategory(String categoryString, Auction auction) {
        List<String> attr = categoriesMap.get(categoryString);
        List<AuctionDetails> details = new ArrayList<>();
        AuctionDetails ad;
        for (String att : attr) {
            ad = new AuctionDetails();
            ad.setAuction(auction);
            ad.setCategoryAttribute(att);
            ad.setAttributeValue(getRandomAttValue(att));
            details.add(ad);
        }
        return details;
    }

    private Map<String, List<String>> getAttachments() {
        Map<String, List<String>> att = new HashMap<>();
        att.put("Telefony", Arrays.asList("phone.png", "phone1.png", "phone3.png", "clock.png", "clock1.png"));
        att.put("Tablety", Arrays.asList("tablet.png", "tablet1.png", "tablet2.png", "tablet5.png"));
        att.put("Telewizory", Arrays.asList("tv1.png", "tv2.png", "phone3.png"));
        att.put("Laptopy", Arrays.asList("laptop.png", "laptop1.png", "laptop12.png", "tablet12.png", "display.png", "dysk.png", "intel.png", "karta.png", "headphone.png", "pc.png", "keyboard.png"));
        att.put("Gry i konsole", Arrays.asList("dirt.png", "gta.png", "grid2.png"));
        att.put("Fotografia", Arrays.asList("camera.png", "camera1.png", "camera3.png"));
        return att;
    }

    private String getRandomImageNameByCategory(String category) {
        List<String> list = attachmentMap.get(category);
        return list.get(random.nextInt(list.size()));
    }

    private String getRandomAttValue(String att) {
        List<String> list = attributesMap.get(att);
        return list.get(random.nextInt(list.size()));
    }

    private int getRandomPrice() {
        return random.nextInt(100000);
    }

    private City getRandomCity() {
        City city = cityList.get(random.nextInt(cityList.size()));
        city.setId(null);
        return city;
    }

    private String getRandomCategory() {
        return categoryList.get(random.nextInt(categoryList.size()));
    }

    private String getDescription() {
        String lorrem = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of";
        return lorrem.substring(0, random.nextInt(lorrem.length() / 2));
    }

    private List<String> getCategoryList() {
        List<String> list = new ArrayList<>();
        list.add("Telefony");
        list.add("Tablety");
        list.add("Telewizory");
        list.add("Laptopy");
        list.add("Gry i konsole");
        list.add("Fotografia");
        return list;
    }
}
