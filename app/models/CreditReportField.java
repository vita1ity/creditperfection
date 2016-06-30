package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class CreditReportField extends Model {
    @Id
    public int id;
    public String name;
    public String value;
    @ManyToOne(cascade = CascadeType.ALL)
    public CreditReport creditReport;
}
