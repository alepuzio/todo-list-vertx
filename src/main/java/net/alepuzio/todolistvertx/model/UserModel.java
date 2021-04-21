package net.alepuzio.todolistvertx.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import net.alepuzio.todolistvertx.serializer.LocalDateDeserializer;
import net.alepuzio.todolistvertx.serializer.LocalDateSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class UserModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

    private String username;

    private String password;

    private String name;

    private String surname;

    private String email;

    private LocalDateTime creationDate;

    public UserModel() {

    }

    public UserModel(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @JsonDeserialize(using = LocalDateDeserializer.class)
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(id, userModel.id) &&
                Objects.equals(username, userModel.username) &&
                Objects.equals(password, userModel.password) &&
                Objects.equals(name, userModel.name) &&
                Objects.equals(surname, userModel.surname) &&
                Objects.equals(email, userModel.email) &&
                Objects.equals(creationDate, userModel.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, name, surname, email, creationDate);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserModel{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", surname='").append(surname).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", creationDate=").append(creationDate);
        sb.append('}');
        return sb.toString();
    }
}
