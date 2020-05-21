package pl.dawid.kaszyca.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.dawid.kaszyca.model.Attachment;
import pl.dawid.kaszyca.model.Image;
import pl.dawid.kaszyca.model.User;
import pl.dawid.kaszyca.repository.ImageRepository;
import pl.dawid.kaszyca.repository.UserRepository;

import java.io.IOException;
@Service
public class ImageFileStoreService {
    @Autowired
private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    public Image storeFile(MultipartFile file, User user) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            //deleteIfUserHasAlreadyImage(user);
            Image dbFile = new Image();
            Attachment attachment = new Attachment(fileName, file.getContentType(), file.getBytes());
            dbFile.setAttachment(attachment);
            return imageRepository.save(dbFile);
        } catch (IOException ex) {
            System.out.println(ex.getCause());
        }
        return null;
    }

/*
    private void deleteIfUserHasAlreadyImage(User user) {
        Image image = imageRepository.findFirstByUser(user);
        if(image != null){
            imageRepository.delete(image);
        }
    }

    public Image getFile(User user) {
        return imageRepository.findFirstByUser(user);
    }
*/

/*    public User getUserFromPrincipal(){
 *//*       SecurityUtils.getCurrentUserLogin().ifPresent();
        UserPrinciple principal = (UserPrinciple) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findFirstByUserName(principal.getUsername());*//*
    }*/
}
