package com.ychs.web.notice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@Accessors(chain = true)
@TableName("sys_notice")
@Schema(name = "SysNotice", description = "系统公告")
public class SysNotice extends Model<SysNotice> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "公告标题")
    @TableField("title")
    private String title;

    @Schema(description = "公告内容")
    @TableField("content")
    private String content;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @Schema(description = "删除标识 0正常 1删除")
    @TableField("del_flag")
    private Byte delFlag;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
