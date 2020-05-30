package pl.dawid.kaszyca.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.dawid.kaszyca.model.Attachment;
import pl.dawid.kaszyca.model.Image;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.repository.AuctionRepository;
import pl.dawid.kaszyca.repository.ImageRepository;
import pl.dawid.kaszyca.vm.AttachmentSaveVM;

import java.io.IOException;
import java.util.*;

@Service
public class AttachmentService {

    AuctionRepository auctionRepository;
    ImageRepository imageRepository;
    UserService userService;

    public AttachmentService(AuctionRepository auctionRepository,
                             ImageRepository imageRepository, UserService userService) {
        this.auctionRepository = auctionRepository;
        this.imageRepository = imageRepository;
        this.userService = userService;
    }

    public void saveAuctionAttachments(List<MultipartFile> files, AttachmentSaveVM attachmentSaveVM) throws IOException {
        Optional<Auction> auction = auctionRepository.findById(attachmentSaveVM.getAuctionId());
        List<Attachment> attachments = prepareAttachmentToSave(files);
        if (auction.isPresent()) {
            Auction auctionObj = auction.get();
            List<Image> imageList = prepareImageListToSave(auctionObj, attachments, attachmentSaveVM.getMainPhotoId());
            auctionObj.setImages(imageList);
            auctionRepository.save(auctionObj);
        }
    }

    private List<Attachment> prepareAttachmentToSave(List<MultipartFile> fileList) throws IOException {
        List<Attachment> attachmentList = new ArrayList<>();
        for (MultipartFile file : fileList) {
            attachmentList.add(convertMultiPartFileToAttachment(file));
        }
        return attachmentList;
    }

    private Attachment convertMultiPartFileToAttachment(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        return new Attachment(fileName, file.getContentType(), file.getBytes());
    }

    private List<Image> prepareImageListToSave(Auction auction, List<Attachment> attachments, Long mainPhotoId) {
        List<Image> imageList = new ArrayList<>();
        for (int i = 0; i < attachments.size(); i++) {
            Attachment attachment = attachments.get(i);
            Image image = new Image();
            image.setAttachment(attachment);
            image.setAuction(auction);
            if (i == mainPhotoId)
                image.setIsMainAuctionPhoto(true);
            imageList.add(image);
        }
        return imageList;
    }

    public Map<String, String> getPhotosByListId(List<Long> idOfAuctionMainPhotoToGet) {
        Map<String, String> photosUrl = new HashMap<>();
        for (Long id : idOfAuctionMainPhotoToGet) {
            Optional<Image> image = imageRepository.findFirstByAuctionIdAndIsMainAuctionPhoto(id, true);
            if (image.isPresent()) {
                String url = convertImageToResponseIfExist(image.get().getAttachment());
                if (url != null)
                    photosUrl.put(id.toString(), url);
            }
        }
        return photosUrl;
    }

    public Map<String, String> getPhotosForAuctionById(long auctionId) {
        Map<String, String> photosUrl = new HashMap<>();
        List<Image> images = imageRepository.findAllByAuctionId(auctionId);
        int it = 2;
        for (Image image : images) {
            String url = convertImageToResponseIfExist(image.getAttachment());
            if (image.getIsMainAuctionPhoto())
                photosUrl.put("1", url);
            else {
                photosUrl.put(String.valueOf(it), url);
                it++;
            }
        }
        return photosUrl;
    }

    private String convertImageToResponseIfExist(Attachment attachment) {
        if (attachment != null) {
            byte[] bytes = attachment.getData();
            String encodeBase64 = Base64.getEncoder().encodeToString(bytes);
            return "data:image/" + "png" + ";base64," + encodeBase64;
        }
        return null;
    }

    public List<String> getUserPhoto() {
        Optional<User> user = userService.getCurrentUserObject();
        List<String> userUrl = new ArrayList<>();
        if (user.isPresent())
            userUrl.add(convertImageToResponseIfExist(user.get().getProfile_Image()));
        return userUrl;
    }

    public void saveUserPhoto(MultipartFile file) throws IOException {
        Optional<User> user = userService.getCurrentUserObject();
        if (user.isPresent()) {
            user.get().setProfile_Image(convertMultiPartFileToAttachment(file));
            userService.updateUser(user.get());
        }
    }
}
