package com.bss.bssserverapi.domain.order.dto;

import com.bss.bssserverapi.domain.order.SideType;
import jakarta.validation.constraints.*;
        import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class CreateSpotOrderByLimitReqDto {

    @NotBlank
    @Pattern(regexp = "^[A-Z0-9]{6,}$")
    private String symbol;

    @DecimalMin(value = "0.01", inclusive = true)
    @Digits(integer = 14, fraction = 2)
    private BigDecimal price;

    @DecimalMin(value = "0.00001", inclusive = true)
    @Digits(integer = 14, fraction = 5)
    private BigDecimal qty;

    @NotNull
    private SideType sideType;
}
