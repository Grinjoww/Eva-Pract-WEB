package com.quevedo.mercado.inventario.service.impl;

import com.quevedo.mercado.inventario.dto.PageResultDTO;
import com.quevedo.mercado.inventario.dto.ProductoRequestDTO;
import com.quevedo.mercado.inventario.dto.ProductoResponseDTO;
import com.quevedo.mercado.inventario.entity.Producto;
import com.quevedo.mercado.inventario.exception.ResourceNotFoundException;
import com.quevedo.mercado.inventario.mapper.ProductoMapper;
import com.quevedo.mercado.inventario.repository.ProductoRepository;
import com.quevedo.mercado.inventario.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementacion del servicio de productos.
 *
 * Patron cache-aside con Redis:
 *  - listar(): primero consulta el cache "productos"; si no existe la entrada
 *    (cache miss), consulta la base de datos, arma la respuesta y la guarda
 *    en el cache (esto lo hace automaticamente @Cacheable).
 *  - crear() / eliminar(): invalidan (evict) todas las entradas del cache
 *    "productos", ya que cualquier alta o baja cambia el resultado de
 *    cualquier pagina/orden previamente cacheada.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private static final String CACHE_PRODUCTOS = "productos";

    private final ProductoRepository productoRepository;

    @Override
    @Cacheable(
            value = CACHE_PRODUCTOS,
            key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()"
    )
    public PageResultDTO<ProductoResponseDTO> listar(Pageable pageable) {
        log.info("Cache MISS -> consultando productos activos en la base de datos (page={}, size={}, sort={})",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Producto> paginaProductos = productoRepository.findByActivoTrue(pageable);

        List<ProductoResponseDTO> contenido = paginaProductos.getContent().stream()
                .map(ProductoMapper::toResponseDTO)
                .toList();

        return PageResultDTO.<ProductoResponseDTO>builder()
                .content(contenido)
                .page(paginaProductos.getNumber())
                .size(paginaProductos.getSize())
                .totalElements(paginaProductos.getTotalElements())
                .totalPages(paginaProductos.getTotalPages())
                .first(paginaProductos.isFirst())
                .last(paginaProductos.isLast())
                .build();
    }

    @Override
    @CacheEvict(value = CACHE_PRODUCTOS, allEntries = true)
    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO request) {
        Producto producto = ProductoMapper.toEntity(request);
        Producto guardado = productoRepository.save(producto);
        log.info("Producto creado id={} nombre='{}' -> cache 'productos' invalidado", guardado.getId(), guardado.getNombre());
        return ProductoMapper.toResponseDTO(guardado);
    }

    @Override
    @CacheEvict(value = CACHE_PRODUCTOS, allEntries = true)
    @Transactional
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .filter(Producto::getActivo)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontro un producto activo con id " + id));

        producto.setActivo(Boolean.FALSE);
        productoRepository.save(producto);
        log.info("Producto id={} marcado como inactivo (soft delete) -> cache 'productos' invalidado", id);
    }

}
