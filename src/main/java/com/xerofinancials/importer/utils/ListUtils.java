package com.xerofinancials.importer.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {
    public static <T> List<List<T>> partitions(List<T> values, int partitionSize) {
        List<List<T>> result = new ArrayList<>();

        if (partitionSize <= 0) {
            result.add(values);
            return result;
        }

        int partitionsCount = (values.size() + partitionSize - 1) / partitionSize;

        for (int i = 0; i < partitionsCount; i++) {
            List<T> partition = values
                    .stream()
                    .skip(i * partitionSize)
                    .limit(partitionSize)
                    .collect(Collectors.toList());
            result.add(partition);
        }

        return result;
    }
}
