package com.ychs.web.lawyerArchiveTransfer.entity;

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
@TableName("lawyer_archive_transfer")
@Schema(name = "LawyerArchiveTransfer", description = "律师档案调转记录")
public class LawyerArchiveTransfer extends Model<LawyerArchiveTransfer> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "律师ID")
    @TableField("lawyer_id")
    private Long lawyerId;

    @Schema(description = "调转类型 1调出 2调入")
    @TableField("transfer_type")
    private Byte transferType;

    @Schema(description = "原档案托管机构")
    @TableField("from_org")
    private String fromOrg;

    @Schema(description = "现档案托管机构")
    @TableField("to_org")
    private String toOrg;

    @Schema(description = "调转日期")
    @TableField("transfer_date")
    private Date transferDate;

    @Schema(description = "调转材料附件地址")
    @TableField("file_url")
    private String fileUrl;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

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