package com.gpy.gpyes.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @ClassName User
 * @Description
 * @Author guopy
 * @Date 2022/3/25 14:33
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private Integer age;
}
