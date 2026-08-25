package com.ychs.web.admission.entity;

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
@TableName("admission_attachment")
@Schema(name = "AdmissionAttachment", description = "入所申请附件")
public class AdmissionAttachment extends Model<AdmissionAttachment> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "申请ID")
    @TableField("apply_id")
    private Long applyId;

    @Schema(description = "附件类型 1身份证 2毕业证 3学位证 4法律职业资格证 5蓝底一寸照片 6个人简历 7其他")
    @TableField("att_type")
    private Byte attType;

    @Schema(description = "附件名称")
    @TableField("att_name")
    private String attName;

    @Schema(description = "附件地址")
    @TableField("att_url")
    private String attUrl;

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