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
 * CREATE TABLE `tb_file` (
 *   `pk_file_id` bigint unsigned NOT NULL COMMENT '文件ID',
 *   `file_md5` varchar(32) DEFAULT NULL COMMENT '文件md5值',
 *   `file_pid` bigint unsigned DEFAULT NULL COMMENT '文件父级ID',
 *   `file_size` bigint unsigned DEFAULT NULL COMMENT '文件大小字节数',
 *   `file_name` varchar(200) DEFAULT NULL COMMENT '文件名',
 *   `file_cover` varchar(100) DEFAULT NULL COMMENT '封面',
 *   `file_path` varchar(100) DEFAULT NULL COMMENT '文件路径',
 *   `gmt_create` datetime DEFAULT NULL COMMENT '文件创建时间',
 *   `gmt_modified` datetime DEFAULT NULL COMMENT '文件更新时间',
 *   `folder_type` tinyint unsigned DEFAULT NULL COMMENT '文件类型 0: 文件 1: 目录',
 *   `status` tinyint unsigned DEFAULT NULL COMMENT '0:转码中 1:转码成功 2:转码失败',
 *   `recovery_time` datetime DEFAULT NULL COMMENT '进入回收站时间',
 *   `del_flag` tinyint unsigned DEFAULT NULL COMMENT '标记删除 0: 删除 1: 回收站 2: 正常',
 *   PRIMARY KEY (`pk_file_id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
 */
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
@TableName(value = "tb_file")
public class FileInfo {

    @TableId("pk_file_id")
    private Long pkFileId;

    @TableField("file_md5")
    private String fileMd5;

    @TableField("file_pid")
    private Long filePid;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_name")
    private String fileName;

    @TableField("file_cover")
    private String fileCover;

    @TableField("file_path")
    private String filePath;

    @TableField("gmt_create")
    private Date gmtCreate;

    @TableField("gmt_modified")
    private Date gmtModified;

    @TableField("folder_type")
    private short folderType;

    @TableField("status")
    private short status;

    @TableField("recovery_time")
    private Date recoveryTime;

    @TableField("del_flag")
    private short delFlag;
}
