package com.ychs.web.home.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ychs.utils.ResultUtils;
import com.ychs.utils.ResultVo;
import com.ychs.web.contract.entity.Contract;
import com.ychs.web.contract.service.ContractService;
import com.ychs.web.home.entity.Echart;
import com.ychs.web.home.entity.EchartsItem;
import com.ychs.web.home.entity.TotalVo;
import com.ychs.web.lawyer.entity.Lawyer;
import com.ychs.web.lawyer.service.LawyerService;
import com.ychs.web.notice.entity.SysNotice;
import com.ychs.web.notice.service.SysNoticeService;
import com.ychs.web.sys_user.entity.SysUser;
import com.ychs.web.sys_user.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 首页统计
 * 数据源约定：contract 表即「案件」表。
 * 进行案件 = status=1（正常在途）；结算案件 = invoice_flag=1（已开票）。
 */
@RestController
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private LawyerService lawyerService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private SysNoticeService sysNoticeService;

    //首页总数统计：用户总数 / 律师总数 / 进行案件 / 结算案件
    @GetMapping("/getTotal")
    public ResultVo getTotal() {
        TotalVo vo = new TotalVo();
        //用户总数（可用账号 is_enabled=1；用字符串列名避免 isEnabled 布尔字段的 Lambda 解析问题）
        long userCount = sysUserService.count(new QueryWrapper<SysUser>()
                .eq("is_enabled", 1));
        vo.setUserCount(userCount);
        //律师总数
        long lawyerCount = lawyerService.count(new LambdaQueryWrapper<Lawyer>()
                .eq(Lawyer::getDelFlag, 0));
        vo.setLawyerCount(lawyerCount);
        //进行案件（正常在途）
        long ongoingCount = contractService.count(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getDelFlag, 0)
                .eq(Contract::getStatus, 1));
        vo.setOngoingCount(ongoingCount);
        //结算案件（已开票）
        long settledCount = contractService.count(new LambdaQueryWrapper<Contract>()
                .eq(Contract::getDelFlag, 0)
                .eq(Contract::getInvoiceFlag, 1));
        vo.setSettledCount(settledCount);
        return ResultUtils.success("查询成功", vo);
    }

    //首页每月案件统计（柱状图）
    @GetMapping("/getEchartTotal")
    public ResultVo getEchartTotal() {
        QueryWrapper<Contract> qw = new QueryWrapper<>();
        //注意：groupBy/orderBy 不能直接传函数表达式（MyBatis-Plus 会用反引号包裹成非法列名），故用 last 追加原生 SQL
        qw.select("DATE_FORMAT(receive_date,'%Y-%m') AS month, COUNT(*) AS cnt")
                .eq("del_flag", 0)
                .isNotNull("receive_date")
                .last(" GROUP BY DATE_FORMAT(receive_date,'%Y-%m') ORDER BY DATE_FORMAT(receive_date,'%Y-%m')");
        List<Map<String, Object>> list = contractService.listMaps(qw);
        Echart echart = new Echart();
        List<String> names = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        if (list != null) {
            for (Map<String, Object> m : list) {
                names.add(String.valueOf(m.get("month")));
                Object cnt = m.get("cnt");
                values.add(cnt == null ? 0 : ((Number) cnt).intValue());
            }
        }
        echart.setNames(names);
        echart.setValues(values);
        return ResultUtils.success("查询成功", echart);
    }

    //首页案件类型统计（饼状图）
    @GetMapping("/getCaseType")
    public ResultVo getCaseType() {
        QueryWrapper<Contract> qw = new QueryWrapper<>();
        qw.select("case_type AS name, COUNT(*) AS value")
                .eq("del_flag", 0)
                .isNotNull("case_type")
                .ne("case_type", "")
                .groupBy("case_type");
        List<Map<String, Object>> list = contractService.listMaps(qw);
        List<EchartsItem> data = new ArrayList<>();
        if (list != null) {
            for (Map<String, Object> m : list) {
                EchartsItem item = new EchartsItem();
                item.setName(String.valueOf(m.get("name")));
                Object value = m.get("value");
                item.setValue(value == null ? 0 : ((Number) value).intValue());
                data.add(item);
            }
        }
        return ResultUtils.success("查询成功", data);
    }

    //首页最受欢迎律师（环形图）
    @GetMapping("/getBestSale")
    public ResultVo getBestSale() {
        QueryWrapper<Contract> qw = new QueryWrapper<>();
        qw.select("lawyer_name AS name, COUNT(*) AS value")
                .eq("del_flag", 0)
                .isNotNull("lawyer_name")
                .ne("lawyer_name", "")
                .groupBy("lawyer_name")
                .orderByDesc("value")
                .last(" limit 10");
        List<Map<String, Object>> list = contractService.listMaps(qw);
        List<EchartsItem> data = new ArrayList<>();
        if (list != null) {
            for (Map<String, Object> m : list) {
                EchartsItem item = new EchartsItem();
                item.setName(String.valueOf(m.get("name")));
                Object value = m.get("value");
                item.setValue(value == null ? 0 : ((Number) value).intValue());
                data.add(item);
            }
        }
        return ResultUtils.success("查询成功", data);
    }

    //首页公告（全部有效公告，按时间倒序；前端区域内滚动展示）
    @GetMapping("/getNoticeList")
    public ResultVo getNoticeList() {
        LambdaQueryWrapper<SysNotice> query = new LambdaQueryWrapper<>();
        query.eq(SysNotice::getDelFlag, 0)
                .orderByDesc(SysNotice::getCreateTime);
        List<SysNotice> list = sysNoticeService.list(query);
        return ResultUtils.success("查询成功", list);
    }
}
