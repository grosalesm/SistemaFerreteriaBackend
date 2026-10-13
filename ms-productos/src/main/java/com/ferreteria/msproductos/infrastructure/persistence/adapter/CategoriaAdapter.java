package com.ferreteria.msproductos.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.ferreteria.msproductos.application.port.outservice.CategoriaOutService;
import com.ferreteria.msproductos.domain.bean.Categoria;
import com.ferreteria.msproductos.infrastructure.mapper.CategoriaMapper;
import com.ferreteria.msproductos.infrastructure.persistence.entity.CategoriaEntity;
import com.ferreteria.msproductos.infrastructure.persistence.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoriaAdapter implements CategoriaOutService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<Categoria> listar() {
        return categoriaMapper.toBeans(categoriaRepository.findAll(Sort.by("nombre")));
    }

    @Override
    public Optional<Categoria> obtenerPorId(Long idCategoria) {
        return categoriaRepository.findById(idCategoria).map(categoriaMapper::toBean);
    }

    @Override
    public Optional<Categoria> obtenerPorNombre(String nombre) {
        return categoriaRepository.findByNombreIgnoreCase(nombre).map(categoriaMapper::toBean);
    }

    @Override
    public Categoria insertar(Categoria categoria) {
        CategoriaEntity entity = categoriaMapper.toEntity(categoria);
        entity.setIdCategoria(null);
        entity.setActivo(true);
        return categoriaMapper.toBean(categoriaRepository.save(entity));
    }
}
