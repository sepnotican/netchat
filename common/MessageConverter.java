package common;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class MessageConverter {

    private static final MessageConverter instance;

    static {
        instance = new MessageConverter();
    }

    private JAXBContext jaxbContext;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;


    private MessageConverter() {
        try {
            jaxbContext = JAXBContext.newInstance(Message.class);
            marshaller = jaxbContext.createMarshaller();
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static MessageConverter getInstance() {
        return instance;
    }

    public String marshall(Message message) {
        OutputStream out = new ByteArrayOutputStream();
        try {
            marshaller.marshal(message, out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    public Message unmarshall(String xmlMessage) {
        try {
            final byte[] bytes = xmlMessage.getBytes("UTF-8");
            return (Message) unmarshaller.unmarshal(new ByteArrayInputStream(bytes));
        } catch (JAXBException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
