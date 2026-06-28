package com.sistemabarberia.fadex_backend.modules.ruleta.categoria.service.impll;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.request.RuletaCategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.dto.response.RuletaCategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.entity.RuletaCategoria;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.mapper.RuletaCategoriaMapper;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.repository.RuletaCategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.ruleta.categoria.service.IRuletaCategoriaService;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.entity.Ruleta;
import com.sistemabarberia.fadex_backend.modules.ruleta.ruleta.repository.RuletaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RuletaCategoriaServiceImpl implements IRuletaCategoriaService {

    private final RuletaCategoriaRepository ruletaCategoriaRepository;
    private final RuletaRepository ruletaRepository;
    private final CategoriaRepository categoriaRepository;
    private final RuletaCategoriaMapper ruletaCategoriaMapper;

    @Override
    public RuletaCategoriaResponseDTO crearCategoria(RuletaCategoriaRequestDTO request) {
        Ruleta ruleta = ruletaRepository.findById(request.getIdRuleta()).orElseThrow(() -> new EntityNotFoundException("Ruleta no encontrada"));
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria()).orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
        RuletaCategoria entity = new RuletaCategoria();
        entity.setRuleta(ruleta);
        entity.setCategoria(categoria);
        return ruletaCategoriaMapper.toResponse(ruletaCategoriaRepository.save(entity));
    }

    @Override
    public RuletaCategoriaResponseDTO actualizarCategoria(Integer id, RuletaCategoriaRequestDTO request) {
        RuletaCategoria entity = ruletaCategoriaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Relación no encontrada"));
        Ruleta ruleta = ruletaRepository.findById(request.getIdRuleta()).orElseThrow(() -> new EntityNotFoundException("Ruleta no encontrada"));
        Categoria categoria = categoriaRepository.findById(request.getIdCategoria()).orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
        entity.setRuleta(ruleta);
        entity.setCategoria(categoria);
        return ruletaCategoriaMapper.toResponse(ruletaCategoriaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public RuletaCategoriaResponseDTO obtenerCategoriaPorId(Integer id) {
        return ruletaCategoriaMapper.toResponse(ruletaCategoriaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Relación no encontrada")));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RuletaCategoriaResponseDTO> listarCategoria(Pageable pageable) {
        return ruletaCategoriaRepository.findAll(pageable).map(ruletaCategoriaMapper::toResponse);
    }

    @Override
    public void eliminarCategoria(Integer id) {
        if (!ruletaCategoriaRepository.existsById(id)) {
            throw new EntityNotFoundException("Relación no encontrada");
        }
        ruletaCategoriaRepository.deleteById(id);
    }
}