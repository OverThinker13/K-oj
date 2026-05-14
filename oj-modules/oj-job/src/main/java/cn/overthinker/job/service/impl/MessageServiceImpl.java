package cn.overthinker.job.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.overthinker.job.domain.message.Message;
import cn.overthinker.job.mapper.message.MessageMapper;
import cn.overthinker.job.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Override
    public boolean batchInsert(List<Message> messageList) {
        return saveBatch(messageList);
    }
}
