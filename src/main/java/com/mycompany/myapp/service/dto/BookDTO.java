package com.mycompany.myapp.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.mycompany.myapp.domain.Book} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class BookDTO implements Serializable {

    private Long id;

    private String title;

    private String descripton;

    private LocalDate publicationDate;

    @Lob
    private byte[] bookimage;

    private String bookimageContentType;
    private AuthorDTO name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescripton() {
        return descripton;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public byte[] getBookimage() {
        return bookimage;
    }

    public void setBookimage(byte[] bookimage) {
        this.bookimage = bookimage;
    }

    public String getBookimageContentType() {
        return bookimageContentType;
    }

    public void setBookimageContentType(String bookimageContentType) {
        this.bookimageContentType = bookimageContentType;
    }

    public AuthorDTO getName() {
        return name;
    }

    public void setName(AuthorDTO name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookDTO)) {
            return false;
        }

        BookDTO bookDTO = (BookDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, bookDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BookDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", descripton='" + getDescripton() + "'" +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", bookimage='" + getBookimage() + "'" +
            ", name=" + getName() +
            "}";
    }
}
