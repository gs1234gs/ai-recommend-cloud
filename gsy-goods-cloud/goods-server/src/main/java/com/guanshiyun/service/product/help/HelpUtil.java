package com.guanshiyun.service.product.help;

import com.db.dbnumber.ConstNumber;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HelpUtil {

    // 衰减系数 λ 可自行调参，论文固定 0.1
    private static final double LAMBDA = 0.1;
    public static void shuffleInGroups(List<Long> list) {
        int groupSize = ConstNumber.INT_FIVE;
        if (list == null || list.size() <= 1) {
            return;
        }

        int size = list.size();
        for (int i = 0; i < size; i += groupSize) {
            // 计算当前组的结束位置
            int end = Math.min(i + groupSize, size);

            // 如果组内元素大于 1 个，则打乱该子列表
            if (end - i > 1) {
                // subList 返回的是视图，直接 shuffle 会影响原 list
                Collections.shuffle(list.subList(i, end));
            }
        }
    }

    /**
     * 时间衰减权重计算 公式：w = e^(-λ * Δt)
     *
     * @param behaviorTime 行为时间
     * @param now          当前时间
     * @return 衰减权重
     */
    public static double getTimeDecayWeight(LocalDateTime behaviorTime, LocalDateTime now) {
        if (Objects.isNull(behaviorTime)) {
            return 0.0;
        }
        // 计算时间差 小时
        long hourDiff = ChronoUnit.HOURS.between(behaviorTime, now);
        // 指数时间衰减
        return Math.exp(-LAMBDA * hourDiff);
    }
}
