package com.gpy.gpyes.jddemo.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName Content
 * @Description
 * @Author guopy
 * @Date 2022/3/28 16:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Content {

    private String title;
    private String img;
    private String price;

}
