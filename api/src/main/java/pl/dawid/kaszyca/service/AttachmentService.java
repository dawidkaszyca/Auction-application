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
import pl.dawid.kaszyca.vm.AttachmentVM;
import pl.dawid.kaszyca.vm.ImageVM;

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

    public void saveAuctionAttachments(List<MultipartFile> files, AttachmentVM attachmentToSaveVM) throws IOException {
        Optional<Auction> auction = auctionRepository.findById(attachmentToSaveVM.getAuctionId());
        List<Attachment> attachments = prepareAttachmentToSave(files);
        if (auction.isPresent()) {
            Auction auctionObj = auction.get();
            List<Image> imageList = prepareImageListToSave(auctionObj, attachments, attachmentToSaveVM.getMainPhotoId());
            auctionObj.setImages(imageList);
            auctionRepository.save(auctionObj);
        }
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

    public List<ImageVM> getPhotosForAuctionById(long auctionId) {
        List<ImageVM> photosUrl = new ArrayList<>();
        List<Image> images = imageRepository.findAllByAuctionId(auctionId);
        for (Image image : images) {
            String url = convertImageToResponseIfExist(image.getAttachment());
            ImageVM imageVM = new ImageVM();
            imageVM.setUrl(url);
            imageVM.setMainPhoto(image.getIsMainAuctionPhoto());
            imageVM.setPhotoId(image.getId());
            photosUrl.add(imageVM);
        }
        return photosUrl;
    }

    public List<String> getUserPhoto() {
        Optional<User> user = userService.getCurrentUserObject();
        List<String> userUrl = new ArrayList<>();
        if (user.isPresent())
            userUrl.add(convertImageToResponseIfExist(user.get().getProfileImage()));
        return userUrl;
    }

    public void saveUserPhoto(MultipartFile file) throws IOException {
        Optional<User> user = userService.getCurrentUserObject();
        if (user.isPresent()) {
            user.get().setProfileImage(convertMultiPartFileToAttachment(file));
            userService.updateUser(user.get());
        }
    }

    public List<String> getUserPhoto(long id) {
        User user = userService.getUserObjectById(id);
        List<String> userUrl = new ArrayList<>();
        userUrl.add(convertImageToResponseIfExist(user.getProfileImage()));
        return userUrl;
    }

    private String convertImageToResponseIfExist(Attachment attachment) {
        if (attachment != null) {
            byte[] bytes = attachment.getData();
            String encodeBase64 = Base64.getEncoder().encodeToString(bytes);
            return "data:image/" + "png" + ";base64," + encodeBase64;
        }
        return null;
    }

    public void updateAuctionAttachments(List<MultipartFile> files, AttachmentVM attachment) throws IOException {
        removeImages(attachment.getIdsToRemoved());
        List<Image> images = imageRepository.findAllByAuctionId(attachment.getAuctionId());
        List<Attachment> attachments = prepareAttachmentToSave(files);
        Optional<Auction> auction = auctionRepository.findById(attachment.getAuctionId());
        changeMainPhoto(images, attachment);
        if (auction.isPresent()) {
            Auction auctionObj = auction.get();
            images.addAll(prepareImageListToSave(auctionObj, attachments, attachment.getMainPhotoId()));
            auctionObj.setImages(images);
            auctionRepository.save(auctionObj);
        }
    }

    private void removeImages(List<Long> idsToRemoved) {
        for (Long id : idsToRemoved) {
            Optional<Image> image = imageRepository.findById(id);
            if (image.isPresent()) {
                imageRepository.delete(image.get());
            }
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

    private void changeMainPhoto(List<Image> images, AttachmentVM attachment) {
        for (Image image : images) {
            image.setIsMainAuctionPhoto(isMainPhoto(image, attachment));
        }
        if (!attachment.isMainPhotoAllReadySaved()) {
            Long id = attachment.getMainPhotoId();
            attachment.setMainPhotoId(id - images.size());
        }
    }

    private Boolean isMainPhoto(Image image, AttachmentVM attachment) {
        return image.getId().equals(attachment.getMainPhotoId()) && attachment.isMainPhotoAllReadySaved();
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
}
