# 东软云HIS医院管理系统

> 基于云医院的 AI 智能驱动软件工程项目 —— 第六组代码实现仓库。

## 项目简介

东软云 HIS 医院管理系统，覆盖门诊医生、药房管理、检验/检查/处置管理、挂号收费、综合管理等医院核心业务流程，采用前后端分离架构。

## 技术栈

| 层级   | 技术                                  |
| ------ | ------------------------------------- |
| 后端   | Java · Spring Boot · MyBatis · Maven  |
| 前端   | Vue · Element-UI · Node.js            |
| 数据库 | MySQL                                 |
| 架构   | 前后端分离                            |

## 团队成员与分工

| 成员 | GitHub                                   | 负责模块                             |
| ---- | ---------------------------------------- | ------------------------------------ |
| MYL  | [Vania-myl](https://github.com/Vania-myl) | 挂号收费模块、综合管理模块           |
| WWJ  | —                                        | 门诊医生模块、药房管理模块           |
| HYJ  | —                                        | 检验管理模块、检查管理模块、处置管理模块 |

## 项目文档

- [需求规格说明书](docs/第六组-东软云HIS医院管理系统需求规格说明书.docx)
- [系统设计说明书](docs/第六组-东软云HIS医院管理系统系统设计说明书.docx)

## 目录结构（规划）

```
.
├── backend/    # 后端：Spring Boot + MyBatis + Maven
├── frontend/   # 前端：Vue + Element-UI + Node.js
├── docs/       # 需求、设计文档
└── README.md
```

## 分支协作规范

- `main` 为受保护主分支，始终保持可运行状态
- 每人一个功能分支，命名格式 `feature/<模块>`，例如：
  - `feature/registration` —— 挂号收费
  - `feature/general-management` —— 综合管理
  - `feature/outpatient` —— 门诊医生
  - `feature/pharmacy` —— 药房管理
  - `feature/lab` —— 检验管理
  - `feature/examination` —— 检查管理
  - `feature/treatment` —— 处置管理
- 开发完成后合并回 `main`，合并前先拉取最新 `main` 解决冲突

## 快速开始

> 待代码实现阶段补充。
