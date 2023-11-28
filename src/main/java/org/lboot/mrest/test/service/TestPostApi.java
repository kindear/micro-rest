package org.lboot.mrest.test.service;

import org.lboot.mrest.annotation.*;
import org.lboot.mrest.test.domain.PostVO;

import java.util.List;

@MicroRest
public interface TestPostApi {
    @Get("http://jsonplaceholder.typicode.com/posts")
    List<PostVO> getPosts();

    @Get("http://jsonplaceholder.typicode.com/posts")
    List<PostVO> getUserPosts(@Query("userId") String userId);

    @Get("http://jsonplaceholder.typicode.com/posts/{id}")
    PostVO getPost(@PathVar Long id);

    @Post("http://jsonplaceholder.typicode.com/posts")
    Object createPost(@Body PostVO postVO);
}
