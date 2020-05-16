package com.autolearn.icve.entity.icve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户加班级/课程id信息
 *
 * @author 胡江斌
 * @version 1.0
 * @title: IcveUser
 * @projectName autolearn
 * @description: TODO
 * @date 2020/1/15 18:36
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class IcveUserAndId extends IcveUser implements Serializable {

    private String courseId;
    private String courseOpenId;
    private String openClassId;

}
