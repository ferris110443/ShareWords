package org.yplin.project.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.yplin.project.data.dto.WorkspaceMemberDto;

import java.util.List;

@Repository
public class WorkspaceMemberRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WorkspaceMemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WorkspaceMemberDto> findWorkspaceMembersByWorkspaceId(long roomNumber) {
        String sql = "SELECT ui.email, ui.name, ui.user_image_url, w.workspace_name, uw.user_id, uw.workspace_id, w.workspace_owner " +
                "FROM user_workspace uw " +
                "JOIN workspace w ON w.id = uw.workspace_id " +
                "JOIN user_information ui ON ui.id = uw.user_id " +
                "WHERE w.id = ?";

        return jdbcTemplate.query(sql, new Object[]{roomNumber}, new RowMapper<WorkspaceMemberDto>() {
            @Override
            public WorkspaceMemberDto mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
                WorkspaceMemberDto dto = new WorkspaceMemberDto();
                dto.setEmail(rs.getString("email"));
                dto.setName(rs.getString("name"));
                dto.setWorkspaceName(rs.getString("workspace_name"));
                dto.setUserId(rs.getLong("user_id"));
                dto.setWorkspaceId(rs.getLong("workspace_id"));
                dto.setWorkspaceOwner(rs.getString("workspace_owner"));
                dto.setUserImageUrl(rs.getString("user_image_url"));
                return dto;
            }
        });
    }
}
