package in.anujjamdade.groceryapi.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import in.anujjamdade.groceryapi.dto.AddressDetailsDto;
import in.anujjamdade.groceryapi.dto.AddressDto;
import in.anujjamdade.groceryapi.dto.AddressListResponse;
import in.anujjamdade.groceryapi.dto.AddressResponse;
import in.anujjamdade.groceryapi.model.Address;
import in.anujjamdade.groceryapi.model.User;
import in.anujjamdade.groceryapi.repository.AddressRepository;
import in.anujjamdade.groceryapi.repository.UserRepository;
import in.anujjamdade.groceryapi.service.AddressService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public AddressResponse addAddress(String userId, AddressDto addressDto) {
        // Verify user exists
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Create Address object from DTO
        Address address = new Address();
        address.setUserId(userId);
        address.setFirstName(addressDto.getFirstName());
        address.setLastName(addressDto.getLastName());
        address.setEmail(addressDto.getEmail());
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setZipcode(addressDto.getZipcode());
        address.setCountry(addressDto.getCountry());
        address.setPhone(addressDto.getPhone());

        // Save address to addresses collection
        addressRepository.save(address);

        return new AddressResponse(true, "Address added successfully");
    }

    @Override
    public AddressListResponse getUserAddresses(String userId) {
        // Get all addresses for the user
        List<Address> addresses = addressRepository.findAllByUserId(userId);

        // Convert to DTOs
        List<AddressDetailsDto> addressDtos = addresses.stream()
            .map(address -> {
                AddressDetailsDto dto = new AddressDetailsDto();
                dto.setId(address.getId());
                dto.setUserId(address.getUserId());
                dto.setFirstName(address.getFirstName());
                dto.setLastName(address.getLastName());
                dto.setEmail(address.getEmail());
                dto.setStreet(address.getStreet());
                dto.setCity(address.getCity());
                dto.setState(address.getState());
                dto.setZipcode(address.getZipcode());
                dto.setCountry(address.getCountry());
                dto.setPhone(address.getPhone());
                return dto;
            })
            .collect(Collectors.toList());

        return new AddressListResponse(true, addressDtos);
    }
}

