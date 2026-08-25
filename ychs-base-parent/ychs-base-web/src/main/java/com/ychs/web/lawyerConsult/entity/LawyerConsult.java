package com.ychs.web.lawyerConsult.entity;

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
@TableName("lawyer_consult")
@Schema(name = "LawyerConsult", description = "法律顾问单位")
public class LawyerConsult extends Model<LawyerConsult> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "律师ID")
    @TableField("lawyer_id")
    private Long lawyerId;

    @Schema(description = "顾问单位名称")
    @TableField("company_name")
    private String companyName;

    @Schema(description = "担任职务")
    @TableField("position")
    private String position;

    @Schema(description = "担任起始日期")
    @TableField("start_date")
    private Date startDate;

    @Schema(description = "结束日期")
    @TableField("end_date")
    private Date endDate;

    @Schema(description = "是否现任 0否 1是")
    @TableField("is_current")
    private Byte isCurrent;

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