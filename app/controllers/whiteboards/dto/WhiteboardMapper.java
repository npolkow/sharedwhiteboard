package controllers.whiteboards.dto;

import actors.events.socket.boardstate.SimpleUser;
import controllers.common.Paths;
import controllers.common.dto.XHref;
import model.user.entities.User;
import model.whiteboards.entities.Whiteboard;

/**
 * Created by Flo on 07.05.2015.
 */
public class WhiteboardMapper {


    public static class UserDescription {
        private String username;

        public UserDescription(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }
    }

    public static Whiteboard mapFromNewWriteDTO(NewWhiteboardWriteDTO dto) {
        Whiteboard entity = new Whiteboard();

        entity.setName(dto.getName());

        return entity;
    }

    public static WhiteboardReadDTO mapEntityToReadDTO(Whiteboard whiteboard) {
        WhiteboardReadDTO dto = new WhiteboardReadDTO();

        mapCommonReadDTOProps(whiteboard, dto);

        return dto;
    }

    public static WhiteboardReadDetailDTO mapEntityToReadDetailDTO(Whiteboard whiteboard) {
        WhiteboardReadDetailDTO dto = new WhiteboardReadDetailDTO();

        mapCommonReadDTOProps(whiteboard, dto);

        return dto;
    }

    private static void mapCommonReadDTOProps(Whiteboard whiteboard, WhiteboardReadDTO dto) {
        dto.setName(whiteboard.getName());
        dto.setId(whiteboard.getId());
        dto.setSocket(new XHref("connect", Paths.TicketPathForSockets(whiteboard), XHref.POST, null));

        dto.setOwner(new XHref(null, Paths.USERS_FULL, null, new SimpleUser(whiteboard.getOwner().getId(), whiteboard.getOwner().getUsername())));
        for (User u : whiteboard.getCollaborators()) {
            dto.getCollaborators().add(new XHref(null, Paths.USERS_FULL, null, new SimpleUser(u.getId(), u.getUsername())));
        }
    }

}
