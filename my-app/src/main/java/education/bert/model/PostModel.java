package education.bert.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostModel {
    private int id;
    private String postName;
    private int creatorId;
}
