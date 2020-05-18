package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentSaveVM {
    Long auctionId;
    Long mainPhotoId;
    boolean isUserImagePhoto;
}
