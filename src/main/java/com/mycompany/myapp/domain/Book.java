package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Book.
 */
@Table("book")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("title")
    private String title;

    @Column("descripton")
    private String descripton;

    @Column("publication_date")
    private LocalDate publicationDate;

    @Column("bookimage")
    private byte[] bookimage;

    @Column("bookimage_content_type")
    private String bookimageContentType;

    @Transient
    private Author name;

    @Column("name_id")
    private Long nameId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Book id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Book title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescripton() {
        return this.descripton;
    }

    public Book descripton(String descripton) {
        this.setDescripton(descripton);
        return this;
    }

    public void setDescripton(String descripton) {
        this.descripton = descripton;
    }

    public LocalDate getPublicationDate() {
        return this.publicationDate;
    }

    public Book publicationDate(LocalDate publicationDate) {
        this.setPublicationDate(publicationDate);
        return this;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public byte[] getBookimage() {
        return this.bookimage;
    }

    public Book bookimage(byte[] bookimage) {
        this.setBookimage(bookimage);
        return this;
    }

    public void setBookimage(byte[] bookimage) {
        this.bookimage = bookimage;
    }

    public String getBookimageContentType() {
        return this.bookimageContentType;
    }

    public Book bookimageContentType(String bookimageContentType) {
        this.bookimageContentType = bookimageContentType;
        return this;
    }

    public void setBookimageContentType(String bookimageContentType) {
        this.bookimageContentType = bookimageContentType;
    }

    public Author getName() {
        return this.name;
    }

    public void setName(Author author) {
        this.name = author;
        this.nameId = author != null ? author.getId() : null;
    }

    public Book name(Author author) {
        this.setName(author);
        return this;
    }

    public Long getNameId() {
        return this.nameId;
    }

    public void setNameId(Long author) {
        this.nameId = author;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        return id != null && id.equals(((Book) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Book{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", descripton='" + getDescripton() + "'" +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", bookimage='" + getBookimage() + "'" +
            ", bookimageContentType='" + getBookimageContentType() + "'" +
            "}";
    }
}
