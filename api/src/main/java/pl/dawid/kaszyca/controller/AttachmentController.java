package pl.dawid.kaszyca.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.dawid.kaszyca.model.Attachment;
import pl.dawid.kaszyca.model.auction.AuctionDetails;
import pl.dawid.kaszyca.repository.AttachmentRepository;
import pl.dawid.kaszyca.repository.AuctionRepository;
import pl.dawid.kaszyca.service.AttachmentService;
import pl.dawid.kaszyca.vm.AttachmentSaveVM;
import pl.dawid.kaszyca.vm.LoginFormVM;
import pl.dawid.kaszyca.vm.NewAuctionVM;

import javax.validation.Valid;
import java.io.File;
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

    @RequestMapping(value = "/attachments", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity saveAttachments(@RequestParam("files") List<MultipartFile> files,
                                          @RequestParam("data") String data) {
        try {
            Gson gson = new Gson();
            AttachmentSaveVM attachmentSaveVM = gson.fromJson(data, AttachmentSaveVM.class);
            attachmentService.saveAuctionAttachments(files, attachmentSaveVM);
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Something went wrong during saving attachments");
        }
        return new ResponseEntity(HttpStatus.valueOf(422));
    }

    @GetMapping(value = "/attachments/{auctionIdList}")
    public ResponseEntity getPhotosByAuctionId(@PathVariable List<Long> auctionIdList) {
        try {
            Map<String, String> photosUrlMap = attachmentService.getPhotosUrl(auctionIdList);
            return new ResponseEntity(photosUrlMap, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Something went wrong during saving attachments");
        }
        return new ResponseEntity(HttpStatus.valueOf(422));
    }
}
