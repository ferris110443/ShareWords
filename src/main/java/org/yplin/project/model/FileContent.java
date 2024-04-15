package org.yplin.project.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_content")
@Data
@NoArgsConstructor
public class FileContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id", nullable = false)
    private int fileId = 123;

    @Column(name = "file_title", nullable = false)
    private String fileTitle = "test";

    @Column(name = "content",columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_URL", nullable = false)
    private String fileURL;


    @Override
    public String toString() {
        return "FileContent{" +
                "id=" + id +
                ", fileId='" + fileId + '\'' +
                ", fileTitle='" + fileTitle + '\'' +
                ", content='" + content + '\'' +
                ", fileURL='" + fileURL + '\'' +
                '}';
    }
}
