package org.yplin.project.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_content")
@Data
@NoArgsConstructor
public class FileContentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "workspace_id", nullable = false)
    private long workspaceId;

    @Column(name = "file_title", nullable = false)
    private String fileTitle;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_URL", nullable = false)
    private String fileURL;


    @Override
    public String toString() {
        return "FileContent{" +
                "id=" + id +
                ", workspaceId=" + workspaceId +
                ", fileTitle='" + fileTitle + '\'' +
                ", content='" + content + '\'' +
                ", fileURL='" + fileURL + '\'' +
                '}';
    }
}
