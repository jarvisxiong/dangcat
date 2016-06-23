package org.dangcat.commons.reflect.examples;

import java.util.List;
import java.util.Map;

public class MethodGeneric<T, K> {
    public void execute1(T tParam, K kParam) {
    }

    public void execute2(List<T> tList, List<K> kList) {
    }

    public void execute3(Map<T, K> tMap, Map<K, T> kMap) {
    }

    public void execute4(T[] tArray, K[] kArray) {
    }
}
