package pl.dawid.kaszyca.config;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.dawid.kaszyca.model.Authority;
import pl.dawid.kaszyca.model.City;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.model.auction.*;
import pl.dawid.kaszyca.repository.AuctionRepository;
import pl.dawid.kaszyca.repository.AuthorityRepository;
import pl.dawid.kaszyca.repository.CategoryRepository;
import pl.dawid.kaszyca.repository.UserRepository;
import pl.dawid.kaszyca.service.AttachmentService;
import pl.dawid.kaszyca.vm.AttachmentSaveVM;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


@Component
class DbSeeder implements CommandLineRunner {

    private CategoryRepository categoryRepository;
    private AuthorityRepository authorityRepository;
    private AuctionRepository auctionRepository;
    private AttachmentService attachmentService;
    private UserRepository userRepository;
    private PasswordEncoder password;
    @Autowired
    PasswordEncoder encoder;

    public DbSeeder(AuthorityRepository authorityRepository, CategoryRepository categoryRepository,
                    AuctionRepository auctionRepository, PasswordEncoder password, UserRepository userRepository,
                    AttachmentService attachmentService) {
        this.authorityRepository = authorityRepository;
        this.categoryRepository = categoryRepository;
        this.auctionRepository = auctionRepository;
        this.password = password;
        this.userRepository = userRepository;
        this.attachmentService = attachmentService;
    }

    @Override
    public void run(String... args) throws IOException {
        addRolesAndUser();
        createCategories();
        createExampleAuctions();
        System.out.println("Initialized database");
    }

    private void addRolesAndUser() {
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        authorityRepository.save(authority);
        Set<Authority> authorities = new HashSet<>(authorityRepository.findAll());
        User user = new User();
        user.setActivated(true);
        user.setAuthorities(authorities);
        user.setEmail("testowy@gmail.com");
        user.setFirstName("David");
        user.setLastName("Kaszyca");
        String encryptedPassword = password.encode("admin11");
        user.setPassword(encryptedPassword);
        user.setLogin("admin");
        userRepository.save(user);
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
        }
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
        for (String val : listOfValues) {
            AttributeValues att = new AttributeValues();
            att.setValue(val);
            att.setCategoryAttributes(categoryAttribute);
            attributeValues.add(att);
        }
        return attributeValues;
    }

    private void createExampleAuctions() throws IOException {
        Auction auction = new Auction();
        List<MultipartFile> list = new ArrayList<>();
        list.add(getMultipartFile("dysk.png"));

        City city = new City("Katowice");
        Condition condition = new Condition("Używane");
        Optional<Category> category = categoryRepository.findFirstByCategory("Telefony");
        if (category.isPresent())
            auction.setCategory(category.get());
        auction.setCity(city);
        auction.setCondition(condition.getCondition());
        Optional<User> user = userRepository.findOneByLogin("admin");
        if (user.isPresent())
            auction.setUser(user.get());
        auction.setPhone("666777333");
        auction.setPrice(123.31f);
        auction.setViewers(21312);
        auction.setDescription("description example");
        auction.setTitle("title example");
        auction = auctionRepository.save(auction);
        AttachmentSaveVM attachmentSaveVM = new AttachmentSaveVM();
        attachmentSaveVM.setMainPhotoId(0L);
        attachmentSaveVM.setAuctionId(auction.getId());
        attachmentService.saveAuctionAttachments(list, attachmentSaveVM);


        auction = new Auction();
        city = new City("Mysłowice");
        condition = new Condition("Nowy");
        category = categoryRepository.findFirstByCategory("Tablety");
        if (category.isPresent())
            auction.setCategory(category.get());
        auction.setCity(city);
        auction.setCondition(condition.getCondition());
        user = userRepository.findOneByLogin("admin");
        if (user.isPresent())
            auction.setUser(user.get());
        auction.setPhone("622333444");
        auction.setPrice(1239.99f);
        auction.setViewers(1212);
        auction.setDescription("Przykładowy opis");
        auction.setTitle("Przykładowy tytuł");
        auction = auctionRepository.save(auction);
        attachmentSaveVM.setAuctionId(auction.getId());
        list = new ArrayList<>();
        list.add(getMultipartFile("intel.png"));
        attachmentService.saveAuctionAttachments(list, attachmentSaveVM);

        auction = new Auction();
        city = new City("Sosnowiec");
        condition = new Condition("Nowy");
        category = categoryRepository.findFirstByCategory("Laptopy");
        if (category.isPresent())
            auction.setCategory(category.get());
        auction.setCity(city);
        auction.setCondition(condition.getCondition());
        user = userRepository.findOneByLogin("admin");
        if (user.isPresent())
            auction.setUser(user.get());
        auction.setPhone("233444555");
        auction.setPrice(999.99f);
        auction.setViewers(112);
        auction.setDescription("Tu będzie opis");
        auction.setTitle("Tu będzie tutuł");
        auction = auctionRepository.save(auction);
        attachmentSaveVM.setAuctionId(auction.getId());
        list = new ArrayList<>();
        list.add(getMultipartFile("pc.png"));
        attachmentService.saveAuctionAttachments(list, attachmentSaveVM);

        auction = new Auction();
        city = new City("Kraków");
        condition = new Condition("Używany");
        category = categoryRepository.findFirstByCategory("Telewizory");
        if (category.isPresent())
            auction.setCategory(category.get());
        auction.setCity(city);
        auction.setCondition(condition.getCondition());
        user = userRepository.findOneByLogin("admin");
        if (user.isPresent())
            auction.setUser(user.get());
        auction.setPhone("222333444");
        auction.setPrice(99.99f);
        auction.setViewers(12);
        auction.setDescription("Przykładowy opis12");
        auction.setTitle("Przykładowy tytuł12");
        auction = auctionRepository.save(auction);
        attachmentSaveVM.setAuctionId(auction.getId());
        list = new ArrayList<>();
        list.add(getMultipartFile("karta.png"));
        attachmentService.saveAuctionAttachments(list, attachmentSaveVM);
    }

    private MultipartFile getMultipartFile(String name) throws IOException {
        File file = new File("src/main/resources/attachment/" + name);
        FileInputStream input = new FileInputStream(file);
        return new MockMultipartFile("file",
                file.getName(), "image/png", IOUtils.toByteArray(input));
    }
}
