package com.laisen.autojob.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laisen.autojob.core.constants.Constants;
import com.laisen.autojob.core.dto.AccountDTO;
import com.laisen.autojob.core.entity.Account;
import com.laisen.autojob.core.repository.AccountRepository;
import com.laisen.autojob.core.utils.AESUtil;
import com.laisen.autojob.quartz.QuartzJob;
import com.laisen.autojob.quartz.entity.QuartzBean;
import com.laisen.autojob.quartz.repository.QuartzBeanRepository;
import com.laisen.autojob.quartz.util.QuartzUtils;
import io.vavr.control.Try;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private Scheduler            scheduler;
    @Autowired
    private AccountRepository    accountRepository;
    @Autowired
    private QuartzBeanRepository quartzBeanRepository;

    @GetMapping("getopenid")
    public String getOpenId(String code) {
        String secret = System.getenv("wx127525214d4abbe0_secret");
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=wx127525214d4abbe0&secret=" + secret + "&js_code=" + code
                + "&grant_type=authorization_code";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        return Try.of(() -> {
            Response response = client.newCall(request).execute();
            String responseText = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            Map map = mapper.readValue(responseText, Map.class);
            String openid = map.get("openid").toString();

            return openid;
        }).getOrElse("");
    }

    @PostMapping("/create")
    @ResponseBody
    public String createJob(@RequestBody AccountDTO dto) {
        try {
            final String userId = dto.getUserId();
            if (Objects.isNull(userId)) {
                return "用户信息不正确";
            }
            Account account = new Account();
            account.setUserId(userId);
            account.setPassword(AESUtil.encrypt(dto.getPassword()));
            account.setTime((dto.getHour() < 10 ? "0" + dto.getHour() : dto.getHour()) + ":" + (dto.getMins() < 10 ? "0" + dto.getMins()
                    : dto.getMins()));

            account = accountRepository.save(account);
            QuartzBean quartzBean = quartzBeanRepository.findByAccountId(account.getId());
            if (Objects.isNull(quartzBean)) {
                quartzBean = new QuartzBean();
            }
            quartzBean.setUserId(userId);
            quartzBean.setAccountId(account.getId());
            quartzBean.setJobClass(QuartzJob.class.getName());

            quartzBean.setJobName(String.format(Constants.JOB_PATTERN, dto.getType(), userId, account.getId()));
            //"0 0 12 * * ?" 每天中午12点触发
            quartzBean.setCronExpression("0 " + dto.getMins() + " " + dto.getHour() + " * * ?");
            //            quartzBean.setCronExpression("*/10 * * * * ?");

            quartzBeanRepository.save(quartzBean);
            try {
                QuartzUtils.createScheduleJob(scheduler, quartzBean);
            } catch (Exception e) {
                QuartzUtils.updateScheduleJob(scheduler, quartzBean);
            }
            QuartzUtils.runOnce(scheduler, quartzBean.getJobName());
        } catch (Exception e) {
            e.printStackTrace();
            return "配置失败";
        }
        return "配置成功";
    }

    @PostMapping("/get")
    @ResponseBody
    public List getDetail(@RequestBody AccountDTO dto) {
        List<Account> accountList = accountRepository.findAllByUserIdAndType(dto.getUserId(), dto.getType());
        List<AccountDTO> result = new ArrayList<>();
        for (Account account : accountList) {
            AccountDTO vo = new AccountDTO();
            if (!Objects.isNull(account)) {
                vo.setAccount(account.getAccount());
                vo.setPassword("****");
                vo.setTime(account.getTime());
            } else {
                vo.setAccount("");
                vo.setPassword("");
                vo.setTime("");
            }
        }
        return result;
    }

    @PostMapping("/delete")
    @ResponseBody
    public String deleteJob(@RequestBody AccountDTO dto) {
        try {
            QuartzBean quartzBean = quartzBeanRepository.findByAccountId(dto.getId());
            if (!Objects.isNull(quartzBean)) {
                QuartzUtils.deleteScheduleJob(scheduler, quartzBean.getJobName());
                quartzBeanRepository.delete(quartzBean);
            }
            accountRepository.deleteById(dto.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return "删除失败";
        }
        return "删除成功";
    }
}
