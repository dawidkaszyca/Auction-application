package pl.dawid.kaszyca.vm;

import lombok.Getter;
import lombok.Setter;
import pl.dawid.kaszyca.dto.CategoryAttributesDTO;

import java.util.List;

@Getter
@Setter
public class NewAuctionVM {
    private String category;
    private String city;
    private String condition;
    private List<CategoryAttributesDTO> attributes;
    private String price;
    private String title;
    private String description;
    private String phone;
}
