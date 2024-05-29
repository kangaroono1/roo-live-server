package com.roo.entity.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 *   `pk_uid` bigint unsigned NOT NULL COMMENT '用户id',
 *   `password` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '签名密码',
 *   `nick_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '昵称',
 *   `qq_avatar_url` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'qq头像url',
 *   `qq_open_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT 'qq open id',
 *   `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
 *   `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
 *   `gmt_modified` datetime DEFAULT NULL COMMENT '最后修改时间',
 *   `gmt_last_login` datetime DEFAULT NULL COMMENT '最后登录时间',
 *   `status` tinyint unsigned DEFAULT NULL COMMENT '0:禁用 1:启用',
 *   `use_space` bigint unsigned DEFAULT NULL COMMENT '已用空间',
 *   `total_space` bigint DEFAULT NULL COMMENT '总空间',
 */
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
@TableName(value = "tb_user")
public class UserInfo {
    @TableId("pk_uid")
    private Long uid;
    @TableField("password")
    private String password;
    @TableField("nick_name")
    private String nickName;
    @TableField("qq_avatar_url")
    private String qqAvatarUrl;
    @TableField("qq_open_id")
    private String qqOpenId;
    @TableField("email")
    private String email;
    @TableField("gmt_create")
    private Date gmtCreate;
    @TableField("gmt_modified")
    private Date gmtModified;
    @TableField("gmt_last_login")
    private Date gmtLastLogin;
    @TableField("status")
    private short status;
    @TableField("use_space")
    private Long useSpace;
    @TableField("total_space")
    private Long totalSpace;
}
