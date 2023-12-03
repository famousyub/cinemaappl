package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Book;
import com.mycompany.myapp.repository.BookRepository;
import com.mycompany.myapp.repository.EntityManager;
import com.mycompany.myapp.service.dto.BookDTO;
import com.mycompany.myapp.service.mapper.BookMapper;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link BookResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class BookResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTON = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTON = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_PUBLICATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_PUBLICATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final byte[] DEFAULT_BOOKIMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BOOKIMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_BOOKIMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BOOKIMAGE_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/books";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Book book;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createEntity(EntityManager em) {
        Book book = new Book()
            .title(DEFAULT_TITLE)
            .descripton(DEFAULT_DESCRIPTON)
            .publicationDate(DEFAULT_PUBLICATION_DATE)
            .bookimage(DEFAULT_BOOKIMAGE)
            .bookimageContentType(DEFAULT_BOOKIMAGE_CONTENT_TYPE);
        return book;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Book createUpdatedEntity(EntityManager em) {
        Book book = new Book()
            .title(UPDATED_TITLE)
            .descripton(UPDATED_DESCRIPTON)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .bookimage(UPDATED_BOOKIMAGE)
            .bookimageContentType(UPDATED_BOOKIMAGE_CONTENT_TYPE);
        return book;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Book.class).block();
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
        book = createEntity(em);
    }

    @Test
    void createBook() throws Exception {
        int databaseSizeBeforeCreate = bookRepository.findAll().collectList().block().size();
        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookDTO))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate + 1);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getDescripton()).isEqualTo(DEFAULT_DESCRIPTON);
        assertThat(testBook.getPublicationDate()).isEqualTo(DEFAULT_PUBLICATION_DATE);
        assertThat(testBook.getBookimage()).isEqualTo(DEFAULT_BOOKIMAGE);
        assertThat(testBook.getBookimageContentType()).isEqualTo(DEFAULT_BOOKIMAGE_CONTENT_TYPE);
    }

    @Test
    void createBookWithExistingId() throws Exception {
        // Create the Book with an existing ID
        book.setId(1L);
        BookDTO bookDTO = bookMapper.toDto(book);

        int databaseSizeBeforeCreate = bookRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllBooks() {
        // Initialize the database
        bookRepository.save(book).block();

        // Get all the bookList
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
            .value(hasItem(book.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].descripton")
            .value(hasItem(DEFAULT_DESCRIPTON))
            .jsonPath("$.[*].publicationDate")
            .value(hasItem(DEFAULT_PUBLICATION_DATE.toString()))
            .jsonPath("$.[*].bookimageContentType")
            .value(hasItem(DEFAULT_BOOKIMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].bookimage")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_BOOKIMAGE)));
    }

    @Test
    void getBook() {
        // Initialize the database
        bookRepository.save(book).block();

        // Get the book
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, book.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(book.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.descripton")
            .value(is(DEFAULT_DESCRIPTON))
            .jsonPath("$.publicationDate")
            .value(is(DEFAULT_PUBLICATION_DATE.toString()))
            .jsonPath("$.bookimageContentType")
            .value(is(DEFAULT_BOOKIMAGE_CONTENT_TYPE))
            .jsonPath("$.bookimage")
            .value(is(Base64Utils.encodeToString(DEFAULT_BOOKIMAGE)));
    }

    @Test
    void getNonExistingBook() {
        // Get the book
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingBook() throws Exception {
        // Initialize the database
        bookRepository.save(book).block();

        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();

        // Update the book
        Book updatedBook = bookRepository.findById(book.getId()).block();
        updatedBook
            .title(UPDATED_TITLE)
            .descripton(UPDATED_DESCRIPTON)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .bookimage(UPDATED_BOOKIMAGE)
            .bookimageContentType(UPDATED_BOOKIMAGE_CONTENT_TYPE);
        BookDTO bookDTO = bookMapper.toDto(updatedBook);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bookDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getDescripton()).isEqualTo(UPDATED_DESCRIPTON);
        assertThat(testBook.getPublicationDate()).isEqualTo(UPDATED_PUBLICATION_DATE);
        assertThat(testBook.getBookimage()).isEqualTo(UPDATED_BOOKIMAGE);
        assertThat(testBook.getBookimageContentType()).isEqualTo(UPDATED_BOOKIMAGE_CONTENT_TYPE);
    }

    @Test
    void putNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, bookDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.save(book).block();

        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBook.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBook.getDescripton()).isEqualTo(DEFAULT_DESCRIPTON);
        assertThat(testBook.getPublicationDate()).isEqualTo(DEFAULT_PUBLICATION_DATE);
        assertThat(testBook.getBookimage()).isEqualTo(DEFAULT_BOOKIMAGE);
        assertThat(testBook.getBookimageContentType()).isEqualTo(DEFAULT_BOOKIMAGE_CONTENT_TYPE);
    }

    @Test
    void fullUpdateBookWithPatch() throws Exception {
        // Initialize the database
        bookRepository.save(book).block();

        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();

        // Update the book using partial update
        Book partialUpdatedBook = new Book();
        partialUpdatedBook.setId(book.getId());

        partialUpdatedBook
            .title(UPDATED_TITLE)
            .descripton(UPDATED_DESCRIPTON)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .bookimage(UPDATED_BOOKIMAGE)
            .bookimageContentType(UPDATED_BOOKIMAGE_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBook.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBook))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
        Book testBook = bookList.get(bookList.size() - 1);
        assertThat(testBook.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBook.getDescripton()).isEqualTo(UPDATED_DESCRIPTON);
        assertThat(testBook.getPublicationDate()).isEqualTo(UPDATED_PUBLICATION_DATE);
        assertThat(testBook.getBookimage()).isEqualTo(UPDATED_BOOKIMAGE);
        assertThat(testBook.getBookimageContentType()).isEqualTo(UPDATED_BOOKIMAGE_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, bookDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBook() throws Exception {
        int databaseSizeBeforeUpdate = bookRepository.findAll().collectList().block().size();
        book.setId(count.incrementAndGet());

        // Create the Book
        BookDTO bookDTO = bookMapper.toDto(book);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(bookDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Book in the database
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBook() {
        // Initialize the database
        bookRepository.save(book).block();

        int databaseSizeBeforeDelete = bookRepository.findAll().collectList().block().size();

        // Delete the book
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, book.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Book> bookList = bookRepository.findAll().collectList().block();
        assertThat(bookList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
