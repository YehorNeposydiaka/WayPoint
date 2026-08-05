package org.example.Trip.DTO;

import jakarta.validation.constraints.NotNull;
import org.example.Trip.Entity.MemberRole;


public record UpdateMemberRoleRequest (
    @NotNull MemberRole role
) {}
