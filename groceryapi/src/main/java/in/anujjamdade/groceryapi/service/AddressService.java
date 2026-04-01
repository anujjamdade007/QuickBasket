package in.anujjamdade.groceryapi.service;

import in.anujjamdade.groceryapi.dto.AddressDto;
import in.anujjamdade.groceryapi.dto.AddressListResponse;
import in.anujjamdade.groceryapi.dto.AddressResponse;

public interface AddressService {
    AddressResponse addAddress(String userId, AddressDto addressDto);
    AddressListResponse getUserAddresses(String userId);
}

