package com.jza.service;

import com.jza.dao.QuestionDao;
import com.jza.dao.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.CRC32;

@Service
public class BloomFilterService implements InitializingBean {

    @Autowired
    UserDao userDao;
    @Autowired
    QuestionDao questionDao;

    private final int NAME_K = 13;
    private final int NAME_SIZE = 5990662;
    private int[] nameFilter;

    public int[] getNameFilter() {
        return nameFilter;
    }

    public int[] buildBloomFilter(int size, int k, List<String> inputs) {
        //bit数组，length太大可以用int、long、二维数组代替
        //bit = 191701167
        //size = 5990662
        //k = 13
        int[] bits = new int[size];
        for (String input : inputs) {
            List<Long> list = doHash(input, k);
            for (long l : list) {
                bits[(int) (l % size)] = 1;
            }
        }
        return bits;
    }

    public boolean isExist(int[] bits, String s) {
        int size = bits.length;
        List<Long> list = doHash(s, NAME_K);
        for (long l : list) {
            if (bits[(int) (l % size)] == 0)
                return false;
        }
        return true;
    }

    //多次hash
    private List<Long> doHash(String s, int n) {
        LinkedList<Long> list = new LinkedList<>();
        CRC32 crc32 = new CRC32();
        crc32.update(s.getBytes());
        long res = crc32.getValue();
        for (int i = 0; i < n; i++) {
            res = res + (res >> 5) * i;
            list.add(res);
        }
        return list;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        List<String> names = questionDao.selectQuestionNames();
        nameFilter = this.buildBloomFilter(NAME_SIZE, NAME_K, names);
    }
}
