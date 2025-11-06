package com.xinghe.zmoudle;

import lombok.*;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
class ZMoudleApplicationTests {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() throws InterruptedException {
        String queueName = "test.queue";
        String message = "hello world";
        for(int i = 0; i < 100; i++){
            rabbitTemplate.convertAndSend(queueName, message+ i);
//            Thread.sleep(1000);
        }

    }

    public static void main(String[] args) {
        List<Integer> list = List.of(2, 7, 11, 15);
        List<ResultNumber> newList;
        int target = 9;
//        for (int i = 0; i < list.size(); i++) {
//            for (int i1 = i+1; i1 < list.size()-1; i1++) {
//                if(list.get(i)+list.get(i1)==target){
//                    newList.add(
//                            ResultNumber.builder()
//                                    .number1(list.get(i))
//                                    .number2(list.get(i1))
//                                    .build()
//                    );
//                }
//            }
//        }
       newList = IntStream.range(0, list.size())
                .boxed()
                .flatMap(i ->
                        IntStream.range(i + 1, list.size())
                                .boxed()
                                .filter(i1 -> list.get(i) + list.get(i1) == target)
                                .map(i1 ->
                                        ResultNumber.builder()
                                                .number1(list.get(i))
                                                .number2(list.get(i1))
                                                .build()
                                )
                ).toList();
        newList.forEach(System.out::println);
    }

}
@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class ResultNumber{
    private int number1;
    private int number2;
}
class Test1{
    public static void main(String[] args) {

    }
}
