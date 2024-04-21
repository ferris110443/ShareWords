package org.yplin.project.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.yplin.project.data.form.MarkdownForm;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FileContentBatchInsertRepository {
    String sql = "UPDATE file_content SET content = ?, file_title = ? WHERE file_id = ?";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void updateFileContentInBatch(List<MarkdownForm> markdownForms) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MarkdownForm markdownForm = markdownForms.get(i);
                ps.setString(1, markdownForm.getMarkdownText());
                ps.setString(2, markdownForm.getTitle());
                ps.setString(3, markdownForm.getFileId());
            }

            @Override
            public int getBatchSize() {
                return markdownForms.size();
            }
        });
    }
}
