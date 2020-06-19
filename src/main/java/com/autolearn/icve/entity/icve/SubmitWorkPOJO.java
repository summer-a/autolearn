package com.autolearn.icve.entity.icve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 胡江斌
 * @version 1.0
 * @title: SubmitWorkPOJO
 * @projectName autolearn
 * @description: TODO
 * @date 2020/6/18 21:13
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SubmitWorkPOJO {

    private String cookie;
    private String uniqueId;
    private String homeworkId;
    private String openClassId;
    private String homeworkTermTimeId;
    private String data;

}
