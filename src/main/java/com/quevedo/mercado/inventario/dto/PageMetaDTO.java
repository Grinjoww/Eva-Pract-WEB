package com.quevedo.mercado.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Metadatos de paginacion que se devuelven en el campo "meta" de ApiResponse
 * para los listados paginados (page, size, sort).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageMetaDTO {

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

}
