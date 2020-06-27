package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ImageVM {
    Long photoId;
    boolean isMainPhoto;
    String url;
}
