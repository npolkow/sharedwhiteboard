package model.user.entities;

import model.AbstractEntity;
import model.user.UserAlreadyExistsException;
import model.whiteboards.entities.Whiteboard;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F;
//import play.db.jpa.JPA;
//import play.libs.F;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Flo on 27.04.2015.
 */
@Entity
public class User extends AbstractEntity {

    @Column(unique = true)
    private String username;

    private String password;

    @ManyToMany
    @JoinTable(name = "whiteboard_collaborators")
    private Set<Whiteboard> whiteboards = new HashSet<>();

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

    /**
     * @return Set of Whiteboards, on which this user is collaborating
     */
    public Set<Whiteboard> getWhiteboards() {
        return whiteboards;
    }
}
