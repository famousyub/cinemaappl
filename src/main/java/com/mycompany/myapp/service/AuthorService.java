package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.AuthorDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Author}.
 */
public interface AuthorService {
    /**
     * Save a author.
     *
     * @param authorDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<AuthorDTO> save(AuthorDTO authorDTO);

    /**
     * Updates a author.
     *
     * @param authorDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<AuthorDTO> update(AuthorDTO authorDTO);

    /**
     * Partially updates a author.
     *
     * @param authorDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<AuthorDTO> partialUpdate(AuthorDTO authorDTO);

    /**
     * Get all the authors.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<AuthorDTO> findAll(Pageable pageable);

    /**
     * Returns the number of authors available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" author.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<AuthorDTO> findOne(Long id);

    /**
     * Delete the "id" author.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
