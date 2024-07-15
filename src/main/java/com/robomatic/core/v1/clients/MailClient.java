package com.robomatic.core.v1.clients;

import com.robomatic.core.v1.models.SendMailRequest;

public interface MailClient {

    void sendMail(SendMailRequest req);

}
