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
 * 充值信息
 * @TableName recharge_info
 */
@TableName(value ="recharge_info")
@Data
public class RechargeInfo implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 充值订单编号
     */
    private String orderNo;

    /**
     * 充值状态：0901-未支付 0902-已支付 0903-已取消
     */
    private String rechargeStatus;

    /**
     * 充值金额
     */
    private BigDecimal rechargeAmount;

    /**
     * 支付方式：1101-微信 1102-支付宝
     */
    private String payWay;

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