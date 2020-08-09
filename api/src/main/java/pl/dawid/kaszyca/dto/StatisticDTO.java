package pl.dawid.kaszyca.dto;

import lombok.Data;

import java.util.Date;

@Data
public class StatisticDTO {

    private Date date;

    private Long value;

}
