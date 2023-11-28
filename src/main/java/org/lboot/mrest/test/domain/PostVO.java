package org.lboot.mrest.test.domain;

import lombok.Data;

@Data
public class PostVO {
    Long userId;
    Long id;
    String title;
    String body;
}
