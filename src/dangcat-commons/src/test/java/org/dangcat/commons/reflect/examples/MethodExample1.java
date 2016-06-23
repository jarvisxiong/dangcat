package org.dangcat.commons.reflect.examples;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class MethodExample1 {
    public void execute1(int intParam, String stringParam, boolean booleanParam, double doubleParam, char charParam, byte byteParam, char[] charsParam, byte[] bytesParam, Date dateParam) {
    }

    public void execute2(Integer intParam, String stringParam, Boolean booleanParam, Double doubleParam, Character charParam, Byte byteParam, Character[] charsParam, Byte[] bytesParam, Date dateParam) {
    }

    public void execute3(List<Integer> intList, List<String> stringList, List<Boolean> booleanList, List<Double> doubleList, List<Character> charList, List<Byte> byteList,
                         List<Character[]> charsList, List<Byte[]> bytesList, List<Date> dateList) {
    }

    public void execute4(Map<Integer, Integer> intMap, Map<String, String> stringMap, Map<Boolean, Boolean> booleanMap, Map<Double, Double> doubleMap, Map<Character, Character> charMap,
                         Map<Byte, Byte> byteMap, Map<Character[], Character[]> charsMap, Map<Byte[], Byte[]> bytesMap, Map<Date, Date> dateMap) {
    }
}
