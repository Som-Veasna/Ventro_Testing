package com.ventro.ventro_testing.repository;

import com.ventro.ventro_testing.model.entity.Branch;
import com.ventro.ventro_testing.model.entity.Role;
import com.ventro.ventro_testing.model.entity.User;
import com.ventro.ventro_testing.model.request.UpdateStaffRequest;
import com.ventro.ventro_testing.utils.UUIDTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface UserRepository {

    @Select("""
    SELECT u.user_id, u.first_name, u.last_name, u.image, u.email,
           u.password, u.requires_password_change, u.phone_number, u.is_active,
           u.created_at, u.updated_at,
           r.role_id, r.role_name,
           b.branch_id as b_branch_id,
           b.branch_name,
           b.location as branch_location
    FROM users u
    JOIN roles r ON u.role_id = r.role_id
    LEFT JOIN branches b ON u.branch_id = b.branch_id
    WHERE u.email = #{email}
""")
    @Results(id = "UserMapper", value = {
            @Result(property = "userId",                  column = "user_id",              typeHandler = UUIDTypeHandler.class),
            @Result(property = "firstName",               column = "first_name"),
            @Result(property = "lastName",                column = "last_name"),
            @Result(property = "image",                   column = "image"),
            @Result(property = "email",                   column = "email"),
            @Result(property = "password",                column = "password"),
            @Result(property = "requiresPasswordChange",  column = "requires_password_change"),
            @Result(property = "phoneNumber",             column = "phone_number"),
            @Result(property = "isActive",                column = "is_active"),
            @Result(property = "createdAt",               column = "created_at"),
            @Result(property = "updatedAt",               column = "updated_at"),
            @Result(property = "role.roleId",             column = "role_id",              typeHandler = UUIDTypeHandler.class),
            @Result(property = "role.roleName",           column = "role_name"),
            @Result(property = "branch.branchId",         column = "b_branch_id",          typeHandler = UUIDTypeHandler.class),
            @Result(property = "branch.branchName",       column = "branch_name"),
            @Result(property = "branch.location",         column = "branch_location")
    })
    User findByEmail(String email);

    @Select("""
    WITH inserted AS (
        INSERT INTO users (user_id, first_name, last_name, email, password,
                           phone_number, is_active, requires_password_change,
                           role_id, created_at, updated_at)
        VALUES (
            #{user.userId}::uuid,
            #{user.firstName},
            #{user.lastName},
            #{user.email},
            #{user.password},
            #{user.phoneNumber},
            true,
            true,
            #{user.role.roleId}::uuid,
            now(), now()
        )
        RETURNING *
    )
    SELECT i.*, r.role_id, r.role_name
    FROM inserted i
    JOIN roles r ON i.role_id = r.role_id
""")
    @ResultMap("UserMapper")
    User save(@Param("user") User user);

    @Update("""
    UPDATE users
    SET password                 = #{password},
        requires_password_change = false,
        updated_at               = now()
    WHERE email = #{email}
""")
    void resetPassword(@Param("email") String email,
                       @Param("password") String password);

    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    int existsByEmail(String email);

    @Select("SELECT * FROM roles WHERE role_name = #{roleName}")
    @Results({
            @Result(property = "roleId",   column = "role_id",   typeHandler = UUIDTypeHandler.class),
            @Result(property = "roleName", column = "role_name")
    })
    Role findRoleByName(String roleName);
}