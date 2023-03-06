package com.utiltest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whz
 * @date 2023/3/6 15:47
 */
public class ListUtilTest {

    public static void main(String[] args) {
        List<String> _OLD_LIST = Arrays.asList(
                "唐僧,悟空,八戒,沙僧,曹操,刘备,孙权".split(","));
        List<List<String>> list = partition(_OLD_LIST, 3);
        System.out.println(list);
        list = partition(_OLD_LIST, 20);
        System.out.println(list);
    }

    static <T> List<List<T>> partition(List<T> list, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("size必须大于0");
        }
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        List<List<T>> newList = new ArrayList<>();
        if (list.size() <= size) {
            newList.add(list);
        } else {
            for (int i = 0; i < list.size(); i++) {
                int start = i * size;
                int end = (i + 1) * size;
                newList.add(list.subList(start, end));
                int remainder = list.size() - end;
                if (remainder > 0 && remainder <= size) {
                    newList.add(list.subList(end, list.size()));
                    break;
                }
            }
        }
        return newList;
    }
}
