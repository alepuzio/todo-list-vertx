package io.vertx.mokabyte.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.vertx.mokabyte.serializer.LocalDateDeserializer;
import io.vertx.mokabyte.serializer.LocalDateSerializer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class TodoModel implements Serializable {

    private final Long id;

    private String todoText;

    private UserModel user;

    private LocalDateTime creationDate;

    public TodoModel() {
        this.id = null;
    }

    public TodoModel(Long id)  {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getTodoText() {
        return todoText;
    }

    public void setTodoText(String todoText) {
        this.todoText = todoText;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
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
        TodoModel todoModel = (TodoModel) o;
        return Objects.equals(id, todoModel.id) &&
                Objects.equals(todoText, todoModel.todoText) &&
                Objects.equals(user, todoModel.user) &&
                Objects.equals(creationDate, todoModel.creationDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, todoText, user, creationDate);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TodoModel{");
        sb.append("id=").append(id);
        sb.append(", todoText='").append(todoText).append('\'');
        sb.append(", user=").append(user);
        sb.append(", creationDate=").append(creationDate);
        sb.append('}');
        return sb.toString();
    }
}

