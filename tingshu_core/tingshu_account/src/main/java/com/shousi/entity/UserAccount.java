package com.shousi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 用户账户
 * @TableName user_account
 */
@TableName(value ="user_account")
@Data
public class UserAccount implements Serializable {
    /**
     * 编号
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 账户总金额
     */
    private BigDecimal totalAmount;

    /**
     * 锁定金额
     */
    private BigDecimal lockAmount;

    /**
     * 可用金额
     */
    private BigDecimal availableAmount;

    /**
     * 总收入
     */
    private BigDecimal totalIncomeAmount;

    /**
     * 总支出
     */
    private BigDecimal totalPayAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}