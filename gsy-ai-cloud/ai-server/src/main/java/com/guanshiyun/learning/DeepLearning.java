package com.guanshiyun.learning;

import ai.djl.Device;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeepLearning {

    public static void main(String[] args) {
        try (NDManager manager = NDManager.newBaseManager(Device.gpu())) {
            NDArray arange = manager.arange(12);
            log.info("arange:\n {}", arange);
            Shape shape = arange.getShape();
            log.info("shape:\n {}", shape);
            NDArray reshape = arange.reshape(new Shape(3, 4));
            log.info("reshape:\n {}", reshape);
            NDArray ndArray = manager.create(new Shape(3, 4));
            log.info("create:\n {}", ndArray);

            NDArray ndArray1 = manager.create(new float[]{2, 1, 4, 3, 1, 2, 3, 4, 4, 3, 2, 1}, new Shape(3, 4));
            log.info("create:\n {}", ndArray1);
            NDArray x = manager.create(new float[]{1f, 2f, 4f, 8f});
            NDArray y = manager.create(new float[]{2f, 2f, 2f, 2f});
            NDArray add = x.add(y);
            log.info("add:\n {}", add);
            NDArray sub = x.sub(y);
            log.info("sub:\n {}", sub);
            NDArray mul = x.mul(y);
            log.info("mul:\n {}", mul);
            NDArray pow = x.pow(y);
            log.info("pow:\n {}", pow);
        } catch (Exception e) {
            log.error("Error occurred", e);
        }
    }
}
