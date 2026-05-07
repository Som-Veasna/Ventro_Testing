package com.ventro.ventro_testing.repository;

import com.ventro.ventro_testing.model.entity.User;
import com.ventro.ventro_testing.model.request.UpdateStaffRequest;
import com.ventro.ventro_testing.utils.UUIDTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;
@Mapper
public interface StaffRepository {

    @Insert("""
        INSERT INTO users (user_id, first_name, last_name, email, password,
                           phone_number, branch_id,
                           requires_password_change, is_active,
                           role_id, created_at, updated_at)
        VALUES (
            #{user.userId}::uuid,
            #{user.firstName},
            #{user.lastName},
            #{user.email},
            #{user.password},
            #{user.phoneNumber},
            #{user.branchId}::uuid,
            true,
            true,
            #{user.role.roleId}::uuid,
            now(), now()
        )
    """)
    void insertStaff(@Param("user") User user);

    @Select("""
        SELECT u.user_id, u.first_name, u.last_name, u.image, u.email,
               u.password, u.requires_password_change, u.phone_number,
               u.is_active, u.branch_id, u.created_at, u.updated_at,
               r.role_id, r.role_name,
               b.branch_id as b_branch_id,
               b.branch_name,
               b.location as branch_location
        FROM users u
        JOIN roles r ON u.role_id = r.role_id
        LEFT JOIN branches b ON u.branch_id = b.branch_id
        WHERE u.user_id = #{userId}::uuid
    """)
    @Results(id = "StaffMapper", value = {
            @Result(property = "userId",                 column = "user_id",        typeHandler = UUIDTypeHandler.class),
            @Result(property = "firstName",              column = "first_name"),
            @Result(property = "lastName",               column = "last_name"),
            @Result(property = "image",                  column = "image"),
            @Result(property = "email",                  column = "email"),
            @Result(property = "password",               column = "password"),
            @Result(property = "requiresPasswordChange", column = "requires_password_change"),
            @Result(property = "phoneNumber",            column = "phone_number"),
            @Result(property = "isActive",               column = "is_active"),
            @Result(property = "branchId",               column = "branch_id",      typeHandler = UUIDTypeHandler.class),
            @Result(property = "createdAt",              column = "created_at"),
            @Result(property = "updatedAt",              column = "updated_at"),
            @Result(property = "role.roleId",            column = "role_id",        typeHandler = UUIDTypeHandler.class),
            @Result(property = "role.roleName",          column = "role_name"),
            @Result(property = "branch.branchId",        column = "b_branch_id",    typeHandler = UUIDTypeHandler.class),
            @Result(property = "branch.branchName",      column = "branch_name"),
            @Result(property = "branch.location",        column = "branch_location")
    })
    User findById(@Param("userId") UUID userId);

    @Select("""
        SELECT u.user_id, u.first_name, u.last_name, u.image, u.email,
               u.password, u.requires_password_change, u.phone_number,
               u.is_active, u.branch_id, u.created_at, u.updated_at,
               r.role_id, r.role_name,
               b.branch_id as b_branch_id,
               b.branch_name,
               b.location as branch_location
        FROM users u
        JOIN roles r ON u.role_id = r.role_id
        LEFT JOIN branches b ON u.branch_id = b.branch_id
        WHERE r.role_name IN ('MANAGER', 'STAFF')
        ORDER BY u.created_at DESC
    """)
    @Results(id = "StaffListMapper", value = {
            @Result(property = "userId",                 column = "user_id",        typeHandler = UUIDTypeHandler.class),
            @Result(property = "firstName",              column = "first_name"),
            @Result(property = "lastName",               column = "last_name"),
            @Result(property = "image",                  column = "image"),
            @Result(property = "email",                  column = "email"),
            @Result(property = "password",               column = "password"),
            @Result(property = "requiresPasswordChange", column = "requires_password_change"),
            @Result(property = "phoneNumber",            column = "phone_number"),
            @Result(property = "isActive",               column = "is_active"),
            @Result(property = "branchId",               column = "branch_id",      typeHandler = UUIDTypeHandler.class),
            @Result(property = "createdAt",              column = "created_at"),
            @Result(property = "updatedAt",              column = "updated_at"),
            @Result(property = "role.roleId",            column = "role_id",        typeHandler = UUIDTypeHandler.class),
            @Result(property = "role.roleName",          column = "role_name"),
            @Result(property = "branch.branchId",        column = "b_branch_id",    typeHandler = UUIDTypeHandler.class),
            @Result(property = "branch.branchName",      column = "branch_name"),
            @Result(property = "branch.location",        column = "branch_location")
    })
    List<User> findAllStaff();

    @Select("""
        UPDATE users
        SET first_name   = #{req.firstName},
            last_name    = #{req.lastName},
            phone_number = #{req.phoneNumber},
            branch_id    = #{req.branchId}::uuid,
            is_active    = #{req.isActive},
            updated_at   = now()
        WHERE user_id = #{userId}::uuid
        RETURNING *
    """)
    @ResultMap("StaffListMapper")
    User updateStaff(@Param("userId") UUID userId,
                     @Param("req") UpdateStaffRequest req);

    @Update("""
        UPDATE users
        SET is_active  = false,
            updated_at = now()
        WHERE user_id = #{userId}::uuid
    """)
    void deactivate(@Param("userId") UUID userId);
}