package bek.dev.studentcrud.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Data
public class Student{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "Id of the Student",name="id")
    private Long id;

    @Column(nullable = false)
    @ApiModelProperty(notes = "First name of the Student",name="firstName",required=true,value="test firsName")
    @NotBlank
    @Size(min = 0, max = 50)
    private String firstName;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Last name of the Student",name="lastName",required=true,value="test lastName")
    @NotBlank
    @Size(min = 0, max = 50)
    private String lastName;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Birth date of the Student",name="birthDate",required=true)
    private LocalDate birthDate;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Group name of the Student",name="groupName",required=true,value="test groupName")
    @NotBlank
    @Size(min = 0, max = 50)
    private String groupName;

    @Transient
    private Integer age;

    @OneToOne(cascade = CascadeType.ALL)
    @ApiModelProperty(notes = "Attachment of the Student")
    private Attachment attachment;

    @JsonIgnore
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Visit> visit;


    public Integer getAge() {
        if (this.birthDate==null)
            return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", groupName='" + groupName + '\'' +
                ", age=" + age +
                ", attachment=" + attachment +
                ", visit=" + visit +
                '}';
    }
}
