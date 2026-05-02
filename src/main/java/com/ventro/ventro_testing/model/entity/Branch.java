package com.ventro.ventro_testing.model.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    private UUID branchId;
    private String branchName;
    private String location;
    private LocalDateTime createdAt;
}