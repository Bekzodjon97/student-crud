package bek.dev.studentcrud.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Data
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "Id name of the Visit", name = "id")
    private Long id;

    @Column(nullable = false, updatable = false)
    @ApiModelProperty(notes = "Come time name of the Student")
    private Date comeTime;

    @ApiModelProperty(notes = "Back time of the Student")
    private Date backTime;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(optional = false)
    @ApiModelProperty(notes = "Student of the Visit")
    private Student student;

    @Override
    public String toString() {
        return "Visit{" +
                "id=" + id +
                ", comeTime=" + comeTime +
                ", backTime=" + backTime +
                ", student=" + student +
                '}';
    }
}