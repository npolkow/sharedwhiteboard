package controllers.users;

import controllers.users.dto.UserMapper;
import controllers.users.dto.UserReadDTO;
import model.user.entities.User;
import model.user.repositories.UserRepo;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Flo on 27.04.2015.
 */
public class UserCollectionController extends Controller {

    public static Result getAllUsers() {
        List<User> users = UserRepo.findAll();

        List<UserReadDTO> dtos = new ArrayList<>();
        for (User u : users) {
            dtos.add(UserMapper.mapToReadDTO(u));
        }

        return ok(Json.toJson(dtos));
    }
}
