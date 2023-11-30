package org.lboot.mrest.test.service;

import org.lboot.mrest.annotation.Body;
import org.lboot.mrest.annotation.Get;
import org.lboot.mrest.annotation.MicroRest;
import org.lboot.mrest.annotation.Post;
import org.lboot.mrest.test.domain.PostVO;

import java.util.List;

@MicroRest
public interface PostApi {

    @Get(url = "http://jsonplaceholder.typicode.com/posts")
    List<PostVO> getPosts();

    @Post(url = "http://jsonplaceholder.typicode.com/posts")
    PostVO writePost(@Body PostVO vo);
}
