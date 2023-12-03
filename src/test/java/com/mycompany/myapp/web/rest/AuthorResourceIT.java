package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Author;
import com.mycompany.myapp.repository.AuthorRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.AuthorDTO;
import com.mycompany.myapp.service.mapper.AuthorMapper;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link AuthorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AuthorResourceIT {

    private static final String DEFAULT_NAMEAUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_NAMEAUTHOR = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BIRTH_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/authors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private AuthorMapper authorMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Author author;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Author createEntity(EntityManager em) {
        Author author = new Author().nameauthor(DEFAULT_NAMEAUTHOR).birthDate(DEFAULT_BIRTH_DATE).description(DEFAULT_DESCRIPTION);
        return author;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Author createUpdatedEntity(EntityManager em) {
        Author author = new Author().nameauthor(UPDATED_NAMEAUTHOR).birthDate(UPDATED_BIRTH_DATE).description(UPDATED_DESCRIPTION);
        return author;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Author.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        author = createEntity(em);
    }

    @Test
    void createAuthor() throws Exception {
        int databaseSizeBeforeCreate = authorRepository.findAll().collectList().block().size();
        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(authorDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate + 1);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getNameauthor()).isEqualTo(DEFAULT_NAMEAUTHOR);
        assertThat(testAuthor.getBirthDate()).isEqualTo(DEFAULT_BIRTH_DATE);
        assertThat(testAuthor.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createAuthorWithExistingId() throws Exception {
        // Create the Author with an existing ID
        author.setId(1L);
        AuthorDTO authorDTO = authorMapper.toDto(author);

        int databaseSizeBeforeCreate = authorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(authorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAuthors() {
        // Initialize the database
        authorRepository.save(author).block();

        // Get all the authorList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(author.getId().intValue()))
            .jsonPath("$.[*].nameauthor")
            .value(hasItem(DEFAULT_NAMEAUTHOR))
            .jsonPath("$.[*].birthDate")
            .value(hasItem(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getAuthor() {
        // Initialize the database
        authorRepository.save(author).block();

        // Get the author
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, author.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(author.getId().intValue()))
            .jsonPath("$.nameauthor")
            .value(is(DEFAULT_NAMEAUTHOR))
            .jsonPath("$.birthDate")
            .value(is(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingAuthor() {
        // Get the author
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingAuthor() throws Exception {
        // Initialize the database
        authorRepository.save(author).block();

        int databaseSizeBeforeUpdate = authorRepository.findAll().collectList().block().size();

        // Update the author
        Author updatedAuthor = authorRepository.findById(author.getId()).block();
        updatedAuthor.nameauthor(UPDATED_NAMEAUTHOR).birthDate(UPDATED_BIRTH_DATE).description(UPDATED_DESCRIPTION);
        AuthorDTO authorDTO = authorMapper.toDto(updatedAuthor);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, authorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(authorDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getNameauthor()).isEqualTo(UPDATED_NAMEAUTHOR);
        assertThat(testAuthor.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testAuthor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().collectList().block().size();
        author.setId(count.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, authorDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(authorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().collectList().block().size();
        author.setId(count.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(authorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().collectList().block().size();
        author.setId(count.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(authorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAuthorWithPatch() throws Exception {
        // Initialize the database
        authorRepository.save(author).block();

        int databaseSizeBeforeUpdate = authorRepository.findAll().collectList().block().size();

        // Update the author using partial update
        Author partialUpdatedAuthor = new Author();
        partialUpdatedAuthor.setId(author.getId());

        partialUpdatedAuthor.birthDate(UPDATED_BIRTH_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuthor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAuthor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getNameauthor()).isEqualTo(DEFAULT_NAMEAUTHOR);
        assertThat(testAuthor.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testAuthor.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateAuthorWithPatch() throws Exception {
        // Initialize the database
        authorRepository.save(author).block();

        int databaseSizeBeforeUpdate = authorRepository.findAll().collectList().block().size();

        // Update the author using partial update
        Author partialUpdatedAuthor = new Author();
        partialUpdatedAuthor.setId(author.getId());

        partialUpdatedAuthor.nameauthor(UPDATED_NAMEAUTHOR).birthDate(UPDATED_BIRTH_DATE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAuthor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAuthor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
        Author testAuthor = authorList.get(authorList.size() - 1);
        assertThat(testAuthor.getNameauthor()).isEqualTo(UPDATED_NAMEAUTHOR);
        assertThat(testAuthor.getBirthDate()).isEqualTo(UPDATED_BIRTH_DATE);
        assertThat(testAuthor.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().collectList().block().size();
        author.setId(count.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, authorDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(authorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().collectList().block().size();
        author.setId(count.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(authorDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAuthor() throws Exception {
        int databaseSizeBeforeUpdate = authorRepository.findAll().collectList().block().size();
        author.setId(count.incrementAndGet());

        // Create the Author
        AuthorDTO authorDTO = authorMapper.toDto(author);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(authorDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Author in the database
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAuthor() {
        // Initialize the database
        authorRepository.save(author).block();

        int databaseSizeBeforeDelete = authorRepository.findAll().collectList().block().size();

        // Delete the author
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, author.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Author> authorList = authorRepository.findAll().collectList().block();
        assertThat(authorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
