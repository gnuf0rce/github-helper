# [GitHub Helper](https://github.com/gnuf0rce/github-helper)

> 基于 [Mirai Console](https://github.com/mamoe/mirai-console) 的 GitHub Notice 订阅插件

[![Release](https://img.shields.io/github/v/release/gnuf0rce/github-helper)](https://github.com/gnuf0rce/github-helper/releases)
[![Downloads](https://img.shields.io/github/downloads/gnuf0rce/github-helper/total)](https://shields.io/category/downloads)
[![MiraiForum](https://img.shields.io/badge/post-on%20MiraiForum-yellow)](https://mirai.mamoe.net/topic/554)

## 指令

注意: 使用前请确保可以 [在聊天环境执行指令](https://github.com/project-mirai/chat-command)   
`<...>`中的是指令名，由空格隔开表示或，选择其中任一名称都可执行例如`/repo-issue add mamoe/mirai`  
`[...]`表示参数，当`[...]`后面带`?`时表示参数可选  
`{...}`表示连续的多个参数

`[repo]` 格式为 `{owner}/{repo}`, 举例 `mamoe/mirai`  
`[contact]?`是可选的参数，会自动由当前环境填充，例如群聊填充群号，私聊填充QQ号

### GitHubIssuesCommand

| 指令                             | 描述             |
|:---------------------------------|:-----------------|
| `/<issues> <add> [contact]?`     | 添加订阅         |
| `/<issues> <remove> [contact]?`  | 移除订阅         |
| `/<issues> <interval> [millis]?` | 设置订阅轮询间隔 |
| `/<issues> <list>?`              | 查看订阅列表     |

这个指令用于获取当前用户的`issues`，所以需要有效 `token`

### GitHubRepoCommitCommand

| 指令                                         | 描述             |
|:---------------------------------------------|:-----------------|
| `/<repo-commit> <add> [repo] [contact]?`     | 添加订阅         |
| `/<repo-commit> <remove> [repo] [contact]?`  | 移除订阅         |
| `/<repo-commit> <interval> [repo] [millis]?` | 设置订阅轮询间隔 |
| `/<repo-commit> <list> [contact]?`           | 查看订阅列表     |

### GitHubRepoIssueCommand

| 指令                                        | 描述             |
|:--------------------------------------------|:-----------------|
| `/<repo-issue> <add> [repo] [contact]?`     | 添加订阅         |
| `/<repo-issue> <remove> [repo] [contact]?`  | 移除订阅         |
| `/<repo-issue> <interval> [repo] [millis]?` | 设置订阅轮询间隔 |
| `/<repo-issue> <list> [contact]?`           | 查看订阅列表     |

### GitHubRepoPullCommand

| 指令                                       | 描述             |
|:-------------------------------------------|:-----------------|
| `/<repo-pull> <add> [repo] [contact]?`     | 添加订阅         |
| `/<repo-pull> <remove> [repo] [contact]?`  | 移除订阅         |
| `/<repo-pull> <interval> [repo] [millis]?` | 设置订阅轮询间隔 |
| `/<repo-pull> <list> [contact]?`           | 查看订阅列表     |

### GitHubRepoReleaseCommand

| 指令                                          | 描述             |
|:----------------------------------------------|:-----------------|
| `/<repo-release> <add> [repo] [contact]?`     | 添加订阅         |
| `/<repo-release> <remove> [repo] [contact]?`  | 移除订阅         |
| `/<repo-release> <interval> [repo] [millis]?` | 设置订阅轮询间隔 |
| `/<repo-release> <list> [contact]?`           | 查看订阅列表     |

### GitHubStatsCommand

| 指令                     | 描述     |
|:-------------------------|:---------|
| `/<stats> <card> [name]` | 查看状态 |

## 设置

### GithubConfig

* `proxy` Format http://127.0.0.1:8080 or socks://127.0.0.1:1080
* `token` [Personal Access Tokens](https://github.com/settings/tokens)
* `reply` Subscriber Reply Message Type `TEXT, XML, JSON`, JSON 尚不可用
* `percentage_member_join` 加群放行活跃等级（百分制），默认0，不开启功能
* `sign_member_join` 加群放行提示信息
* `github_readme_stats` stats card 绘制参数