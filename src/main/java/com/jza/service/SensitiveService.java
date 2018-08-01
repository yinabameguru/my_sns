package com.jza.service;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);

    private static final String DEFAULT_REPLACEMENT = "***";

    private class TrieNode{

        private boolean end = false;

        private Map<Character,TrieNode> subNodes = new HashMap<>();

        //addSubNode getSubNode isKeywordEnd setKeywordEnd getSubNodeCount
        void addSubNode(Character key,TrieNode value){
            subNodes.put(key,value);
        }

        TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        boolean isKeywordEnd() {
            return end;
        }

        void setKeywordEnd(boolean end) {
            this.end = end;
        }

        int getSubNodeCount() {
            return subNodes.size();
        }
    }

    private TrieNode rootNode = new TrieNode();

    private boolean isSymbol(char c){
        int ic = (int) c;
        return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
    }

    public String filter(String text){
        if (StringUtils.isEmpty(text))
            return text;
        String replacement = DEFAULT_REPLACEMENT;
        StringBuilder result = new StringBuilder();
        TrieNode tempNode = rootNode;
        int begin = 0;
        int position = 0;

        while (position < text.length()) {
            char c = text.charAt(position);
            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    result.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {
                result.append(text.charAt(begin));
                begin++;
                position = begin;
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                result.append(replacement);
                position++;
                begin = position;
                tempNode = rootNode;
            }else
                position++;
        }
        result.append(text.substring(begin));
        return result.toString();
    }

    private void addWord(String lineTxt) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < lineTxt.length(); ++i) {
            Character c = lineTxt.charAt(i);
            if (isSymbol(c)) {
                continue;
            }
            TrieNode node = tempNode.getSubNode(c);
            if (node == null) {
                node = new TrieNode();
                tempNode.addSubNode(c, node);
            }
            tempNode = node;
            if (i == lineTxt.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        rootNode = new TrieNode();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
            InputStreamReader reader = new InputStreamReader(is);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String lineTxt;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                lineTxt = lineTxt.trim();
                addWord(lineTxt);
            }
            reader.close();
        }catch (Exception e){
            logger.error("读取敏感词文件失败" + e.getMessage());
        }
    }
//    public static void main(String[] argv) {
//        SensitiveService s = new SensitiveService();
//        s.addWord("色情");
//        System.out.print(s.filter("你好X色**情XX"));
//    }
}

