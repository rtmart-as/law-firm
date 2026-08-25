package com.ychs.web.lawyerArchievement.entity;

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
@TableName("lawyer_achievement")
@Schema(name = "LawyerAchievement", description = "律师学术成果")
public class LawyerAchievement extends Model<LawyerAchievement> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "律师ID")
    @TableField("lawyer_id")
    private Long lawyerId;

    @Schema(description = "论文题目")
    @TableField("paper_title")
    private String paperTitle;

    @Schema(description = "发表期刊")
    @TableField("journal")
    private String journal;

    @Schema(description = "期刊期号")
    @TableField("journal_issue")
    private String journalIssue;

    @Schema(description = "出版社")
    @TableField("publisher")
    private String publisher;

    @Schema(description = "发表日期")
    @TableField("publish_date")
    private Date publishDate;

    @Schema(description = "论文附件地址")
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