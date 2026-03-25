package com.guanshiyun.rpc.behaviorapi.click;

import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.rpc.apisave.UserClickSaveApiVO;
import com.guanshiyun.rpc.profile.ClickProfileApi;
import reactor.core.publisher.Mono;


import java.util.List;

public interface UserClickServiceApi {
    // 获取用户点击记录
    Mono<ResultT<List<ClickProfileApi>>> findUserClickRecord(Integer rows);
    //保存用户点击记录
    Mono<ResultT<Long>> saveUserClickRecord(UserClickSaveApiVO userClickSaveApiVO);
}
