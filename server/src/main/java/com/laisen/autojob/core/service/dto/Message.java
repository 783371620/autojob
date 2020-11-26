
package com.laisen.autojob.core.service.dto;

import lombok.Data;

/**
 * @author lise
 * @version Message.java, v 0.1 2020年11月26日 14:28 lise
 */
@Data
public class Message {
    private String     touser;
    private String     template_id;
    private String     lang = "zh_CN";
    private DataDetail data;

}