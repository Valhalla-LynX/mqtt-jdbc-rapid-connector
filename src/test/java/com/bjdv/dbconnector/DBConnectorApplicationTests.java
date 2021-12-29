package com.bjdv.dbconnector;

import com.alibaba.fastjson.JSONArray;
import com.bjdv.dbconnector.direct.JDBCHolder;
import com.bjdv.dbconnector.dynamic.service.ClickHouseService;
import com.bjdv.dbconnector.model.DBCTest;
import com.bjdv.dbconnector.mqtt.MqttPublishException;
import com.bjdv.dbconnector.mqtt.MqttService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
class DBConnectorApplicationTests {
    static final Long s = 100000L;
    static Long i = 0L;
    static Long o = 0L;
    static long sleep = 8L;
    //    @Autowired
//    DirectClickHouseService directClickHouseService;
    @Autowired
    ClickHouseService clickHouseService;
    @Autowired
    JDBCHolder jdbcHolder;
    @Autowired
    MqttService mqttService;

    public void test() {
        System.out.println("spring test");
    }

    public void testTime1000() {
        Date start, end;
        log.info("spring test");
        log.info("发起1,000次插入");
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 1000; i++) {
//            directClickHouseService.insertTest();
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("一千次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 1000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000() {
        Date start, end;
        log.info("spring test");
        log.info("发起100,000次插入");
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000; i++) {
//            directClickHouseService.insertTest();
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000T2() {
        Date start, end;
        log.info("spring test");
        log.info("发起100,000次插入");
        class TestThread extends Thread {
            @Override
            public void run() {
                for (int i = 0; i < 50000; i++) {
//                    directClickHouseService.insertTest();
                }
            }
        }
        TestThread t1 = new TestThread();
        TestThread t2 = new TestThread();
        start = new Date();
        log.info("发起时间 {}", start);
        t1.start();
        t2.start();
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime1000CP() {
        Date start, end;
        log.info("spring test");
        log.info("发起1,000次插入");
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 1000; i++) {
            clickHouseService.addOne();
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("一千次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 1000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CP() {
        Date start, end;
        log.info("spring test");
        log.info("发起100,000次插入");
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000; i++) {
            clickHouseService.addOne();
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPB50() {
        Date start, end;
        int block = 50;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPB100() {
        Date start, end;
        int block = 100;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPB500() {
        Date start, end;
        int block = 500;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPB1000() {
        Date start, end;
        int block = 1000;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPB2000() {
        Date start, end;
        int block = 2000;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPB5000() {
        Date start, end;
        int block = 5000;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPB10000() {
        Date start, end;
        int block = 10000;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPB20000() {
        Date start, end;
        int block = 20000;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPB50000() {
        Date start, end;
        int block = 50000;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 100000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime1000000CPB200000() {
        Date start, end;
        int block = 200000;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 1000000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 1000000 / (end.getTime() - start.getTime()));
    }

    public void testTime1000000CPB500000() {
        Date start, end;
        int block = 500000;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 1000000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 1000000 / (end.getTime() - start.getTime()));
    }

    public void testTime10000000CPB1000000() {
        Date start, end;
        int block = 1000000;
        log.info("spring test");
        log.info("发起100,000次插入");
        log.info("块：" + block);
        start = new Date();
        log.info("发起时间 {}", start);
        for (int i = 0; i < 10000000 / block; i++) {
            clickHouseService.add(block);
        }
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 10000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPT2() {
        Date start, end;
        log.info("spring test");
        log.info("发起100,000次插入");
        class TestThread extends Thread {
            @Override
            public void run() {
                for (int i = 0; i < 50000; i++) {
                    clickHouseService.addOne();
                }
            }
        }
        TestThread t1 = new TestThread();
        TestThread t2 = new TestThread();
        start = new Date();
        log.info("发起时间 {}", start);
        t1.start();
        t2.start();
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPT5() {
        Date start, end;
        log.info("spring test");
        log.info("发起100,000次插入");
        class TestThread extends Thread {
            @Override
            public void run() {
                for (int i = 0; i < 20000; i++) {
                    clickHouseService.addOne();
                }
            }
        }
        TestThread t1 = new TestThread();
        TestThread t2 = new TestThread();
        TestThread t3 = new TestThread();
        TestThread t4 = new TestThread();
        TestThread t5 = new TestThread();
        start = new Date();
        log.info("发起时间 {}", start);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTime100000CPT2B50() {
        Date start, end;
        log.info("spring test");
        log.info("发起100,000次插入");
        class TestThread extends Thread {
            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {
                    clickHouseService.add(50);
                }
            }
        }
        TestThread t1 = new TestThread();
        TestThread t2 = new TestThread();
        start = new Date();
        log.info("发起时间 {}", start);
        t1.start();
        t2.start();
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("十万次简单插入在{}时间内完成", end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", 100000000 / (end.getTime() - start.getTime()));
    }

    public void testTimeAll() {
        int block = 1000;
        log.info("spring test");
        log.info("单次批量块测试");
        Date start, end;
        for (int j = 0; j < 10; j++) {
            start = new Date();
            clickHouseService.add(block);
            end = new Date();
            log.info("插入条数{},执行时间{}", block, end.getTime() - start.getTime());
            block += 1000;
        }
        block = 10000;
        for (int j = 0; j < 4; j++) {
            block *= 2;
            start = new Date();
            clickHouseService.add(block);
            end = new Date();
            log.info("插入条数{},执行时间{}", block, end.getTime() - start.getTime());
        }
    }

    public void testDirectTime10000() throws InterruptedException {
        for (int j = 0; j < 20; j++) {
            Long start = new Date().getTime();
//            directClickHouseService.insertTest(10000);
            Long stop = new Date().getTime();
            System.out.println(stop - start + "毫秒-处理时间");
            Thread.sleep(2000);
//            directClickHouseService.insertTest((new Random().nextInt(10) + 1)*1000);
        }
    }

    public void testDirectTimeAll() {
        int block = 1000;
        log.info("spring test");
        log.info("单次批量块测试");
        Date start, end;
        for (int j = 0; j < 10; j++) {
            start = new Date();
//            directClickHouseService.insertTest(block);
            end = new Date();
            log.info("插入条数{},执行时间{}", block, end.getTime() - start.getTime());
            block += 1000;
        }
        block = 10000;
        for (int j = 0; j < 4; j++) {
            block *= 2;
            start = new Date();
//            directClickHouseService.insertTest(block);
            end = new Date();
            log.info("插入条数{},执行时间{}", block, end.getTime() - start.getTime());
        }
    }

    public void testNow() throws InterruptedException {
        Cache cache = new Cache();
        class IThread extends Thread {
            private int id;

            public IThread(int id) {
                this.id = id;
            }

            @SneakyThrows
            @Override
            public void run() {
                int ti = 0;
                while (ti < s / 4 / 10) {
                    cache.in(10);
                    ti++;
                    Thread.sleep(1);
                }
            }
        }
        class OThread extends Thread {
            private int id;

            public OThread(int id) {
                this.id = id;
            }

            @SneakyThrows
            @Override
            public void run() {
                while (o < s) {
                    sleep(512);
                    List list = cache.out();
                    if (list != null && !list.isEmpty()) {
                        Date start, end;
                        start = new Date();
                        clickHouseService.add(list);
                        end = new Date();
                        log.info("ID{},插入条数{},等待时间,执行时间{}", id, list.size(), end.getTime() - start.getTime());
                        o += list.size();
                    }
                }
            }
        }

        IThread i1 = new IThread(1);
        IThread i2 = new IThread(2);
        IThread i3 = new IThread(3);
        IThread i4 = new IThread(4);
        IThread i5 = new IThread(5);
        IThread i6 = new IThread(6);

        OThread o1 = new OThread(1);
        OThread o2 = new OThread(2);

        Date start, end;
        log.info("spring test");
        log.info("发起{}次插入", s);
        start = new Date();
        log.info("发起时间 {}", start);

        i1.start();
        i2.start();
        i3.start();
        i4.start();
//        i5.start();
//        i6.start();
        o1.start();
        o2.start();

        while (o < s) {
            Thread.sleep(1000);
        }
        System.out.println(i + " " + o);
        end = new Date();
        log.info("结束时间 {}", end);
        log.info("{}次简单插入在{}毫秒内完成", o, end.getTime() - start.getTime());
        log.info("每秒吞吐{}条", s * 1000 / (end.getTime() - start.getTime()));
    }

    public void jsonTest() {
        JSONArray jsonArray = JSONArray.parseArray("[10, \"21221a1\"]");
        System.out.println(jsonArray.getString(0));
        System.out.println(Integer.valueOf(jsonArray.getString(1)));
    }

    @Test
    public void loop10000() throws MqttPublishException, InterruptedException {
        Date date = new Date();
        for (int j = 0; j < 9999; j++) {
            mqttService.publish(0, false, "dbc_test", "{\"type\":\"single\",\"table\":\"dbc_test\",\"data\":[0,\"a\"]}");
        }
        mqttService.publish(0, false, "dbc_test", "{\"type\":\"single\",\"table\":\"dbc_test\",\"data\":[1,\"b\"]}");
        while (true) {
            if (clickHouseService.countTest() == 10000) {
                break;
            }
            Thread.sleep(1000);
        }
        System.out.println("===== ===== ===== ===== =====");
        System.out.println(new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS").format(date));
    }

    @Test
    public void loop100000() throws MqttPublishException, InterruptedException {
        Date date = new Date();
        for (int j = 0; j < 99999; j++) {
            mqttService.publish(0, false, "dbc_test", "{\"type\":\"single\",\"table\":\"dbc_test\",\"data\":[0,\"a\"]}");
        }
        mqttService.publish(0, false, "dbc_test", "{\"type\":\"single\",\"table\":\"dbc_test\",\"data\":[1,\"b\"]}");
        while (true) {
            if (clickHouseService.countTest() == 100000) {
                break;
            }
            Thread.sleep(1000);
        }
        System.out.println("===== ===== ===== ===== =====");
        System.out.println(new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS").format(date));
    }

    @Test
    public void loop1000000() throws MqttPublishException, InterruptedException {
        Date date = new Date();
        for (int j = 0; j < 999999; j++) {
            mqttService.publish(0, false, "dbc_test", "{\"type\":\"single\",\"table\":\"dbc_test\",\"data\":[0,\"a\"]}");
        }
        mqttService.publish(0, false, "dbc_test", "{\"type\":\"single\",\"table\":\"dbc_test\",\"data\":[1,\"b\"]}");
        while (true) {
            if (clickHouseService.countTest() == 1000000) {
                break;
            }
            Thread.sleep(1000);
        }
        System.out.println("===== ===== ===== ===== =====");
        System.out.println(new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss-SSS").format(date));
    }

    class Cache {
        LinkedList<DBCTest> list = new LinkedList<>();

        public synchronized void in() throws InterruptedException {
            list.add(new DBCTest(1, "name"));
//            System.out.println(list.size());
        }

        public synchronized void in(int n) throws InterruptedException {
            for (int j = 0; j < n; j++) {
                list.add(new DBCTest(1, "name"));
            }
//            System.out.println(list.size());
        }

        public synchronized List<DBCTest> out() {
            List nList = (List<DBCTest>) list.clone();
            list.clear();
            return nList;
        }
    }
}
