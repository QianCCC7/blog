package com.xiaoqian.common.service;

import com.xiaoqian.common.domain.ResponseResult;
import com.xiaoqian.common.domain.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaoqian.common.domain.vo.CommentVo;
import com.xiaoqian.common.domain.vo.PageVo;

/**
 * <p>
 * 评论表 服务类
 * </p>
 *
 * @author QianCCC
 * @since 2024-02-04
 */
public interface ICommentService extends IService<Comment> {

    ResponseResult<PageVo<CommentVo>> queryCommentList(Long articleId, Integer pageNo, Integer pageSize);
}
