import io.vertx.mokabyte.model.TodoModel;
import io.vertx.mokabyte.model.UserModel;

public class TestUtil {

    public static TodoModel createTestModel() {
        final UserModel userModel = new UserModel();
        userModel.setName("Marco");
        userModel.setSurname("Rotondi");
        userModel.setEmail("email@email.it");
        userModel.setUsername("mrc");
        userModel.setPassword("secret");

        final TodoModel todoModel = new TodoModel();
        todoModel.setTodoText("Appointment with All");
        todoModel.setUser(userModel);

        return todoModel;
    }
}
