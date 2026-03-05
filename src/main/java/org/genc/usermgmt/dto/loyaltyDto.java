package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class loyaltyDto {
    private String Tier;
    private Integer LoyaltyPoints;

}
