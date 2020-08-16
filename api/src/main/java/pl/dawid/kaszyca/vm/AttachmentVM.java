package pl.dawid.kaszyca.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AttachmentVM {

    Long auctionId;

    Long mainPhotoId;

    boolean isUserImagePhoto;

    List<Long> idsToRemoved;

    boolean isMainPhotoAllReadySaved;
}
