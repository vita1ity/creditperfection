package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class CreditReport extends Model {
    @Id
    public int id;
    public String bureau;
    @OneToMany(cascade = CascadeType.ALL)
    public List<CreditReportField> fields;
}
