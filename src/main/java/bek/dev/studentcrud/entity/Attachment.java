package bek.dev.studentcrud.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@Data
@Entity
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "Id  of the Attachment")
    private Long id;

    @Column(nullable = false)
    @ApiModelProperty(notes = "fileOriginalName of the Attachment",name="fileOriginalName",required=true,value="test img.jpeg")
    private String fileOriginalName;

    @Column(nullable = false)
    @ApiModelProperty(notes = "Size of the Attachment",name="size",required=true)
    private long size;

    @ApiModelProperty(notes = "contentType of the Attachment",name="contentType",required=true,value="test img/jpeg")
    private String contentType;

    private String name;
}
