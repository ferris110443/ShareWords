package org.yplin.project.service.impl;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEmailNameProjection {
    private String email;
    private String name;
}
