package com.jza.my_sns;

import com.jza.dao.UserDao;
import com.jza.service.BloomFilterService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MySnsApplicationTests {
    @Autowired
    UserDao userDao;
    @Autowired
    BloomFilterService bloomFilterService;
    @Test
    public void contextLoads() {
    }

    @Test
    public void test() {
//        BloomFilterService bf = new BloomFilterService();
//        bf.bloomFilter();
//        bf.doHash("aaa", 3);
        List<String> names = userDao.selectUserNames();
//        int[] ints = bf.buildBloomFilter(5990662, 13, names);
        boolean b = bloomFilterService.isExist(bloomFilterService.getNameFilter(), "meguru@163.com");
        System.out.println(b);
//        System.out.println(Arrays.toString(ints));
    }
}
