package pl.dawid.kaszyca.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.dawid.kaszyca.service.AttachmentService;
import pl.dawid.kaszyca.util.MapperUtils;
import pl.dawid.kaszyca.vm.AttachmentVM;
import pl.dawid.kaszyca.vm.ImageVM;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("api")
public class AttachmentController {

    AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping(value = "/attachments", consumes = "multipart/form-data")
    public ResponseEntity saveAttachments(@RequestParam("files") List<MultipartFile> files,
                                          @RequestParam("data") String data) {
        try {
            AttachmentVM attachmentToSaveVM = MapperUtils.mapJsonToObject(data, AttachmentVM.class);
            attachmentService.saveAuctionAttachments(files, attachmentToSaveVM);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Something went wrong during saving attachments");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PutMapping(value = "/attachments", consumes = "multipart/form-data")
    public ResponseEntity updateAttachments(@RequestParam("files") List<MultipartFile> files,
                                            @RequestParam("data") String data) {
        try {
            AttachmentVM attachment = MapperUtils.mapJsonToObject(data, AttachmentVM.class);
            attachmentService.updateAuctionAttachments(files, attachment);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Something went wrong during updating attachments");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping(value = "/attachments/{auctionIdList}")
    public ResponseEntity getPhotosByListOfAuctions(@PathVariable List<Long> auctionIdList) {
        try {
            Map<String, String> photosUrlMap = attachmentService.getPhotosByListId(auctionIdList);
            return photosUrlMap.isEmpty() ? new ResponseEntity(photosUrlMap, HttpStatus.NO_CONTENT)
                    : new ResponseEntity(photosUrlMap, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Something went wrong during saving attachments");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping(value = "/attachments")
    public ResponseEntity getPhotosForAuctionById(@RequestParam("id") long auctionId) {
        try {
            List<ImageVM> photosUrlMap = attachmentService.getPhotosForAuctionById(auctionId);
            return photosUrlMap.isEmpty() ? new ResponseEntity(photosUrlMap, HttpStatus.NO_CONTENT)
                    : new ResponseEntity(photosUrlMap, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Something went wrong during saving attachments");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping(value = "/attachments/users")
    public ResponseEntity getUserPhoto() {
        try {
            List<String> photos = attachmentService.getUserPhoto();
            return photos.isEmpty() ? new ResponseEntity(photos, HttpStatus.NO_CONTENT)
                    : new ResponseEntity(photos, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Cannot get user photo");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @GetMapping(value = "/attachments/users/{id}")
    public ResponseEntity getUserPhotoById(@PathVariable long id) {
        try {
            List<String> photos = attachmentService.getUserPhoto(id);
            return photos.isEmpty() ? new ResponseEntity(photos, HttpStatus.NO_CONTENT)
                    : new ResponseEntity(photos, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Cannot get user photo by id");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }

    @PostMapping(value = "/attachments/users", consumes = "multipart/form-data")
    public ResponseEntity saveUserPhoto(@RequestParam("files") MultipartFile file) {
        try {
            attachmentService.saveUserPhoto(file);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error("Something went wrong during saving user photo image");
            return new ResponseEntity(e.getMessage(), HttpStatus.valueOf(500));
        }
    }
}
