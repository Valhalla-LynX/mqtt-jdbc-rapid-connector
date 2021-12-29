package com.bjdv.dbconnector.mqtt;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttPublishException extends MqttException {
    private String pushMsg = null;

    public MqttPublishException(MqttException me, String pushMsg) {
        super(me.getReasonCode());
        this.pushMsg = pushMsg;
    }

    public MqttPublishException(int reasonCode) {
        super(reasonCode);
    }

    public MqttPublishException(Throwable cause) {
        super(cause);
    }

    public MqttPublishException(int reason, Throwable cause) {
        super(reason, cause);
    }

    public String getPushMsg() {
        return pushMsg;
    }
}
