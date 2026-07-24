package com.quevedo.mercado.inventario.controller;

import com.quevedo.mercado.inventario.dto.ApiResponse;
import com.quevedo.mercado.inventario.dto.PageMetaDTO;
import com.quevedo.mercado.inventario.dto.PageResultDTO;
import com.quevedo.mercado.inventario.dto.ProductoRequestDTO;
import com.quevedo.mercado.inventario.dto.ProductoResponseDTO;
import com.quevedo.mercado.inventario.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST del inventario de productos del Mercado Municipal de Quevedo.
 *
 * Reglas de acceso (ver SecurityConfig):
 *  - GET    /api/v1/productos      -> requiere ROLE_USER
 *  - POST   /api/v1/productos      -> requiere ROLE_ADMIN
 *  - DELETE /api/v1/productos/{id} -> requiere ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Lista los productos activos de forma paginada.
     * Aplica el patron cache-aside con Redis (ver ProductoServiceImpl).
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> listar(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {

        PageResultDTO<ProductoResponseDTO> resultado = productoService.listar(pageable);

        PageMetaDTO meta = PageMetaDTO.builder()
                .page(resultado.getPage())
                .size(resultado.getSize())
                .totalElements(resultado.getTotalElements())
                .totalPages(resultado.getTotalPages())
                .first(resultado.isFirst())
                .last(resultado.isLast())
                .build();

        return ResponseEntity.ok(
                ApiResponse.ok(resultado.getContent(), "Productos obtenidos exitosamente", meta));
    }

    /**
     * Crea un nuevo producto. El cuerpo se valida con @Valid segun las
     * restricciones definidas en ProductoRequestDTO.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> crear(@Valid @RequestBody ProductoRequestDTO request) {
        ProductoResponseDTO creado = productoService.crear(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.ok(creado, "Producto creado exitosamente"));
    }

    /**
     * Elimina (baja logica / soft delete) un producto por id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.<Void>ok(null, "Producto eliminado exitosamente (soft delete)"));
    }

}
