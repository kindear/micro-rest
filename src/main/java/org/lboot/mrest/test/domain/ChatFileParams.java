package org.lboot.mrest.test.domain;

import lombok.Data;

import java.io.File;

@Data
public class ChatFileParams {
    File file;

    String purpose;
}
