package com.quevedo.mercado.inventario.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias puras (sin levantar el contexto de Spring) de las
 * restricciones @NotBlank, @Min y @DecimalMin definidas en
 * ProductoRequestDTO, que es lo que dispara el HTTP 400 en el controlador.
 */
class ProductoRequestDTOValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void productoValidoNoDebeGenerarViolaciones() {
        ProductoRequestDTO dto = ProductoRequestDTO.builder()
                .nombre("Tomate rinon")
                .categoria("Verduras")
                .stock(50)
                .precio(new BigDecimal("1.25"))
                .build();

        Set<ConstraintViolation<ProductoRequestDTO>> violaciones = validator.validate(dto);

        assertThat(violaciones).isEmpty();
    }

    @Test
    void nombreEnBlancoDebeGenerarViolacion() {
        ProductoRequestDTO dto = ProductoRequestDTO.builder()
                .nombre("   ")
                .categoria("Verduras")
                .stock(10)
                .precio(new BigDecimal("2.00"))
                .build();

        Set<ConstraintViolation<ProductoRequestDTO>> violaciones = validator.validate(dto);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("nombre"));
    }

    @Test
    void stockNegativoDebeGenerarViolacion() {
        ProductoRequestDTO dto = ProductoRequestDTO.builder()
                .nombre("Platano")
                .categoria("Frutas")
                .stock(-5)
                .precio(new BigDecimal("0.50"))
                .build();

        Set<ConstraintViolation<ProductoRequestDTO>> violaciones = validator.validate(dto);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("stock"));
    }

    @Test
    void precioCeroONegativoDebeGenerarViolacion() {
        ProductoRequestDTO dto = ProductoRequestDTO.builder()
                .nombre("Cebolla")
                .categoria("Verduras")
                .stock(20)
                .precio(BigDecimal.ZERO)
                .build();

        Set<ConstraintViolation<ProductoRequestDTO>> violaciones = validator.validate(dto);

        assertThat(violaciones).anyMatch(v -> v.getPropertyPath().toString().equals("precio"));
    }

    @Test
    void precioExactamenteEnElLimiteMinimoEsValido() {
        // El enunciado exige precio >= 0.01 (limite inclusive).
        ProductoRequestDTO dto = ProductoRequestDTO.builder()
                .nombre("Sal")
                .categoria("Abarrotes")
                .stock(5)
                .precio(new BigDecimal("0.01"))
                .build();

        Set<ConstraintViolation<ProductoRequestDTO>> violaciones = validator.validate(dto);

        assertThat(violaciones).isEmpty();
    }

}
