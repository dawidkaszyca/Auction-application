package pl.dawid.kaszyca.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.dawid.kaszyca.model.Attachment;
import pl.dawid.kaszyca.model.Image;
import pl.dawid.kaszyca.model.auction.Auction;
import pl.dawid.kaszyca.repository.AttachmentRepository;
import pl.dawid.kaszyca.repository.AuctionRepository;
import pl.dawid.kaszyca.repository.ImageRepository;
import pl.dawid.kaszyca.vm.AttachmentSaveVM;

import java.io.IOException;
import java.util.*;

@Service
public class AttachmentService {

    AttachmentRepository attachmentRepository;
    AuctionRepository auctionRepository;
    ImageRepository imageRepository;

    public AttachmentService(AttachmentRepository attachmentRepository, AuctionRepository auctionRepository,
                             ImageRepository imageRepository) {
        this.attachmentRepository = attachmentRepository;
        this.auctionRepository = auctionRepository;
        this.imageRepository = imageRepository;
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
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            Attachment attachment = new Attachment(fileName, file.getContentType(), file.getBytes());
            attachmentList.add(attachment);
        }
        return attachmentList;
    }

    private List<Image> prepareImageListToSave(Auction auction, List<Attachment> attachments, Long mainPhotoId) {
        List<Image> imageList = new ArrayList<>();
        for (int i = 0; i < attachments.size(); i++) {
            Attachment attachment = attachments.get(i);
            Image image = new Image();
            image.setAttachment(attachment);
            image.setAuction(auction);
            if(i == mainPhotoId)
                image.setIsMainAuctionPhoto(true);
            imageList.add(image);
        }
        return imageList;
    }

    public Map<String, String> getPhotosUrl(List<Long> idOfAuctionMainPhotoToGet) {
        Map<String, String> photosUrl = new HashMap<>();
        for(Long id: idOfAuctionMainPhotoToGet) {
            Optional<Image> image = imageRepository.findFirstByAuctionIdAndIsMainAuctionPhoto(id, true);
            if(image.isPresent()) {
                String url = convertImageToResponseIfExist(image.get().getAttachment());
                if(url != null)
                    photosUrl.put(id.toString(), url);
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
}
