package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AttachmentToUpdateVm extends AttachmentToSaveVM {
    List<Long> idsToRemoved;
    boolean isMainPhotoAllReadySaved;
}
