/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80024
Source Host           : localhost:3306
Source Database       : ychs-base

Target Server Type    : MYSQL
Target Server Version : 80024
File Encoding         : 65001

Date: 2025-04-16 15:36:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `sys_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` int NOT NULL AUTO_INCREMENT COMMENT '閼挎粌宕焛d',
  `parent_id` int DEFAULT NULL COMMENT '上级id',
  `title` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单名称',
  `code` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '权限字段',
  `name` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '路由名称',
  `path` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '路由地址',
  `url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '组件路径',
  `type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单类型 0:目录 1：菜单 2：按钮',
  `icon` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '菜单图标',
  `parent_name` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '上级菜单名称',
  `order_num` int DEFAULT NULL COMMENT '序号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '0', '系统管理', 'sys:manage', 'system', '/system', '', '0', 'Setting', '顶级菜单', '1', '2023-09-14 09:46:23', null);
INSERT INTO `sys_menu` VALUES ('2', '1', '用户管理', 'sys:user:index', 'userList', '/userList', 'views/system/User/UserList.vue', '1', 'UserFilled', '顶级菜单', '2', '2023-09-14 09:48:59', '2024-10-11 10:01:29');
INSERT INTO `sys_menu` VALUES ('3', '1', '角色管理', 'sys:role:index', 'roleList', '/roleList', 'views/system/Role/RoleList.vue', '1', 'Wallet', '系统管理', '3', '2023-09-14 09:52:40', null);
INSERT INTO `sys_menu` VALUES ('4', '1', '菜单管理', 'sys:menu:index', 'menuList', '/menuList', 'views/system/Menu/MenuList.vue', '1', 'Menu', '系统管理', '4', '2023-09-14 09:53:55', null);
INSERT INTO `sys_menu` VALUES ('8', '2', '新增', 'sys:user:add', '', '', '', '2', '', '用户管理', '21', '2023-09-14 09:58:43', null);
INSERT INTO `sys_menu` VALUES ('11', '2', '编辑', 'sys:user:edit', '', '', '', '2', '', '用户管理', '22', '2023-09-15 16:42:54', '2023-09-27 19:19:22');
INSERT INTO `sys_menu` VALUES ('12', '2', '删除', 'sys:user:delete', '', '', '', '2', '', '用户管理', '23', '2023-09-15 16:43:17', null);
INSERT INTO `sys_menu` VALUES ('13', '3', '新增', 'sys:role:add', '', '', '', '2', '', '角色管理', '31', '2023-09-15 16:44:34', null);
INSERT INTO `sys_menu` VALUES ('14', '3', '编辑', 'sys:role:edit', '', '', '', '2', '', '角色管理', '32', '2023-09-15 16:44:56', null);
INSERT INTO `sys_menu` VALUES ('15', '3', '删除', 'sys:role:delete', '', '', '', '2', '', '角色管理', '33', '2023-09-15 16:45:17', null);
INSERT INTO `sys_menu` VALUES ('16', '4', '新增', 'sys:menu:add', '', '', '', '2', '', '菜单管理', '41', '2023-09-15 16:45:46', null);
INSERT INTO `sys_menu` VALUES ('17', '4', '编辑', 'sys:menu:edit', '', '', '', '2', '', '菜单管理', '42', '2023-09-15 16:46:08', null);
INSERT INTO `sys_menu` VALUES ('18', '4', '删除', 'sys:menu:delete', '', '', '', '2', '', '菜单管理', '43', '2023-09-15 16:46:28', null);
INSERT INTO `sys_menu` VALUES ('19', '3', '分配权限', 'sys:role:assign', '', '', '', '2', '', '角色管理', '34', '2023-09-27 19:17:40', null);
INSERT INTO `sys_menu` VALUES ('20', '2', '重置密码', 'sys:user:reset', '', '', '', '2', '', '用户管理', '24', '2023-09-27 19:20:06', null);

-- ----------------------------
-- Table structure for `sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` int NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '角色名称',
  `type` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '类型',
  `remark` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('11', '部门管理人员', null, '部门管理人员', '2023-09-10 08:00:00', '2023-09-11 15:55:15');
INSERT INTO `sys_role` VALUES ('13', '系统管理员', null, '系统管理员', '2023-09-10 08:00:00', '2023-09-11 15:54:48');
INSERT INTO `sys_role` VALUES ('18', '测试角色2', null, '', '2024-10-11 08:00:00', '2024-10-11 14:37:23');
INSERT INTO `sys_role` VALUES ('19', '测试角色1', null, '', '2024-10-11 14:37:18', null);
INSERT INTO `sys_role` VALUES ('20', '测试角色3', null, '无', '2025-03-20 08:00:00', '2025-03-26 14:48:48');
INSERT INTO `sys_role` VALUES ('21', '测试角色', null, '无', null, null);
INSERT INTO `sys_role` VALUES ('23', '角色测试4', null, '无', null, null);

-- ----------------------------
-- Table structure for `sys_role_menu`
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_menu_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_id` int DEFAULT NULL COMMENT '角色id',
  `menu_id` int DEFAULT NULL COMMENT '菜单id',
  PRIMARY KEY (`role_menu_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=159 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('78', '11', '8');
INSERT INTO `sys_role_menu` VALUES ('79', '11', '13');
INSERT INTO `sys_role_menu` VALUES ('80', '11', '14');
INSERT INTO `sys_role_menu` VALUES ('81', '11', '16');
INSERT INTO `sys_role_menu` VALUES ('82', '11', '1');
INSERT INTO `sys_role_menu` VALUES ('83', '11', '2');
INSERT INTO `sys_role_menu` VALUES ('84', '11', '3');
INSERT INTO `sys_role_menu` VALUES ('85', '11', '4');
INSERT INTO `sys_role_menu` VALUES ('86', '14', '2');
INSERT INTO `sys_role_menu` VALUES ('87', '14', '8');
INSERT INTO `sys_role_menu` VALUES ('88', '14', '11');
INSERT INTO `sys_role_menu` VALUES ('89', '14', '12');
INSERT INTO `sys_role_menu` VALUES ('90', '14', '20');
INSERT INTO `sys_role_menu` VALUES ('91', '14', '1');
INSERT INTO `sys_role_menu` VALUES ('101', '18', '2');
INSERT INTO `sys_role_menu` VALUES ('102', '18', '8');
INSERT INTO `sys_role_menu` VALUES ('103', '18', '11');
INSERT INTO `sys_role_menu` VALUES ('104', '18', '12');
INSERT INTO `sys_role_menu` VALUES ('105', '18', '20');
INSERT INTO `sys_role_menu` VALUES ('106', '18', '1');
INSERT INTO `sys_role_menu` VALUES ('107', '19', '13');
INSERT INTO `sys_role_menu` VALUES ('108', '19', '1');
INSERT INTO `sys_role_menu` VALUES ('109', '19', '3');
INSERT INTO `sys_role_menu` VALUES ('152', '20', '8');
INSERT INTO `sys_role_menu` VALUES ('153', '20', '13');
INSERT INTO `sys_role_menu` VALUES ('154', '20', '14');
INSERT INTO `sys_role_menu` VALUES ('155', '20', '15');
INSERT INTO `sys_role_menu` VALUES ('156', '20', '1');
INSERT INTO `sys_role_menu` VALUES ('157', '20', '2');
INSERT INTO `sys_role_menu` VALUES ('158', '20', '3');

-- ----------------------------
-- Table structure for `sys_user`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录账户',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '登录密码',
  `phone` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '电话',
  `email` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `sex` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '性别',
  `is_admin` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否是超级管理员 1：是 0：否',
  `is_account_non_expired` tinyint DEFAULT NULL COMMENT '账户是否过期 1 未过期，0已过期',
  `is_account_non_locked` tinyint DEFAULT NULL COMMENT '帐户是否被锁定(1 未锁定，0已锁定)',
  `is_credentials_non_expired` tinyint DEFAULT NULL COMMENT '密码是否过期(1 未过期，0已过期)',
  `is_enabled` tinyint DEFAULT NULL COMMENT '帐户是否可用(1 可用，0 删除用户)',
  `nick_name` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', 'admin', '$2a$10$Pt7YBeFKdhsrhRfhGsvbfObXs.z5bD9bsnAuopRZXsLevt7DWlFhC', '*************', '*************', '0', '1', '1', '1', '1', '1', '张三266', '2023-09-11 18:26:20', '2023-09-12 21:31:31');
INSERT INTO `sys_user` VALUES ('2', 'ls', '$2a$10$lOAe6oprp4CEL7FgPkEl7OFp0IEYJsl8lychh.QWNLKREzJMoIkly', '*************', '*************', '0', null, '1', '1', '1', '1', '李四', '2023-09-11 18:28:28', '2023-09-27 19:21:22');
INSERT INTO `sys_user` VALUES ('3', 'ww', '$2a$10$lOAe6oprp4CEL7FgPkEl7OFp0IEYJsl8lychh.QWNLKREzJMoIkly', '*************', '*************', '0', null, '1', '1', '1', '1', '王五', '2023-09-11 18:30:46', null);
INSERT INTO `sys_user` VALUES ('4', 'system', '$2a$10$lOAe6oprp4CEL7FgPkEl7OFp0IEYJsl8lychh.QWNLKREzJMoIkly', '*************', '*************', '1', null, '1', '1', '1', '1', '张三1', '2023-09-12 15:09:23', '2023-09-12 21:24:47');
INSERT INTO `sys_user` VALUES ('10', 'cs001', '$2a$10$Vl.iY3fiHixUu87doODOPOSyVOiZy1wVbuvyq4N94GK3nLr7R1aFu', '*********', '', '0', null, '1', '1', '1', '1', 'cs001', '2024-10-11 14:36:04', '2024-10-11 14:38:08');
INSERT INTO `sys_user` VALUES ('11', 'zhangsan', '$2a$10$5XpN3hDATj/FPoJQwsugi.UuF5FFfvkCJFQKz/2tNGRrbQVYwt042', '13456258985', '', '0', null, '1', '1', '1', '1', 'zhangsan', '2025-03-20 15:07:53', null);
INSERT INTO `sys_user` VALUES ('12', 'lisi', '666666', '13456789890', 'admin@qq.com', '0', '', '1', '1', '1', '1', 'lisi2', '2025-03-26 15:23:38', '2025-04-04 12:45:34');
INSERT INTO `sys_user` VALUES ('14', 'wangwu', '$2a$10$OaR8ISDmNHuHw47BI4Zbuemn71QEvhhiaEQohpAOm4IRfHCA7p5zq', '13234567890', 'admin', '0', null, '1', '1', '1', '1', '王五', '2025-04-04 12:50:30', null);
INSERT INTO `sys_user` VALUES ('17', 'admin112', '111222', '13245678789', '23546677@qq.com', '女', '1', '1', '1', '1', '1', 'admin', '2025-04-09 16:10:51', '2025-04-09 16:11:59');
INSERT INTO `sys_user` VALUES ('18', 'lily110', '$2a$10$K.UARreUPMIEqjx8MnqCbe5B9Fkwh5O/zMr8lW85Aa3DgRvk6MsPS', '13554567890', 'lily110@qq.com', '1', null, '1', '1', '1', '1', '莉莉', '2025-04-11 11:25:39', '2025-04-11 11:29:09');

-- ----------------------------
-- Table structure for `sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_role_id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` int DEFAULT NULL COMMENT '用户id',
  `role_id` int DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`user_role_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('5', '3', '11');
INSERT INTO `sys_user_role` VALUES ('20', '4', '13');
INSERT INTO `sys_user_role` VALUES ('21', '4', '11');
INSERT INTO `sys_user_role` VALUES ('28', '1', '13');
INSERT INTO `sys_user_role` VALUES ('30', '1', '11');
INSERT INTO `sys_user_role` VALUES ('37', '2', '11');
INSERT INTO `sys_user_role` VALUES ('46', '10', '18');
INSERT INTO `sys_user_role` VALUES ('47', '10', '19');
INSERT INTO `sys_user_role` VALUES ('48', '11', '20');
INSERT INTO `sys_user_role` VALUES ('53', '12', '20');
INSERT INTO `sys_user_role` VALUES ('54', '14', '20');
INSERT INTO `sys_user_role` VALUES ('58', '17', '11');
INSERT INTO `sys_user_role` VALUES ('61', '18', '13');
