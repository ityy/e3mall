package cn.yang.e3mall.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * 使用spring配置ActiveMQ后
 * 发送消息
 * 可以用普通的接收消息的方法来测试接收
 */
public class ActiveSpringSendTest {

    @Test
    public void sendMesage() throws Exception {
        //初始化spring容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");

        //从容器获得jms模板对象
        JmsTemplate jmsTemplate = applicationContext.getBean(JmsTemplate.class);

        //从容器获得destination对象
        Destination destination = (Destination) applicationContext.getBean("topicDestination");//根据ID来取
        //发送消息
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //创建一个消息对象并返回
                TextMessage textMessage = session.createTextMessage("spring activemq test message");
                return textMessage;

            }
        });

    }


}
