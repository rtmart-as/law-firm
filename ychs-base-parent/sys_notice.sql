/*
 * 律所系统 - 系统公告模块
 * 用途：首页「公告列表」展示 + 后台「公告管理」增删改
 * 说明：菜单 id 114~117 为当前库 sys_menu 的空闲 id（已核对，可直接执行）。
 *       若在其他环境执行且 id 冲突，请改用空闲 id。
 */

-- ----------------------------
-- Table structure for `sys_notice`
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '公告内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '删除标识 0正常 1删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC COMMENT='系统公告';

-- ----------------------------
-- Records of sys_notice（示例数据，可自行增删）
-- ----------------------------
INSERT INTO `sys_notice` VALUES ('1', '欢迎使用东奥律所管理系统', '欢迎大家使用本系统，如有问题请联系管理员。', NOW(), NULL, '0');

-- ----------------------------
-- Records of sys_menu（公告管理 菜单）
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('114', '0', '公告管理', 'sys:notice:index', 'noticeList', '/notice', 'views/notice/index.vue', '1', 'Bell', '顶级菜单', '6', NOW(), NULL);
INSERT INTO `sys_menu` VALUES ('115', '114', '新增', 'sys:notice:add', '', '', '', '2', '', '公告管理', '61', NOW(), NULL);
INSERT INTO `sys_menu` VALUES ('116', '114', '编辑', 'sys:notice:edit', '', '', '', '2', '', '公告管理', '62', NOW(), NULL);
INSERT INTO `sys_menu` VALUES ('117', '114', '删除', 'sys:notice:delete', '', '', '', '2', '', '公告管理', '63', NOW(), NULL);

-- ----------------------------
-- Records of sys_role_menu（给「系统管理员」角色 role_id=13 授权公告管理）
-- 其他角色请在页面「角色管理 → 分配权限」中勾选
-- ----------------------------
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES ('13', '114');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES ('13', '115');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES ('13', '116');
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES ('13', '117');
