package org.example.laptrinhmang.common.model;
//Dữ liệu: Các Vai Trò Người Dùng trong hệ thống (Enum: ADMIN, TEACHER, STUDENT).
public enum UserRole {
    STUDENT(1),
    TEACHER(2),
    ADMIN(3);

    private final int roleId;

    UserRole(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleId() {
        return roleId;
    }

    // Có thể thêm phương thức để lấy UserRole từ roleId nếu cần
    public static UserRole fromRoleId(int roleId) {
        for (UserRole role : UserRole.values()) {
            if (role.getRoleId() == roleId) {
                return role;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy UserRole với roleId: " + roleId);
    }
}
