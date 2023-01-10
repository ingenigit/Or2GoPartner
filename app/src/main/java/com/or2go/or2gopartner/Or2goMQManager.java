package com.or2go.or2gopartner;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

public class Or2goMQManager {
    Context mContext;

    AppEnv gAppEnv;

    String mMsgKeyName;

    //static final String EXCHANGE_NAME = "genipos";
    static final String EXCHANGE_NAME = "or2go";
    //static final String QUEUE_NAME = "genipos.8895269273.queue";
    static String QUEUE_NAME;// = "genipos.8895269273.queue";
    Connection gposConn;
    Channel gposMsgChannel=null;
    String 		gposQueueName = "";

    Integer   mMqMsgNo=0;

    Or2goMQManager(Context con)
    {
        mContext = con;
        gAppEnv = (AppEnv)mContext;
        mMqMsgNo=0;

        mMsgKeyName = gAppEnv.gAppSettings.getStoreId();

        //QUEUE_NAME = "genipos."+mUserId+".queue";
        QUEUE_NAME = "or2go."+mMsgKeyName+".queue";

        System.out.println("Initializing Messaging Manager: queue="+QUEUE_NAME+ "  Vendor Id="+gAppEnv.gAppSettings.getVendorId());
        //Toast.makeText(mContext, "Initializing Messaging Manager: ",Toast.LENGTH_LONG).show();

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    initBrokerConnection();

                    ///registerConsumer(1000);

                    while (true)
                    {
                        Thread.sleep(3000);
                        if (gposMsgChannel!= null)
                        {
                            registerConsumer(1000);

                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }


    void initBrokerConnection() throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("139.162.49.92");
        factory.setUsername("geniposmq");
        factory.setPassword("ing3ni*mq");
        /*factory.setHost("172.105.54.66");
        factory.setUsername("or2goadmin");
        factory.setPassword("ing3ni*or2go");*/
        factory.setRequestedHeartbeat(10);
        factory.setConnectionTimeout(5000);
        factory.setNetworkRecoveryInterval(5000);
        factory.setAutomaticRecoveryEnabled(true);

        //factory.setTopologyRecoveryEnabled(true);


        gposConn = factory.newConnection();
        gposMsgChannel = gposConn.createChannel();

        gposMsgChannel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC,true);
        ///gposQueueName = gposMsgChannel.queueDeclare().getQueue();
        ///gposMsgChannel.queueBind(gposQueueName, EXCHANGE_NAME, /*"genipos.global"*/);

        //durable, non-exclusive, non-autodelete queue with a well-known name
        gposMsgChannel.queueDeclare(QUEUE_NAME, true, false, false, null);

        //gposMsgChannel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "genipos.global");
        gposMsgChannel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "or2go.global");

        //gposMsgChannel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "genipos.8895269273");
        //gposMsgChannel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "genipos."+mUserId);
        gposMsgChannel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "or2go."+mMsgKeyName);

    }

    void registerConsumer(final int timeout) throws IOException {

        //QueueingConsumer
        Consumer consumer = new DefaultConsumer(gposMsgChannel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {

                String message = new String(body, "UTF-8");
                gAppEnv.getGposLogger().d(" [x] Rabbitmq Msg Received '" + envelope.getRoutingKey() + "':'" + message + "'");
                //Toast.makeText(mContext, "Rabbitmq Msg Received: ",Toast.LENGTH_LONG).show();
                ///GposAlert alert = new GposAlert();
                ///alert.alertType = "AMQP";
                //alert.alertData1 = "";
                //alert.alertMessage = message;
                ///gAppEnv.getAlertManager().setAlert(alert);
                Handler mHandler = gAppEnv.getOr2goMsgHandler();
                if (mHandler != null) {
                    Message msg = mHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("RoutingKey", envelope.getRoutingKey());
                    bundle.putString("Or2Go", message);
                    msg.setData(bundle);
                    msg.what = getMqmMsgNo();
                    mHandler.sendMessage(msg);
                }
                		/*
                		try {
                			Thread.sleep(timeout);
                		} catch (Exception e) {
                			//Toast.makeText(mContext, "Rabbitmq Consumer: Exception ",Toast.LENGTH_LONG).show();
                		}*/
            }
        };

        gposMsgChannel.basicConsume(gposQueueName, true /* auto-ack */, consumer);

    }

    int getMqmMsgNo()
    {
        int newno = mMqMsgNo;

        mMqMsgNo++;

        return newno;
    }

    void shutdownMQ()
    {
        try {
            gposConn.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
