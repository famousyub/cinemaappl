package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.repository.AuthorRepository;
import com.mycompany.myapp.service.AuthorService;
import com.mycompany.myapp.service.dto.AuthorDTO;
import com.mycompany.myapp.service.mapper.AuthorMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Author}.
 */
@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorMapper authorMapper) {
        this.authorRepository = authorRepository;
        this.authorMapper = authorMapper;
    }

    @Override
    public Mono<AuthorDTO> save(AuthorDTO authorDTO) {
        log.debug("Request to save Author : {}", authorDTO);
        return authorRepository.save(authorMapper.toEntity(authorDTO)).map(authorMapper::toDto);
    }

    @Override
    public Mono<AuthorDTO> update(AuthorDTO authorDTO) {
        log.debug("Request to update Author : {}", authorDTO);
        return authorRepository.save(authorMapper.toEntity(authorDTO)).map(authorMapper::toDto);
    }

    @Override
    public Mono<AuthorDTO> partialUpdate(AuthorDTO authorDTO) {
        log.debug("Request to partially update Author : {}", authorDTO);

        return authorRepository
            .findById(authorDTO.getId())
            .map(existingAuthor -> {
                authorMapper.partialUpdate(existingAuthor, authorDTO);

                return existingAuthor;
            })
            .flatMap(authorRepository::save)
            .map(authorMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<AuthorDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Authors");
        return authorRepository.findAllBy(pageable).map(authorMapper::toDto);
    }

    public Mono<Long> countAll() {
        return authorRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<AuthorDTO> findOne(Long id) {
        log.debug("Request to get Author : {}", id);
        return authorRepository.findById(id).map(authorMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Author : {}", id);
        return authorRepository.deleteById(id);
    }
}
