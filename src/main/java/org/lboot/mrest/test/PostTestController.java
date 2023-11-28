package org.lboot.mrest.test;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.lboot.mrest.annotation.Get;
import org.lboot.mrest.annotation.Post;
import org.lboot.mrest.test.domain.PostVO;
import org.lboot.mrest.test.domain.QueryParams;
import org.lboot.mrest.test.service.TestPostApi;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("test")
@Api(tags = "测试接口")
@AllArgsConstructor
public class PostTestController {
    TestPostApi testPostApi;
    @GetMapping("posts")
    @ApiOperation(value = "查询列表")
    public Object getList(QueryParams params){
        return testPostApi.getUserPosts(params.getUserId());
    }


    @GetMapping("posts/{id}")
    @ApiOperation(value = "查询单条数据")
    public Object getPost(@PathVariable("id") Long id){
        return testPostApi.getPost(id);
    }

    @PostMapping("posts")
    @ApiOperation(value = "文章新建")
    public Object postCreate(@RequestBody PostVO vo){
        return testPostApi.createPost(vo);
    }
}
