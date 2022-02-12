package bek.dev.studentcrud.payload;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Result {
    private String message;
    private boolean success;

}
