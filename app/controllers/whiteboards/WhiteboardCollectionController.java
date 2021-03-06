package controllers.whiteboards;

import actors.events.socket.boardstate.SimpleUser;
import actors.events.socket.liststate.ListStateChangedEvent;
import controllers.common.Paths;
import controllers.common.dto.LinkedDTO;
import controllers.common.dto.XHref;
import controllers.common.mediatypes.ConsumesJSON;
import controllers.common.security.AuthRequired;
import controllers.users.dto.NewUserWriteDTO;
import controllers.users.routes;
import controllers.whiteboards.dto.NewWhiteboardWriteDTO;
import controllers.whiteboards.dto.WhiteboardCollectionReadDTO;
import controllers.whiteboards.dto.WhiteboardMapper;
import model.AlreadyExistsException;
import model.user.entities.User;
import model.whiteboards.entities.Whiteboard;
import model.whiteboards.repositories.WhiteboardRepo;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;

/**
 * Producing Representations for Whiteboard Collection:
 * "{@value Paths#WHITEBOARDS_RELATIVE}"
 */
public class WhiteboardCollectionController extends Controller {

    private static WhiteboardRepo whiteboardRepo = new WhiteboardRepo();

    /**
     * GET whiteboard collection
     *
     * @return ok with {@link WhiteboardCollectionReadDTO}
     */
    @AuthRequired
    public static Result getWhiteboardCollection() {
        //fetch from db:
        List<Whiteboard> whiteboards = whiteboardRepo.findAll();
        WhiteboardCollectionReadDTO collectionDto = new WhiteboardCollectionReadDTO();

        //map:
        for (Whiteboard wb : whiteboards) {
            collectionDto.getBoards().add(WhiteboardMapper.mapEntityToReadDTO(wb));
        }

        //response:
        addCreateWhiteboardXHref(collectionDto);

        return ok(Json.toJson(collectionDto));
    }


    /**
     * POST whiteboard item
     *
     * @return created with location link to Whiteboard; 422 if whiteboard name already exists.
     */
    @ConsumesJSON
    @AuthRequired
    public static Result createNewWhiteboard() {
        //TODO test
        //parse body:
        NewWhiteboardWriteDTO newWhiteboardWriteDTO;
        try {
            newWhiteboardWriteDTO = Json.fromJson(request().body().asJson(), NewWhiteboardWriteDTO.class);
        } catch (Exception e) {
            return badRequest("Could not parse your json values:\n " + e.getCause().getMessage());
        }

        if (newWhiteboardWriteDTO.getName() == null || newWhiteboardWriteDTO.getName().isEmpty()) {
            return badRequest("no board name found.");
        }

        //map: current authenticated user will be the owner.
        Whiteboard wb = WhiteboardMapper.mapFromNewWriteDTO(newWhiteboardWriteDTO);
        User currentuser = (User) ctx().args.get("currentuser");
        wb.setOwner(currentuser);

        //persist:
        try {
            whiteboardRepo.createWhiteboard(wb);
        } catch (AlreadyExistsException e) {
            return status(422, "" + e.getMessage());
        }
        wb.getCollaborators().add(currentuser);
        whiteboardRepo.saveWhiteboard(wb);

        ListStateChangedEvent lscEvent = new ListStateChangedEvent();
        lscEvent.setUser(new SimpleUser(currentuser));
        Akka.system().eventStream().publish(lscEvent);

        //response:
        response().setHeader(
                Http.HeaderNames.LOCATION,
                Paths.forWhiteboard(wb));

        return created();
    }

    /**
     * Adds a POST-{@link XHref} to the actions-list of dto.
     *
     * @param dto the dto to add the actionlink to.
     */
    private static void addCreateWhiteboardXHref(LinkedDTO dto) {
        NewWhiteboardWriteDTO whiteboardWriteTemplate = new NewWhiteboardWriteDTO();

        whiteboardWriteTemplate.setName("My tiny Whiteboard.");

        dto.getActions().add(
                new XHref(
                        "create",
                        Paths.WHITEBOARDS_FULL,
                        XHref.POST,
                        whiteboardWriteTemplate)
        );
    }

    /**
     * "inject" (overwrite) required components.
     *
     * @param whiteboardRepo
     */
    public static void setRequired(WhiteboardRepo whiteboardRepo) {
        WhiteboardCollectionController.whiteboardRepo = whiteboardRepo;
    }

}
