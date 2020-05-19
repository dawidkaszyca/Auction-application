package pl.dawid.kaszyca;


import org.junit.jupiter.api.Test;
import pl.dawid.kaszyca.dto.AuctionDetailsDTO;
import pl.dawid.kaszyca.model.auction.AuctionDetails;
import pl.dawid.kaszyca.util.MapperUtils;



public class MapperUtilsTest {


    @Test
    public void mapAuctionDetails() {
        AuctionDetails auctionDetails = new AuctionDetails();
        auctionDetails.setAttributeValue("test");
        auctionDetails.setCategoryAttribute("test");
        MapperUtils.map(auctionDetails, AuctionDetailsDTO.class);
    }
}
