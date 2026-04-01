package in.anujjamdade.groceryapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressRequest {

    @NotNull(message = "Address is required")
    @Valid
    private AddressDto address;
}

