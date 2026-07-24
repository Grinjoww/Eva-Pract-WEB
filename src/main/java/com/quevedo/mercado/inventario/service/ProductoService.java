package com.quevedo.mercado.inventario.service;

import com.quevedo.mercado.inventario.dto.PageResultDTO;
import com.quevedo.mercado.inventario.dto.ProductoRequestDTO;
import com.quevedo.mercado.inventario.dto.ProductoResponseDTO;
import org.springframework.data.domain.Pageable;

public interface ProductoService {

    PageResultDTO<ProductoResponseDTO> listar(Pageable pageable);

    ProductoResponseDTO crear(ProductoRequestDTO request);

    void eliminar(Long id);

}
