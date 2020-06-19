package com.autolearn.icve.entity.icve.dto;

import com.xiaoleilu.hutool.json.JSONArray;
import com.xiaoleilu.hutool.util.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 单元/组列表
 *
 * @author h1525
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CellListDTO {
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 课程id
     */
    private String courseOpenId;
    /**
     * 班级id
     */
    private String openClassId;
    /**
     * 单元列表
     */
    private List<CellList> cellList;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CellList {
        private String Id;
        private String categoryName;
        private String categoryNameDb;
        private String cellContent;
        private String cellName;
        private Integer cellType;
        private List<CellList> childNodeList;
        private String courseOpenId;
        private String externalLinkUrl;
        private Integer fromType;
        private String isAllowDownLoad;
        private Integer isGJS;
        private Integer isOriginal;
        private String parentId;
        private String resourceUrl;
        private Integer sortOrder;
        private Integer stuCellCount;
        private Integer stuCellPercent;
        private String topicId;
        private String upCellId;

        /**
         * 有组的情况下，组下的单元多出的属性
         */
        private String stuCellFourCount;
        private String stuCellFourPercent;

        public void setChildNodeList(List<CellList> childNodeList) {
            if (CollectionUtil.isNotEmpty(childNodeList)) {
                JSONArray nodeListJson = new JSONArray(childNodeList);
                this.childNodeList = nodeListJson.toList(CellList.class);
            }
        }
    }

}
