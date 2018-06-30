package cn.yang.e3mall.test;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

import javax.jms.*;

/**
 * 点对点的消息队列测试
 * 可以先执行Producer，后执行Consumer以获取消息，因为消息没被接收时会被保存在服务端。
 * 也可以先执行Consumer，Consumer处于监听状态。此时执行多次Producer，都可以在Consumer中取出。
 */
public class ActiveQueueTest {


    /**
     * 队列 点到点 发送消息
     * 执行此方法，向ActiveMQ服务器发送消息
     * @throws Exception
     */
    @Test
    public void testQueueProducer() throws  Exception {

        // 第一步：创建ConnectionFactory对象，需要指定服务端ip及端口号。
            //brokerURL服务器的ip及端口号，端口号默认61616
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.131:61616");

        // 第二步：使用ConnectionFactory对象创建一个Connection对象。
        Connection connection = connectionFactory.createConnection();

        // 第三步：开启连接，调用Connection对象的start方法。
        connection.start();

        // 第四步：使用Connection对象创建一个Session对象。
            //第一个参数：是否开启事务。true：开启事务，第二个参数忽略。
            //第二个参数：当第一个参数为false时，才有意义。消息的应答模式。1、自动应答2、手动应答。一般是自动应答。
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // 第五步：使用Session对象创建一个Destination对象（topic、queue），此处创建一个Queue对象。
            //参数：队列的名称。
        Queue queue = session.createQueue("test-queue");

        // 第六步：使用Session对象创建一个Producer对象。
        MessageProducer producer = session.createProducer(queue);

        // 第七步：创建一个Message对象，创建一个TextMessage对象。
            /*TextMessage message = new ActiveMQTextMessage();
            message.setText("hello activeMq,this is my first test.");*/
        TextMessage textMessage = session.createTextMessage("hello activeMq,this is my first test.");

        // 第八步：使用Producer对象发送消息。
        producer.send(textMessage);

        // 第九步：关闭资源。
        producer.close();
        session.close();
        connection.close();
    }

    /**
     * 队列 点到点 接收消息
     * 执行此方法，从ActiveMQ服务器获取消息
     * @throws Exception
     */
    @Test
    public void testQueueConsumer() throws Exception {
        // 第一步：创建一个ConnectionFactory对象。
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.131:61616");
        // 第二步：从ConnectionFactory对象中获得一个Connection对象。
        Connection connection = connectionFactory.createConnection();
        // 第三步：开启连接。调用Connection对象的start方法。
        connection.start();
        // 第四步：使用Connection对象创建一个Session对象。
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // 第五步：使用Session对象创建一个Destination对象。和发送端保持一致queue，并且队列的名称一致。
        Queue queue = session.createQueue("test-queue");
        // 第六步：使用Session对象创建一个Consumer对象。
        MessageConsumer consumer = session.createConsumer(queue);
        // 第七步：接收消息。
        consumer.setMessageListener(new MessageListener() {

            @Override
            public void onMessage(Message message) {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    String text = null;
                    //取消息的内容
                    text = textMessage.getText();
                    // 第八步：打印消息。
                    System.out.println(text);
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
        //等待键盘输入
        System.in.read();
        // 第九步：关闭资源
        consumer.close();
        session.close();
        connection.close();
    }



}
