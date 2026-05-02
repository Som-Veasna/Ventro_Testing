package com.ventro.ventro_testing.model.entity;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private UUID roleId;
    private String roleName;
}