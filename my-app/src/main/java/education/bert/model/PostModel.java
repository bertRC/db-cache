package education.bert.model;

import java.util.Objects;

public class PostModel {
    private int id;
    private String postName;
    private int creatorId;

    public PostModel(int id, String postName, int creatorId) {
        this.id = id;
        this.postName = postName;
        this.creatorId = creatorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public String toString() {
        return "PostModel{" +
                "id=" + id +
                ", postName='" + postName + '\'' +
                ", creatorId=" + creatorId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostModel postModel = (PostModel) o;
        return id == postModel.id &&
                creatorId == postModel.creatorId &&
                postName.equals(postModel.postName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postName, creatorId);
    }
}
