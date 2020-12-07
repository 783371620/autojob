package com.laisen.autojob.core.controller;

import com.laisen.autojob.core.dto.AccountDTO;
import com.laisen.autojob.core.entity.EventLog;
import com.laisen.autojob.core.repository.AccountRepository;
import com.laisen.autojob.core.repository.EventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("logs")
public class LogController extends BaseController {
    @Autowired
    private EventLogRepository eventLogRepository;

    @PostMapping("/log")
    @ResponseBody
    public List getLogs(@RequestBody AccountDTO dto) {
        EventLog el = new EventLog();
        el.setUserId(dto.getUserId());
        el.setType(dto.getType());
        Example<EventLog> ex = Example.of(el);
        PageRequest page = PageRequest.of(0, 20, Sort.by(Direction.DESC, "gmtCreate"));
        List<EventLog> logs = eventLogRepository.findAll(ex, page).toList();
        return logs;
    }
}
