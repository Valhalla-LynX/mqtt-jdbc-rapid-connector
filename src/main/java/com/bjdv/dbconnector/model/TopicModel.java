package com.bjdv.dbconnector.model;

import com.bjdv.dbconnector.direct.JDBCHolder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-27 17:28
 **/
@Data
public class TopicModel {
    private String datasource = JDBCHolder.getMaster();
    @NotBlank(message = "topic required")
    private String topic;
    private String share;
    @NotBlank(message = "table required")
    private String table;
    @NotNull(message = "column required")
    private String[] column;
    @NotNull(message = "type required")
    private DataType[] type;

    public enum DataType implements Serializable {
        Number(), String();

        DataType() {
        }
    }
}
