package org.genc.usermgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoyaltyDTO {
    private String tier;
    private Integer loyaltyPoints;

}
