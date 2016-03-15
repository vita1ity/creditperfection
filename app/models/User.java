package models;

import com.avaje.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User extends Model {
    @Id
    public int id;
    @Column(unique = true)
    public String username;
    public String email;
    public String password;
    public boolean active;
}
