package com.quevedo.mercado.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Resultado paginado "plano" (POJO simple) usado como valor cacheado en Redis.
 *
 * Se evita cachear directamente un Page<T> de Spring Data porque su
 * implementacion (PageImpl) no serializa/deserializa de forma confiable
 * con Jackson + Redis (no tiene constructor por defecto). Este DTO propio
 * si es 100% serializable a JSON sin ambiguedades.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResultDTO<T> implements Serializable {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

}
