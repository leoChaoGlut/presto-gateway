package personal.leo.presto.gateway;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class CommonTest {
    @Test
    public void test() {
        System.out.println(Arrays.asList("a", "b").stream()
                .filter(s -> s.equalsIgnoreCase("c"))
                .collect(Collectors.toList()));
    }

    @Test
    public void test1() throws Exception {
        final StopWatch watch = StopWatch.createStarted();
        try (
                final CloseableHttpClient client = HttpClients.createDefault();
        ) {
            HttpPost post = new HttpPost("http://query05:10100/ui/login");
            post.setEntity(new UrlEncodedFormEntity(Arrays.asList(
                    new BasicNameValuePair("username", "guest")
            )));
            final CloseableHttpResponse resp = client.execute(post);
            System.out.println(resp.getStatusLine().getStatusCode());
//
            HttpGet get = new HttpGet("http://query05:10100/ui/api/stats");
            final CloseableHttpResponse resp2 = client.execute(get);
            final String json = IOUtils.toString(resp2.getEntity().getContent(), StandardCharsets.UTF_8);
            System.out.println(JSON.parseObject(json).getInteger("activeWorkers"));
        }
        watch.stop();
        System.out.println(watch);
    }

    @Test
    public void test2() {
        final Properties kafkaProducerProps = new Properties();
        kafkaProducerProps.put("bootstrap.servers", "mq1:9200,mq2:9200,mq3:9200");
        kafkaProducerProps.put("acks", "all");
        kafkaProducerProps.put("key.serializer", StringSerializer.class.getName());
        kafkaProducerProps.put("value.serializer", StringSerializer.class.getName());

        final KafkaProducer<Object, Object> kafkaProducer = new KafkaProducer<>(kafkaProducerProps);
        kafkaProducer.send(new ProducerRecord<>("test", "abc"));
        kafkaProducer.flush();
        kafkaProducer.close();
    }

    @Test
    public void test3() throws ParseException {
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        System.out.println(utc);
        String d1 = "2021-01-27 17:51:57";
        String d2 = "2021-01-27 23:51:57";
        final String format = "yyyy-MM-dd HH:mm:ss";
        final Date dd1 = DateUtils.parseDate(d1, format);
        System.out.println(dd1);
//        final Date dd2 = DateUtils.parseDate(d2, format);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(sdf.format(dd1));
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        System.out.println(sdf.format(dd1));
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-8"));
        System.out.println(sdf.format(dd1));
        sdf.setTimeZone(TimeZone.getTimeZone("CST"));
        System.out.println(sdf.format(dd1));
    }
}
