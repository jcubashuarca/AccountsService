package com.nttdata.bootcamp.accountsservice.serviceimpl;

import com.nttdata.bootcamp.accountsservice.dto.LinkRequest;
import com.nttdata.bootcamp.accountsservice.dto.MessageKafka;
import com.nttdata.bootcamp.accountsservice.dto.Result;
import com.nttdata.bootcamp.accountsservice.dto.Yanki;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageService {

    @Autowired
    private StreamBridge streamBridge;

    public boolean sendToYanki(Yanki yanki){
        return streamBridge.send("toyanki-out-0",yanki);
    }
    public boolean sendResult(Result result){
        streamBridge.send("result-out-0",result);
        return result.getStatus().equals("Success");
    }

    public boolean sendToLink(LinkRequest linkRequest){
        return streamBridge.send("link-out-0",linkRequest);
    }

    public boolean sendProcess(MessageKafka messageKafka) {
        return streamBridge.send("proccessaccount-out-0", messageKafka);
    }

}
