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
    public String first_name;
    public String last_name;
    public String email;
    public String zip;
    public String password;
    public String token;
    public boolean active;

}
