
package com.laisen.autojob.core.constants;

/**
 * @author lise
 * @version Constants.java, v 0.1 2020年11月27日 22:59 lise
 */
public interface Constants {
    String MODULE_EVERPHOTO = "everphoto";
    String MODULE_CLOUD189  = "cloud189";

    String LOG_MODULES_CORE      = "系统";
    String LOG_MODULES_EVERPHOTO = "时光相册";
    String LOG_MODULES_CLOUD189  = "天翼云盘";
    String LOG_OPERATE_LOGIN     = "登录";
    String LOG_OPERATE_CHECKIN   = "签到";
    String LOG_OPERATE_LOTTERY   = "抽奖";
    String LOG_OPERATE_JOB       = "定时任务";

    /**
     * type.job.userId.accountId
     */
    String JOB_PATTERN = "%s.job.%s.%s";

    String JOB_PREFIX_CLOUD189  = "cloud189.job.";
    String JOB_PREFIX_EVERPHOTO = "everphoto.job.";
}