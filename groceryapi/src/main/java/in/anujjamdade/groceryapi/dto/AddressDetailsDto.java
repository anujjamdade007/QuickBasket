package in.anujjamdade.groceryapi.dto;

import lombok.Data;

@Data
public class AddressDetailsDto {
    private String id;
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String street;
    private String city;
    private String state;
    private Integer zipcode;
    private String country;
    private String phone;
}

