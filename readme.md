# [GitHub Helper](https://github.com/gnuf0rce/github-helper)

> 基于 [Mirai Console](https://github.com/mamoe/mirai-console) 的 GitHub Notice 订阅插件

[![Release](https://img.shields.io/github/v/release/gnuf0rce/github-helper)](https://github.com/gnuf0rce/github-helper/releases)
[![Downloads](https://img.shields.io/github/downloads/gnuf0rce/github-helper/total)](https://shields.io/category/downloads)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net/topic/554)

**使用前应该查阅的相关文档或项目**

* [User Manual](https://github.com/mamoe/mirai/blob/dev/docs/UserManual.md)
* [Permission Command](https://github.com/mamoe/mirai/blob/dev/mirai-console/docs/BuiltInCommands.md#permissioncommand)
* [Chat Command](https://github.com/project-mirai/chat-command)

## 指令

注意: 使用前请确保可以 [在聊天环境执行指令](https://github.com/project-mirai/chat-command)   
`<...>`中的是指令名，例如`/repo-issue add mamoe/mirai`  
`[...]`表示参数，当`[...]`后面带`?`时表示参数可选  
`{...}`表示连续的多个参数

本插件指令权限ID 格式为 `io.github.gnuf0rce.github-helper:command.*`, `*` 是指令的第一指令名  
例如 `/repo-issue add mamoe/mirai` 的权限ID为 `io.github.gnuf0rce.github-helper:command.repo-issue`

`[repo]` 格式为 `{owner}/{repo}`, 举例 `mamoe/mirai`  
`[contact]?`是可选的参数，会自动由当前环境填充，例如群聊填充群号，私聊填充QQ号

### GitHubIssuesCommand

| 指令                               | 描述       |
|:---------------------------------|:---------|
| `/<issues> <add> [contact]?`     | 添加订阅     |
| `/<issues> <remove> [contact]?`  | 移除订阅     |
| `/<issues> <interval> [millis]?` | 设置订阅轮询间隔 |
| `/<issues> <list>?`              | 查看订阅列表   |

这个指令用于获取当前用户的`issues`，所以需要有效 `token`

### GitHubRepoCommitCommand

| 指令                                           | 描述       |
|:---------------------------------------------|:---------|
| `/<repo-commit> <add> [repo] [contact]?`     | 添加订阅     |
| `/<repo-commit> <remove> [repo] [contact]?`  | 移除订阅     |
| `/<repo-commit> <interval> [repo] [millis]?` | 设置订阅轮询间隔 |
| `/<repo-commit> <list> [contact]?`           | 查看订阅列表   |

### GitHubRepoIssueCommand

| 指令                                          | 描述       |
|:--------------------------------------------|:---------|
| `/<repo-issue> <add> [repo] [contact]?`     | 添加订阅     |
| `/<repo-issue> <remove> [repo] [contact]?`  | 移除订阅     |
| `/<repo-issue> <interval> [repo] [millis]?` | 设置订阅轮询间隔 |
| `/<repo-issue> <list> [contact]?`           | 查看订阅列表   |

### GitHubRepoPullCommand

| 指令                                         | 描述       |
|:-------------------------------------------|:---------|
| `/<repo-pull> <add> [repo] [contact]?`     | 添加订阅     |
| `/<repo-pull> <remove> [repo] [contact]?`  | 移除订阅     |
| `/<repo-pull> <interval> [repo] [millis]?` | 设置订阅轮询间隔 |
| `/<repo-pull> <list> [contact]?`           | 查看订阅列表   |

### GitHubRepoReleaseCommand

| 指令                                            | 描述       |
|:----------------------------------------------|:---------|
| `/<repo-release> <add> [repo] [contact]?`     | 添加订阅     |
| `/<repo-release> <remove> [repo] [contact]?`  | 移除订阅     |
| `/<repo-release> <interval> [repo] [millis]?` | 设置订阅轮询间隔 |
| `/<repo-release> <list> [contact]?`           | 查看订阅列表   |

### GitHubStatsCommand

| 指令                               | 描述   |
|:---------------------------------|:-----|
| `/<stats> <card> [name]`         | 查看状态 |
| `/<stats> <contribution> [name]` | 查看贡献 |

## 自动通过加群问题放行开发者

`1.1.7` 起对接到 [mirai-administrator](https://github.com/cssxsh/mirai-administrator) 实现此功能

举例：

```
问题：GitHub ID ?
答案：cssxsh
```

机器人会检查 `cssxsh` 的活跃度是否满足要求，如满足要求则放行

## 设置

### GithubConfig

* `proxy` Format http://127.0.0.1:8080 or socks://127.0.0.1:1080
* `token` [Personal Access Tokens](https://github.com/settings/tokens)
* `reply` Subscriber Reply Message Type `TEXT, XML, JSON`, JSON 尚不可用
* `percentage_member_join` 加群放行 GitHub 活跃等级（百分制），默认0，不开启功能
* `percentages` 加群放行 GitHub 活跃等级（百分制），默认0，不开启功能
* `sign_member_join` 加群放行提示信息
* `github_readme_stats` stats card 绘制参数

## 安装

### MCL 指令安装

`./mcl --update-package io.github.gnuf0rce:github-helper --channel stable --type plugin`

### 手动安装

* 运行 [Mirai Console](https://github.com/mamoe/mirai-console) 生成`plugins`文件夹
* 从 [Releases](https://github.com/gnuf0rce/github-helper/releases) 下载`jar`并将其放入`plugins`文件夹中