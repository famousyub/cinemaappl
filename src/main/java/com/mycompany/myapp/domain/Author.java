package com.mycompany.myapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Author.
 */
@Table("author")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nameauthor")
    private String nameauthor;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("description")
    private String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Author id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameauthor() {
        return this.nameauthor;
    }

    public Author nameauthor(String nameauthor) {
        this.setNameauthor(nameauthor);
        return this;
    }

    public void setNameauthor(String nameauthor) {
        this.nameauthor = nameauthor;
    }

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public Author birthDate(LocalDate birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getDescription() {
        return this.description;
    }

    public Author description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Author)) {
            return false;
        }
        return id != null && id.equals(((Author) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Author{" +
            "id=" + getId() +
            ", nameauthor='" + getNameauthor() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
