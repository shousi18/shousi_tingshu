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
 * 用户账户明细
 * @TableName user_account_detail
 */
@TableName(value ="user_account_detail")
@Data
public class UserAccountDetail implements Serializable {
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
     * 交易标题
     */
    private String title;

    /**
     * 交易类型：1201-充值 1202-锁定 1203-解锁 1204-消费
     */
    private String tradeType;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 订单编号
     */
    private String orderNo;

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
    private String isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}