/*
 * 英才汇硕信息科技有限公司 拥有本软件版权 2024-2028 并保留所有权利。
 * Copyright 2024-2028, YCHS Information&Science Technology Co.,Ltd,
 * All rights reserved.
 */
package com.ychs.web.lawyer.entity;

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

/**
 *
 * @author fanyuyang
 * @version 1.0
 */
@Data
@Accessors(chain = true)
@TableName("lawyer")
@Schema(name = "Lawyer", description = "$!{table.comment}")
public class Lawyer extends Model<Lawyer> {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "姓名")
    @TableField("name")
    private String name;

    @Schema(description = "性别 1男 2女")
    @TableField("gender")
    private Byte gender;

    @Schema(description = "民族")
    @TableField("nation")
    private String nation;

    @Schema(description = "出生日期")
    @TableField("birth_date")
    private Date birthDate;

    @Schema(description = "毕业院校")
    @TableField("graduate_school")
    private String graduateSchool;

    @Schema(description = "专业")
    @TableField("major")
    private String major;

    @Schema(description = "学历")
    @TableField("education")
    private String education;

    @Schema(description = "政治面貌")
    @TableField("political_status")
    private String politicalStatus;

    @Schema(description = "入党时间")
    @TableField("party_time")
    private Date partyTime;

    @Schema(description = "身份证号")
    @TableField("id_card")
    private String idCard;

    @Schema(description = "手机号")
    @TableField("phone")
    private String phone;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "现居住地")
    @TableField("address")
    private String address;

    @Schema(description = "执业证号")
    @TableField("practice_cert_no")
    private String practiceCertNo;

    @Schema(description = "取得职业资格日期")
    @TableField("qualification_get_date")
    private Date qualificationGetDate;

    @Schema(description = "业务特长,逗号分隔：刑事案件,民事案件...")
    @TableField("business_specialty")
    private String businessSpecialty;

    @Schema(description = "是否合伙人 0否1是")
    @TableField("is_partner")
    private Byte isPartner;

    @Schema(description = "聘用日期")
    @TableField("hire_date")
    private Date hireDate;

    @Schema(description = "律师类型 1专职律师 2兼职律师")
    @TableField("lawyer_type")
    private Byte lawyerType;

    @Schema(description = "兼职律师工作单位")
    @TableField("part_time_company")
    private String partTimeCompany;

    @Schema(description = "是否人大代表/政协委员 0否1是")
    @TableField("is_cpc_or_committee")
    private Byte isCpcOrCommittee;

    @Schema(description = "受过的奖励")
    @TableField("award_record")
    private String awardRecord;

    @Schema(description = "受过的处分")
    @TableField("punish_record")
    private String punishRecord;

    @Schema(description = "律师在职状态 0离职 1在职")
    @TableField("work_status")
    private Byte workStatus;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "蓝底一寸照片地址")
    @TableField("photo_url")
    private String photoUrl;

    @Schema(description = "个人简历附件地址(大学起简历)")
    @TableField("resume_url")
    private String resumeUrl;

    @Schema(description = "首次社保缴费日期")
    @TableField("first_social_date")
    private Date firstSocialDate;

    @Schema(description = "社保编号")
    @TableField("social_no")
    private String socialNo;

    @Schema(description = "社会职务（其它组织担任的职务）")
    @TableField("social_post")
    private String socialPost;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @Schema(description = "删除标识 0正常1删除")
    @TableField("del_flag")
    private Byte delFlag;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
