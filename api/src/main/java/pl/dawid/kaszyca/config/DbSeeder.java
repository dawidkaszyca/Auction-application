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
        setCategories();
        createExampleAuctions();
        System.out.println("Initialized database");
    }

    private void addRolesAndUser() {
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        authorityRepository.save(authority);
        authority.setName(AuthoritiesConstants.ADMIN);
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

    private void setCategories() {
        categoryRepository.save(new Category("phone"));
        categoryRepository.save(new Category("tablet"));
        categoryRepository.save(new Category("tv"));
        categoryRepository.save(new Category("pc"));
        categoryRepository.save(new Category("consoleGames"));
        categoryRepository.save(new Category("photo"));
        categoryRepository.save(createCategory());
    }

    private Category createCategory() {
        Category category = new Category();
        category.setCategory("category1");
        category.setCategoryAttributes(createCategoryAttributes(category));
        return category;
    }

    private List<CategoryAttributes> createCategoryAttributes(Category category) {
        List<CategoryAttributes> categoryAttributesList = new ArrayList();
        for (int i = 0; i < 10; i++) {
            CategoryAttributes categoryAttributes = new CategoryAttributes();
            categoryAttributes.setId("attribute" + i);
            categoryAttributes.setCategory(category);
            categoryAttributes.setAttributeValues(createAttributeValues(categoryAttributes, i + 1));
            categoryAttributesList.add(categoryAttributes);
        }
        return categoryAttributesList;
    }

    private List<AttributeValues> createAttributeValues(CategoryAttributes categoryAttributes, int number) {
        List<AttributeValues> attributeValuesList = new ArrayList<>();
        for (int i = 0; i < 1 * number; i++) {
            AttributeValues attributeValues = new AttributeValues();
            attributeValues.setValue("values" + i);
            attributeValues.setCategoryAttributes(categoryAttributes);
            attributeValuesList.add(attributeValues);
        }
        return attributeValuesList;
    }

    private void createExampleAuctions() throws IOException {
        Auction auction = new Auction();
        List<MultipartFile> list = new ArrayList<>();
        list.add(getMultipartFile("dysk.png"));

        City city = new City("Katowice");
        Condition condition = new Condition("Używane");
        Optional<Category> category = categoryRepository.findFirstByCategory("phone");
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
        category = categoryRepository.findFirstByCategory("tablet");
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
        category = categoryRepository.findFirstByCategory("pc");
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
        category = categoryRepository.findFirstByCategory("tv");
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
