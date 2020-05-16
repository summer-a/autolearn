package com.autolearn.icve.entity.icve;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author 胡江斌
 * @version 1.0
 * @title ViewDirectoryDTO
 * @projectName autolearn
 * @description TODO
 * @date 2020/5/9 15                  43
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ViewDirectoryDTO {

    private Double audioVideoLong;
    private String categoryName;
    private String cellContent;
    private String cellId;
    private String cellLogId;
    private String cellName;
    private Integer cellPercent;
    private List<Object> cellPositions;
    private String cellQuestionList;
    private Integer cellType;
    private Integer code;
    private String courseName;
    private String courseOpenId;
    private String downLoadUrl;
    private Integer dtype;
    private String externalLinkUrl;
    private String flag;
    private String guIdToken;
    private Boolean isAllowDownLoad;
    private Boolean isDownLoad;
    private Integer isNeedUpdate;
    private String moduleId;
    private String openClassId;
    private Integer pageCount;
    private Integer position;
    private List<Object> rarList;
    private String resUrl;
    private Integer stuCellPicCount;
    private Integer stuCellViewTime;
    private Integer stuStudyNewlyPicCount;
    private Double stuStudyNewlyTime;
    private String topicId;
    private Integer userType;

    public void setPageCount(Integer pageCount) {
        if (pageCount != null) {
            this.pageCount = pageCount <= 0 ? 0 : pageCount;
        }
    }
}
