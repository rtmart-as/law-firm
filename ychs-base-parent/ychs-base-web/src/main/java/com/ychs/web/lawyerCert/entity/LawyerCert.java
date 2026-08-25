package com.ychs.web.lawyerCert.entity;

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
@TableName("lawyer_cert")
@Schema(name = "LawyerCert", description = "律师证书")
public class LawyerCert extends Model<LawyerCert> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "律师ID")
    @TableField("lawyer_id")
    private Long lawyerId;

    @Schema(description = "证书名称（自填，如专业代理人证/证券基金从业资格证/律师等级资格证/获奖证书）")
    @TableField("cert_name")
    private String certName;

    @Schema(description = "证书编号")
    @TableField("cert_no")
    private String certNo;

    @Schema(description = "发证机关")
    @TableField("issue_org")
    private String issueOrg;

    @Schema(description = "发证日期")
    @TableField("issue_date")
    private Date issueDate;

    @Schema(description = "证书扫描件地址")
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