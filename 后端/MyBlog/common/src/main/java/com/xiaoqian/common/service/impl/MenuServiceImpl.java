package com.xiaoqian.common.service.impl;

import com.xiaoqian.common.constants.SystemConstants;
import com.xiaoqian.common.domain.pojo.Menu;
import com.xiaoqian.common.domain.pojo.Role;
import com.xiaoqian.common.mapper.MenuMapper;
import com.xiaoqian.common.mapper.RoleMapper;
import com.xiaoqian.common.service.IMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author QianCCC
 * @since 2024-02-27
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {
    private final RoleMapper roleMapper;

    /**
     * 查询用户的权限信息
     */
    @Override
    public List<String> queryPermissionsInfoByUserId(Long userId) {
        // 1. 判断是否为超级管理员，如果是则返回所有权限
        if (userId == 1L) {
            List<Menu> menuList = lambdaQuery()
                    .in(Menu::getMenuType, SystemConstants.MENU_TYPE_C, SystemConstants.MENU_TYPE_F)
                    .eq(Menu::getStatus, SystemConstants.MENU_STATUS_NORMAL)
                    .list();
            if (CollectionUtils.isEmpty(menuList)) {
                return new ArrayList<>();
            }
            return menuList.stream().map(Menu::getPerms).collect(Collectors.toList());
        }
        // 2. 查询普通管理员的角色集合
        List<Role> roles = roleMapper.queryRoleInfoByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }
        // 3. 查询普通管理员的角色 id集合
        List<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        // 4. 查询普通管理员菜单集合
        List<Menu> menuList = this.getBaseMapper().queryMenuInfoByRoleIds(roleIds);
        if (CollectionUtils.isEmpty(menuList)) {
            return new ArrayList<>();
        }
        // 5. 封装权限集合
        return menuList.stream()
                .map(Menu::getPerms)
                .collect(Collectors.toList());
    }

    /**
     * 查询超级管理员的菜单信息
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Menu> queryMenuInfoByAdmin() {
        List<Menu> menuList = lambdaQuery()
                .in(Menu::getMenuType, SystemConstants.MENU_TYPE_M, SystemConstants.MENU_TYPE_C)
                .eq(Menu::getStatus, SystemConstants.MENU_STATUS_NORMAL)
                .orderByAsc(Menu::getParentId, Menu::getOrderNum)
                .list();
        if (CollectionUtils.isEmpty(menuList)) {
            return new ArrayList<>();
        }
        return CollectionUtils.isEmpty(menuList) ? new ArrayList<>() : menuList;
    }

    /**
     * 查询普通管理员的菜单信息
     */
    @Override
    public List<Menu> queryMenuInfoByUserId(Long userId) {
        // 1. 查询用户的角色集合
        List<Role> roles = roleMapper.queryRoleInfoByUserId(userId);
        if (CollectionUtils.isEmpty(roles)) {
            return new ArrayList<>();
        }
        // 2. 查询用户的角色 id集合
        List<Long> roleIds = roles.stream().map(Role::getId).collect(Collectors.toList());
        List<Menu> menuList = this.getBaseMapper().queryMenuInfoByRoleIds2(roleIds);
        return CollectionUtils.isEmpty(menuList) ? new ArrayList<>() : menuList;
    }
}
